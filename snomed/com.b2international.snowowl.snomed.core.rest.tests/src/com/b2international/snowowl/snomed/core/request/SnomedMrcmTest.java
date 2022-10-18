package com.b2international.snowowl.snomed.core.request;

import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.SUBSTANCE;
import static com.b2international.snowowl.snomed.common.SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.base.Objects;

public class SnomedMrcmTest {
	
	private static final String CODESYSTEM = "SNOMEDCT";

	@Test
	public void applicableTypesTest() {
		final String isAModificationOf = "738774007";
		final String hasDisposition = "726542003";
		
		Collection<String> substanceDataTypes = SnomedRequests.getApplicableTypes(Services.bus(), CODESYSTEM, 
				Set.of(SUBSTANCE), Set.of(), List.of(Concepts.MODULE_SCT_CORE), true, false).getSync(1, TimeUnit.MINUTES);
		assertEquals(0, substanceDataTypes.size());
		
		Collection<String> substanceObjectTypes = SnomedRequests.getApplicableTypes(Services.bus(), CODESYSTEM, 
				Set.of(SUBSTANCE), Set.of(), List.of(Concepts.MODULE_SCT_CORE), false, true).getSync(1, TimeUnit.MINUTES);
		assertEquals(2, substanceObjectTypes.size());
		assertTrue(substanceObjectTypes.containsAll(List.of(isAModificationOf, hasDisposition)));
	}
	
	@Test
	public void applicableRangeTest() {
		SnomedReferenceSetMembers substanceDataTypeRanges = SnomedRequests.getApplicableRanges(Services.bus(), CODESYSTEM, 
				Set.of(SUBSTANCE), Set.of(), List.of(Concepts.MODULE_SCT_CORE), true, false).getSync(1, TimeUnit.MINUTES);
		assertEquals(0, substanceDataTypeRanges.getTotal());
		
		SnomedReferenceSetMembers substanceObjectTypeRanges = SnomedRequests.getApplicableRanges(Services.bus(), CODESYSTEM, 
				Set.of(SUBSTANCE), Set.of(), List.of(Concepts.MODULE_SCT_CORE), false, true).getSync(1, TimeUnit.MINUTES);
		assertEquals(2, substanceObjectTypeRanges.getTotal());
		Map<String, String> ranges = Map.of("738774007", "<< 105590001 |Substance (substance)|", //Is A Modification Of
				"726542003", "<< 726711005 |Disposition (disposition)|"); //Has Disposition
		substanceObjectTypeRanges.forEach(member -> {
			final String expectedRange = ranges.get(member.getReferencedComponentId());
			final String actualRange = (String) member.getProperties().get(FIELD_MRCM_RANGE_CONSTRAINT);
			assertTrue(String.format("Expected range (%s) does not match actual range (%s)", expectedRange, actualRange), Objects.equal(expectedRange, actualRange));
		});
	}
	
}
