/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * Superclass for common REST-related test functionality
 * @since 6.9
 */
public class FhirRestTest extends FhirTest {
	
	protected static final String APPLICATION_FHIR_JSON = "application/fhir+json;charset=utf-8";
	
	protected static final String FHIR_ROOT_CONTEXT = "/fhir"; //$NON-NLS-N$
	
	protected static final String SNOMED_VERSION = "2018-07-31";

}
