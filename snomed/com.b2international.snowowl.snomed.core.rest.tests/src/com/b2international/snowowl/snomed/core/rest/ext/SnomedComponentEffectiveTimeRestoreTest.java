/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.core.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.core.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.core.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.childUnderRootWithDefaults;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.inactivateConcept;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.reactivateConcept;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.CodeSystemRestRequests;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;

/**
 * @since 7.13
 */
public class SnomedComponentEffectiveTimeRestoreTest extends AbstractSnomedApiTest {

	private static final String SNOMEDCT = SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
	
	private static final String EXT_BASE_SI_VERSION = "2019-07-31";
	private static final String EXT_UPGRADE_SI_VERSION = "2020-01-31";
	private static final String EXT_VERSION = "2019-10-31";
	
	@Test
	public void restoreEffectiveTimeOnReleasedConcept() throws Exception {
		String conceptId = createNewConcept(branchPath);

		String shortName = "SNOMEDCT-CON-1";
		createCodeSystem(branchPath, shortName).statusCode(201);
		String effectiveDate = getNextAvailableEffectiveDateAsString(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		// After versioning, the concept should be released and have an effective time set on it
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveDate));

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
			.body("effectiveTime", equalTo(effectiveDate));
	}
	
	@Test
	public void restoreExtensionEffectiveTimeOnExtension() throws Exception {
		// create extension on the base SI VERSION
		final CodeSystem extension = createExtension(CodeSystemURI.branch(SNOMEDCT, EXT_BASE_SI_VERSION), branchPath);
		// create a concept on the extension
		String conceptId = createConcept(branchPath, childUnderRootWithDefaults());
		// create an extension version, concept receives effective time
		createVersion(extension.getShortName(), EXT_VERSION, "20191031") // TODO support yyyy-MM-dd effective dates
			.statusCode(201);
		SnomedConcept concept = getConcept(conceptId);
		assertEquals(EffectiveTimes.parse(EXT_VERSION), concept.getEffectiveTime());
		// create a change on the concept, like change the module
		updateConcept(conceptId, Map.of(
			"moduleId", Concepts.MODULE_SCT_MODEL_COMPONENT
		));
		concept = getConcept(conceptId);
		assertEquals(null, concept.getEffectiveTime());
		// revert the change, so it reverts the effective time to the EXT_VER effective time
		updateConcept(conceptId, Map.of(
			"moduleId", Concepts.MODULE_SCT_CORE
		));
		concept = getConcept(conceptId);
		assertEquals(EffectiveTimes.parse(EXT_VERSION), concept.getEffectiveTime());
	}
	
	@Test
	public void restoreInternationalEffectiveTimeOnExtension() throws Exception {
		
	}
	
	@Test
	public void restoreInternationalEffectiveTimeOnExtensionUpgrade() throws Exception {
		
	}
	
	@Test
	public void restoreExtensionEffectiveTimeOnExtensionUpgrade() throws Exception {
		
	}
	
	// branch have been already created by the outer rules, so we are just reusing it to create an extension branch
	private CodeSystem createExtension(CodeSystemURI extensionOf, IBranchPath extensionBranch) {
		final String codeSystemId = extensionBranch.lastSegment();
		CodeSystemRestRequests.createCodeSystem(extensionBranch, codeSystemId)
			.assertThat()
			.statusCode(201);
		return CodeSystemRestRequests.getCodeSystem(codeSystemId).extract().as(CodeSystem.class);
	}
	
}
