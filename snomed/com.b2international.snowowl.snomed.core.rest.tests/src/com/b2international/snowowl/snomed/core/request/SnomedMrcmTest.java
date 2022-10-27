/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.MrcmTypeRequest.ATTRIBUTE_TYPE;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.base.Objects;

/**
 * @since 8.7.0
 */
public class SnomedMrcmTest {
	
	private static final String CODESYSTEM = "SNOMEDCT";

	@Test
	public void applicableTypesTest() {
		final String isAModificationOf = "738774007";
		final String hasDisposition = "726542003";
		
		SnomedReferenceSetMembers substanceDataTypes = SnomedRequests.prepareGetMrcmTypeRules()
				.setAttributeType(ATTRIBUTE_TYPE.DATA)
				.setModuleIds(List.of(Concepts.MODULE_SCT_CORE))
				.setParentIds(Set.of(SUBSTANCE))
				.build(CODESYSTEM)
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
		assertEquals(0, substanceDataTypes.getTotal());
		
		Collection<String> substanceObjectTypes = SnomedRequests.prepareGetMrcmTypeRules()
			.setAttributeType(ATTRIBUTE_TYPE.OBJECT)
			.setModuleIds(List.of(Concepts.MODULE_SCT_CORE))
			.setParentIds(Set.of(SUBSTANCE))
			.build(CODESYSTEM)
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES)
			.stream()
			.map(SnomedReferenceSetMember::getReferencedComponentId)
			.collect(Collectors.toSet());
		
		assertEquals(2, substanceObjectTypes.size());
		assertTrue(substanceObjectTypes.containsAll(List.of(isAModificationOf, hasDisposition)));
	}
	
	@Test
	public void applicableRangeTest() {
		SnomedReferenceSetMembers substanceDataTypeRanges = SnomedRequests.prepareGetMrcmRangeRules()
				.setAttributeType(ATTRIBUTE_TYPE.DATA)
				.setModuleIds(List.of(Concepts.MODULE_SCT_CORE))
				.setParentIds(Set.of(SUBSTANCE))
				.build(CODESYSTEM)
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
		assertEquals(0, substanceDataTypeRanges.getTotal());
		
		SnomedReferenceSetMembers substanceObjectTypeRanges = SnomedRequests.prepareGetMrcmRangeRules()
				.setAttributeType(ATTRIBUTE_TYPE.OBJECT)
				.setModuleIds(List.of(Concepts.MODULE_SCT_CORE))
				.setParentIds(Set.of(SUBSTANCE))
				.build(CODESYSTEM)
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
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
