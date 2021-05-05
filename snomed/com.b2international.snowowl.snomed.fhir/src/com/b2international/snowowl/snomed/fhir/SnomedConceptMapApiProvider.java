/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.fhir;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersion;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement;
import com.b2international.snowowl.fhir.core.model.conceptmap.Group;
import com.b2international.snowowl.fhir.core.model.conceptmap.Group.Builder;
import com.b2international.snowowl.fhir.core.model.conceptmap.Match;
import com.b2international.snowowl.fhir.core.model.conceptmap.Target;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.provider.FhirApiProvider;
import com.b2international.snowowl.fhir.core.provider.IConceptMapApiProvider;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * Reference Set provider for Concept Map FHIR support
 * 
 * @since 7.1
 * 
 * @see IConceptMapApiProvider
 * @see FhirApiProvider
 */
public final class SnomedConceptMapApiProvider extends SnomedFhirApiProvider implements IConceptMapApiProvider {

	private static final List<SnomedRefSetType> CONCEPT_MAP_TYPES = ImmutableList.of(SnomedRefSetType.SIMPLE_MAP, 
			SnomedRefSetType.COMPLEX_MAP, 
			SnomedRefSetType.COMPLEX_BLOCK_MAP,
			SnomedRefSetType.EXTENDED_MAP);

	@Component
	public static final class Factory implements IConceptMapApiProvider.Factory {
		@Override
		public IConceptMapApiProvider create(IEventBus bus, List<ExtendedLocale> locales) {
			return new SnomedConceptMapApiProvider(bus, locales);
		}		
	}
	
	public SnomedConceptMapApiProvider(IEventBus bus, List<ExtendedLocale> locales) {
		super(bus, locales);
	}
	
