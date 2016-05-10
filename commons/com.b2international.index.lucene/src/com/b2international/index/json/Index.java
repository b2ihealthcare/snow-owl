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
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.util.Reflections;

/**
 * @since 4.7
 */
class Index implements Operation {

	private final String uid;
	private final String key;
	private final Object document;
	private final JsonDocumentMappingStrategy mapper;
	private final DocumentMapping mapping;

	Index(String uid, String key, Object document, JsonDocumentMappingStrategy mapper, DocumentMapping mapping) {
		this.uid = uid;
		this.key = key;
		this.document = document;
		this.mapper = mapper;
		this.mapping = mapping;
	}
	
	@Override
	public void execute(IndexWriter writer) throws IOException {
		final Collection<Document> docs = newLinkedList();
		collectDocs(uid, key, document, mapping, docs);
		// update all documents with the same uid
		writer.updateDocuments(JsonDocumentMapping._uid().toTerm(uid), docs);
	}
	
	/* traverse the fields and map the given object and its nested objects */
	private void collectDocs(String uid, String key, Object object, final DocumentMapping mapping, final Collection<Document> docs) throws IOException {
		for (Field field : mapping.getFields()) {
			final Class<?> fieldType = Reflections.getType(field);
			if (DocumentMapping.isNestedDoc(fieldType)) {
				final DocumentMapping nestedTypeMapping = mapping.getNestedMapping(fieldType);
				final Object fieldValue = Reflections.getValue(object, field);
				if (fieldValue instanceof Iterable) {
					for (Object item : (Iterable<?>) fieldValue) {
						collectDocs(uid, UUID.randomUUID().toString(), item, nestedTypeMapping, docs);
					}
				} else {
					collectDocs(uid, UUID.randomUUID().toString(), fieldValue, nestedTypeMapping, docs);
				}
			}
		}
		final Document doc = this.mapper.map(uid, key, object, mapping);
		docs.add(doc);
	}
	
}
