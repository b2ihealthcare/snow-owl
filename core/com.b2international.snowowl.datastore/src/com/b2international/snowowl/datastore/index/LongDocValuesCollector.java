/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.index;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.Scorer;

import bak.pcj.LongCollection;
import bak.pcj.list.LongArrayList;

import com.google.common.base.Preconditions;

/**
 * Collector instance for gathering long doc values from the matching documents.
 */
public class LongDocValuesCollector extends AbstractDocsOutOfOrderCollector {

		/**Default size for the underlying collection for primitives. Value: {@value}.*/
		private static final int DEFAULT_SIZE = 10000;

		private String fieldName;
		private LongCollection values;
		private NumericDocValues docValues;

		/**
		 * Creates a doc value collector with the specified field name.
		 * @param fieldName the field name.
		 */
		public LongDocValuesCollector(final String fieldName) {
			this(fieldName, DEFAULT_SIZE);
		}
		
		/**
		 * Creates a doc value collector with a given field name and a backing collection with the expected size.
		 * @param fieldName the field name.
		 * @param expectedMatchingDocumentCount expected size of the underlying collection.
		 */
		public LongDocValuesCollector(final String fieldName, final int expectedMatchingDocumentCount) {
			this.fieldName = Preconditions.checkNotNull(fieldName, "Field name argument cannot be null.");
			values = 0 > expectedMatchingDocumentCount ? new LongArrayList(expectedMatchingDocumentCount) : new LongArrayList();
		}
		
		/* (non-Javadoc)
		 * @see org.apache.lucene.search.Collector#setScorer(org.apache.lucene.search.Scorer)
		 */
		@Override
		public void setScorer(final Scorer scorer) throws IOException {
			//no scorer is available
		}

		/* (non-Javadoc)
		 * @see org.apache.lucene.search.Collector#collect(int)
		 */
		@Override
		public void collect(int doc) throws IOException {
			if (null != docValues) {
				values.add(docValues.get(doc));
			}
		}

		/* (non-Javadoc)
		 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
		 */
		@Override
		public void setNextReader(final AtomicReaderContext context) throws IOException {
			Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.");
			docValues = context.reader().getNumericDocValues(fieldName);
			
		}
		
		/**
		 * Returns with a collection of values that are extracted from the doc value source.
		 * <p><b>NOTE:&nbsp;</b>The collection may contain duplicate elements.
		 * TODO profile performance loss based on the LongCollection to LongSet wrapping.
		 * XXX this may change in the future.
		 * @return the values.
		 */
		public LongCollection getValues() {
			return values;
		}
		
	} 