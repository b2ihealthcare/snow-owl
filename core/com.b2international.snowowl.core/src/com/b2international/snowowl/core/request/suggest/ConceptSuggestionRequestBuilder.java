/*
 * Copyright 2020-2023 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator.OptionKey;
import com.b2international.snowowl.core.request.SearchPageableCollectionResourceRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 7.7
 */
public final class ConceptSuggestionRequestBuilder
		extends SearchPageableCollectionResourceRequestBuilder<ConceptSuggestionRequestBuilder, ServiceProvider, Suggestions>
		implements SystemRequestBuilder<Suggestions> {

	private Suggester suggester;
	
	/**
	 * Selects the substrate where the suggestions can come from. 
	 * @param from - supports any code system {@link ResourceURI} optionally with {@link ResourceURIWithQuery query part (ECL)} 
	 * @return
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setFrom(String from) {
		return addOption(ConceptSuggestionRequest.OptionKey.FROM, from);
	}
	
	/**
	 * Configures the suggester to run when suggesting concepts.
	 * 
	 * @param suggester - configure the suggester type and its optional settings
	 * @return 
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setSuggester(Suggester suggester) {
		this.suggester = suggester;
		return getSelf();
	}
	
	/**
	 * Suggest concepts that are similar to the text or concepts (selected via ECL query) specified here. 
	 * 
	 * @param likeTextOrQuery - supports any text as input, usually free text, terms or {@link ResourceURIWithQuery URIs with optional queries} 
	 * @return
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setLike(String likeTextOrQuery) {
		return addOption(ConceptSuggestionRequest.OptionKey.LIKE, likeTextOrQuery);
	}
	
	/**
	 * Suggest concepts that are similar to the array of text or concepts (selected via an array of ECL queries) specified here.
	 * 
	 * @param likeTextsOrQueries - supports any text as input, usually free text, terms or {@link ResourceURIWithQuery URIs with optional queries}
	 * @return
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setLike(Iterable<String> likeTextsOrQueries) {
		return addOption(ConceptSuggestionRequest.OptionKey.LIKE, likeTextsOrQueries);
	}
	
	/**
	 * Suggest concepts that are dissimilar to the text or concepts (selected via ECL query) specified here.
	 * 
	 * @param unlikeTextOrQuery - supports any text as input, usually free text, terms or {@link ResourceURIWithQuery URIs with optional queries}
	 * @return
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setUnlike(String unlikeTextOrQuery) {
		return addOption(ConceptSuggestionRequest.OptionKey.UNLIKE, unlikeTextOrQuery);
	}
	
	/**
	 * Suggest concepts that are dissimilar to the array of texts or concepts (selected via an array of ECL queries) specified here.
	 * 
	 * @param unlikeTextsOrQueries - supports any text as input, usually free text, terms or {@link ResourceURIWithQuery URIs with optional queries}
	 * @return
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setUnlike(Iterable<String> unlikeTextsOrQueries) {
		return addOption(ConceptSuggestionRequest.OptionKey.UNLIKE, unlikeTextsOrQueries);
	}
	
	/**
	 * Set the {@link SnomedDisplayTermType} of the returning term in case of SNOMED.
	 * 
	 * @param display
	 *            - the display type
	 * @return
	 */
	public ConceptSuggestionRequestBuilder setPreferredDisplay(final String display) {
		return addOption(OptionKey.DISPLAY, display);
	}
	
	@Override
	protected SearchResourceRequest<ServiceProvider, Suggestions> createSearch() {
		ConceptSuggestionRequest req = new ConceptSuggestionRequest();
		req.setSuggester(suggester);
		return req;
	}

}
