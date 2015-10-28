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

public interface QuickSearchExtractor {

	/**
	 * Computes and returns additional information from the specified quick search element. This includes:
	 * <ul>
	 * <li>highlighted regions which match the search term and should be displayed prominently;
	 * <li>suggestions for suffixes with which the user can extend their input. 
	 * </ul> 
	 * 
	 * @param element the element to process
	 * @return the extracted highlighting and suffix information
	 */
	QuickSearchElementInfo process(QuickSearchElement element);
}
