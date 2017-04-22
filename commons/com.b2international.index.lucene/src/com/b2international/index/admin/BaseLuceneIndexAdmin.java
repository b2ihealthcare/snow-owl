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
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
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
import org.apache.lucene.util.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.Analyzed;
import com.b2international.index.AnalyzerImpls;
import com.b2international.index.Analyzers;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexException;
import com.b2international.index.LuceneIndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.slowlog.SlowLogConfig;
import com.b2international.index.translog.TransactionLog;
import com.google.common.collect.Maps;
import com.google.common.io.Closer;

/**
 * @since 4.7
 */
public abstract class BaseLuceneIndexAdmin implements LuceneIndexAdmin {

	private static class Holder {
		private static final Timer PERIODIC_COMMIT_TIMER = new Timer("Index commit thread", true);
		private static final Timer PERIODIC_TRANSLOG_SYNC_TIMER = new Timer("Index translog sync thread", true);
	}

	private final String name;
	private final AtomicReference<PeriodicCommit> periodicCommit = new AtomicReference<>();
	private final AtomicReference<PeriodicTranslogSync> periodicTranslogSync = new AtomicReference<>();
	private final AtomicBoolean open = new AtomicBoolean(false);
	private final Mappings mappings;
	private final Map<String, Object> settings;
	
	private Closer closer;
	private Directory directory;
	private IndexWriter writer;
	private ReferenceManager<IndexSearcher> manager;
	private ExecutorService executor;
	private TransactionLog tlog;
	private ReentrantLock lock = new ReentrantLock();
	private QueryBuilder queryBuilder;
	
	protected BaseLuceneIndexAdmin(String name, Mappings mappings) {
		this(name, mappings, Maps.<String, Object>newHashMap());
	}
	
	protected BaseLuceneIndexAdmin(String name, Mappings mappings, Map<String, Object> settings) {
		this.name = name;
		this.mappings = mappings;
		
		// init default settings
		this.settings = newHashMap(settings);
		if (!this.settings.containsKey(IndexClientFactory.COMMIT_INTERVAL_KEY)) {
			this.settings.put(IndexClientFactory.COMMIT_INTERVAL_KEY, IndexClientFactory.DEFAULT_COMMIT_INTERVAL);
		}
		
		if (!this.settings.containsKey(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY)) {
			this.settings.put(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY, IndexClientFactory.DEFAULT_TRANSLOG_SYNC_INTERVAL);
		}
		
		if (!this.settings.containsKey(IndexClientFactory.LOG_KEY)) {
			this.settings.put(IndexClientFactory.LOG_KEY, LoggerFactory.getLogger(String.format("index.%s", name)));
		}
		
		if (!this.settings.containsKey(IndexClientFactory.SLOW_LOG_KEY)) {
			this.settings.put(IndexClientFactory.SLOW_LOG_KEY, new SlowLogConfig(this.settings));
		}
		
		if (!this.settings.containsKey(IndexClientFactory.RESULT_WINDOW_KEY)) {
			this.settings.put(IndexClientFactory.RESULT_WINDOW_KEY, IndexClientFactory.DEFAULT_RESULT_WINDOW);
		}
	}
	
	private void ensureOpen() {
		if (!open.get()) {
			throw new IllegalStateException("Index is not available");
		}
	}
	
	@Override
	public Logger log() {
		return (Logger) settings().get(IndexClientFactory.LOG_KEY);
	}

	@Override
	public IndexWriter getWriter() {
		ensureOpen();
		return writer;
	}
	
	@Override
	public final QueryBuilder getQueryBuilder() {
		return queryBuilder;
	}
	
	@Override
	public TransactionLog getTransactionlog() {
		return tlog;
	}
	
