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

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.BytesRefHash;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * Collector gathering concrete domains with distinct labels. The gathered labels will be grouped by {@link DataType
 * concrete domain}.
 */
public class ReducedConcreteDomainFragmentCollector extends AbstractDocsOutOfOrderCollector {

	private final BytesRefHash booleanLabels = new BytesRefHash();
	private final BytesRefHash dateLabels = new BytesRefHash();
	private final BytesRefHash floatLabels = new BytesRefHash();
	private final BytesRefHash integerLabels = new BytesRefHash();
	private final BytesRefHash stringLabels = new BytesRefHash();

	private BinaryDocValues labelValues;
	private NumericDocValues typeValues;

	@Override
	protected void initDocValues(final AtomicReader reader) throws IOException {
		labelValues = SnomedMappings.memberDataTypeLabel().getDocValues(reader);
		typeValues = SnomedMappings.memberDataTypeOrdinal().getDocValues(reader);
	}

	@Override
	protected boolean isLeafCollectible() {
		return labelValues != null && typeValues != null;
	}

	@Override
	public void collect(final int docId) throws IOException {
		final byte type = (byte) typeValues.get(docId);
		getBytesRefHash(type).add(deepCopyOf(labelValues.get(docId)));
	}

	/**
	 * Returns all distinct labels for the given {@link DataType data type}.
	 * 
	 * @param dataType the data type to check
	 * @return labels associated with the data type
	 */
	public BytesRefHash getLabels(final DataType dataType) {
		return getBytesRefHash((byte) dataType.ordinal());
	}

	private BytesRefHash getBytesRefHash(final byte dataTypeOrdinal) {

		switch (dataTypeOrdinal) {
			case 0: return integerLabels;
			case 1: return floatLabels;
			case 2: return booleanLabels;
			case 3: return dateLabels;
			case 4: return stringLabels;

			default: throw new IllegalArgumentException("Cannot specify MRCM data type based on its ordinal: " + dataTypeOrdinal);
		}		
	}
}
