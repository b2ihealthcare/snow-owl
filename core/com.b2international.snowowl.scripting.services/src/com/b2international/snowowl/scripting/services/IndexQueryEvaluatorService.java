/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.scripting.services.api.IQueryEvaluatorService;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Service for evaluating index queries.
 * @see IQueryEvaluatorService
 */
public class IndexQueryEvaluatorService implements IQueryEvaluatorService<SnomedConceptDocument> {

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
	public List<SnomedConceptDocument> evaluate(final String queryExpression) {
		
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		List<ExtendedLocale> languagePreference = applicationContext.getService(LanguageSetting.class).getLanguagePreference();
		IEventBus eventBus = applicationContext.getService(IEventBus.class);
		IBranchPath branchPath = BranchPathUtils.createMainPath();
		
		if (ids.length == 0) {
			SnomedConcepts snomedConcepts = SnomedRequests.prepareSearchConcept()
					.filterByTerm(Strings.emptyToNull(queryExpression))
					.setLimit(10000)
					.setExpand("pt()")
					.setLocales(languagePreference)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
					.execute(eventBus)
					.getSync();
			return SnomedConceptDocument.fromConcepts(snomedConcepts);
		} else {
			SnomedConcepts snomedConcepts = SnomedRequests.prepareSearchConcept().filterByTerm(queryExpression).filterByIds(Sets.newHashSet(ids)).
					setLimit(10000).setExpand("pt()").setLocales(languagePreference).build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath()).execute(eventBus).getSync();
			return SnomedConceptDocument.fromConcepts(snomedConcepts);
		}
		
	}

	@Override
	public List<String> evaluateForIds(String queryExpression) {
		return newArrayList(ComponentUtils.getIds(evaluate(queryExpression)));
	}
	
}