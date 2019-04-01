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
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 6.14
 */
public final class DescriptionEffectiveTimeRestorer extends ComponentEffectiveTimeRestorer {

	@Override
	protected boolean canRestoreComponentEffectiveTime(Component componentToRestore, SnomedComponent previousVersion) {
		final Description descriptionToRestore = (Description) componentToRestore;
		final SnomedDescription previousDescriptionVersion = (SnomedDescription) previousVersion;
		
		if (!descriptionToRestore.getConcept().getId().equals(previousDescriptionVersion.getConceptId())) return false;
		if (!descriptionToRestore.getLanguageCode().equals(previousDescriptionVersion.getLanguageCode())) return false;
		if (!descriptionToRestore.getType().getId().equals(previousDescriptionVersion.getTypeId())) return false;
		if (!descriptionToRestore.getTerm().equals(previousDescriptionVersion.getTerm())) return false;
		if (!descriptionToRestore.getCaseSignificance().getId().equals(previousDescriptionVersion.getCaseSignificance().getConceptId())) return false;
		
		return true;
	}

	@Override
	protected SnomedComponent getVersionedComponent(String branch, String componentId) {
		return SnomedRequests.prepareGetDescription(componentId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.get();
	}




}
