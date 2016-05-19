package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCanBeUpdated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentHasProperty;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertConceptExists;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.google.common.collect.ImmutableMap;

public class SnomedReleasedConceptApiTest extends AbstractSnomedApiTest {

	@Test
	public void automaticRestorationOfEffectiveTime() {
		givenBranchWithPath(testBranchPath);
		final String conceptId = "63961392103";
		assertConceptExists(testBranchPath, conceptId);
		
		assertComponentHasProperty(testBranchPath, SnomedComponentType.CONCEPT, conceptId, "effectiveTime", "20150204");
		
		updateDefinitionStatus(conceptId, DefinitionStatus.FULLY_DEFINED);

		assertComponentHasProperty(testBranchPath, SnomedComponentType.CONCEPT, conceptId, "effectiveTime", null);

		updateDefinitionStatus(conceptId, DefinitionStatus.PRIMITIVE);

		assertComponentHasProperty(testBranchPath, SnomedComponentType.CONCEPT, conceptId, "effectiveTime", "20150204");
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
