/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createConceptRequestBody;

import java.util.concurrent.TimeUnit;

import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.CodeSystemRestRequests;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

import io.restassured.response.ValidatableResponse;

/**
 * @since 7.14.1
 */
public abstract class AbstractSnomedExtensionApiTest extends AbstractSnomedApiTest {

	protected static final String SNOMEDCT = SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
	
	protected final String createModule(CodeSystem extension) {
		// generate ID for the module first
		String moduleId = SnomedRequests.identifiers().prepareGenerate()
				.setCategory(ComponentCategory.CONCEPT)
				.setNamespace(Concepts.B2I_NAMESPACE)
				.setQuantity(1)
				.buildAsync()
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES)
				.first()
				.map(SctId::getSctid)
				.orElseThrow();
		// then create the module concept
		return createConcept(
			extension.getCodeSystemURI(), 
			createConceptRequestBody(Concepts.MODULE_ROOT, moduleId).with("id", moduleId)
		);
	}

	// branch have been already created by the outer rules, so we are just reusing it to create an extension branch
	protected final CodeSystem createExtension(CodeSystemURI extensionOf, String codeSystemId) {
		CodeSystemRestRequests.createCodeSystem(extensionOf, codeSystemId)
			.assertThat()
			.statusCode(201);
		return CodeSystemRestRequests.getCodeSystem(codeSystemId).extract().as(CodeSystem.class);
	}

	protected final ValidatableResponse assertCodeSystemUpgrade(CodeSystemURI upgradeOf, CodeSystemURI extensionOf) {
		return CodeSystemRestRequests.upgrade(upgradeOf, extensionOf).assertThat();
	}
	
	protected final CodeSystem createExtensionUpgrade(CodeSystemURI upgradeOf, CodeSystemURI extensionOf) {
		final String upgradeCodeSystemId = RestExtensions.lastPathSegment(assertCodeSystemUpgrade(upgradeOf, extensionOf).statusCode(201).extract().header("Location"));
		return CodeSystemRestRequests.getCodeSystem(upgradeCodeSystemId).extract().as(CodeSystem.class);
	}

}
