/*
 * Copyright 2020-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.b2international.snowowl.core.domain.ConceptMapMappings;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.ConceptMapMappingSearchRequestEvaluator;
import com.b2international.snowowl.core.request.MappingCorrelation;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.SnomedDisplayTermType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.google.common.base.Strings;

/**
 * @since 7.8
 */
public final class SnomedConceptMapSearchRequestEvaluator implements ConceptMapMappingSearchRequestEvaluator {

	@Override
	public Set<ResourceURI> evaluateSearchTargetResources(ServiceProvider context, Options search) {
		if (search.containsKey(OptionKey.URI)) {
			// TODO support proper refset SNOMED URIs as well
			final Set<ResourceURI> targetResources = search.getCollection(OptionKey.URI, String.class)
				.stream()
				.filter(ComponentURI::isValid)
				.map(uriValue -> ComponentURI.of(uriValue).resourceUri())
				.collect(Collectors.toSet());
			
			return targetResources;
		}
		
		/*
		 * XXX: We can no longer exit early at this point if SOURCE_TOOLING_ID is set
		 * and it does not contain "snomed", as "map to SNOMED CT" type reference sets may still
		 * produce results.
		 */
		return CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.setFields(ResourceDocument.Fields.RESOURCE_TYPE, ResourceDocument.Fields.ID)
			.filterByToolingId(SnomedTerminologyComponentConstants.TOOLING_ID)
			.buildAsync()
			.execute(context)
			.stream()
			.map(CodeSystem::getResourceURI)
			.collect(Collectors.toSet());
	}

	@Override
	public ConceptMapMappings evaluate(ResourceURI uri, ServiceProvider context, Options search) {
		final String preferredDisplay = search.get(OptionKey.DISPLAY, String.class);
		final SnomedDisplayTermType snomedDisplayTermType = SnomedDisplayTermType.getEnum(preferredDisplay);
		
		final SnomedReferenceSetMembers refSetMembers = fetchRefsetMembers(uri, context, search, snomedDisplayTermType);
		return toCollectionResource(refSetMembers, uri, context, search, snomedDisplayTermType);
	}

	private SnomedReferenceSetMembers fetchRefsetMembers(ResourceURI resourceUri, ServiceProvider context, Options search, SnomedDisplayTermType snomedDisplayTermType) {
		final Integer limit = search.get(OptionKey.LIMIT, Integer.class);
		final String searchAfter = search.get(OptionKey.AFTER, String.class);
		final List<ExtendedLocale> locales = search.getList(OptionKey.LOCALES, ExtendedLocale.class);
		final Collection<String> mapSourceIds = search.containsKey(OptionKey.MAP_SOURCE) ? search.getCollection(OptionKey.MAP_SOURCE, String.class) : null;
		final Collection<String> mapTargetIds = search.containsKey(OptionKey.MAP_TARGET) ? search.getCollection(OptionKey.MAP_TARGET, String.class) : null;
		final Collection<String> componentIds = search.containsKey(OptionKey.COMPONENT) ? search.getCollection(OptionKey.COMPONENT, String.class) : null;
	
		final SnomedRefSetMemberSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchMember();
	
		if (search.containsKey(OptionKey.URI)) {
			// Extract reference set IDs from URI-like filter values...
			final Set<String> refsetIds = search.getCollection(OptionKey.URI, String.class)
				.stream()
				.filter(uriValue -> ComponentURI.isValid(uriValue))
				.map(ComponentURI::of)
				// ... if they refer to the currently queried resource
				.filter(componentUri -> resourceUri.equals(componentUri.resourceUri()))
				.map(ComponentURI::identifier)
				.collect(Collectors.toSet());
			
			requestBuilder.filterByRefSet(refsetIds);
		}
		
		if (search.containsKey(OptionKey.ACTIVE)) {
			requestBuilder.filterByActive(search.getBoolean(OptionKey.ACTIVE));
		}
	
		return requestBuilder
			.filterByComponentIds(componentIds)
			.filterByReferencedComponentType(SnomedConcept.TYPE)
			.filterByMapSourceIds(mapSourceIds)
			.filterByMapTargetIds(mapTargetIds)
			.setLocales(locales)
			.setExpand(String.format("referencedComponent(%s)", !Strings.isNullOrEmpty(snomedDisplayTermType.getExpand()) ? "expand(" + snomedDisplayTermType.getExpand() + ")" : ""))
			.setLimit(limit)
			.setSearchAfter(searchAfter)
			.build(resourceUri)
			.execute(context);
	}

