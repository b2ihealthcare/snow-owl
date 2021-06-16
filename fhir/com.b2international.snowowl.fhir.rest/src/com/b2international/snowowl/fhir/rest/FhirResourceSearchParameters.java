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

import com.google.common.base.MoreObjects;

import io.swagger.annotations.ApiParam;

/**
 * @since 8.0
 */
public class FhirResourceSearchParameters {

	// filters
	@ApiParam(value = "_id")
	String[] _id;
	@ApiParam(value = "_name")
	String[] _name;
	@ApiParam(value = "_title")
	String _title;
	@ApiParam(value = "_lastUpdated")
	String _lastUpdated;
	@ApiParam
	String _content;
	
	// content selectors
	@ApiParam
	String _summary;
	@ApiParam
	String[] _elements;
	
	// paging
	@ApiParam(value = "The maximum number of items to return", defaultValue = "10")
	int _count = 10;
	
	@ApiParam
	String[] _sort;
	
	// extensions (paging)
	@ApiParam
	String _after;

	public String[] getId() {
		return _id;
	}
	
	public String getAfter() {
		return _after;
	}
	
	public String getContent() {
		return _content;
	}
	
	public int getCount() {
		return _count;
	}
	
	public String[] getElements() {
		return _elements;
	}

	public String getLastUpdated() {
		return _lastUpdated;
	}
	
	public String[] getName() {
		return _name;
	}
	
	public String[] getSort() {
		return _sort;
	}
	
	public String getSummary() {
		return _summary;
	}
	
	public String getTitle() {
		return _title;
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
	
	public void set_elements(String[] _elements) {
		this._elements = _elements;
	}
	
	public void set_lastUpdated(String _lastUpdated) {
		this._lastUpdated = _lastUpdated;
	}
	
	public void set_name(String[] _name) {
		this._name = _name;
	}
	
	public void set_sort(String[] _sort) {
		this._sort = _sort;
	}
	
	public void set_summary(String _summary) {
		this._summary = _summary;
	}
	
	public void set_title(String _title) {
		this._title = _title;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.omitNullValues()
				.add("_id", _id)
				.add("_name", _name)
				.add("_title", _title)
				.add("_lastUpdated", _lastUpdated)
				.add("_content", _content)
				.add("_summary", _summary)
				.add("_elements", _elements)
				.add("_count", _count)
				.add("_sort", _sort)
				.add("_after", _after)
				.toString();
	}
	
}