	@Override
	public boolean isSupported(ComponentURI componentURI) {
		return componentURI.codeSystem().startsWith(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
	}
	
	@Override
	public Collection<ConceptMap> getConceptMaps(final Set<FhirSearchParameter> searchParameters) {
		
		Optional<FhirSearchParameter> idParamOptional = getSearchParam(searchParameters, "_id");
		Optional<FhirSearchParameter> nameOptional = getSearchParam(searchParameters, "_name");
		
		//Collect every version on every extension
		List<CodeSystemVersion> codeSystemVersionList = collectCodeSystemVersions(repositoryId);
		
		//might be nicer to maintain the order by version
		List<ConceptMap> conceptMaps = codeSystemVersionList.stream()
				.map(csve -> {
					SnomedRefSetSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchRefSet().all();
					
					if (idParamOptional.isPresent()) {
						Collection<String> uris = idParamOptional.get().getValues();
						Collection<String> ids = collectIds(uris);
						requestBuilder.filterByIds(ids);
					}
					
					//TODO - referenced component name?
					if (nameOptional.isPresent()) {
						Collection<String> names = nameOptional.get().getValues();
						//requestBuilder.filterByNameExact(names);
					}
					
					return requestBuilder
						.filterByTypes(CONCEPT_MAP_TYPES)
						// TODO figure out how to expand members on a ConceptMap in a pageable fashion (there is no official API for it right now)
//						.setExpand("members(expand(referencedComponent(expand(pt()))), limit:"+ Integer.MAX_VALUE +")")
						.setLocales(getLocales())
						.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
						.build(csve.getUri())
						.execute(getBus())
						.getSync()
						.stream()
						.map(r -> buildConceptMap(r, csve, getLocales()))
						.map(ConceptMap.Builder::build)
						.collect(Collectors.toList());
				})
				.flatMap(List::stream)
				.collect(Collectors.toList()); //List<List<?> -> List<?>
		
		return conceptMaps;
	}
	
	@Override
	public ConceptMap getConceptMap(ComponentURI componentURI) {
		
		CodeSystemVersion codeSystemVersion = findCodeSystemVersion(componentURI, "ConceptMap.id");
		
		SnomedReferenceSet snomedReferenceSet = SnomedRequests.prepareSearchRefSet()
			.all()
			.filterById(componentURI.identifier())
			.filterByTypes(CONCEPT_MAP_TYPES)
			// TODO figure out how to expand members on a ConceptMap in a pageable fashion (there is no official API for it right now)
//			.setExpand("members(expand(referencedComponent(expand(pt()))), limit:" + Integer.MAX_VALUE +")")
			.setLocales(getLocales())
			.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
			.build(componentURI.codeSystemUri())
			.execute(getBus())
			.getSync()
			.stream()
			.findFirst()
			.orElseThrow(() -> new NotFoundException("Could not find map type refset '%s'.", componentURI.toString()));
		
		ConceptMap.Builder conceptMapBuilder = buildConceptMap(snomedReferenceSet, codeSystemVersion, getLocales());
		
		return conceptMapBuilder.build();
	}
	
	@Override
	public TranslateResult translate(ComponentURI componentURI, TranslateRequest translateRequest) {
		
		String sourceSystem = null;
		String targetSystem = null;
		
		if (translateRequest.getReverse() == null || !translateRequest.getReverse().booleanValue()) {
			sourceSystem = translateRequest.getSystemValue();
			targetSystem = translateRequest.getTargetsystem().getUriValue();
		} else {
			//reverse
			sourceSystem = translateRequest.getTargetsystem().getUriValue();
			targetSystem = translateRequest.getSystemValue();
		}
		
		//source system is not SNOMED CT, this is a contradiction/error
		if (sourceSystem != null && !sourceSystem.startsWith(SnomedUri.SNOMED_BASE_URI_STRING)) {
			throw new BadRequestException("Source system URI '" + sourceSystem + 
					"' is invalid (not SNOMED CT).", "$translate.system");
		}
				
		//can we find the refset in question?
		CodeSystemVersion codeSystemVersion = findCodeSystemVersion(componentURI, "ConceptMap.id");

		SnomedReferenceSet referenceSet = SnomedRequests.prepareSearchRefSet()
			.one()
			.filterByActive(true)
			.filterById(componentURI.identifier())
			.filterByTypes(CONCEPT_MAP_TYPES)
			.build(componentURI.codeSystemUri())
			.execute(getBus())
			.getSync()
			.first()
			.orElseThrow(() -> new NotFoundException("Map type reference set", componentURI.identifier()));
		
		//test the target
		if (targetSystem.equals(SnomedUri.SNOMED_BASE_URI_STRING)) {
			targetSystem = SnomedTerminologyComponentConstants.CONCEPT;
		}
				
		try {
			TerminologyRegistry.INSTANCE.getTerminologyComponentById(targetSystem);
		} catch (RuntimeException ex) {
			throw new BadRequestException("Target system '" + targetSystem + "' not found or invalid.", "$translate.targetsystem");
		}
		
		String mapTargetComponentType = referenceSet.getMapTargetComponentType();
		if (!targetSystem.equals(mapTargetComponentType)) {
			return TranslateResult.builder().message(
					String.format("Reference set '%s', map target component type '%s' does not match the requested target system '%s'.",
							componentURI.toString(), mapTargetComponentType, targetSystem))
					.build();
		}
		
		TranslateResult.Builder builder = TranslateResult.builder();
		builder.message(String.format("Results for reference set '%s' with map target component type '%s'.",
					componentURI.toString(), mapTargetComponentType));
		
		SnomedUri snomedUri = SnomedUri.fromUriString(sourceSystem, "ConceptMap$translate.system");
		
		SnomedRefSetMemberSearchRequestBuilder memberSearchRequestBuilder = SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.setExpand("referencedComponent(expand(pt()))")
				.filterByRefSet(componentURI.identifier())
				.filterByRefSetType(CONCEPT_MAP_TYPES)
				.setLocales(getLocales())
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT);
				

		if (translateRequest.getReverse() == null || !translateRequest.getReverse().booleanValue()) {
			memberSearchRequestBuilder.filterByReferencedComponent(translateRequest.getCodeValue());
		} else {
			//reverse
			memberSearchRequestBuilder.filterByProps(Options.builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, translateRequest.getCodeValue())
					.build());
		}
		
		Set<Match> matches = memberSearchRequestBuilder.build(codeSystemVersion.getUri())
				.execute(getBus())
				.getSync()
				.stream()
				.map(member -> createMatch(snomedUri, member))
				.collect(Collectors.toSet());
		
