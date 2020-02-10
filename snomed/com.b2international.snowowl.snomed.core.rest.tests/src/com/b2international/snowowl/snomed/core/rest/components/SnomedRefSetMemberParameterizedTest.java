/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP;
import static com.b2international.snowowl.snomed.core.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.core.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.core.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.snomed.core.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.*;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.searchComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRefSetRestRequests.updateRefSetComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRefSetRestRequests.updateRefSetMemberEffectiveTime;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewRefSet;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createRefSetMemberRequestBody;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createReferencedComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.getFirstAllowedReferencedComponentCategory;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.getFirstAllowedReferencedComponentType;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.getFirstMatchingComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.getValidProperties;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.reserveComponentId;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipGetRequestBuilder;
import com.b2international.snowowl.test.commons.rest.BranchBase;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import io.restassured.response.ValidatableResponse;

/**
 * @since 5.7
 */
@RunWith(Parameterized.class)
@BranchBase(isolateTests = false) // run all tests on the same branch so we can reuse the same reference sets through all tests
public class SnomedRefSetMemberParameterizedTest extends AbstractSnomedApiTest {

	private static final List<String> REFERENCED_COMPONENT_TYPES = ImmutableList.of(CONCEPT, DESCRIPTION, RELATIONSHIP);
	
	private static final Map<SnomedRefSetType, String> REFSET_CACHE = Maps.newHashMap();

