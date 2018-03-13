/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.tests.validation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.api.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.api.codesystems.IssueType;
import com.b2international.snowowl.fhir.api.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.api.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.api.exceptions.FhirException;
import com.b2international.snowowl.fhir.api.model.Issue;
import com.b2international.snowowl.fhir.api.model.Issue.Builder;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.api.tests.FhirExceptionIssueMatcher;

/**
 * 
 * @since 6.3
 */
public class ExceptionTest extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("No system specified");
	
	@Test
	public void testException() throws Exception {

		Issue expectedIssue = builder.addLocation("LookupRequest.system")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_BAD_SYNTAX, "Bad Syntax in LookupRequest.system")
				.build();
		
		BadRequestException bre = new BadRequestException("No system specified", "LookupRequest.system");
		
		exception.expect(FhirException.class);
		exception.expectMessage("No system specified");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		throw bre;
	}
	
	@Test
	public void testException2() throws Exception {

		Issue expectedIssue = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("No system specified [null]")
				.addLocation("LookupRequest.system")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_BAD_SYNTAX, "Bad Syntax in LookupRequest.system")
				.build();
		
		BadRequestException bre = new BadRequestException("No system specified %s", "LookupRequest.system", "[null]");
		
		exception.expect(FhirException.class);
		exception.expectMessage("No system specified [null]");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		throw bre;
	}

}
