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
 * Represents a quick search result entry.
 * 
 */
public interface IQuickSearchProviderResponseEntry {

	/**
	 * Returns the identifier of the entry.
	 * 
	 * @return the identifier
	 */
	String getId();
	
	/**
	 * Returns the label of the entry.
	 * 
	 * @return the label
	 */
	String getLabel();
	
	/**
	 * Returns <code>true</code> if the result entry represents an approximate match, <code>false</code> otherwise.
	 * 
	 * @return true if the result entry represents an approximate match, false otherwise
	 */
	boolean isApproximate();
	
	/**
	 * Returns the URL of the image associated with the result entry.
	 * 
	 * @return the URL of the associated image
	 */
	String getImageUrl();
	
	/**
	 * Returns the {@link IQuickSearchMatchRegion match regions} within the result entry.
	 * 
	 * @return the match regions
	 */
	List<IQuickSearchMatchRegion> getMatchRegions();

}