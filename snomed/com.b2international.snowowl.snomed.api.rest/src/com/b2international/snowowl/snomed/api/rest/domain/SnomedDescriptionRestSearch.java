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

import com.b2international.snowowl.snomed.core.domain.Acceptability;

import io.swagger.annotations.ApiParam;

/**
 * @since 6.16
 */
public final class SnomedDescriptionRestSearch {

	@ApiParam(value = "The Description identifier(s) to match")
	private Set<String> id;
	
	@ApiParam(value="The effective time to match (yyyyMMdd, exact matches only)")
	private String effectiveTime;
	
	@ApiParam(value="The status to match")
	private Boolean active;

	@ApiParam(value="The module identifier to match")
	private String module;
	
	@ApiParam(value="The namespace to match")
	private String namespace;
	
	@ApiParam(value="The term to match")
	private String term;

	@ApiParam(value="The concept ECL expression to match")
	private String concept;
	
	@ApiParam(value="The type ECL expression to match")
	private String type;
	
	@ApiParam(value="The case significance ECL expression to match")
	private String caseSignificance;

	@ApiParam(value="Semantic tag(s) to match")
	private String[] semanticTag;
	
	@ApiParam(value="The acceptability to match. DEPRECATED! Use acceptableIn or preferredIn!")
	private Acceptability acceptability;
	
	@ApiParam(value="Acceptable membership to match in these language refsets")
	private String[] acceptableIn;
	
	@ApiParam(value="Preferred membership to match in these language refsets")
	private String[] preferredIn;
	
	@ApiParam(value="Any membership to match in these language refsets")
	private String[] languageRefSet;
	
	@ApiParam(value="The scrollKeepAlive to start a scroll using this query")
	private String scrollKeepAlive;
	
	@ApiParam(value="A scrollId to continue scrolling a previous query")
	private String scrollId;
	
	@ApiParam(value="The search key to use for retrieving the next page of results")
	private String searchAfter;

	@ApiParam(value="The maximum number of items to return")
	private int limit = 50;
	
	@ApiParam(value="Expansion parameters")
	private String expand;
	
	@ApiParam(value="Sort keys")
	private List<String> sort;

	public Set<String> getId() {
		return id;
	}
	
	public void setId(Set<String> id) {
		this.id = id;
	}
	
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

	public Acceptability getAcceptability() {
		return acceptability;
	}

	public void setAcceptability(Acceptability acceptability) {
		this.acceptability = acceptability;
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

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getExpand() {
		return expand;
	}

	public void setExpand(String expand) {
		this.expand = expand;
	}

	public List<String> getSort() {
		return sort;
	}

	public void setSort(List<String> sort) {
		this.sort = sort;
	}
	
	
	
}
