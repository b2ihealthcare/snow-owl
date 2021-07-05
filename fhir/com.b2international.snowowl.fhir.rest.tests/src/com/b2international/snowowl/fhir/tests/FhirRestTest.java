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
package com.b2international.snowowl.fhir.tests;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;

import com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests;

/**
 * Superclass for common REST-related test functionality. All tests receive a single Code System to test/verify/use as test fixture. The CodeSystemId
 * can be accessed with the {@link #getTestCodeSystemId()} method and it is available at the start of the test and will be removed once the test
 * completes. Extra CodeSystems can be requested with the {@link #createCodeSystem(String)} and those will be removed at the end of the test as well.
 * 
 * @since 6.9
 */
public class FhirRestTest extends FhirTest {
	
	protected static final String APPLICATION_FHIR_JSON = "application/fhir+json;charset=utf-8";
	
	public static final String FHIR_ROOT_CONTEXT = "/fhir"; //$NON-NLS-N$
	
	protected static final String SNOMED_VERSION = "2018-07-31";
	
	public static final class Endpoints {
		public static final String CODESYSTEM = "/CodeSystem";
		public static final String CODESYSTEM_ID = "/CodeSystem/{id}";
		public static final String CODESYSTEM_LOOKUP = "/CodeSystem/$lookup";
		public static final String CODESYSTEM_SUBSUMES = "/CodeSystem/$subsumes";
		public static final String CODESYSTEM_VALIDATE_CODE = "/CodeSystem/$validate-code";
	}
	
	protected final String getTestCodeSystemId() {
		return methodNameRule.get().replaceAll("\\$", "");
	}
	
	private final Set<String> createdCodeSystems = new HashSet<>(); 
	
	@Before
	public void before() {
		createCodeSystem(getTestCodeSystemId());
	}
	
	protected final String createCodeSystem(String codeSystemId) {
		CodeSystemRestRequests.createCodeSystem(codeSystemId).statusCode(201);
		createdCodeSystems.add(codeSystemId);
		return codeSystemId;
	}

	@After
	public void after() {
		for (String codeSystemId : createdCodeSystems) {
			CodeSystemRestRequests.deleteCodeSystem(codeSystemId).statusCode(204);
		}
	}
	
	protected final String getTestCodeSystemUrl() {
		return CodeSystemRestRequests.getCodeSystemUrl(getTestCodeSystemId());
	}

}
