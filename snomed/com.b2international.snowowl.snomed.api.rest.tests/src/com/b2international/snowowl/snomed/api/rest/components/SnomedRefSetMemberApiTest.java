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
package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.*;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.5
 */
public class SnomedRefSetMemberApiTest extends AbstractSnomedApiTest {

	@Test
	public void getReferenceSetMemberFromNonExistingBranch() throws Exception {
		assertComponentReadWithStatus(BranchPathUtils.createPath("MAIN/nonexistent"), SnomedComponentType.MEMBER, "fake", 404);
	}
	
	@Test
	public void getNonExistingReferenceSetMember() throws Exception {
		assertComponentReadWithStatus(BranchPathUtils.createMainPath(), SnomedComponentType.MEMBER, "123456789", 404);
	}
	
	@Test
	public void createSimpleReferenceSetMemberForConcept() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		// create concept
		final Map<?, ?> conceptReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String createdConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptReq);
		
		// create refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create member
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(Concepts.MODULE_SCT_CORE, createdConceptId, createdRefSetId);
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		assertComponentExists(testBranchPath, SnomedComponentType.MEMBER, memberId);
	}
	
}
