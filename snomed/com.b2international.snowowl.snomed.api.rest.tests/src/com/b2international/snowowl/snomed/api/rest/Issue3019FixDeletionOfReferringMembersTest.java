/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRefSet;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;
/**
 * @since 6.5
 */
public class Issue3019FixDeletionOfReferringMembersTest extends AbstractSnomedApiTest {

	@Test
	public void testDeletionOfRefsetMember() {
		String queryRefsetId = createNewRefSet(branchPath, SnomedRefSetType.QUERY);
		String queryMemberRefsetId = createNewRefSet(branchPath);
		
		final Map<?, ?> memberRequest = ImmutableMap.<String, Object>builder()
				.put(SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", queryRefsetId)
				.put(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, queryMemberRefsetId)
				.put(SnomedRf2Headers.FIELD_QUERY, "<" + Concepts.REFSET_ROOT_CONCEPT)
				.put("commitComment", "Created new query reference set member")
				.build();
		
		final String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));
		
		// delete query refset member's corresponding refset
		SnomedRequests.prepareDeleteConcept(queryMemberRefsetId)
		.build(
				SnomedDatastoreActivator.REPOSITORY_UUID,
				branchPath.getPath(),
				"info@b2international.com", 
				"Deleted reference set which was member of query type refset"
			  )
		.execute(getBus())
		.getSync();

		// check if refset was deleted after request
		getComponent(branchPath, SnomedComponentType.REFSET, queryMemberRefsetId).statusCode(404);
		
		// check if member is deleted after deleting the refset
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(404);
	}
	
}
