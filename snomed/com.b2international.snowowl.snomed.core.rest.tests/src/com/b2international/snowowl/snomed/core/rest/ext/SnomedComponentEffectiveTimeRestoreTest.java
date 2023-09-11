/*
 * Copyright 2020-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.ext;

import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.inactivateConcept;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.reactivateConcept;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;

/**
 * @since 7.14
 */
public class SnomedComponentEffectiveTimeRestoreTest extends AbstractSnomedExtensionApiTest {

	@Test
	public void restoreEffectiveTimeOnReleasedConcept() throws Exception {
		String conceptId = createNewConcept(branchPath);

		String shortName = "SNOMEDCT-CON-1";
		createCodeSystem(branchPath, shortName).statusCode(201);
		LocalDate effectiveDate = getNextAvailableEffectiveDate(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		// After versioning, the concept should be released and have an effective time set on it
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveDate.format(DateTimeFormatter.BASIC_ISO_DATE)));

		inactivateConcept(branchPath, conceptId);

		// An inactivation should unset the effective time field
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
			.body("active", equalTo(false))
			.body("released", equalTo(true))
	 		.body("effectiveTime", nullValue());

		reactivateConcept(branchPath, conceptId);

		// Getting the concept back to its originally released state should restore the effective time
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveDate.format(DateTimeFormatter.BASIC_ISO_DATE)));
	}
	
	@Test
	public void restoreExtensionEffectiveTimeOnExtension() throws Exception {
		// create extension on the base SI VERSION
		final CodeSystem extension = createExtension(baseInternationalCodeSystem, branchPath.lastSegment());
		// create the module to represent the extension
		String moduleId = createModule(extension);
		// create an extension version, concept receives effective time
		createVersion(extension.getId(), EXT_VERSION, LocalDate.parse("2019-10-31"))
			.statusCode(201);
		SnomedConcept concept = getConcept(extension.getResourceURI(), moduleId);
		assertEquals(EffectiveTimes.parse(EXT_VERSION), concept.getEffectiveTime());
		// create a change on the concept, like change the definition status
		updateConcept(extension.getResourceURI(), moduleId, Map.of(
			"definitionStatusId", Concepts.FULLY_DEFINED
		));
		concept = getConcept(extension.getResourceURI(), moduleId);
		assertEquals(null, concept.getEffectiveTime());
		// revert the change, so it reverts the effective time to the EXT_VER effective time
		updateConcept(extension.getResourceURI(), moduleId, Map.of(
			"definitionStatusId", Concepts.PRIMITIVE
		));
		concept = getConcept(extension.getResourceURI(), moduleId);
		assertEquals(EffectiveTimes.parse(EXT_VERSION), concept.getEffectiveTime());
	}
	
	@Test
	public void restoreInternationalEffectiveTimeOnExtension() throws Exception {
		// create extension on the base SI VERSION
		CodeSystem extension = createExtension(baseInternationalCodeSystem, branchPath.lastSegment());
		// get the first concept from the Base SI version
		SnomedConcept concept = searchConcepts(baseInternationalCodeSystem, Map.of("module", Concepts.MODULE_SCT_CORE), 1).stream().findFirst().get();
		LocalDate lastReleasedEffectiveTime = concept.getEffectiveTime();
		String conceptId = concept.getId();
		// create a change on the concept, like change the definition status
		updateConcept(extension.getResourceURI(), concept.getId(), Map.of(
			"definitionStatusId", Concepts.FULLY_DEFINED
		));
		concept = getConcept(extension.getResourceURI(), conceptId);
		assertEquals(null, concept.getEffectiveTime());
		// revert the change, so it reverts the effective time to the EXT_VER effective time
		updateConcept(extension.getResourceURI(), conceptId, Map.of(
			"definitionStatusId", Concepts.PRIMITIVE
		));
		concept = getConcept(extension.getResourceURI(), conceptId);
		assertEquals(lastReleasedEffectiveTime, concept.getEffectiveTime());
	}
	
}
