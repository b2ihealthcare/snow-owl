/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.commons.StringUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
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
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
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
public class SnomedConceptMapApiProvider extends SnomedFhirApiProvider implements IConceptMapApiProvider {

	@Override
	public Collection<ConceptMap> getConceptMaps() {
		
		//Collect every version on every extension
		List<CodeSystemVersionEntry> codeSystemVersionList = collectCodeSystemVersions(repositoryId);
		
		int all = Integer.MAX_VALUE;
		
		//might be nicer to maintain the order by version
		List<ConceptMap> conceptMaps = codeSystemVersionList.stream().map(csve -> {
			
			return SnomedRequests.prepareSearchRefSet()
				.all()
				.filterByTypes(ImmutableList.of(SnomedRefSetType.SIMPLE_MAP, SnomedRefSetType.COMPLEX_MAP, SnomedRefSetType.EXTENDED_MAP))
				.setExpand("members(expand(referencedComponent(expand(pt()))), limit:"+ all +")")
				.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.build(repositoryId, csve.getPath())
				.execute(getBus())
				.then(refsets -> {
					return refsets.stream()
						.map(r -> buildConceptMap(r, csve, displayLanguage))
						.map(ConceptMap.Builder::build)
						.collect(Collectors.toList());
				})
				.getSync();
				
		}).collect(Collectors.toList())
			.stream().flatMap(List::stream).collect(Collectors.toList()); //List<List<?> -> List<?>
		
		return conceptMaps;
	}
	
	@Override
	public ConceptMap getConceptMap(LogicalId logicalId) {
		
		CodeSystemVersionEntry codeSystemVersion = findCodeSystemVersion(logicalId);
		
		int all = Integer.MAX_VALUE;
		
		SnomedReferenceSet snomedReferenceSet = SnomedRequests.prepareSearchRefSet()
			.all()
			.filterById(logicalId.getComponentId())
			.filterByTypes(ImmutableList.of(SnomedRefSetType.SIMPLE_MAP, SnomedRefSetType.COMPLEX_MAP, SnomedRefSetType.EXTENDED_MAP))
			.setExpand("members(expand(referencedComponent(expand(pt()))), limit:"+ all +")")
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
			.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
			.build(repositoryId,logicalId.getBranchPath())
			.execute(getBus())
			.getSync()
			.stream()
			.findFirst()
			.orElseThrow(() -> new NotFoundException("Could not find map type refset '%s'.", logicalId.toString()));
		
		ConceptMap.Builder conceptMapBuilder = buildConceptMap(snomedReferenceSet, codeSystemVersion, displayLanguage);
		
		return conceptMapBuilder.build();
	}
	
	@Override
	public TranslateResult translate(LogicalId logicalId, TranslateRequest translateRequest) {
		
		//TODO: move this somewhere else
		if (!translateRequest.getSystemValue().equals("SNOMEDCT")) {
			return TranslateResult.builder().message("Source code system is not SNOMED CT.").build();
		}
	
		/*
		String mapTargetComponentId = getMapTargetComponentType(translateRequest.getSystem());
		SnomedRequests.prepareSearchRefSet().all()
		.filterByActive(true)
		.filterByMapTargetComponentType(mapTargetComponentId)
		..
		*/
		
		//Cannot filter for map target component type (implicite the target code system)
		int all = Integer.MAX_VALUE;
		SnomedRequests.prepareSearchMember()
			.all()
			.filterById(logicalId.getComponentId())
			.filterByActive(true)
			.setExpand("referencedComponent(expand(pt()))")
			.filterByReferencedComponent(translateRequest.getCodeValue())
			.filterByRefSetType(ImmutableList.of(SnomedRefSetType.SIMPLE_MAP, SnomedRefSetType.COMPLEX_MAP, SnomedRefSetType.EXTENDED_MAP))
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
			.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
			.build(repositoryId, logicalId.getBranchPath())
			.execute(getBus())
			.then(ms -> {
				
				return ms.stream().filter(m -> {
				
					m.getReferenceSetId();
					return true;
				})
				.collect(Collectors.toSet());
			})
			.getSync();
		
		throw new NotImplementedException();
	}