	private ConceptMapMappings toCollectionResource(
		SnomedReferenceSetMembers refSetMembers, 
		ResourceURI uri, 
		ServiceProvider context, 
		Options search, 
		SnomedDisplayTermType snomedDisplayTermType) {
		
		final Set<String> refSetIds = refSetMembers.stream()
			.map(SnomedReferenceSetMember::getRefsetId)
			.collect(Collectors.toSet());
		
		final Map<String, SnomedConcept> identifierConceptsById = SnomedRequests.prepareSearchConcept()
			.all()
			.filterByIds(refSetIds)
			.setLocales(search.getList(OptionKey.LOCALES, ExtendedLocale.class))
			.setExpand("pt(),referenceSet()")
			.build(uri)
			.execute(context.service(IEventBus.class))
			.getSync(1, TimeUnit.MINUTES)
			.stream()
			.collect(Collectors.toMap(
				concept -> concept.getId(), 
				concept -> concept));
		
		final Map<String, ComponentURI> componentURITemplates = getComponentURITemplates(context, identifierConceptsById);
		
		List<ConceptMapMapping> mappings = refSetMembers.stream()
			.map(m -> toMapping(m, uri, componentURITemplates.get(m.getRefsetId()), snomedDisplayTermType, identifierConceptsById.get(m.getRefsetId())))
			.collect(Collectors.toList());
		
		if (!mappings.isEmpty()) {
			final Set<ComponentURI> urisToLabel = mappings.stream()
				.map(m -> m.getSourceIconId() == null ? m.getSourceComponentURI() : m.getTargetComponentURI())
				.filter(u -> !u.isUnspecified())
				.collect(Collectors.toSet());
			
			final Set<String> ids = urisToLabel.stream()
				.map(c -> c.identifier())
				.collect(Collectors.toSet());
			
			final Set<ResourceURI> codeSystemUris = urisToLabel.stream()
				.map(c -> c.resourceUri())
				.collect(Collectors.toSet());
			
			final Map<ComponentURI, Concept> conceptsToLabel = CodeSystemRequests.prepareSearchConcepts()
				.all()
				.filterByCodeSystemUris(codeSystemUris)
				.filterByIds(ids)
				.buildAsync()
				.execute(context.service(IEventBus.class))
				.getSync(5, TimeUnit.MINUTES)
				.stream()
				.filter(c -> urisToLabel.contains(c.getCode()))
				.collect(Collectors.toMap(
					c -> c.getCode(), 
					c -> c));

			mappings = mappings.stream().map(mapping -> {
				if (mapping.getSourceIconId() == null && conceptsToLabel.containsKey(mapping.getSourceComponentURI())) {
					final Concept concept = conceptsToLabel.get(mapping.getSourceComponentURI());
					return mapping.toBuilder()
						.sourceTerm(concept.getTerm())
						.sourceIconId(concept.getIconId())
						.build();
				}
				
				if (mapping.getTargetIconId() == null && conceptsToLabel.containsKey(mapping.getTargetComponentURI())) {
					final Concept concept = conceptsToLabel.get(mapping.getTargetComponentURI());
					return mapping.toBuilder()
						.targetTerm(concept.getTerm())
						.targetIconId(concept.getIconId())
						.build();
				}

				return mapping;
			}).collect(Collectors.toList());
		}
		
		return new ConceptMapMappings(
			mappings,
			refSetMembers.getSearchAfter(),
			refSetMembers.getLimit(),
			refSetMembers.getTotal()
		);
	}

