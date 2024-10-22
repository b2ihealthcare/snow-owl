/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.Set;
import java.util.SortedSet;

import org.hl7.fhir.exceptions.FHIRFormatError;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @since 8.0
 */
public class FhirResourceSearchParameters extends FhirResourceSelectors {

	private static final String PARAM_ID = "_id";
	private static final String PARAM_NAME = "name";
	private static final String PARAM_TITLE = "title";
	private static final String PARAM_LAST_UPDATED = "_lastUpdated";
	private static final String PARAM_CONTENT = "_content";
	private static final String PARAM_URL = "url";
	private static final String PARAM_SYSTEM = "system";
	private static final String PARAM_VERSION = "version";
	private static final String PARAM_STATUS = "status";
	private static final String PARAM_COUNT = "_count";
	private static final String PARAM_SORT = "_sort";
	private static final String PARAM_AFTER = "_after";

	private static final SortedSet<String> ACCEPTED_PARAMS = ImmutableSortedSet.of(
		PARAM_ID,
		PARAM_NAME ,
		PARAM_TITLE,
		PARAM_LAST_UPDATED,
		PARAM_CONTENT,
		PARAM_URL,
		PARAM_SYSTEM,
		PARAM_VERSION,
		PARAM_STATUS,
		PARAM_COUNT,
		PARAM_SORT,
		PARAM_AFTER,				
		PARAM_SUMMARY,
		PARAM_ELEMENTS
	);

	// filters
	@Parameter(description = PARAM_ID)
	private String[] _id;
	
	@Parameter(description = PARAM_NAME)
	private String[] name;
	
	@Parameter(description = PARAM_TITLE)
	private String title;
	
	@Parameter(description = PARAM_LAST_UPDATED)
	private String _lastUpdated;
	
	@Parameter(description = PARAM_CONTENT)
	private String _content;
	
	@Parameter(description = PARAM_URL)
	private List<String> url;
	
	@Parameter(description = PARAM_SYSTEM)
	private List<String> system;
	
	@Parameter(description = PARAM_VERSION)
	private List<String> version;
	
	@Parameter(description = PARAM_STATUS)
	private List<String> status;
	
	// paging
	@Parameter(description = "The maximum number of items to return", schema = @Schema(defaultValue = "10"))
	private int _count = 10;
	
	@Schema
	private String[] _sort;
	
	// extensions (paging)
	@Schema
	private String _after;

	private final Set<String> unknownParameterNames = Sets.newHashSet();
	
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
	
	public List<String> getStatus() {
		return status;
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
	
	public void setStatus(List<String> status) {
		this.status = status;
	}
	
	@JsonAnySetter
	public void setAdditionalParameter(String parameterName, Object _parameterValue) {
		// We are only interested in the name of each unknown search parameter but the method signature needs to include the value
		if (!ACCEPTED_PARAMS.contains(parameterName)) {
			unknownParameterNames.add(parameterName);
		}
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
			.omitNullValues()
			.add(PARAM_ID, _id)
			.add(PARAM_NAME, name)
			.add(PARAM_TITLE, title)
			.add(PARAM_LAST_UPDATED, _lastUpdated)
			.add(PARAM_CONTENT, _content)
			.add(PARAM_URL, url)
			.add(PARAM_SYSTEM, system)
			.add(PARAM_VERSION, version)
			.add(PARAM_STATUS, status)
			.add(PARAM_COUNT, _count)
			.add(PARAM_SORT, _sort)
			.add(PARAM_AFTER, _after)
			.add(PARAM_SUMMARY, get_summary())
			.add(PARAM_ELEMENTS, get_elements())
			.toString();
	}
	
	/**
	 * @throws FHIRFormatError - if there are unknown/unrecognized parameters specified
	 */ 
	public final void checkParameters() {
		Set<String> acceptedParameterNames = getAcceptedParameterNames();
		
		if (acceptedParameterNames == null || acceptedParameterNames.isEmpty()) {
			return;
		}
		
		if (!unknownParameterNames.isEmpty()) {
			throw new FHIRFormatError(String.format("Unknown/Unsupported parameters found in the request '%s'. Accepted parameters are: %s.", unknownParameterNames, acceptedParameterNames));
		}
	}
	
	/**
	 * Subclasses may optionally override this method to provide support for parameter validation via the {@link #checkParameters(boolean)} method.
	 * @return
	 */
	protected SortedSet<String> getAcceptedParameterNames() {
		return ACCEPTED_PARAMS;
	}	
}
