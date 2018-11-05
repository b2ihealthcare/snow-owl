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
package com.b2international.snowowl.fhir.tests.endpoints.valueset;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.SYNONYM;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.request.CommitResult;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.tests.FhirTestConcepts;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @since 7.0
 */
public class TestArtifactCreator {
	
	
	/**
	 * 
	 * @param branchPath
	 * @param refsetName
	 * @param version
	 * @return
	 */
	public static String createSimpleTypeReferenceSet(String branchPath, String refsetName, String version) {
	
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
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Simple Type Refset Member")
			.execute(ApplicationContext.getServiceForClass(IEventBus.class));
		
	}


	/**
	 * @param branchPath
	 * @param refsetName
	 * @return refset logical id
	 */
	public static String createQueryTypeReferenceSet(String branchPath, String refsetName, String version) {
		
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
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
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
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
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
			.addRelationship(createIsaRelationship(CharacteristicType.STATED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.QUERY)))
			.addRelationship(createIsaRelationship(CharacteristicType.INFERRED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.QUERY)))
			.setRefSet(SnomedRequests.prepareNewRefSet()
				.setReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.setType(SnomedRefSetType.QUERY))
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Query Type Reference Set")
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
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Query Type Value Set")
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
			.addRelationship(createIsaRelationship(CharacteristicType.STATED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE)))
			.addRelationship(createIsaRelationship(CharacteristicType.INFERRED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE)))
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
	
	private static void createVersion(String version, String codeSystemName) {
		
		Request<ServiceProvider, Boolean> request = CodeSystemRequests.prepareNewCodeSystemVersion()
			.setCodeSystemShortName(codeSystemName)
			.setDescription("FHIR Test version")
			.setVersionId(version)
			.setEffectiveTime(new Date())
			.build();
			
		String jobId = JobRequests.prepareSchedule()
			.setDescription(String.format("Creating version '%s/%s'", 
					codeSystemName, version))
			.setUser(User.SYSTEM.getUsername())
			.setRequest(request)
			.buildAsync()
			.execute(getEventBus())
			.getSync();
		
		RemoteJobEntry job = null;
		do {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new SnowowlRuntimeException(e);
			}
			
			job = JobRequests.prepareGet(jobId)
					.buildAsync()
					.execute(getEventBus())
					.getSync();
		} while (job == null || !job.isDone());
	}
	
	private static IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

}
