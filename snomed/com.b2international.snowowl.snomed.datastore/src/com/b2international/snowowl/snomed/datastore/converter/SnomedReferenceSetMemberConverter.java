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
package com.b2international.snowowl.snomed.datastore.converter;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
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
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 4.5
 */
final class SnomedReferenceSetMemberConverter extends BaseSnomedComponentConverter<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember, SnomedReferenceSetMembers> {

	SnomedReferenceSetMemberConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedReferenceSetMembers createCollectionResource(List<SnomedReferenceSetMember> results, int offset, int limit, int total) {
		return new SnomedReferenceSetMembers(results, offset, limit, total);
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
							((SnomedReferenceSetMemberImpl) member).setProperties(newProps);
						}
					}
				}
			}
		}
	}

	private CollectionResource<? extends SnomedCoreComponent> getComponents(ComponentCategory category, Collection<String> componentIds, Options expand) {
		switch (category) {
		case CONCEPT: return SnomedRequests.prepareSearchConcept().setLimit(componentIds.size()).setComponentIds(componentIds).setExpand(expand).setLocales(locales()).build().execute(context());
		case DESCRIPTION: return SnomedRequests.prepareSearchDescription().setLimit(componentIds.size()).setComponentIds(componentIds).setExpand(expand).setLocales(locales()).build().execute(context());
		case RELATIONSHIP: return SnomedRequests.prepareSearchRelationship().setLimit(componentIds.size()).setComponentIds(componentIds).setExpand(expand).setLocales(locales()).build().execute(context());
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
		member.setIconId(entry.getIconId());
		member.setReferenceSetId(entry.getRefSetIdentifierId());
		member.setType(entry.getRefSetType());
		
		// XXX would be nice to refactor this switch
		final Builder<String, Object> props = ImmutableMap.builder();
		switch (entry.getRefSetType()) {
			case QUERY:
				props.put(SnomedRf2Headers.FIELD_QUERY, entry.getQuery());
				break;
			case ATTRIBUTE_VALUE:
				props.put(SnomedRf2Headers.FIELD_VALUE_ID, entry.getValueId());
				break;
			case ASSOCIATION:
				props.put(SnomedRf2Headers.FIELD_TARGET_COMPONENT, convertToResource(entry.getTargetComponentId()));
				break;
			case EXTENDED_MAP:
				props.put(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, entry.getMapCategoryId());
			case COMPLEX_MAP:
				props.put(SnomedRf2Headers.FIELD_MAP_GROUP, entry.getMapGroup());
				props.put(SnomedRf2Headers.FIELD_MAP_PRIORITY, entry.getMapPriority());
				props.put(SnomedRf2Headers.FIELD_MAP_RULE, entry.getMapRule() != null ? entry.getMapRule() : "");
				props.put(SnomedRf2Headers.FIELD_MAP_ADVICE, entry.getMapAdvice() != null ? entry.getMapAdvice() : "");
				props.put(SnomedRf2Headers.FIELD_CORRELATION_ID, entry.getCorrelationId());
			case SIMPLE_MAP:
				props.put(SnomedRf2Headers.FIELD_MAP_TARGET, entry.getMapTargetComponentId());
				if (!Strings.isNullOrEmpty(entry.getMapTargetDescription())) {
					props.put(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION, entry.getMapTargetDescription());
				}
				props.put(SnomedMappings.memberMapTargetComponentType().fieldName(), entry.getMapTargetComponentTypeAsShort());
				break;
			case CONCRETE_DATA_TYPE:
				props.put(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, entry.getAttributeLabel());
				props.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, entry.getCharacteristicTypeId());
				props.put(SnomedRf2Headers.FIELD_VALUE, SnomedRefSetUtil.serializeValue(entry.getRefSetPackageDataType(), entry.getValue()));
				props.put(SnomedRf2Headers.FIELD_UNIT_ID, entry.getUomComponentId());
				props.put(SnomedRf2Headers.FIELD_OPERATOR_ID, entry.getOperatorComponentId());
				props.put(SnomedMappings.memberDataTypeOrdinal().fieldName(), entry.getRefSetPackageDataType().ordinal());
				break;
			case LANGUAGE:
				props.put(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, entry.getAcceptabilityId());
				break;
			case DESCRIPTION_TYPE:
				props.put(SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT, entry.getDescriptionFormat());
				props.put(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, entry.getDescriptionLength());
				break;
			// TODO module dependency refset
			default:
				break;
		}
		member.setProperties(props.build());
		
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
