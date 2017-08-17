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
package com.b2international.index.translog;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.Doc;
import com.b2international.index.Hits;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.LuceneIndexAdmin;
import com.b2international.index.json.Delete;
import com.b2international.index.json.Index;
import com.b2international.index.json.JsonDocumentSearcher;
import com.b2international.index.lucene.Directories;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.slowlog.SlowLogConfig;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import groovy.lang.Script;

/**
 * @since 5.0
 */
final class EsTransactionLogTest {
	
	private final Logger logger = LoggerFactory.getLogger(EsTransactionLogTest.class);
	
	@Doc
	public static class Data {

		private String field1;
		private String field2;

		public Data() {
			this("field1", "field2");
		}

		@JsonCreator
		public Data(@JsonProperty("field1") String field1, @JsonProperty("field2") String field2) {
			this.field1 = field1;
			this.field2 = field2;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Data other = (Data) obj;
			return Objects.equals(field1, other.field1) && Objects.equals(field2, other.field2);
		}

		@Override
		public int hashCode() {
			return Objects.hash(field1, field2);
		}

	}
	
	private final class EsTransactionLogTestAdmin implements LuceneIndexAdmin {
		
		private final SearcherManager manager;
		private final Map<String, Object> settings;

		private EsTransactionLogTestAdmin(final SearcherManager manager) {
			this.manager = manager;
			this.settings = ImmutableMap.<String, Object>of(IndexClientFactory.SLOW_LOG_KEY, new SlowLogConfig(Maps.<String, Object>newHashMap()));
		}

		@Override
		public Logger log() {
			return logger;
		}
		
		@Override
		public Script compile(String script) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean exists() {
			return false;
		}

		@Override
		public void create() {
		}

		@Override
		public void delete() {
		}

		@Override
		public <T> void clear(Class<T> type) {
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
			return null;
		}

		@Override
		public void optimize(int maxSegments) {
		}

		@Override
		public void close() {
		}

		@Override
		public ReentrantLock getLock() {
			return null;
		}

		@Override
		public IndexWriter getWriter() {
			return null;
		}
		
		@Override
		public QueryBuilder getQueryBuilder() {
			return null;
		}

		@Override
		public ReferenceManager<IndexSearcher> getManager() {
			return manager;
		}

		@Override
		public TransactionLog getTransactionlog() {
			return null;
		}
		
	}

	private final ObjectMapper mapper;
	private final String indexName = "tlogTest";
	private final Mappings mappings = new Mappings(Data.class);

