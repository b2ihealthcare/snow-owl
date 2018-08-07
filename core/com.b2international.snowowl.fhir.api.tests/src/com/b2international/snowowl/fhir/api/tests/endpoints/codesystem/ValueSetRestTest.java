/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.tests.endpoints.codesystem;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.SYNONYM;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.request.CommitResult;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
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
import com.b2international.snowowl.snomed.fhir.SnomedUri;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;

/**
 * Generic ValueSet REST end-point test cases
 * @since 6.7
 */
public class ValueSetRestTest extends FhirTest {
	
	/**
	 * 
	 */
	private static final String FHIR_QUERY_TYPE_REFSET_VERSION = "FHIR_QUERY_TYPE_REFSET_VERSION";
	private static final String B2I_NAMESPACE = "1000154";
	
	@BeforeClass
	public static void setupSpec() {
		
		RestAssuredConfig config = RestAssured.config();
		LogConfig logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.given().config(config.logConfig(logConfig));
	}
	
	//@Test
	public void printValueSets() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.when().get("/ValueSet")
		.prettyPrint();
	}
	
	//@Test
	public void valueSetsTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.when().get("/ValueSet")
		.then()
		.body("resourceType", equalTo("Bundle"))
		.body("type", equalTo("searchset"))
		.body("total", notNullValue())
		
		//SNOMED CT
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/snomedStore:MAIN/2018-01-31:723264001'}")
		.body("resource.resourceType", equalTo("ValueSet"))
		.body("resource.id", equalTo("snomedStore:MAIN/2018-01-31:723264001"))
		.body("resource.url", equalTo("http://snomed.info/sct/version/20180131"))
		.body("resource.version", equalTo("2018-01-31"))
		.body("resource.title", equalTo("Lateralizable body structure reference set"))
		.body("resource.name", equalTo("Lateralizable body structure reference set"))
		.body("resource.status", equalTo("active"))
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/snomedStore:MAIN/2018-01-31:723264001'}.resource.compose[0].include[0]")
		.body("system", equalTo(SnomedUri.SNOMED_BASE_URI_STRING))
		.body("filter.size()", equalTo(1))
		.body("filter[0].property", equalTo("expression"))
		.body("filter[0].value", equalTo("^723264001"))
		.body("filter[0].op", equalTo("="))
		.statusCode(200);
	}
	
	//@Test
	public void valueSetsSummaryTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.param("_summary", true)
		.when().get("/ValueSet")
		.then()
		.body("resourceType", equalTo("Bundle"))
		.body("type", equalTo("searchset"))
		.body("total", notNullValue())
		
		//SNOMED CT
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/snomedStore:MAIN/2018-01-31:723264001'}")
		.body("resource.resourceType", equalTo("ValueSet"))
		.body("resource.id", equalTo("snomedStore:MAIN/2018-01-31:723264001"))
		.body("resource.url", equalTo("http://snomed.info/sct/version/20180131"))
		.body("resource.version", equalTo("2018-01-31"))
		.body("resource.title", equalTo("Lateralizable body structure reference set"))
		.body("resource.name", equalTo("Lateralizable body structure reference set"))
		.body("resource.status", equalTo("active"))
		
		//subsetted
		.body("resource.meta.tag[0].code", equalTo("SUBSETTED"))
		
		.statusCode(200);
	}
	
	//This is junk as the ID is hard-coded
	//@Test
	public void getSingleSnomedValueSetTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore:MAIN/2018-01-31:723264001") 
			.when().get("/ValueSet/{id}")
			.prettyPrint();
	}
	
	//'Virtual' value set
	@Test
	public void getSingleQueryTypeValueSetTest() {
		
		String refsetLogicalId = getRefsetLogicalId();
		System.out.println("Refset concept ID: " + refsetLogicalId);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore:MAIN/" + FHIR_QUERY_TYPE_REFSET_VERSION + ":" + refsetLogicalId) 
			.when().get("/ValueSet/{id}")
			.prettyPrint();
	}

	
	//************************************************************ //
	/**
	 * @return
	 */
	private String getRefsetLogicalId() {
		
		String mainBranch = IBranchPath.MAIN_BRANCH;
		String refsetName = "FHIR Automated Test Query Type Refset";
		
		
		Optional<SnomedConcept> refsetConcept = SnomedRequests.prepareSearchConcept()
			.filterByTerm(refsetName)
			.all()
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, mainBranch)
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync()
			.first();
		
		if (!refsetConcept.isPresent()) {
			System.out.println("Creating test query type reference set...");
			String combinedId = createRefset(mainBranch, refsetName);
			System.out.println("Versioning content...");
			createVersion();
			
			return combinedId;
			
		} else {
			System.out.println("Found existing test query type reference set...");
			String refsetId = refsetConcept.get().getId();
			
			//grab the first member
			SnomedReferenceSetMember firstMember = SnomedRequests.prepareSearchMember()
				.one()
				.filterByRefSet(refsetId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, mainBranch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync()
				.stream()
				.findFirst()
				.get();
			
			return refsetId + "|" + firstMember.getId();
		}
	}

	/**
	 * 
	 */
	private void createVersion() {
		
		Request<ServiceProvider, Boolean> request = CodeSystemRequests.prepareNewCodeSystemVersion()
			.setCodeSystemShortName(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME)
			.setDescription("FHIR Test version for Query type reference sets")
			.setVersionId(FHIR_QUERY_TYPE_REFSET_VERSION)
			.setEffectiveTime(new Date())
			.build();
			
		String jobId = JobRequests.prepareSchedule()
			.setDescription(String.format("Creating version '%s/%s'", 
					SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, FHIR_QUERY_TYPE_REFSET_VERSION))
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

	private String createRefset(String branchPath, String refsetName) {
		
		CommitResult commitResult = SnomedRequests.prepareNewConcept()
			.setIdFromNamespace(B2I_NAMESPACE)
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
		
		String referencedSimpleTypeRefsetId = createSimpleTypeRefsetConcept(branchPath);
		
		Map<String, Object> memberMap = Maps.newHashMap();
		memberMap.put(SnomedRf2Headers.FIELD_QUERY, "<<49111001");
		
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
	
	private String createSimpleTypeRefsetConcept(String branchPath) {
		return SnomedRequests.prepareNewConcept()
			.setIdFromNamespace(B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.addDescription(createDescription("FHIR Automated Test Simple Type Refset (foundation metadata concept)", FULLY_SPECIFIED_NAME))
			.addDescription(createDescription("FHIR Automated Test Simple Type Refset", SYNONYM))
			.addRelationship(createIsaRelationship(CharacteristicType.STATED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE)))
			.addRelationship(createIsaRelationship(CharacteristicType.INFERRED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE)))
			.setRefSet(SnomedRequests.prepareNewRefSet()
					.setReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
					.setType(SnomedRefSetType.SIMPLE))
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath, "info@b2international.com", "FHIR Automated Test Simple Type Value Set")
			.execute(getEventBus())
			.getSync()
			.getResultAs(String.class);
	}

	private SnomedDescriptionCreateRequestBuilder createDescription(final String term, final String type) {
		
		return SnomedRequests.prepareNewDescription()
			.setIdFromNamespace(B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setLanguageCode("en")
			.setTypeId(type)
			.setTerm(term)
			.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE)
			.setAcceptability(ImmutableMap.of(SnomedConstants.Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));
}

	private SnomedRelationshipCreateRequestBuilder createIsaRelationship(final CharacteristicType characteristicType, String destinationId) {
		return SnomedRequests.prepareNewRelationship() 
			.setIdFromNamespace(B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setDestinationId(destinationId)
			.setTypeId(IS_A)
			.setCharacteristicType(characteristicType)
			.setModifier(RelationshipModifier.EXISTENTIAL);
	}
	
	private IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
}