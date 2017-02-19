/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 5.0
 */
public class SnomedRefSetRestRequests {

	public static ValidatableResponse updateRefSetComponent(IBranchPath branchPath, SnomedComponentType type, String id, Map<?, ?> requestBody, boolean force) {
		assertThat(type, anyOf(equalTo(SnomedComponentType.REFSET), equalTo(SnomedComponentType.MEMBER)));

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.queryParam("force", force)
				.put("/{path}/{componentType}/{id}", branchPath.getPath(), type.toLowerCasePlural(), id)
				.then();
	}

	public static void updateRefSetMemberEffectiveTime(IBranchPath memberPath, String memberId, Date effectiveTime) {
		String effectiveTimeAsString = EffectiveTimes.format(effectiveTime, DateFormats.SHORT);

		Map<?, ?> parentRequest = ImmutableMap.builder()
				.put("effectiveTime", effectiveTimeAsString)
				.put("commitComment", "Updated effective time on reference set member")
				.build();

		updateRefSetComponent(memberPath, SnomedComponentType.MEMBER, memberId, parentRequest, true).statusCode(204);
	}

	private SnomedRefSetRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
