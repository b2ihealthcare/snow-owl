/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.api;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Abstract implementation that delegates the no-argument call to the context-based call with an empty context.
 * 
 *
 * @param <K>
 * @param <T>
 */
public abstract class AbstractSearchResultProvider<K extends Serializable, T extends IComponent<K>> {

	private static final Map<String, Object> EMPTY = ImmutableMap.of();
	
	/**
	 * Returns with a terminology independent component after performing a
	 * custom search. Client should implement the process of the component
	 * search.
	 * 
	 * @return the found component. This is the result of the search result.
	 */
	public T getSearchResult() {
		return getSearchResult(EMPTY);
	}
	
	/**
	 * Returns with a terminology independent component after performing a
	 * custom search. Client should implement the process of the component
	 * search.
	 * 
	 * @param searchContext key-value pairs providing additional information
	 * @return the found component. This is the result of the search result.
	 */
	public abstract T getSearchResult(Map<String, Object> searchContext);
	
}