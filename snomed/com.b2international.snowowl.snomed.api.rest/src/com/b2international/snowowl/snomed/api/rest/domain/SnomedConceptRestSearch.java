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
package com.b2international.snowowl.snomed.api.rest.domain;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiParam;

/**
 * @since 6.16
 */
public final class SnomedConceptRestSearch {

	// concept filters
	@ApiParam(value = "The Concept identifier(s) to match")
	private Set<String> id;
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
	private String[] parents;
	@ApiParam(value = "The inferred ancestor(s) to match")
	private String[] ancestors;
	@ApiParam(value = "The stated parent(s) to match")
	private String[] statedParents;
	@ApiParam(value = "The stated ancestor(s) to match")
	private String[] statedAncestors;

	// scrolling/paging/expansion/sorting
	@ApiParam(value = "Expansion parameters")
	private String expand;
	@ApiParam(value = "The scrollKeepAlive to start a scroll using this query")
	private String scrollKeepAlive;
	@ApiParam(value = "A scrollId to continue scrolling a previous query")
	private String scrollId;
	@ApiParam(value = "The search key to use for retrieving the next page of results")
	private String searchAfter;
	@ApiParam(value = "Sort keys")
	private List<String> sort;
	@ApiParam(value = "The maximum number of items to return")
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

	public String[] getParents() {
		return parents;
	}

	public void setParents(String[] parents) {
		this.parents = parents;
	}

	public String[] getAncestors() {
		return ancestors;
	}

	public void setAncestors(String[] ancestors) {
		this.ancestors = ancestors;
	}

	public String[] getStatedParents() {
		return statedParents;
	}

	public void setStatedParents(String[] statedParents) {
		this.statedParents = statedParents;
	}

	public String[] getStatedAncestors() {
		return statedAncestors;
	}

	public void setStatedAncestors(String[] statedAncestors) {
		this.statedAncestors = statedAncestors;
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