/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.JSON_UTF8;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.google.common.collect.HashBiMap;

/**
 * @since 8.0.0
 */
public class SnomedConceptSearchApiTest extends AbstractSnomedApiTest {
	
	@Test
	public void searchBySemanticTag() throws Exception {
		String conceptId = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
		createNewDescription(branchPath, Json.object(
			"conceptId", conceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", "My awesome fsn (tag)",
			"languageCode", "en",
			"acceptability", UK_PREFERRED_MAP,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE,
			"commitComment", "New FSN"
		));
		
		SnomedConcepts hits = givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of("semanticTag", "tag"))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.extract().as(SnomedConcepts.class);
		assertThat(hits.getTotal()).isEqualTo(1);
	}
	
	/**
	 * Related to https://github.com/b2ihealthcare/snow-owl/issues/738
	 */
	@Test
	public void searchByBothSemanticTagAndTerm() throws Exception {
		String conceptId = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
		createNewDescription(branchPath, Json.object(
			"conceptId", conceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", "My awesome fsn (tag)",
			"languageCode", "en",
			"acceptability", UK_PREFERRED_MAP,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE,
			"commitComment", "New FSN"
		));
		createNewDescription(branchPath, Json.object(
			"conceptId", conceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", "My another fsn (other)",
			"languageCode", "en",
			"acceptability", UK_PREFERRED_MAP,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE,
			"commitComment", "New FSN"
		));
		
		SnomedConcepts hits = givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of(
				"term", "another",
				"semanticTag", "tag"
			))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.extract().as(SnomedConcepts.class);
		assertThat(hits.getTotal()).isEqualTo(1);
	}

	@Test
	public void searchByMembership() throws Exception {
		String conceptId1 = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
		String conceptId2 = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
		String refSetId = createNewRefSet(branchPath, SnomedRefSetType.SIMPLE);
		String memberId1 = createNewRefSetMember(branchPath, conceptId1, refSetId); 
		createNewRefSetMember(branchPath, conceptId2, refSetId);
		
		updateComponent(
			branchPath, 
			SnomedComponentType.MEMBER, 
			memberId1, 
			Json.object(
				"active", false,
				"commitComment", "Inactivated reference set member"
			)
		).statusCode(204);
		
		SnomedConcepts hits = givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of("isActiveMemberOf", List.of(refSetId)))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.extract().as(SnomedConcepts.class);

		assertThat(hits.getTotal()).isEqualTo(1);
	}
	
	@Test
	public void searchByNamespace() throws Exception {
		String conceptId = createNewConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT)
			.with("namespaceId", "1000001")
			.with("commitComment", "Create new concept"));
		
		SnomedConcepts hits = givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of("namespace", List.of("1000001")))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.extract().as(SnomedConcepts.class);

		assertThat(hits.getTotal()).isEqualTo(1);
		assertThat(hits.getItems()).allMatch(c -> conceptId.equals(c.getId()));
	}
	
	@Test
	public void searchByNamespaceConceptId() throws Exception {
		String conceptId = createNewConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT)
			.with("namespaceId", "1000001")
			.with("commitComment", "Create new concept"));
		
		SnomedConcepts hits = givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of("namespaceConceptId", List.of("370138007"))) // Extension Namespace {1000001} (namespace concept) 
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.extract().as(SnomedConcepts.class);

		assertThat(hits.getTotal()).isEqualTo(1);
		assertThat(hits.getItems()).allMatch(c -> conceptId.equals(c.getId()));
	}
	
	@Test
	public void expandInactivationIndicator() {
		final String inactiveConceptId = createInactiveConcept(branchPath);
		createNewRefSetMember(branchPath, inactiveConceptId, Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR, Json.object(SnomedRf2Headers.FIELD_VALUE_ID, Concepts.AMBIGUOUS));
		
		givenAuthenticatedRequest(getApiBaseUrl())
				.accept(JSON_UTF8)
				.queryParams(Map.of(
						"id", List.of(inactiveConceptId),
						"expand", "inactivationProperties(expand(inactivationIndicator()))"))
				.get("/{path}/concepts/", branchPath.getPath())
				.then().assertThat()
				.statusCode(200)
				.assertThat()
				.body("total", equalTo(1))
				.body("items[0].inactivationProperties.inactivationIndicator.id", equalTo(Concepts.AMBIGUOUS));
	}
	
	@Test
	public void expandAssociationTargetComponents() throws Exception {
		final String inactiveConceptId = createInactiveConcept(branchPath);
		final String wasAConceptId = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
		createNewRefSetMember(branchPath, inactiveConceptId, Concepts.REFSET_WAS_A_ASSOCIATION, Json.object(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID, wasAConceptId));
		
		givenAuthenticatedRequest(getApiBaseUrl())
				.accept(JSON_UTF8)
				.queryParams(Map.of(
						"id", List.of(inactiveConceptId),
						"expand", "inactivationProperties(expand(associationTargets(expand(targetComponent(expand(fsn()))))))"))
				.get("/{path}/concepts/", branchPath.getPath())
				.then().assertThat()
				.statusCode(200)
				.assertThat()
				.body("total", equalTo(1))
				.body("items[0].inactivationProperties.associationTargets.size()", equalTo(1))
				.body("items[0].inactivationProperties.associationTargets[0].targetComponent.id", equalTo(wasAConceptId))
				.body("items[0].inactivationProperties.associationTargets[0].targetComponent.fsn.term", equalTo("FSN of concept"));
	}
	
	@Test
	public void sortAscending() throws Exception {
		String conceptId1 = createNewConceptWithDescription(branchPath, Concepts.ROOT_CONCEPT, "AAA AAA");
		String conceptId2 = createNewConceptWithDescription(branchPath, Concepts.ROOT_CONCEPT, "AAA BBB");
		String conceptId3 = createNewConceptWithDescription(branchPath, Concepts.ROOT_CONCEPT, "AAA CCC");
		
		List<String> hits = givenAuthenticatedRequest(getApiBaseUrl())
				.accept(JSON_UTF8)
				.queryParams(Map.of("sort", "term:asc",
						"limit", "3",
						"term", "AAA"))
				.get("/{path}/concepts/", branchPath.getPath())
				.then().assertThat()
				.statusCode(200)
				.extract().as(SnomedConcepts.class)
				.getItems()
				.stream()
				.map(concept -> concept.getId())
				.collect(Collectors.toList());

		assertThat(hits).containsExactly(conceptId1, conceptId2, conceptId3);
	}
	
	@Test
	public void sortDescending() throws Exception {
		String conceptId1 = createNewConceptWithDescription(branchPath, Concepts.ROOT_CONCEPT, "AAA AAA");
		String conceptId2 = createNewConceptWithDescription(branchPath, Concepts.ROOT_CONCEPT, "AAA BBB");
		String conceptId3 = createNewConceptWithDescription(branchPath, Concepts.ROOT_CONCEPT, "AAA CCC");
		
		List<String> hits = givenAuthenticatedRequest(getApiBaseUrl())
				.accept(JSON_UTF8)
				.queryParams(Map.of("sort", "term:desc",
						"limit", "3",
						"term", "AAA"))
				.get("/{path}/concepts/", branchPath.getPath())
				.then().assertThat()
				.statusCode(200)
				.extract().as(SnomedConcepts.class)
				.getItems()
				.stream()
				.map(concept -> concept.getId())
				.collect(Collectors.toList());
		
		assertThat(hits).containsExactly(conceptId3, conceptId2, conceptId1);
	}
	
	@Test
	public void searchByHierarchy() throws Exception {
		final Map<String, String> roleToId = createConceptHierarchy(branchPath);
		
		assertHierarchyContains("statedParent", "parent", roleToId, Set.of("child1", "child2"));
		assertHierarchyContains("statedAncestor", "parent", roleToId, Set.of("child1", "child2", "descendant1", "descendant2", "descendant3"));
		assertHierarchyContains("parent", "parent", roleToId, Set.of("child1"));
		assertHierarchyContains("ancestor", "parent", roleToId, Set.of("child1", "descendant1"));
	}
	
	@Test
	public void searchByEffectiveTime() throws Exception {
		List<String> matchedEffectiveTimes = givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of("effectiveTime", "20050131"))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.assertThat()
			.extract()
			.path("items.effectiveTime");
		
		assertThat(matchedEffectiveTimes).containsOnly("20050131");
	}
	
	@Test
	public void searchByEffectiveTimeRange() throws Exception {
		List<String> matchedEffectiveTimes = givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of("effectiveTime", "20050131...20050731"))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.assertThat()
			.extract()
			.path("items.effectiveTime");
		
		assertThat(matchedEffectiveTimes).containsOnly("20050131", "20050731");
	}
	
	@Test
	public void searchByEffectiveTimeRangeNoStartDate() throws Exception {
		givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of("effectiveTime", "...20050731"))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(400);
	}
	
	@Test
	public void searchByEffectiveTimeRangeNoEndDate() throws Exception {
		givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of("effectiveTime", "20050131..."))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(400);
	}
	
	/**
	 * Single nested module expand of definitionStatus results in status 500
	 */
	@Test
	public void expandNestedModuleOnly() {
		final String conceptExpand = "definitionStatus(expand(module()))";
		
		final String conceptId = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
		
		givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of(
					"id", conceptId,
					"expand", conceptExpand))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.assertThat()
			.body("total", equalTo(1))
			.body("items[0].definitionStatus.module.id", equalTo(Concepts.MODULE_SCT_MODEL_COMPONENT));
	}
	
	/**
	 * Definitions status module expand fail.
	 */
	@Test
	public void expandNestedModule() {
		final String conceptExpand = "definitionStatus(expand(pt(),fsn(),module(expand(pt(),fsn())))),module(expand(pt(),fsn()))";
		
		final String conceptId = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
		
		givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of(
					"id", conceptId,
					"expand", conceptExpand))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.assertThat()
			.body("total", equalTo(1))
			.body("items[0].definitionStatus.module.id", equalTo(Concepts.MODULE_SCT_MODEL_COMPONENT))
			.body("items[0].module.id", equalTo(Concepts.MODULE_SCT_CORE));
	}
	
	/**
	 * Multiple nested module expansion results in evaluating only the first.
	 */
	@Test
	public void multipleNestedModuleExpand() {
		final String descriptionExpand = "descriptions(expand(acceptabilities(expand(acceptability(expand(pt(),fsn())),languageRefSet(expand(pt(),fsn())))),type(expand(pt(),fsn())),module(expand(pt(),fsn())),caseSignificance(expand(pt(),fsn()))))";
		final String relationshipExpand = "relationships(expand(type(expand(pt(),fsn())),destination(expand(pt(),fsn())),module(expand(pt(),fsn())),characteristicType(expand(pt(),fsn())),modifier(expand(pt(),fsn()))))";
		final String conceptExpand = "definitionStatus(expand(pt(),fsn())),module(expand(pt(),fsn())),pt(),fsn()";
		final String inactivationPropertiesExpand = "inactivationProperties(expand(associationTargets(expand(targetComponent(expand(pt(),fsn())))),inactivationIndicator(expand(pt(),fsn()))))";
		
		final String expand = String.format("%s,%s,%s,%s", descriptionExpand, relationshipExpand, conceptExpand, inactivationPropertiesExpand);
		final String conceptId = createNewConcept(branchPath, Concepts.ROOT_CONCEPT);
		
		givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of(
					"id", conceptId,
					"expand", expand))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.assertThat()
			.body("total", equalTo(1))
			.body("items[0].descriptions.items.module.id", allOf(not(emptyIterable()), everyItem(equalTo(Concepts.MODULE_SCT_CORE))))
			.body("items[0].relationships.items.module.id", allOf(not(emptyIterable()), everyItem(equalTo(Concepts.MODULE_SCT_CORE))));
	}
	
	@Test
	public void wildcardAcceptLanguageHeader() throws Exception {
		givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of("expand", "pt()"))
			.header("Accept-Language", "*")
			.get("/{path}/concepts/", branchPath.getPath())
			.then()
			.assertThat()
			.statusCode(200)
			.body("items[0].pt.term", CoreMatchers.notNullValue());
	}

	private void assertHierarchyContains(String hierarchyField, String parentOrAncestorRole, Map<String, String> roleToId, Set<String> expectedRoles) {
		Map<String, String> idToRole = HashBiMap.create(roleToId).inverse();
		
		List<String> hits = givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of(hierarchyField, roleToId.get(parentOrAncestorRole)))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.extract().as(SnomedConcepts.class)
			.getItems()
			.stream()
			.map(concept -> idToRole.getOrDefault(concept.getId(), concept.getId()))
			.collect(Collectors.toList());
		
		assertThat(hits).containsExactlyInAnyOrderElementsOf(expectedRoles);
	}
}
