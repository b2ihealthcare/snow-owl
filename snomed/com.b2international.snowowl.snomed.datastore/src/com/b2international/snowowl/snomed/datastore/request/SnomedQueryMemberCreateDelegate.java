/*
 * Copyright 2017-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.cis.action.IdActionRecorder;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
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
	public String execute(SnomedReferenceSet refSet, TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.QUERY);

		if (Strings.isNullOrEmpty(getReferencedComponentId())) {
			return createWithNewRefSet(context, refSet);
		} else {
			return createWithExistingRefSet(refSet, context);
		}
	}

	private String createWithNewRefSet(TransactionContext context, SnomedReferenceSet refSet) {
		checkNonEmptyProperty(REFERENCED_COMPONENT);
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());

		// create new simple type reference set
		final SnomedConcept referencedComponent = getProperty(REFERENCED_COMPONENT, SnomedConcept.class);
		final String referencedComponentId = referencedComponent.toCreateRequest().execute(context);
		// make sure we register the ID
		context.service(IdActionRecorder.class).register(Set.of(referencedComponentId));

		// write the generated ID back to the request  
		setReferencedComponentId(referencedComponentId);

		// add all matching members 
		if (!Strings.isNullOrEmpty(getProperty(SnomedRf2Headers.FIELD_QUERY))) {
			int pageSize = context.service(RepositoryConfiguration.class).getIndexConfiguration().getResultWindow();
			
			SnomedRequests.prepareSearchConcept()
				.setFields(SnomedConceptDocument.Fields.ID)
				.setLimit(pageSize)
				.filterByEcl(getProperty(SnomedRf2Headers.FIELD_QUERY))
				.build()
				.execute(context)
				.stream()
				.forEach(concept -> {
					SnomedComponents.newSimpleMember()
					.withActive(isActive())
					.withReferencedComponent(concept.getId())
					.withModuleId(getModuleId())
					.withRefSet(referencedComponentId)
					.addTo(context);
				});
		}

		return createWithExistingRefSet(refSet, context);
	}

	private String createWithExistingRefSet(SnomedReferenceSet refSet, TransactionContext context) {
		checkReferencedComponent(refSet);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());

		SnomedRefSetMemberIndexEntry member = SnomedComponents.newQueryMember()
				.withId(getId())
				.withActive(isActive())
				.withReferencedComponent(getReferencedComponentId())
				.withModuleId(getModuleId())
				.withRefSet(getReferenceSetId())
				.withQuery(getProperty(SnomedRf2Headers.FIELD_QUERY))
				.addTo(context);

		return member.getId();
	}

}
