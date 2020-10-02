/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.endpoints.valueset;

import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.SYNONYM;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.fhir.tests.FhirTestConcepts;
import com.b2international.snowowl.fhir.tests.TestArtifactCreator;
import com.b2international.snowowl.snomed.common.SnomedConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @since 7.0
 */
public class TestReferenceSetCreator extends TestArtifactCreator {
	
	/**
	 * @param branchPath
	 * @param refsetName
	 * @param version
	 * @return
	 */
	public static synchronized String createSimpleTypeReferenceSet(String branchPath, String refsetName, String version) {
	
		Optional<SnomedConcept> refsetConcept = getRefsetConcept(branchPath, refsetName);
		if (!refsetConcept.isPresent()) {
			System.out.println("Creating test simple type reference set...");
			String refsetId = createSimpleTypeRefsetConcept(branchPath, refsetName);
			
			
			System.out.println("Creating reference set members for simple type refset...");
			createMember(branchPath, refsetId, FhirTestConcepts.BACTERIA);
			createMember(branchPath, refsetId, FhirTestConcepts.MICROORGANISM);
			
			System.out.println("Versioning content...");
			createVersion(version, SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
			return refsetId;
		} else {
			System.out.println("Found existing test simple type reference set...");
			String refsetId = refsetConcept.get().getId();
			return refsetId;
		}
	}
	
	
	private static void createMember(String branchPath, String refsetId, String referencedConceptId) {
		
		SnomedRequests.prepareNewMember()
			.setId(UUID.randomUUID().toString())
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setActive(true)
			.setReferenceSetId(refsetId)
			.setReferencedComponentId(referencedConceptId)
			.build(REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Simple Type Refset Member")
			.execute(getEventBus())
			.getSync();
	}


	/**
	 * @param branchPath
	 * @param refsetName
	 * @return refset logical id
	 */
	public static synchronized String createQueryTypeReferenceSet(String branchPath, String refsetName, String version) {
		
		Optional<SnomedConcept> refsetConcept = getRefsetConcept(branchPath, refsetName);
			
			if (!refsetConcept.isPresent()) {
				System.out.println("Creating test query type reference set...");
				String combinedId = createQueryTypeReferenceset(branchPath, refsetName);
				System.out.println("Versioning content...");
				createVersion(version, SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
				
				return combinedId;
				
			} else {
				System.out.println("Found existing test query type reference set...");
				String refsetId = refsetConcept.get().getId();
				
				//grab the first member
				SnomedReferenceSetMember firstMember = SnomedRequests.prepareSearchMember()
					.one()
					.filterByRefSet(refsetId)
					.build(REPOSITORY_UUID, branchPath)
					.execute(getEventBus())
					.getSync()
					.stream()
					.findFirst()
					.get();
				
				return refsetId + "|" + firstMember.getId();
			}
	}

	private static Optional<SnomedConcept> getRefsetConcept(String branchPath, String refsetName) {
		Optional<SnomedConcept> refsetConcept = SnomedRequests.prepareSearchConcept()
				.filterByTerm(refsetName)
				.all()
				.build(REPOSITORY_UUID, branchPath)
				.execute(getEventBus())
				.getSync()
				.first();
		return refsetConcept;
	}
	
	private static String createQueryTypeReferenceset(String branchPath, String refsetName) {
		
		CommitResult commitResult = SnomedRequests.prepareNewConcept()
			.setIdFromNamespace(Concepts.B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.addDescription(createDescription(refsetName + "FHIR Automated Test Query Type Refset (foundation metadata concept)", FULLY_SPECIFIED_NAME))
			.addDescription(createDescription(refsetName, SYNONYM))
			.addRelationship(createIsaRelationship(Concepts.STATED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.QUERY)))
			.addRelationship(createIsaRelationship(Concepts.INFERRED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.QUERY)))
			.setRefSet(SnomedRequests.prepareNewRefSet()
				.setReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.setType(SnomedRefSetType.QUERY))
			.build(REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Query Type Reference Set")
			.execute(getEventBus())
			.fail(t -> {
				t.printStackTrace();
				return null;
			})
			.getSync();
		
		String refsetId = commitResult.getResultAs(String.class);
		
		String referencedSimpleTypeRefsetId = createSimpleTypeRefsetConcept(branchPath, "FHIR Automated Test Simple Type Refset");
		
		Map<String, Object> memberMap = Maps.newHashMap();
		memberMap.put(SnomedRf2Headers.FIELD_QUERY, "<<410607006"); //Organism (SNOMED MINI)
		
		String memberId = SnomedRequests.prepareNewMember()
			.setReferenceSetId(refsetId)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setActive(true)
			.setProperties(memberMap)
			.setReferencedComponentId(referencedSimpleTypeRefsetId)
			.build(REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Query Type Value Set")
			.execute(getEventBus())
			.getSync()
			.getResultAs(String.class);
		
		return refsetId + "|" + memberId;
	}
	
	private static String createSimpleTypeRefsetConcept(String branchPath, String refsetName) {
		return SnomedRequests.prepareNewConcept()
			.setIdFromNamespace(Concepts.B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.addDescription(createDescription(refsetName +" (foundation metadata concept)", FULLY_SPECIFIED_NAME))
			.addDescription(createDescription(refsetName, SYNONYM))
			.addRelationship(createIsaRelationship(Concepts.STATED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE)))
			.addRelationship(createIsaRelationship(Concepts.INFERRED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE)))
			.setRefSet(SnomedRequests.prepareNewRefSet()
					.setReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
					.setType(SnomedRefSetType.SIMPLE))
			.build(REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Simple Type Reference Set")
			.execute(getEventBus())
			.getSync()
			.getResultAs(String.class);
	}

	private static SnomedDescriptionCreateRequestBuilder createDescription(final String term, final String type) {
		
		return SnomedRequests.prepareNewDescription()
			.setIdFromNamespace(Concepts.B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setLanguageCode("en")
			.setTypeId(type)
			.setTerm(term)
			.setCaseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
			.setAcceptability(ImmutableMap.of(SnomedConstants.Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));
	}

	private static SnomedRelationshipCreateRequestBuilder createIsaRelationship(final String characteristicTypeId, String destinationId) {
		return SnomedRequests.prepareNewRelationship() 
			.setIdFromNamespace(Concepts.B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setDestinationId(destinationId)
			.setTypeId(IS_A)
			.setCharacteristicTypeId(characteristicTypeId)
			.setModifierId(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);
	}
	
}
