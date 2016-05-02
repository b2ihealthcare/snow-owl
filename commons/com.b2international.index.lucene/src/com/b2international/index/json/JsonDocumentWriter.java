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

import static com.google.common.collect.Lists.newLinkedList;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;

import com.b2international.index.util.Reflections;
import com.b2international.index.write.Writer;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class JsonDocumentWriter implements Writer {

	private final IndexWriter writer;
	private final ReferenceManager<IndexSearcher> searchers;
	private final JsonDocumentMappingStrategy mappingStrategy;

	public JsonDocumentWriter(IndexWriter writer, ReferenceManager<IndexSearcher> searchers, ObjectMapper mapper) {
		this.writer = writer;
		this.searchers = searchers;
		this.mappingStrategy = new JsonDocumentMappingStrategy(mapper);
	}
	
	@Override
	public void close() throws Exception {
		// TODO rollback changes if there were exceptions
		searchers.maybeRefreshBlocking();
	}

	@Override
	public void put(String key, Object object) throws IOException {
		putAll(Collections.singletonMap(key, object));
	}
	
	@Override
	public void putAll(Map<String, Object> objectByKeys) throws IOException {
		final Collection<Document> docs = newLinkedList();
		for (Entry<String, Object> entry : objectByKeys.entrySet()) {
			collectDocs(entry.getKey(), entry.getValue(), docs);
		}
		writer.addDocuments(docs);
	}

	/* traverse the fields and map the given object and its nested objects */
	private void collectDocs(String key, Object object, final Collection<Document> docs) throws IOException {
		collectDocs(JsonDocumentMapping.toUid(object.getClass(), key), key, object, docs);
	}
	
	private void collectDocs(String uid, String key, Object object, final Collection<Document> docs) throws IOException {
		for (Field field : Reflections.getFields(object.getClass())) {
			final Class<?> fieldType = Reflections.getType(field);
			if (JsonDocumentMapping.isNestedDoc(fieldType)) {
				final Object fieldValue = Reflections.getValue(object, field);
				if (fieldValue instanceof Iterable) {
					for (Object item : (Iterable<?>) fieldValue) {
						collectDocs(uid, UUID.randomUUID().toString(), item, docs);
					}
				} else {
					collectDocs(uid, UUID.randomUUID().toString(), fieldValue, docs);
				}
			}
		}
		final Document doc = mappingStrategy.map(uid, key, object);
		docs.add(doc);
	}

	@Override
	public void remove(Class<?> type, String key) throws IOException {
		removeAll(Collections.<Class<?>, String>singletonMap(type, key));
	}
	
	@Override
	public void removeAll(Map<Class<?>, String> keysByType) throws IOException {
		final BooleanQuery deleteQuery = new BooleanQuery(true);
		for (Entry<Class<?>, String> entry : keysByType.entrySet()) {
			deleteQuery.add(JsonDocumentMapping._uid().toQuery(JsonDocumentMapping.toUid(entry.getKey(), entry.getValue())), Occur.SHOULD);
		}
		writer.deleteDocuments(deleteQuery);
	}

}
