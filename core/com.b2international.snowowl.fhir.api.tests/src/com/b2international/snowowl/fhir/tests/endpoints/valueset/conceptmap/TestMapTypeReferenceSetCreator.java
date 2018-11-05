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
package com.b2international.snowowl.fhir.tests.endpoints.valueset.conceptmap;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.SYNONYM;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.tests.FhirTestConcepts;
import com.b2international.snowowl.fhir.tests.TestArtifactCreator;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @since 7.0
 */
public class TestMapTypeReferenceSetCreator extends TestArtifactCreator {
	
	
	/**
	 * 
	 * @param branchPath
	 * @param refsetName
	 * @param version
	 * @return
	 */
	public static String createSimpleMapTypeReferenceSets(String branchPath, String refsetName, String version) {
	
		Optional<SnomedConcept> refsetConcept = getRefsetConcept(branchPath, refsetName);
		if (!refsetConcept.isPresent()) {
			System.out.println("Creating test map type reference set...");
			String refsetId = createRefsetConcept(branchPath, refsetName, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE));
			
			
			System.out.println("Creating reference set members for amp type refset...");
			createMapping(branchPath, refsetId, FhirTestConcepts.BACTERIA, "Bacteria Target");
			createMapping(branchPath, refsetId, FhirTestConcepts.MICROORGANISM, "MO");
			
			
			
			//version the created content
			System.out.println("Versioning content...");
			createVersion(version, SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
			return refsetId;
		} else {
			System.out.println("Found existing test map type reference set...");
			String refsetId = refsetConcept.get().getId();
			return refsetId;
		}
	}
	
	
	private static void createMapping(String branchPath, String refsetId, String referencedConceptId, String mappingTarget) {
		
		Map<String, Object> properties = Maps.newHashMap();
		properties.put(SnomedRf2Headers.FIELD_MAP_TARGET, mappingTarget);
		
		SnomedRequests.prepareNewMember()
			.setId(UUID.randomUUID().toString())
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setActive(true)
			.setReferenceSetId(refsetId)
			.setProperties(properties)
			.setReferencedComponentId(referencedConceptId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Simple Type Refset Member")
			.execute(ApplicationContext.getServiceForClass(IEventBus.class));
		
	}

	private static Optional<SnomedConcept> getRefsetConcept(String branchPath, String refsetName) {
		Optional<SnomedConcept> refsetConcept = SnomedRequests.prepareSearchConcept()
				.filterByTerm(refsetName)
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync()
				.first();
		return refsetConcept;
	}
	
	private static String createRefsetConcept(String branchPath, String refsetName, String parentConcept) {
		return SnomedRequests.prepareNewConcept()
			.setIdFromNamespace(Concepts.B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.addDescription(createDescription(refsetName +" (foundation metadata concept)", FULLY_SPECIFIED_NAME))
			.addDescription(createDescription(refsetName, SYNONYM))
			.addRelationship(createIsaRelationship(CharacteristicType.STATED_RELATIONSHIP, parentConcept))
			.addRelationship(createIsaRelationship(CharacteristicType.INFERRED_RELATIONSHIP, parentConcept))
			.setRefSet(SnomedRequests.prepareNewRefSet()
					.setReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
					.setType(SnomedRefSetType.SIMPLE))
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Simple Type Reference Set")
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
			.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE)
			.setAcceptability(ImmutableMap.of(SnomedConstants.Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));
	}

	private static SnomedRelationshipCreateRequestBuilder createIsaRelationship(final CharacteristicType characteristicType, String destinationId) {
		return SnomedRequests.prepareNewRelationship() 
			.setIdFromNamespace(Concepts.B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setDestinationId(destinationId)
			.setTypeId(IS_A)
			.setCharacteristicType(characteristicType)
			.setModifier(RelationshipModifier.EXISTENTIAL);
	}
	
}
