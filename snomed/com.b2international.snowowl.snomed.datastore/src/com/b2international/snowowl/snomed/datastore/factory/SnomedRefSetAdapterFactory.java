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

import org.eclipse.emf.spi.cdo.FSMUtil;

import com.b2international.commons.TypeSafeAdapterFactory;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;

/**
 * Adapter factory implementation for SNOMED CT reference sets.
 */
public class SnomedRefSetAdapterFactory extends TypeSafeAdapterFactory {

	public SnomedRefSetAdapterFactory() {
		super(IComponent.class, SnomedConceptDocument.class);
	}

	@Override
	protected <T> T getAdapterSafe(final Object adaptableObject, final Class<T> adapterType) {

		if (adaptableObject instanceof SnomedConceptDocument) {
			return adapterType.cast(adaptableObject);
		} 

		if (adaptableObject instanceof SnomedRefSet) {
			final SnomedRefSet refSet = (SnomedRefSet) adaptableObject;
			final Concept identifierConcept = new SnomedConceptLookupService().getComponent(refSet.getIdentifierId(), refSet.cdoView());
			final SnomedConceptDocument refSetIndexEntry;
			
			if (FSMUtil.isClean(refSet) && FSMUtil.isClean(identifierConcept) && !refSet.cdoRevision().isHistorical() && !identifierConcept.cdoRevision().isHistorical()) {
				refSetIndexEntry = new SnomedRefSetLookupService().getComponent(BranchPathUtils.createPath(refSet), refSet.getIdentifierId());
			} else {
				refSetIndexEntry = SnomedConceptDocument.builder()
						.id(refSet.getIdentifierId()) 
						.iconId(refSet.getIdentifierId()) // XXX: An IconProvider might give an exact value here, but this is OK
						.moduleId(identifierConcept.getModule().getId())
						.active(identifierConcept.isActive())
						.released(identifierConcept.isReleased())
						.effectiveTime(identifierConcept.isSetEffectiveTime() ? identifierConcept.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
						.refSet(refSet).build();
			}

			return adapterType.cast(refSetIndexEntry);
		}

		return null;
	}
}
