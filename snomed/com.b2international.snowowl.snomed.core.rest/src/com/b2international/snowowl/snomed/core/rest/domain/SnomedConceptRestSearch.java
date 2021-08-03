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

import com.b2international.snowowl.core.rest.domain.ObjectRestSearch;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 6.16
 */
public final class SnomedConceptRestSearch extends ObjectRestSearch {

	// concept filters
	@Parameter(description = "The effective time to match (yyyyMMdd, exact matches only)")
	private String effectiveTime;

	@Parameter(description = "The concept status to match")
	private Boolean active = null;
	@Parameter(description = "The concept module identifier to match")
	private String module;
	@Parameter(description = "The definition status to match")
	private String definitionStatus;
	@Parameter(description = "The namespace to match")
	private String namespace;

	// query expressions
	@Parameter(description = "The ECL expression to match on the inferred form")
	private String ecl;
	@Parameter(description = "The ECL expression to match on the stated form")
	private String statedEcl;

	// description filters
	@Parameter(description = "Description semantic tag(s) to match")
	private String[] semanticTag;
	@Parameter(description = "The description term to match")
	private String term;
	@Parameter(description = "Description type ECL expression to match")
	private String descriptionType;

	// hiearchy filters
	@Parameter(description = "The inferred parent(s) to match")
	private String[] parent;
	@Parameter(description = "The inferred ancestor(s) to match")
	private String[] ancestor;
	@Parameter(description = "The stated parent(s) to match")
	private String[] statedParent;
	@Parameter(description = "The stated ancestor(s) to match")
	private String[] statedAncestor;
	@Parameter(description = "doi (degree-of-interest-based scoring)")
	private Boolean doi = null;

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

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getDefinitionStatus() {
		return definitionStatus;
	}

	public void setDefinitionStatus(String definitionStatus) {
		this.definitionStatus = definitionStatus;
	}

	public String[] getParent() {
		return parent;
	}

	public void setParent(String[] parent) {
		this.parent = parent;
	}

	public String[] getAncestor() {
		return ancestor;
	}

	public void setAncestor(String[] ancestor) {
		this.ancestor = ancestor;
	}

	public String[] getStatedParent() {
		return statedParent;
	}

	public void setStatedParents(String[] statedParent) {
		this.statedParent = statedParent;
	}

	public String[] getStatedAncestor() {
		return statedAncestor;
	}

	public void setStatedAncestor(String[] statedAncestor) {
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

	public String[] getSemanticTag() {
		return semanticTag;
	}

	public void setSemanticTag(String[] semanticTag) {
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