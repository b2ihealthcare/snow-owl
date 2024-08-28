/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.request.BaseRevisionResourceConverter;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedOWLRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptRequestCache;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Strings;
import com.google.common.collect.*;

/**
 * @since 4.5
 */
public final class SnomedReferenceSetMemberConverter extends BaseRevisionResourceConverter<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember, SnomedReferenceSetMembers> {

	final SnomedOWLExpressionConverter owlExpressionConverter;
	
	public SnomedReferenceSetMemberConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
		owlExpressionConverter = new SnomedOWLExpressionConverter(context);
	}

	@Override
	protected SnomedReferenceSetMembers createCollectionResource(List<SnomedReferenceSetMember> results, String searchAfter, int limit, int total) {
		return new SnomedReferenceSetMembers(results, searchAfter, limit, total);
	}
	
	@Override
	public void expand(List<SnomedReferenceSetMember> results) {
		expandReferencedComponent(results);
		expandTargetComponent(results);
		new ModuleExpander(context(), expand(), locales()).expand(results);
	}

	private void expandTargetComponent(List<SnomedReferenceSetMember> results) {
		if (expand().containsKey(SnomedReferenceSetMember.Expand.TARGET_COMPONENT)) {
			final Options expandOptions = expand().get(SnomedReferenceSetMember.Expand.TARGET_COMPONENT, Options.class);
			
			final Multimap<String, SnomedReferenceSetMember> membersByTargetComponent = HashMultimap.create();
			for (SnomedReferenceSetMember member : results) {
				final Map<String, Object> props = member.getProperties();
				if (props.containsKey(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID)) {
					membersByTargetComponent.put(((String) props.get(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID)), member);
				}
			}
			
			final Multimap<ComponentCategory,String> targetComponentIdsByCategory = Multimaps.index(membersByTargetComponent.keySet(), SnomedIdentifiers::getComponentCategory);
			
			for (ComponentCategory category : targetComponentIdsByCategory.keySet()) {
				final Collection<String> targetComponentIds = targetComponentIdsByCategory.get(category);
				final Map<String, ? extends SnomedCoreComponent> componentsById = Maps.uniqueIndex(getComponents(category, targetComponentIds, expandOptions.get("expand", Options.class)), IComponent::getId);
				for (String targetComponentId : targetComponentIds) {
					final SnomedCoreComponent targetComponent = componentsById.get(targetComponentId);
					if (targetComponent != null) {
						for (SnomedReferenceSetMember member : membersByTargetComponent.get(targetComponentId)) {
							final Map<String, Object> newProps = Maps.newHashMap(member.getProperties());
							newProps.put(SnomedReferenceSetMember.Expand.TARGET_COMPONENT, targetComponent);
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
		default: throw new NotImplementedException("Not implemented non core target component expansion: '%s'", category);
		}
	}

	private void expandReferencedComponent(List<SnomedReferenceSetMember> results) {
		if (expand().containsKey(SnomedReferenceSetMember.Expand.REFERENCED_COMPONENT)) {
			Options expandOptions = expand().get(SnomedReferenceSetMember.Expand.REFERENCED_COMPONENT, Options.class);
			
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

	private ListMultimap<ComponentCategory, String> collectReferencedComponentCategories(final Multimap<String, SnomedReferenceSetMember> refCompToMembers) {
		return FluentIterable.from(refCompToMembers.keySet()).index(SnomedIdentifiers::getComponentCategory);
	}

	private ListMultimap<String, SnomedReferenceSetMember> collectReferencedComponentIds(List<SnomedReferenceSetMember> results) {
		return FluentIterable.from(results).index(input -> input.getReferencedComponent().getId());
	}

	@Override
	protected SnomedReferenceSetMember toResource(SnomedRefSetMemberIndexEntry entry) {
		final SnomedReferenceSetMember member = new SnomedReferenceSetMember();
		member.setId(entry.getId());
		member.setEffectiveTime(toEffectiveTime(entry.getEffectiveTime()));
		member.setReleased(entry.isReleased());
		member.setActive(entry.isActive());
		member.setModuleId(entry.getModuleId());
		member.setIconId(entry.getIconId());
		member.setRefsetId(entry.getRefsetId());
		member.setType(entry.getReferenceSetType());
		member.setScore(entry.getScore());

		final Map<String, Object> props = Maps.newHashMap(entry.getAdditionalFields());

		// convert stored long values to short date format
		props.computeIfPresent(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, (key, currentValue) -> toEffectiveTime((long) currentValue));
		props.computeIfPresent(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, (key, currentValue) -> toEffectiveTime((long) currentValue));

		// convert concrete domain value to serialized String format
		if (entry.getValue() != null) {
			props.put(SnomedRf2Headers.FIELD_VALUE, SnomedRefSetUtil.serializeValue(entry.getDataType(), entry.getValue()));
		}
		
		member.setProperties(props);
		
		String owlExpression = entry.getOwlExpression();
		if (Concepts.REFSET_OWL_AXIOM.equals(entry.getRefsetId()) &&
				expand().containsKey(SnomedReferenceSetMember.Expand.OWL_RELATIONSHIPS) && 
				!Strings.isNullOrEmpty(owlExpression)) {
			
			Options expandOptions = expand().getOptions(SnomedReferenceSetMember.Expand.OWL_RELATIONSHIPS).getOptions("expand");
			
			if (!CompareUtils.isEmpty(entry.getClassAxiomRelationships())) {
				if (owlExpression.startsWith("EquivalentClasses")) {
					member.setEquivalentOWLRelationships(toOwlRelationships(entry.getClassAxiomRelationships(), expandOptions));
				} else {
					member.setClassOWLRelationships(toOwlRelationships(entry.getClassAxiomRelationships(), expandOptions));
				}
			} else if (!CompareUtils.isEmpty(entry.getGciAxiomRelationships())) {
				member.setGciOWLRelationships(toOwlRelationships(entry.getGciAxiomRelationships(), expandOptions));
			}
		}
		
		setReferencedComponent(member, entry.getReferencedComponentId(), entry.getReferencedComponentType());
		return member;
	}
	
	private List<SnomedOWLRelationship> toOwlRelationships(List<SnomedOWLRelationshipDocument> owlRelationshipDocs, Options expandOptions) {
		if (owlRelationshipDocs == null) {
			return null;
		}
		
		List<SnomedOWLRelationship> owlRelationships = owlRelationshipDocs.stream().map(this::toOwlRelationship).toList();
		
		expandType(owlRelationships, expandOptions);
		expandDestination(owlRelationships, expandOptions);
		
		return owlRelationships;
	}
	
	private SnomedOWLRelationship toOwlRelationship(SnomedOWLRelationshipDocument owlRelationshipDoc) {
		SnomedOWLRelationship owlRelationship = new SnomedOWLRelationship();
		
		owlRelationship.setDestinationId(owlRelationshipDoc.getDestinationId());
		owlRelationship.setTypeId(owlRelationshipDoc.getTypeId());
		owlRelationship.setRelationshipGroup(owlRelationshipDoc.getRelationshipGroup());
		owlRelationship.setValueAsObject(owlRelationshipDoc.getValueAsObject());
		
		return owlRelationship;
	}
	
	private void expandType(List<SnomedOWLRelationship> results, Options expandOptions) {
		if (expandOptions.containsKey(SnomedOWLRelationship.Expand.TYPE)) {
			final Options typeOptions = expandOptions.get(SnomedOWLRelationship.Expand.TYPE, Options.class);
			final Iterable<String> typeConceptIds = FluentIterable.from(results).transform(SnomedOWLRelationship::getTypeId);
			
			context().service(SnomedConceptRequestCache.class)
				.request(context(), typeConceptIds, typeOptions.getOptions("expand"), locales(), typeConceptsById -> {
					for (SnomedOWLRelationship relationship : results) {
						final String typeId = relationship.getTypeId();
						if (typeConceptsById.containsKey(typeId)) {
							final SnomedConcept typeConcept = typeConceptsById.get(typeId);
							((SnomedOWLRelationship) relationship).setType(typeConcept);
						}
					}
				});

		}
	}

	private void expandDestination(List<SnomedOWLRelationship> results, Options expandOptions) {
		if (expandOptions.containsKey(SnomedOWLRelationship.Expand.DESTINATION)) {
			final Options destinationOptions = expandOptions.get(SnomedOWLRelationship.Expand.DESTINATION, Options.class);
			final Iterable<String> destinationConceptIds = FluentIterable.from(results)
				.filter(r -> !r.hasValue()) // skip expand on relationships with value
				.transform(SnomedOWLRelationship::getDestinationId);
			
			context().service(SnomedConceptRequestCache.class)
				.request(context(), destinationConceptIds, destinationOptions.getOptions("expand"), locales(), destinationConceptsById -> {
					for (SnomedOWLRelationship relationship : results) {
						final String destinationId = relationship.getDestinationId();
						// containsKey handles any null values here
						if (destinationConceptsById.containsKey(destinationId)) {
							final SnomedConcept destinationConcept = destinationConceptsById.get(destinationId);
							((SnomedOWLRelationship) relationship).setDestination(destinationConcept);
						}
					}
				});
			
		}
	}

	private void setReferencedComponent(SnomedReferenceSetMember member, String referencedComponentId, String referencedComponentType) {
		// XXX: partial field loading support
		if (referencedComponentType == null || referencedComponentId == null) return;
		final SnomedCoreComponent component;
		switch (referencedComponentType) {
			// TODO support query type refset refcomp expansion, currently it's a concept
			case SnomedConcept.REFSET_TYPE:
			case SnomedConcept.TYPE:
				component = new SnomedConcept();
				((SnomedConcept) component).setId(referencedComponentId);
				break;
			case SnomedDescription.TYPE:
				component = new SnomedDescription();
				((SnomedDescription) component).setId(referencedComponentId);
				break;
			case SnomedRelationship.TYPE:
				component = new SnomedRelationship();
				((SnomedRelationship) component).setId(referencedComponentId);
				break;
			default: 
				throw new UnsupportedOperationException("UnsupportedReferencedComponentType: " + referencedComponentType);
		}
		member.setReferencedComponent(component);
	}
}
