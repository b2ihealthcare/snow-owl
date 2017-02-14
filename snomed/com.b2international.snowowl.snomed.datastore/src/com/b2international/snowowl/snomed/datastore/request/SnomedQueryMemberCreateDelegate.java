/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;

/**
 * @since 5.0
 */
final class SnomedQueryMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	// "Synthetic" reference set member property that carries an entire SnomedConcept
	private static final String REFERENCED_COMPONENT = "referencedComponent";

	SnomedQueryMemberCreateDelegate(SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	@Override
	public String execute(SnomedRefSet refSet, TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.QUERY);
		checkNonEmptyProperty(refSet, SnomedRf2Headers.FIELD_QUERY);

		if (Strings.isNullOrEmpty(getReferencedComponentId())) {
			return createWithNewRefSet(refSet, context);
		} else {
			return createWithExistingRefSet(refSet, context);
		}
	}

	private String createWithNewRefSet(SnomedRefSet refSet, TransactionContext context) {
		checkNonEmptyProperty(refSet, REFERENCED_COMPONENT);

		// create new simple type reference set
		final SnomedConcept referencedComponent = getProperty(REFERENCED_COMPONENT, SnomedConcept.class);
		final String referencedComponentId = new IdRequest<>(referencedComponent.toCreateRequest()).execute(context);

		// write the generated ID back to the request  
		setReferencedComponentId(referencedComponentId);

		// add all matching members 
		final SnomedConcepts queryResults = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByEscg(getProperty(SnomedRf2Headers.FIELD_QUERY))
				.build()
				.execute(context);

		for (SnomedConcept queryResult : queryResults) {
			SnomedComponents.newSimpleMember()
					.withActive(isActive())
					.withReferencedComponent(queryResult.getId())
					.withModule(getModuleId())
					.withRefSet(referencedComponentId)
					.addTo(context);
		}


		return createWithExistingRefSet(refSet, context);
	}

	private String createWithExistingRefSet(SnomedRefSet refSet, TransactionContext context) {
		checkReferencedComponentId(refSet);

		SnomedQueryRefSetMember member = SnomedComponents.newQueryMember()
				.withActive(isActive())
				.withReferencedComponent(getReferencedComponentId())
				.withModule(getModuleId())
				.withRefSet(getReferenceSetId())
				.withQuery(getProperty(SnomedRf2Headers.FIELD_QUERY))
				.addTo(context);

		return member.getUuid();
	}

}
