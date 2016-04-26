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

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.BytesRef;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * Custom collector for getting the bare minimum of a data type for the classification process.
 */
public class ConcreteDomainFragmentCollector extends AbstractDocsOutOfOrderCollector {

	private static final int DEFAULT_SIZE = 100000;

	private NumericDocValues uomValues;

	private BinaryDocValues valueValues;
	private BinaryDocValues labelValues;
	private NumericDocValues referencedIdValues;
	private NumericDocValues typeValues;
	private NumericDocValues storageKeyValues;
	private NumericDocValues refSetIdValues;

	private final LongKeyMap<Collection<ConcreteDomainFragment>> dataTypeMap;

	/**
	 * Creates a data type collector instance with the default expected size ({@value #DEFAULT_SIZE} items).
	 */
	public ConcreteDomainFragmentCollector() {
		this(DEFAULT_SIZE);
	}

	/**
	 * Creates a data type collector instance with an initial expected size for the backing map.
	 * 
	 * @param expectedSize the expected size
	 */
	public ConcreteDomainFragmentCollector(final int expectedSize) {
		dataTypeMap = (0 > expectedSize) 
				? PrimitiveMaps.<Collection<ConcreteDomainFragment>>newLongKeyOpenHashMapWithExpectedSize(expectedSize)
				: PrimitiveMaps.<Collection<ConcreteDomainFragment>>newLongKeyOpenHashMap();
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		uomValues = SnomedMappings.memberUomId().getDocValues(leafReader);
		valueValues = SnomedMappings.memberSerializedValue().getDocValues(leafReader);
		labelValues = SnomedMappings.memberDataTypeLabel().getDocValues(leafReader);
		referencedIdValues = SnomedMappings.memberReferencedComponentId().getDocValues(leafReader);
		typeValues = SnomedMappings.memberDataTypeOrdinal().getDocValues(leafReader);
		storageKeyValues = Mappings.storageKey().getDocValues(leafReader);
		refSetIdValues = SnomedMappings.memberRefSetId().getDocValues(leafReader);
	}

	@Override
	protected boolean isLeafCollectible() {
		// XXX: uomValues is ignored deliberately below
		return valueValues != null
				&& labelValues != null
				&& referencedIdValues != null
				&& typeValues != null
				&& storageKeyValues != null
				&& refSetIdValues != null;
	}

	@Override
	public void collect(final int docId) throws IOException {

		long uomId = (null == uomValues) ? 0L : uomValues.get(docId);

		if (uomId == 0L) {
			uomId = ConcreteDomainFragment.UNSET_UOM_ID;
		}

		final byte type = (byte) typeValues.get(docId);

		final long id = referencedIdValues.get(docId);
		final long storageKey = storageKeyValues.get(docId);

		final BytesRef value = BytesRef.deepCopyOf(valueValues.get(docId));
		final BytesRef label = BytesRef.deepCopyOf(labelValues.get(docId));

		final long refSetId = refSetIdValues.get(docId);

		final ConcreteDomainFragment fragment = new ConcreteDomainFragment(
				value, 
				label, 
				type, 
				uomId, 
				storageKey,
				refSetId);

		final List<ConcreteDomainFragment> dataTypes;

		if (dataTypeMap.containsKey(id)) {
			dataTypes = (List<ConcreteDomainFragment>) getDataTypesById(id);
		} else {
			dataTypes = newArrayList();
			dataTypeMap.put(id, dataTypes);
		}

		dataTypes.add(fragment);
	}

	/**
	 * Returns a multimap of concrete domain fragments, keyed by referenced component identifier.
	 * 
	 * @return the collected map of data types
	 */
	public LongKeyMap<Collection<ConcreteDomainFragment>> getDataTypeMap() {
		return dataTypeMap;
	}

	private Collection<ConcreteDomainFragment> getDataTypesById(final long id) {
		return dataTypeMap.get(id);
	}
}
