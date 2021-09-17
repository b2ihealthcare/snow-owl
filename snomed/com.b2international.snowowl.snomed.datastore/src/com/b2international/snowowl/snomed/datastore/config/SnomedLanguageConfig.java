/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.config;

import java.util.List;

import com.b2international.commons.collections.Collections3;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.1
 */
public class SnomedLanguageConfig {

	private String languageTag;
	private List<String> languageRefSetIds;
	
	public SnomedLanguageConfig(String languageTag, String...languageRefSetIds) {
		this(languageTag, List.of(languageRefSetIds));
	}
	
	@JsonCreator
	public SnomedLanguageConfig(
			@JsonProperty("languageTag") String languageTag, 
			@JsonProperty("languageRefSetIds") List<String> languageRefSetIds) {
		this.languageTag = languageTag;
		this.languageRefSetIds = Collections3.toImmutableList(languageRefSetIds);
	}
	
	public String getLanguageTag() {
		return languageTag;
	}
	
	public List<String> getLanguageRefSetIds() {
		return languageRefSetIds;
	}
	
}
