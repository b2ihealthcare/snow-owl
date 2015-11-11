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
package com.b2international.snowowl.datastore.server.snomed.escg;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import bak.pcj.LongCollection;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.dsl.escg.EscgUtils;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

public class EscgQueryEvaluatorService implements IEscgQueryEvaluatorService, Serializable {

	private static final long serialVersionUID = -6008793055376183745L;

	@Override
	public Collection<SnomedConceptIndexEntry> evaluate(final IBranchPath branchPath, final String queryExpression) {
		final QueryEvaluator delegate = new QueryEvaluator(branchPath);
		return delegate.evaluate(EscgUtils.INSTANCE.parseRewrite(queryExpression));
	}

	@Override
	public BooleanQuery evaluateBooleanQuery(final IBranchPath branchPath, final String expression) {
		final IndexQueryQueryEvaluator delegate = new IndexQueryQueryEvaluator();
		final BooleanQuery booleanQuery = delegate.evaluate(EscgUtils.INSTANCE.parseRewrite(expression));
		booleanQuery.add(SnomedMappings.newQuery().active().matchAll(), Occur.MUST);
		booleanQuery.add(SnomedMappings.newQuery().type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER).matchAll(), Occur.MUST);
		return booleanQuery;
	}
	
	@Override
	public LongCollection evaluateConceptIds(final IBranchPath branchPath, final String queryExpression) {
		checkNotNull(queryExpression, "ESCG query expression wrapper argument cannot be null.");
		final ConceptIdQueryEvaluator2 delegate = new ConceptIdQueryEvaluator2(branchPath);
		return delegate.evaluate(EscgUtils.INSTANCE.parseRewrite(queryExpression));
	}
}