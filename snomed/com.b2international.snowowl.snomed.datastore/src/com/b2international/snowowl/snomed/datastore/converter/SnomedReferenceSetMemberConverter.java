/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.converter;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.request.BaseRevisionResourceConverter;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 4.5
 */
final class SnomedReferenceSetMemberConverter extends BaseRevisionResourceConverter<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember, SnomedReferenceSetMembers> {

	SnomedReferenceSetMemberConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedReferenceSetMembers createCollectionResource(List<SnomedReferenceSetMember> results, String scrollId, String searchAfter, int limit, int total) {
		return new SnomedReferenceSetMembers(results, scrollId, searchAfter, limit, total);
	}
	
	@Override
	protected void expand(List<SnomedReferenceSetMember> results) {
		expandReferencedComponent(results);
		expandTargetComponent(results);
	}

	private void expandTargetComponent(List<SnomedReferenceSetMember> results) {
		if (expand().containsKey(SnomedRf2Headers.FIELD_TARGET_COMPONENT)) {
			final Options expandOptions = expand().get(SnomedRf2Headers.FIELD_TARGET_COMPONENT, Options.class);
			
			final Multimap<String, SnomedReferenceSetMember> membersByTargetComponent = HashMultimap.create();
			for (SnomedReferenceSetMember member : results) {
				final Map<String, Object> props = member.getProperties();
				if (props.containsKey(SnomedRf2Headers.FIELD_TARGET_COMPONENT)) {
					membersByTargetComponent.put(((SnomedCoreComponent) props.get(SnomedRf2Headers.FIELD_TARGET_COMPONENT)).getId(), member);
				}
			}
			
			final Multimap<ComponentCategory,String> targetComponentIdsByCategory = Multimaps.index(membersByTargetComponent.keySet(), new Function<String, ComponentCategory>() {
				@Override
				public ComponentCategory apply(String id) {
					return SnomedIdentifiers.getComponentCategory(id);
				}
			});
			
			for (ComponentCategory category : targetComponentIdsByCategory.keySet()) {
				final Collection<String> targetComponentIds = targetComponentIdsByCategory.get(category);
				final Map<String,? extends SnomedCoreComponent> componentsById = Maps.uniqueIndex(getComponents(category, targetComponentIds, expandOptions.get("expand", Options.class)), IComponent.ID_FUNCTION);
				for (String targetComponentId : targetComponentIds) {
					final SnomedCoreComponent targetComponent = componentsById.get(targetComponentId);
					if (targetComponent != null) {
						for (SnomedReferenceSetMember member : membersByTargetComponent.get(targetComponentId)) {
							final Map<String, Object> newProps = newHashMap(member.getProperties());
							newProps.put(SnomedRf2Headers.FIELD_TARGET_COMPONENT, targetComponent);
							((SnomedReferenceSetMember) member).setProperties(newProps);
						}
					}
				}
			}
		}
	}

	private CollectionResource<? extends SnomedCoreComponent> getComponents(ComponentCategory category, Collection<String> componentIds, Options expand) {
		switch (category) {
		case CONCEPT: return SnomedRequests.prepareSearchConcept().setLimit(componentIds.size()).filterByIds(componentIds).setExpand(expand).setLocales(locales()).build().execute(context());
		case DESCRIPTION: return SnomedRequests.prepareSearchDescription().setLimit(componentIds.size()).filterByIds(componentIds).setExpand(expand).setLocales(locales()).build().execute(context());
		case RELATIONSHIP: return SnomedRequests.prepareSearchRelationship().setLimit(componentIds.size()).filterByIds(componentIds).setExpand(expand).setLocales(locales()).build().execute(context());
		default: throw new NotImplementedException("Not implemented non core target component expansion", category);
		}
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
		final SearchResourceRequestBuilder<?, BranchContext, ? extends CollectionResource<? extends SnomedCoreComponent>> search;
		
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
			.filterByIds(componentIds)
			.setLimit(componentIds.size())
			.setLocales(locales())
			.setExpand(expandOptions.get("expand", Options.class));
		
		CollectionResource<? extends SnomedCoreComponent> components = search.build().execute(context());
		
		for (SnomedCoreComponent component : components) {
			for (SnomedReferenceSetMember member : referencedComponentIdToMemberMap.get(component.getId())) {
				((SnomedReferenceSetMember) member).setReferencedComponent(component);
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
		final SnomedReferenceSetMember member = new SnomedReferenceSetMember();
		member.setStorageKey(entry.getStorageKey());
		member.setId(entry.getId());
		member.setEffectiveTime(toEffectiveTime(entry.getEffectiveTime()));
		member.setReleased(entry.isReleased());
		member.setActive(entry.isActive());
		member.setModuleId(entry.getModuleId());
		member.setIconId(entry.getIconId());
		member.setReferenceSetId(entry.getReferenceSetId());
		member.setType(entry.getReferenceSetType());
		member.setScore(entry.getScore());

		final Map<String, Object> props = newHashMap(entry.getAdditionalFields());
		// XXX refset type can be null if the document was loaded partially
		if (entry.getReferenceSetType() != null) {
			switch (entry.getReferenceSetType()) {
			case ASSOCIATION:
				// convert ID to resources where possible to override value with nested object in JSON
				props.put(SnomedRf2Headers.FIELD_TARGET_COMPONENT, convertToResource(entry.getTargetComponent()));
				break;
			case MODULE_DEPENDENCY:
				// convert stored long values to short date format
				props.put(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, entry.getSourceEffectiveTime() == null ? null : toEffectiveTime(entry.getSourceEffectiveTime()));
				props.put(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, entry.getTargetEffectiveTime() == null ? null : toEffectiveTime(entry.getTargetEffectiveTime()));
				break;
			case CONCRETE_DATA_TYPE:
				// convert concrete domain value to serialized String format
				props.put(SnomedRf2Headers.FIELD_VALUE, SnomedRefSetUtil.serializeValue(entry.getDataType(), entry.getValue()));
				break;
			default:
				break;
			}
			
			member.setProperties(props);
		}
		
		setReferencedComponent(member, entry.getReferencedComponentId(), entry.getReferencedComponentType());
		return member;
	}
	
	private SnomedCoreComponent convertToResource(String targetComponentId) {
		switch (SnomedIdentifiers.getComponentCategory(targetComponentId)) {
		case CONCEPT: return new SnomedConcept(targetComponentId);
		case DESCRIPTION: return new SnomedDescription(targetComponentId);
		case RELATIONSHIP: return new SnomedRelationship(targetComponentId);
		default: throw new NotImplementedException("Cannot convert '%s' to component, unknown type.", targetComponentId);
		}
	}

	private void setReferencedComponent(SnomedReferenceSetMember member, String referencedComponentId, short referencedComponentType) {
		final SnomedCoreComponent component;
		switch (referencedComponentType) {
		// TODO support query type refset refcomp expansion, currently it's a concept
		case SnomedTerminologyComponentConstants.REFSET_NUMBER:
		case SnomedTerminologyComponentConstants.CONCEPT_NUMBER:
			component = new SnomedConcept();
			((SnomedConcept) component).setId(referencedComponentId);
			break;
		case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
			component = new SnomedDescription();
			((SnomedDescription) component).setId(referencedComponentId);
			break;
		case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
			component = new SnomedRelationship();
			((SnomedRelationship) component).setId(referencedComponentId);
			break;
		// XXX partial field loading support
		case 0:
			component = null;
			break;
		default: throw new UnsupportedOperationException("UnsupportedReferencedComponentType: " + referencedComponentType);
		}
		member.setReferencedComponent(component);
	}
}