	public static void main(String[] args) {
		final EsTransactionLogTest test = new EsTransactionLogTest();

		try {
			test.opShouldBeRecoveredFromTranslog();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			test.opShouldNotBeRecoveredFromTranslogAfterCommit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			test.complexScenario();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private EsTransactionLogTest() {
		mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}

	private void opShouldBeRecoveredFromTranslog() throws Exception {
		IndexWriter writer2 = null;
		SearcherManager manager2 = null;
		JsonDocumentSearcher searcher2 = null;

		EsTransactionLog tlog = null;
		ExecutorService executor = null;

		try {
			final Path indexPath = Files.createTempDir().toPath();
			executor = Executors.newFixedThreadPool(1);

			IndexWriter writer = newIndexWriter(indexPath);
			SearcherManager manager = newSearcherManager(writer, executor);
			EsTransactionLogTestAdmin admin = new EsTransactionLogTestAdmin(manager);
			JsonDocumentSearcher searcher = new JsonDocumentSearcher(admin, mapper);

			tlog = new EsTransactionLog(indexName, indexPath.resolve("translog"), mapper, mappings, Maps.<String, String> newHashMap(), logger);

			final String key = "tl";
			final Data data = new Data();
			final DocumentMapping mapping = mappings.getMapping(data.getClass());
			final Index index = new Index(key, data, mapper, mapping);

			index.execute(writer, searcher);
			tlog.addOperation(index);

			close(writer, manager, searcher);

			writer2 = newIndexWriter(indexPath);
			manager2 = newSearcherManager(writer2, executor);
			EsTransactionLogTestAdmin admin2 = new EsTransactionLogTestAdmin(manager2);
			searcher2 = new JsonDocumentSearcher(admin2, mapper);

			tlog.recoverFromTranslog(writer2, searcher2);

			final Query<Data> query = Query.select(Data.class).where(Expressions.exactMatch("field1", "field1")).build();
			final Hits<Data> hits = searcher2.search(query);

			if (hits.getTotal() != 1) {
				throw new IllegalStateException(String.format("Expected one search hit, found %d.", hits.getTotal()));
			}
		} finally {
			close(tlog, writer2, manager2, searcher2);
			executor.shutdown();
		}
	}

	private void opShouldNotBeRecoveredFromTranslogAfterCommit() throws Exception {
		IndexWriter writer2 = null;
		SearcherManager manager2 = null;
		JsonDocumentSearcher searcher2 = null;

		EsTransactionLog tlog = null;
		ExecutorService executor = null;

		try {
			final Path indexPath = Files.createTempDir().toPath();
			executor = Executors.newFixedThreadPool(1);

			IndexWriter writer = newIndexWriter(indexPath);
			SearcherManager manager = newSearcherManager(writer, executor);
			EsTransactionLogTestAdmin admin = new EsTransactionLogTestAdmin(manager);
			JsonDocumentSearcher searcher = new JsonDocumentSearcher(admin, mapper);

			tlog = new EsTransactionLog(indexName, indexPath.resolve("translog"), mapper, mappings, Maps.<String, String> newHashMap(), logger);

			final String key = "tl";
			final Data data = new Data();
			final DocumentMapping mapping = mappings.getMapping(data.getClass());

			final Index index = new Index(key, data, mapper, mapping);
			index.execute(writer, searcher);

			tlog.addOperation(index);
			tlog.commit(writer);

			final Delete delete = new Delete(mapping.toUid(key));
			delete.execute(writer, searcher);
			writer.commit();

			close(writer, manager, searcher);

			writer2 = newIndexWriter(indexPath);
			manager2 = newSearcherManager(writer2, executor);
			EsTransactionLogTestAdmin admin2 = new EsTransactionLogTestAdmin(manager2);
			searcher2 = new JsonDocumentSearcher(admin2, mapper);

			tlog.recoverFromTranslog(writer2, searcher2);

			final Query<Data> query = Query.select(Data.class).where(Expressions.exactMatch("field1", "field1")).build();
			final Hits<Data> hits = searcher2.search(query);

			if (hits.getTotal() != 0) {
				throw new IllegalStateException(String.format("Expected no search hit, found %d.", hits.getTotal()));
			}
		} finally {
			close(tlog, writer2, manager2, searcher2);
			executor.shutdown();
		}
	}

	private void complexScenario() throws Exception {
		IndexWriter writer2 = null;
		SearcherManager manager2 = null;
		JsonDocumentSearcher searcher2 = null;

		EsTransactionLog tlog = null;
		EsTransactionLog tlog2 = null;
		ExecutorService executor = null;

		try {
			final Path indexPath = Files.createTempDir().toPath();
			executor = Executors.newFixedThreadPool(1);

			IndexWriter writer = newIndexWriter(indexPath);
			SearcherManager manager = newSearcherManager(writer, executor);
			EsTransactionLogTestAdmin admin = new EsTransactionLogTestAdmin(manager);
			JsonDocumentSearcher searcher = new JsonDocumentSearcher(admin, mapper);

			tlog = new EsTransactionLog(indexName, indexPath.resolve("translog"), mapper, mappings, Maps.<String, String> newHashMap(), logger);

			final String key1 = "tl1";
			final Data data1 = new Data("field1", "field1");

			final String key2 = "tl2";
			final Data data2 = new Data("field2", "field2");

			final String key3 = "tl3";
			final Data data3 = new Data("field3", "field3");

			final String key4 = "tl4";
			final Data data4 = new Data("field4", "field4");

			final DocumentMapping mapping = mappings.getMapping(Data.class);

			final Index index1 = new Index(key1, data1, mapper, mapping);
			final Index index2 = new Index(key2, data2, mapper, mapping);

			index1.execute(writer, searcher);
			index2.execute(writer, searcher);

			tlog.addOperation(index1);
			tlog.addOperation(index2);

			tlog.commit(writer);

			final Map<String, String> commitData = writer.getCommitData();

			final Delete delete1 = new Delete(mapping.toUid(key1));
			final Delete delete2 = new Delete(mapping.toUid(key2));

			delete1.execute(writer, searcher);
			delete2.execute(writer, searcher);

			tlog.addOperation(delete1);
			tlog.addOperation(delete2);

			final Index index3 = new Index(key3, data3, mapper, mapping);
			final Index index4 = new Index(key4, data4, mapper, mapping);
			
			index3.execute(writer, searcher);
			index4.execute(writer, searcher);

			tlog.addOperation(index3);
			tlog.addOperation(index4);

			final Delete delete3 = new Delete(mapping.toUid(key3));
			delete3.execute(writer, searcher);

			tlog.addOperation(delete3);

			close(tlog, writer, manager, searcher);

			writer2 = newIndexWriter(indexPath);
			manager2 = newSearcherManager(writer2, executor);
			EsTransactionLogTestAdmin admin2 = new EsTransactionLogTestAdmin(manager2);
			searcher2 = new JsonDocumentSearcher(admin2, mapper);

			tlog2 = new EsTransactionLog(indexName, indexPath.resolve("translog"), mapper, mappings, commitData, logger);
			tlog2.recoverFromTranslog(writer2, searcher2);

			final Query<Data> query1 = Query.select(Data.class).where(Expressions.exactMatch("field1", "field1")).build();
			final Hits<Data> hits1 = searcher2.search(query1);
			if (hits1.getTotal() != 0) {
				throw new IllegalStateException(String.format("Expected no search hit for data 1, found %d.", hits1.getTotal()));
			}

			final Query<Data> query2 = Query.select(Data.class).where(Expressions.exactMatch("field1", "field2")).build();
			final Hits<Data> hits2 = searcher2.search(query2);
			if (hits2.getTotal() != 0) {
				throw new IllegalStateException(String.format("Expected no search hit for data 2, found %d.", hits2.getTotal()));
			}

			final Query<Data> query3 = Query.select(Data.class).where(Expressions.exactMatch("field1", "field3")).build();
			final Hits<Data> hits3 = searcher2.search(query3);
			if (hits3.getTotal() != 0) {
				throw new IllegalStateException(String.format("Expected no search hit for data 3, found %d.", hits3.getTotal()));
			}

			final Query<Data> query4 = Query.select(Data.class).where(Expressions.exactMatch("field1", "field4")).build();
			final Hits<Data> hits4 = searcher2.search(query4);
			if (hits4.getTotal() != 1) {
				throw new IllegalStateException(String.format("Expected one search hit for data 4, found %d.", hits4.getTotal()));
			}
		} finally {
			close(tlog2, writer2, manager2, searcher2);
			executor.shutdown();
		}
	}

	private IndexWriter newIndexWriter(final Path indexPath) throws IOException {
		final IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);

		final Directory directory = Directories.openFile(indexPath.resolve(indexName));
		return new IndexWriter(directory, config);
	}

	private SearcherManager newSearcherManager(final IndexWriter writer, final ExecutorService executor) throws IOException {
		return new SearcherManager(writer, true, new SearcherFactory() {
			@Override
			public IndexSearcher newSearcher(IndexReader reader, IndexReader previousReader) throws IOException {
				return new IndexSearcher(reader, executor);
			}
		});
	}

	private void close(final AutoCloseable... closeables) throws Exception {
		for (final AutoCloseable closeable : closeables) {
			closeable.close();
		}
	}

}
