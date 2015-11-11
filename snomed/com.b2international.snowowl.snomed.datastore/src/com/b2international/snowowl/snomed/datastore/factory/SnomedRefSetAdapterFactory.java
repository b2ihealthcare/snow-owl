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
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry.Builder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;

/**
 * Adapter factory implementation for SNOMED CT reference sets.
 */
public class SnomedRefSetAdapterFactory extends TypeSafeAdapterFactory {

	public SnomedRefSetAdapterFactory() {
		super(IComponent.class, SnomedRefSetIndexEntry.class);
	}

	@Override
	protected <T> T getAdapterSafe(final Object adaptableObject, final Class<T> adapterType) {

		if (adaptableObject instanceof SnomedRefSetIndexEntry) {
			return adapterType.cast(adaptableObject);
		} 

		if (adaptableObject instanceof SnomedRefSet) {
			final SnomedRefSet refSet = (SnomedRefSet) adaptableObject;
			final Concept identifierConcept = new SnomedConceptLookupService().getComponent(refSet.getIdentifierId(), refSet.cdoView());
			final Builder builder = SnomedRefSetIndexEntry.builder()
					.id(refSet.getIdentifierId()) 
					.moduleId(identifierConcept.getModule().getId())
					.storageKey(CDOIDUtils.asLongSafe(refSet.cdoID()))
					.active(identifierConcept.isActive())
					.released(identifierConcept.isReleased())
					.effectiveTimeLong(identifierConcept.isSetEffectiveTime() ? identifierConcept.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
					.type(refSet.getType()) 
					.referencedComponentType(refSet.getReferencedComponentType())
					.structural(refSet instanceof SnomedStructuralRefSet);

			if (refSet instanceof SnomedMappingRefSet) {
				builder.mapTargetComponentType(((SnomedMappingRefSet) refSet).getMapTargetComponentType());
			}

			return adapterType.cast(builder.build());
		}

		return null;
	}
}
