/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest;

import java.util.List;

import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @since 8.0
 */
public class FhirResourceSearchParameters extends FhirResourceSelectors {

	// filters
	@Parameter(description = "_id")
	private String[] _id;
	
	@Parameter(description = "name")
	private String[] name;
	
	@Parameter(description = "title")
	private String title;
	
	@Parameter(description = "_lastUpdated")
	private String _lastUpdated;
	
	@Parameter(description = "_content")
	private String _content;
	
	@Parameter(description = "url")
	private List<String> url;
	
	@Parameter(description = "system")
	private List<String> system;
	
	@Parameter(description = "version")
	private List<String> version;
	
	// paging
	@Parameter(description = "The maximum number of items to return", schema = @Schema(defaultValue = "10"))
	private int _count = 10;
	
	@Schema
	private String[] _sort;
	
	// extensions (paging)
	@Schema
	private String _after;

	public String[] get_id() {
		return _id;
	}
	
	public String get_after() {
		return _after;
	}
	
	public String get_content() {
		return _content;
	}
	
	public int get_count() {
		return _count;
	}
	
	public String get_lastUpdated() {
		return _lastUpdated;
	}
	
	public String[] getName() {
		return name;
	}
	
	public String[] get_sort() {
		return _sort;
	}
	
	public String getTitle() {
		return title;
	}
	
	public List<String> getUrl() {
		return url;
	}
	
	public List<String> getSystem() {
		return system;
	}
	
	public List<String> getVersion() {
		return version;
	}
	
	public void set_id(String[] _id) {
		this._id = _id;
	}
	
	public void set_after(String _after) {
		this._after = _after;
	}
	
	public void set_content(String _content) {
		this._content = _content;
	}
	
	public void set_count(int _count) {
		this._count = _count;
	}
	
	public void set_lastUpdated(String _lastUpdated) {
		this._lastUpdated = _lastUpdated;
	}
	
	public void setName(String[] name) {
		this.name = name;
	}
	
	public void set_sort(String[] _sort) {
		this._sort = _sort;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setUrl(List<String> url) {
		this.url = url;
	}
	
	public void setSystem(List<String> system) {
		this.system = system;
	}
	
	public void setVersion(List<String> version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.omitNullValues()
				.add("_id", _id)
				.add("name", name)
				.add("title", title)
				.add("_lastUpdated", _lastUpdated)
				.add("_content", _content)
				.add("url", url)
				.add("system", system)
				.add("_summary", get_summary())
				.add("_elements", get_elements())
				.add("_count", _count)
				.add("_sort", _sort)
				.add("_after", _after)
				.toString();
	}
	
}
