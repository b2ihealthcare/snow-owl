/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.SetMember;
import com.b2international.snowowl.core.domain.SetMembers;
import com.b2international.snowowl.core.request.SetMemberSearchRequestEvaluator;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.7
 */
public final class SnomedMemberSearchRequestEvaluator implements SetMemberSearchRequestEvaluator {

	@Override
	public SetMembers evaluate(ResourceURI uri, BranchContext context, Options search) {
		SnomedReferenceSetMembers referenceSetMembers = fetchRefsetMembers(uri, context, search);
		return toCollectionResource(referenceSetMembers, uri);
	}

	private SetMembers toCollectionResource(SnomedReferenceSetMembers referenceSetMembers, ResourceURI uri) {
		return new SetMembers(referenceSetMembers.stream().map(m -> toMember(m, uri)).collect(Collectors.toList()),
				referenceSetMembers.getSearchAfter(),
				referenceSetMembers.getLimit(),
				referenceSetMembers.getTotal());
	}

	private SetMember toMember(SnomedReferenceSetMember member, ResourceURI codeSystemURI) {	 		
		final String term;		
		final String iconId = member.getReferencedComponent().getIconId();
		short terminologyComponentId = member.getReferencedComponent().getTerminologyComponentId();
		switch (terminologyComponentId) {
		case SnomedTerminologyComponentConstants.CONCEPT_NUMBER: 
			SnomedConcept concept = (SnomedConcept) member.getReferencedComponent();
			term = concept.getFsn().getTerm();
			break;
		case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
			SnomedDescription description = (SnomedDescription) member.getReferencedComponent();
			term = description.getTerm();
			break;
		default: term = member.getReferencedComponentId();
		}

		return new SetMember(ComponentURI.of(codeSystemURI.getResourceId(), terminologyComponentId, member.getReferencedComponentId()),
				term, 
				iconId);
	}

	private SnomedReferenceSetMembers fetchRefsetMembers(ResourceURI uri, BranchContext context, Options search) {

		final Integer limit = search.get(OptionKey.LIMIT, Integer.class);
		final String searchAfter = search.get(OptionKey.AFTER, String.class);
		final List<ExtendedLocale> locales = search.getList(OptionKey.LOCALES, ExtendedLocale.class);

		SnomedRefSetMemberSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchMember();

		if (search.containsKey(OptionKey.SET)) {
			final Collection<String> refsetId = search.getCollection(OptionKey.SET, String.class);
			requestBuilder.filterByRefSet(refsetId);
		}
		
		return requestBuilder
				.filterByActive(true)
				.filterByRefSetType(getSympleTypeRefSets())
				.setLocales(locales)
				.setExpand("referencedComponent(expand(fsn()))")
				.setLimit(limit)
				.setSearchAfter(searchAfter)
				.build()
				.execute(context);
	}

	private List<SnomedRefSetType> getSympleTypeRefSets() {
		return ImmutableList.of(SnomedRefSetType.SIMPLE, SnomedRefSetType.DESCRIPTION_TYPE);
	}

}
