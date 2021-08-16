/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.suggestion;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 8.0
 */
public class SuggestionRestParameters {
	
	@Parameter(description = "Code System path to find the concept in.")
	private String codeSystemPath;
	
	@Parameter(description = "The term to match")
	private String term;

	public String getCodeSystemPath() {
		return codeSystemPath;
	}
	
	public void setCodeSystemPath(String codeSystemPath) {
		this.codeSystemPath = codeSystemPath;
	}
	
	public String getTerm() {
		return term;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
}
