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
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;

/**
 * Adapter factory implementation for SNOMED CT descriptions.
 */
public class SnomedDescriptionAdapterFactory extends TypeSafeAdapterFactory {

	public SnomedDescriptionAdapterFactory() {
		super(IComponent.class, SnomedDescriptionIndexEntry.class);
	}

	@Override
	protected <T> T getAdapterSafe(final Object adaptableObject, final Class<T> adapterType) {

		if (adaptableObject instanceof SnomedDescriptionIndexEntry) {
			return adapterType.cast(adaptableObject);
		}

		if (adaptableObject instanceof Description) {

			final Description description = (Description) adaptableObject;
			final SnomedDescriptionIndexEntry adaptedEntry = SnomedDescriptionIndexEntry.builder()
					.id(description.getId()) 
					.term(description.getTerm())
					.moduleId(description.getModule().getId())
					.storageKey(CDOUtils.getStorageKey(description))
					.released(description.isReleased()) 
					.active(description.isActive()) 
					.typeId(description.getType().getId()) 
					.caseSignificanceId(description.getCaseSignificance().getId()) 
					.conceptId(description.getConcept().getId())
					.languageCode(description.getLanguageCode())
					.effectiveTimeLong(description.isSetEffectiveTime() ? description.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
					.build();

			return adapterType.cast(adaptedEntry);
		}

		return null;
	}
}
