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

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.createRefSetMemberRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.createRefSetRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;

/**
 * @since 4.7
 */
public class SnomedRefSetApiAssert {

	public static String createSimpleConceptReferenceSetMember(IBranchPath branchPath) {
		return createSimpleConceptReferenceSetMember(branchPath, null);
	}
	
	public static String createSimpleConceptReferenceSetMember(IBranchPath branchPath, String referencedComponentId) {
		
		String conceptId = referencedComponentId;
		
		if (Strings.isNullOrEmpty(referencedComponentId)) {
			// create concept ref. component
			final Map<?, ?> conceptReq = givenConceptRequestBody(referencedComponentId, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
			conceptId = assertComponentCreated(branchPath, SnomedComponentType.CONCEPT, conceptReq);
		}
		
		// create refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(branchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(branchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create member
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(conceptId, createdRefSetId);
		return assertComponentCreated(branchPath, SnomedComponentType.MEMBER, memberReq);
	}
	
	public static void updateMemberEffectiveTime(final IBranchPath branchPath, final String memberId, final String effectiveTime, boolean force) {
		final Map<?, ?> effectiveTimeUpdate = ImmutableMap.of("effectiveTime", effectiveTime, "commitComment", "Update member effective time: " + memberId);
		// without force flag API responds with 204, but the content remains the same
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.with().contentType(ContentType.JSON)
			.and().body(effectiveTimeUpdate)
			.when().put("/{path}/{componentType}/{id}?force="+force, branchPath.getPath(), SnomedComponentType.MEMBER.toLowerCasePlural(), memberId)
			.then().log().ifValidationFails()
			.statusCode(204);
	}
	
}
