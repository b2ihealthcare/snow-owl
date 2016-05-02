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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;

import com.b2international.index.Analyzed;
import com.b2international.index.mapping.IndexField;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.util.Reflections;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class JsonDocumentMappingStrategy {

	private final ObjectMapper mapper;

	public JsonDocumentMappingStrategy(ObjectMapper mapper) {
		this.mapper = checkNotNull(mapper, "mapper");
	}
	
	Document map(String uid, String key, Object object) throws IOException {
		final Document doc = new Document();
		// metadata fields
		JsonDocumentMapping._id().addTo(doc, key);
		JsonDocumentMapping._type().addTo(doc, JsonDocumentMapping.getType(object));
		JsonDocumentMapping._uid().addTo(doc, uid);
		// TODO create byte fields
		doc.add(new StoredField("_source", mapper.writeValueAsBytes(object)));
		// add all other fields
		for (Field field : Reflections.getFields(object.getClass())) {
			final Object value = Reflections.getValue(object, field);
			final IndexField indexField = getIndexField(field);
			if (Mappings.none() != indexField) {
				if (value instanceof Iterable) {
					for (Object item : (Iterable<?>) value) {
						indexField.addTo(doc, item);
					}
				} else {
					indexField.addTo(doc, value);
				}
			}
		}
		return doc;
	}
	
	private IndexField getIndexField(Field field) {
		final Class<?> fieldType = Reflections.getType(field);
		final String fieldName = field.getName();
		final boolean analyzed = field.isAnnotationPresent(Analyzed.class);
		if (fieldType == String.class) {
			return analyzed ? Mappings.textField(fieldName) : Mappings.stringField(fieldName);
		} else if (fieldType == Boolean.class || fieldType == boolean.class) {
			return Mappings.boolField(fieldName);
		} else if (fieldType == Integer.class || fieldType == int.class) {
			return Mappings.intField(fieldName);
		} else if (fieldType == Float.class || fieldType == float.class) {
			return Mappings.floatField(fieldName);
		} else if (fieldType == Long.class || fieldType == long.class) {
			return Mappings.longField(fieldName);
		} else if (fieldType.isArray()) {
			throw new UnsupportedOperationException("Arrays are not supported: " + field);
		} else if (fieldType.isEnum()) {
			throw new UnsupportedOperationException("Enums are not supported: " + field);
		} else {
			return Mappings.none();
		}
	}

}
