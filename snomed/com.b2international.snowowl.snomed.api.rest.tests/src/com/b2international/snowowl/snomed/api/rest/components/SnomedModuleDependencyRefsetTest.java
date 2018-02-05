package com.b2international.snowowl.snomed.api.rest.moduledependency;

import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getVersion;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createConceptRequestBody;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

public class SnomedModuleDependencyApiTest extends AbstractSnomedApiTest {

	private static final String MODULE_DEPENDECY_REFSET_ID = "900000000000534007";
	private static final String NORWEGIAN_MODULE_CONCEPT_ID = "51000202101";
	private static final String ROOT_CONCEPT = "138875005";

	@Test
	public void testForModuleDependencyMembers() {

		final String shortName = "SNOMEDCT-MODULEDEPENDENCY";
		createCodeSystem(branchPath, shortName).statusCode(201);
		
		// get number of module_dependecy_refset_member

		int sizeBeforeConceptCreation = SnomedRequests.prepareSearchRefSet()
				.all()
				.filterByActive(true)
				.filterByType(SnomedRefSetType.MODULE_DEPENDENCY)
				.setExpand("members()")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync()
				.getItems().get(0).getMembers().getItems().size();
				

		Map<?, ?> norwegianModuleRequestBody = createConceptRequestBody(Concepts.MODULE_ROOT, NORWEGIAN_MODULE_CONCEPT_ID, SnomedApiTestConstants.UK_PREFERRED_MAP)
				.put("id", NORWEGIAN_MODULE_CONCEPT_ID)
				.put("commitComment", "Created norwegian module concept")
				.build();
		createComponent(branchPath, SnomedComponentType.CONCEPT, norwegianModuleRequestBody).statusCode(201);
		
		Map<?,?> inferredRelationshipRequestBody = SnomedRestFixtures.createRelationshipRequestBody(NORWEGIAN_MODULE_CONCEPT_ID, Concepts.IS_A, Concepts.MODULE_ROOT, NORWEGIAN_MODULE_CONCEPT_ID, CharacteristicType.INFERRED_RELATIONSHIP, 0)
				.put("commitComment", "Created inferred isa from the norwegian rule to SCT_MODULE_CORE")
				.build();
		
		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, inferredRelationshipRequestBody).statusCode(201);
		
		Map<?, ?> norwegianConceptRequestBody = createConceptRequestBody(ROOT_CONCEPT, NORWEGIAN_MODULE_CONCEPT_ID, SnomedApiTestConstants.UK_PREFERRED_MAP)
			.put("commitComment", "Created concept in the norwegian module")
			.build();
		createComponent(branchPath, SnomedComponentType.CONCEPT, norwegianConceptRequestBody);
		
		SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
				.filterById(NORWEGIAN_MODULE_CONCEPT_ID)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();

		concepts.forEach(c -> assertEquals("Effective time must still be null", null, c.getEffectiveTime()));
		int sizeAfterConceptCreation = SnomedRequests.prepareSearchRefSet()
				.all()
				.filterByActive(true)
				.filterById(MODULE_DEPENDECY_REFSET_ID)
				.setExpand("members()")
				.filterByType(SnomedRefSetType.MODULE_DEPENDENCY)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync()
				.getItems().get(0).getMembers().getItems().size();

		assertEquals("After creating module concept the moduleDependencyRefset shouldn't change", sizeBeforeConceptCreation, sizeAfterConceptCreation);

		// version branch
		final String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		final String versionId = "testForModuleDependencyMembers";
		createVersion(shortName, versionId, effectiveDate).statusCode(201);
		getVersion(shortName, versionId).statusCode(200);

		// get number of module_dependecy_refset_member after versioning
		List<SnomedReferenceSetMember> items = SnomedRequests.prepareSearchRefSet()
				.all()
				.filterByActive(true)
				.filterById(MODULE_DEPENDECY_REFSET_ID)
				.filterByType(SnomedRefSetType.MODULE_DEPENDENCY)
				.setExpand("members()")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync()
				.getItems().get(0).getMembers().getItems();
		
		items.forEach(i -> System.out.println(i.getModuleId()));
		int sizeAfterVersioningAndConceptCreation = items.size();
		
		assertNotEquals("Csocsóa$ztalt megborító megamix", sizeAfterVersioningAndConceptCreation, sizeAfterConceptCreation);

	}

}
