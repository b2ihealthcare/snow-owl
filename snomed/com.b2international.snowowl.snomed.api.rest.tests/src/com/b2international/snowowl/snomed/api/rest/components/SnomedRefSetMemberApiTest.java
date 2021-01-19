/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.executeMemberAction;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.updateRefSetComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createConcreteDomainParentConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createConcreteDomainRefSet;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRefSet;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRelationship;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createRefSetMemberRequestBody;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.request.CommitResult;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;

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
		Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_TYPE_ID, Concepts.REFSET_ATTRIBUTE)
				.put(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, 0)
				.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.STATED_RELATIONSHIP)
				.put(SnomedRf2Headers.FIELD_VALUE, "five") // bad
				.put("commitComment", "Created new reference set member")
				.build();

		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(400);
	}

	@Test
	public void createConcreteDomainMember() {
		createConcreteDomainParentConcept(branchPath);

		String refSetId = createConcreteDomainRefSet(branchPath, DataType.DECIMAL);
		Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_TYPE_ID, Concepts.REFSET_ATTRIBUTE) // Using "Reference set attribute" root as a data attribute
				.put(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, 0)
				.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.STATED_RELATIONSHIP)
				.put(SnomedRf2Headers.FIELD_VALUE, "3.1415927")
				.put("commitComment", "Created new reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
		.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.REFSET_ATTRIBUTE))
		.body(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, equalTo(0))
		.body(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, equalTo(Concepts.STATED_RELATIONSHIP))
		.body(SnomedRf2Headers.FIELD_VALUE, equalTo("3.1415927"));
	}

	@Test
	public void updateConcreteDomainMember() {
		createConcreteDomainParentConcept(branchPath);

		String refSetId = createConcreteDomainRefSet(branchPath, DataType.DECIMAL);
		Map<?, ?> createRequest = createRefSetMemberRequestBody(refSetId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_TYPE_ID, Concepts.REFSET_ATTRIBUTE)
				.put(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, 1)
				.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.STATED_RELATIONSHIP)
				.put(SnomedRf2Headers.FIELD_VALUE, "3.1415927")
				.put("commitComment", "Created new concrete domain reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, createRequest)
				.statusCode(201)
				.extract().header("Location"));

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

		Map<?, ?> requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, "domainConstraint")
				.put(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, "parentDomain")
				.put(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "proximalPrimitiveConstraint")
				.put(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, "proximalPrimitiveRefinement")
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "domainTemplateForPrecoordination")
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, "domainTemplateForPostcoordination")
				.put(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE, "editorialGuideReference")
				.put("commitComment", "Created new reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, equalTo("domainConstraint"))
			.body(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, equalTo("parentDomain"))
			.body(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, equalTo("proximalPrimitiveConstraint"))
			.body(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, equalTo("proximalPrimitiveRefinement"))
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, equalTo("domainTemplateForPrecoordination"))
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, equalTo("domainTemplateForPostcoordination"))
			.body(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE, equalTo("editorialGuideReference"));
	}
	
	@Test
	public void createMRCMDomainMemberWithOnlyMandatoryFields() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_DOMAIN));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_DOMAIN, newIdentifierConceptId);

		Map<?, ?> requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, "domainConstraint")
				.put(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "proximalPrimitiveConstraint")
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "domainTemplateForPrecoordination")
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, "domainTemplateForPostcoordination")
				.put("commitComment", "Created new reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, equalTo("domainConstraint"))
			.body(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, nullValue())
			.body(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, equalTo("proximalPrimitiveConstraint"))
			.body(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, nullValue())
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, equalTo("domainTemplateForPrecoordination"))
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, equalTo("domainTemplateForPostcoordination"))
			.body(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE, nullValue());
	}
	
	@Test
	public void createMRCMAttributeDomainMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN, newIdentifierConceptId);

		Map<?, ?> requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE)
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, equalTo(Concepts.ROOT_CONCEPT))
			.body(SnomedRf2Headers.FIELD_MRCM_GROUPED, equalTo(Boolean.TRUE))
			.body(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, equalTo("attributeCardinality"))
			.body(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, equalTo("attributeInGroupCardinality"))
			.body(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, equalTo(Concepts.ROOT_CONCEPT))
			.body(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, equalTo(Concepts.ROOT_CONCEPT));
		
		Map<?, ?> requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, "159725002") // XXX batman should not be in the test dataset
				.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE)
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new reference set member")
				.build();
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2).statusCode(400);
		
		Map<?, ?> requestBody3 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE)
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, "159725002") // XXX batman should not be in the test dataset
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new reference set member")
				.build();
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody3).statusCode(400);
		
		Map<?, ?> requestBody4 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE)
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, "159725002") // XXX batman should not be in the test dataset
				.put("commitComment", "Created new reference set member")
				.build();
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody4).statusCode(400);
	}
	
	@Test
	public void createMRMCAttributeRangeMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_ATTRIBUTE_RANGE));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_ATTRIBUTE_RANGE, newIdentifierConceptId);

		Map<?, ?> requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, "rangeConstraint")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, "attributeRule")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, equalTo("rangeConstraint"))
			.body(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, equalTo("attributeRule"))
			.body(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, equalTo(Concepts.ROOT_CONCEPT))
			.body(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, equalTo(Concepts.ROOT_CONCEPT));
		
		Map<?, ?> requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, "159725002") // XXX batman should not be in the test dataset
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new reference set member")
				.build();
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2).statusCode(400);
		
		Map<?, ?> requestBody3 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, "159725002") // XXX batman should not be in the test dataset
				.put("commitComment", "Created new reference set member")
				.build();
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody3).statusCode(400);
	}
	
	@Test
	public void createMRCMModuleScopeMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.MRCM_MODULE_SCOPE));
		createNewRefSet(branchPath, SnomedRefSetType.MRCM_MODULE_SCOPE, newIdentifierConceptId);

		Map<?, ?> requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, equalTo(Concepts.ROOT_CONCEPT));
		
		Map<?, ?> requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, "159725002") // XXX batman should not be in the test dataset
				.put("commitComment", "Created new reference set member")
				.build();
		
		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2).statusCode(400);
	}
	
	@Test
	public void searchOWLAxiomRefsetMemberBySourceTypeDestination() {
		
		String conceptId = createNewConcept(branchPath);
		
		Map<?, ?> requestBody = createRefSetMemberRequestBody(Concepts.REFSET_OWL_AXIOM, conceptId)
				.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, "SubClassOf(:" + conceptId + " :" + Concepts.NAMESPACE_ROOT + ")")
				.put("commitComment", "Created new OWL Axiom reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

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
		
		Map<?, ?> requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, SnomedApiTestConstants.OWL_AXIOM_1)
				.put("commitComment", "Created new OWL Axiom reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);
		
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(404);
		
	}
	
	@Test
	public void deleteReferringOwlOntologyRefsetMember() {
		
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.OWL_ONTOLOGY));
		createNewRefSet(branchPath, SnomedRefSetType.OWL_ONTOLOGY, newIdentifierConceptId);
		
		String conceptId = createNewConcept(branchPath);
		
		Map<?, ?> requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, SnomedApiTestConstants.OWL_AXIOM_1)
				.put("commitComment", "Created new OWL Ontology reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

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
		
		Map<?, ?> requestBody = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, "domainConstraint")
				.put(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "proximalPrimitiveConstraint")
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "domainTemplateForPrecoordination")
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, "domainTemplateForPostcoordination")
				.put("commitComment", "Created new MRCM domain reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

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
		Map<?, ?> requestBody1 = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE)
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new MRCM attribute domain reference set member")
				.build();

		String memberId1 = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody1)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1).statusCode(200);

		// create member where the concept is referenced in custom field
		Map<?, ?> requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, conceptId)
				.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE)
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new MRCM attribute domain reference set member")
				.build();
		
		String memberId2 = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2)
				.statusCode(201)
				.extract().header("Location"));
		
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2).statusCode(200);
		
		// create member where the concept is referenced in custom field
		Map<?, ?> requestBody3 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE)
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, conceptId)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new MRCM attribute domain reference set member")
				.build();

		String memberId3 = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody3)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId3).statusCode(200);
		
		// create member where the concept is referenced in custom field
		Map<?, ?> requestBody4 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE)
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, conceptId)
				.put("commitComment", "Created new MRCM attribute domain reference set member")
				.build();

		String memberId4 = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody4)
				.statusCode(201)
				.extract().header("Location"));

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
		Map<?, ?> requestBody1 = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.put(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, "rangeConstraint")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, "attributeRule")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new MRCM attribute range reference set member")
				.build();

		String memberId1 = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody1)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1).statusCode(200);

		// create member where the concept is referenced in custom field
		Map<?, ?> requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, "rangeConstraint")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, "attributeRule")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, conceptId)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new MRCM attribute range reference set member")
				.build();

		String memberId2 = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2).statusCode(200);
		
		// create member where the concept is referenced in custom field
		Map<?, ?> requestBody3 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, "rangeConstraint")
				.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, "attributeRule")
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, conceptId)
				.put("commitComment", "Created new MRCM attribute range reference set member")
				.build();

		String memberId3 = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody3)
				.statusCode(201)
				.extract().header("Location"));

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
		Map<?, ?> requestBody1 = createRefSetMemberRequestBody(newIdentifierConceptId, conceptId)
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new MRCM module scope reference set member")
				.build();

		String memberId1 = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody1)
				.statusCode(201)
				.extract().header("Location"));
		
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1).statusCode(200);

		// create member where the concept is referenced in custom field
		Map<?, ?> requestBody2 = createRefSetMemberRequestBody(newIdentifierConceptId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, conceptId)
				.put("commitComment", "Created new MRCM module scope reference set member")
				.build();

		String memberId2 = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody2)
				.statusCode(201)
				.extract().header("Location"));
		
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2).statusCode(200);
		
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2).statusCode(404);
		
	}
	
	@Test
	public void createAndUpdateModuleDependencyMemberWithEffectiveTime() {
		
		Map<?, ?> requestBody = createRefSetMemberRequestBody(Concepts.REFSET_MODULE_DEPENDENCY_TYPE, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.put(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "20181001")
				.put(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "20181001")
				.put("commitComment", "Created new module dependency reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		SnomedReferenceSetMember member = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertEquals(Dates.parse("20181001", DateFormats.SHORT), member.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertEquals(Dates.parse("20181001", DateFormats.SHORT), member.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "")
				.put(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "")
				.put("commitComment", "Updated reference set member")
				.build();

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);
		
		SnomedReferenceSetMember updatedMember = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertNull(updatedMember.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertNull(updatedMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
	}
	
	@Test
	public void createAndUpdateUnpublishedModuleDependencyMembers() {
		
		Map<?, ?> requestBody = createRefSetMemberRequestBody(Concepts.REFSET_MODULE_DEPENDENCY_TYPE, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.put(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "")
				.put(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "")
				.put("commitComment", "Created new module dependency reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		SnomedReferenceSetMember member = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertNull(member.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertNull(member.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "20181002")
				.put(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "20181002")
				.put("commitComment", "Updated reference set member")
				.build();

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);
		
		SnomedReferenceSetMember updatedMember = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertEquals(Dates.parse("20181002", DateFormats.SHORT), updatedMember.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertEquals(Dates.parse("20181002", DateFormats.SHORT), updatedMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
	}
	
	@Test
	public void createAndUpdateNewModuleDependencyMembers() {
		
		Map<?, ?> requestBody = createRefSetMemberRequestBody(Concepts.REFSET_MODULE_DEPENDENCY_TYPE, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.put("commitComment", "Created new module dependency reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		SnomedReferenceSetMember member = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertNull(member.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertNull(member.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "20181002")
				.put(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "20181002")
				.put("commitComment", "Updated reference set member")
				.build();

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);
		
		SnomedReferenceSetMember updatedMember = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.extract().as(SnomedReferenceSetMember.class);
		
		assertEquals(Dates.parse("20181002", DateFormats.SHORT), updatedMember.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME));
		assertEquals(Dates.parse("20181002", DateFormats.SHORT), updatedMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME));
		
	}
	
	@Test
	public void executeInvalidAction() throws Exception {
		String queryRefSetId = createNewRefSet(branchPath, SnomedRefSetType.QUERY);
		String simpleRefSetId = createNewRefSet(branchPath);

		final Map<?, ?> memberRequest = ImmutableMap.<String, Object>builder()
				.put(SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", queryRefSetId)
				.put(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId)
				.put(SnomedRf2Headers.FIELD_QUERY, "<" + Concepts.REFSET_ROOT_CONCEPT)
				.put("commitComment", "Created new query reference set member")
				.build();

		final String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);

		final Map<?, ?> invalidActionRequest = ImmutableMap.<String, Object>builder()
				.put("action", "invalid")
				.put("commitComment", "Executed invalid action on reference set member")
				.build();

		executeMemberAction(branchPath, memberId, invalidActionRequest).statusCode(400)
		.body("message", CoreMatchers.equalTo("Invalid action type 'invalid'."));
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
			createNewRelationship(branchPath, conceptId, Concepts.IS_A, parentId, CharacteristicType.INFERRED_RELATIONSHIP);
		}

		final Map<?, ?> memberRequest = ImmutableMap.<String, Object>builder()
				.put(SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", queryRefSetId)
				.put(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId)
				.put(SnomedRf2Headers.FIELD_QUERY, "<" + parentId)
				.put("commitComment", "Created new query reference set member")
				.build();

		final String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);

		// Since we have used an existing simple type refset, it will have no members initially, so sync first 
		executeSyncAction(memberId);
		checkReferencedComponentIds(conceptIds, simpleRefSetId);

		// Add a new concept that matches the query, then sync again
		String extraConceptId = createNewConcept(branchPath, parentId);
		conceptIds.add(extraConceptId);
		createNewRelationship(branchPath, extraConceptId, Concepts.IS_A, parentId, CharacteristicType.INFERRED_RELATIONSHIP);

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
		
		final Map<?, ?> memberRequest = ImmutableMap.<String, Object>builder()
				.put(SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", simpleRefSetId)
				.put(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId)
				.put("commitComment", "Created new simple reference set member")
				.build();
		
		final String member1Id = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));
		
		final String member2Id = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));
		
		final String member3Id = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));
		
		final BulkRequestBuilder<TransactionContext> bulk = BulkRequest.create();
		
		bulk.add(SnomedRequests.prepareDeleteMember(member1Id));
		bulk.add(SnomedRequests.prepareDeleteMember(member3Id));

		SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Delete reference set members in ascending index order")
			.setUserId("test")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
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
			.setReferenceSetId(simpleRefSetId)
			.setReferencedComponentId(simpleRefSetId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath(), "info@b2international.com", "Creating refset member")
			.execute(getBus())
			.getSync();
		
		final String member1Id = result1.getResultAs(String.class);
		assertEquals(memberId, member1Id);	
		
		final CommitResult result2 = SnomedRequests.prepareNewMember()
			.setId(memberId)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setReferenceSetId(simpleRefSetId)
			.setReferencedComponentId(simpleRefSetId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath(), "info@b2international.com", "Creating refset member")
			.execute(getBus())
			.getSync();	
	}
	
	@Test
	public void testCreateMemberWithSpecificId() {
		final String simpleRefSetId = createNewRefSet(branchPath);
		final String specificId = UUID.randomUUID().toString();
		final Map<?, ?> memberRequest = ImmutableMap.<String, Object>builder()
				.put(SnomedRf2Headers.FIELD_ID, specificId)
				.put(SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", simpleRefSetId)
				.put(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId)
				.put("commitComment", "Created new simple reference set member")
				.build();
		
		final String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));
		
		assertEquals(specificId, memberId);
	}

	private void executeSyncAction(final String memberId) {
		final Map<?, ?> syncActionRequest = ImmutableMap.<String, Object>builder()
				.put("action", "sync")
				.put(SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE)
				.put("commitComment", "Executed sync action on reference set member")
				.build();

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
