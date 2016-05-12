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
package com.b2international.index;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.b2international.index.analyzer.ComponentTermAnalyzer;
import com.b2international.index.lucene.Directories;
import com.google.common.collect.Maps;
import com.google.common.io.Closer;

/**
 * @since 4.7
 */
final class FSIndexAdmin implements LuceneIndexAdmin {

	private static class Holder {
		private static final Timer CLEANUP_TIMER = new Timer("Review cleanup", true);
	}

	private static final long DEFAULT_COMMIT_INTERVAL = TimeUnit.MINUTES.toMillis(5L);
	private static final String COMMIT_INTERVAL_KEY = "hardCommitInterval";
	
	private final String name;
	private final Path indexPath;
	private final AtomicReference<PeriodicCommit> periodicCommit = new AtomicReference<>();
	private final AtomicBoolean open = new AtomicBoolean(false);
	private final Map<String, Object> settings;
	
	private Closer closer;
	private FSDirectory directory;
	private IndexWriter writer;
	private ReferenceManager<IndexSearcher> manager;

	public FSIndexAdmin(File directory, String name) {
		this(directory, name, Maps.<String, Object>newHashMap());
	}
	
	public FSIndexAdmin(File directory, String name, Map<String, Object> settings) {
		this.name = name;
		this.indexPath = directory.toPath().resolve(name);
		
		// init default settings
		if (!settings.containsKey(COMMIT_INTERVAL_KEY)) {
			settings.put(COMMIT_INTERVAL_KEY, DEFAULT_COMMIT_INTERVAL);
		}
		
		this.settings = settings;
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
			directory = Directories.open(indexPath.toFile());
			closer.register(directory);
			writer = new IndexWriter(directory, createConfig(false));
			initPeriodicCommit(writer);
			closer.register(writer);
			if (!DirectoryReader.indexExists(directory)) {
				writer.commit(); // actually create the index
			}
			// TODO configure warmer???
			manager = new SearcherManager(writer, true, null);
			closer.register(manager);
			open.set(true);
		} catch (IOException e) {
			close();
			throw new IndexException("Couldn't create index " + name(), e);
		}
	}

	private void initPeriodicCommit(IndexWriter writer) {
		final long periodicCommitInterval = (long) settings().get(COMMIT_INTERVAL_KEY);
		final PeriodicCommit newPc = new PeriodicCommit(writer);
		final PeriodicCommit previousPc = periodicCommit.getAndSet(newPc);
		if (previousPc != null) {
			previousPc.cancel();
		}
		Holder.CLEANUP_TIMER.schedule(newPc, periodicCommitInterval, periodicCommitInterval);
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
			directory = null;
			writer = null;
			manager = null;
			closer.close();
			closer = null;
			open.set(false);
		} catch (IOException e) {
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
	public <T> void clear(Class<T> type) {
		// TODO remove all documents matching the given type, based on mappings
		throw new UnsupportedOperationException();
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
		
		public PeriodicCommit(IndexWriter writer) {
			this.writer = checkNotNull(writer, "writer");
		}

		@Override
		public void run() {
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
