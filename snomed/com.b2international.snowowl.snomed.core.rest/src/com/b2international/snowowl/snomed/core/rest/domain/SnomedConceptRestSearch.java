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
public final class SnomedConceptRestSearch extends SnomedComponentRestSearch {

	@Parameter(description = "The definition status to match")
	private String definitionStatus;

	// query expressions
	@Parameter(description = "The ECL expression to match on the inferred form")
	private String ecl;
	@Parameter(description = "The ECL expression to match on the stated form")
	private String statedEcl;

	// description filters
	@Parameter(description = "Description semantic tag(s) to match")
	private List<String> semanticTag;
	@Parameter(description = "The description term to match")
	private String term;
	@Parameter(description = "Description type ECL expression to match")
	private String descriptionType;

	// hiearchy filters
	@Parameter(description = "The inferred parent(s) to match")
	private List<String> parent;
	@Parameter(description = "The inferred ancestor(s) to match")
	private List<String> ancestor;
	@Parameter(description = "The stated parent(s) to match")
	private List<String> statedParent;
	@Parameter(description = "The stated ancestor(s) to match")
	private List<String> statedAncestor;

	@Parameter(description = "doi (degree-of-interest-based scoring)")
	private Boolean doi = null;

	public String getDefinitionStatus() {
		return definitionStatus;
	}

	public void setDefinitionStatus(String definitionStatus) {
		this.definitionStatus = definitionStatus;
	}

	public List<String> getParent() {
		return parent;
	}

	public void setParent(List<String> parent) {
		this.parent = parent;
	}

	public List<String> getAncestor() {
		return ancestor;
	}

	public void setAncestor(List<String> ancestor) {
		this.ancestor = ancestor;
	}

	public List<String> getStatedParent() {
		return statedParent;
	}

	public void setStatedParents(List<String> statedParent) {
		this.statedParent = statedParent;
	}

	public List<String> getStatedAncestor() {
		return statedAncestor;
	}

	public void setStatedAncestor(List<String> statedAncestor) {
		this.statedAncestor = statedAncestor;
	}

	public String getEcl() {
		return ecl;
	}

	public void setEcl(String ecl) {
		this.ecl = ecl;
	}

	public String getStatedEcl() {
		return statedEcl;
	}
	
		public void setStatedEcl(String statedEcl) {
		this.statedEcl = statedEcl;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public List<String> getSemanticTag() {
		return semanticTag;
	}

	public void setSemanticTag(List<String> semanticTag) {
		this.semanticTag = semanticTag;
	}

	public String getDescriptionType() {
		return descriptionType;
	}

	public void setDescriptionType(String descriptionType) {
		this.descriptionType = descriptionType;
	}
	
	public Boolean getDoi() {
		return doi;
	}
	
	public void setDoi(Boolean doi) {
		this.doi = doi;
	}
	
}