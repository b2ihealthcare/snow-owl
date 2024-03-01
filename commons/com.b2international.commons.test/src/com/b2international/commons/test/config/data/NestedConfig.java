/*
 * Copyright 2011-2024 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.test.config.data;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 3.4
 */
public class NestedConfig {

	@NotEmpty
	private String globalParameter;
	
	@NotNull
	private TestConfig nestedConfig;
	
	@JsonProperty("config")
	public TestConfig getNestedConfig() {
		return nestedConfig;
	}
	
	public String getGlobalParameter() {
		return globalParameter;
	}

	@JsonProperty("config")
	public void setNestedConfig(TestConfig nestedConfig) {
		this.nestedConfig = nestedConfig;
	}
	
	public void setGlobalParameter(String globalParameter) {
		this.globalParameter = globalParameter;
	}
	
}