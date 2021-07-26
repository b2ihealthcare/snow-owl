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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.ValueSetMember;
import com.b2international.snowowl.core.domain.ValueSetMembers;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.ValueSetMemberSearchRequestEvaluator;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;

/**
 * @since 7.7
 */
public final class SnomedValueSetMemberSearchRequestEvaluator implements ValueSetMemberSearchRequestEvaluator {

	private static final List<SnomedRefSetType> SUPPORTED_REFSET_TYPES = List.of(SnomedRefSetType.SIMPLE, SnomedRefSetType.DESCRIPTION_TYPE); 
	
	@Override
	public Set<ResourceURI> evaluateSearchTargetResources(ServiceProvider context, Options search) {
		if (search.containsKey(OptionKey.URI)) {
			// TODO support proper refset SNOMED URIs as well
			Set<ResourceURI> targetResources = search.getCollection(OptionKey.URI, String.class).stream()
				.filter(ComponentURI::isValid)
				.map(ComponentURI::of)
				.map(ComponentURI::resourceUri)
				.collect(Collectors.toSet());
			
			if (!targetResources.isEmpty()) {
				return targetResources;
			}
		}
		
		// any SNOMED CT CodeSystem can be target resource, so search all by default
		return CodeSystemRequests.prepareSearchCodeSystem()
				.all()
				.setFields(ResourceDocument.Fields.RESOURCE_TYPE, ResourceDocument.Fields.ID)
				.buildAsync()
				.execute(context)
				.stream()
				.map(CodeSystem::getResourceURI)
				.collect(Collectors.toSet());
	}
	
	@Override
	public ValueSetMembers evaluate(ResourceURI uri, ServiceProvider context, Options search) {
		SnomedReferenceSetMembers referenceSetMembers = fetchRefsetMembers(uri, context, search);
		return toCollectionResource(referenceSetMembers, uri);
	}

	private ValueSetMembers toCollectionResource(SnomedReferenceSetMembers referenceSetMembers, ResourceURI uri) {
		return new ValueSetMembers(referenceSetMembers.stream().map(m -> toMember(m, uri)).collect(Collectors.toList()),
				referenceSetMembers.getSearchAfter(),
				referenceSetMembers.getLimit(),
				referenceSetMembers.getTotal());
	}

	private ValueSetMember toMember(SnomedReferenceSetMember member, ResourceURI codeSystemURI) {	 		
		final String term;		
		final String iconId = member.getReferencedComponent().getIconId();
		String terminologyComponentId = member.getReferencedComponent().getComponentType();
		switch (terminologyComponentId) {
		case SnomedConcept.TYPE: 
			SnomedConcept concept = (SnomedConcept) member.getReferencedComponent();
			term = concept.getFsn().getTerm();
			break;
		case SnomedDescription.TYPE:
			SnomedDescription description = (SnomedDescription) member.getReferencedComponent();
			term = description.getTerm();
			break;
		default: term = member.getReferencedComponentId();
		}

		return new ValueSetMember(
			ComponentURI.of(codeSystemURI, terminologyComponentId, member.getReferencedComponentId()),
			term, 
			iconId
		);
	}

	private SnomedReferenceSetMembers fetchRefsetMembers(ResourceURI uri, ServiceProvider context, Options search) {

		final Integer limit = search.get(OptionKey.LIMIT, Integer.class);
		final String searchAfter = search.get(OptionKey.AFTER, String.class);
		final List<ExtendedLocale> locales = search.getList(OptionKey.LOCALES, ExtendedLocale.class);

		SnomedRefSetMemberSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchMember();

		if (search.containsKey(OptionKey.URI)) {
			// extract refset IDs from pontential URI-like filter values
			final Set<String> refsetId = search.getCollection(OptionKey.URI, String.class).stream()
					.map(uriValue -> {
						// TODO support SNOMED URIs
						try {
							return ComponentURI.of(uriValue).identifier();
						} catch (Exception e) {
							return uriValue;
						}
					})
					.collect(Collectors.toSet());;
			
			
			requestBuilder.filterByRefSet(refsetId);
		}
		
		return requestBuilder
				.filterByActive(true)
				.filterByRefSetType(SUPPORTED_REFSET_TYPES)
				.setLocales(locales)
				.setExpand("referencedComponent(expand(fsn()))")
				.setLimit(limit)
				.setSearchAfter(searchAfter)
				.build(uri)
				.execute(context);
	}

}
