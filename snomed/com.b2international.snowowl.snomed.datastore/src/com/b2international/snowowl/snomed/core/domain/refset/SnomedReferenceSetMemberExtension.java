/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.refset;

import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.core.sets.SetMemberExtension;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator; 
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
* @since 7.7
*/
@Component
public class SnomedReferenceSetMemberExtension 
	implements SetMemberExtension<SnomedConceptSearchRequestBuilder, SnomedConcept, SnomedConcepts> {

	
	private static final long serialVersionUID = 1L;

	@Override
	public short terminologyComponentId() {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
	}

	@Override
	public String sourceCode(SnomedConcept member) {
			return member.getId();
	}

	@Override
	public String sourceCodeSystem(String codeSystem, SnomedConcept member) {
		return codeSystem;
	}

	@Override
	public String sourceTerm(SnomedConcept member) {
		return member.getPreferredDescriptions().first().get().getTerm();
	}

	@Override
	public SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> memberIterator(
			BranchContext context,
			String branch,
			String codeSystem, 
			String componentId) {
		return new SearchResourceRequestIterator<>(
				SnomedRequests.prepareSearchConcept().isActiveMemberOf(componentId).setExpand("preferredDescriptions()").setLimit(10_000), 
				r -> r.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch).execute(context.service(IEventBus.class)).getSync());
	}

}
