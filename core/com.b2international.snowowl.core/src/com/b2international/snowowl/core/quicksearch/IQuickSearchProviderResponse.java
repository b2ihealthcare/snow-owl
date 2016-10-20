/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.quicksearch;

import java.util.List;

/**
 * Represents the response from a single quick search provider.
 * 
 */
public interface IQuickSearchProviderResponse {

	/**
	 * Returns the name of the quick search provider.
	 * 
	 * @return the name of the quick search provider
	 */
	String getProviderName();
	
	/**
	 * Returns the total number of matches for the last expression received via {@link #filterElements(String)}.
	 * 
	 * @return the number of matches
	 */
	int getTotalHitCount();

	/**
	 * Returns the {@link IQuickSearchProviderResponseEntry quick search result entries} found by this provider.
	 * 
	 * @return the quick search result entries
	 */
	List<QuickSearchElement> getEntries();
}