	@Override
	public Collection<Match> translate(TranslateRequest translateRequest) {
		
		//This should not happen
		if (translateRequest.getSystemValue() == null) {
			return Collections.emptySet();
		}
		
		//source system is not SNOMED CT, nothing to do here
		if (translateRequest.getSystemValue() != null && !translateRequest.getSystemValue().startsWith(SnomedUri.SNOMED_BASE_URI_STRING)) {
			return Collections.emptySet();
		}
		System.err.println("I am here: " + translateRequest.getSystemValue());
		
		String locationName = "$translate.system";
		SnomedUri snomedUri = SnomedUri.fromUriString(translateRequest.getSystemValue(), locationName);
		CodeSystemVersionEntry codeSystemVersion = getCodeSystemVersion(snomedUri.getVersionTag());
		
		//Look for the source in ANY mapping set
		return SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.setExpand("referencedComponent(expand(pt()))")
			.filterByReferencedComponent(translateRequest.getCodeValue())
			.filterByRefSetType(ImmutableList.of(SnomedRefSetType.SIMPLE_MAP, SnomedRefSetType.COMPLEX_MAP, SnomedRefSetType.EXTENDED_MAP))
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
			.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
			.build(repositoryId, codeSystemVersion.getPath())
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

	private ConceptMap.Builder buildConceptMap(SnomedReferenceSet snomedReferenceSet, CodeSystemVersionEntry codeSystemVersion, String displayLanguage) {
		
		LogicalId logicalId = new LogicalId(repositoryId, codeSystemVersion.getPath(), snomedReferenceSet.getId());
		ConceptMap.Builder conceptMapBuilder = ConceptMap.builder(logicalId.toString());

		String referenceSetId = snomedReferenceSet.getId();

		//Need the query to grab the preferred term of the reference set
		SnomedConcept refsetConcept = SnomedRequests.prepareGetConcept(referenceSetId)
			.setExpand("pt()")
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
			.build(getRepositoryId(), codeSystemVersion.getPath())
			.execute(getBus())
			.getSync();
			
		conceptMapBuilder.name(refsetConcept.getPt().getTerm())
			.title(refsetConcept.getPt().getTerm())
			.status(snomedReferenceSet.isActive() ? PublicationStatus.ACTIVE : PublicationStatus.RETIRED)
			.date(new Date(codeSystemVersion.getEffectiveDate()))
			.language(displayLanguage)
			.version(codeSystemVersion.getVersionId());
	
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
		
		String targetCodeSystemShortName = getShortName(snomedReferenceSet);
		
		Builder groupBuilder = Group.builder();
		groupBuilder
			.source(SnomedUri.SNOMED_BASE_URI_STRING)  //how about extensions?
			.sourceVersion(snomedUri.getVersionTag())
			.target(targetCodeSystemShortName);
			//.targetVersion(targetVersion) //there is not information
		
		SnomedReferenceSetMembers members = snomedReferenceSet.getMembers();
		
		//no members, nothing to do further
		if (members.isEmpty()) return groupBuilder.build();
		
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
			elementBuilder.display(concept.getPt().getTerm());
			
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
	
	private String getMapTargetComponentType(String codeSystemShortName) {
		
		CodeSystemEntry codeSystemEntry = CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.filterById(codeSystemShortName)
			.build(repositoryId)
			.execute(getBus())
			.getSync()
			.first()
			.orElseThrow(() -> new NotFoundException("Code system", codeSystemShortName));
		
		String toolingId = codeSystemEntry.getTerminologyComponentId();
		Collection<String> componentTypeIds = CoreTerminologyBroker.getInstance().getAllRegisteredTerminologiesWithComponents().get(toolingId);
		
		//mighty hack
		if (toolingId.contains("snomed")) {
			return SnomedTerminologyComponentConstants.CONCEPT;
		}
		return componentTypeIds.iterator().next();
	}

	//This will be removed for 7.x
	private String getShortName(SnomedReferenceSet snomedReferenceSet) {
		String mapTargetComponentType = snomedReferenceSet.getMapTargetComponentType();
		
		String unknownTargetCodeSystem = "uri://unknown_target_codesystem";
		
		if (mapTargetComponentType == null ) {
			return unknownTargetCodeSystem;
		}
		
		String terminologyId = CoreTerminologyBroker.getInstance().getTerminologyIdForTerminologyComponentId(mapTargetComponentType);
		
		CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.build(repositoryId)
			.execute(getBus())
			.getSync();
		
		return codeSystems.getItems().stream()
			.filter(cs -> cs.getTerminologyComponentId().equals(terminologyId))
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
