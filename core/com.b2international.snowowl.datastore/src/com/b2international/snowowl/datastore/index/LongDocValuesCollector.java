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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.LongCollection;
import bak.pcj.list.LongArrayList;

/**
 * Collector instance for gathering long doc values from the matching documents.
 */
public class LongDocValuesCollector extends AbstractDocsOutOfOrderCollector {

	/**
	 * Default size for the underlying collection for primitives. Value: {@value}.
	 */
	private static final int DEFAULT_SIZE = 10000;

	private final String fieldName;
	private final LongCollection collectedValues;

	private NumericDocValues numericDocValues;

	/**
	 * Creates a collector instance with the specified field name and default initial size for collected values.
	 * 
	 * @param fieldName the name of the field for which values should be collected
	 */
	public LongDocValuesCollector(final String fieldName) {
		this(fieldName, DEFAULT_SIZE);
	}

	/**
	 * Creates a collector isntance with the specified field name and a backing collection with the expected size.
	 * 
	 * @param fieldName the name of the field for which values should be collected
	 * @param expectedSize the expected size of the values collection, or <= 0 to use the default size
	 */
	public LongDocValuesCollector(final String fieldName, final int expectedSize) {
		this.fieldName = checkNotNull(fieldName, "Field name argument cannot be null.");
		this.collectedValues = (0 > expectedSize) ? new LongArrayList(expectedSize) : new LongArrayList();
	}

	@Override
	public void collect(final int docId) throws IOException {
		collectedValues.add(numericDocValues.get(docId));
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		numericDocValues = leafReader.getNumericDocValues(fieldName);
	}

	@Override
	protected boolean isLeafCollectible() {
		return numericDocValues != null;
	}

	/**
	 * Returns a collection of values that are extracted from the specified docValue source.
	 * <p>
	 * <b>NOTE:&nbsp;</b>The collection may contain duplicate elements.
	 * 
	 * @return the collected values
	 */
	public LongCollection getValues() {
		return collectedValues;
	}
}
