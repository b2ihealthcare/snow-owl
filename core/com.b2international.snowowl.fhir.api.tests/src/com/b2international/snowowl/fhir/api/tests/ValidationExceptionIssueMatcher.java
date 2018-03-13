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
package com.b2international.snowowl.fhir.api.tests;

import java.util.Collection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.b2international.snowowl.fhir.api.exceptions.ValidationException;
import com.b2international.snowowl.fhir.api.model.Issue;
import com.b2international.snowowl.fhir.api.model.OperationOutcome;
import com.b2international.snowowl.fhir.api.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.api.model.dt.Coding;
import com.google.common.collect.HashMultiset;

/**
 * Matcher to check operation outcome for validation exceptions
 * @see ValidationExceptionIssueMatcher
 * @since 6.3
 */
public class ValidationExceptionIssueMatcher extends TypeSafeMatcher<ValidationException> {
	
	private Issue expectedIssue;
	private Issue foundIssue;

	/**
	 * @param expectedIssue
	 * @return
	 */
	public static ValidationExceptionIssueMatcher issue(Issue expectedIssue) {
		return new ValidationExceptionIssueMatcher(expectedIssue);
	}
	
	public ValidationExceptionIssueMatcher(Issue expectedIssue) {
		this.expectedIssue = expectedIssue;
	}

	@Override
	public void describeTo(Description description) {
		description
			.appendValue(foundIssue)
			.appendText(" instead of the expected:  ")
			.appendValue(expectedIssue);
	}

	@Override
	protected boolean matchesSafely(ValidationException validationException) {
		OperationOutcome operationOutcome = validationException.toOperationOutcome();
		Collection<Issue> issues = operationOutcome.getIssues();
		
		if (issues.isEmpty()) {
			return false;
		}
		foundIssue = issues.iterator().next();
		
		boolean issueMatched = foundIssue.getCode().equals(expectedIssue.getCode()) && 
			foundIssue.getSeverity().equals(expectedIssue.getSeverity()) &&
			foundIssue.getDiagnostics().equals(expectedIssue.getDiagnostics());
		
		if (!issueMatched) { 
			return false;
		}
		
		boolean collectionsMatch = HashMultiset.create(foundIssue.getLocations()).equals(HashMultiset.create(expectedIssue.getLocations())) &&
				HashMultiset.create(foundIssue.getExpressions()).equals(HashMultiset.create(expectedIssue.getExpressions()));
		
		if (!collectionsMatch) { 
			return false;
		}
		
		if (foundIssue.getCodeableConcept() == null && expectedIssue.getCodeableConcept()!=null) {
			return false;
		}
		
		if (foundIssue.getCodeableConcept() != null && expectedIssue.getCodeableConcept()==null) {
			return false;
		}
		
		if (foundIssue.getCodeableConcept() != null && expectedIssue.getCodeableConcept() != null) {
			CodeableConcept foundCodeableConcept = foundIssue.getCodeableConcept();
			CodeableConcept expectedCodeableConcept = expectedIssue.getCodeableConcept();
		
			Collection<Coding> foundCodings = foundCodeableConcept.getCodings();
			Collection<Coding> expectedCodings = expectedCodeableConcept.getCodings();
			boolean codingMatched = foundCodeableConcept.getText().equals(expectedCodeableConcept.getText()) &&
					HashMultiset.create(foundCodings).equals(HashMultiset.create(expectedCodings));
			if (!codingMatched) {
				return false;
			}
		}
		return true;
	}


}
