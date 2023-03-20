/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.versioning;

import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR;
import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.INT_CODESYSTEM;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 2.0
 */
public class SnomedVersioningApiTest extends AbstractSnomedApiTest {

	@Test
	public void getNonExistentVersion() {
		getVersion(INT_CODESYSTEM, "nonexistent-version-id").statusCode(404);
	}

	@Test
	public void createVersionWithoutDescription() {
		createVersion(INT_CODESYSTEM, "", getNextAvailableEffectiveDateAsString(INT_CODESYSTEM)).statusCode(400);
	}

	@Test
	public void createVersionWithNonLatestEffectiveDate() {
		createVersion(INT_CODESYSTEM, "not-latest-effective-time", "20020101").statusCode(400);
	}

	@Test
	public void createRegularVersion() {
		createVersion(INT_CODESYSTEM, "regular-version", getNextAvailableEffectiveDateAsString(INT_CODESYSTEM)).statusCode(201);
		getVersion(INT_CODESYSTEM, "regular-version").statusCode(200);
	}

	@Test
	public void createVersionWithSameNameAsBranch() {
		createVersion(INT_CODESYSTEM, "SnomedVersioningApiTest", getNextAvailableEffectiveDateAsString(INT_CODESYSTEM)).statusCode(409);
	}
	
	@Test
	public void forceCreateVersionWithDifferentVersionId() throws Exception {
		final String versionName = "forceCreateVersionWithDifferentVersionId";
		String versionEffectiveDate = getNextAvailableEffectiveDateAsString(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, versionName, versionEffectiveDate).statusCode(201);
		createVersion(INT_CODESYSTEM, versionName + "-force", versionEffectiveDate, true).statusCode(400);
	}
	
	@Test
	public void forceCreateVersionWithDifferentEffectiveDate() throws Exception {
		final String versionName = "forceCreateVersionWithDifferentEffectiveDate";
		String versionEffectiveDate = getNextAvailableEffectiveDateAsString(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, versionName, versionEffectiveDate).statusCode(201);
		String nextEffectiveDate = getNextAvailableEffectiveDateAsString(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, versionName, nextEffectiveDate, true).statusCode(201);
	}
	
	@Test
	public void forceCreateVersionShouldUpdateEffectiveTime() {
		final String versionName = "forceCreateVersionShouldUpdateEffectiveTime";
		CodeSystemURI codeSystemVersionURI = CodeSystemURI.branch(INT_CODESYSTEM, versionName);
		
		String versionEffectiveDate = getNextAvailableEffectiveDateAsString(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, versionName, versionEffectiveDate).statusCode(201);
		String conceptId = createConcept(new CodeSystemURI(INT_CODESYSTEM), SnomedRestFixtures.childUnderRootWithDefaults());
		
		SnomedConcept afterFailedVersioning = getConcept(new CodeSystemURI(INT_CODESYSTEM), conceptId);
		assertEquals(null, afterFailedVersioning.getEffectiveTime());
		
		//Should succeed to recreate version with the force flag set
		createVersion(INT_CODESYSTEM, versionName, versionEffectiveDate, true).statusCode(201);
		SnomedConcept afterForceVersioning = getConcept(codeSystemVersionURI, conceptId);
		assertEquals(versionEffectiveDate, EffectiveTimes.format(afterForceVersioning.getEffectiveTime(), DateFormats.SHORT));
	}
	
	@Test
	public void createVersionShouldPreserveDocumentPRoperties() {
		String conceptId = createConcept(new CodeSystemURI(INT_CODESYSTEM), SnomedRestFixtures.childUnderRootWithDefaults());
		createMember(new CodeSystemURI(INT_CODESYSTEM), Map.of(
				"active", true,
				"moduleId", Concepts.MODULE_SCT_CORE,
				"referenceSetId", Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR,
				"referencedComponentId", conceptId,
				"valueId", Concepts.PENDING_MOVE
			));
		
		SnomedConcepts conceptBeforeVersioning = searchConcept(new CodeSystemURI(INT_CODESYSTEM), Map.of(
				"activeMemberOf", REFSET_CONCEPT_INACTIVITY_INDICATOR,
				"id", conceptId), 1);
		assertTrue(conceptBeforeVersioning.getTotal() == 1);
		
		createVersion(INT_CODESYSTEM, "versionToTestDocumentPreservation", getNextAvailableEffectiveDateAsString(INT_CODESYSTEM)).statusCode(201);
		getVersion(INT_CODESYSTEM, "versionToTestDocumentPreservation").statusCode(200);
		
		SnomedConcepts conceptAfterVersioning = searchConcept(new CodeSystemURI(INT_CODESYSTEM), Map.of(
				"activeMemberOf", REFSET_CONCEPT_INACTIVITY_INDICATOR,
				"id", conceptId), 1);
		assertTrue(conceptAfterVersioning.getTotal() == 1);
	}

	
	@Test
	public void publishAssignedIdsOnVersionCreate() throws Exception {
		final String versionName = "publishAssignedIdsOnVersionCreate";
		String conceptId = createConcept(new CodeSystemURI(INT_CODESYSTEM), SnomedRestFixtures.childUnderRootWithDefaults());
		
		String versionEffectiveDate = getNextAvailableEffectiveDateAsString(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, versionName, versionEffectiveDate).statusCode(201);
		
		SctId sctId = SnomedRequests.identifiers().prepareGet()
				.setComponentId(conceptId)
				.buildAsync()
				.execute(Services.bus())
				.getSync()
				.first()
				.get();
			
		assertEquals("Published", sctId.getStatus());
	}
	
}
