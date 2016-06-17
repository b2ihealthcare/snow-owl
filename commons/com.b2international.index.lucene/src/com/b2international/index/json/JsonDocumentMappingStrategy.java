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
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;

import com.b2international.index.lucene.Fields;
import com.b2international.index.mapping.DocumentMapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @since 4.7
 */
public class JsonDocumentMappingStrategy {

	private final ObjectMapper mapper;

	public JsonDocumentMappingStrategy(ObjectMapper mapper) {
		this.mapper = checkNotNull(mapper, "mapper");
	}
	
	Document map(String uid, String key, Object object, DocumentMapping mapping) throws IOException {
		final Document doc = new Document();
		// metadata fields
		JsonDocumentMapping._id().addTo(doc, key);
		JsonDocumentMapping._type().addTo(doc, mapping.typeAsString());
		JsonDocumentMapping._uid().addTo(doc, uid);
		// TODO create byte fields
		doc.add(new StoredField("_source", mapper.writeValueAsBytes(object)));
		final ObjectNode node = mapper.valueToTree(object);
		// add all other fields
		final Iterator<Entry<String, JsonNode>> fields = node.fields();
		while (fields.hasNext()) {
			final Entry<String, JsonNode> field = fields.next();
			final String name = field.getKey();
			if (JsonDocumentMapping._id().fieldName().equals(name)) {
				// skip _id field we add that manually
				continue;
			}			
			final JsonNode value = field.getValue();
			addToDoc(doc, name, value, mapping);
		}
		return doc;
	}

	private void addToDoc(final Document doc, final String name, final JsonNode node, DocumentMapping mapping) {
		switch (node.getNodeType()) {
		case ARRAY:
			// FIXME deeply nested objects, etc.
			// for now only basic lists are supported
			final Iterator<JsonNode> array = node.iterator();
			while (array.hasNext()) {
				addToDoc(doc, name, array.next(), mapping);
			}
			break;
		case STRING:
			// TODO get from mapping if the field is analyzed and use text fields
			Fields.searchOnlyStringField(name).addTo(doc, node.textValue());
			break;
		case BOOLEAN:
			Fields.searchOnlyBoolField(name).addTo(doc, node.booleanValue());
			break;
		case NUMBER:
			Class<?> fieldType = mapping.getField(name).getType();
			if (fieldType == Long.class || fieldType == long.class) {
				Fields.searchOnlyLongField(name).addTo(doc, node.longValue());
			} else if (fieldType == Float.class || fieldType == float.class) {
				Fields.searchOnlyFloatField(name).addTo(doc, node.floatValue());
			} else if (fieldType == Integer.class || fieldType == int.class) {
				Fields.searchOnlyIntField(name).addTo(doc, node.intValue());
			} else if (fieldType == Short.class || fieldType == short.class) {
				Fields.searchOnlyIntField(name).addTo(doc, node.intValue());
			}
			break;
		default:
			break;
		}
	}
	
}
