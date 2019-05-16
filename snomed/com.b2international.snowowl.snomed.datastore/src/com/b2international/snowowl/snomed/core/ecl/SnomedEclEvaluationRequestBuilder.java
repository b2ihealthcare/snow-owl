/*
 * Copyright 2016-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ecl;

import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.google.common.base.Preconditions;

/**
 * @since 5.4
 */
public final class SnomedEclEvaluationRequestBuilder 
		extends BaseRequestBuilder<SnomedEclEvaluationRequestBuilder, BranchContext, Promise<Expression>> 
		implements RevisionIndexRequestBuilder<Promise<Expression>> {

	private String expression;
	private String expressionForm = Trees.INFERRED_FORM;
	
	public SnomedEclEvaluationRequestBuilder(String expression) {
		this.expression = expression;
	}
	
	public SnomedEclEvaluationRequestBuilder setExpressionForm(String expressionForm) {
		Preconditions.checkArgument(Trees.INFERRED_FORM.equals(expressionForm) || Trees.STATED_FORM.equals(expressionForm));
		this.expressionForm = expressionForm;
		return getSelf();
	}
	
	@Override
	protected Request<BranchContext, Promise<Expression>> doBuild() {
		final SnomedEclEvaluationRequest req = new SnomedEclEvaluationRequest();
		req.setExpression(expression);
		req.setExpressionForm(expressionForm);
		return req;
	}
	
}