	// TODO figure out a better way to access codesystems from the perspective of a refset/mapset
	private Map<String, ComponentURI> getComponentURITemplates(ServiceProvider context, Map<String, SnomedConcept> refSetsById) {
		
		// Step 1: extract tooling IDs from map target / map source component types
		final Set<String> toolingIds = refSetsById.values()
			.stream()
			.map(SnomedConcept::getReferenceSet)
			.flatMap(rs -> List.of(rs.getMapTargetComponentType(), rs.getMapSourceComponentType()).stream())
			.map(type -> type.split("\\.")[0])
			.collect(Collectors.toSet());

		/*
		 * Step 2: convert tooling IDs to resource IDs -- where a 1:1 translation
		 * between them exists (eg. ICD10 tooling and ICD-10, the code system).
		 * 
		 * Where multiple code systems are available for a tooling, like in the case of LCS
		 * or SNOMEDCT, use the first code system returned for the tooling.
		 */
		final Map<String, CodeSystem> codeSystemByToolingId = CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.filterByToolingIds(toolingIds)
			.buildAsync()
			.execute(context)
			.stream()
			.collect(Collectors.toMap(CodeSystem::getToolingId, c -> c, (c1, c2) -> c1)); 
		
		/*
		 * Step 3: build a "template" component URI for each reference set, using the
		 * reference set ID as the placeholder for the source/target component ID.
		 */
		return refSetsById.entrySet()
			.stream()
			.collect(Collectors.toMap(
				entry -> entry.getKey(), 
				entry -> {
					final SnomedConcept identifierConcept = entry.getValue();
					final SnomedReferenceSet refSet = identifierConcept.getReferenceSet();
					
					final String mapComponentType;
					if (SnomedRefSetType.SIMPLE_MAP_TO.equals(refSet.getType())) {
						mapComponentType = refSet.getMapSourceComponentType();
					} else {
						mapComponentType = refSet.getMapTargetComponentType();
					}
					
					final String[] typeParts = mapComponentType.split("\\.");
					if (Strings.isNullOrEmpty(mapComponentType) || !codeSystemByToolingId.containsKey(typeParts[0])) {
						return ComponentURI.UNSPECIFIED;
					}
					
					final CodeSystem codeSystem = codeSystemByToolingId.get(typeParts[0]);
					return ComponentURI.of(codeSystem.getResourceURI(), typeParts[1], identifierConcept.getId());
				}));
	}

