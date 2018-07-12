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

import com.b2international.snowowl.fhir.api.tests.serialization.domain.BundleSerializationTest;

/**
 * FHIR test suite.
 * @since 6.4
 */
@RunWith(Suite.class)
@SuiteClasses({ 

	/*
	UriParsingTest.class,
	PrimitiveDataTypeSerializationTest.class,
	ComplexDataTypeSerializationTest.class,
	ParameterDeserializationTest.class,
	ParameterSerializationTest.class,
	PropertySerializationTest.class,
	DesignationSerializationTest.class,
	ModelSerializationTest.class,
	UsageContextSerializationTest.class,
	LookupResultSerializationTest.class,
	CodeSystemSerializationTest.class,
	 */
	BundleSerializationTest.class,
	/*
	
	//This tests are pretty meaningless
	ValueSetSerializationTest.class,
	ModelDeserializationTest.class,
	LookupRequestDeserializationTest.class,
	ExceptionTest.class,

	//tests related to parameter parsing and filtering
	ParameterParsingTest.class,
	FilterTest.class,
	SubsumptionRequestTest.class
	 */
})
public class AllFhirTests {
}
