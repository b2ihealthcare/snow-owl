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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.b2international.snowowl.core.quicksearch.IQuickSearchProvider;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.datastore.IBranchPathMap;

/**
 * This interface is representing a server side service for providing the terminology independent components
 * as the underlying items for the {@link IQuickSearchProvider quick search provider} implementations.
 * 
 */
public interface IQuickSearchContentProvider {

	/**
	 * Returns a pair of hit count and an ordered collection of terminology independent components as an outcome of a terminology specific search.
	 * <p><b>NOTE:&nbsp;</b>The integer value representing the hit count for the matching items can be different as the size of the returning results.
	 * @param queryExpression the query expression for the search process. Can be {@code null}.
	 * @param branchPathMap a map of branch paths (keyed by repository UUID) where the operation should be performed. Cannot be {@code null}.
	 * @param limit the maximum number of the returning objects.
	 * @param configuration configuration for the terminology independent configuration. Can be {@code null}.
	 * @return pair of hit count and the actual matching results. The list of matching results can be empty event the hit count is greater than 0.
	 */
	QuickSearchContentResult getComponents(@Nullable final String queryExpression, @Nonnull final IBranchPathMap branchPathMap, @Nonnegative final int limit, 
			@Nullable final Map<String, Object> configuration);
	
}