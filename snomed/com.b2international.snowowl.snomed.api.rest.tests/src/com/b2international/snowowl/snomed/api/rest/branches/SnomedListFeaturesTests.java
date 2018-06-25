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
package com.b2international.snowowl.snomed.api.rest.branches;

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewDescription;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRelationship;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createRefSetMemberRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.merge;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;

/**
 * @since 5.10.16
 */
public class SnomedListFeaturesTests extends AbstractSnomedApiTest {
	
	@Test
	public void conceptOutboundRelationshipsListUpdateTest() throws Exception {
		
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String relationshipId = createNewRelationship(a);
		String relationshipId2 = createNewRelationship(a);

		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);

		String relationshipId3 = createNewRelationship(b);
		String relationshipId4 = createNewRelationship(b);
		
		String relationshipIdOnMain = createNewRelationship(branchPath);
		merge(branchPath, a, "Rebased project on MAIN").body("status", equalTo(Merge.Status.COMPLETED.name()));
		
		assertTrue(hasOutboundRelationship(branchPath, Concepts.ROOT_CONCEPT, relationshipIdOnMain));
		assertTrue(hasOutboundRelationship(a, Concepts.ROOT_CONCEPT, relationshipIdOnMain));
		
		assertTrue(hasOutboundRelationship(a, Concepts.ROOT_CONCEPT, relationshipId));
		assertTrue(hasOutboundRelationship(a, Concepts.ROOT_CONCEPT, relationshipId2));

		merge(a, b, "Rebased component updates over deletion").body("status", equalTo(Merge.Status.COMPLETED.name()));

		assertTrue(hasOutboundRelationship(b, Concepts.ROOT_CONCEPT, relationshipId3));
		assertTrue(hasOutboundRelationship(b, Concepts.ROOT_CONCEPT, relationshipId4));
		
