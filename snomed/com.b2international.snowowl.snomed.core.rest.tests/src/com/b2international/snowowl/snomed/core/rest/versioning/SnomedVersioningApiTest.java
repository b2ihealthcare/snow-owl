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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getVersion;

import org.junit.Test;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures;

/**
 * @since 2.0
 */
public class SnomedVersioningApiTest extends AbstractSnomedApiTest {

	@Test
	public void getNonExistentVersion() {
		getVersion(SNOMED_SHORT_NAME, "nonexistent-version-id").statusCode(404);
	}

	@Test
	public void createVersionWithoutDescription() {
		createVersion(SNOMED_SHORT_NAME, "", getNextAvailableEffectiveDateAsString(SNOMED_SHORT_NAME)).statusCode(400);
	}

	@Test
	public void createVersionWithNonLatestEffectiveDate() {
		createVersion(SNOMED_SHORT_NAME, "not-latest-effective-time", "20020101").statusCode(400);
	}

	@Test
	public void createRegularVersion() {
		createVersion(SNOMED_SHORT_NAME, "regular-version", getNextAvailableEffectiveDateAsString(SNOMED_SHORT_NAME)).statusCode(201);
		getVersion(SNOMED_SHORT_NAME, "regular-version").statusCode(200);
	}

	@Test
	public void createVersionWithSameNameAsBranch() {
		createVersion(SNOMED_SHORT_NAME, "SnomedVersioningApiTest", getNextAvailableEffectiveDateAsString(SNOMED_SHORT_NAME)).statusCode(409);
	}
	
	@Test
	public void createForcedVersionWithSameNameAsBranch() {
		final String versionName = "VersionToRecreate";		
		CodeSystemURI codeSystemVersionURI = new CodeSystemURI(String.join(Branch.SEPARATOR, SNOMED_SHORT_NAME, versionName));
		
		createVersion(SNOMED_SHORT_NAME, versionName, getNextAvailableEffectiveDateAsString(SNOMED_SHORT_NAME)).statusCode(201);
		String conceptId = createConcept(codeSystemVersionURI, SnomedRestFixtures.createConceptRequestBody(Concepts.ROOT_CONCEPT));
		
		//Should fail to recreate version without the force flag
		createVersion(SNOMED_SHORT_NAME, versionName, getNextAvailableEffectiveDateAsString(SNOMED_SHORT_NAME)).statusCode(409);
		assertGetConcept(codeSystemVersionURI, conceptId, new String[0]).statusCode(200);
		
		//Should succeed to recreate version with the force flag set
		createVersion(SNOMED_SHORT_NAME, versionName, getNextAvailableEffectiveDateAsString(SNOMED_SHORT_NAME), true).statusCode(201);
		assertGetConcept(codeSystemVersionURI, conceptId, new String[0]).statusCode(404);
	}
	
}
