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
package com.b2international.snowowl.snomed.datastore.quicksearch;

import java.util.Map;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.quicksearch.CompactQuickSearchElement;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.4
 */
public class MissingSnomedComponentQuickSearchContentProvider implements IQuickSearchContentProvider {

	@Override
	public QuickSearchContentResult getComponents(String queryExpression, IBranchPathMap branchPathMap, int limit, Map<String, Object> configuration) {
		try {
			SnomedIdentifiers.validate(queryExpression);
			final IBranchPath branch = branchPathMap.getBranchPath(SnomedPackage.eINSTANCE);
			if (!ApplicationContext.getInstance().getServiceChecked(SnomedTerminologyBrowser.class).exists(branch, queryExpression)) {
				return new QuickSearchContentResult(1, ImmutableList.of(new CompactQuickSearchElement(queryExpression, Concepts.ROOT_CONCEPT, queryExpression, false)));
			}
		} catch (IllegalArgumentException e) {
			// ignore invalid SNOMED CT IDs and return empty result
		}
		return new QuickSearchContentResult();
	}

}
