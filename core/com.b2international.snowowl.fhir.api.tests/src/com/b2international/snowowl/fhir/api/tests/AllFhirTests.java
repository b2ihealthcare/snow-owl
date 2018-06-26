/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.fhir.api.tests.serialization.parameterized.ParameterDeserializationTest;

/**
 * FHIR test suite.
 * @since 6.4
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	/*
	ParameterSerializationTest.class,
	PropertySerializationTest.class,
	DesignationSerializationTest.class,
	
	OperationOutcomeSerializationTest.class
	LookupResultSerializationTest.class
	*/
	ParameterDeserializationTest.class,
	/*
	CodeSystemSerializationTest.class,
	ValueSetSerializationTest.class,
	ModelDeserializationTest.class,
	CodingValidationTest.class,
	DesignationValidationTest.class,
	PropertyValidationTest.class,
	SubPropertyValidationTest.class,
	ExceptionTest.class,
	ParameterParsingTest.class,
	FilterTest.class
	*/
})
public class AllFhirTests {
}
