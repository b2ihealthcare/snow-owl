/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 6.16
 */
public final class SnomedConceptRestSearch {

	// concept filters
	@Parameter(description = "The Concept identifier(s) to match")
	private Set<String> id;
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
	@Parameter(description = "The SNOMED CT Query expression to match (inferred form only)")
	private String query;

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

	// scrolling/paging/expansion/sorting
	@Parameter(description = "Expansion parameters")
	private String expand;
	@Parameter(description = "The scrollKeepAlive to start a scroll using this query")
	private String scrollKeepAlive;
	@Parameter(description = "A scrollId to continue scrolling a previous query")
	private String scrollId;
	@Parameter(description = "The search key to use for retrieving the next page of results")
	private String searchAfter;
	@Parameter(description = "Sort keys")
	private List<String> sort;
	@Parameter(description = "The maximum number of items to return")
	private int limit = 50;

	public Set<String> getId() {
		return id;
	}

	public void setId(Set<String> id) {
		this.id = id;
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

	public String getExpand() {
		return expand;
	}

	public void setExpand(String expand) {
		this.expand = expand;
	}

	public String getScrollKeepAlive() {
		return scrollKeepAlive;
	}

	public void setScrollKeepAlive(String scrollKeepAlive) {
		this.scrollKeepAlive = scrollKeepAlive;
	}

	public String getScrollId() {
		return scrollId;
	}

	public void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}

	public String getSearchAfter() {
		return searchAfter;
	}

	public void setSearchAfter(String searchAfter) {
		this.searchAfter = searchAfter;
	}

	public List<String> getSort() {
		return sort;
	}

	public void setSort(List<String> sort) {
		this.sort = sort;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}