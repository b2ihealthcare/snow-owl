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
package com.b2international.snowowl.snomed.datastore.server.converter;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.request.SearchRequestBuilder;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMemberImpl;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
final class SnomedReferenceSetMemberConverter extends BaseSnomedComponentConverter<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember, SnomedReferenceSetMembers> {

	SnomedReferenceSetMemberConverter(BranchContext context, Options expand, List<ExtendedLocale> locales, AbstractSnomedRefSetMembershipLookupService membershipLookupService) {
		super(context, expand, locales, membershipLookupService);
	}

	@Override
	protected SnomedReferenceSetMembers createCollectionResource(List<SnomedReferenceSetMember> results, int offset, int limit, int total) {
		return new SnomedReferenceSetMembers(results, offset, limit, total);
	}
	
	@Override
	protected void expand(List<SnomedReferenceSetMember> results) {
		expandReferencedComponent(results);
	}

	private void expandReferencedComponent(List<SnomedReferenceSetMember> results) {
		if (expand().containsKey("referencedComponent")) {
			Options expandOptions = expand().get("referencedComponent", Options.class);
			
			final Multimap<String, SnomedReferenceSetMember> referencedComponentIdToMemberMap = collectReferencedComponentIds(results);
			final Multimap<ComponentCategory, String> componentCategoryToIdMap = collectReferencedComponentCategories(referencedComponentIdToMemberMap);
			
			for (ComponentCategory category : componentCategoryToIdMap.keySet()) {
				expandComponentCategory(expandOptions, referencedComponentIdToMemberMap, componentCategoryToIdMap, category);
			}
		}
	}

	private void expandComponentCategory(Options expandOptions,
			Multimap<String, SnomedReferenceSetMember> referencedComponentIdToMemberMap,
			Multimap<ComponentCategory, String> componentCategoryToIdMap, 
			ComponentCategory category) {
		
		final Collection<String> componentIds = componentCategoryToIdMap.get(category);
		final SearchRequestBuilder<?, ? extends CollectionResource<? extends SnomedCoreComponent>> search;
		
		switch (category) {
			case CONCEPT:
				search = SnomedRequests.prepareSearchConcept();
				break;
			case DESCRIPTION:
				search = SnomedRequests.prepareSearchDescription();
				break;
			case RELATIONSHIP:
				search = SnomedRequests.prepareSearchRelationship();
				break;
			default: 
				throw new UnsupportedOperationException("Category is not supported in referenced component expansion");
		}

		search
			.setComponentIds(componentIds)
			.setLimit(componentIds.size())
			.setLocales(locales())
			.setExpand(expandOptions.get("expand", Options.class));
		
		CollectionResource<? extends SnomedCoreComponent> components = search.build().execute(context());
		
		for (SnomedCoreComponent component : components) {
			for (SnomedReferenceSetMember member : referencedComponentIdToMemberMap.get(component.getId())) {
				((SnomedReferenceSetMemberImpl) member).setReferencedComponent(component);
			}
		}
	}

	private ImmutableListMultimap<ComponentCategory, String> collectReferencedComponentCategories(
			final Multimap<String, SnomedReferenceSetMember> refCompToMembers) {
		
		return FluentIterable.from(refCompToMembers.keySet()).index(new Function<String, ComponentCategory>() {
			@Override
			public ComponentCategory apply(String input) {
				return SnomedIdentifiers.getComponentCategory(input);
			}
		});
	}

	private ImmutableListMultimap<String, SnomedReferenceSetMember> collectReferencedComponentIds(
			List<SnomedReferenceSetMember> results) {
		
		return FluentIterable.from(results).index(new Function<SnomedReferenceSetMember, String>() {
			@Override
			public String apply(SnomedReferenceSetMember input) {
				return input.getReferencedComponent().getId();
			}
		});
	}

	@Override
	protected SnomedReferenceSetMember toResource(SnomedRefSetMemberIndexEntry entry) {
		final SnomedReferenceSetMemberImpl member = new SnomedReferenceSetMemberImpl();
		member.setId(entry.getId());
		member.setEffectiveTime(EffectiveTimes.toDate(entry.getEffectiveTimeAsLong()));
		member.setReleased(entry.isReleased());
		member.setActive(entry.isActive());
		member.setModuleId(entry.getModuleId());
		member.setReferenceSetId(entry.getRefSetIdentifierId());
		member.setType(entry.getRefSetType());
		
		final Builder<String, Object> props = ImmutableMap.builder();
		switch (entry.getRefSetType()) {
			case QUERY:
				props.put(SnomedRf2Headers.FIELD_QUERY, entry.getQuery());
				break;
			case ATTRIBUTE_VALUE:
				props.put(SnomedRf2Headers.FIELD_VALUE_ID, entry.getValueId());
				break;
			case ASSOCIATION:
				props.put(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID, entry.getTargetComponentId());
				break;
			default:
				break;
		}
		member.setProperties(props.build());
		
		setReferencedComponent(member, entry.getReferencedComponentId(), entry.getReferencedComponentType());
		return member;
	}
	
	private void setReferencedComponent(SnomedReferenceSetMemberImpl member, String referencedComponentId, String referencedComponentType) {
		final SnomedCoreComponent component;
		switch (referencedComponentType) {
		// TODO support query type refset refcomp expansion, currently it's a concept
		case SnomedTerminologyComponentConstants.REFSET:
		case SnomedTerminologyComponentConstants.CONCEPT:
			component = new SnomedConcept();
			((SnomedConcept) component).setId(referencedComponentId);
			break;
		case SnomedTerminologyComponentConstants.DESCRIPTION:
			component = new SnomedDescription();
			((SnomedDescription) component).setId(referencedComponentId);
			break;
		case SnomedTerminologyComponentConstants.RELATIONSHIP:
			component = new SnomedRelationship();
			((SnomedRelationship) component).setId(referencedComponentId);
			break;
		default: throw new UnsupportedOperationException("UnsupportedReferencedComponentType");
		}
		member.setReferencedComponent(component);
	}
}
