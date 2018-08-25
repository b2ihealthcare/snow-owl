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
package com.b2international.snowowl.snomed.datastore.request.rf2.validation;

import java.util.Collection;

import org.slf4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 7.0
 */
public final class Rf2ValidationIssueReporter {
	
	private static final int MAX_NUMBER_OF_VALIDATION_PROBLEMS = 100;
	
	private Multimap<Rf2ValidationType, String> validationProblems = ArrayListMultimap.create();
	
	public Rf2ValidationIssueReporter() {

	}
	
	public void error(String validationMessage) {
		if (getNumberOfErrors() < MAX_NUMBER_OF_VALIDATION_PROBLEMS) {
			validationProblems.put(Rf2ValidationType.ERROR, validationMessage);
		}
	}
	
	public void warning(String validationMessage) {
		if (getNumberOfWarnings() < MAX_NUMBER_OF_VALIDATION_PROBLEMS) {
			validationProblems.put(Rf2ValidationType.WARNING, validationMessage);
		}
	}
	
	public int getNumberOfErrors() {
		return validationProblems.get(Rf2ValidationType.ERROR).size();
	}
	
	public int getNumberOfWarnings() {
		return validationProblems.get(Rf2ValidationType.WARNING).size();
	}
	
	public Collection<String> getErrors() {
		return validationProblems.get(Rf2ValidationType.ERROR);

	}
	
	public Collection<String> getWarnings() {
		return validationProblems.get(Rf2ValidationType.WARNING);
	}
	
	public Collection<String> getIssues() {
		return validationProblems.values();
	}

	public void logWarnings(Logger log) {
		getWarnings().forEach(log::warn);;
	}
	
	public void logErrors(Logger log) {
		getErrors().forEach(log::error);
	}
	
}
