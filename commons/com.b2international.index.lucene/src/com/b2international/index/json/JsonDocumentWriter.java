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
package com.b2international.index.json;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;

import com.b2international.index.BulkUpdate;
import com.b2international.index.IndexException;
import com.b2international.index.Searcher;
import com.b2international.index.WithId;
import com.b2international.index.Writer;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.translog.TransactionLog;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class JsonDocumentWriter implements Writer {

	private final IndexWriter writer;
	private final TransactionLog tlog;
	private final ReferenceManager<IndexSearcher> searchers;
	private final Collection<Operation> operations = newArrayList();
	private final ObjectMapper mapper;
	private final Mappings mappings;
	
	private boolean withUpdate = false;
	private JsonDocumentSearcher searcher;

	public JsonDocumentWriter(IndexWriter writer, TransactionLog tlog, ReferenceManager<IndexSearcher> searchers, ObjectMapper mapper, Mappings mappings) {
		this.writer = writer;
		this.tlog = tlog;
		this.searchers = searchers;
		this.mapper = mapper;
		this.mappings = mappings;
		this.searcher = new JsonDocumentSearcher(searchers, mapper, mappings);
	}
	
	@Override
	public Searcher searcher() {
		return searcher;
	}
	
	@Override
	public void close() throws Exception {
		this.operations.clear();
		this.searcher.close();
	}
	
	@Override
	public void commit() throws IOException {
		// TODO add a txId to new documents, so we will be able to delete all changes
		if (withUpdate) {
			synchronized (writer) {
				try {
					// reopen search for bulk update operations
					searcher.close();
					searcher = new JsonDocumentSearcher(searchers, mapper, mappings);
					applyOperations();
				} catch (Exception e) {
					throw new IndexException("Failed to commit transaction", e);
				}
			}
		} else {
			applyOperations();
		}
	}
	
	private void applyOperations() throws IOException {
		for (Operation op : this.operations) {
			op.execute(writer, searcher);
			tlog.addOperation(op);
		}
		searchers.maybeRefreshBlocking();
	}
	
	@Override
	public void put(String key, Object object) throws IOException {
		putAll(Collections.singletonMap(key, object));
	}
	
	@Override
	public <T> void putAll(Map<String, T> objectsByKey) throws IOException {
		for (Entry<String, T> entry : objectsByKey.entrySet()) {
			final String key = entry.getKey();
			final T doc = entry.getValue();
			final DocumentMapping mapping = mappings.getMapping(doc.getClass());
			operations.add(new Index(key, doc, mapper, mapping));
		}
	}
	
	@Override
	public void remove(Class<?> type, String key) throws IOException {
		removeAll(Collections.<Class<?>, Set<String>>singletonMap(type, Collections.singleton(key)));
	}
	
	@Override
	public void removeAll(Map<Class<?>, Set<String>> keysByType) throws IOException {
		for (Entry<Class<?>, Set<String>> entry : keysByType.entrySet()) {
			final Class<?> type = entry.getKey();
			final Set<String> keys = entry.getValue();
			final DocumentMapping mapping = mappings.getMapping(type);
			for (String key : keys) {
				this.operations.add(new Delete(mapping.toUid(key)));
			}
		}
	}
	
	@Override
	public <T extends WithId> void bulkUpdate(BulkUpdate<T> update) throws IOException {
		this.withUpdate = true;
		this.operations.add(new BulkUpdateOperation<T>(update, mapper, mappings));
	}

}
