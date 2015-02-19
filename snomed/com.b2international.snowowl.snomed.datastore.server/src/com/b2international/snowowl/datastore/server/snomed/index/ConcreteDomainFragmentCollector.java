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

import static org.apache.lucene.util.BytesRef.deepCopyOf;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.BytesRef;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Custom collector for getting the bare minimum of a data type for the classification process.
 * @see AbstractDocsOutOfOrderCollector
 */
public class ConcreteDomainFragmentCollector extends AbstractDocsOutOfOrderCollector {

	/**
	 * Default size for the backing map.
	 */
	private static final int DEFAULT_SIZE = 100000;

	private NumericDocValues uomValues;
	private BinaryDocValues valueValues;
	private BinaryDocValues labelValues;
	private NumericDocValues idValues;
	private NumericDocValues typeValues;
	private NumericDocValues storageKeyValues;
	private NumericDocValues refSetIdValues;
	
	private LongKeyMap dataTypeMap;

	
	/**
	 * Creates a data type collector instance.
	 */
	public ConcreteDomainFragmentCollector() {
		this(DEFAULT_SIZE);
	}

	/**
	 * Creates a data type collector instance with an initial expected size for the backing map. 
	 * @param expectedSize the expected size.
	 */
	public ConcreteDomainFragmentCollector(final int expectedSize) {
		dataTypeMap = 0 > expectedSize ? new LongKeyOpenHashMap(expectedSize) : new LongKeyOpenHashMap();
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#collect(int)
	 */
	@Override
	public void collect(int doc) throws IOException {
		
		if (!checkValues()) { //sources cannot be referenced
			return;
		}
		
		long uomId = null == uomValues ? 0 : uomValues.get(doc);
		
		if (0 == uomId) {
			uomId = ConcreteDomainFragment.UNSET_UOM_ID;
		}
		
		final byte type = (byte) typeValues.get(doc);
		final com.b2international.snowowl.snomed.mrcm.DataType dataType = com.b2international.snowowl.snomed.mrcm.DataType.get(type);
		final com.b2international.snowowl.snomed.snomedrefset.DataType convertedDataType = SnomedRefSetUtil.DATA_TYPE_BIMAP.get(dataType);
		
		final long id = idValues.get(doc);
		final long storageKey = storageKeyValues.get(doc);
		
		final BytesRef value = deepCopyOf(valueValues.get(doc));
		final BytesRef label = deepCopyOf(labelValues.get(doc));
		
		final long refSetId = refSetIdValues.get(doc);
		
		final ConcreteDomainFragment fragment = new ConcreteDomainFragment(
				value, 
				label, 
				(byte) convertedDataType.ordinal(), 
				uomId, 
				storageKey,
				refSetId);
		
		if (dataTypeMap.containsKey(id)) {
			
			final List<ConcreteDomainFragment> dataTypes = getDataTypesById(id);
			dataTypes.add(fragment);
			
		} else {
			
			final List<ConcreteDomainFragment> dataTypes = Lists.newArrayList();
			dataTypes.add(fragment);
			dataTypeMap.put(id, dataTypes);
			
		}
		
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
	 */
	@Override
	public void setNextReader(AtomicReaderContext context) throws IOException {
		
		Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.");

		storageKeyValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY);
		if (null == storageKeyValues) {
			resetValues();
			return;
		}
		
		uomValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UOM_ID);
		
		valueValues = context.reader().getBinaryDocValues(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SERIALIZED_VALUE);
		if (null == valueValues) {
			resetValues();
			return;
		}
		
		labelValues = context.reader().getBinaryDocValues(SnomedIndexBrowserConstants.COMPONENT_LABEL);
		if (null == labelValues) {
			resetValues();
			return;
		}
		
		idValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID);
		if (null == idValues) {
			resetValues();
			return;
		}
		
		typeValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DATA_TYPE_VALUE);
		if (null == typeValues) {
			resetValues();
			return;
		}
		
		refSetIdValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID);
		if (null == refSetIdValues) {
			resetValues();
			return;
		}
	}
	
	/**
	 * Returns with a map of data types.
	 * <ul>
	 * <li>Keys: SNOMED CT component IDs.</li>
	 * <li>Values: a list of data types. Can be {@code null}.</li>
	 * </ul>
	 * @return the a map of data types collected as an outcome of an index search.
	 */
	public LongKeyMap getDataTypeMap() {
		return dataTypeMap;
	}
	
	/*sets the reference on the values to null*/
	private void resetValues() {
		storageKeyValues = null;
		uomValues = null;
		valueValues = null;
		labelValues = null;
		idValues = null;
		typeValues = null;
		refSetIdValues = null;
	}
	
	/*returns true only and if only all the backing values can be referenced*/
	private boolean checkValues() {
		return null != storageKeyValues
			&& null != valueValues
			&& null != labelValues
			&& null != idValues
			&& null != typeValues
			&& null != refSetIdValues;
	}
	
	/*returns with a list of data types associated with the specified SNOMED CT component ID*/
	@SuppressWarnings("unchecked")
	private List<ConcreteDomainFragment> getDataTypesById(final long id) {
		return (List<ConcreteDomainFragment>) dataTypeMap.get(id);
	}


}