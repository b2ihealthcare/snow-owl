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
package com.b2international.snowowl.fhir.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.fhir.tests.filter.ConceptMapFilterTest;
import com.b2international.snowowl.fhir.tests.filter.FilterTest;
import com.b2international.snowowl.fhir.tests.filter.ParameterParsingTest;
import com.b2international.snowowl.fhir.tests.filter.SearchRequestParametersTest;
import com.b2international.snowowl.fhir.tests.serialization.domain.BundleSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.domain.CodeSystemSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.domain.ConceptMapSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.domain.ModelDeserializationTest;
import com.b2international.snowowl.fhir.tests.serialization.domain.ModelSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.domain.UsageContextSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.domain.ValueSetSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.dt.ComplexDataTypeSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.dt.PrimitiveDataTypeSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.DesignationSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.LookupRequestDeserializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.LookupResultSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.ParameterDeserializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.ParameterSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.PropertySerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.SubsumptionRequestDeserializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.TranslateRequestDeserializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.TranslateResultSerializationTest;

/**
 * FHIR test suite.
 * @since 6.4
 */
@RunWith(Suite.class)
@SuiteClasses({ 

	//Generic tests
	SnomedUriParsingTest.class,
	ExceptionTest.class,
	FilterTest.class,

	//tests related to request parameter parsing and filtering
	ParameterParsingTest.class,
	SearchRequestParametersTest.class,

	//Data type tests
	PrimitiveDataTypeSerializationTest.class,
	ComplexDataTypeSerializationTest.class,
	
	//parameterized
	ParameterDeserializationTest.class,
	ParameterSerializationTest.class,
	PropertySerializationTest.class,
	DesignationSerializationTest.class,
	LookupRequestDeserializationTest.class,
	LookupResultSerializationTest.class,
	TranslateRequestDeserializationTest.class,
	TranslateResultSerializationTest.class,
	SubsumptionRequestDeserializationTest.class,

	//Domain models
	UsageContextSerializationTest.class,
	ModelSerializationTest.class,
	ModelDeserializationTest.class,
	BundleSerializationTest.class,
	CodeSystemSerializationTest.class,
	ValueSetSerializationTest.class,
	ConceptMapSerializationTest.class,
	ConceptMapFilterTest.class,

})
public class AllFhirTests {
}
