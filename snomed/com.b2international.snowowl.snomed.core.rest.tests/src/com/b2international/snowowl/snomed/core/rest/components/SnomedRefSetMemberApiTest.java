/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.rest.components;

import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRefSetRestRequests.executeMemberAction;
import static com.b2international.snowowl.snomed.core.rest.SnomedRefSetRestRequests.updateRefSetComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.*;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 4.5
 */
public class SnomedRefSetMemberApiTest extends AbstractSnomedApiTest {

	@Test
	public void getMemberNonExistingBranch() throws Exception {
		// UUID is the language reference set member for the SNOMED CT root concept's FSN
		getComponent(BranchPathUtils.createPath("MAIN/x/y/z"), SnomedComponentType.MEMBER, "e606c375-501d-5db6-821f-f03d8a12ad1c").statusCode(404);
	}

	@Test
	public void getMemberNonExistingIdentifier() throws Exception {
		getComponent(branchPath, SnomedComponentType.MEMBER, "00001111-0000-0000-0000-000000000000").statusCode(404);
	}

	@Test
	public void createConcreteDomainMemberInvalidValue() {
		createConcreteDomainParentConcept(branchPath);

		String refSetId = createConcreteDomainRefSet(branchPath, DataType.INTEGER);
		Json requestBody = createRefSetMemberRequestBody(refSetId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_TYPE_ID, Concepts.REFSET_ATTRIBUTE,
					SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, 0,
					SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.STATED_RELATIONSHIP,
					SnomedRf2Headers.FIELD_VALUE, "five", // bad
					"commitComment", "Created new reference set member"
				));

		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(400);
	}

	@Test
	public void createConcreteDomainMember() {
		createConcreteDomainParentConcept(branchPath);

		String refSetId = createConcreteDomainRefSet(branchPath, DataType.DECIMAL);
		Json requestBody = createRefSetMemberRequestBody(refSetId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_TYPE_ID, Concepts.REFSET_ATTRIBUTE, // Using "Reference set attribute" root as a data attribute
					SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, 0,
					SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.STATED_RELATIONSHIP,
					SnomedRf2Headers.FIELD_VALUE, "3.1415927", // bad
					"commitComment", "Created new reference set member"
				));

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.REFSET_ATTRIBUTE))
			.body(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, equalTo(0))
			.body(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, equalTo(Concepts.STATED_RELATIONSHIP))
			.body(SnomedRf2Headers.FIELD_VALUE, equalTo("3.1415927"));
	}

	@Test
	public void updateConcreteDomainMember() {
		createConcreteDomainParentConcept(branchPath);

		String refSetId = createConcreteDomainRefSet(branchPath, DataType.DECIMAL);
		Json createRequest = createRefSetMemberRequestBody(refSetId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_TYPE_ID, Concepts.REFSET_ATTRIBUTE,
					SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, 1,
					SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.STATED_RELATIONSHIP,
					SnomedRf2Headers.FIELD_VALUE, "3.1415927",
					"commitComment", "Created new concrete domain reference set member"
				));

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, createRequest));

		@SuppressWarnings("unchecked")
		Map<String, Object> member = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
		.statusCode(200)
		.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.REFSET_ATTRIBUTE))
		.body(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, equalTo(1))
		.body(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, equalTo(Concepts.STATED_RELATIONSHIP))
		.body(SnomedRf2Headers.FIELD_VALUE, equalTo("3.1415927"))
		.extract().as(Map.class);

		member.put(SnomedRf2Headers.FIELD_TYPE_ID, Concepts.CONCEPT_MODEL_ATTRIBUTE);
		member.put(SnomedRf2Headers.FIELD_VALUE, "2.7182818");
		member.put("commitComment", "Updated existing concrete domain reference set member");

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, member, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
		.statusCode(200)
		.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.CONCEPT_MODEL_ATTRIBUTE))
		.body(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, equalTo(1))
		.body(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, equalTo(Concepts.STATED_RELATIONSHIP))
		.body(SnomedRf2Headers.FIELD_VALUE, equalTo("2.7182818"));
	}

	@Test
	public void createMRCMDomainMemberWithMandatoryAndOptionalFields() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_DOMAIN));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_DOMAIN, newIdentifierConceptId);

		Json requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, "domainConstraint",
					SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, "parentDomain",
					SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "proximalPrimitiveConstraint",
					SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, "proximalPrimitiveRefinement",
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "domainTemplateForPrecoordination",
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, "domainTemplateForPostcoordination",
					SnomedRf2Headers.FIELD_MRCM_GUIDEURL, "guideURL",
					"commitComment", "Created new reference set member"
				));

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, equalTo("domainConstraint"))
			.body(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, equalTo("parentDomain"))
			.body(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, equalTo("proximalPrimitiveConstraint"))
			.body(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, equalTo("proximalPrimitiveRefinement"))
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, equalTo("domainTemplateForPrecoordination"))
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, equalTo("domainTemplateForPostcoordination"))
			.body(SnomedRf2Headers.FIELD_MRCM_GUIDEURL, equalTo("guideURL"));
	}
	
	@Test
	public void createMRCMDomainMemberWithOnlyMandatoryFields() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_DOMAIN));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_DOMAIN, newIdentifierConceptId);

		Json requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, "domainConstraint",
					SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, "parentDomain",
					SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, "proximalPrimitiveRefinement",
					SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "proximalPrimitiveConstraint",
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "domainTemplateForPrecoordination",
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, "domainTemplateForPostcoordination",
					"commitComment", "Created new reference set member"
				));

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, equalTo("domainConstraint"))
			.body(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, equalTo("parentDomain"))
			.body(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, equalTo("proximalPrimitiveConstraint"))
			.body(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, equalTo("proximalPrimitiveRefinement"))
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, equalTo("domainTemplateForPrecoordination"))
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, equalTo("domainTemplateForPostcoordination"))
			.body(SnomedRf2Headers.FIELD_MRCM_GUIDEURL, nullValue());
	}
	
	@Test
	public void createMRCMAttributeDomainMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN, newIdentifierConceptId);

		Json requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE,
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT,
					"commitComment", "Created new reference set member"
				));

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, equalTo(Concepts.ROOT_CONCEPT))
			.body(SnomedRf2Headers.FIELD_MRCM_GROUPED, equalTo(Boolean.TRUE))
			.body(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, equalTo("attributeCardinality"))
			.body(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, equalTo("attributeInGroupCardinality"))
			.body(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, equalTo(Concepts.ROOT_CONCEPT))
			.body(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, equalTo(Concepts.ROOT_CONCEPT));
		
		Json requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, "159725002", // XXX batman should not be in the test dataset
					SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE,
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT,
					"commitComment", "Created new reference set member"
				));
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2).statusCode(400);
		
		Json requestBody3 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE,
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, "159725002", // XXX batman should not be in the test dataset
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT,
					"commitComment", "Created new reference set member"
				));
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody3).statusCode(400);
		
		Json requestBody4 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
			.with(Json.object(
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT,
				SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE,
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality",
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality",
				SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT,
				SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, "159725002", // XXX batman should not be in the test dataset
				"commitComment", "Created new reference set member"
			));
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody4).statusCode(400);
	}
	
	@Test
	public void createMRMCAttributeRangeMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_ATTRIBUTE_RANGE));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_ATTRIBUTE_RANGE, newIdentifierConceptId);

		Json requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, "rangeConstraint",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, "attributeRule",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT,
					"commitComment", "Created new reference set member"
				));

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, equalTo("rangeConstraint"))
			.body(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, equalTo("attributeRule"))
			.body(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, equalTo(Concepts.ROOT_CONCEPT))
			.body(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, equalTo(Concepts.ROOT_CONCEPT));
		
		Json requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, "159725002", // XXX batman should not be in the test dataset
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT,
					"commitComment", "Created new reference set member"		
				));
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2).statusCode(400);
		
		Json requestBody3 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, "159725002", // XXX batman should not be in the test dataset
					"commitComment", "Created new reference set member"
				));
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody3).statusCode(400);
	}
	
	@Test
	public void createMRCMModuleScopeMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_MODULE_SCOPE));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_MODULE_SCOPE, newIdentifierConceptId);

		Json requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, Concepts.ROOT_CONCEPT)
				.with("commitComment", "Created new reference set member");

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, equalTo(Concepts.ROOT_CONCEPT));
		
		Json requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, "159725002") // XXX batman should not be in the test dataset
				.with("commitComment", "Created new reference set member");
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2).statusCode(400);
	}
	
	@Test
	public void searchOWLAxiomRefsetMemberBySourceTypeDestination() {
		
		String conceptId = createNewConcept(branchPath);
		
		Json requestBody = createRefSetMemberRequestBody(Concepts.REFSET_OWL_AXIOM, conceptId)
				.with(SnomedRf2Headers.FIELD_OWL_EXPRESSION, "SubClassOf(:" + conceptId + " :" + Concepts.NAMESPACE_ROOT + ")")
				.with("commitComment", "Created new OWL Axiom reference set member");

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		SnomedReferenceSetMembers results = givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.queryParam("referencedComponentId", conceptId)
			.queryParam("owlExpression.typeId", Concepts.IS_A)
			.queryParam("owlExpression.destinationId", Concepts.NAMESPACE_ROOT)
			.get("/{path}/members", branchPath.getPath())
			.then()
			.extract().as(SnomedReferenceSetMembers.class);
		
		assertEquals(1, results.getItems().size());
		
		SnomedReferenceSetMember member = results.getItems().stream().findFirst().get();
		
		assertEquals(memberId, member.getId());
		
	}
	
	@Test
	public void deleteReferringOwlAxiomRefsetMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.OWL_AXIOM));
		createNewRefSet(branchPath, SnomedRefSetType.OWL_AXIOM, newIdentifierConceptId);
		
		String conceptId = createNewConcept(branchPath);
		
		Json requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.with(SnomedRf2Headers.FIELD_OWL_EXPRESSION, SnomedApiTestConstants.owlAxiom1(conceptId))
				.with("commitComment", "Created new OWL Axiom reference set member");

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);
		
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(404);
		
	}
	
	@Ignore
	@Test
	public void createOwlAxiomWithIncorrectFocusConceptIdInExpression() throws Exception {
		// ROOT != Abbreviation
		Json createRequestBody = createRefSetMemberRequestBody(Concepts.REFSET_OWL_AXIOM, Concepts.ROOT_CONCEPT)
				.with(SnomedRf2Headers.FIELD_OWL_EXPRESSION, SnomedApiTestConstants.owlAxiom3(Concepts.ABBREVIATION))
				.with("commitComment", "Created new OWL Axiom reference set member");
		createComponent(branchPath, SnomedComponentType.MEMBER, createRequestBody)
			.statusCode(400);
	}
	
	@Test 
	public void updateOwlAxiomRefsetMembers() {
		String conceptId = createNewConcept(branchPath);
		
		Json createRequestBody = createRefSetMemberRequestBody(Concepts.REFSET_OWL_AXIOM, conceptId)
				.with(SnomedRf2Headers.FIELD_OWL_EXPRESSION, SnomedApiTestConstants.owlAxiom3(conceptId))
				.with("commitComment", "Created new OWL Axiom reference set member");

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, createRequestBody));

		final SnomedReferenceSetMember member = SnomedRequests.prepareGetMember(memberId)
				.setExpand("owlRelationships(expand(type(),destination()))")
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		assertThat(member.getClassOWLRelationships())
			.containsOnly(
				SnomedOWLRelationship.create(Concepts.IS_A, "410680006", 0),
				SnomedOWLRelationship.create("734136001", "900000000000470007", 1)
			);
		
		// successful expansion check
		assertThat(member.getClassOWLRelationships())
			.extracting(SnomedOWLRelationship::getType)
			.extracting(SnomedConcept::isActive)
			.containsOnly(true);
		
		assertThat(member.getClassOWLRelationships())
			.extracting(SnomedOWLRelationship::getDestination)
			.extracting(SnomedConcept::isActive)
			.containsOnly(true);
		
		final Json updateRequestBody = Json.object(
			SnomedRf2Headers.FIELD_OWL_EXPRESSION, SnomedApiTestConstants.owlAxiom4(conceptId),
			"commitComment", "Update OWL Axiom reference set member"
		);
		
		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequestBody, true).statusCode(204);

		final SnomedReferenceSetMember updatedMember = SnomedRequests.prepareGetMember(memberId)
				.setExpand("owlRelationships()")
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		assertThat(updatedMember.getClassOWLRelationships())
			.containsOnly(
				SnomedOWLRelationship.create(Concepts.IS_A, "410680006", 0),
				SnomedOWLRelationship.create("734136001", "900000000000470007", 1),
				SnomedOWLRelationship.create("371881003", "900000000000450001", 1)
			);
	}
	
	@Test
	public void deleteReferringOwlOntologyRefsetMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.OWL_ONTOLOGY));
		createNewRefSet(branchPath, SnomedRefSetType.OWL_ONTOLOGY, newIdentifierConceptId);
		
		String conceptId = createNewConcept(branchPath);
		
		Json requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.with(SnomedRf2Headers.FIELD_OWL_EXPRESSION, SnomedApiTestConstants.owlAxiom1(conceptId))
				.with("commitComment", "Created new OWL Ontology reference set member");

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);
		
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(404);
		
	}
	
	@Test
	public void deleteReferringMRCMDomainRefsetMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_DOMAIN));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_DOMAIN, newIdentifierConceptId);
		
		String conceptId = createNewConcept(branchPath);
		
		Json requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, "domainConstraint",
					SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, "parentDomain",
					SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, "proximalPrimitiveRefinement",
					SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "proximalPrimitiveConstraint",
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "domainTemplateForPrecoordination",
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, "domainTemplateForPostcoordination",
					"commitComment", "Created new MRCM domain reference set member"
				));

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);
		
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(404);
		
	}
	
	@Test
	public void deleteReferringMRCMAttributeDomainRefsetMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN, newIdentifierConceptId);
		
		String conceptId = createNewConcept(branchPath);
		
		// create member where the referenced component is the concept
		Json requestBody1 = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE,
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT,
					"commitComment", "Created new MRCM attribute domain reference set member"
				));

		String memberId1 = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody1));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1).statusCode(200);

		// create member where the concept is referenced in custom field
		Json requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, conceptId,
					SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE,
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT,
					"commitComment", "Created new MRCM attribute domain reference set member"
				));
		
		String memberId2 = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2));
		
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2).statusCode(200);
		
		// create member where the concept is referenced in custom field
		Json requestBody3 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE,
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, conceptId,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT,
					"commitComment", "Created new MRCM attribute domain reference set member"
				));

		String memberId3 = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody3));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId3).statusCode(200);
		
		// create member where the concept is referenced in custom field
		Json requestBody4 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE,
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, conceptId,
					"commitComment", "Created new MRCM attribute domain reference set member"
				));

		String memberId4 = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody4));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId4).statusCode(200);
		
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId3).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId4).statusCode(404);
		
	}
	
	@Test
	public void deleteReferringMRCMAttributeRangeRefsetMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_ATTRIBUTE_RANGE));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_ATTRIBUTE_RANGE, newIdentifierConceptId);
		
		String conceptId = createNewConcept(branchPath);
		
		// create member where the referenced component is the concept
		Json requestBody1 = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, "rangeConstraint",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, "attributeRule",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT,
					"commitComment", "Created new MRCM attribute range reference set member"
				));

		String memberId1 = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody1));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1).statusCode(200);

		// create member where the concept is referenced in custom field
		Json requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, "rangeConstraint",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, "attributeRule",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, conceptId,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT,
					"commitComment", "Created new MRCM attribute range reference set member"
				));

		String memberId2 = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2).statusCode(200);
		
		// create member where the concept is referenced in custom field
		Json requestBody3 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(Json.object(
					SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, "rangeConstraint",
					SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, "attributeRule",
					SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT,
					SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, conceptId,
					"commitComment", "Created new MRCM attribute range reference set member"						
				));

		String memberId3 = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody3));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId3).statusCode(200);
		
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId3).statusCode(404);
		
	}
	
	@Test
	public void deleteReferringMRCMModuleScopeRefsetMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_MODULE_SCOPE));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_MODULE_SCOPE, newIdentifierConceptId);
		
		String conceptId = createNewConcept(branchPath);
		
		// create member where the referenced component is the concept
		Json requestBody1 = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.with(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, Concepts.ROOT_CONCEPT)
				.with("commitComment", "Created new MRCM module scope reference set member");

		String memberId1 = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody1));
		
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1).statusCode(200);

		// create member where the concept is referenced in custom field
		Json requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.with(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, conceptId)
				.with("commitComment", "Created new MRCM module scope reference set member");

		String memberId2 = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2));
		
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2).statusCode(200);
		
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2).statusCode(404);
		
	}
	
	@Test
	public void createAndUpdateModuleDependencyMemberWithEffectiveTime() {
		
		Json requestBody = createRefSetMemberRequestBody(Concepts.REFSET_MODULE_DEPENDENCY_TYPE, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.with(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "20181001")
				.with(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "20181001")
				.with("commitComment", "Created new module dependency reference set member");

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		SnomedReferenceSetMember member = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertEquals(EffectiveTimes.parse("20181001", DateFormats.SHORT), member.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertEquals(EffectiveTimes.parse("20181001", DateFormats.SHORT), member.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
		Json updateRequest = Json.object(
			SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "",
			SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "",
			"commitComment", "Updated reference set member"
		);

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);
		
		SnomedReferenceSetMember updatedMember = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertNull(updatedMember.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertNull(updatedMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
	}
	
	@Test
	public void createAndUpdateUnpublishedModuleDependencyMembers() {
		
		Json requestBody = createRefSetMemberRequestBody(Concepts.REFSET_MODULE_DEPENDENCY_TYPE, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.with(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "")
				.with(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "")
				.with("commitComment", "Created new module dependency reference set member");

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		SnomedReferenceSetMember member = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertNull(member.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertNull(member.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
		Json updateRequest = Json.object(
			SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "20181002",
			SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "20181002",
			"commitComment", "Updated reference set member"
		);

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);
		
		SnomedReferenceSetMember updatedMember = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertEquals(EffectiveTimes.parse("20181002", DateFormats.SHORT), updatedMember.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertEquals(EffectiveTimes.parse("20181002", DateFormats.SHORT), updatedMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
	}
	
	@Test
	public void createAndUpdateNewModuleDependencyMembers() {
		
		Json requestBody = createRefSetMemberRequestBody(Concepts.REFSET_MODULE_DEPENDENCY_TYPE, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.with("commitComment", "Created new module dependency reference set member");

		String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));

		SnomedReferenceSetMember member = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertNull(member.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertNull(member.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
		Json updateRequest = Json.object(
			SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "20181002",
			SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "20181002",
			"commitComment", "Updated reference set member"
		);

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);
		
		SnomedReferenceSetMember updatedMember = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertEquals(EffectiveTimes.parse("20181002", DateFormats.SHORT), updatedMember.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertEquals(EffectiveTimes.parse("20181002", DateFormats.SHORT), updatedMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
	}
	
	@Test
	public void executeInvalidAction() throws Exception {
		String queryRefSetId = createNewRefSet(branchPath, SnomedRefSetType.QUERY);
		String simpleRefSetId = createNewRefSet(branchPath);

		final Json memberRequest = Json.object(
			SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE,
			"refsetId", queryRefSetId,
			SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId,
			SnomedRf2Headers.FIELD_QUERY, "<" + Concepts.REFSET_ROOT_CONCEPT,
			"commitComment", "Created new query reference set member"
		);

		final String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);

		final Json invalidActionRequest = Json.object(
			"action", "invalid",
			"commitComment", "Executed invalid action on reference set member"
		);

		executeMemberAction(branchPath, memberId, invalidActionRequest).statusCode(400)
		.body("message", containsString("Invalid"))
		.body("message", containsString("CREATE"))
		.body("message", containsString("UPDATE"))
		.body("message", containsString("DELETE"))
		.body("message", containsString("SYNC"));
	}

	@Test
	public void executeSyncAction() throws Exception {
		String queryRefSetId = createNewRefSet(branchPath, SnomedRefSetType.QUERY);
		String simpleRefSetId = createNewRefSet(branchPath);

		String parentId = createNewConcept(branchPath);
		List<String> conceptIds = newArrayList();
		for (int i = 0; i < 3; i++) {
			String conceptId = createNewConcept(branchPath, parentId);
			conceptIds.add(conceptId);
			// Need to add an inferred IS A counterpart, as query evaluation uses inferred relationships
			createNewRelationship(branchPath, conceptId, Concepts.IS_A, parentId, Concepts.INFERRED_RELATIONSHIP);
		}

		final Json memberRequest = Json.object(
			SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE,
			"refsetId", queryRefSetId,
			SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId,
			SnomedRf2Headers.FIELD_QUERY, "<" + parentId,
			"commitComment", "Created new query reference set member"
		);

		final String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);

		// Since we have used an existing simple type refset, it will have no members initially, so sync first 
		executeSyncAction(memberId);
		checkReferencedComponentIds(conceptIds, simpleRefSetId);

		// Add a new concept that matches the query, then sync again
		String extraConceptId = createNewConcept(branchPath, parentId);
		conceptIds.add(extraConceptId);
		createNewRelationship(branchPath, extraConceptId, Concepts.IS_A, parentId, Concepts.INFERRED_RELATIONSHIP);

		executeSyncAction(memberId);
		checkReferencedComponentIds(conceptIds, simpleRefSetId);
	}
	
	/**
	 * Removals are sent in a BulkRequest which includes individual DeleteRequests for each member to be deleted. The version of SnomedEditingContext prior to the fix, however, used
	 * a server-side query to determine the list index for each member, and the list index reported by the database become misaligned with the actual
	 * contents if the members were not removed in decreasing index order. This test verifies that order of delete requests inside a bulk request does not matter.
	 */
	@Test
	public void issue_SO_2501_ioobe_during_member_deletion() throws Exception {
		String simpleRefSetId = createNewRefSet(branchPath);
		
		final Json memberRequest = Json.object(
			SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE,
			"refsetId", simpleRefSetId,
			SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId,
			"commitComment", "Created new simple reference set member"
		);
		
		final String member1Id = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest));
		final String member2Id = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest));
		final String member3Id = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest));
		
		final BulkRequestBuilder<TransactionContext> bulk = BulkRequest.create();
		
		bulk.add(SnomedRequests.prepareDeleteMember(member1Id));
		bulk.add(SnomedRequests.prepareDeleteMember(member3Id));

		SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Delete reference set members in ascending index order")
			.setAuthor("test")
			.build(branchPath.getPath())
			.execute(Services.bus())
			.getSync();
		
		// Check that member 2 still exists
		getComponent(branchPath, SnomedComponentType.MEMBER, member2Id).statusCode(200);
	}
	
	@Test(expected = AlreadyExistsException.class)
	public void testDuplicateIdCreationShouldFail() {
		final String simpleRefSetId = createNewRefSet(branchPath);
		
		final String memberId = UUID.randomUUID().toString();
		final CommitResult result1 = SnomedRequests.prepareNewMember()
			.setId(memberId)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setRefsetId(simpleRefSetId)
			.setReferencedComponentId(simpleRefSetId)
			.build(branchPath.getPath(), RestExtensions.USER, "Creating refset member")
			.execute(getBus())
			.getSync();
		
		final String member1Id = result1.getResultAs(String.class);
		assertEquals(memberId, member1Id);	
		
		final CommitResult result2 = SnomedRequests.prepareNewMember()
			.setId(memberId)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setRefsetId(simpleRefSetId)
			.setReferencedComponentId(simpleRefSetId)
			.build(branchPath.getPath(), RestExtensions.USER, "Creating refset member")
			.execute(getBus())
			.getSync();	
	}
	
	@Test
	public void testCreateMemberWithSpecificId() {
		final String simpleRefSetId = createNewRefSet(branchPath);
		final String specificId = UUID.randomUUID().toString();
		final Json memberRequest = Json.object(
			SnomedRf2Headers.FIELD_ID, specificId,
			SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE,
			"refsetId", simpleRefSetId,
			SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId,
			"commitComment", "Created new simple reference set member"
		);
		
		final String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest));
		
		assertEquals(specificId, memberId);
	}
	
	@Test
	public void restoreEffectiveTimeOnReleasedMember() throws Exception {
		final String simpleRefSetId = createNewRefSet(branchPath);
		final String memberId = UUID.randomUUID().toString();

		SnomedRequests.prepareNewMember()
			.setId(memberId)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setRefsetId(simpleRefSetId)
			.setReferencedComponentId(simpleRefSetId)
			.build(branchPath.getPath(), RestExtensions.USER, "Create refset member")
			.execute(getBus())
			.getSync();
		
		final String shortName = "SNOMEDCT-REF-1";
		createCodeSystem(branchPath, shortName).statusCode(201);
		final LocalDate effectiveDate = getNextAvailableEffectiveDate(shortName);
		final String effectiveDateAsString = EffectiveTimes.format(effectiveDate, DateFormats.SHORT);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		// After versioning, the reference set member should be released and have an effective time set on it
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveDateAsString));
		
		Json inactivationRequestBody = Json.object(
			"active", false,
			"commitComment", "Inactivate refset member"
		);

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, inactivationRequestBody, false).statusCode(204);

		// An inactivation should unset the effective time field
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body("active", equalTo(false))
			.body("released", equalTo(true))
	 		.body("effectiveTime", nullValue());

		SnomedReferenceSetMember member = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);

		// XXX: Explicitly create a new Boolean object to simulate deserialization bug
		member.setActive(new Boolean(true));
		
		SnomedRequests.prepareCommit()
			.setAuthor(RestExtensions.USER)
			.setCommitComment("Reactivate refset member")
			.setBody(member.toUpdateRequest())
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);

		// Getting the member back to its originally released state should restore the effective time
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveDateAsString));
	}

	private void executeSyncAction(final String memberId) {
		final Json syncActionRequest = Json.object(
			"action", "sync",
			SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE,
			"commitComment", "Executed sync action on reference set member"
		);

		executeMemberAction(branchPath, memberId, syncActionRequest).statusCode(200);
	}

	private void checkReferencedComponentIds(List<String> conceptIds, String simpleRefSetId) {
		List<String> referencedComponentIds = getComponent(branchPath, SnomedComponentType.REFSET, simpleRefSetId, "members()")
				.statusCode(200)
				.extract().path("members.items.referencedComponent.id");

		assertEquals(conceptIds.size(), referencedComponentIds.size());
		assertTrue(referencedComponentIds.containsAll(conceptIds));
	}
}
