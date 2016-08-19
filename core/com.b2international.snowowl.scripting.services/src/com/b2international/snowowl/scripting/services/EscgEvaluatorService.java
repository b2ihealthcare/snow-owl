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

import java.util.List;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.scripting.services.api.IQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.SnomedClientRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorClientService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.collect.Lists;

/**
 * Service for evaluating an ESCG expression.
 * This evaluator service uses the latest version of the {@link SnomedClientTerminologyBrowser terminology}, 
 * {@link SnomedClientStatementBrowser statement} and {@link SnomedClientRefSetBrowser reference} set browsers for the 
  * SNOMED&nbsp;CT terminology.
  * 
 * @see IQueryEvaluatorService
 */
public final class EscgEvaluatorService implements IQueryEvaluatorService<SnomedConceptIndexEntry> {
	
	@Override
	public List<SnomedConceptIndexEntry> evaluate(final String queryExpression) {
		final IEscgQueryEvaluatorClientService delegate = ApplicationContext.getInstance().getService(IEscgQueryEvaluatorClientService.class);
		return Lists.newArrayList(delegate.evaluate(queryExpression));
	}
	
	@Override
	public List<String> evaluateForIds(final String queryExpression) {
		return newArrayList(ComponentUtils.getIds(evaluate(queryExpression)));
	}
}