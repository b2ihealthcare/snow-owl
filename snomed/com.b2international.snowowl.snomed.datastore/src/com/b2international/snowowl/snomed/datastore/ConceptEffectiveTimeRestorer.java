/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 6.14
 */
public final class ConceptEffectiveTimeRestorer extends ComponentEffectiveTimeRestorer {

	@Override
	protected boolean canRestoreComponentEffectiveTime(Component componentToRestore, SnomedComponent previousVersion) {
		final Concept conceptToRestore = (Concept) componentToRestore;
		final SnomedConcept previousVersionConcept = (SnomedConcept) previousVersion;
		
		return conceptToRestore.getDefinitionStatus().getId().equals(previousVersionConcept.getDefinitionStatus().getConceptId());
	}

	@Override
	protected SnomedComponent getVersionedComponent(String branch, String conceptId) {
		return SnomedRequests.prepareGetConcept(conceptId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.get();
	}


}
