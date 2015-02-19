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
package com.b2international.snowowl.datastore.server.index;

import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.personalization.IBookmarksManager;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;

/**
 */
public class BookmarksQuickSearchContentProvider implements IQuickSearchContentProvider {

	@Override
	public QuickSearchContentResult getComponents(@Nullable String queryExpression, @Nonnull IBranchPathMap branchPathMap, @Nonnegative int limit, @Nullable Map<String, Object> configuration) {
		return ApplicationContext.getInstance().getService(IBookmarksManager.class).getComponents(queryExpression, branchPathMap, limit, configuration);
	}
}