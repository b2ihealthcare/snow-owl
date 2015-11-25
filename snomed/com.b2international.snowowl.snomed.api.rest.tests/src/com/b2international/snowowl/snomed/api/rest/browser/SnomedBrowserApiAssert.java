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
package com.b2international.snowowl.snomed.api.rest.browser;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Date;
import java.util.Map;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.domain.browser.SnomedBrowserDescriptionType;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 4.5
 */
public class SnomedBrowserApiAssert {
	
	public static ValidatableResponse assertComponentCreatedWithStatus(final IBranchPath branchPath, 
			final Map<?, ?> requestBody, 
			final int statusCode) {
		return whenCreatingComponent(branchPath, requestBody)
				.then().log().ifValidationFails().assertThat().statusCode(statusCode);
	}
	
	private static Response whenCreatingComponent(final IBranchPath branchPath, 
			final Map<?, ?> requestBody) {

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post("/browser/{path}/concepts", branchPath.getPath());
	}
	
	public static ValidatableResponse assertComponentNotCreated(final IBranchPath branchPath, 
			final Map<?, ?> requestBody) {

		return assertComponentCreatedWithStatus(branchPath, requestBody, 400)
				.and().body("status", equalTo(400));
	}
	
	public static ValidatableResponse assertComponentUpdatedWithStatus(final IBranchPath branchPath, 
			final String conceptId,
			final Map<?, ?> requestBody, 
			final int statusCode) {
		return whenUpdatingComponent(branchPath, conceptId, requestBody)
				.then().log().ifValidationFails().assertThat().statusCode(statusCode);
	}
	
	private static Response whenUpdatingComponent(final IBranchPath branchPath, 
			final String conceptId,
			final Map<?, ?> requestBody) {

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().put("/browser/{path}/concepts/{conceptId}", branchPath.getPath(), conceptId);
	}
	
	public static Map<String, Object> givenConceptRequestBody(final String conceptId, final boolean active, final String fsn, final String moduleId,
			final ImmutableList<?> descriptions, final ImmutableList<?> relationships, final Date creationDate) {
		final ImmutableMap.Builder<String, Object> conceptBuilder = ImmutableMap.<String, Object>builder()
				.put("fsn", fsn)
				.put("preferredSynonym", fsn)
				.put("moduleId", moduleId)
				.put("isLeafInferred", "")
				.put("isLeafStated", "")
				.put("definitionStatus", DefinitionStatus.PRIMITIVE);
		
		if (null != conceptId)
			conceptBuilder.put("conceptId", conceptId);
		
		if (null == descriptions)
			conceptBuilder.put("descriptions", Lists.newArrayList());
		else
			conceptBuilder.put("descriptions", descriptions);
		
		if (null == relationships)
			conceptBuilder.put("relationships", Lists.newArrayList());
		else
			conceptBuilder.put("relationships", relationships);
		
		return conceptBuilder.build();
	}
	
	public static ImmutableList<?> createDescriptions(final String fsn, final String moduleId, final Map<?, ?> fsnAcceptabilityMap,
			final Date creationDate) {
		final Map<?, ?> fsnDescription = ImmutableMap.<String, Object>builder()
				.put("descriptionId", generateComponentId(null, ComponentCategory.DESCRIPTION))
				.put("effectiveTime", creationDate)
				.put("conceptId", "")
				.put("active", true)
				.put("term", fsn)
				.put("type", SnomedBrowserDescriptionType.FSN)
				.put("lang", "en")
				.put("moduleId", moduleId)
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("acceptabilityMap", fsnAcceptabilityMap)
				.build();

		final Map<?, ?> ptDescription = ImmutableMap.<String, Object>builder()
				.put("descriptionId", generateComponentId(null, ComponentCategory.DESCRIPTION))
				.put("effectiveTime", creationDate)
				.put("conceptId", "")
				.put("active", true)
				.put("term", "New PT at " + creationDate)
				.put("type", SnomedBrowserDescriptionType.SYNONYM)
				.put("lang", "en")
				.put("moduleId", moduleId)
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("acceptabilityMap", PREFERRED_ACCEPTABILITY_MAP)
				.build();
		
		return ImmutableList.of(fsnDescription, ptDescription);
	}
	
	public static ImmutableList<?> createIsaRelationship(final String parentId, final String moduleId, final Date creationDate) {
		final Map<?, ?> type = ImmutableMap.<String, Object>builder()
				.put("conceptId", IS_A)
				.put("fsn", "Is a (attribute)")
				.build();
		
		final Map<?, ?> target = ImmutableMap.<String, Object>builder()
				.put("effectiveTime", creationDate)
				.put("active", true)
				.put("moduleId", moduleId)
				.put("conceptId", null == parentId ? generateComponentId(null, ComponentCategory.CONCEPT) : parentId)
				.put("fsn", "")
				.put("definitionStatus", DefinitionStatus.PRIMITIVE)
				.build();
		
		final Map<?, ?> isaRelationship = ImmutableMap.<String, Object>builder()
				.put("sourceId", "")
				.put("effectiveTime", creationDate)
				.put("modifier", RelationshipModifier.UNIVERSAL)
				.put("groupId", "0")
				.put("characteristicType", CharacteristicType.STATED_RELATIONSHIP)
				.put("active", true)
				.put("type", type)
				.put("relationshipId", generateComponentId(null, ComponentCategory.RELATIONSHIP))
				.put("moduleId", moduleId)
				.put("target", target)
				.build();

		return ImmutableList.of(isaRelationship);
	}
	
	public static String generateComponentId(final String namespace, final ComponentCategory category) {
		final ISnomedIdentifierService identifierService = ApplicationContext.getInstance().getService(ISnomedIdentifierService.class);
		final SnomedIdentifiers snomedIdentifiers = new SnomedIdentifiers(identifierService);
		return snomedIdentifiers.generate(namespace, category);
	}
	
	public static Map<String, Object> getConcept(final IBranchPath branchPath, final String conceptId) {
		final Map<?, ?> concept = givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.with().contentType(ContentType.JSON)
				.when().get("/browser/{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
				.then().extract().as(Map.class);
		
		return (Map<String, Object>) concept;
	}

}
