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
public final class SnomedReferenceSetMemberRestSearch {

	@ApiParam(value = "Member UUID(s) to match")
	private Set<String> id;
	
	@ApiParam(value="The effective time to match (yyyyMMdd, exact matches only)")
	private String effectiveTime;
	
	@ApiParam(value="The status to match")
	private Boolean active;
	
	@ApiParam(value="The module identifier to match")
	private String module;
	
	@ApiParam(value="The reference set identifier(s) to match, or a single ECL expression")
	private List<String> referenceSet;
	
	@ApiParam(value="The referenced component identifier(s) to match")
	private List<String> referencedComponentId;

	// Special RF2 member columns go here
	// TODO figure out how to dynamically include query params with swagger, or just replace swagger with a better alternative???
	@ApiParam(value="The target component identifier(s) to match in case of association refset members")
	private List<String> targetComponent;
	
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

	public List<String> getReferenceSet() {
		return referenceSet;
	}

	public void setReferenceSet(List<String> referenceSet) {
		this.referenceSet = referenceSet;
	}

	public List<String> getReferencedComponentId() {
		return referencedComponentId;
	}

	public void setReferencedComponentId(List<String> referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
	}

	public List<String> getTargetComponent() {
		return targetComponent;
	}

	public void setTargetComponent(List<String> targetComponent) {
		this.targetComponent = targetComponent;
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
