/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.suggest;

import java.util.List;

import com.b2international.snowowl.core.request.suggest.Suggester;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @since 8.0
 */
public class SuggestRestParameters {

	@Parameter(description = "Code System path (or URI with query) to suggest concepts from.")
	private String from;
	
	@Parameter(description = "An array of like texts or resource URI with optional query (ECL) part. Similar terms or concepts that should be close to the suggested concepts (NOTE: they will be excluded as well).")
	private List<String> like;
	
	@Parameter(description = "An array of unlike texts or resource URI with optional query (ECL) part. Dissimilar terms or concepts that should be excluded or far from the suggested concepts.")
	private List<String> unlike;
	
	@Parameter(description = "Configuration for the selected suggester to tweak the returned suggestions")
	private Suggester suggester;
	
	@Parameter(description = "The maximum number of items to return", example = "1", schema = @Schema(defaultValue = "1"))
	private int limit = 1;

	@Parameter(description = "The preferred term display in case of SNOMED CT", example = "PT", schema = @Schema(allowableValues = {
			"FSN", "PT", "ID_ONLY" }, defaultValue = "PT"))
	private String preferredDisplay = "PT";
	
	@Parameter(description = "Accepted language tags, in order of preference (overrides Accept-Language header if specified).")
	private String acceptLanguage;

	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public List<String> getLike() {
		return like;
	}
	
	public void setLike(List<String> like) {
		this.like = like;
	}
	
	public List<String> getUnlike() {
		return unlike;
	}
	
	public void setUnlike(List<String> unlike) {
		this.unlike = unlike;
	}
	
	public Suggester getSuggester() {
		return suggester;
	}
	
	public void setSuggester(Suggester suggester) {
		this.suggester = suggester;
	}
	
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getPreferredDisplay() {
		return preferredDisplay;
	}

	public void setPreferredDisplay(String preferredDisplay) {
		this.preferredDisplay = preferredDisplay;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}
	
}
