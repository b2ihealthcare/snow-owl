/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.QueryExpressionDiffs;
import com.b2international.snowowl.core.request.QueryOptimizer;

/**
 * @since 8.12.0
 */
public final class SnomedQueryOptimizerFactory implements QueryOptimizer {

	@Override
	public QueryExpressionDiffs optimize(final BranchContext context, final Options params) {
		final SnomedQueryOptimizer delegate = new SnomedQueryOptimizer(context.getPageSize());
		return delegate.optimize(context, params);
	}
}
