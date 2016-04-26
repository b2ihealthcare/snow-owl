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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.google.common.hash.Hashing;

/**
 * Abstract implementation of a SNOMED CT component property collector.
 */
public abstract class ComponentPropertyCollector extends AbstractDocsOutOfOrderCollector {

	protected LongCollection acceptedStorageKeys;
	protected LongKeyMap<long[]> mapping;
	protected NumericDocValues storageKeys;

	public ComponentPropertyCollector(final LongCollection acceptedStorageKeys) {
		this.acceptedStorageKeys = checkNotNull(acceptedStorageKeys, "acceptedStorageKeys");
		this.mapping = PrimitiveMaps.newLongKeyOpenHashMap(Hashing.murmur3_32());
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		storageKeys = Mappings.storageKey().getDocValues(leafReader);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Note:</b> Overriding methods must invoke {@code super.isLeafCollectible()} as part of their return value
	 * expression.
	 */
	@Override
	protected boolean isLeafCollectible() {
		return storageKeys != null;
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long storageKey = getStorageKey(docId);
		if (isStorageKeyAccepted(storageKey)) {
			registerMapping(storageKey, docId);
		}
	}

	private long getStorageKey(final int docId) {
		return storageKeys.get(docId);
	}

	private boolean isStorageKeyAccepted(final long storageKey) {
		return acceptedStorageKeys.contains(storageKey);
	}

	private void registerMapping(final long storageKey, final int docId) {
		mapping.put(storageKey, collectProperties(docId));
	}

	/**
	 * Collects component properties for the specified document identifier.
	 * 
	 * @param docId the segment-relative document identifier to use for collection
	 * 
	 * @return the collected long values in a {@code long[]}
	 */
	protected abstract long[] collectProperties(int docId);

	/**
	 * Returns collected long values, keyed by storage key.
	 * 
	 * @return the created storage key mapping
	 */
	public LongKeyMap<long[]> getMapping() {
		return mapping;
	}
}
