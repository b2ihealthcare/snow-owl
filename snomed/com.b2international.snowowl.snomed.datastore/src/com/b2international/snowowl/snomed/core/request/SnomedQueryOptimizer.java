/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import java.util.List;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.QueryExpression;
import com.b2international.snowowl.core.domain.QueryExpressionDiff;
import com.b2international.snowowl.core.domain.QueryExpressionDiffs;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.request.QueryOptimizer;

/**
 * @since 7.7
 */
public final class SnomedQueryOptimizer implements QueryOptimizer {

	@Override
	public QueryExpressionDiffs optimize(BranchContext context, Options params) {
		QueryExpressionDiff diff = new QueryExpressionDiff(
				List.of(new QueryExpression(IDs.base64UUID(), "422497000|Distance vision 20/20 (finding)|", false)), 
				List.of(), 
				List.of());
		
		return new QueryExpressionDiffs(List.of(diff));
	}
}
