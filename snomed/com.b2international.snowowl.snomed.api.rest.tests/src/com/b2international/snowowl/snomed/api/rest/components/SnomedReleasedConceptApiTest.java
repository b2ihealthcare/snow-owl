package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCanBeDeleted;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCanBeUpdated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCanNotBeDeleted;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentHasProperty;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertConceptExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertConceptNotExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertDescriptionExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertDescriptionNotExists;

import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.google.common.collect.ImmutableMap;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedReleasedConceptApiTest extends AbstractSnomedApiTest {

	private static final String CONCEPT_ID = "63961392103";
	private static final String DESCRIPTION_ID = "11320138110";
	private final IBranchPath mainPath = createMainPath();

	@Test
	public void automaticRestorationOfEffectiveTime() {
		givenBranchWithPath(testBranchPath);
		assertConceptExists(testBranchPath, CONCEPT_ID);
		
		assertComponentHasProperty(testBranchPath, SnomedComponentType.CONCEPT, CONCEPT_ID, "effectiveTime", "20150204");
		
		updateDefinitionStatus(CONCEPT_ID, DefinitionStatus.FULLY_DEFINED);

		assertComponentHasProperty(testBranchPath, SnomedComponentType.CONCEPT, CONCEPT_ID, "effectiveTime", null);

		updateDefinitionStatus(CONCEPT_ID, DefinitionStatus.PRIMITIVE);

		assertComponentHasProperty(testBranchPath, SnomedComponentType.CONCEPT, CONCEPT_ID, "effectiveTime", "20150204");
	}

	@Test
	public void deleteFailsConcept() {
		assertConceptExists(mainPath, CONCEPT_ID);
		assertComponentCanNotBeDeleted(mainPath, SnomedComponentType.CONCEPT, CONCEPT_ID, false);
		assertConceptExists(mainPath, CONCEPT_ID);
	}

	@Test
	public void deleteFailsDescription() {
		assertDescriptionExists(mainPath, DESCRIPTION_ID);
		assertComponentCanNotBeDeleted(mainPath, SnomedComponentType.DESCRIPTION, DESCRIPTION_ID, false);
		assertDescriptionExists(mainPath, DESCRIPTION_ID);
	}

	@Test
	public void forceDeleteConcept() {
		assertConceptExists(mainPath, CONCEPT_ID);
		assertComponentCanBeDeleted(mainPath, SnomedComponentType.CONCEPT, CONCEPT_ID, true);
		assertConceptNotExists(mainPath, CONCEPT_ID);
	}

	@Test
	public void forceDeleteDescription() {
		assertDescriptionExists(mainPath, DESCRIPTION_ID);
		assertComponentCanBeDeleted(mainPath, SnomedComponentType.DESCRIPTION, DESCRIPTION_ID, true);
		assertDescriptionNotExists(mainPath, DESCRIPTION_ID);
	}

	private void updateDefinitionStatus(final String conceptId,
			final DefinitionStatus definitionStatus) {
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("definitionStatus", definitionStatus.toString())
				.put("commitComment", "Changed concept definition status")
				.build();
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, conceptId, updateRequestBody);
	}

}
