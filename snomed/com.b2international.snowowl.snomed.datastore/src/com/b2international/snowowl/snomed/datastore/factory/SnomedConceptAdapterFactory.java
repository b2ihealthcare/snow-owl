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
package com.b2international.snowowl.snomed.datastore.factory;

import com.b2international.commons.TypeSafeAdapterFactory;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

/**
 * Adapter factory implementation for SNOMED CT concepts.
 */
public class SnomedConceptAdapterFactory extends TypeSafeAdapterFactory {

	public SnomedConceptAdapterFactory() {
		super(IComponent.class, SnomedConceptIndexEntry.class);
	}

	@Override
	protected <T> T getAdapterSafe(final Object adaptableObject, final Class<T> adapterType) {

		if (adaptableObject instanceof SnomedConceptIndexEntry) {
			return adapterType.cast(adaptableObject);
		} 

		if (adaptableObject instanceof Concept) {

			final Concept concept = (Concept) adaptableObject;
			final SnomedConceptIndexEntry adaptedEntry = SnomedConceptIndexEntry.builder()
					.id(concept.getId())
					.iconId(SnomedIconProvider.getInstance().getIconComponentId(concept.getId())) 
					.moduleId(concept.getModule().getId()) 
					.storageKey(CDOUtils.getStorageKey(concept))
					.active(concept.isActive())
					.primitive(concept.isPrimitive())
					.exhaustive(concept.isExhaustive())
					.released(concept.isReleased()) 
					.effectiveTimeLong(concept.isSetEffectiveTime() ? concept.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
					.build();

			return adapterType.cast(adaptedEntry);
		}

		return null;
	}
}