		builder.addMatches(matches);
		return builder.build();
	}

	@Override
	public Collection<Match> translate(TranslateRequest translateRequest) {
		
		if (!isValidTranslateRequest(translateRequest)) {
			return Collections.emptySet();
		}
		
		String sourceSystem = null;
		String targetSystem = null;
		
		if (translateRequest.getReverse() == null || !translateRequest.getReverse().booleanValue()) {
			sourceSystem = translateRequest.getSystemValue();
			targetSystem = translateRequest.getTargetsystem().getUriValue();
		} else {
			//reverse
			sourceSystem = translateRequest.getTargetsystem().getUriValue();
			targetSystem = translateRequest.getSystemValue();
		}
		
		//"com.b2international.snowowl.terminology.atc.concept" is expected
		//or the standard SNOMED CT URI
		if (targetSystem.equals(SnomedUri.SNOMED_BASE_URI_STRING)) {
			targetSystem = SnomedTerminologyComponentConstants.CONCEPT;
		}
		
		int terminologyComponentIdAsInt = TerminologyRegistry.INSTANCE.getTerminologyComponentById(targetSystem).shortId();
		
		String locationName = "$translate.system";
		SnomedUri snomedUri = SnomedUri.fromUriString(sourceSystem, locationName);
		CodeSystemVersion codeSystemVersion = getCodeSystemVersion(snomedUri.getVersionTag());
		
		//no target code system is specified on the Map
		Set<String> refsetIds = SnomedRequests.prepareSearchRefSet()
			.all()
			.filterByActive(true)
			.filterByTypes(CONCEPT_MAP_TYPES)
			.filterByMapTargetComponentType(terminologyComponentIdAsInt)
			.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
			.build(codeSystemVersion.getUri())
			.execute(getBus())
			.getSync()
			.stream()
			.map(r -> r.getId())
			.collect(Collectors.toSet());
		
		SnomedRefSetMemberSearchRequestBuilder memberSearchRequestBuilder = SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.setExpand("referencedComponent(expand(pt()))")
				.filterByRefSet(refsetIds)
				.filterByRefSetType(CONCEPT_MAP_TYPES)
				.setLocales(getLocales())
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT);
				

		if (translateRequest.getReverse() == null || !translateRequest.getReverse().booleanValue()) {
			memberSearchRequestBuilder.filterByReferencedComponent(translateRequest.getCodeValue());
		} else {
			//reverse
			memberSearchRequestBuilder.filterByProps(Options.builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, translateRequest.getCodeValue())
					.build());
		}
		
		return memberSearchRequestBuilder.build(codeSystemVersion.getUri())
				.execute(getBus())
				.then(members -> {
					return members.stream()
						.map(member -> createMatch(snomedUri, member))
						.collect(Collectors.toSet());
				})
				.getSync();
	}
	
	private Match createMatch(SnomedUri snomedUri, SnomedReferenceSetMember member) {
		
		Coding.Builder codingBuilder = Coding.builder();

		Map<String, Object> properties = member.getProperties();
		String mapTarget = (String) properties.get(SnomedRf2Headers.FIELD_MAP_TARGET);
		
		if (!StringUtils.isEmpty(mapTarget)) {
			codingBuilder.code(mapTarget);
		}
		
		String mapTargetDescription = (String) properties.get(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION);
		
		if (!StringUtils.isEmpty(mapTargetDescription)) {
			codingBuilder.display(mapTargetDescription);
		}
		
		//This should be coming from the referenceSet.getMapTargetType() and TerminologyBroker....
		//codingBuilder.system(system);
		
		//there is no way to get the version of the target code system :-(
		//codingBuilder.version("VERSION");
		
		Match.Builder builder = Match.builder()
			.source(snomedUri.toString() + "/id/" + member.getReferenceSetId())
			.equivalence(getEquivalence(member))
			.concept(codingBuilder.build());
		
		return builder.build();
	}

	private ConceptMap.Builder buildConceptMap(SnomedReferenceSet snomedReferenceSet, CodeSystemVersion codeSystemVersion, List<ExtendedLocale> locales) {
		
		ComponentURI componentURI = ComponentURI.of(codeSystemVersion.getUri(), SnomedTerminologyComponentConstants.REFSET_NUMBER, snomedReferenceSet.getId());
		ConceptMap.Builder conceptMapBuilder = ConceptMap.builder(componentURI.toString());

		String referenceSetId = snomedReferenceSet.getId();

		//Need the query to grab the preferred term of the reference set
		SnomedConcept refsetConcept = SnomedRequests.prepareGetConcept(referenceSetId)
			.setExpand("pt()")
			.setLocales(locales)
			.build(codeSystemVersion.getUri())
			.execute(getBus())
			.getSync();
			
		String pt = getPreferredTermOrId(refsetConcept);
		conceptMapBuilder.name(pt)
			.title(pt)
			.status(snomedReferenceSet.isActive() ? PublicationStatus.ACTIVE : PublicationStatus.RETIRED)
			.date(Date.from(codeSystemVersion.getEffectiveTime().atStartOfDay(ZoneOffset.UTC).toInstant()))
			.language(locales.get(0).getLanguageTag())
			.version(codeSystemVersion.getVersion());
	
		//ID metadata
		SnomedUri uri = SnomedUri.builder().version(codeSystemVersion.getEffectiveDate()).build();
		
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(uri.toString())
			.value(referenceSetId)
			.build();
		
		conceptMapBuilder.url(uri.toUri())
			.identifier(identifier);
		
		Group group = buildGroup(uri, snomedReferenceSet);
		conceptMapBuilder.addGroup(group);
		return conceptMapBuilder;
	}
	
	private Group buildGroup(SnomedUri snomedUri, SnomedReferenceSet snomedReferenceSet) {
		
		//hack for SNOMED
		String targetSystem = null;
		String mapTargetComponentType = snomedReferenceSet.getMapTargetComponentType();
		
		if (mapTargetComponentType == null) {
			targetSystem = "uri://unknown_target_codesystem";
		} else if (mapTargetComponentType.contains("snomed")) {
			targetSystem = SnomedUri.SNOMED_BASE_URI_STRING;
		} else {
			targetSystem = mapTargetComponentType;
		}

		Builder groupBuilder = Group.builder();
		groupBuilder
			.source(SnomedUri.SNOMED_BASE_URI_STRING)  //how about extensions?
			.sourceVersion(snomedUri.getVersionTag())
			.target(targetSystem);
			//.targetVersion(targetVersion) //there is not information
		
		SnomedReferenceSetMembers members = snomedReferenceSet.getMembers();
		
		//no members, nothing to do further
		if (CompareUtils.isEmpty(members)) return groupBuilder.build();
		
		//Potentially many targets for the same source
		Multimap<String, SnomedReferenceSetMember> mappingMultimap = HashMultimap.create();
		
		for (SnomedReferenceSetMember snomedReferenceSetMember : members) {
			mappingMultimap.put(snomedReferenceSetMember.getId(), snomedReferenceSetMember);
		}
		
		for (String memberId : mappingMultimap.keySet()) {
		
			Collection<SnomedReferenceSetMember> targetMembers = mappingMultimap.get(memberId);
			
			//source
			SnomedReferenceSetMember member = targetMembers.iterator().next();
			SnomedConcept concept = (SnomedConcept) member.getReferencedComponent();
			
			ConceptMapElement.Builder elementBuilder = ConceptMapElement.builder();
			elementBuilder.code(concept.getId());
			elementBuilder.display(getPreferredTermOrId(concept));
			
			//Targets - potentially many
			for (SnomedReferenceSetMember mappingSetMember : targetMembers) {
				elementBuilder.addTarget(getTarget(mappingSetMember));
			}
			groupBuilder.addElement(elementBuilder.build());
		}
		return groupBuilder.build();
	}

	private Target getTarget(SnomedReferenceSetMember mappingSetMember) {
		
		Target.Builder targetBuilder = Target.builder();
		
		Map<String, Object> properties = mappingSetMember.getProperties();
		if (properties != null && !properties.isEmpty()) {
			String mapTarget = (String) properties.get(SnomedRf2Headers.FIELD_MAP_TARGET);
			
			if (!StringUtils.isEmpty(mapTarget)) {
				targetBuilder.code(mapTarget);
			}
		}
		targetBuilder.equivalence(getEquivalence(mappingSetMember));
		
		return targetBuilder.build();
	}

	private ConceptMapEquivalence getEquivalence(SnomedReferenceSetMember mappingSetMember) {
		
		SnomedRefSetType snomedRefSetType = mappingSetMember.type();
		
		if (snomedRefSetType == SnomedRefSetType.SIMPLE_MAP) {
			return ConceptMapEquivalence.EQUIVALENT; //probably true
		}
		
		Map<String, Object> properties = mappingSetMember.getProperties();
		
		if (properties == null || properties.isEmpty()) {
			return ConceptMapEquivalence.RELATEDTO; //?
		}
		
		String correlationId = (String) properties.get(SnomedRf2Headers.FIELD_CORRELATION_ID);
		
		switch (correlationId) {
			case Concepts.MAP_CORRELATION_EXACT_MATCH:
				return ConceptMapEquivalence.EQUIVALENT;
			case Concepts.MAP_CORRELATION_BROAD_TO_NARROW:
				return ConceptMapEquivalence.NARROWER;
			case Concepts.MAP_CORRELATION_NARROW_TO_BROAD:
				return ConceptMapEquivalence.WIDER;
			case Concepts.MAP_CORRELATION_PARTIAL_OVERLAP:
				return ConceptMapEquivalence.INEXACT;
			case Concepts.MAP_CORRELATION_NOT_MAPPABLE:
				return ConceptMapEquivalence.DISJOINT;
			case Concepts.MAP_CORRELATION_NOT_SPECIFIED:
				return ConceptMapEquivalence.UNMATCHED;
			default:
				return ConceptMapEquivalence.UNMATCHED; //what should we do here?
			}
	}
	
	private boolean isValidTranslateRequest(TranslateRequest translateRequest) {
		
		//This should not happen
		if (translateRequest.getSystemValue() == null) {
			return false;
		}
				
		//source system is not SNOMED CT, nothing to do here
		if (translateRequest.getReverse() == null || !translateRequest.getReverse().booleanValue()) {
			if (translateRequest.getSystemValue() != null && !translateRequest.getSystemValue().startsWith(SnomedUri.SNOMED_BASE_URI_STRING)) {
				return false;
			}
		} else {
			//reverse
			if (translateRequest.getTargetsystem() != null && !translateRequest.getTargetsystem().getUriValue().startsWith(SnomedUri.SNOMED_BASE_URI_STRING)) {
				return false;
			}
		}
		
		//test the target
		String targetsystem = translateRequest.getTargetsystem().getUriValue();
		if (targetsystem.equals(SnomedUri.SNOMED_BASE_URI_STRING)) {
			targetsystem = SnomedTerminologyComponentConstants.CONCEPT;
		}
		try {
			TerminologyRegistry.INSTANCE.getTerminologyComponentById(targetsystem);
		} catch (RuntimeException ex) {
			return false;
		}
		
		return true;
	}
	
	private String getMapTargetComponentType(String codeSystemShortName) {
		
		com.b2international.snowowl.core.codesystem.CodeSystem codeSystem = CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.filterById(codeSystemShortName)
			.build(repositoryId)
			.execute(getBus())
			.getSync()
			.first()
			.orElseThrow(() -> new NotFoundException("Code system", codeSystemShortName));
		
		String toolingId = codeSystem.getTerminologyId();
		
		//mighty hack
		if (toolingId.contains("snomed")) {
			return SnomedTerminologyComponentConstants.CONCEPT;
		}
		
		return TerminologyRegistry.INSTANCE.getTerminologyComponentIdsByTerminology(toolingId).iterator().next();
	}
	
	//This will be removed for 7.x
	private String getShortName(SnomedReferenceSet snomedReferenceSet) {
		
		//CONCEPT = "com.b2international.snowowl.terminology.atc.concept"
		String mapTargetComponentType = snomedReferenceSet.getMapTargetComponentType();
		
		String unknownTargetCodeSystem = "uri://unknown_target_codesystem";
		
		if (mapTargetComponentType == null ) {
			return unknownTargetCodeSystem;
		}
		
		String terminologyId = TerminologyRegistry.INSTANCE.getTerminologyByTerminologyComponentId(mapTargetComponentType).getId();
		
		CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.build(repositoryId)
			.execute(getBus())
			.getSync();
		
		return codeSystems.getItems().stream()
			.filter(cs -> cs.getTerminologyId().equals(terminologyId))
			.map(cs -> {
				
				//hack for SNOMED CT until URIs are added to the code system registry
				if (cs.getShortName().contains("SNOMEDCT")) {
					return SnomedUri.SNOMED_BASE_URI_STRING;
				} else {
					return cs.getShortName();
				}
			})
			.findFirst()
			.orElse(unknownTargetCodeSystem);
	}

}
