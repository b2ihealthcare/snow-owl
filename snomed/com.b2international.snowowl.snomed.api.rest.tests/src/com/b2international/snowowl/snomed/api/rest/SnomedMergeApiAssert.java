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
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetApiAssert.createSimpleConceptReferenceSetMember;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.7
 */
public abstract class SnomedMergeApiAssert {
	
	public static final String ASSOCIATED_MORPHOLOGY = "116676008";
	public static final String MORPHOLOGIC_ABNORMALITY = "49755003";
	
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

	public static void assertRefSetMemberExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertComponentExists(branchPath, SnomedComponentType.MEMBER, symbolicNameMap.get(symbolicName));
	}
	
	public static void assertRefSetMemberNotExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertComponentNotExists(branchPath, SnomedComponentType.MEMBER, symbolicNameMap.get(symbolicName));
	}
	
	public static void assertDescriptionProperty(final IBranchPath branchPath, final String descriptionId, final String propertyName, final String expectedResult) {
		SnomedComponentApiAssert.assertComponentHasProperty(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, propertyName, expectedResult);
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
	
	public static void assertDescriptionCreatedWithId(final IBranchPath branchPath, final String symbolicName, final String descriptionId, final Map<?, ?> acceptabilityMap) {
		final Date creationDate = new Date();

		final Map<?, ?> requestBody = ImmutableMap.builder()
				.put("conceptId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", "New description at " + creationDate)
				.put("languageCode", "en")
				.put("acceptability", acceptabilityMap)
				.put("id", descriptionId)
				.put("commitComment", "New description")
				.build();

		assertComponentCreated(branchPath, symbolicName, SnomedComponentType.DESCRIPTION, requestBody);
	}
	
	public static void assertDescriptionCreated(final IBranchPath branchPath, final String symbolicName, final Map<?, ?> acceptabilityMap) {
		assertDescriptionCreated(branchPath, symbolicName, Concepts.SYNONYM, acceptabilityMap);
	}

	public static void assertRelationshipCreated(final IBranchPath branchPath, final String symbolicName) {
		// destination - Morphologic abnormality
		assertRelationshipCreated(branchPath, symbolicName, Concepts.ROOT_CONCEPT, MORPHOLOGIC_ABNORMALITY);
	}
	
	public static void assertRelationshipCreated(final IBranchPath branchPath, final String symbolicName, final String sourceId, final String destinationId) {
		final Map<?, ?> requestBody = ImmutableMap.builder()
				.put("sourceId", sourceId)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", ASSOCIATED_MORPHOLOGY)
				.put("destinationId", destinationId)
				.put("commitComment", "New relationship")
				.build();

		assertComponentCreated(branchPath, symbolicName, SnomedComponentType.RELATIONSHIP, requestBody);
	}
	
	public static void assertRelationshipCreated(final IBranchPath branchPath, final String symbolicName, final String sourceId, final String destinationId, final String typeId) {
		final Map<?, ?> requestBody = ImmutableMap.builder()
				.put("sourceId", sourceId)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", typeId)
				.put("destinationId", destinationId)
				.put("commitComment", "New relationship")
				.build();

		assertComponentCreated(branchPath, symbolicName, SnomedComponentType.RELATIONSHIP, requestBody);
	}

	public static void assertRefsetMemberCreated(final IBranchPath branchPath, final String symbolicName) {
		symbolicNameMap.put(symbolicName, createSimpleConceptReferenceSetMember(branchPath));
	}
	
	public static void assertRefsetMemberCreated(final IBranchPath branchPath, final String symbolicName, final String referencedComponentId) {
		symbolicNameMap.put(symbolicName, createSimpleConceptReferenceSetMember(branchPath, referencedComponentId));
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
	
	public static void assertRefSetMemberCanBeDeleted(final IBranchPath branchPath, final String symbolicName) {
		assertComponentCanBeDeleted(branchPath, symbolicName, SnomedComponentType.MEMBER);
	}
	
	public static List<Map<String, String>> createAttributesMap(ConflictingAttribute attribute) {
		return createAttributesMap(Collections.<ConflictingAttribute>singletonList(attribute));
	}
	
	public static List<Map<String, String>> createAttributesMap(List<ConflictingAttribute> attributes) {
		return FluentIterable.from(attributes)
				.transform(new Function<ConflictingAttribute, Map<String, String>>() {
					@Override
					public Map<String, String> apply(ConflictingAttribute input) {
						return createAttributeMap(input);
					}
				}).toList();
	}

	private static Map<String, String> createAttributeMap(ConflictingAttribute attribute) {
		if (!Strings.isNullOrEmpty(attribute.getValue()) && !Strings.isNullOrEmpty(attribute.getOldValue())) {
			return ImmutableMap.<String, String>builder()
					.put("property", attribute.getProperty())
					.put("value", attribute.getValue())
					.put("oldValue", attribute.getOldValue())
					.build();
		} else if (!Strings.isNullOrEmpty(attribute.getValue())) {
			return ImmutableMap.<String, String>builder()
					.put("property", attribute.getProperty())
					.put("value", attribute.getValue())
					.build();
		} else if (!Strings.isNullOrEmpty(attribute.getOldValue())) {
			return ImmutableMap.<String, String>builder()
					.put("property", attribute.getProperty())
					.put("oldValue", attribute.getValue())
					.build();
		}
		return ImmutableMap.<String, String>builder()
				.put("property", attribute.getProperty())
				.build();
	}
	
	private SnomedMergeApiAssert() {}
	
}
