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
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongMapIterator;
import bak.pcj.map.LongKeyLongOpenHashMap;

import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;

/**
 * Collector for gathering the icon IDs for SNOMED CT concepts.
 */
public class SnomedConceptIconIdCollector extends AbstractDocsOutOfOrderCollector {

	private final LongCollection conceptIds;
	private final LongKeyLongMap idIconIdMapping;

	private NumericDocValues idDocValues;
	private NumericDocValues iconIdDocValues;

	/**
	 * Creates a new instance with the specified accepted concept identifiers.
	 * 
	 * @param conceptIds the collection of concept identifiers to accept
	 */
	public SnomedConceptIconIdCollector(final LongCollection conceptIds) {
		this.conceptIds = checkNotNull(conceptIds, "Concept identifier collection may not be null.");
		this.idIconIdMapping = new LongKeyLongOpenHashMap(LongSets.isEmpty(conceptIds) ? 1 : conceptIds.size());
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long conceptId = idDocValues.get(docId);

		if (conceptIds.contains(conceptId)) {
			final long iconId = iconIdDocValues.get(docId);
			idIconIdMapping.put(conceptId, iconId);
		}
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		idDocValues = leafReader.getNumericDocValues(CommonIndexConstants.COMPONENT_ID);
		iconIdDocValues = leafReader.getNumericDocValues(CommonIndexConstants.COMPONENT_ICON_ID);
	}

	@Override
	protected boolean isLeafCollectible() {
		return idDocValues != null && iconIdDocValues != null;
	}

	/**
	 * Returns a map of icon identifiers keyed by concept identifiers.
	 * 
	 * @return the map between concept IDs and icon IDs
	 */
	public LongKeyLongMap getIdIconIdMapping() {
		return idIconIdMapping;
	}

	/**
	 * Returns a map of icon identifiers keyed by concept identifiers, where keys and values are all Strings.
	 * 
	 * @return the map between concept IDs and icon IDs
	 */
	public Map<String, String> getIdIconIdStringMapping() {
		final Map<String, String> result = newHashMapWithExpectedSize(idIconIdMapping.size());

		for (final LongKeyLongMapIterator itr = idIconIdMapping.entries(); itr.hasNext(); /**/) {
			itr.next();
			result.put(Long.toString(itr.getKey()), Long.toString(itr.getValue()));
		}

		return result;
	}
}
