/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;

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
}
