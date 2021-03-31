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

import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.INT_CODESYSTEM;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getLatestVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getVersion;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.core.rest.ext.AbstractSnomedExtensionApiTest;

/**
 * @since 2.0
 */
public class SnomedVersioningApiTest extends AbstractSnomedExtensionApiTest {

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
	public void createVersionOnUpgradeCodeSystem() {
		CodeSystemURI latestInternationalVersion = CodeSystemURI.branch(SNOMEDCT, getLatestVersion(SNOMEDCT));
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, effectiveDate, effectiveDate).statusCode(201);
		
		String newEffectiveDate = getNextAvailableEffectiveDateAsString(INT_CODESYSTEM);
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(INT_CODESYSTEM, effectiveDate));
		createVersion(upgradeCodeSystem.getShortName(), newEffectiveDate, newEffectiveDate).statusCode(400);
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
	
}
