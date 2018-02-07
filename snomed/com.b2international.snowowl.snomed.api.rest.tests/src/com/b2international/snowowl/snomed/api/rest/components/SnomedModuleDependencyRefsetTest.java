package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getVersion;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createConceptRequestBody;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Maps;

public class SnomedModuleDependencyRefsetTest extends AbstractSnomedApiTest {

	private static final String NORWEGIAN_MODULE_CONCEPT_ID = "51000202101";
	
	@Test
	public void updateExistingModuleDependencyMembers() {

		IBranchPath mainPath = BranchPathUtils.createMainPath();

		Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.MODULE_ROOT, Concepts.MODULE_SCT_CORE, // it must belong to the core module
				SnomedApiTestConstants.UK_PREFERRED_MAP)
				.put("commitComment", "Created concept with INT core module")
				.build();

		String conceptId = lastPathSegment(createComponent(mainPath, SnomedComponentType.CONCEPT, conceptRequestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));

		getComponent(mainPath, SnomedComponentType.CONCEPT, conceptId)
			.statusCode(200)
			.body("effectiveTime", nullValue());

		// version branch
		final String versionEffectiveTime = getNextAvailableEffectiveDateAsString(
				SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		
		final Date versionEffectiveDate = EffectiveTimes.parse(versionEffectiveTime, DateFormats.SHORT);
		
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

		coreModuleDependencyMembersAfterVersioning.forEach(member -> {
			assertEquals("Effective time must be updated after versioning", versionEffectiveDate, member.getEffectiveTime());
		});
		
	}
	
	@Test
	public void moduleDependecyDuplicateMemberTest() {

		final String shortName = "SNOMEDCT-MODULEDEPENDENCY";
		createCodeSystem(branchPath, shortName).statusCode(201);

		Map<Pair<String, String>, Date> moduleToReferencedComponentAndEffectiveDateMap = Maps.newHashMap();

		SnomedReferenceSetMembers moduleDependencyMembers1 = SnomedRequests.prepareSearchMember()
				.all()
				.filterByRefSet(Concepts.REFSET_MODULE_DEPENDENCY_TYPE)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();

		moduleDependencyMembers1.getItems().forEach(member -> {
			Pair<String, String> pair = Tuples.pair(member.getModuleId(), member.getReferencedComponent().getId());
			moduleToReferencedComponentAndEffectiveDateMap.put(
				pair,
				member.getEffectiveTime()
			);
		});
		
		// create Norwegian module
		Map<?, ?> norwegianModuleRequestBody = createConceptRequestBody(Concepts.MODULE_ROOT, NORWEGIAN_MODULE_CONCEPT_ID, SnomedApiTestConstants.UK_PREFERRED_MAP)
				.put("id", NORWEGIAN_MODULE_CONCEPT_ID)
				.put("commitComment", "Created norwegian module concept")
				.build();
		createComponent(branchPath, SnomedComponentType.CONCEPT, norwegianModuleRequestBody).statusCode(201);
		
		// create both inferred and stated relationships
		Map<?, ?> inferredRelationshipRequestBody = SnomedRestFixtures
				.createRelationshipRequestBody(NORWEGIAN_MODULE_CONCEPT_ID, Concepts.IS_A, Concepts.MODULE_ROOT, NORWEGIAN_MODULE_CONCEPT_ID, CharacteristicType.INFERRED_RELATIONSHIP, 0)
				.put("commitComment", "Created inferred is_a from the norwegian rule to SCT_MODULE_CORE").build();
		
		Map<?, ?> statedRelationshipRequestBody = SnomedRestFixtures
				.createRelationshipRequestBody(NORWEGIAN_MODULE_CONCEPT_ID, Concepts.IS_A, Concepts.MODULE_ROOT, NORWEGIAN_MODULE_CONCEPT_ID, CharacteristicType.STATED_RELATIONSHIP, 0)
				.put("commitComment", "Created state is_a from the norwegian rule to SCT_MODULE_CORE").build();

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, inferredRelationshipRequestBody).statusCode(201);
		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, statedRelationshipRequestBody).statusCode(201);
		
		SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
				.filterById(NORWEGIAN_MODULE_CONCEPT_ID)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();

		concepts.forEach(c -> assertEquals("Effective time must still be null", null, c.getEffectiveTime()));
				
		// version branch
		final String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		final String versionId = "testForModuleDependencyMembers";
		createVersion(shortName, versionId, effectiveDate).statusCode(201);
		getVersion(shortName, versionId).statusCode(200);

		SnomedReferenceSetMembers moduleDependencyMembersAfterVersioning = SnomedRequests.prepareSearchMember()
				.all()
				.filterByRefSet(Concepts.REFSET_MODULE_DEPENDENCY_TYPE)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		moduleDependencyMembersAfterVersioning.getItems().forEach(member-> {
			final Pair<String, String> pair = Tuples.pair(member.getModuleId(), member.getReferencedComponent().getId());
			final Date originalMemberEffectiveTime = moduleToReferencedComponentAndEffectiveDateMap.get(pair);
			final Date versionDate = EffectiveTimes.parse(effectiveDate, DateFormats.SHORT);
			if(originalMemberEffectiveTime != null) {
				assertEquals("Effective dates on existing module dependency members shouldn't be updated after versioning" , originalMemberEffectiveTime,  member.getEffectiveTime());
				moduleToReferencedComponentAndEffectiveDateMap.remove(pair);
			} else {
				assertEquals("The new members effective time should match the versionDate", versionDate, member.getEffectiveTime());
			}

		});
	}
	
}
