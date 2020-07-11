/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.SetMapping;
import com.b2international.snowowl.core.domain.SetMappings;
import com.b2international.snowowl.core.request.MappingCorrelation;
import com.b2international.snowowl.core.request.SetMappingSearchRequestEvaluator;
import com.b2international.snowowl.core.terminology.Terminology;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 7.8
 */
public class SnomedRefSetMappingSearchRequestEvaluator extends SnomedCollectionSearchRequestEvaluator<SetMapping, SetMappings> implements SetMappingSearchRequestEvaluator {

	//RefsetID -> targetComponentURI
	private Map<String, ComponentURI> targetCodeSystemMap = Maps.newHashMap();

	public SetMappings evaluate(CodeSystemURI uri, BranchContext context, Options search) {
		
		if (search.containsKey(OptionKey.SET)) {
			
			final Collection<String> refsetIds = search.getCollection(OptionKey.SET, String.class);
			for (String refsetId : refsetIds) {
				targetCodeSystemMap.put(refsetId, getTargetCodeSystemName(uri, context, refsetId, search));
			}
		}
		
		SnomedReferenceSetMembers referenceSetMembers = fetchRefsetMembers(uri, context, search);
		return toCollectionResource(referenceSetMembers, uri);
	}

	@Override
	protected SetMappings toCollectionResource(SnomedReferenceSetMembers referenceSetMembers, CodeSystemURI uri) {
		
		
		List<SetMapping> mappings = referenceSetMembers.stream()
			.filter(m -> {
				short terminologyComponentId = m.getReferencedComponent().getTerminologyComponentId();
				return terminologyComponentId == SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
			})
			.map(m -> toMapping(m, uri))
			.collect(Collectors.toList());
		
		
		return new SetMappings(mappings,
				referenceSetMembers.getSearchAfter(),
				referenceSetMembers.getLimit(),
				referenceSetMembers.getTotal()
				);
	}
	
	private SetMapping toMapping(SnomedReferenceSetMember member, CodeSystemURI codeSystemURI) {	 		
		final String term;		
		final String iconId = member.getReferencedComponent().getIconId();
		short terminologyComponentId = member.getReferencedComponent().getTerminologyComponentId();
		
		SnomedConcept concept = (SnomedConcept) member.getReferencedComponent();
		term = concept.getFsn().getTerm();
		
		ComponentURI targetComponentURI = ComponentURI.UNSPECIFIED;
		
		Map<String, Object> properties = member.getProperties();
		if (properties != null) {
			String mapTarget = (String) properties.get(SnomedRf2Headers.FIELD_MAP_TARGET);
			if (ComponentURI.isValid(mapTarget)) {
				targetComponentURI = ComponentURI.of(mapTarget);
			} else {
				ComponentURI refsetComponentURI = targetCodeSystemMap.get(member.getReferenceSetId());
				targetComponentURI = ComponentURI.of(refsetComponentURI.codeSystem(), refsetComponentURI.terminologyComponentId(), mapTarget);
			}
		}
		
		SetMapping mapping = new SetMapping(ComponentURI.of(codeSystemURI.getCodeSystem(), terminologyComponentId, member.getReferencedComponentId()),
				targetComponentURI,
				term, 
				iconId,
				"", //targetTerm
				member.isActive(),
				getEquivalence(member));
		
		System.out.println("Mapping: " + mapping);
		return mapping;
	}
	
	private MappingCorrelation getEquivalence(SnomedReferenceSetMember mappingSetMember) {
		
		SnomedRefSetType snomedRefSetType = mappingSetMember.type();
		
		if (snomedRefSetType == SnomedRefSetType.SIMPLE_MAP) {
			return MappingCorrelation.EXACT_MATCH; //probably true
		}
		
		Map<String, Object> properties = mappingSetMember.getProperties();
		
		if (properties == null || properties.isEmpty()) {
			return MappingCorrelation.NOT_SPECIFIED; 
		}
		
		String correlationId = (String) properties.get(SnomedRf2Headers.FIELD_CORRELATION_ID);
		
		switch (correlationId) {
			case Concepts.MAP_CORRELATION_EXACT_MATCH:
				return MappingCorrelation.EXACT_MATCH;
			case Concepts.MAP_CORRELATION_BROAD_TO_NARROW:
				return MappingCorrelation.BROAD_TO_NARROW;
			case Concepts.MAP_CORRELATION_NARROW_TO_BROAD:
				return MappingCorrelation.NARROW_TO_BROAD;
			case Concepts.MAP_CORRELATION_PARTIAL_OVERLAP:
				return MappingCorrelation.PARTIAL_OVERLAP;
			case Concepts.MAP_CORRELATION_NOT_MAPPABLE:
				return MappingCorrelation.NOT_MAPPABLE;
			case Concepts.MAP_CORRELATION_NOT_SPECIFIED:
				return MappingCorrelation.NOT_SPECIFIED;
			default:
				return MappingCorrelation.NOT_SPECIFIED;
			}
	}

	@Override
	protected Set<SnomedRefSetType> getRefsetTypes() {
		return Sets.newHashSet(SnomedRefSetType.SIMPLE_MAP, 
				SnomedRefSetType.COMPLEX_MAP, 
				SnomedRefSetType.EXTENDED_MAP, 
				SnomedRefSetType.COMPLEX_BLOCK_MAP);
	}
	
	private ComponentURI getTargetCodeSystemName(CodeSystemURI uri, final BranchContext context, final String refsetId, Options search) {
		
		SnomedReferenceSet referenceSet = SnomedRequests.prepareGetReferenceSet(refsetId)
			.build()
			.execute(context);
		
		String mapTargetComponentType = referenceSet.getMapTargetComponentType();
		if (mapTargetComponentType == null) {
			return ComponentURI.UNSPECIFIED;
		}
		
		TerminologyRegistry terminologyRegistry = TerminologyRegistry.INSTANCE;
		
		Terminology sourceTerminology = terminologyRegistry.getTerminologyByTerminologyComponentId(mapTargetComponentType);
		System.out.println("Terminology ID: " + sourceTerminology.getId());
		
		TerminologyComponent sourceTerminologyComponent = terminologyRegistry.getTerminologyComponentById(mapTargetComponentType);
		System.out.println("STC: " + sourceTerminologyComponent.shortId());
		
		
		if (Strings.isNullOrEmpty(mapTargetComponentType)) {
			return ComponentURI.UNSPECIFIED;
		} else {
		
			Optional<CodeSystem> codeSystemOptional = CodeSystemRequests.getAllCodeSystems(context)
					.stream()
					.filter(cs -> cs.getTerminologyId().equals(sourceTerminology.getId()))
					.findFirst();
			if (codeSystemOptional.isPresent()) {
				return ComponentURI.of(codeSystemOptional.get().getShortName(), sourceTerminologyComponent.shortId(), refsetId);
			} else {
				return ComponentURI.UNSPECIFIED;
			}
		}
	}


}
