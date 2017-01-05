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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.util.OrderedBytes;
import org.apache.hadoop.hbase.util.SimplePositionedMutableByteRange;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.CollectionTerminatedException;
import org.apache.lucene.search.CollectorManager;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.util.BytesRef;

import com.b2international.index.json.DocValueCollectorManager.DocValueCollector;
import com.b2international.index.mapping.DocumentMapping;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

/**
 * @since 5.4
 */
public final class DocValueCollectorManager implements CollectorManager<DocValueCollector, List<Map<String, Object>>> {

	private final Set<String> fields;
	private final DocumentMapping mapping;
	
	public DocValueCollectorManager(Set<String> fields, DocumentMapping mapping) {
		this.fields = fields;
		this.mapping = mapping;
	}
	
	@Override
	public DocValueCollector newCollector() throws IOException {
		return new DocValueCollector();
	}

	@Override
	public List<Map<String, Object>> reduce(Collection<DocValueCollector> collectors) throws IOException {
		return FluentIterable.from(collectors).transformAndConcat(DocValueCollector::getHits).toList();
	}
	
	/**
	 * @since 5.4
	 */
	public class DocValueCollector extends SimpleCollector {

		private final Map<String, Object> docValues = newHashMapWithExpectedSize(fields.size());
		private final Collection<Map<String, Object>> hits = newArrayList();
		
		@Override
		protected void doSetNextReader(LeafReaderContext context) throws IOException {
			for (String field : fields) {
				Object docValues = null;
				Field mappingField = mapping.getField(field);
				Class<?> fieldType = NumericClassUtils.unwrapCollectionType(mappingField);
				
				if (NumericClassUtils.isCollection(mappingField)) {
					throw new IllegalStateException("Docvalues can not be retrieved for a collection of type: " + fieldType + " for field: " + field);
				}
				
				if (NumericClassUtils.isFloat(fieldType) || NumericClassUtils.isLong(fieldType) || NumericClassUtils.isInt(fieldType) || NumericClassUtils.isShort(fieldType)) {
					docValues = context.reader().getNumericDocValues(field);
				} else if (NumericClassUtils.isBigDecimal(fieldType) || String.class.isAssignableFrom(fieldType)) {
					docValues = context.reader().getBinaryDocValues(field);
				}
				if (docValues == null) {
					throw new CollectionTerminatedException();
				}
				this.docValues.put(field, docValues);
			}
		}
		
		@Override
		public boolean needsScores() {
			return false;
		}

		@Override
		public void collect(int doc) throws IOException {
			final Map<String, Object> hit = newHashMapWithExpectedSize(fields.size());
			for (String field : fields) {
				Object docValues = this.docValues.get(field);
				Class<?> fieldType = mapping.getField(field).getType();
				
				if (NumericClassUtils.isFloat(fieldType)) {
					int intBits = (int) ((NumericDocValues) docValues).get(doc);
					hit.put(field, Float.intBitsToFloat(intBits));  
				} else if (NumericClassUtils.isLong(fieldType)) {
					hit.put(field, ((NumericDocValues) docValues).get(doc));
				} else if (NumericClassUtils.isInt(fieldType)) {
					hit.put(field, (int) ((NumericDocValues) docValues).get(doc));
				} else if (NumericClassUtils.isShort(fieldType)) {
					hit.put(field, (short) ((NumericDocValues) docValues).get(doc));
				} else if (NumericClassUtils.isBigDecimal(fieldType)) {
					BytesRef bytesRef = ((BinaryDocValues) docValues).get(doc);
					SimplePositionedMutableByteRange src = new SimplePositionedMutableByteRange(bytesRef.bytes, bytesRef.offset, bytesRef.length);
					hit.put(field, OrderedBytes.decodeNumericAsBigDecimal(src));
				} else if (String.class.isAssignableFrom(fieldType)) {
					hit.put(field, ((BinaryDocValues) docValues).get(doc).utf8ToString());
				} else {
					throw new UnsupportedOperationException("Unhandled docValues for field: " + field + " of type: " + fieldType + " -> " + docValues);
				}
			}
			if (!hit.keySet().containsAll(fields)) {
				throw new IllegalStateException(String.format("Missing fields on partially loaded document: %s", Sets.difference(fields, hit.keySet())));
			}
			hits.add(hit);
		}
		
		public Collection<Map<String, Object>> getHits() {
			return hits;
		}
		
	}

}
