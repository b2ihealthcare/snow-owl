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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * A set of assert methods related to manipulation of components through the REST API.
 *
 * @since 2.0
 */
public abstract class SnomedComponentApiAssert {

	private static ValidatableResponse assertComponentReadWithStatus(final IBranchPath branchPath, 
			final SnomedComponentType componentType, 
			final String componentId, 
			final int statusCode) {

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.when().get("/{path}/{componentType}/{id}", branchPath.getPath(), componentType.toLowerCasePlural(), componentId)
				.then().assertThat().statusCode(statusCode);
	}

	/**
	 * Asserts that the component with the given type and identifier exists on the given branch.
	 *  
	 * @param branchPath the branch path to test
	 * @param componentType the expected component type
	 * @param componentId the expected component identifier
	 */
	public static void assertComponentExists(final IBranchPath branchPath, final SnomedComponentType componentType, final String componentId) {
		assertComponentReadWithStatus(branchPath, componentType, componentId, 200);
	}

	/**
	 * Asserts that the component with the given type and identifier does not exist on the given branch.
	 *  
	 * @param branchPath the branch path to test
	 * @param componentType the expected component type
	 * @param componentId the expected component identifier
	 */
	public static void assertComponentNotExists(final IBranchPath branchPath, final SnomedComponentType componentType, final String componentId) {
		assertComponentReadWithStatus(branchPath, componentType, componentId, 404);
	}

	private static Response whenCreatingComponent(final IBranchPath branchPath, 
			final SnomedComponentType componentType, 
			final Map<?, ?> requestBody) {

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post("/{path}/{componentType}", branchPath.getPath(), componentType.toLowerCasePlural());
	}

	public static ValidatableResponse assertComponentCreatedWithStatus(final IBranchPath branchPath, 
			final SnomedComponentType componentType, 
			final Map<?, ?> requestBody, 
			final int statusCode) {

		return whenCreatingComponent(branchPath, componentType, requestBody)
				.then().assertThat().statusCode(statusCode);
	}

	/**
	 * Asserts that the component with the given type can be created on the specified branch.
	 * 
	 * @param branchPath the branch path to test
	 * @param componentType the expected component type
	 * @param requestBody the request body used for creating the component
	 * 
	 * @return the identifier of the created component
	 */
	public static String assertComponentCreated(final IBranchPath branchPath, 
			final SnomedComponentType componentType, 
			final Map<?, ?> requestBody) {

		final String componentLocation = assertComponentCreatedWithStatus(branchPath, componentType, requestBody, 201)
				.and().header("Location", containsString(String.format("%s/%s", branchPath.getPath(), componentType.toLowerCasePlural())))
				.and().body(equalTo(""))
				.and().extract().response().getHeader("Location");

		return lastPathSegment(componentLocation);
	}

	/**
	 * Asserts that the component creation with the given type will be rejected on the specified branch.
	 * 
	 * @param branchPath the branch path to test
	 * @param componentType the expected component type
	 * @param requestBody the request body used for creating the component
	 * 
	 * @return the validatable response for additional checks
	 */
	public static ValidatableResponse assertComponentNotCreated(final IBranchPath branchPath, 
			final SnomedComponentType componentType, 
			final Map<?, ?> requestBody) {

		return assertComponentCreatedWithStatus(branchPath, componentType, requestBody, 400)
				.and().body("status", equalTo(400));
	}

	/**
	 * Asserts that the given property name/value pair appears on the component when read.
	 * 
	 * @param branchPath the branch path to test
	 * @param componentType the expected component type
	 * @param componentId the component identifier
	 * @param propertyName the property name to test
	 * @param value the expected value 
	 */
	public static void assertComponentHasProperty(final IBranchPath branchPath,
			final SnomedComponentType componentType, 
			final String componentId, 
			final String propertyName, 
			final Object value) {

		assertComponentReadWithStatus(branchPath, componentType, componentId, 200)
		.and().body(propertyName, equalTo(value));
	}

