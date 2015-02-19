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

import static com.b2international.commons.pcj.LongSets.isEmpty;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCache.Longs;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongMapIterator;
import bak.pcj.map.LongKeyLongOpenHashMap;

import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Collector for gathering the icon IDs for SNOMED&nbsp;CT concept using the {@link FieldCache}.
 */
public class SnomedConceptIconIdCollector extends AbstractDocsOutOfOrderCollector {

	private final LongCollection conceptIds;
	private final LongKeyLongMap idIconIdMapping;

	private Longs longs;
	private NumericDocValues idDocValues;
	
	public SnomedConceptIconIdCollector(final LongCollection conceptIds) {
		this.conceptIds = Preconditions.checkNotNull(conceptIds, "Concept IDs argument cannot be null.");
		idIconIdMapping = new LongKeyLongOpenHashMap(isEmpty(conceptIds) ? 1 : this.conceptIds.size());
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#collect(int)
	 */
	@Override
	public void collect(final int doc) throws IOException {
		
		if (null != idDocValues) {
				
			final long conceptId = idDocValues.get(doc);
			
			if (conceptIds.contains(conceptId)) {
				
				final long iconId = longs.get(doc);
				idIconIdMapping.put(conceptId, iconId);
				
			}
			
			
		}
		
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
	 */
	@Override
	public void setNextReader(final AtomicReaderContext context) throws IOException {

		Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.");

		longs = FieldCache.DEFAULT.getLongs(
				Preconditions.checkNotNull(context.reader(), "Index reader cannot be null for " + context), 
				CommonIndexConstants.COMPONENT_ICON_ID, 
				false);
		
		
		idDocValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.COMPONENT_ID);
		
	}

	/**
	 * Returns with a map of concept IDs and the concept's icon ID.
	 * @return the map between concept IDs and icon IDs. 
	 */
	public LongKeyLongMap getIdIconIdMapping() {
		return idIconIdMapping;
	}

	/**
	 * Returns with a map of concept IDs and the concept's icon ID.
	 * @return the map between concept IDs and icon IDs. 
	 */
	public Map<String, String> getIdIconIdStringMapping() {
		
		final Map<String, String> $ = Maps.newHashMapWithExpectedSize(idIconIdMapping.size());
		
		for (final LongKeyLongMapIterator itr = idIconIdMapping.entries(); itr.hasNext(); /**/) {
			
			itr.next();
			
			$.put(
					Long.toString(itr.getKey()), //ID
					Long.toString(itr.getValue())); //icon ID
			
		}
		
		return $;
		
	}

}