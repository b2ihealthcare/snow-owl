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
package com.b2international.snowowl.scripting.services;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.scripting.services.api.IQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedDOIQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.collect.Iterables;

/**
 * Service for evaluating index queries.
 * @see IQueryEvaluatorService
 */
public class IndexQueryEvaluatorService implements IQueryEvaluatorService<SnomedConceptIndexEntry> {

	private final String[] ids;

	/**
	 * Initialize a service instance.
	 */
	public IndexQueryEvaluatorService() {
		this(Collections.<IComponent<String>>emptySet());
	}

	/**
	 * Initialize an index query evaluator service with a set of SNOMED&nbsp;CT concepts.
	 * <p>The index query evaluation will be limited to the specified subset of concepts.
	 * @param ids a subset of SNOMED&nbsp;CT concepts to limit the index query evaluation.
	 */
	public IndexQueryEvaluatorService(final Collection<IComponent<String>> ids) {
		this.ids = Iterables.toArray(ComponentUtils.getIds(ids), String.class);
	}

	@Override
	public List<SnomedConceptIndexEntry> evaluate(final String queryExpression) {
		return search(new SnomedDOIQueryAdapter(queryExpression, ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getUserId(), ids));
	}

	@Override
	public List<String> evaluateForIds(String queryExpression) {
		return newArrayList(ComponentUtils.getIds(evaluate(queryExpression)));
	}
	
	private List<SnomedConceptIndexEntry> search(final SnomedDOIQueryAdapter snomedDOIQueryAdapter) {
		return 0 == ids.length
			? getIndexService().search(snomedDOIQueryAdapter)
			: getIndexService().search(snomedDOIQueryAdapter, 100);
	}

	private SnomedClientIndexService getIndexService() {
		return ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
	}

}