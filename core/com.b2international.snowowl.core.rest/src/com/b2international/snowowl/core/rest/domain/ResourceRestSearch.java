/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.domain;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiParam;

/**
 * @since 7.3
 */
public abstract class ResourceRestSearch {

	@ApiParam(value = "The identifier(s) to match")
	private Set<String> id;
	
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
	@ApiParam(value = "The maximum number of items to return", defaultValue = "50")
	private int limit = 50;

	public Set<String> getId() {
		return id;
	}

	public void setId(Set<String> id) {
		this.id = id;
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
