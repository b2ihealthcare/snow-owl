/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createConceptRequestBody;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getVersion;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;
import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Maps;

public class SnomedModuleDependencyRefsetTest extends AbstractSnomedApiTest {

	private static final String ICD_10_MAPPING_MODULE = "449080006";
	private static final String NORWEGIAN_MODULE_CONCEPT_ID = "51000202101";
	
	@Test
	public void updateExistingModuleDependencyMembers() {

		IBranchPath mainPath = BranchPathUtils.createMainPath();

		Json conceptRequestBody = createConceptRequestBody(Concepts.MODULE_ROOT, Concepts.MODULE_SCT_CORE, // it must belong to the core module
				SnomedApiTestConstants.UK_PREFERRED_MAP)
				.with("commitComment", "Created concept with INT core module");

		String conceptId = assertCreated(createComponent(mainPath, SnomedComponentType.CONCEPT, conceptRequestBody));

		getComponent(mainPath, SnomedComponentType.CONCEPT, conceptId)
			.statusCode(200)
			.body("effectiveTime", nullValue());

		// version branch
		final String versionEffectiveTime = getNextAvailableEffectiveDateAsString(
				SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		
		final LocalDate	versionEffectiveDate = EffectiveTimes.parse(versionEffectiveTime, DateFormats.SHORT);
		
		final String versionId = "updateExistingModuleDependencyMembers";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, versionEffectiveTime).statusCode(201);
		getVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId).statusCode(200);

		getComponent(mainPath, SnomedComponentType.CONCEPT, conceptId)
			.statusCode(200)
			.body("effectiveTime", equalTo(versionEffectiveTime));
		
		SnomedReferenceSetMembers coreModuleDependencyMembersAfterVersioning = SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.filterByModule(Concepts.MODULE_SCT_CORE) // filter for members where the module id is the SCT core module
				.filterByRefSet(Concepts.REFSET_MODULE_DEPENDENCY_TYPE)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, mainPath.getPath())
				.execute(getBus())
				.getSync();
		
		for (SnomedReferenceSetMember member : coreModuleDependencyMembersAfterVersioning) {
			assertEquals(versionEffectiveDate, member.getEffectiveTime());
		}
		
	}
	
	@Test
	public void updateRelevantModDepMembersForExtensionVersions() {

		final String shortName = "SNOMEDCT-MODULEDEPENDENCY";
		createCodeSystem(branchPath, shortName).statusCode(201);

		Set<String> INT_MODULE_IDS = Set.of(Concepts.MODULE_SCT_MODEL_COMPONENT, Concepts.MODULE_SCT_CORE, ICD_10_MAPPING_MODULE);
		Map<Pair<String, String>, LocalDate> moduleToReferencedComponentAndEffectiveDateMap = Maps.newHashMap();

		SnomedReferenceSetMembers intModuleDependencyMembers = SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.filterByRefSet(Concepts.REFSET_MODULE_DEPENDENCY_TYPE)
				.filterByModules(INT_MODULE_IDS)
				.filterByReferencedComponent(INT_MODULE_IDS)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();

		intModuleDependencyMembers.forEach(member -> {
			Pair<String, String> pair = Tuples.pair(member.getModuleId(), member.getReferencedComponent().getId());
			moduleToReferencedComponentAndEffectiveDateMap.put(
				pair,
				member.getEffectiveTime()
			);
		});
		
		// create Norwegian module
		Json norwegianModuleRequestBody = createConceptRequestBody(Concepts.MODULE_ROOT, NORWEGIAN_MODULE_CONCEPT_ID, SnomedApiTestConstants.UK_PREFERRED_MAP)
				.with("id", NORWEGIAN_MODULE_CONCEPT_ID)
				.with("commitComment", "Created norwegian module concept");
		createComponent(branchPath, SnomedComponentType.CONCEPT, norwegianModuleRequestBody).statusCode(201);
		
		// create both inferred and stated relationships
		Json inferredRelationshipRequestBody = SnomedRestFixtures
				.createRelationshipRequestBody(NORWEGIAN_MODULE_CONCEPT_ID, Concepts.IS_A, Concepts.MODULE_ROOT, NORWEGIAN_MODULE_CONCEPT_ID, Concepts.INFERRED_RELATIONSHIP, 0)
				.with("commitComment", "Created inferred is_a from the norwegian module concept to SCT_MODULE_CORE");
		
		Json statedRelationshipRequestBody = SnomedRestFixtures
				.createRelationshipRequestBody(NORWEGIAN_MODULE_CONCEPT_ID, Concepts.IS_A, Concepts.MODULE_ROOT, NORWEGIAN_MODULE_CONCEPT_ID, Concepts.STATED_RELATIONSHIP, 0)
				.with("commitComment", "Created state is_a from the norwegian module concept to SCT_MODULE_CORE");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, inferredRelationshipRequestBody).statusCode(201);
		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, statedRelationshipRequestBody).statusCode(201);
		
		// check for the newly created module concept to have effective set to null
		SnomedRequests.prepareSearchConcept()
			.filterById(NORWEGIAN_MODULE_CONCEPT_ID)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync()
			.forEach(c -> assertEquals("Effective time must still be null", null, c.getEffectiveTime()));
				
		// version branch
		final String effectiveTime = getNextAvailableEffectiveDateAsString(shortName);
		final LocalDate effectiveDate = EffectiveTimes.parse(effectiveTime, DateFormats.SHORT);
		final String versionId = "testForModuleDependencyMembers";
		createVersion(shortName, versionId, effectiveTime).statusCode(201);
		getVersion(shortName, versionId).statusCode(200);
		
		// check for the newly created module concept after versioning to have effectiveTime set to the correct date
		SnomedConcept norwegianModule = SnomedRequests.prepareGetConcept(NORWEGIAN_MODULE_CONCEPT_ID)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync();
		assertEquals("Effective time should have been set to the date of versioning", effectiveDate, norwegianModule.getEffectiveTime());
		
		SnomedReferenceSetMembers moduleDependencyMembersAfterVersioning = SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.filterByRefSet(Concepts.REFSET_MODULE_DEPENDENCY_TYPE)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();

		moduleDependencyMembersAfterVersioning.forEach(member-> {
			final Pair<String, String> pair = Tuples.pair(member.getModuleId(), member.getReferencedComponent().getId());
			final LocalDate originalMemberEffectiveTime = moduleToReferencedComponentAndEffectiveDateMap.get(pair);
			if (originalMemberEffectiveTime != null) {
				assertEquals(String.format("Effective dates on unaffected existing module dependency members shouldn't be updated after versioning. ModuleID: %s", member.getReferencedComponentId()), originalMemberEffectiveTime,  member.getEffectiveTime());
			} else {
				assertEquals("The new members effective time should match the versionDate", effectiveDate, member.getEffectiveTime());
			}

		});
	}
	
}
