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

import com.b2international.snowowl.core.rest.domain.ObjectRestSearch;

import io.swagger.annotations.ApiParam;

/**
 * @since 6.16
 */
public final class SnomedConceptRestSearch extends ObjectRestSearch {

	// concept filters
	@ApiParam(value = "The effective time to match (yyyyMMdd, exact matches only)")
	private String effectiveTime;

	@ApiParam(value = "The concept status to match")
	private Boolean active = null;
	@ApiParam(value = "The concept module identifier to match")
	private String module;
	@ApiParam(value = "The definition status to match")
	private String definitionStatus;
	@ApiParam(value = "The namespace to match")
	private String namespace;

	// query expressions
	@ApiParam(value = "The ECL expression to match on the inferred form")
	private String ecl;
	@ApiParam(value = "The ECL expression to match on the stated form")
	private String statedEcl;
	@ApiParam(value = "The SNOMED CT Query expression to match (inferred form only)")
	private String query;

	// description filters
	@ApiParam(value = "Description semantic tag(s) to match")
	private String[] semanticTag;
	@ApiParam(value = "The description term to match")
	private String term;
	@ApiParam(value = "Description type ECL expression to match")
	private String descriptionType;

	// hiearchy filters
	@ApiParam(value = "The inferred parent(s) to match")
	private String[] parent;
	@ApiParam(value = "The inferred ancestor(s) to match")
	private String[] ancestor;
	@ApiParam(value = "The stated parent(s) to match")
	private String[] statedParent;
	@ApiParam(value = "The stated ancestor(s) to match")
	private String[] statedAncestor;
	@ApiParam(value = "doi (degree-of-interest-based scoring)")
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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
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