/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ql;

import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;

/**
 * @since 6.12
 */
public final class SnomedQueryEvaluationRequestBuilder 
		extends BaseRequestBuilder<SnomedQueryEvaluationRequestBuilder, BranchContext, Promise<Expression>> 
		implements RevisionIndexRequestBuilder<Promise<Expression>> {

	private final String expression;
	
	public SnomedQueryEvaluationRequestBuilder(String expression) {
		this.expression = expression;
	}
	
	@Override
	protected Request<BranchContext, Promise<Expression>> doBuild() {
		SnomedQueryEvaluationRequest request = new SnomedQueryEvaluationRequest();
		request.setExpression(expression);
		return request;
	}
	
}
