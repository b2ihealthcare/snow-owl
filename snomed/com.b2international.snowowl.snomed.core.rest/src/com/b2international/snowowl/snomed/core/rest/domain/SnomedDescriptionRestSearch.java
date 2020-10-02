/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.rest.domain.ResourceRestSearch;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 6.16
 */
public final class SnomedDescriptionRestSearch extends ResourceRestSearch {

	@Parameter(description = "The effective time to match (yyyyMMdd, exact matches only)")
	private String effectiveTime;

	@Parameter(description = "The status to match")
	private Boolean active;

	@Parameter(description = "The module identifier to match")
	private String module;

	@Parameter(description = "The namespace to match")
	private String namespace;

	@Parameter(description = "The term to match")
	private String term;

	@Parameter(description = "The concept ECL expression to match")
	private String concept;

	@Parameter(description = "The language code to match")
	private String[] languageCode;

	@Parameter(description = "The type ECL expression to match")
	private String type;

	@Parameter(description = "The case significance ECL expression to match")
	private String caseSignificance;

	@Parameter(description = "Semantic tag(s) to match")
	private String[] semanticTag;

	@Parameter(description = "Acceptable membership to match in these language refsets")
	private String[] acceptableIn;

	@Parameter(description = "Preferred membership to match in these language refsets")
	private String[] preferredIn;

	@Parameter(description = "Any membership to match in these language refsets")
	private String[] languageRefSet;

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}
	
	public String[] getLanguageCode() {
		return languageCode;
	}
	
	public void setLanguageCode(String[] languageCode) {
		this.languageCode = languageCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCaseSignificance() {
		return caseSignificance;
	}

	public void setCaseSignificance(String caseSignificance) {
		this.caseSignificance = caseSignificance;
	}

	public String[] getSemanticTag() {
		return semanticTag;
	}

	public void setSemanticTag(String[] semanticTag) {
		this.semanticTag = semanticTag;
	}

	public String[] getAcceptableIn() {
		return acceptableIn;
	}

	public void setAcceptableIn(String[] acceptableIn) {
		this.acceptableIn = acceptableIn;
	}

	public String[] getPreferredIn() {
		return preferredIn;
	}

	public void setPreferredIn(String[] preferredIn) {
		this.preferredIn = preferredIn;
	}

	public String[] getLanguageRefSet() {
		return languageRefSet;
	}

	public void setLanguageRefSet(String[] languageRefSet) {
		this.languageRefSet = languageRefSet;
	}

}