		assertEquals(3, getOutboundRelationshipsSize(a, Concepts.ROOT_CONCEPT));
		assertEquals(5, getOutboundRelationshipsSize(b, Concepts.ROOT_CONCEPT));
	}

	@Test
	public void conceptDescriptionsListUpdateTest() throws Exception {
		
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		int numberOfDescriptions = getDescriptionsSize(branchPath, Concepts.ROOT_CONCEPT);
		
		String descriptionIdOnProject = createNewDescription(a);

		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);

		String descriptionIdOnTask = createNewDescription(b);
		
		String descriptionIdOnMain = createNewDescription(branchPath);
		
		merge(branchPath, a, "Rebased project on MAIN").body("status", equalTo(Merge.Status.COMPLETED.name()));
		
		assertTrue(hasDescription(branchPath, Concepts.ROOT_CONCEPT, descriptionIdOnMain));

		assertTrue(hasDescription(a, Concepts.ROOT_CONCEPT, descriptionIdOnProject));
		assertTrue(hasDescription(a, Concepts.ROOT_CONCEPT, descriptionIdOnMain));

		merge(a, b, "Rebased task on project").body("status", equalTo(Merge.Status.COMPLETED.name()));

		assertTrue(hasDescription(b, Concepts.ROOT_CONCEPT, descriptionIdOnProject));
		assertTrue(hasDescription(b, Concepts.ROOT_CONCEPT, descriptionIdOnTask));
		
		assertEquals(numberOfDescriptions + 1, getDescriptionsSize(branchPath, Concepts.ROOT_CONCEPT));
		assertEquals(numberOfDescriptions + 2, getDescriptionsSize(a, Concepts.ROOT_CONCEPT));
		assertEquals(numberOfDescriptions + 3, getDescriptionsSize(b, Concepts.ROOT_CONCEPT));
	}
	
	@Test
	public void descriptionInactivationMemberUpdateTest() throws Exception {
		
		ImmutableMap<String, Object> fields = ImmutableMap.of(SnomedRf2Headers.FIELD_VALUE_ID, Concepts.ROOT_CONCEPT);
		
		testMemberUpdate(createNewDescription(branchPath), Description.class,
				SnomedPackage.Literals.INACTIVATABLE__INACTIVATION_INDICATOR_REF_SET_MEMBERS, Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
				fields);
	}
	
	
	@Test
	public void conceptAssociationMemberUpdateTest() throws Exception {
		
		ImmutableMap<String, Object> fields = ImmutableMap.of(SnomedRf2Headers.FIELD_TARGET_COMPONENT, ImmutableMap.of("id", Concepts.ROOT_CONCEPT));
		
		testMemberUpdate(createNewConcept(branchPath), Concept.class, SnomedPackage.Literals.INACTIVATABLE__ASSOCIATION_REF_SET_MEMBERS,
				Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, fields);
	}
	
	@Test
	public void conceptInactivationMemberUpdateTest() throws Exception {
		
		ImmutableMap<String, Object> fields = ImmutableMap.of(SnomedRf2Headers.FIELD_VALUE_ID, Concepts.ROOT_CONCEPT);
		
		testMemberUpdate(createNewConcept(branchPath), Concept.class,
				SnomedPackage.Literals.INACTIVATABLE__INACTIVATION_INDICATOR_REF_SET_MEMBERS, Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR,
				fields);
	}
	
	@Test
	public void descriptionLanguageRefsetMemberUpdateTest() throws Exception {
		
		String containerId = createNewDescription(branchPath);
		
		String parentConceptId = SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.LANGUAGE);
		Map<?, ?> refSetRequestBody = createConceptRequestBody(parentConceptId)
				.put("type", SnomedRefSetType.LANGUAGE.name())
				.put("referencedComponentType", SnomedTerminologyComponentConstants.DESCRIPTION)
				.put("commitComment", "Created new language reference set")
				.build();

		String newLanguageRefsetId = lastPathSegment(createComponent(branchPath, SnomedComponentType.REFSET, refSetRequestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));

		int numberOfMembers = getComponentListSize(branchPath, containerId, Description.class, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS);
		
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);
		
		Map<?, ?> memberRequestBodyOnProject = createRefSetMemberRequestBody(Concepts.REFSET_LANGUAGE_TYPE_US, containerId)
			.put(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE)
			.put("commitComment", "add refset member on project")
			.build();
		
		String memberIdOnProject = lastPathSegment(createComponent(a, SnomedComponentType.MEMBER, memberRequestBodyOnProject)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		assertEquals(numberOfMembers + 1, getComponentListSize(a, containerId, Description.class, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS));
		assertTrue(hasMember(a, containerId, Description.class, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS, memberIdOnProject));
		
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);
		
		Map<?, ?> memberRequestBodyOnTask = createRefSetMemberRequestBody(newLanguageRefsetId, containerId)
				.put(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE)
				.put("commitComment", "add refset member on task")
				.build();
			
		String memberIdOnTask = lastPathSegment(createComponent(b, SnomedComponentType.MEMBER, memberRequestBodyOnTask)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		assertEquals(numberOfMembers + 2, getComponentListSize(b, containerId, Description.class, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS));
		assertTrue(hasMember(b, containerId, Description.class, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS, memberIdOnTask));
		
		createNewRelationship(branchPath);
		
		merge(branchPath, a, "Rebased project on MAIN").body("status", equalTo(Merge.Status.COMPLETED.name()));
		
		assertTrue(hasMember(a, containerId, Description.class, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS, memberIdOnProject));
		assertEquals(numberOfMembers + 1, getComponentListSize(a, containerId, Description.class, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS));

		merge(a, b, "Rebased task on project").body("status", equalTo(Merge.Status.COMPLETED.name()));
		
		assertTrue(hasMember(b, containerId, Description.class, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS, memberIdOnTask));
		assertTrue(hasMember(b, containerId, Description.class, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS, memberIdOnProject));
		assertEquals(numberOfMembers + 2, getComponentListSize(b, containerId, Description.class, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS));
	}
	
	public <T extends Component> void testMemberUpdate(String containerId, Class<T> clazz, EStructuralFeature feature, String refsetId,
			Map<String, Object> fields) throws Exception {
	
		int numberOfMembers = getComponentListSize(branchPath, containerId, clazz, feature);
		
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);
		
		Map<?, ?> memberRequestBodyOnProject = createRefSetMemberRequestBody(refsetId, containerId)
			.putAll(fields)
			.put("commitComment", "add refset member on project")
			.build();
		
		String memberIdOnProject = lastPathSegment(createComponent(a, SnomedComponentType.MEMBER, memberRequestBodyOnProject)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		assertEquals(numberOfMembers + 1, getComponentListSize(a, containerId, clazz, feature));
		assertTrue(hasMember(a, containerId, clazz, feature, memberIdOnProject));
		
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);
		
		Map<?, ?> memberRequestBodyOnTask = createRefSetMemberRequestBody(refsetId, containerId)
				.putAll(fields)
				.put("commitComment", "add refset member on task")
				.build();
			
		String memberIdOnTask = lastPathSegment(createComponent(b, SnomedComponentType.MEMBER, memberRequestBodyOnTask)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		assertEquals(numberOfMembers + 2, getComponentListSize(b, containerId, clazz, feature));
		assertTrue(hasMember(b, containerId, clazz, feature, memberIdOnTask));
		
		Map<?, ?> memberRequestBodyOnMain = createRefSetMemberRequestBody(refsetId, containerId)
				.putAll(fields)
				.put("commitComment", "add refset member on main")
				.build();
			
		String memberIdOnMain = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequestBodyOnMain)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		merge(branchPath, a, "Rebased project on MAIN").body("status", equalTo(Merge.Status.COMPLETED.name()));
		
		assertTrue(hasMember(a, containerId, clazz, feature, memberIdOnProject));
		assertTrue(hasMember(a, containerId, clazz, feature, memberIdOnMain));
		assertEquals(numberOfMembers + 2, getComponentListSize(a, containerId, clazz, feature));

		merge(a, b, "Rebased task on project").body("status", equalTo(Merge.Status.COMPLETED.name()));
		
		assertTrue(hasMember(b, containerId, clazz, feature, memberIdOnTask));
		assertTrue(hasMember(b, containerId, clazz, feature, memberIdOnProject));
		assertTrue(hasMember(b, containerId, clazz, feature, memberIdOnMain));
		assertEquals(numberOfMembers + 3, getComponentListSize(b, containerId, clazz, feature));
	}
	
	private boolean hasDescription(IBranchPath branchPath, String conceptId, String descriptionId) {
		return hasComponent(branchPath, conceptId, Concept.class, SnomedPackage.Literals.CONCEPT__DESCRIPTIONS, descriptionId);
	}
	
	private boolean hasOutboundRelationship(IBranchPath branchPath, String conceptId, String relationshipId) {
		return hasComponent(branchPath, conceptId, Concept.class, SnomedPackage.Literals.CONCEPT__OUTBOUND_RELATIONSHIPS, relationshipId);
	}
	
	private int getOutboundRelationshipsSize(IBranchPath branchPath, String conceptId) {
		return getComponentListSize(branchPath, conceptId, Concept.class, SnomedPackage.Literals.CONCEPT__OUTBOUND_RELATIONSHIPS);
	}
	
	private int getDescriptionsSize(IBranchPath branchPath, String conceptId) {
		return getComponentListSize(branchPath, conceptId, Concept.class, SnomedPackage.Literals.CONCEPT__DESCRIPTIONS);
	}
	
	private <T extends Component> int getComponentListSize(IBranchPath branchPath, String containerId, Class<T> clazz, EStructuralFeature feature) {
		try (SnomedEditingContext ctx = new SnomedEditingContext(branchPath)) {
			T container = ctx.lookup(containerId, clazz);
			Object list = container.eGet(feature);
			if (list instanceof EList<?>) {
				EList<?> featureList = (EList<?>) list;
				return featureList.size();
			}
		}
		return -1;
	}
	
	private <T extends Component> boolean hasComponent(IBranchPath branchPath, String containerId, Class<T> clazz, EStructuralFeature feature, String listElementId) {
		try (SnomedEditingContext ctx = new SnomedEditingContext(branchPath)) {
			T container = ctx.lookup(containerId, clazz);
			Object list = container.eGet(feature);
			if (list instanceof EList<?>) {
				EList<?> featureList = (EList<?>) list;
				return featureList.stream()
						.filter( element -> element instanceof Component)
						.map( c -> (Component) c)
						.anyMatch(c -> c.getId().equals(listElementId));
			}
			return false;
		}
	}
	
	private <T extends Component> boolean hasMember(IBranchPath branchPath, String containerId, Class<T> clazz, EStructuralFeature feature, String listElementId) {
		try (SnomedEditingContext ctx = new SnomedEditingContext(branchPath)) {
			T container = ctx.lookup(containerId, clazz);
			Object list = container.eGet(feature);
			if (list instanceof EList<?>) {
				EList<?> featureList = (EList<?>) list;
				return featureList.stream()
						.filter( element -> element instanceof SnomedRefSetMember)
						.map( c -> (SnomedRefSetMember) c)
						.anyMatch(c -> c.getUuid().equals(listElementId));
			}
			return false;
		}
	}
	
}
