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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.3
 */
public class RefSetIconIdUpdater extends IconIdUpdater {

	private Map<String, SnomedRefSetType> referenceSetIdToTypeMap;

	public RefSetIconIdUpdater(ISnomedTaxonomyBuilder taxonomyBuilder, String conceptId, boolean active, Collection<String> availableImages, Map<String, SnomedRefSetType> referenceSetIdToTypeMap) {
		super(taxonomyBuilder, conceptId, active, availableImages);
		this.referenceSetIdToTypeMap = referenceSetIdToTypeMap == null ? Collections.<String, SnomedRefSetType>emptyMap() : referenceSetIdToTypeMap;
	}
	
	@Override
	protected String getIconId(String conceptId, boolean active) {
		final String originalIconId = super.getIconId(conceptId, active);
		if (active && Concepts.ROOT_CONCEPT.equals(originalIconId)) {
			if (referenceSetIdToTypeMap.containsKey(conceptId)) {
				return SnomedRefSetUtil.getConceptId(referenceSetIdToTypeMap.get(conceptId));
			}
		}
		return originalIconId;
	}
	

}
