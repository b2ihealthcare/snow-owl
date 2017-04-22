/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.BytesRef;

import com.b2international.index.Searcher;
import com.b2international.index.lucene.Fields;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.Revision;
import com.b2international.index.util.NumericClassUtils;
import com.b2international.index.util.Reflections;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.hash.Hashing;

/**
 * @since 4.7
 */
public final class Index implements Operation {

	private final String key;
	private byte[] source;

	// transient values, should not be serialized into the translog
	private final Object document;
	private final DocumentMapping mapping;
	private ObjectMapper mapper;

	public Index(String key, Object source, ObjectMapper mapper, DocumentMapping mapping) {
		this.key = key;
		this.document = source;
		this.mapper = mapper;
		this.mapping = mapping;
	}
	
	public Index(String key, byte[] source, ObjectMapper mapper, DocumentMapping mapping) {
		this.key = key;
		this.source = source;
		this.mapper = mapper;
		this.mapping = mapping;
		// no document source in this case, we will use the source byte array as document source
		this.document = null;
	}
	
	@Override
	public void execute(IndexWriter writer, Searcher searcher) throws IOException {
		final String uid = mapping.toUid(key);
		final Collection<Document> docs = newLinkedList();
		
		final JsonNode doc = this.document == null ? mapper.readTree(source) : mapper.valueToTree(document);
		if (source == null) {
			source = mapper.writeValueAsBytes(doc);
		}
		if (doc instanceof ObjectNode) {
			collectDocs(uid, key, (ObjectNode) doc, mapping, docs);
		} else {
			throw new IllegalArgumentException("Expecting root object documents instead of " + doc);
		}
		
		// update all documents with the same uid
		writer.updateDocuments(JsonDocumentMapping._uid().toTerm(uid), docs);
	}
	
	/* traverse the fields and map the given object and its nested objects */
	private void collectDocs(String uid, String key, ObjectNode object, final DocumentMapping mapping, final Collection<Document> docs) throws IOException {
		for (Field field : mapping.getFields()) {
			// skip Maps
			if (Reflections.isMapType(field)) {
				continue;
			}
			final Class<?> fieldType = Reflections.getType(field);
			if (DocumentMapping.isNestedDoc(fieldType)) {
				final DocumentMapping nestedTypeMapping = mapping.getNestedMapping(fieldType);
				final JsonNode node = object.get(field.getName());
				if (node instanceof ArrayNode) {
					for (JsonNode item : node) {
						if (item instanceof ObjectNode) {
							collectDocs(uid, UUID.randomUUID().toString(), (ObjectNode) item, nestedTypeMapping, docs);
						}
					}
				} else if (node instanceof ObjectNode) {
					collectDocs(uid, UUID.randomUUID().toString(), (ObjectNode) node, nestedTypeMapping, docs);
				}
			}
		}
		final Document doc = map(uid, key, object, mapping);
		docs.add(doc);
	}
	
	public String key() {
		return key;
	}
	
	public String uid() {
		return mapping.toUid(key);
	}
	
	public DocumentMapping mapping() {
		return mapping;
	}
	
	public byte[] source() {
		return source;
	}
	
	private Document map(String uid, String key, ObjectNode node, DocumentMapping mapping) throws IOException {
		final Document doc = new Document();
		// metadata fields
		JsonDocumentMapping._id().addTo(doc, key);
		JsonDocumentMapping._type().addTo(doc, mapping.typeAsString());
		JsonDocumentMapping._uid().addTo(doc, uid);
		// TODO create byte fields
		final byte[] nodeSource; 
		if (key.equals(this.key)) {
			// this is the ROOT node, do not serialize it again
			nodeSource = this.source;
		} else {
			nodeSource = mapper.writeValueAsBytes(node);
		}
		doc.add(new StoredField("_source", nodeSource));
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
			addToDoc(doc, name, value, mapping, true);
		}
		
		// add a hash of the object after processed all props (required for revision compare)
		// modify the original _source JSON by removing Revision props completely, so that the hash function produces consistent hashes
		final String content = node.remove(Revision.REV_FIELDS).toString();
		final String _hash = Hashing.sha1().hashUnencodedChars(content).toString();
		JsonDocumentMapping._hash().addTo(doc, _hash);
		Fields.stringDocValuesField(DocumentMapping._HASH).addTo(doc, _hash);
		
		return doc;
	}

	/* 
	 * TODO: 
	 * - add support for nested object properties without nested document mapping
	 * - add support for selective field loading and sorting on all fields by storing DocValues 
	 *   (boolean values and collections can not be selected at the moment)
	 */
	private void addToDoc(final Document doc, final String name, final JsonNode node, DocumentMapping mapping, boolean docValues) {
		switch (node.getNodeType()) {
		case ARRAY:
			for (final JsonNode item : node) {
				addToDoc(doc, name, item, mapping, false);
			}
			break;
		case STRING:
			if (mapping.isAnalyzed(name)) {
				for (String analyzedFieldName : mapping.getAnalyzers(name).keySet()) {
					Fields.searchOnlyTextField(analyzedFieldName).addTo(doc, node.textValue());
				}
			} else {
				Fields.searchOnlyStringField(name).addTo(doc, node.textValue());
			}
			
			if (docValues) {
				doc.add(new BinaryDocValuesField(name, new BytesRef(node.textValue())));
			}
			break;
		case BOOLEAN:
			Fields.searchOnlyBoolField(name).addTo(doc, node.booleanValue());
			break;
		case NUMBER:
			final Field field = mapping.getField(name);
			final Class<?> fieldType = NumericClassUtils.unwrapCollectionType(field);
			
			if (docValues && NumericClassUtils.isCollection(field)) {
				throw new IllegalStateException("Docvalues can not be indexed for a collection of type: " + fieldType + " for field: " + name);
			}
			
			if (NumericClassUtils.isLong(fieldType)) {
				Fields.searchOnlyLongField(name).addTo(doc, node.longValue());
			} else if (NumericClassUtils.isFloat(fieldType)) {
				Fields.searchOnlyFloatField(name).addTo(doc, node.floatValue());
			} else if (NumericClassUtils.isInt(fieldType) || NumericClassUtils.isShort(fieldType)) {
				Fields.searchOnlyIntField(name).addTo(doc, node.intValue());
			} else if (NumericClassUtils.isBigDecimal(fieldType)) {
				throw new UnsupportedOperationException("BigDecimal should be encoded to a ordered Base64 String value");
			} else if (NumericClassUtils.isDate(fieldType)) {
				Fields.searchOnlyLongField(name).addTo(doc, node.longValue());
			} else {
				throw new UnsupportedOperationException("Unsupported number type: " + fieldType + " for field: " + name);
			}
			
			if (docValues) {
				if (NumericClassUtils.isFloat(fieldType)) {
					doc.add(new FloatDocValuesField(name, node.floatValue()));
				} else if (NumericClassUtils.isLong(fieldType) || NumericClassUtils.isInt(fieldType) || NumericClassUtils.isShort(fieldType) || NumericClassUtils.isDate(fieldType)) {
					doc.add(new NumericDocValuesField(name, node.longValue()));
				}
				// BigDecimals are handled above
			}
			break;
		default:
			break;
		}
	}
}
