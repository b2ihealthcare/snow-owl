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

import static com.b2international.commons.pcj.LongHashFunctionAdapter.hashOf;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.hash.Hashing.murmur3_32;

import java.io.IOException;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;

/**
 * Abstract implementation of a SNOMED&nbsp;CT component property collector.
 *
 */
public abstract class ComponentPropertyCollector extends AbstractDocsOutOfOrderCollector {

	protected LongCollection acceptedIds;
	protected LongKeyOpenHashMap mapping;
	protected NumericDocValues storageKeys;

	public ComponentPropertyCollector(final LongCollection acceptedIds) {
		this.acceptedIds = checkNotNull(acceptedIds, "acceptedIds");
		mapping = new LongKeyOpenHashMap(hashOf(murmur3_32()));
	}
	
	@Override
	public void setNextReader(final AtomicReaderContext context) throws IOException {
		setNextReader(context.reader());
	}

	
	@Override
	public void collect(final int doc) throws IOException {
		if (check()) {
			final long storageKey = getStorageKey(doc);
			if (isAccepted(storageKey)) {
				mapping.put(storageKey, initProperties(doc));
			}
		}
	}

	/**
	 * Returns with the mappings for the components collected by this collector instance.
	 */
	public LongKeyMap getMapping() {
		return mapping;
	}
	
	/**
	 * Creates the {@link NumericDocValues} based on the reader argument.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void setNextReader(final AtomicReader reader) throws IOException {
		storageKeys = reader.getNumericDocValues(SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY);
	}

	/**
	 * Initialize and returns with the component properties.
	 */
	protected abstract Object initProperties(int doc);

	/**
	 * Returns with {@code true} if the given storage key argument has
	 * to be processed.
	 */
	protected boolean isAccepted(long storageKey) {
		return acceptedIds.contains(storageKey);
	}

	/**
	 * Checks the underlying {@link NumericDocValues} if any.
	 * <br>Returns with {@code true} by default.
	 */
	protected boolean check() {
		return true;
	}

	private long getStorageKey(final int doc) {
		return storageKeys.get(doc);
	}
	
}