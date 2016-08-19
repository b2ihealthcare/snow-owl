/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.*;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
 * TODO try to create a member with invalid refcompid
 * TODO update query refset member's query
 * @since 4.5
 */
public class SnomedRefSetMemberApiTest extends AbstractSnomedApiTest {

	@Test
	public void getReferenceSetMemberFromNonExistingBranch() throws Exception {
		assertComponentReadWithStatus(BranchPathUtils.createPath("MAIN/nonexistent"), SnomedComponentType.MEMBER, "fake", 404);
	}
	
	@Test
	public void getNonExistingReferenceSetMember() throws Exception {
		assertComponentReadWithStatus(BranchPathUtils.createMainPath(), SnomedComponentType.MEMBER, "123456789", 404);
	}

	@Test
	public void cannotCreateMemberWithoutReferenceSet() throws Exception {
		givenBranchWithPath(testBranchPath);
		// try to create member
		final ISnomedIdentifierService identifierService = ApplicationContext.getInstance().getServiceChecked(ISnomedIdentifierService.class);
		final String referenceSetId = identifierService.reserve(null, ComponentCategory.CONCEPT);
		final String referencedComponentId = identifierService.reserve(null, ComponentCategory.DESCRIPTION);
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(referencedComponentId, referenceSetId);
		assertComponentNotCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
	}
	
	@Test
	public void cannotCreateSimpleMemberWithoutReferencedComponentId() throws Exception {
		givenBranchWithPath(testBranchPath);
		// create refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		// try to create member
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(null, createdRefSetId);
		assertComponentNotCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
	}
	
