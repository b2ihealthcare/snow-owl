/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.index.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexException;
import com.b2international.index.LuceneIndexAdmin;
import com.b2international.index.analyzer.ComponentTermAnalyzer;
import com.google.common.collect.Maps;
import com.google.common.io.Closer;

/**
 * @since 4.7
 */
public abstract class BaseLuceneIndexAdmin implements LuceneIndexAdmin {

	private static class Holder {
		private static final Timer PERIODIC_COMMIT_TIMER = new Timer("Index commit thread", true);
	}

	private final String name;
	private final AtomicReference<PeriodicCommit> periodicCommit = new AtomicReference<>();
	private final AtomicBoolean open = new AtomicBoolean(false);
	private final Map<String, Object> settings;
	
	private Closer closer;
	private Directory directory;
	private IndexWriter writer;
	private ReferenceManager<IndexSearcher> manager;
	private ExecutorService executor;
	
	protected BaseLuceneIndexAdmin(String name) {
		this(name, Maps.<String, Object>newHashMap());
	}
	
	protected BaseLuceneIndexAdmin(String name, Map<String, Object> settings) {
		this.name = name;
		
		// init default settings
		this.settings = newHashMap(settings);
		if (!this.settings.containsKey(IndexClientFactory.COMMIT_INTERVAL_KEY)) {
			this.settings.put(IndexClientFactory.COMMIT_INTERVAL_KEY, IndexClientFactory.DEFAULT_COMMIT_INTERVAL);
		}
	}
	
	private void ensureOpen() {
		if (!open.get()) {
			throw new IllegalStateException("Index is not available");
		}
	}

	@Override
	public IndexWriter getWriter() {
		ensureOpen();
		return writer;
	}
	
	@Override
	public ReferenceManager<IndexSearcher> getManager() {
		ensureOpen();
		return manager;
	}
	
	@Override
	public boolean exists() {
		return directoryExists() && writer != null && manager != null;
	}

	private boolean directoryExists() {
		try {
			return directory != null && DirectoryReader.indexExists(directory);
		} catch (IOException e) {
			throw new IndexException("Failed to check directory", e);
		}
	}

	@Override
	public void create() {
		if (exists() || open.get()) {
			throw new IllegalStateException("Index already exists " + name());
		}
		try {
			closer = Closer.create();
			directory = openDirectory();
			closer.register(directory);
			writer = new IndexWriter(directory, createConfig(false));
			initPeriodicCommit(writer);
			closer.register(writer);
			if (!DirectoryReader.indexExists(directory)) {
				writer.commit(); // actually create the index
			}
			// TODO configure warmer???
			executor = Executors.newFixedThreadPool(Math.max(2, Math.min(16, Runtime.getRuntime().availableProcessors())));
			manager = new SearcherManager(writer, true, new SearcherFactory() {
				@Override
				public IndexSearcher newSearcher(IndexReader reader) throws IOException {
					return new IndexSearcher(reader, executor);
				}
			});
			closer.register(manager);
			open.set(true);
		} catch (IOException e) {
			close();
			throw new IndexException("Couldn't create index " + name(), e);
		}
	}

	protected abstract Directory openDirectory() throws IOException;

	private void initPeriodicCommit(IndexWriter writer) {
		final long periodicCommitInterval = (long) settings().get(IndexClientFactory.COMMIT_INTERVAL_KEY);
		final PeriodicCommit newPc = new PeriodicCommit(name, writer);
		final PeriodicCommit previousPc = periodicCommit.getAndSet(newPc);
		if (previousPc != null) {
			previousPc.cancel();
		}
		Holder.PERIODIC_COMMIT_TIMER.schedule(newPc, periodicCommitInterval, periodicCommitInterval);
	}

	private IndexWriterConfig createConfig(boolean clean) {
		// TODO configurable analyzer and options
		final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, new ComponentTermAnalyzer(true, true));
		config.setOpenMode(clean ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
		return config;
	}
	
	@Override
	public void close() {
		ensureOpen();
		try {
			final PeriodicCommit pc = periodicCommit.getAndSet(null);
			if (pc != null) {
				pc.cancel();
			}
			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.MINUTES);
			directory = null;
			writer = null;
			manager = null;
			closer.close();
			closer = null;
			open.set(false);
		} catch (IOException | InterruptedException e) {
			throw new IndexException("Couldn't close index " + name(), e);
		}
	}

	@Override
	public void delete() {
		ensureOpen();
		try {
			// reopen writer with clean option to clear directory
			writer.close();
			writer = new IndexWriter(directory, createConfig(true));
			closer.register(writer);
			close();
		} catch (IOException e) {
			throw new IndexException("Couldn't delete index " + name(), e);
		}
	}
	
	@Override
	public void optimize(int maxSegments) {
		ensureOpen();
		try {
			writer.forceMerge(maxSegments);
		} catch (IOException e) {
			throw new IndexException("Couldn't optimize index " + name(), e);
		}
	}

	@Override
	public <T> void clear(Class<T> type) {
		// TODO remove all documents matching the given type, based on mappings
	}

	@Override
	public Map<String, Object> settings() {
		return settings;
	}

	@Override
	public String name() {
		return name;
	}

	/**
	 * Periodically commits an {@link IndexWriter}.
	 *  
	 * @since 4.7
	 */
	private static class PeriodicCommit extends TimerTask {
		
		private final IndexWriter writer;
		private final String name;
		
		private PeriodicCommit(String name, IndexWriter writer) {
			this.name = name;
			this.writer = checkNotNull(writer, "writer");
		}

		@Override
		public void run() {
			Thread.currentThread().setName(String.format("'%s' index commit", name));
			try {
				writer.commit();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AlreadyClosedException e) {
				cancel();
			}
		}
	}

}
