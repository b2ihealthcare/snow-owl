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
package com.b2international.snowowl.snomed.datastore.index.update;

import java.util.Map;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollections;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.3
 */
public class RefSetParentageUpdater extends ParentageUpdater {

	private Map<String, SnomedRefSetType> referenceSetIdToTypeMap;

	public RefSetParentageUpdater(ISnomedTaxonomyBuilder taxonomyBuilder, Map<String, SnomedRefSetType> referenceSetIdToTypeMap, boolean stated) {
		super(taxonomyBuilder, stated);
		this.referenceSetIdToTypeMap = referenceSetIdToTypeMap;
	}
	
	@Override
	protected LongCollection getParentIds(String conceptId) {
		final LongCollection result = super.getParentIds(conceptId);
		if (result.isEmpty() && referenceSetIdToTypeMap.containsKey(conceptId)) {
			final SnomedRefSetType type = referenceSetIdToTypeMap.get(conceptId);
			return LongCollections.singletonSet(Long.parseLong(SnomedRefSetUtil.getConceptId(type))); 
		}
		return result;
	}
	
	@Override
	protected LongCollection getAncestorIds(String conceptId) {
		final LongCollection result = super.getAncestorIds(conceptId);
		if (result.isEmpty() && referenceSetIdToTypeMap.containsKey(conceptId)) {
			final SnomedRefSetType type = referenceSetIdToTypeMap.get(conceptId);
			return getTaxonomyBuilder().getAllAncestorNodeIds(SnomedRefSetUtil.getConceptId(type));
		}
		return result;
	}
	
}
