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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.CollectionTerminatedException;
import org.apache.lucene.search.CollectorManager;
import org.apache.lucene.search.SimpleCollector;

import com.b2international.index.json.DocValueCollectorManager.DocValueCollector;
import com.google.common.collect.FluentIterable;

/**
 * @since 5.4
 */
public final class DocValueCollectorManager implements CollectorManager<DocValueCollector, List<Map<String, Object>>> {

	private final Set<String> fields;
	
	public DocValueCollectorManager(Set<String> fields) {
		this.fields = fields;
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
				Object docValues = context.reader().getNumericDocValues(field);
				if (docValues == null) {
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
				if (docValues instanceof BinaryDocValues) {
					hit.put(field, ((BinaryDocValues) docValues).get(doc).utf8ToString());
				} else if (docValues instanceof NumericDocValues) {
					hit.put(field, ((NumericDocValues) docValues).get(doc));
				} else {
					throw new UnsupportedOperationException("Unhandled docValues for field: " + field + " -> " + docValues);
				}
			}
			checkState(!hit.isEmpty(), "No values loaded from document for fields: %s", fields);
			hits.add(hit);
		}
		
		public Collection<Map<String, Object>> getHits() {
			return hits;
		}
		
	}

}
