/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.fhir.core.model.valueset.Compose;
import com.b2international.snowowl.fhir.tests.domain.*;
import com.b2international.snowowl.fhir.tests.domain.valueset.ValueSetSerializationTest;
import com.b2international.snowowl.fhir.tests.dt.*;
import com.b2international.snowowl.fhir.tests.filter.ConceptMapFilterTest;
import com.b2international.snowowl.fhir.tests.filter.FhirRequestParameterTest;
import com.b2international.snowowl.fhir.tests.serialization.domain.*;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.*;

/**
 * FHIR test suite.
 * @since 6.4
 */
@RunWith(Suite.class)
@SuiteClasses({ 

	//Generic tests
	SnomedUriParsingTest.class,
	ExceptionTest.class,

	//tests related to request parameter parsing and filtering
	FhirRequestParameterTest.class,
	
	//Data type tests
	PrimitiveDataTypeTest.class,
	CodingTest.class,
	CodeableConcepTest.class,
	IdentiferTest.class,
	PeriodTest.class,
	ReferenceTest.class,
	ContactPointTest.class,
	ContactDetailTest.class,
	MetaTest.class,
	NarrativeTest.class,
	QuantityTest.class,
	SimpleQuantityTest.class,
	RangeTest.class,
	SignatureTest.class,
	
	//parameterized
	ParameterDeserializationTest.class,
	ParameterSerializationTest.class,
	PropertySerializationTest.class,
	DesignationSerializationTest.class,
	LookupRequestDeserializationTest.class,
	LookupResultSerializationTest.class,
	ValidateCodeSystemCodeRequestTest.class,
	ValidateCodeResultTest.class,
	TranslateRequestDeserializationTest.class,
	TranslateResultSerializationTest.class,
	SubsumptionRequestDeserializationTest.class,
	ExpandValueSetRequestDeserializationTest.class,

	//Domain models
	TypedPropertySerializationTest.class,
	UsageContextSerializationTest.class,
	
	//New format
	IssueTest.class,
	OperationOutcomeTest.class,
	BundleEntryTest.class,
	BundleTest.class,
	ConceptPropertyTest.class,
	FilterTest.class,
	CodeSystemTest.class,
	
	//ValueSet
	Compose.class,
	ValueSetSerializationTest.class,

	ConceptMapSerializationTest.class,
	ConceptMapFilterTest.class,
	ElementDefinitionSerializationTest.class,
	
	
})
public class AllFhirTests {
}
