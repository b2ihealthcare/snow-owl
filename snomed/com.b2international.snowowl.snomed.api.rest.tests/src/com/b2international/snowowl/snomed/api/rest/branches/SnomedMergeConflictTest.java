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
package com.b2international.snowowl.snomed.api.rest.branches;

import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.SCT_API;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCanBeMerged;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertMergeJobFails;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedMergeApiAssert.*;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetApiAssert.updateMemberEffectiveTime;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.jayway.restassured.response.Response;

/**
 * @since 4.7
 */
public class SnomedMergeConflictTest extends AbstractSnomedApiTest {

	/*
	 * The test branch path is MAIN/<random UUID>/merge-conflict-test. Tests should affect <random UUID> and merge-conflict-test branches
	 */
	@Override
	public void setup() {
		super.setup();
		givenBranchWithPath(testBranchPath);
	}

	@Override
	protected IBranchPath createRandomBranchPath() {
		final IBranchPath parentBranch = super.createRandomBranchPath();
		givenBranchWithPath(parentBranch);
		return BranchPathUtils.createPath(parentBranch, "merge-conflict-test");
	}

	@Test
	public void changedInSourceAndTargetMergeConflict() {
		assertDescriptionCreated(testBranchPath, "D100", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D100");

		assertBranchCanBeMerged(testBranchPath, "Merge new description");

		assertDescriptionExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");

		final Map<?, ?> changesOnParent = ImmutableMap.builder().put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("commitComment", "Changed case significance on parent").build();

		assertDescriptionCanBeUpdated(testBranchPath.getParent(), "D100", changesOnParent);

		final Map<?, ?> changesOnBranch = ImmutableMap.builder().put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("commitComment", "Changed case significance on branch").build();

		assertDescriptionCanBeUpdated(testBranchPath, "D100", changesOnBranch);

		Response mergeResponse = assertMergeJobFails(testBranchPath.getParent(), testBranchPath, "commit");

		Map<String, Map<String, Object>> additionalInfos = mergeResponse.then().extract().path("apiError.additionalInfo");

		assertEquals(1, additionalInfos.size());

		Map<String, Object> additionalInfo = Iterables.getOnlyElement(additionalInfos.values());

		assertThat(getProperty(additionalInfo, "sourceType", String.class), allOf(notNullValue(), is("Description")));
		assertThat(getProperty(additionalInfo, "targetType", String.class), allOf(notNullValue(), is("Description")));
		assertThat(getProperty(additionalInfo, "sourceId", String.class), allOf(notNullValue(), is(symbolicNameMap.get("D100"))));
		assertThat(getProperty(additionalInfo, "targetId", String.class), allOf(notNullValue(), is(symbolicNameMap.get("D100"))));

		Collection<String> sourceFeatures = getMultiValueProperty(additionalInfo, "changedSourceFeatures", String.class);
		assertEquals(1, sourceFeatures.size());
		assertThat(sourceFeatures, hasItem("caseSignificance"));

		Collection<String> targetFeatures = getMultiValueProperty(additionalInfo, "changedTargetFeatures", String.class);
		assertEquals(1, targetFeatures.size());
		assertThat(targetFeatures, hasItem("caseSignificance"));
	}

	@Test
	public void changedInSourceDetachedInTargetMergeConflict() {

		setup();

		assertRefsetMemberCreated(testBranchPath, "M1");

		assertRefSetMemberExists(testBranchPath, "M1");
		assertRefSetMemberNotExists(testBranchPath.getParent(), "M1");

		assertBranchCanBeMerged(testBranchPath, "merge branch");

		assertRefSetMemberExists(testBranchPath, "M1");
		assertRefSetMemberExists(testBranchPath.getParent(), "M1");

		givenAuthenticatedRequest(SCT_API).when().get("{path}/members/{memberId}", testBranchPath.getParent().getPath(), symbolicNameMap.get("M1"))
				.then().assertThat().body("effectiveTime", nullValue()).body("released", equalTo(false));

		String effectiveTime = EffectiveTimes.format(new Date(), DateFormats.SHORT);

		updateMemberEffectiveTime(testBranchPath.getParent(), symbolicNameMap.get("M1"), effectiveTime, true);

		givenAuthenticatedRequest(SCT_API).when().get("{path}/members/{memberId}", testBranchPath.getParent().getPath(), symbolicNameMap.get("M1"))
				.then().assertThat().body("effectiveTime", equalTo(effectiveTime)).body("released", equalTo(true));

		assertRefSetMemberCanBeDeleted(testBranchPath, "M1");

		assertRefSetMemberExists(testBranchPath.getParent(), "M1");
		assertRefSetMemberNotExists(testBranchPath, "M1");

		Response mergeResponse = assertMergeJobFails(testBranchPath.getParent(), testBranchPath, "commit");

		Map<String, Map<String, Object>> additionalInfos = mergeResponse.then().extract().path("apiError.additionalInfo");

		assertEquals(1, additionalInfos.size());

		Map<String, Object> additionalInfo = Iterables.getOnlyElement(additionalInfos.values());

		assertThat(getProperty(additionalInfo, "sourceType", String.class), allOf(notNullValue(), is("SnomedRefSetMember")));
		assertThat(getProperty(additionalInfo, "targetType", String.class), nullValue());
		assertThat(getProperty(additionalInfo, "sourceId", String.class), allOf(notNullValue(), is(symbolicNameMap.get("M1"))));
		assertThat(getProperty(additionalInfo, "targetId", String.class), nullValue());

		Collection<String> sourceFeatures = getMultiValueProperty(additionalInfo, "changedSourceFeatures", String.class);
		assertEquals(2, sourceFeatures.size());
		assertThat(sourceFeatures, allOf(hasItem("released"), hasItem("effectiveTime")));

		Collection<String> targetFeatures = getMultiValueProperty(additionalInfo, "changedTargetFeatures", String.class);
		assertTrue(targetFeatures.isEmpty());
	}

	@Test
	public void changedInSourceDetachedInTargetNoMergeConflict() {

		setup();

		assertDescriptionCreated(testBranchPath, "D100", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D100");

		assertBranchCanBeMerged(testBranchPath, "Merge new description into parent branch");

		assertDescriptionExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");

		assertDescriptionProperty(testBranchPath.getParent(), symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.name());

		final Map<?, ?> changesOnParent = ImmutableMap.builder().put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("commitComment", "Changed case significance on parent").build();

		assertDescriptionCanBeUpdated(testBranchPath.getParent(), "D100", changesOnParent);

		assertDescriptionProperty(testBranchPath.getParent(), symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.CASE_INSENSITIVE.name());

		assertDescriptionCanBeDeleted(testBranchPath, "D100");

		assertDescriptionNotExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");

		assertBranchCanBeMerged(testBranchPath.getParent(), testBranchPath, "commit");

		assertDescriptionNotExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");
	}
	
	@Test
	public void changedInTargetDetachedInSourceMergeConflict() {
		
		setup();
		
		assertDescriptionCreated(testBranchPath, "D100", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D100");

		assertBranchCanBeMerged(testBranchPath, "Merge new description into parent branch");

		assertDescriptionExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");
		
		assertDescriptionProperty(testBranchPath, symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.name());
		
		final Map<?, ?> changesOnBranch = ImmutableMap.builder().put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("commitComment", "Changed case significance on branch").build();

		assertDescriptionCanBeUpdated(testBranchPath, "D100", changesOnBranch);
		
		assertDescriptionProperty(testBranchPath, symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.name());
	
		assertDescriptionCanBeDeleted(testBranchPath.getParent(), "D100");
		
		assertDescriptionNotExists(testBranchPath.getParent(), "D100");
		assertDescriptionExists(testBranchPath, "D100");
		
		Response mergeResponse = assertMergeJobFails(testBranchPath.getParent(), testBranchPath, "merge");
		
		Map<String, Map<String, Object>> additionalInfos = mergeResponse.then().extract().path("apiError.additionalInfo");

		assertEquals(1, additionalInfos.size());

		Map<String, Object> additionalInfo = Iterables.getOnlyElement(additionalInfos.values());

		assertThat(getProperty(additionalInfo, "sourceType", String.class), nullValue());
		assertThat(getProperty(additionalInfo, "targetType", String.class), allOf(notNullValue(), is("Description")));
		assertThat(getProperty(additionalInfo, "sourceId", String.class), nullValue());
		assertThat(getProperty(additionalInfo, "targetId", String.class), allOf(notNullValue(), is(symbolicNameMap.get("D100"))));

		Collection<String> sourceFeatures = getMultiValueProperty(additionalInfo, "changedSourceFeatures", String.class);
		assertTrue(sourceFeatures.isEmpty());

		Collection<String> targetFeatures = getMultiValueProperty(additionalInfo, "changedTargetFeatures", String.class);
		assertEquals(1, targetFeatures.size());
		assertThat(targetFeatures, hasItem("caseSignificance"));
	}
	
	@Test
	public void addedInSourceAndTargetConflict() {
		
		assertDescriptionCreated(testBranchPath, "D200", ACCEPTABLE_ACCEPTABILITY_MAP);
		
		String descriptionId = symbolicNameMap.get("D200");
		
		assertDescriptionExists(testBranchPath, "D200");
		assertDescriptionNotExists(testBranchPath.getParent(), "D200");
		
		assertDescriptionCreatedWithId(testBranchPath.getParent(), "D300", descriptionId, ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath.getParent(), "D300");

		assertEquals(descriptionId, symbolicNameMap.get("D300"));
		
		Response mergeResponse = assertMergeJobFails(testBranchPath.getParent(), testBranchPath, "commit");
		
		Map<String, Map<String, Object>> additionalInfos = mergeResponse.then().extract().path("apiError.additionalInfo");

		assertEquals(1, additionalInfos.size());

		Map<String, Object> additionalInfo = Iterables.getOnlyElement(additionalInfos.values());

		assertThat(getProperty(additionalInfo, "sourceType", String.class), allOf(notNullValue(), is("Description")));
		assertThat(getProperty(additionalInfo, "targetType", String.class), allOf(notNullValue(), is("Description")));
		assertThat(getProperty(additionalInfo, "sourceId", String.class), allOf(notNullValue(), is(descriptionId)));
		assertThat(getProperty(additionalInfo, "targetId", String.class), nullValue());

		Collection<String> sourceFeatures = getMultiValueProperty(additionalInfo, "changedSourceFeatures", String.class);
		assertTrue(sourceFeatures.isEmpty());

		Collection<String> targetFeatures = getMultiValueProperty(additionalInfo, "changedTargetFeatures", String.class);
		assertTrue(targetFeatures.isEmpty());
	}

	private <T> T getProperty(Map<String, Object> additionalInfo, String propertyName, Class<T> type) {
		if (additionalInfo.containsKey(propertyName)) {
			Object property = additionalInfo.get(propertyName);
			if (type.isInstance(property)) {
				return type.cast(property);
			}
		}
		return null;
	}

	private <T> Collection<T> getMultiValueProperty(Map<String, Object> additionalInfo, String propertyName, Class<T> type) {
		if (additionalInfo.containsKey(propertyName)) {
			List<T> results = newArrayList();
			Object property = additionalInfo.get(propertyName);
			if (property instanceof Collection) {
				Collection<?> collection = (Collection<?>) property;
				for (Object object : collection) {
					if (type.isInstance(object)) {
						results.add(type.cast(object));
					}
				}
			}
			return results;
		}
		return emptyList();
	}
}