	@Test
	public void cannotCreateSimpleMemberForDescriptionInConceptBasedRefSet() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// try to create member
		final ISnomedIdentifierService identifierService = ApplicationContext.getInstance().getServiceChecked(ISnomedIdentifierService.class);
		final String referencedComponentId = identifierService.reserve(null, ComponentCategory.DESCRIPTION);
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(referencedComponentId, createdRefSetId);
		assertComponentNotCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq).and().body("message",
				CoreMatchers.equalTo(String.format("'%s' reference set can't reference '%s | %s' component. Only '%s' components are allowed.",
						createdRefSetId, referencedComponentId, SnomedTerminologyComponentConstants.DESCRIPTION, SnomedTerminologyComponentConstants.CONCEPT)));
	}
	
	@Test
	public void createSimpleReferenceSetMemberForConcept() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		// create concept
		final Map<?, ?> conceptReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String createdConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptReq);
		
		// create refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create member
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(createdConceptId, createdRefSetId);
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		assertComponentExists(testBranchPath, SnomedComponentType.MEMBER, memberId);
	}
	
	@Test
	public void cannotCreateQueryTypeReferenceSetMemberWithReferencedComponentId() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create query type refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.QUERY, SnomedTerminologyComponentConstants.REFSET, Concepts.REFSET_QUERY_SPECIFICATION_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create query type reference set member specify the ID
		final ISnomedIdentifierService identifierService = ApplicationContext.getInstance().getServiceChecked(ISnomedIdentifierService.class);
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(identifierService.reserve(null, ComponentCategory.CONCEPT), createdRefSetId);
		assertComponentNotCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq).and().body("message",
				CoreMatchers.equalTo(
						String.format("'%s' type reference set members can't reference components manually, specify a '%s' property instead.",
								SnomedRefSetType.QUERY, SnomedRf2Headers.FIELD_QUERY)));
	}
	
	@Test
	public void cannotCreateQueryTypeReferenceSetMemberWithoutQuery() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create query type refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.QUERY, SnomedTerminologyComponentConstants.REFSET, Concepts.REFSET_QUERY_SPECIFICATION_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create query type reference set member without query
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(null, createdRefSetId);
		assertComponentNotCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq).and().body("message",
				CoreMatchers.equalTo(
						String.format("'%s' cannot be null or empty for '%s' type reference sets.",
								SnomedRf2Headers.FIELD_QUERY, SnomedRefSetType.QUERY)));
	}
	
	@Test
	public void createQueryTypeReferenceSetMember() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create query type refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.QUERY, SnomedTerminologyComponentConstants.REFSET, Concepts.REFSET_QUERY_SPECIFICATION_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create query type reference set member with query (ALL characteristic types)
		final String query = "<"+Concepts.CHARACTERISTIC_TYPE;
		final ImmutableMap<String, Object> queryProps = ImmutableMap.<String, Object>of(SnomedRf2Headers.FIELD_QUERY, query, "refSetDescription", "QTM-AllCharTypes");
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(Concepts.MODULE_SCT_CORE, null, createdRefSetId, queryProps);
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		Response response = getComponent(testBranchPath, SnomedComponentType.MEMBER, memberId);
		response.then().assertThat()
			.statusCode(200)
			.and()
			.body("query", CoreMatchers.equalTo(query))
			.and()
			.body("referencedComponent.id", CoreMatchers.notNullValue());
			
		final String referencedComponentId = response.body().path("referencedComponent.id");
		getComponent(testBranchPath, SnomedComponentType.REFSET, referencedComponentId, "members()")
			.then()
			.log().ifValidationFails()
			.assertThat()
			.statusCode(200)
			.and()
			.body("members.items.referencedComponent.id", CoreMatchers.hasItems(Concepts.STATED_RELATIONSHIP, Concepts.INFERRED_RELATIONSHIP, Concepts.DEFINING_RELATIONSHIP));
	}
	
	@Test
	public void executeInvalidReferenceSetMemberAction() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create query type refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.QUERY, SnomedTerminologyComponentConstants.REFSET, Concepts.REFSET_QUERY_SPECIFICATION_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create query type reference set member with query (ALL characteristic types)
		final String query = "<"+Concepts.CHARACTERISTIC_TYPE;
		final ImmutableMap<String, Object> queryProps = ImmutableMap.<String, Object>of(SnomedRf2Headers.FIELD_QUERY, query, "refSetDescription", "QTM-AllCharTypes");
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(Concepts.MODULE_SCT_CORE, null, createdRefSetId, queryProps);
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		// try to execute unknown action on member
		final Map<?, ?> invalidActionReq = ImmutableMap.of("action", "invalid");
		executeMemberAction(memberId, invalidActionReq)
			.then()
			.log().ifValidationFails()
			.statusCode(400)
			.and()
			.body("message", CoreMatchers.equalTo("Invalid action type 'invalid'."));
	}

	private Response executeMemberAction(final String memberId, final Map<?, ?> invalidActionReq) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.body(invalidActionReq)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.post("/{path}/members/{id}/actions", testBranchPath.getPath(), memberId);
	}
	
	@Test
	public void evaluateAndUpdateQueryTypeReferenceSetMember() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create query type refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.QUERY, SnomedTerminologyComponentConstants.REFSET, Concepts.REFSET_QUERY_SPECIFICATION_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create query type reference set member with query (ALL characteristic types)
		final String query = "<"+Concepts.CHARACTERISTIC_TYPE;
		final ImmutableMap<String, Object> queryProps = ImmutableMap.<String, Object>of(SnomedRf2Headers.FIELD_QUERY, query, "refSetDescription", "QTM-AllCharTypes");
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(Concepts.MODULE_SCT_CORE, null, createdRefSetId, queryProps);
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		
		// create new char type concept
		final Map<?, ?> newCharTypeConcept = givenConceptRequestBody(null, Concepts.CHARACTERISTIC_TYPE, Concepts.MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String newCharTypeConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, newCharTypeConcept);
		// create inferred relationship between new concept and char type, so ESCG can pick it up properly
		final Map<?, ?> baseBody = givenRelationshipRequestBody(newCharTypeConceptId, Concepts.IS_A, Concepts.CHARACTERISTIC_TYPE, Concepts.MODULE_SCT_CORE, "New inferred ISA");
		final Map<?, ?> inferredIsaBody = ImmutableMap.builder().putAll(baseBody).put("characteristicType", CharacteristicType.INFERRED_RELATIONSHIP).build();
		assertComponentCreated(testBranchPath, SnomedComponentType.RELATIONSHIP, inferredIsaBody);
		
		// execute member update
		final Map<?, ?> updateActionReq = ImmutableMap.of("action", "sync", "moduleId", Concepts.MODULE_SCT_CORE, "commitComment", "Update query member: " + memberId);
		executeMemberAction(memberId, updateActionReq)
			.then()
			.statusCode(200);
		// verify that the query type refset has 4 members now
		final String referencedComponentId = getComponent(testBranchPath, SnomedComponentType.MEMBER, memberId).body().path("referencedComponent.id");
		getComponent(testBranchPath, SnomedComponentType.REFSET, referencedComponentId, "members()")
			.then()
			.log().ifValidationFails()
			.assertThat()
			.statusCode(200)
			.and()
			.body("members.items.referencedComponent.id", CoreMatchers.hasItems(Concepts.STATED_RELATIONSHIP, Concepts.INFERRED_RELATIONSHIP, Concepts.DEFINING_RELATIONSHIP, newCharTypeConceptId));
	}
	
	@Test
	public void evaluateAndUpdateQueryTypeRefSetMemberWithComplexESCGQuery() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create query type refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.QUERY, SnomedTerminologyComponentConstants.REFSET, Concepts.REFSET_QUERY_SPECIFICATION_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create a member with complex query
		final String query = String.format("<%s:%s=%s", "404684003", "116676008", "50960005");
		final ImmutableMap<String, Object> queryProps = ImmutableMap.<String, Object>of(SnomedRf2Headers.FIELD_QUERY, query, "refSetDescription", "QTM-BleedingOnlyInMiniCT");
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(Concepts.MODULE_SCT_CORE, null, createdRefSetId, queryProps);
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		
		// execute member update
		final Map<?, ?> updateActionReq = ImmutableMap.of("action", "sync", "moduleId", Concepts.MODULE_SCT_CORE, "commitComment", "Update query member: " + memberId);
		executeMemberAction(memberId, updateActionReq)
			.then()
			.statusCode(200);
		// verify that the query type refset has 4 members now
		final String referencedComponentId = getComponent(testBranchPath, SnomedComponentType.MEMBER, memberId).body().path("referencedComponent.id");
		getComponent(testBranchPath, SnomedComponentType.REFSET, referencedComponentId, "members()")
			.then()
			.log().ifValidationFails()
			.assertThat()
			.statusCode(200)
			.and()
			.body("members.items.referencedComponent.id", CoreMatchers.hasItems("131148009"));
	}
	
	@Test
	public void deleteSimpleReferenceSetMember() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		// create concept
		final Map<?, ?> conceptReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String createdConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptReq);
		
		// create refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create member
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(createdConceptId, createdRefSetId);
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		assertComponentExists(testBranchPath, SnomedComponentType.MEMBER, memberId);
		
		assertComponentCanBeDeleted(testBranchPath, SnomedComponentType.MEMBER, memberId);
		assertComponentNotExists(testBranchPath, SnomedComponentType.MEMBER, memberId);
	}
	
	@Test
	public void inactivateSimpleReferenceSetMember() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		// create concept
		final Map<?, ?> conceptReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String createdConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptReq);
		
		// create refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create member
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(createdConceptId, createdRefSetId);
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		assertComponentExists(testBranchPath, SnomedComponentType.MEMBER, memberId);
		
		// inactivate member by sending update with active flag set to false
		final Map<?, ?> inactivationReq = ImmutableMap.of("active", false, "moduleId", Concepts.MODULE_ROOT, "commitComment", "Inactivate member and move to root module: " + memberId);
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.with().contentType(ContentType.JSON)
			.and().body(inactivationReq)
			.when().put("/{path}/{componentType}/{id}", testBranchPath.getPath(), SnomedComponentType.MEMBER.toLowerCasePlural(), memberId);
		
		// verify that member has been inactivated successfully
		getComponent(testBranchPath, SnomedComponentType.MEMBER, memberId)
			.then()
			.body("active", CoreMatchers.equalTo(false))
			.and()
			.body("moduleId", CoreMatchers.equalTo(Concepts.MODULE_ROOT));
	}
	
}
