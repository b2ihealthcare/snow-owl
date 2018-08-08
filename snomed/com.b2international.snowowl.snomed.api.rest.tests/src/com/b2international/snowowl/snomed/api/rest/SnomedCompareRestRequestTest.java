/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.SCT_API;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.compare.CompareResult;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
/**
 * @since 7.0
 */
public class SnomedCompareRestRequestTest extends AbstractSnomedApiTest {
	
	private IBranchPath parentBranch;
	private IEventBus bus;

	private String repositoryUuid;
	
	private ImmutableMap<String, Object> createCompareRequest(IBranchPath base, IBranchPath compareBranch) {
		return createCompareRequest(base.toString(), compareBranch.toString());
	}
	
	private ImmutableMap<String, Object> createCompareRequest(String base, String compareBranch) {
		return ImmutableMap.<String, Object>builder()
				.put("baseBranch", base)
				.put("compareBranch", compareBranch)
				.build();
	}
	
	@Before
	public void setup() {
		parentBranch = BranchPathUtils.createPath("MAIN/SnomedCompareRestRequestTest");
		bus = ApplicationContext.getServiceForClass(IEventBus.class);
		repositoryUuid = SnomedDatastoreActivator.REPOSITORY_UUID;
	}
	
	@Test
	public void testCompareOfSameBranch() throws IOException {
		final CompareResult compareResult = getCompareResult(branchPath.getPath(), parentBranch.getPath());
		
		assertThat(compareResult.getBaseBranch().equals(branchPath.toString()));
		assertThat(compareResult.getCompareBranch().equals(branchPath.toString()));
		assertThat(compareResult.getChangedComponents().isEmpty());
		assertThat(compareResult.getDeletedComponents().isEmpty());
		assertThat(compareResult.getNewComponents().isEmpty());
	}
	
	@Test
	public void testCompareOfNonExistantBranch() {
		final ImmutableMap<String, Object> compareRequest = createCompareRequest(branchPath, BranchPathUtils.createPath("MAIN/Branch/does/not/exist"));
		
		final Response compareResponse = givenAuthenticatedRequest(SCT_API)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(compareRequest)
				.post("/compare")
				.thenReturn();
		
		final int statusCode = compareResponse.statusCode();
		assertEquals(404, statusCode);
	}
	
	@Test
	public void testCompareWithChangedNewComponents() throws IOException {
		final String newConceptId = createNewConcept(branchPath);
		
		final CompareResult compareResult = getCompareResult(parentBranch.getPath(), branchPath.getPath());
		final Set<ComponentIdentifier> newIds = prepareNewChanges(newConceptId, branchPath);
		assertThat(compareResult.getNewComponents()).containsAll(newIds);
		assertThat(compareResult.getChangedComponents()).doesNotContainAnyElementsOf(newIds);
		assertThat(compareResult.getDeletedComponents()).isEmpty();
	}
	
	@Test
	public void testCompareWithChangedComponents() throws IOException {
		final String newConceptId = createNewConcept(parentBranch);
		final SnomedConcept concept = getComponent(parentBranch, SnomedComponentType.CONCEPT, newConceptId).extract().as(SnomedConcept.class);
		
		final String parentBranchPath = parentBranch.toString();
		final String childBranchPath =  createBranch(parentBranchPath, "SNOMEDCT-CHANGED");
		
		SnomedRequests.prepareUpdateConcept(concept.getId())
			.setModuleId(Concepts.MODULE_SCT_MODEL_COMPONENT)
			.build(repositoryUuid, childBranchPath, "info@b2international.com", "Change module ID")
			.execute(bus)
			.getSync();
		
		final CompareResult compareResult = getCompareResult(parentBranchPath, childBranchPath);
		// compare child branch with it's parent
		assertThat(compareResult.getNewComponents()).isEmpty();
		assertThat(compareResult.getChangedComponents()).contains(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, newConceptId));
		assertThat(compareResult.getDeletedComponents()).isEmpty();
	}
	
	@Test
	public void testCompareWithDeletedComponents() throws Exception {
		final String newConceptId = createNewConcept(parentBranch);
		final String parentBranchPath = parentBranch.toString();
		final String childBranchPath =  createBranch(parentBranchPath, "SNOMEDCT-DELETED");
		
		final Set<ComponentIdentifier> newIds = prepareNewChanges(newConceptId, parentBranch);
		final ComponentIdentifier concept = newIds.stream()
				.filter(ci -> ci.getTerminologyComponentId() == SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.findFirst()
				.get();
		
		SnomedRequests.prepareDeleteConcept(concept.getComponentId())
			.build(repositoryUuid, childBranchPath, "info@b2international.com", "Delete concept on child branch")
			.execute(bus)
			.getSync();
		
		final CompareResult compareResult = getCompareResult(parentBranchPath, childBranchPath);
		
		// compare task branch and its parent
		assertThat(compareResult.getNewComponents()).isEmpty();
		assertThat(compareResult.getChangedComponents()).isEmpty();
		assertThat(compareResult.getDeletedComponents()).containsAll(newIds);
	}

	private CompareResult getCompareResult(final String parentBranchPath, final String childBranchPath) throws IOException {
		final ImmutableMap<String, Object> compareRequest = createCompareRequest(parentBranchPath, childBranchPath);
		final InputStream responseInputStream = givenAuthenticatedRequest(SCT_API)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(compareRequest)
				.post("/compare")
				.asInputStream();
		
		final ObjectMapper mapper = new ObjectMapper();
		final CompareResult compareResult = mapper.readValue(responseInputStream, CompareResult.class);
		return compareResult;
	}
	
	private String createBranch(String parent, String name) {
		return RepositoryRequests.branching().prepareCreate()
				.setParent(parent)
				.setName(name)
				.build(repositoryUuid)
				.execute(bus)
				.getSync();
	}
	
	private Set<ComponentIdentifier> prepareNewChanges(String conceptId, IBranchPath branchPath) {
		
		final SnomedConcept concept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "descriptions(expand(members())),relationships()")
			.extract().as(SnomedConcept.class);
		final Set<ComponentIdentifier> newIds = newHashSet();
		newIds.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, concept.getId()));
		for (SnomedDescription description : concept.getDescriptions()) {
			newIds.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, description.getId()));
			for (SnomedReferenceSetMember member : description.getMembers()) {
				newIds.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, member.getId()));
			}
		}
		for (SnomedRelationship relationship : concept.getRelationships()) {
			newIds.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship.getId()));
		}
		return newIds;
	}
	
}
