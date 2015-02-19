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
package com.b2international.snowowl.snomed.datastore.index;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.LongCollection;
import bak.pcj.list.LongArrayList;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.google.common.base.Preconditions;

/**
 * Collector implementation for collecting all SNOMED&nbsp;CT relationship's destination concept IDs
 * if the statement's both attribute ID and destination ID fulfills the previously specified condition.  
 */
public class StatementObjectIdCollector extends AbstractDocsOutOfOrderCollector {

	/**
	 * Default initial size for the underlying collection.
	 */
	private static final int DEFAULT_EXPECTED_SIZE = 100000;
	
	private final LongCollection attributeIds;
	private final LongCollection valueIds;
	private final LongCollection objectIds;

	private NumericDocValues attributeIdValues;
	private NumericDocValues valueIdsValues;
	private NumericDocValues objectIdValues;

	/**
	 * Creates a new collector instance with a subset of attribute and value IDs. 
	 * @param attributeIds the attribute IDs.
	 * @param valueIds the value IDs.
	 */
	public StatementObjectIdCollector(final LongCollection attributeIds, final LongCollection valueIds) {
		this(attributeIds, valueIds, DEFAULT_EXPECTED_SIZE);
	}
	
	/**
	 * Creates a new collector instance with a subset of attribute and value IDs and an expected size of the
	 * matching relationships. 
	 * @param attributeIds the attribute IDs.
	 * @param valueIds the value IDs.
	 * @param expectedSize expected number of matching relationships.
	 */
	public StatementObjectIdCollector(final LongCollection attributeIds, final LongCollection valueIds, final int expectedSize) {
		this.attributeIds = Preconditions.checkNotNull(attributeIds, "Attribute IDs collection argument cannot be null");
		this.valueIds = Preconditions.checkNotNull(valueIds, "Value IDs collection argument cannot be null");
		this.objectIds = 0 > expectedSize ? new LongArrayList(expectedSize) : new LongArrayList();
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#collect(int)
	 */
	@Override
	public void collect(final int doc) throws IOException {

		if (!checkValues()) { //sources cannot be referenced
			return;
		}
		
		final long attributeId = attributeIdValues.get(doc);
		final long valueId = valueIdsValues.get(doc);
		
		//if both statement type and destination type matches we store the source ID
		if (valueIds.contains(valueId) && attributeIds.contains(attributeId)) {
			objectIds.add(objectIdValues.get(doc));
		}
		
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
	 */
	@Override
	public void setNextReader(final AtomicReaderContext context) throws IOException {
		Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.");

		attributeIdValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID);
		if (null == attributeIdValues) {
			resetValues();
			return;
		}
		
		valueIdsValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID);
		if (null == valueIdsValues) {
			resetValues();
			return;
		}
		
		objectIdValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID);
		if (null == objectIdValues) {
			resetValues();
			return;
		}
		
	}

	/**
	 * Returns with a collection of matching object ID of a statement.
	 * @return the objectIds. May contain duplicate entries.
	 */
	public LongCollection getObjectIds() {
		return objectIds;
	}
	
	/*sets the reference on the value to null*/
	private void resetValues() {
		attributeIdValues = null;
		valueIdsValues = null;
		objectIdValues = null;
	}
	
	/*returns true only and if only all the backing values can be referenced*/
	private boolean checkValues() {
		return null != attributeIdValues
			&& null != valueIdsValues
			&& null != objectIdValues;
	}
	
}