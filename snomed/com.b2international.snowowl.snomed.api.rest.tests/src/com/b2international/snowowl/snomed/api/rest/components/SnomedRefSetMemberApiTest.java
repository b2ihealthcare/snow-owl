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
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCanBeDeleted;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentNotCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentNotExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentReadWithStatus;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.createRefSetMemberRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.createRefSetRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;

import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;

/**
 * TODO try to create a member with invalid refcompid
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
	public void cannotCreateMemberWithoutReferenceSet() throws Exception {
		givenBranchWithPath(testBranchPath);
		// try to create member
		final String referenceSetId = SnomedIdentifiers.generateConceptId();
		final String referencedComponentId = SnomedIdentifiers.generateDescriptionId();
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(referencedComponentId, referenceSetId);
		assertComponentNotCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
	}
	
	@Test
	public void cannotCreateSimpleMemberWithoutReferencedComponentId() throws Exception {
		givenBranchWithPath(testBranchPath);
		// create refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		// try to create member
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(null, createdRefSetId);
		assertComponentNotCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
	}
	
	@Test
	public void cannotCreateSimpleMemberForDescriptionInConceptBasedRefSet() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// try to create member
		final String referencedComponentId = SnomedIdentifiers.generateDescriptionId();
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(referencedComponentId, createdRefSetId);
		assertComponentNotCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq).and().body("message",
				CoreMatchers.equalTo(String.format("'%s' reference set can't reference '%s | %s' component. Only '%s' components are allowed.",
						createdRefSetId, referencedComponentId, SnomedTerminologyComponentConstants.DESCRIPTION, SnomedTerminologyComponentConstants.CONCEPT)));
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
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(createdConceptId, createdRefSetId);
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		assertComponentExists(testBranchPath, SnomedComponentType.MEMBER, memberId);
	}
	
	@Test
	public void cannotCreateQueryTypeReferenceSetMemberWithReferencedComponentId() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create query type refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.QUERY, SnomedTerminologyComponentConstants.REFSET, Concepts.REFSET_QUERY_SPECIFICATION_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create query type reference set member specify the ID
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(SnomedIdentifiers.generateConceptId(), createdRefSetId);
		assertComponentNotCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq).and().body("message",
				CoreMatchers.equalTo(
						String.format("'%s' type reference set members can't reference components manually, specify a '%s' property instead.",
								SnomedRefSetType.QUERY, SnomedRf2Headers.FIELD_QUERY)));
	}
	
	@Test
	public void cannotCreateQueryTypeReferenceSetMemberWithoutQuery() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create query type refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.QUERY, SnomedTerminologyComponentConstants.REFSET, Concepts.REFSET_QUERY_SPECIFICATION_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create query type reference set member without query
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(null, createdRefSetId);
		assertComponentNotCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq).and().body("message",
				CoreMatchers.equalTo(
						String.format("'%s' cannot be null or empty for '%s' type reference sets.",
								SnomedRf2Headers.FIELD_QUERY, SnomedRefSetType.QUERY)));
	}
	
	@Test
	public void createQueryTypeReferenceSetMember() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		// create query type refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.QUERY, SnomedTerminologyComponentConstants.REFSET, Concepts.REFSET_QUERY_SPECIFICATION_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create query type reference set member with query (ALL characteristic types)
		final String query = "<"+Concepts.CHARACTERISTIC_TYPE;
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(Concepts.MODULE_SCT_CORE, null, createdRefSetId, ImmutableMap.<String, Object>of(SnomedRf2Headers.FIELD_QUERY, query));
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		assertComponentExists(testBranchPath, SnomedComponentType.MEMBER, memberId)
			.and().body("query", CoreMatchers.equalTo(query));
	}
	
	@Test
	public void deleteSimpleReferenceSetMember() throws Exception {
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
		final Map<String, Object> memberReq = createRefSetMemberRequestBody(createdConceptId, createdRefSetId);
		final String memberId = assertComponentCreated(testBranchPath, SnomedComponentType.MEMBER, memberReq);
		assertComponentExists(testBranchPath, SnomedComponentType.MEMBER, memberId);
		
		assertComponentCanBeDeleted(testBranchPath, SnomedComponentType.MEMBER, memberId);
		assertComponentNotExists(testBranchPath, SnomedComponentType.MEMBER, memberId);
	}
	
}
