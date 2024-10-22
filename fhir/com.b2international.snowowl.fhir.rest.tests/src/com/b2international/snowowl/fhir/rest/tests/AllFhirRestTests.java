/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.rest.tests;

import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.fhir.rest.tests.capabilitystatement.CapabilityStatementApiTest;
import com.b2international.snowowl.fhir.rest.tests.codesystem.FhirCodeSystemApiTest;
import com.b2international.snowowl.fhir.rest.tests.codesystem.FhirSnomedCodeSystemLookupTest;
import com.b2international.snowowl.fhir.rest.tests.codesystem.FhirSnomedCodeSystemSubsumesTest;
import com.b2international.snowowl.fhir.rest.tests.codesystem.FhirSnomedCodeSystemValidateCodeTest;
import com.b2international.snowowl.fhir.rest.tests.conceptmap.FhirConceptMapApiTest;
import com.b2international.snowowl.fhir.rest.tests.valueset.FhirSnomedValueSetExpandTest;
import com.b2international.snowowl.fhir.rest.tests.valueset.FhirValueSetApiTest;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.test.commons.Resources;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * FHIR test suite for RESTful operations.
 * @since 6.6
 */
@RunWith(Suite.class)
@SuiteClasses({
	// Helpers
	SnomedUriParsingTest.class,
	
	// Resource types
	FhirCodeSystemApiTest.class,
	FhirValueSetApiTest.class,
//	FhirConceptMapApiTest.class,
	
	// SNOMED on FHIR tests
	FhirSnomedCodeSystemLookupTest.class,
	FhirSnomedCodeSystemValidateCodeTest.class,
	FhirSnomedCodeSystemSubsumesTest.class,
	FhirSnomedValueSetExpandTest.class,
	// TODO enable ConceptMap test cases
//	FhirSnomedConceptMapTranslateTest.class,
	
	// CapabilityStatement
	CapabilityStatementApiTest.class
	
})
public class AllFhirRestTests {
	
	@ClassRule
	public static final RuleChain APPRULE = RuleChain
		.outerRule(SnowOwlAppRule.snowOwl(AllFhirRestTests.class).bootRestApi())
		.around(new SnomedContentRule(SnomedContentRule.SNOMEDCT, Resources.Snomed.MINI_RF2_INT_20210731, Rf2ReleaseType.FULL).importUntil("20200131"));
	
}
