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
package com.b2international.snowowl.fhir.tests.endpoints.conceptmap;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.SYNONYM;

import java.util.List;
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
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @since 7.0
 */
public class TestMapTypeReferenceSetCreator extends TestArtifactCreator {
	
	
	/**
	 * 
	 * @param branchPath
	 * @param simpleMapName
	 * @param version
	 * @return
	 */
	public static List<String> createSimpleMapTypeReferenceSets(String branchPath, String simpleMapName, String complexMapName, String extendedMapName, String version) {
	
		List<String> refsetIds = Lists.newArrayList();
		
		Optional<SnomedDescription> refsetDescription = getRefsetConcept(branchPath, simpleMapName + " (foundation metadata concept)");
		
		if (!refsetDescription.isPresent()) {
			
			System.out.println("Creating test map type reference set...");
			String refsetId = createRefsetConcept(branchPath, simpleMapName, SnomedRefSetType.SIMPLE_MAP);
			System.out.println("Creating reference set members for map type refset: " + simpleMapName);
			createSimpleMapping(branchPath, refsetId, FhirTestConcepts.BACTERIA, "Bacteria Target");
			createSimpleMapping(branchPath, refsetId, FhirTestConcepts.MICROORGANISM, "MO");
			refsetIds.add(refsetId);
			
			
			System.out.println("Creating test map type reference set...");
			refsetId = createRefsetConcept(branchPath, complexMapName, SnomedRefSetType.COMPLEX_MAP);
			System.out.println("Creating reference set members for complex map type refset: " + complexMapName);
			createComplexMapping(branchPath, refsetId, FhirTestConcepts.BACTERIA, "Bacteria Target");
			createComplexMapping(branchPath, refsetId, FhirTestConcepts.MICROORGANISM, "MO");
			refsetIds.add(refsetId);
			
			System.out.println("Creating test map type reference set...");
			refsetId = createRefsetConcept(branchPath, extendedMapName, SnomedRefSetType.EXTENDED_MAP);
			System.out.println("Creating reference set members for extended map type refset: " + extendedMapName);
			createExtendedMapping(branchPath, refsetId, FhirTestConcepts.BACTERIA, "Bacteria Target");
			createExtendedMapping(branchPath, refsetId, FhirTestConcepts.MICROORGANISM, "MO");
			refsetIds.add(refsetId);
			
			//version the created content
			System.out.println("Versioning content...");
			createVersion(version, SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
			return refsetIds;
		} else {
			System.out.println("Found existing test map type reference set...");
			Optional<SnomedDescription> simpleConcept = getRefsetConcept(branchPath, simpleMapName);
			refsetIds.add(simpleConcept.get().getConceptId());
			Optional<SnomedDescription> complexConcept = getRefsetConcept(branchPath, complexMapName);
			refsetIds.add(complexConcept.get().getConceptId());
			Optional<SnomedDescription> extendedConcept = getRefsetConcept(branchPath, extendedMapName);
			refsetIds.add(extendedConcept.get().getConceptId());
			return refsetIds;
		}
	}
	
	private static void createSimpleMapping(String branchPath, String refsetId, String referencedConceptId, String mappingTarget) {
		
		Map<String, Object> properties = Maps.newHashMap();
		properties.put(SnomedRf2Headers.FIELD_MAP_TARGET, mappingTarget);
		
		SnomedRequests.prepareNewMember()
			.setId(UUID.randomUUID().toString())
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setActive(true)
			.setReferenceSetId(refsetId)
			.setProperties(properties)
			.setReferencedComponentId(referencedConceptId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Simple Map Type Refset Member")
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync();
		
	}
	
	private static void createComplexMapping(String branchPath, String refsetId, String referencedConceptId, String mappingTarget) {
		
		Map<String, Object> properties = Maps.newHashMap();
		properties.put(SnomedRf2Headers.FIELD_MAP_TARGET, mappingTarget);
		properties.put(SnomedRf2Headers.FIELD_MAP_ADVICE, "If microorganism then use something else");
		properties.put(SnomedRf2Headers.FIELD_MAP_GROUP, 1);
		properties.put(SnomedRf2Headers.FIELD_MAP_PRIORITY, 1);
		properties.put(SnomedRf2Headers.FIELD_MAP_RULE, "OTHERWISE TRUE");
		properties.put(SnomedRf2Headers.FIELD_CORRELATION_ID, "447561005"); //correlation not specified
		
		SnomedRequests.prepareNewMember()
			.setId(UUID.randomUUID().toString())
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setActive(true)
			.setReferenceSetId(refsetId)
			.setProperties(properties)
			.setReferencedComponentId(referencedConceptId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Complex Map Type Refset Member")
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync();
		
	}
	
	private static void createExtendedMapping(String branchPath, String refsetId, String referencedConceptId, String mappingTarget) {
		
		Map<String, Object> properties = Maps.newHashMap();
		properties.put(SnomedRf2Headers.FIELD_MAP_TARGET, mappingTarget);
		properties.put(SnomedRf2Headers.FIELD_MAP_ADVICE, "If microorganism then use something else");
		properties.put(SnomedRf2Headers.FIELD_MAP_GROUP, 1);
		properties.put(SnomedRf2Headers.FIELD_MAP_PRIORITY, 1);
		properties.put(SnomedRf2Headers.FIELD_MAP_RULE, "OTHERWISE TRUE");
		properties.put(SnomedRf2Headers.FIELD_CORRELATION_ID, "447561005"); //correlation not specified
		properties.put(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, "447639009");
		
		SnomedRequests.prepareNewMember()
			.setId(UUID.randomUUID().toString())
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setActive(true)
			.setReferenceSetId(refsetId)
			.setProperties(properties)
			.setReferencedComponentId(referencedConceptId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Extended Map Type Refset Member")
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync();
		
	}
	
	

	private static Optional<SnomedDescription> getRefsetConcept(String branchPath, String refsetName) {
		
		return SnomedRequests.prepareSearchDescription()
			.one()
			.filterByExactTerm(refsetName)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync()
			.first();
		
		/*
		Optional<SnomedConcept> refsetConcept = SnomedRequests.prepareSearchConcept()
				.filterByTerm(refsetName)
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync()
				.first();
		
		return refsetConcept;
		*/
	}
	
	private static String createRefsetConcept(String branchPath, String refsetName, SnomedRefSetType refsetType) {
		
		return SnomedRequests.prepareNewConcept()
			.setIdFromNamespace(Concepts.B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.addDescription(createDescription(refsetName + " (foundation metadata concept)", FULLY_SPECIFIED_NAME))
			.addDescription(createDescription(refsetName, SYNONYM))
			.addRelationship(createIsaRelationship(CharacteristicType.STATED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(refsetType)))
			.addRelationship(createIsaRelationship(CharacteristicType.INFERRED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(refsetType)))
			.setRefSet(SnomedRequests.prepareNewRefSet()
					.setReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
					.setMapTargetComponentType(SnomedTerminologyComponentConstants.CONCEPT)
					.setType(refsetType))
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