	public ReentrantLock getLock() {
		return lock;
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
			queryBuilder = new QueryBuilder(getSearchAnalyzer());
			closer.register(writer);
			
			tlog = createTransactionlog(writer.getCommitData());
			closer.register(tlog);
			
			if (!DirectoryReader.indexExists(directory)) {
				tlog.commitWriter(writer);
			} else {
				tlog.recoverFromTranslog(writer, null);
			}
			
			initPeriodicCommit(writer, tlog);
			initPeriodicTranslogSync(tlog);
			
			// TODO configure warmer???
			executor = Executors.newFixedThreadPool(Math.max(2, Math.min(16, Runtime.getRuntime().availableProcessors())));
			manager = new SearcherManager(writer, true, new SearcherFactory() {
				@Override
				public IndexSearcher newSearcher(IndexReader reader, IndexReader previousReader) throws IOException {
					return new IndexSearcher(reader, executor);
				}
			});
			
			closer.register(manager);
			open.set(true);
		} catch (IOException e) {
			throw new IndexException("Couldn't create index " + name(), e);
		}
	}

	protected abstract TransactionLog createTransactionlog(Map<String, String> commitData) throws IOException;

	protected abstract Directory openDirectory() throws IOException;

	private void initPeriodicCommit(IndexWriter writer, TransactionLog tlog) {
		final long periodicCommitInterval = (long) settings().get(IndexClientFactory.COMMIT_INTERVAL_KEY);
		final PeriodicCommit newPc = new PeriodicCommit(name, writer, tlog);
		final PeriodicCommit previousPc = periodicCommit.getAndSet(newPc);
		if (previousPc != null) {
			previousPc.cancel();
		}
		Holder.PERIODIC_COMMIT_TIMER.schedule(newPc, periodicCommitInterval, periodicCommitInterval);
	}
	
	private void initPeriodicTranslogSync(TransactionLog tlog) {
		final long interval = (long) settings().get(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY);
		final PeriodicTranslogSync newSync = new PeriodicTranslogSync(name, tlog);
		final PeriodicTranslogSync oldSync = periodicTranslogSync.getAndSet(newSync);
		
		if (oldSync != null) {
			oldSync.cancel();
		}
		
		Holder.PERIODIC_TRANSLOG_SYNC_TIMER.schedule(newSync, interval, interval);
	}

	private IndexWriterConfig createConfig(boolean clean) {
		// TODO configurable analyzer and options
		final IndexWriterConfig config = new IndexWriterConfig(getIndexAnalyzer());
		config.setOpenMode(clean ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
		return config;
	}
	
	private Analyzer getIndexAnalyzer() {
		final Map<String, Analyzer> fieldAnalyzers = newHashMap();
		for (DocumentMapping mapping : mappings.getMappings()) {
			for (Entry<String, Analyzed> entry : mapping.getAnalyzedFields().entrySet()) {
				final Analyzers analyzer = entry.getValue().analyzer();
				if (Analyzers.DEFAULT != analyzer) {
					fieldAnalyzers.put(entry.getKey(), AnalyzerImpls.getAnalyzer(analyzer));
				}
			}
		}
		return new PerFieldAnalyzerWrapper(AnalyzerImpls.DEFAULT, fieldAnalyzers);
	}
	
	private Analyzer getSearchAnalyzer() {
		final Map<String, Analyzer> fieldAnalyzers = newHashMap();
		for (DocumentMapping mapping : mappings.getMappings()) {
			for (Entry<String, Analyzed> entry : mapping.getAnalyzedFields().entrySet()) {
				final Analyzers indexAnalyzer = entry.getValue().analyzer();
				final Analyzers searchAnalyzer = entry.getValue().searchAnalyzer();
				if (Analyzers.INDEX == searchAnalyzer) {
					fieldAnalyzers.put(entry.getKey(), AnalyzerImpls.getAnalyzer(indexAnalyzer));
				} else {
					fieldAnalyzers.put(entry.getKey(), AnalyzerImpls.getAnalyzer(searchAnalyzer));
				}
			}
		}
		return new PerFieldAnalyzerWrapper(AnalyzerImpls.DEFAULT, fieldAnalyzers);
	}

	@Override
	public void close() {
		ensureOpen();
		try {
			final PeriodicCommit pc = periodicCommit.getAndSet(null);
			if (pc != null) {
				pc.cancel();
			}
			
			final PeriodicTranslogSync pts = periodicTranslogSync.getAndSet(null);
			if (pts != null) {
				pts.cancel();
			}
			
			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.MINUTES);
			tlog.commit(writer);
			directory = null;
			writer = null;
			manager = null;
			tlog = null;
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
			log().info("Optimizing to max. {} number of segments", maxSegments);
			writer.forceMerge(maxSegments);
			writer.commit();
		} catch (IOException e) {
			throw new IndexException("Couldn't optimize index " + name(), e);
		} finally {
			log().info("Optimization to max. {} number of segments completed", maxSegments);
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
	public Mappings mappings() {
		return mappings;
	}

	@Override
	public String name() {
		return name;
	}
	
	public TransactionLog tlog() {
		return tlog;
	}

	/**
	 * Periodically commits an {@link IndexWriter}.
	 *  
	 * @since 4.7
	 */
	private static class PeriodicCommit extends TimerTask {
		
		private final String name;
		private final IndexWriter writer;
		private final TransactionLog tlog;
		
		private PeriodicCommit(String name, IndexWriter writer, TransactionLog tlog) {
			this.name = name;
			this.tlog = tlog;
			this.writer = checkNotNull(writer, "writer");
		}

		@Override
		public void run() {
			Thread.currentThread().setName(String.format("'%s' index commit", name));
			try {
				tlog.commit(writer);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AlreadyClosedException e) {
				cancel();
			}
		}
	}
	
	private static class PeriodicTranslogSync extends TimerTask {
		
		private final String name;
		private final TransactionLog tlog;
		
		private PeriodicTranslogSync(final String name, final TransactionLog tlog) {
			this.name = name;
			this.tlog = tlog;
		}

		@Override
		public void run() {
			Thread.currentThread().setName(String.format("'%s' index translog sync", name));
			try {
				tlog.sync();
			} catch (IOException e) {
				e.printStackTrace(); // TODO log error instead of print
			}
		}
		
	}

}