	/**
	 * Assert that the component with the specified type and identifier is active on the given branch.
	 * 
	 * @param branchPath the branch path to test
	 * @param componentType the expected component type
	 * @param componentId the component identifier
	 * @param active the expected status ({@code true} if active, {@code false} if inactive)
	 */
	public static void assertComponentActive(final IBranchPath branchPath,
			final SnomedComponentType componentType, 
			final String componentId, 
			final boolean active) {

		assertComponentHasProperty(branchPath, componentType, componentId, "active", active);
	}

	/**
	 * Asserts that the component with the specified type and identifier can be updated using the given request body.
	 * 
	 * @param branchPath the branch path to use when updating
	 * @param componentType the component type
	 * @param componentId the identifier of the component to update
	 * @param requestBody the request body containing new property/value pairs
	 */
	public static void assertComponentCanBeUpdated(final IBranchPath branchPath, 
			final SnomedComponentType componentType, 
			final String componentId, 
			final Map<?, ?> requestBody) {

		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.with().contentType(ContentType.JSON)
		.and().body(requestBody)
		.when().post("/{path}/{componentType}/{id}/updates", branchPath.getPath(), componentType.toLowerCasePlural(), componentId)
		.then().assertThat().statusCode(204);
	}

	public static void assertComponentCanBeDeleted(final IBranchPath branchPath, 
			final SnomedComponentType componentType, 
			final String componentId) {

		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when().delete("/{path}/{componentType}/{id}", branchPath.getPath(), componentType.toLowerCasePlural(), componentId)
		.then().assertThat().statusCode(204);
	}

	/**
	 * Asserts that a concept with the given identifier exists on the specified branch.
	 * 
	 * @param branchPath the branch path to check
	 * @param conceptId the concept identifier to check
	 */
	public static void assertConceptExists(final IBranchPath branchPath, final String conceptId) {
		assertComponentExists(branchPath, SnomedComponentType.CONCEPT, conceptId);
	}

	/**
	 * Asserts that a concept with the given identifier does not exist on the specified branch.
	 * 
	 * @param branchPath the branch path to check
	 * @param conceptId the concept identifier to check
	 */
	public static void assertConceptNotExists(final IBranchPath branchPath, final String conceptId) {
		assertComponentNotExists(branchPath, SnomedComponentType.CONCEPT, conceptId);
	}

	/**
	 * Asserts that a description with the given identifier exists on the specified branch.
	 * 
	 * @param branchPath the branch path to check
	 * @param descriptionId the description identifier to check
	 */
	public static void assertDescriptionExists(final IBranchPath branchPath, final String descriptionId) {
		assertComponentExists(branchPath, SnomedComponentType.DESCRIPTION, descriptionId);
	}

	/**
	 * Asserts that a description with the given identifier does not exist on the specified branch.
	 * 
	 * @param branchPath the branch path to check
	 * @param descriptionId the description identifier to check
	 */
	public static void assertDescriptionNotExists(final IBranchPath branchPath, final String descriptionId) {
		assertComponentNotExists(branchPath, SnomedComponentType.DESCRIPTION, descriptionId);
	}

	/**
	 * Asserts that a relationship with the given identifier exists on the specified branch.
	 * 
	 * @param branchPath the branch path to check
	 * @param relationshipId the relationship identifier to check
	 */
	public static void assertRelationshipExists(final IBranchPath branchPath, final String relationshipId) {
		assertComponentExists(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId);
	}

	/**
	 * Asserts that a relationship with the given identifier does not exist on the specified branch.
	 * 
	 * @param branchPath the branch path to check
	 * @param relationshipId the relationship identifier to check
	 */
	public static void assertRelationshipNotExists(final IBranchPath branchPath, final String relationshipId) {
		assertComponentNotExists(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId);
	}

	/**
	 * Asserts that the concept's preferred term in the UK language reference set matches the specified description identifier.
	 * 
	 * @param branchPath the branch path to test
	 * @param conceptId the identifier of the concept where the preferred term should be compared
	 * @param descriptionId the expected description identifier
	 */
	public static void assertPreferredTermEquals(final IBranchPath branchPath, final String conceptId, final String descriptionId) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.with().header("Accept-Language", "en-GB")
		.when().get("/{path}/concepts/{conceptId}/pt", branchPath.getPath(), conceptId)
		.then().assertThat().statusCode(200)
		.and().body("id", equalTo(descriptionId));
	}

	private SnomedComponentApiAssert() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
