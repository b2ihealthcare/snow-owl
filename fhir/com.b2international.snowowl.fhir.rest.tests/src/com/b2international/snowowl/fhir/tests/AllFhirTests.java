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

import com.b2international.snowowl.fhir.tests.domain.BundleEntryTest;
import com.b2international.snowowl.fhir.tests.domain.BundleTest;
import com.b2international.snowowl.fhir.tests.domain.CodeableConceptUsageContextTest;
import com.b2international.snowowl.fhir.tests.domain.IssueTest;
import com.b2international.snowowl.fhir.tests.domain.OperationOutcomeTest;
import com.b2international.snowowl.fhir.tests.domain.QuantityUsageContextTest;
import com.b2international.snowowl.fhir.tests.domain.RangeUsageContextTest;
import com.b2international.snowowl.fhir.tests.domain.codesystem.CodeSystemTest;
import com.b2international.snowowl.fhir.tests.domain.codesystem.ConceptPropertyTest;
import com.b2international.snowowl.fhir.tests.domain.codesystem.ConceptTest;
import com.b2international.snowowl.fhir.tests.domain.codesystem.FilterTest;
import com.b2international.snowowl.fhir.tests.domain.conceptmap.ConceptMapElementTest;
import com.b2international.snowowl.fhir.tests.domain.conceptmap.ConceptMapTest;
import com.b2international.snowowl.fhir.tests.domain.conceptmap.DependsOnTest;
import com.b2international.snowowl.fhir.tests.domain.conceptmap.GroupTest;
import com.b2international.snowowl.fhir.tests.domain.conceptmap.TargetTest;
import com.b2international.snowowl.fhir.tests.domain.conceptmap.UnMappedTest;
import com.b2international.snowowl.fhir.tests.domain.valueset.ComposeTest;
import com.b2international.snowowl.fhir.tests.domain.valueset.ContainsTest;
import com.b2international.snowowl.fhir.tests.domain.valueset.ExpansionParameterTest;
import com.b2international.snowowl.fhir.tests.domain.valueset.ExpansionTest;
import com.b2international.snowowl.fhir.tests.domain.valueset.IncludeTest;
import com.b2international.snowowl.fhir.tests.domain.valueset.ValueSetConceptTest;
import com.b2international.snowowl.fhir.tests.domain.valueset.ValueSetFilterTest;
import com.b2international.snowowl.fhir.tests.domain.valueset.ValueSetTest;
import com.b2international.snowowl.fhir.tests.dt.CodeableConcepTest;
import com.b2international.snowowl.fhir.tests.dt.CodingTest;
import com.b2international.snowowl.fhir.tests.dt.ContactDetailTest;
import com.b2international.snowowl.fhir.tests.dt.ContactPointTest;
import com.b2international.snowowl.fhir.tests.dt.IdentiferTest;
import com.b2international.snowowl.fhir.tests.dt.MetaTest;
import com.b2international.snowowl.fhir.tests.dt.NarrativeTest;
import com.b2international.snowowl.fhir.tests.dt.PeriodTest;
import com.b2international.snowowl.fhir.tests.dt.PrimitiveDataTypeTest;
import com.b2international.snowowl.fhir.tests.dt.QuantityTest;
import com.b2international.snowowl.fhir.tests.dt.RangeTest;
import com.b2international.snowowl.fhir.tests.dt.ReferenceTest;
import com.b2international.snowowl.fhir.tests.dt.SignatureTest;
import com.b2international.snowowl.fhir.tests.dt.SimpleQuantityTest;
import com.b2international.snowowl.fhir.tests.filter.ConceptMapFilterTest;
import com.b2international.snowowl.fhir.tests.filter.FhirRequestParameterTest;
import com.b2international.snowowl.fhir.tests.serialization.domain.ElementDefinitionSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.DesignationSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.ExpandValueSetRequestDeserializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.LookupRequestDeserializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.LookupResultSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.ParameterDeserializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.ParameterSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.PropertySerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.SubsumptionRequestDeserializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.TranslateRequestDeserializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.TranslateResultSerializationTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.ValidateCodeResultTest;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.ValidateCodeSystemCodeRequestTest;

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
	
	//Common
	IssueTest.class,
	OperationOutcomeTest.class,
	BundleEntryTest.class,
	BundleTest.class,
	
	//CodeSystem
	CodeSystemTest.class,
	ConceptPropertyTest.class,
	FilterTest.class,
	ConceptTest.class,
	
	//ValueSet
	ValueSetConceptTest.class,
	ValueSetFilterTest.class,
	IncludeTest.class,
	ComposeTest.class,
	ExpansionParameterTest.class,
	ContainsTest.class,
	ExpansionTest.class,
	QuantityUsageContextTest.class,
	RangeUsageContextTest.class,
	CodeableConceptUsageContextTest.class,
	ValueSetTest.class,

	//ConceptMap
	UnMappedTest.class,
	DependsOnTest.class,
	TargetTest.class,
	ConceptMapElementTest.class,
	GroupTest.class,
	ConceptMapTest.class,
	ConceptMapFilterTest.class,
	ElementDefinitionSerializationTest.class,
	
	
})
public class AllFhirTests {
}
