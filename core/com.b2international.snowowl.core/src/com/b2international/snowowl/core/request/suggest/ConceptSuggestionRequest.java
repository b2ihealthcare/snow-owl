/*
 * Copyright 2020-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.suggest;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.ConceptSearchRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A generic concept suggestion request that uses the generic search
 * functionality to return related concepts of interest to the user.
 * 
 * @since 7.7
 * @see ConceptSearchRequest
 * @see ConceptSuggestionRequestBuilder
 */
public final class ConceptSuggestionRequest extends SearchResourceRequest<ServiceProvider, Suggestions> {

	private static final long serialVersionUID = 2L;
	
	/**
	 * @since 8.5
	 */
	enum OptionKey {
		
		/**
		 * Specifies the source code system (URI with optional query part) where suggestions should come from
		 */
		FROM,
		
		/**
		 * Specifies an array of like texts or concepts to use when suggesting concepts. Usually suggested concepts should be close to these. If these are actual concepts, then the suggester should not return them.
		 */
		LIKE,
		
		/**
		 * Specifies an array of unlike texts or concepts to use when suggesting concepts. Usually suggested concepts should be far from these. If these are actual concepts, then the suggester should not return them.
		 */
		UNLIKE,
		
		/**
		 * Specifies the display term to return for the suggested concepts
		 */
		DISPLAY,
		
	}
	
	@JsonProperty
	private Suggester suggester;
	
	void setSuggester(Suggester suggester) {
		this.suggester = suggester;
	}

	@Override
	protected Suggestions createEmptyResult(int limit) {
		return new Suggestions(null, limit, 0);
	}

	@Override
	protected Suggestions doExecute(ServiceProvider context) throws IOException {
		final ConceptSuggester.Registry suggesterRegistry = context.service(ConceptSuggester.Registry.class);
		// suggester is mandatory, otherwise we can't suggest
		if (suggester == null) {
			final Set<String> availableSuggesters = suggesterRegistry.getSuggesterTypes();		
			throw new BadRequestException("'suggester' is required. Available suggesters are: %s", availableSuggesters);
		}
		
		// from argument is required to suggests concepts
		if (!containsKey(OptionKey.FROM)) {
			throw new BadRequestException("'from' argument is required to suggest concepts from.");
		}
		
		// like argument is required to suggest concepts
		if (!containsKey(OptionKey.LIKE)) {
			throw new BadRequestException("At least one like argument is required to generate suggestions.");
		}
		
		return suggesterRegistry.create(this.suggester)
				.suggest(
					new ConceptSuggestionContext(context, getString(OptionKey.FROM), getList(OptionKey.LIKE, String.class), getList(OptionKey.UNLIKE, String.class), locales()),
					limit(),
					getString(OptionKey.DISPLAY),
					locales()
				)
				.getSync(3, TimeUnit.MINUTES);
		
	}

}