	private ConceptMapMapping toMapping(
		final SnomedReferenceSetMember member, 
		final ResourceURI codeSystemURI, 
		final ComponentURI componentURI, 
		final SnomedDisplayTermType snomedDisplayTermType, 
		final SnomedConcept identifierConcept) {

		final String conceptMapTerm = snomedDisplayTermType.getLabel(identifierConcept);
		final String conceptMapIconId = identifierConcept.getIconId();
		
		final ConceptMapMapping.Builder mappingBuilder = ConceptMapMapping.builder()
				.uri(ComponentURI.of(codeSystemURI, SnomedReferenceSetMember.TYPE, member.getId()).toString())
				.conceptMapUri(ComponentURI.of(codeSystemURI, SnomedConcept.REFSET_TYPE, member.getRefsetId()).toString())
				.conceptMapTerm(conceptMapTerm)
				.conceptMapIconId(conceptMapIconId)
				.active(member.isActive())
				.mappingCorrelation(getEquivalence(member));

		final Map<String, Object> properties = member.getProperties();

		if (properties.containsKey(SnomedRf2Headers.FIELD_MAP_PRIORITY)) {
			int mapPriority = (int) properties.get(SnomedRf2Headers.FIELD_MAP_PRIORITY);
			mappingBuilder.mapPriority(mapPriority);
		}

		if (properties.containsKey(SnomedRf2Headers.FIELD_MAP_GROUP)) {
			int mapGroup = (int) properties.get(SnomedRf2Headers.FIELD_MAP_GROUP);
			mappingBuilder.mapGroup(mapGroup);
		}

		mappingBuilder.mapAdvice((String) properties.get(SnomedRf2Headers.FIELD_MAP_ADVICE));
		mappingBuilder.mapRule((String) properties.get(SnomedRf2Headers.FIELD_MAP_RULE));

		final SnomedConcept referencedConcept = (SnomedConcept) member.getReferencedComponent();
		final String iconId = referencedConcept.getIconId();
		final String componentType = referencedConcept.getComponentType(); // "concept"
		final String term = snomedDisplayTermType.getLabel(referencedConcept);

		final SnomedReferenceSet referenceSet = identifierConcept.getReferenceSet();
		
		if (SnomedRefSetType.SIMPLE_MAP_TO.equals(referenceSet.getType())) {
			// Referenced concept is the _target_
			mappingBuilder
				.targetComponentURI(ComponentURI.of(codeSystemURI, componentType, referencedConcept.getId()))
				.targetTerm(term)
				.targetIconId(iconId);
				
			final String mapSource = (String) properties.get(SnomedRf2Headers.FIELD_MAP_SOURCE);
			if (ComponentURI.isValid(mapSource)) {
				mappingBuilder.sourceComponentURI(ComponentURI.of(mapSource));
			} else {
				// Build one using the template received
				mappingBuilder.sourceComponentURI(ComponentURI.of(componentURI.resourceUri(), componentURI.componentType(), mapSource));
			}
			
			mappingBuilder.sourceTerm(mapSource);
		} else {
			// Referenced concept is the _source_
			mappingBuilder
				.sourceComponentURI(ComponentURI.of(codeSystemURI, componentType, referencedConcept.getId()))
				.sourceTerm(term)
				.sourceIconId(iconId);
			
			final String mapTarget = (String) properties.get(SnomedRf2Headers.FIELD_MAP_TARGET);
			if (ComponentURI.isValid(mapTarget)) {
				mappingBuilder.targetComponentURI(ComponentURI.of(mapTarget));
			} else {
				// Build one using the template received
				mappingBuilder.targetComponentURI(ComponentURI.of(componentURI.resourceUri(), componentURI.componentType(), mapTarget));
			}
			
			mappingBuilder.targetTerm(mapTarget);
		}
		
		return mappingBuilder.build();
	}

	private MappingCorrelation getEquivalence(SnomedReferenceSetMember conceptMapMember) {
		final SnomedRefSetType refSetType = conceptMapMember.type();

		if (SnomedRefSetType.SIMPLE_MAP.equals(refSetType) 
			|| SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION.equals(refSetType)
			|| SnomedRefSetType.SIMPLE_MAP_TO.equals(refSetType)) {
			/*
			 * There is no information on the "quality" of a simple map member, but it is
			 * recommended for use only if a close to 1:1 correlation exists.
			 */
			return MappingCorrelation.EXACT_MATCH;
		}

		final Map<String, Object> properties = conceptMapMember.getProperties();
		if (properties == null || properties.isEmpty() || !properties.containsKey(SnomedRf2Headers.FIELD_CORRELATION_ID)) {
			return MappingCorrelation.NOT_SPECIFIED; 
		}

		final String correlationId = (String) properties.get(SnomedRf2Headers.FIELD_CORRELATION_ID);

		switch (correlationId) {
			case Concepts.MAP_CORRELATION_EXACT_MATCH:     return MappingCorrelation.EXACT_MATCH;
			case Concepts.MAP_CORRELATION_BROAD_TO_NARROW: return MappingCorrelation.BROAD_TO_NARROW;
			case Concepts.MAP_CORRELATION_NARROW_TO_BROAD: return MappingCorrelation.NARROW_TO_BROAD;
			case Concepts.MAP_CORRELATION_PARTIAL_OVERLAP: return MappingCorrelation.PARTIAL_OVERLAP;
			case Concepts.MAP_CORRELATION_NOT_MAPPABLE:    return MappingCorrelation.NOT_MAPPABLE;
			case Concepts.MAP_CORRELATION_NOT_SPECIFIED:   return MappingCorrelation.NOT_SPECIFIED;
			default:                                       return MappingCorrelation.NOT_SPECIFIED;
		}
	}
}
