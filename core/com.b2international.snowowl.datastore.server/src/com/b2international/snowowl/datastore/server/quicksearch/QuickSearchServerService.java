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
package com.b2international.snowowl.datastore.server.quicksearch;

import java.util.Map;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchService;
import com.b2international.snowowl.datastore.quicksearch.QuickSearchResponse;
import com.b2international.snowowl.datastore.server.jobs.ProcessQuickSearchItemsCallable;

/**
 * The server-side implementation of {@link IQuickSearchService}.
 * 
 */
public class QuickSearchServerService implements IQuickSearchService {

	@Override
	public QuickSearchResponse computeContents(IBranchPathMap branchPathMap, Map<String, Map<String, Object>> providerConfiguration, String queryExpression, int limit) {
		final ProcessQuickSearchItemsCallable callable = new ProcessQuickSearchItemsCallable(queryExpression, providerConfiguration, limit, branchPathMap);
		final Map<String, QuickSearchContentResult> results;

		try {
			results = callable.call();
		} catch (Exception e) {
			throw new SnowowlRuntimeException(e);
		}
		
		return new QuickSearchResponse(results); 
	}
}