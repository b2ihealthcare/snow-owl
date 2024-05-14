/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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

import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.UK_PREFERRED_MAP;
import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.US_PREFERRED_MAP;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.cis.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

/**
 * @since 9.2
 */
public class SnomedConceptCreateApiTest extends AbstractSnomedApiTest {

	@Test
	public void createDuplicateConcept() throws Exception {
		String conceptId = createNewConcept(branchPath);
		Map<?, ?> requestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.with("id", conceptId)
				.with("commitComment", "Created new concept with duplicate identifier");

		createComponent(branchPath, SnomedComponentType.CONCEPT, requestBody).statusCode(409);
	}
	
	@Test
	public void createConceptNonExistentBranch() {
		assertCreateConcept(BranchPathUtils.createPath("MAIN/x/y/z"), createConceptRequestBody(Concepts.ROOT_CONCEPT))
			.statusCode(404);
	}

	@Test
	public void createConceptEmptyParent() {
		assertCreateConcept(branchPath, createConceptRequestBody(""))
			.statusCode(400)
			.body("message", equalTo("'destinationId' or 'value' should be specified"));
	}

	@Test
	public void createConceptInvalidParent() {
		assertCreateConcept(branchPath, createConceptRequestBody("11110000"))
			.statusCode(400);
	}

	@Test
	public void createConceptInvalidLanguageRefSet() {
		assertCreateConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.INVALID_PREFERRED_MAP))
			.statusCode(400);
	}

	@Test
	public void createConceptInvalidModule() {
		assertCreateConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT, "11110000", SnomedApiTestConstants.INVALID_PREFERRED_MAP))
			.statusCode(400);
	}

	@Test
	public void createConceptWithoutCommitComment() {
		assertCreateConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT).with("commitComment", ""))
			.statusCode(400);
	}
	
	@Test
	public void createConceptOnDeletedBranch() {
		branching.deleteBranch(branchPath);

		assertCreateConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT))
			.statusCode(400);
	}

	@Test
	public void createShortIsACycle() throws Exception {
		String concept1Id = createNewConcept(branchPath);
		String concept2Id = createNewConcept(branchPath, concept1Id);

		// Try creating a cycle between the two concepts
		Map<?, ?> requestBody = createRelationshipRequestBody(concept1Id, Concepts.IS_A, concept2Id)
				.with("commitComment", "Created an IS A cycle with two relationships");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}
	
	@Test
	public void createConcept() {
		final String conceptId = createConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT));
		final SnomedConcept concept = getConcept(conceptId, "statedAncestors(direct:true),ancestors(direct:true)");
		assertEquals(1, concept.getStatedAncestors().getTotal());
		assertEquals(0, concept.getAncestors().getTotal());
	}
	
	@Test
	public void createConceptWithReservedId() {
		ISnomedIdentifierService identifierService = ApplicationContext.getServiceForClass(ISnomedIdentifierService.class);
		String conceptId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.CONCEPT, 1));

		String createConceptId = createConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT).with("id", conceptId));
		
		assertEquals(conceptId, createConceptId);
		
		SctId conceptSctId = SnomedRequests.identifiers().prepareGet()
			.setComponentId(conceptId)
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES)
			.first()
			.get();
		
		assertEquals(IdentifierStatus.ASSIGNED.getSerializedName(), conceptSctId.getStatus());
	}

	@Test
	public void createLongIsACycle() throws Exception {
		String concept1Id = createNewConcept(branchPath);
		String concept2Id = createNewConcept(branchPath, concept1Id);
		String concept3Id = createNewConcept(branchPath, concept2Id);

		// Try creating a cycle between the starting and the ending concept
		Map<?, ?> requestBody = createRelationshipRequestBody(concept1Id, Concepts.IS_A, concept3Id)
				.with("commitComment", "Created an IS A cycle with three relationships");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}
	
	@Test
	public void createConceptWithSemanticTag() throws Exception {
		String conceptId = createConcept(branchPath, SnomedRestFixtures.createConceptRequestBody(Concepts.ROOT_CONCEPT));
		SnomedConcept concept = getConcept(conceptId, "semanticTags()");
		assertThat(concept.getSemanticTags()).isEmpty();
		
		SnomedRestFixtures.createNewDescription(branchPath, Json.object(
			"conceptId", conceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", "My awesome fsn (tag)",
			"languageCode", "en",
			"acceptability", UK_PREFERRED_MAP,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE,
			"commitComment", "New FSN"
		));
		
		concept = getConcept(conceptId, "semanticTags()");
		assertThat(concept.getSemanticTags()).contains("tag");
	}
	
	@Test
	public void createConceptWithMember() throws Exception {
		String refsetId = createNewRefSet(branchPath);

		Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.with("members", Json.array(Json.object(
					"moduleId", Concepts.MODULE_SCT_CORE,
					"refsetId", refsetId
				)))
				.with("commitComment", "Created concept with reference set member");

		String conceptId = assertCreated(createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody));

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "members()").statusCode(200)
			.body("members.items[0].refsetId", equalTo(refsetId));
	}
	
	@Test
	public void createConceptWithOwlAxiomMemberWithSubClassOfExpression() throws Exception {
		final String conceptId = ApplicationContext.getServiceForClass(ISnomedIdentifierService.class).generate(null, ComponentCategory.CONCEPT, 1).iterator().next();
		final String owlSubclassOfExpression = String.format("SubClassOf(:%s :%s)", conceptId, Concepts.AMBIGUOUS);
		
		final Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.FULLY_SPECIFIED_NAME)
			.with(Json.object(
				"id", conceptId,
				"members", Json.array(Json.object(
					"moduleId", Concepts.MODULE_SCT_CORE,
					"refsetId", Concepts.REFSET_OWL_AXIOM,
					SnomedRf2Headers.FIELD_OWL_EXPRESSION, owlSubclassOfExpression
				)),
				"commitComment", "Created concept with owl axiom reference set member"
			));

		createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody)
				.statusCode(201);
		
		final SnomedConcept conceptWithAxiomMember = SnomedRequests.prepareGetConcept(conceptId)
				.setExpand("members()")
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		
		assertNotNull(conceptWithAxiomMember);
		assertEquals(1, conceptWithAxiomMember.getMembers().getTotal());
		assertEquals(Concepts.PRIMITIVE, conceptWithAxiomMember.getDefinitionStatusId()); 
	}
	
	@Test
	public void createConceptWithOwlAxiomMemberWithEquivalentClassesExpression() throws Exception {
		final String conceptId = ApplicationContext.getServiceForClass(ISnomedIdentifierService.class).generate(null, ComponentCategory.CONCEPT, 1).iterator().next();
		
		final String owlEquivalentClassesExpression = String.format("EquivalentClasses(:%s ObjectIntersectionOf(:%s :%s))", conceptId, Concepts.AMBIGUOUS, Concepts.NAMESPACE_ROOT);
		
		final Json conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.with(Json.object(
					"id", conceptId,
					"members", Json.array(Json.object(
						"moduleId", Concepts.MODULE_SCT_CORE,
						"refsetId", Concepts.REFSET_OWL_AXIOM,
						SnomedRf2Headers.FIELD_OWL_EXPRESSION, owlEquivalentClassesExpression
					)),
					"commitComment", "Created concept with owl axiom reference set member"
				));

		createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody).statusCode(201);
		
		final SnomedConcept conceptWithAxiomMember = SnomedRequests.prepareGetConcept(conceptId)
				.setExpand("members()")
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		assertNotNull(conceptWithAxiomMember);
		assertEquals(1, conceptWithAxiomMember.getMembers().getTotal());
		assertEquals(Concepts.FULLY_DEFINED, conceptWithAxiomMember.getDefinitionStatusId()); 
	}

	@Test
	public void createConceptWithOwlAxiomMemberWithComplexSubClassOfExpressionShouldDefaultToPrimitive() throws Exception {
		final String conceptId = ApplicationContext.getServiceForClass(ISnomedIdentifierService.class).generate(null, ComponentCategory.CONCEPT, 1).iterator().next();
		
		final String owlSubClassOfExpression = "SubClassOf(ObjectIntersectionOf(:73211009 ObjectSomeValuesFrom(:609096000 ObjectSomeValuesFrom(:100106001 :100102001))) :"+conceptId+")";
		final Map<?, ?> memberRequestBody = Json.object(
			"moduleId", Concepts.MODULE_SCT_CORE,
			"refsetId", Concepts.REFSET_OWL_AXIOM,
			SnomedRf2Headers.FIELD_OWL_EXPRESSION, owlSubClassOfExpression
		);
		
		final Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.with(Json.object(
					"id", conceptId,
					"members", Json.array(memberRequestBody),
					"commitComment", "Created concept with owl axiom reference set member"
				));
		
		createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody)
				.statusCode(201);
		
		final SnomedConcept conceptWithAxiomMember = SnomedRequests.prepareGetConcept(conceptId)
				.setExpand("members()")
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		assertNotNull(conceptWithAxiomMember);
		assertEquals(1, conceptWithAxiomMember.getMembers().getTotal());
		assertEquals(Concepts.PRIMITIVE, conceptWithAxiomMember.getDefinitionStatusId()); 
	}
	
	@Test
	public void createConceptWithoutOwlAxiomMembersConceptDefinitionStatusShouldDefaultToPrimitive() throws Exception {
		final Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.with("commitComment", "Created concept");
		
		final String conceptId = assertCreated(createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody));
		
		final SnomedConcept concept = SnomedRequests.prepareGetConcept(conceptId)
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		assertNotNull(concept);
		assertEquals(Concepts.PRIMITIVE, concept.getDefinitionStatusId()); 
	}
	
	@Test
	public void createConceptWithHierarchy() {
		final Map<String, String> roleToId = createConceptHierarchy(branchPath);

		assertExpandedHierarchyContains(SnomedConcept.Expand.STATED_DESCENDANTS, true, SnomedConcept::getStatedDescendants, "parent", roleToId, 
			Set.of("child1", "child2"));
		assertExpandedHierarchyContains(SnomedConcept.Expand.STATED_DESCENDANTS, false, SnomedConcept::getStatedDescendants, "parent", roleToId, 
			Set.of("child1", "child2", "descendant1", "descendant2", "descendant3"));
		assertExpandedHierarchyContains(SnomedConcept.Expand.DESCENDANTS, true, SnomedConcept::getDescendants, "parent", roleToId, 
			Set.of("child1"));
		assertExpandedHierarchyContains(SnomedConcept.Expand.DESCENDANTS, false, SnomedConcept::getDescendants, "parent", roleToId, 
			Set.of("child1", "descendant1"));
	}
	
	private void assertExpandedHierarchyContains(String hierarchyField, boolean direct,
			Function<SnomedConcept, SnomedConcepts> hierarchyExtractFunction,
			String parentOrAncestorRole, 
			Map<String, String> roleToId, 
			Set<String> expectedRoles) {
			
		final Map<String, String> idToRole = HashBiMap.create(roleToId).inverse();
		final SnomedConcept conceptWithHierarchy = getConcept(roleToId.get(parentOrAncestorRole), String.format("%s(direct:%s)", hierarchyField, direct));
		final List<String> hits = hierarchyExtractFunction.apply(conceptWithHierarchy)
			.stream()
			.map(concept -> idToRole.getOrDefault(concept.getId(), concept.getId()))
			.collect(Collectors.toList());
		
		assertThat(hits).containsExactlyInAnyOrderElementsOf(expectedRoles);
	}
	
	@Test
	public void createModuleConceptWithAxiom_ShouldPropagateModuleAsUsualWithoutErrors() throws Exception {
		ISnomedIdentifierService identifierService = ApplicationContext.getServiceForClass(ISnomedIdentifierService.class);
		String moduleConceptId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.CONCEPT, 1));

		String createdConceptId = createConcept(branchPath,
			Json.object(
				"id", moduleConceptId,
				"moduleId", moduleConceptId, // module is applied to all subcomponents implicitly via the API
				"active", true,
				"descriptions", Json.array(
					Json.object(
						"typeId", Concepts.FULLY_SPECIFIED_NAME,
						"term", "FSN of module concept",
						"languageCode", DEFAULT_LANGUAGE_CODE,
						"acceptability", UK_PREFERRED_MAP
					),
					Json.object(
						"typeId", Concepts.SYNONYM,
						"term", "PT of concept",
						"languageCode", DEFAULT_LANGUAGE_CODE,
						"acceptability", UK_PREFERRED_MAP
					)
				),
				"relationships", Json.array(
					Json.object(
						"active", true,
						"typeId", Concepts.IS_A,
						"destinationId", Concepts.MODULE_ROOT,
						"characteristicTypeId", Concepts.INFERRED_RELATIONSHIP
					)
				),
				"members", Json.array(
					Json.object(
						"active", true,
						"refsetId", Concepts.REFSET_OWL_AXIOM,
						"owlExpression", String.format("SubClassOf(:%s :%s)", moduleConceptId, Concepts.MODULE_ROOT)
					)
				)
			)
		);
		
		assertEquals(moduleConceptId, createdConceptId);
	}

	@Test
	public void createConceptWithNamespaceConceptId() throws Exception {
		ISnomedIdentifierService identifierService = ApplicationContext.getServiceForClass(ISnomedIdentifierService.class);
		String namespaceConceptId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.CONCEPT, 1));
		String expectedNamespace = "1000999"; 
		// create a namespace concept with a custom namespaceId (simulating a new INT addition of the namespace)
		createConcept(branchPath, Json.object(
			"id", namespaceConceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"active", true,
			"descriptions", Json.array(
				Json.object(
					"typeId", Concepts.FULLY_SPECIFIED_NAME,
					"term", String.format("Extension namespace {%s} (foundation metadata concept)", expectedNamespace),
					"languageCode", DEFAULT_LANGUAGE_CODE,
					"acceptability", US_PREFERRED_MAP
				),
				Json.object(
					"typeId", Concepts.SYNONYM,
					"term", String.format("Extension namespace {%s}", expectedNamespace),
					"languageCode", DEFAULT_LANGUAGE_CODE,
					"acceptability", US_PREFERRED_MAP
				)
			),
			"relationships", Json.array(
				Json.object(
					"active", true,
					"typeId", Concepts.IS_A,
					"destinationId", Concepts.NAMESPACE_ROOT,
					"characteristicTypeId", Concepts.INFERRED_RELATIONSHIP
				)
			),
			"members", Json.array(
				Json.object(
					"active", true,
					"refsetId", Concepts.REFSET_OWL_AXIOM,
					"owlExpression", String.format("SubClassOf(:%s :%s)", namespaceConceptId, Concepts.NAMESPACE_ROOT)
				)
			)
		));
		
		String createdWithNamespaceConceptId = createConcept(branchPath,
			Json.object(
				"namespaceConceptId", namespaceConceptId,
				"moduleId", Concepts.MODULE_SCT_CORE,
				"active", true,
				"descriptions", Json.array(
					Json.object(
						"typeId", Concepts.FULLY_SPECIFIED_NAME,
						"term", "FSN of new concept using namespaceConceptId",
						"languageCode", DEFAULT_LANGUAGE_CODE,
						"acceptability", UK_PREFERRED_MAP
					),
					Json.object(
						"typeId", Concepts.SYNONYM,
						"term", "PT of new concept using namespaceConceptId",
						"languageCode", DEFAULT_LANGUAGE_CODE,
						"acceptability", UK_PREFERRED_MAP
					)
				),
				"relationships", Json.array(
					Json.object(
						"active", true,
						"typeId", Concepts.IS_A,
						"destinationId", Concepts.ROOT_CONCEPT,
						"characteristicTypeId", Concepts.INFERRED_RELATIONSHIP
					)
				)
			)
		);
		
		SnomedConcept conceptCreatedWithNamespaceConceptId = getConcept(createdWithNamespaceConceptId, "descriptions(),relationships()");
		
		assertThat(conceptCreatedWithNamespaceConceptId.getDescriptions())
			.extracting(SnomedDescription::getId)
			.extracting(SnomedIdentifiers::getNamespace)
			.containsOnly(expectedNamespace);
		
		assertThat(conceptCreatedWithNamespaceConceptId.getRelationships())
			.extracting(SnomedRelationship::getId)
			.extracting(SnomedIdentifiers::getNamespace)
			.containsOnly(expectedNamespace);
	}
	
}
