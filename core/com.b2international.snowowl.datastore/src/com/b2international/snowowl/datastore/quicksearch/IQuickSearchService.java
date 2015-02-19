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
package com.b2international.snowowl.datastore.quicksearch;

import java.util.Map;

import com.b2international.snowowl.datastore.IBranchPathMap;

/**
 * Represents the RPC interface for computing the full contents of a quick search dialog on the server.
 * 
 */
public interface IQuickSearchService {

	/**
	 * 
	 * @param branchPathMap
	 * @param providerConfiguration
	 * @param queryExpression
	 * @param limit
	 * @return
	 */
	public QuickSearchResponse computeContents(final IBranchPathMap branchPathMap, final Map<String, Map<String, Object>> providerConfiguration, 
			final String queryExpression, final int limit);
}