	// Single CodeSystem for all refset member tests initialized on first access
	private static String CODESYSTEM_SHORTNAME;

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ 	SnomedRefSetType.ASSOCIATION  					},
			{ 	SnomedRefSetType.ATTRIBUTE_VALUE				},
			//  Concrete data type reference sets are tested separately
			{ 	SnomedRefSetType.COMPLEX_MAP					},
			{ 	SnomedRefSetType.COMPLEX_BLOCK_MAP				},
			{ 	SnomedRefSetType.DESCRIPTION_TYPE				},
			{ 	SnomedRefSetType.EXTENDED_MAP					},
			{ 	SnomedRefSetType.LANGUAGE						},
			{ 	SnomedRefSetType.MODULE_DEPENDENCY				},
			//  Query type reference sets are tested separately
			{ 	SnomedRefSetType.SIMPLE							},
			{ 	SnomedRefSetType.SIMPLE_MAP						},
			{ 	SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION	},
			{ 	SnomedRefSetType.OWL_AXIOM				},
			{ 	SnomedRefSetType.OWL_ONTOLOGY			},
			{ 	SnomedRefSetType.MRCM_DOMAIN			},
			{ 	SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN	},
			{ 	SnomedRefSetType.MRCM_ATTRIBUTE_RANGE	},
			{ 	SnomedRefSetType.MRCM_MODULE_SCOPE		},
		});
	}

	private final SnomedRefSetType refSetType;

	public SnomedRefSetMemberParameterizedTest(SnomedRefSetType refSetType) {
		this.refSetType = refSetType;
	}

	@Test
	public void rejectNonExistentRefSetId() throws Exception {
		String refSetId = reserveComponentId(null, ComponentCategory.CONCEPT);
		String componentId = createReferencedComponent(branchPath, refSetType);

		Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, componentId)
				.putAll(getValidProperties(refSetType))
				.put("commitComment", "Created new reference set member with non-existent refSetId")
				.build();

		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(400);
	}

	@Test
	public void rejectNonExistentComponentId() throws Exception {
		String refSetId = createNewRefSet(branchPath, refSetType);
		String componentId = reserveComponentId(null, getFirstAllowedReferencedComponentCategory(refSetType));

		Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, componentId)
				.putAll(getValidProperties(refSetType))
				.put("commitComment", "Created new reference set member with non-existent referencedComponentId")
				.build();

		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(400);
	}

	@Test
	public void rejectMismatchedComponentType() throws Exception {
		String refSetId = getOrCreateRefSet(branchPath, refSetType);
		String referencedComponentType = getFirstAllowedReferencedComponentType(refSetType);

		for (String disallowedComponentType : REFERENCED_COMPONENT_TYPES) {
			if (!referencedComponentType.equals(disallowedComponentType)) {
				String componentId = getFirstMatchingReferencedComponent(branchPath, disallowedComponentType);

				Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, componentId)
						.putAll(getValidProperties(refSetType))
						.put("commitComment", "Created new reference set member with mismatched referencedComponentId")
						.build();

				String expectedMessage = String.format("'%s' reference set can't reference '%s | %s' component. Only '%s' components are allowed.",
						refSetId, 
						componentId, 
						disallowedComponentType, 
						referencedComponentType);

				createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(400)
				.body("message", equalTo(expectedMessage));
			}
		}
	}

	@Test
	public void rejectInvalidProperties() throws Exception {
		// Simple type reference sets can't be tested with this method
		Assume.assumeFalse(SnomedRefSetType.SIMPLE.equals(refSetType));

		String refSetId = getOrCreateRefSet(branchPath, refSetType);
		String componentId = createNewComponent(branchPath, getFirstAllowedReferencedComponentType(refSetType));

		Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, componentId)
				.putAll(getInvalidProperties())
				.put("commitComment", "Created new reference set member with invalid additional properties")
				.build();

		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(400);
	}

	@Test
	public void acceptValidRequest() throws Exception {
		String memberId = createRefSetMember();		

		ValidatableResponse response = getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);
		for (Entry<String, Object> validProperty : getValidProperties(refSetType).entrySet()) {
			response.body(validProperty.getKey(), equalTo(validProperty.getValue()));
		}
	}

	@Test
	public void acceptValidUpdate() throws Exception {
		// Simple type reference sets can't be tested with this method
		Assume.assumeFalse(SnomedRefSetType.SIMPLE.equals(refSetType));

		String memberId = createRefSetMember();		

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.putAll(getUpdateProperties())
				.put("commitComment", "Updated reference set member")
				.build();

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);

		ValidatableResponse response = getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);
		for (Entry<String, Object> validProperty : getUpdateProperties().entrySet()) {
			response.body(validProperty.getKey(), equalTo(validProperty.getValue()));
		}
	}

	@Test
	public void inactivateMember() throws Exception {
		String memberId = createRefSetMember();

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("active", false)
				.put("moduleId", Concepts.MODULE_ROOT)
				.put("commitComment", "Inactivated reference set member and changed module")
				.build();

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
		.body("active", equalTo(false))
		.body("moduleId", equalTo(Concepts.MODULE_ROOT));
	}

	@Test
	public void updateMemberEffectiveTime() throws Exception {
		String memberId = createRefSetMember();
		String effectiveTime = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("effectiveTime", effectiveTime)
				.put("commitComment", "Updated effective time on reference set member without force flag")
				.build();

		// The update goes through, but will have no effect
		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
		.body("effectiveTime", nullValue())
		.body("released", equalTo(false));
	}

	@Test
	public void forceUpdateMemberEffectiveTime() throws Exception {
		String memberId = createRefSetMember();
		Date effectiveTime = getNextAvailableEffectiveDate(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);

		updateRefSetMemberEffectiveTime(branchPath, memberId, effectiveTime);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
		.body("effectiveTime", equalTo(EffectiveTimes.format(effectiveTime, DateFormats.SHORT)))
		.body("released", equalTo(true));
	}

	@Test
	public void deleteMember() throws Exception {
		String memberId = createRefSetMember();
		deleteComponent(branchPath, SnomedComponentType.MEMBER, memberId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(404);
	}

	@Test
	public void deleteReleasedMember() throws Exception {
		String memberId = createRefSetMember();
		Date effectiveTime = getNextAvailableEffectiveDate(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		updateRefSetMemberEffectiveTime(branchPath, memberId, effectiveTime);

		// A published component can not be deleted without the force flag enabled
		deleteComponent(branchPath, SnomedComponentType.MEMBER, memberId, false).statusCode(409);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);
	}

	@Test
	public void forceDeleteReleasedMember() throws Exception {
		String memberId = createRefSetMember();
		Date effectiveTime = getNextAvailableEffectiveDate(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		updateRefSetMemberEffectiveTime(branchPath, memberId, effectiveTime);

		deleteComponent(branchPath, SnomedComponentType.MEMBER, memberId, true).statusCode(204);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(404);
	}
	
	@Test
	public void testRestorationOfEffectiveTimeOnMutablePropertyChange() {
		// XXX skip simple type refsets
		Assume.assumeFalse(SnomedRefSetType.SIMPLE.equals(refSetType));
		
		final String shortName = getOrCreateCodeSystem();

		// create the previous revision of the member
		final String memberId = createRefSetMember();

		final String effectiveTime = getNextAvailableEffectiveDateAsString(shortName);
		createVersion(shortName, effectiveTime, effectiveTime).statusCode(201);

		// update properties
		final Map<?, ?> updateRequest = ImmutableMap.builder()
				.putAll(ImmutableMap.<String, Object>builder().putAll(getUpdateProperties()).build())
				.put("commitComment", "Updated reference set member")
				.build();

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);

		// Updating a member's properties should unset the effective time
		final ValidatableResponse updateResponse = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.body("released", equalTo(true))
				.body("effectiveTime", equalTo(null));

		// check updated properties
		for (Entry<String, Object> updateProperty : getUpdateProperties().entrySet()) {
			updateResponse.body(updateProperty.getKey(), equalTo(updateProperty.getValue()));
		}

		// revert back to original values
		final Map<?, ?> revertUpdateRequest = ImmutableMap.builder()
				.putAll(ImmutableMap.<String, Object>builder().putAll(getValidProperties(refSetType)).build())
				.put("commitComment", "Reverted previous update of reference set member")
				.build();

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, revertUpdateRequest, false).statusCode(204);

		// Getting the member back to its originally released state should restore the effective time
		ValidatableResponse revertResponse = getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveTime));

		for (Entry<String, Object> validProperty : getValidProperties(refSetType).entrySet()) {
			revertResponse.body(validProperty.getKey(), equalTo(validProperty.getValue()));
		}

	}

	private String getOrCreateCodeSystem() {
		if (CODESYSTEM_SHORTNAME == null) {
			// This will create a code system on the branch MAIN/className
			final String shortName = getClass().getSimpleName();
			createCodeSystem(branchPath, shortName).statusCode(201);
			CODESYSTEM_SHORTNAME = shortName;
		}
		return CODESYSTEM_SHORTNAME;
	}

	private String createRefSetMember() {
		String componentId = getFirstMatchingComponent(branchPath, getFirstAllowedReferencedComponentType(refSetType));

		String refSetId = getOrCreateRefSet(branchPath, refSetType);
		Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, componentId)
				.putAll(getValidProperties(refSetType))
				.put("commitComment", "Created new reference set member")
				.build();
		
		ValidatableResponse response = createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(201);
		
		return lastPathSegment(response.extract().header("Location"));
	}

	private String getFirstMatchingReferencedComponent(IBranchPath branchPath, String referencedComponentType) {
		return searchComponent(branchPath, getSnomedComponentType(referencedComponentType), ImmutableMap.of("limit", 1)).extract().jsonPath().<String>getList("items.id").get(0);
	}

	private SnomedComponentType getSnomedComponentType(String referencedComponentType) {
		switch (referencedComponentType) {
		case SnomedTerminologyComponentConstants.CONCEPT:
			return SnomedComponentType.CONCEPT;
		case SnomedTerminologyComponentConstants.DESCRIPTION:
			return SnomedComponentType.DESCRIPTION;
		case SnomedTerminologyComponentConstants.RELATIONSHIP:
			return SnomedComponentType.RELATIONSHIP;
		default:
			throw new UnsupportedOperationException("Not implemented case for: " + referencedComponentType);
		}
	}

	private static String getOrCreateRefSet(IBranchPath branchPath, SnomedRefSetType refSetType) {
		return REFSET_CACHE.computeIfAbsent(refSetType, type -> createNewRefSet(branchPath, type));
	}

	private Map<String, Object> getUpdateProperties() {
		switch (refSetType) {
		case ASSOCIATION:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_TARGET_COMPONENT, ImmutableMap.of("id", Concepts.NAMESPACE_ROOT))
					.build();
		case ATTRIBUTE_VALUE:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_VALUE_ID, Concepts.NAMESPACE_ROOT)
					.build();
		case COMPLEX_MAP:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, "complexMapTarget2")
					.put(SnomedRf2Headers.FIELD_MAP_GROUP, 1)
					.put(SnomedRf2Headers.FIELD_MAP_PRIORITY, 1)
					.put(SnomedRf2Headers.FIELD_MAP_RULE, "complexMapRule2")
					.put(SnomedRf2Headers.FIELD_MAP_ADVICE, "complexMapAdvice2")
					.put(SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.NAMESPACE_ROOT)
					.build();
		case COMPLEX_BLOCK_MAP:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, "complexBlockMapTarget2")
					.put(SnomedRf2Headers.FIELD_MAP_GROUP, 1)
					.put(SnomedRf2Headers.FIELD_MAP_PRIORITY, 1)
					.put(SnomedRf2Headers.FIELD_MAP_RULE, "complexBlockMapRule2")
					.put(SnomedRf2Headers.FIELD_MAP_ADVICE, "complexBlockMapAdvice2")
					.put(SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.NAMESPACE_ROOT)
					.put(SnomedRf2Headers.FIELD_MAP_BLOCK, 2)
					.build();
		case DESCRIPTION_TYPE:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT, Concepts.NAMESPACE_ROOT)
					.put(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 101)
					.build();
		case EXTENDED_MAP:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, "extendedMapTarget2")
					.put(SnomedRf2Headers.FIELD_MAP_GROUP, 11)
					.put(SnomedRf2Headers.FIELD_MAP_PRIORITY, 11)
					.put(SnomedRf2Headers.FIELD_MAP_RULE, "extendedMapRule2")
					.put(SnomedRf2Headers.FIELD_MAP_ADVICE, "extendedMapAdvice2")
					.put(SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.NAMESPACE_ROOT)
					.put(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, Concepts.NAMESPACE_ROOT)
					.build();
		case LANGUAGE:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)
					.build();
		case MODULE_DEPENDENCY:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "20170224")
					.put(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "20170225")
					.build();
		case SIMPLE:
			return ImmutableMap.of();
		case SIMPLE_MAP:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, "simpleMapTarget2")
					.build();
		case SIMPLE_MAP_WITH_DESCRIPTION:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, "mapTarget2")
					.put(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION, "mapTargetDescription2")
					.build();
		case OWL_AXIOM:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, OWL_AXIOM_2)
					.build();
		case OWL_ONTOLOGY:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, OWL_ONTOLOGY_2)
					.build();
		case MRCM_DOMAIN:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, DOMAIN_CONSTRAINT_2)
					.put(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, "") // unset on purpose
					.put(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, PROXIMAL_PRIMITIVE_CONSTRAINT_2)
					.put(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, "") // unset on purpose
					.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, DOMAIN_TEMPLATE_FOR_PRECOORDINATION_2)
					.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, DOMAIN_TEMPLATE_FOR_POSTCOORDINATION_2)
					.put(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE, "") // unset on purpose
					.build();
		case MRCM_ATTRIBUTE_DOMAIN:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, DOMAIN_ID_2)
					.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.FALSE)
					.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, ATTRIBUTE_CARDINALITY_2)
					.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, ATTRIBUTE_IN_GROUP_CARDINALITY_2)
					.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, RULE_STRENGTH_ID_2)
					.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, CONTENT_TYPE_ID_2)
					.build();
		case MRCM_ATTRIBUTE_RANGE:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, RANGE_CONSTRAINT_2)
					.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, ATTRIBUTE_RULE_2)
					.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, RULE_STRENGTH_ID_2)
					.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, CONTENT_TYPE_ID_2)
					.build();
		case MRCM_MODULE_SCOPE:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, RULE_REFSET_ID_2)
					.build();
		default:
			throw new IllegalStateException("Unexpected reference set type '" + refSetType + "'.");
		}
	}

	private Map<String, Object> getInvalidProperties() {
		switch (refSetType) {
		case ASSOCIATION:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_TARGET_COMPONENT, "")
					.build();
		case ATTRIBUTE_VALUE:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_VALUE_ID, "")
					.build();
		case COMPLEX_MAP:
			// Invalid because FIELD_MAP_PRIORITY is not set
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, "complexMapTarget")
					.put(SnomedRf2Headers.FIELD_MAP_GROUP, (byte) 0)
					.put(SnomedRf2Headers.FIELD_MAP_RULE, "complexMapRule")
					.put(SnomedRf2Headers.FIELD_MAP_ADVICE, "complexMapAdvice")
					.put(SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.REFSET_CORRELATION_NOT_SPECIFIED)
					.build();
		case DESCRIPTION_TYPE:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT, "")
					.put(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 100)
					.build();
		case EXTENDED_MAP:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, "")
					.put(SnomedRf2Headers.FIELD_MAP_GROUP, (byte) 10)
					.put(SnomedRf2Headers.FIELD_MAP_PRIORITY, (byte) 10)
					.put(SnomedRf2Headers.FIELD_MAP_RULE, "extendedMapRule")
					.put(SnomedRf2Headers.FIELD_MAP_ADVICE, "extendedMapAdvice")
					.put(SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.REFSET_CORRELATION_NOT_SPECIFIED)
					.put(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, Concepts.MAP_CATEGORY_NOT_CLASSIFIED)
					.build();
		case COMPLEX_BLOCK_MAP:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, "complexBlockMapTarget")
					.put(SnomedRf2Headers.FIELD_MAP_GROUP, 10)
					.put(SnomedRf2Headers.FIELD_MAP_PRIORITY, 10)
					.put(SnomedRf2Headers.FIELD_MAP_RULE, "complexBlockMapRule")
					.put(SnomedRf2Headers.FIELD_MAP_ADVICE, "complexBlockMapAdvice")
					.put(SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.REFSET_CORRELATION_NOT_SPECIFIED)
					.put(SnomedRf2Headers.FIELD_MAP_BLOCK, "not an integer")
					.build();
		case LANGUAGE:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, "")
					.build();
		case MODULE_DEPENDENCY:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "abc")
					.build();
		case SIMPLE_MAP:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, "")
					.build();
		case SIMPLE_MAP_WITH_DESCRIPTION:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MAP_TARGET, "")
					.put(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION, "mapTargetDescription")
					.build();
		case OWL_AXIOM: //$FALL-THROUGH$
		case OWL_ONTOLOGY:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, "")
					.build();
		case MRCM_DOMAIN:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, "")
					.put(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "")
					.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "")
					.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, "")
					.build();
		case MRCM_ATTRIBUTE_DOMAIN:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, "")
					.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, "booleanValue")
					.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "")
					.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "")
					.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, "1234")
					.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, "")
					.build();
		case MRCM_ATTRIBUTE_RANGE:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, "")
					.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, "")
					.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, "")
					.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, "1234")
					.build();
		case MRCM_MODULE_SCOPE:
			return ImmutableMap.<String, Object>builder()
					.put(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, "")
					.build();
		default:
			throw new IllegalStateException("Unexpected reference set type '" + refSetType + "'.");
		}
	}
}
