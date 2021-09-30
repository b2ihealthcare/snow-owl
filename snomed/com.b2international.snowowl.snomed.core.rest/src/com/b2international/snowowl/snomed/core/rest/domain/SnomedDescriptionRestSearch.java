/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.domain;

import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 6.16
 */
public final class SnomedDescriptionRestSearch extends SnomedComponentRestSearch {

	@Parameter(description = "The term to match")
	private String term;

	@Parameter(description = "The concept(s) or ECL expression to match")
	private List<String> concept;

	@Parameter(description = "The language code to match")
	private List<String> languageCode;

	@Parameter(description = "The type(s) or ECL expression to match")
	private List<String> type;

	@Parameter(description = "The case significance(s) or ECL expression to match")
	private List<String> caseSignificance;

	@Parameter(description = "Semantic tag(s) to match")
	private List<String> semanticTag;

	@Parameter(description = "Acceptable membership to match in these language refsets")
	private List<String> acceptableIn;

	@Parameter(description = "Preferred membership to match in these language refsets")
	private List<String> preferredIn;

	@Parameter(description = "Any membership to match in these language refsets")
	private List<String> languageRefSet;

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public List<String> getConcept() {
		return concept;
	}

	public void setConcept(List<String> concept) {
		this.concept = concept;
	}
	
	public List<String> getLanguageCode() {
		return languageCode;
	}
	
	public void setLanguageCode(List<String> languageCode) {
		this.languageCode = languageCode;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public List<String> getCaseSignificance() {
		return caseSignificance;
	}

	public void setCaseSignificance(List<String> caseSignificance) {
		this.caseSignificance = caseSignificance;
	}

	public List<String> getSemanticTag() {
		return semanticTag;
	}

	public void setSemanticTag(List<String> semanticTag) {
		this.semanticTag = semanticTag;
	}

	public List<String> getAcceptableIn() {
		return acceptableIn;
	}

	public void setAcceptableIn(List<String> acceptableIn) {
		this.acceptableIn = acceptableIn;
	}

	public List<String> getPreferredIn() {
		return preferredIn;
	}

	public void setPreferredIn(List<String> preferredIn) {
		this.preferredIn = preferredIn;
	}

	public List<String> getLanguageRefSet() {
		return languageRefSet;
	}

	public void setLanguageRefSet(List<String> languageRefSet) {
		this.languageRefSet = languageRefSet;
	}

}
