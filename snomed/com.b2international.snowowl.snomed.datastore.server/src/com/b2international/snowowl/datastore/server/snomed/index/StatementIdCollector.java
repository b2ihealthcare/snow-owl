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
package com.b2international.snowowl.datastore.server.snomed.index;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.google.common.base.Preconditions;

/**
 * Collector for gathering a set of object, value and attribute concept IDs for 
 * a subset of SNOMED&nbsp;CT relationships 
 *
 */
public class StatementIdCollector extends AbstractDocsOutOfOrderCollector {

	/**
	 * Default initial size for the underlying collection. Value: {@value}.
	 */
	private static final int DEFAULT_EXPECTED_SIZE = 30000;
	
	private final LongSet ids;
	
	private NumericDocValues attributeIdsValues;
	private NumericDocValues valueIdsValues;
	private NumericDocValues objectIdsValues;
	
	/**
	 * Creates a new collector instance with the default expected size. 
	 */
	public StatementIdCollector() {
		this(DEFAULT_EXPECTED_SIZE);
	}
	
	/**
	 * Creates a collector instance with the given expected size.
	 * @param expectedSize the expected size.
	 */
	public StatementIdCollector(final int expectedSize) {
		ids = 0 > expectedSize ? new LongOpenHashSet(expectedSize) : new LongOpenHashSet();
	}
	
	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#collect(int)
	 */
	@Override
	public void collect(final int doc) throws IOException {
	
		if (!checkValues()) { //sources cannot be referenced
			return;
		}
		
		ids.add(objectIdsValues.get(doc));
		ids.add(attributeIdsValues.get(doc));
		ids.add(valueIdsValues.get(doc));

	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
	 */
	@Override
	public void setNextReader(final AtomicReaderContext context) throws IOException {
		
		Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.");
		
		attributeIdsValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID);
		if (null == attributeIdsValues) {
			resetValues();
			return;
		}
		
		valueIdsValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID);
		if (null == valueIdsValues) {
			resetValues();
			return;
		}
		
		objectIdsValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID);
		if (null == objectIdsValues) {
			resetValues();
			return;
		}

	}
	
	/**
	 * Returns with a set of object, value and attribute concept IDs.
	 * @return the concept IDs.
	 */
	public LongSet getIds() {
		return ids;
	}

	/*sets the reference on the values to null*/
	private void resetValues() {
		attributeIdsValues = null;
		valueIdsValues = null;
		objectIdsValues = null;
	}
	
	/*returns true only and if only all the backing values can be referenced*/
	private boolean checkValues() {
		return null != valueIdsValues 
				&& null != objectIdsValues
				&& null != attributeIdsValues;
	}

}