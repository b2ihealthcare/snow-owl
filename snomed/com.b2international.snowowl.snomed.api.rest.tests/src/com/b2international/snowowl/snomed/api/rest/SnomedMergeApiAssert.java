/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.SYNONYM;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Date;
import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.7
 */
public abstract class SnomedMergeApiAssert {
	
	public static final Map<String, String> symbolicNameMap = newHashMap();

	// --------------------------------------------------------
	// Symbolic component existence checks
	// --------------------------------------------------------

	public static void assertConceptExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertConceptExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	public static void assertDescriptionExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertDescriptionExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	public static void assertRelationshipExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertRelationshipExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	public static void assertConceptNotExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertConceptNotExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	public static void assertDescriptionNotExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertDescriptionNotExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	public static void assertRelationshipNotExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertRelationshipNotExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	// --------------------------------------------------------
	// Symbolic component creation
	// --------------------------------------------------------

	public static void assertComponentCreated(final IBranchPath branchPath, 
			final String symbolicName, 
			final SnomedComponentType componentType, 
			final Map<?, ?> requestBody) {

		symbolicNameMap.put(symbolicName, SnomedComponentApiAssert.assertComponentCreated(branchPath, componentType, requestBody));
	}

	public static void assertConceptCreated(final IBranchPath branchPath, final String symbolicName) {
		final Date creationDate = new Date();

		final Map<?, ?> fsnDescription = ImmutableMap.<String, Object>builder()
				.put("typeId", FULLY_SPECIFIED_NAME)
				.put("term", "New FSN at " + creationDate)
				.put("languageCode", "en")
				.put("acceptability", PREFERRED_ACCEPTABILITY_MAP)
				.build();

		final Map<?, ?> ptDescription = ImmutableMap.<String, Object>builder()
				.put("typeId", SYNONYM)
				.put("term", "New PT at " + creationDate)
				.put("languageCode", "en")
				.put("acceptability", PREFERRED_ACCEPTABILITY_MAP)
				.build();

		final ImmutableMap.Builder<String, Object> conceptBuilder = ImmutableMap.<String, Object>builder()
				.put("commitComment", "New concept")
				.put("parentId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("descriptions", ImmutableList.of(fsnDescription, ptDescription));

		assertComponentCreated(branchPath, symbolicName, SnomedComponentType.CONCEPT, conceptBuilder.build());
	}

	public static void assertDescriptionCreated(final IBranchPath branchPath, final String symbolicName, final String typeId, final Map<?, ?> acceptabilityMap) {
		assertDescriptionCreated(branchPath, symbolicName, Concepts.ROOT_CONCEPT, typeId, acceptabilityMap);
	}
	
	public static void assertDescriptionCreated(final IBranchPath branchPath, final String symbolicName, final String conceptId, final String typeId, final Map<?, ?> acceptabilityMap) {
		final Date creationDate = new Date();

		final Map<?, ?> requestBody = ImmutableMap.builder()
				.put("conceptId", conceptId)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", typeId)
				.put("term", "New description at " + creationDate)
				.put("languageCode", "en")
				.put("acceptability", acceptabilityMap)
				.put("commitComment", "New description")
				.build();

		assertComponentCreated(branchPath, symbolicName, SnomedComponentType.DESCRIPTION, requestBody);
	}
	
	public static void assertDescriptionCreated(final IBranchPath branchPath, final String symbolicName, final Map<?, ?> acceptabilityMap) {
		assertDescriptionCreated(branchPath, symbolicName, Concepts.SYNONYM, acceptabilityMap);
	}

	public static void assertRelationshipCreated(final IBranchPath branchPath, final String symbolicName) {
		// destination - Morphologic abnormality
		assertRelationshipCreated(branchPath, symbolicName, Concepts.ROOT_CONCEPT, "49755003");
	}
	
	public static void assertRelationshipCreated(final IBranchPath branchPath, final String symbolicName, final String sourceId, final String destinationId) {
		final Map<?, ?> requestBody = ImmutableMap.builder()
				.put("sourceId", sourceId)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", "116676008") // Associated morphology
				.put("destinationId", destinationId)
				.put("commitComment", "New relationship")
				.build();

		assertComponentCreated(branchPath, symbolicName, SnomedComponentType.RELATIONSHIP, requestBody);
	}

	// --------------------------------------------------------
	// Symbolic component updates
	// --------------------------------------------------------

	public static void assertComponentCanBeUpdated(final IBranchPath branchPath, 
			final String symbolicName, 
			final SnomedComponentType componentType, 
			final Map<?, ?> requestBody) {

		SnomedComponentApiAssert.assertComponentCanBeUpdated(branchPath, componentType, symbolicNameMap.get(symbolicName), requestBody);
	}

	public static void assertConceptCanBeUpdated(final IBranchPath branchPath, final String symbolicName, final Map<?, ?> requestBody) {
		assertComponentCanBeUpdated(branchPath, symbolicName, SnomedComponentType.CONCEPT, requestBody);
	}

	public static void assertDescriptionCanBeUpdated(final IBranchPath branchPath, final String symbolicName, final Map<?, ?> requestBody) {
		assertComponentCanBeUpdated(branchPath, symbolicName, SnomedComponentType.DESCRIPTION, requestBody);
	}

	// --------------------------------------------------------
	// Symbolic component deletion
	// --------------------------------------------------------

	public static void assertComponentCanBeDeleted(final IBranchPath branchPath, final String symbolicName, final SnomedComponentType componentType) {
		SnomedComponentApiAssert.assertComponentCanBeDeleted(branchPath, componentType, symbolicNameMap.get(symbolicName));
	}

	public static void assertConceptCanBeDeleted(final IBranchPath branchPath, final String symbolicName) {
		assertComponentCanBeDeleted(branchPath, symbolicName, SnomedComponentType.CONCEPT);
	}

	public static void assertDescriptionCanBeDeleted(final IBranchPath branchPath, final String symbolicName) {
		assertComponentCanBeDeleted(branchPath, symbolicName, SnomedComponentType.DESCRIPTION);
	}
	
	private SnomedMergeApiAssert() {}
	
}
