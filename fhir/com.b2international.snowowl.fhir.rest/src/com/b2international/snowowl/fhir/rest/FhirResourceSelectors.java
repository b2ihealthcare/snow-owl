/*
 * Copyright 2021 B2i Healthcare, https://b2ihealthcare.com
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

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 8.0
 */
public class FhirResourceSelectors {

	protected static final String PARAM_SUMMARY = "_summary";
	protected static final String PARAM_ELEMENTS = "_elements";
	
	// content selectors
	@Parameter
	private String _summary;
	
	@Parameter
	private List<String> _elements;
	
	public List<String> get_elements() {
		return _elements;
	}
	
	public String get_summary() {
		return _summary;
	}
	
	public void set_elements(List<String> _elements) {
		this._elements = _elements;
	}
	
	public void set_summary(String _summary) {
		this._summary = _summary;
	}
	
}
