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
package com.b2international.snowowl.fhir.rest.tests;

import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.fhir.rest.tests.batch.FhirBatchApiRestTest;
import com.b2international.snowowl.fhir.rest.tests.codesystem.FhirCodeSystemApiTest;
import com.b2international.snowowl.fhir.rest.tests.codesystem.FhirCodeSystemLookupOperationTest;
import com.b2international.snowowl.fhir.rest.tests.codesystem.FhirCodeSystemSubsumesOperationTest;
import com.b2international.snowowl.fhir.rest.tests.codesystem.FhirCodeSystemValidateCodeOperationTest;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.test.commons.BundleStartRule;
import com.b2international.snowowl.test.commons.Resources;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * FHIR test suite for RESTful operations.
 * @since 6.6
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	// CodeSystem API
	FhirCodeSystemApiTest.class,
	FhirCodeSystemLookupOperationTest.class,
	FhirCodeSystemSubsumesOperationTest.class,
	FhirCodeSystemValidateCodeOperationTest.class,
	
	// ValueSet API
//	SnomedValueSetRestTest.class,
//	ExpandSnomedRestTest.class,
	
	// ConceptMap API
//	SnomedConceptMapRestTest.class,
//	TranslateSnomedConceptMapRestTest.class,
	
	//Batch
//	FhirBatchApiRestTest.class,
})
public class AllFhirRestTests {
	
	@ClassRule
	public static final RuleChain APPRULE = RuleChain
		.outerRule(SnowOwlAppRule.snowOwl(AllFhirRestTests.class))
		.around(new BundleStartRule("org.eclipse.jetty.osgi.boot"))
		.around(new BundleStartRule("com.b2international.snowowl.core.rest"))
		.around(new SnomedContentRule(SnomedContentRule.SNOMEDCT, Resources.Snomed.MINI_RF2_INT_20210731, Rf2ReleaseType.FULL).importUntil("20200131"));
	
}
