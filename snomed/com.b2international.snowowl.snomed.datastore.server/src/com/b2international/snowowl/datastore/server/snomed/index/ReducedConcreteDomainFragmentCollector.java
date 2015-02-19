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

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.ByteBlockPool.DirectAllocator;
import org.apache.lucene.util.BytesRefHash;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.google.common.base.Preconditions;

/**
 * Collector gathering concrete domains with distinct labels. The gathered labels 
 * will be grouped by the {@link DataType data type type}. 
 */
public class ReducedConcreteDomainFragmentCollector extends AbstractDocsOutOfOrderCollector {
	
	private final BytesRefHash booleanLabels;
	private final BytesRefHash dateLabels;
	private final BytesRefHash floatLabels;
	private final BytesRefHash integerLabels;
	private final BytesRefHash stringLabels;
	
	private BinaryDocValues labelValues;
	private NumericDocValues typeValues;
	
	public ReducedConcreteDomainFragmentCollector() {
		booleanLabels = new BytesRefHash(new ByteBlockPool(new DirectAllocator()));
		dateLabels = new BytesRefHash(new ByteBlockPool(new DirectAllocator()));
		floatLabels = new BytesRefHash(new ByteBlockPool(new DirectAllocator()));
		integerLabels = new BytesRefHash(new ByteBlockPool(new DirectAllocator()));
		stringLabels = new BytesRefHash(new ByteBlockPool(new DirectAllocator()));
	}
	
	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#collect(int)
	 */
	@Override
	public void collect(final int doc) throws IOException {

		if (!checkSources()) {
			return; //cannot references direct sources
		}
		
		final byte type = (byte) typeValues.get(doc);
		getMrcmDataTypeToRefHash(type).add(deepCopyOf(labelValues.get(doc)));
		
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
	 */
	@Override
	public void setNextReader(final AtomicReaderContext context) throws IOException {

		Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.");
		
		labelValues = context.reader().getBinaryDocValues(SnomedIndexBrowserConstants.COMPONENT_LABEL);
		if (null == labelValues) {
			resetValues();
			return;
		}
		
		typeValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DATA_TYPE_VALUE);
		if (null == typeValues) {
			resetValues();
			return;
		}
		
	}
	
	/*sets the reference on the sources to null*/
	private void resetValues() {
		labelValues = null;
		typeValues = null;
	}
	
	/*returns true only and if only all the backing sources can be referenced*/
	private boolean checkSources() {
		return null != labelValues
			&& null != typeValues;
	}

	/**
	 * Returns with all distinct labels for the given {@link DataType data types}.
	 * @param dataType the data type.
	 * @return 
	 */
	public BytesRefHash getLabels(final DataType dataType) {
		return getMrcmDataTypeToRefHash((byte) dataType.ordinal());
	}
	
	/*returns with the proper bytes ref hash for the given MRCM data type ordinal*/
	private BytesRefHash getMrcmDataTypeToRefHash(final byte dataTypeOrdinal) {
		
		switch (dataTypeOrdinal) {
			
			case 0: return booleanLabels;
			case 1: return dateLabels;
			case 2: return floatLabels;
			case 3: return integerLabels;
			case 4: return stringLabels;
			default: throw new IllegalArgumentException("Cannot specify MRCM data type based on its ordinal: " + dataTypeOrdinal);
			
 		}		
	}
	
}