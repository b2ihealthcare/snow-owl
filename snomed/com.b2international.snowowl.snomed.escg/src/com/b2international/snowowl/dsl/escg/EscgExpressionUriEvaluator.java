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
package com.b2international.snowowl.dsl.escg;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.uri.IExpressionUriEvaluator;
import com.b2international.snowowl.core.uri.UriUtils;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorClientService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.collect.ImmutableList;

/**
 * ESCG expression URI evaluator.
 * 
 */
public class EscgExpressionUriEvaluator implements IExpressionUriEvaluator<SnomedConceptIndexEntry> {

	@Override
	public List<SnomedConceptIndexEntry> evaluate(final String uri) {
		checkNotNull(uri, "URI must not be null.");
		checkArgument(UriUtils.isExpressionUri(uri), "Unexpected URI: " + uri);
		checkArgument(UriUtils.ESCG_LANGUAGE.equals(UriUtils.getExpressionLanguage(uri)), "Unexpected expression language in URI: " + uri);
		
		final String expression = UriUtils.getExpression(uri);
		IEscgQueryEvaluatorClientService queryEvaluatorService = ApplicationContext.getInstance().getService(IEscgQueryEvaluatorClientService.class);
		final Collection<SnomedConceptIndexEntry> concepts = queryEvaluatorService.evaluate(expression);
		return ImmutableList.copyOf(concepts);
	}

}