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
package com.b2international.snowowl.snomed.core.rest.domain;

import java.util.List;

import com.b2international.snowowl.core.rest.domain.ObjectRestSearch;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 8.0
 */
public class SnomedRestSearch extends ObjectRestSearch {

	@Parameter(description = "The effective time value to match (yyyyMMdd or Unpublished)")
	private String effectiveTime;

	@Parameter(description = "The concept status to match")
	private Boolean active = null;
	
	@Parameter(description = "The concept module identifier(s) or ECL expression to match")
	private List<String> module;
	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<String> getModule() {
		return module;
	}

	public void setModule(List<String> module) {
		this.module = module;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
}
