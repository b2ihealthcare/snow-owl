/*
 * Copyright 2020-2024 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.QueryExpression;
import com.b2international.snowowl.core.domain.QueryExpressionDiffs;

/**
 * @since 7.7
 */
public final class QueryOptimizeRequest extends ResourceRequest<BranchContext, QueryExpressionDiffs> {

	private static final long serialVersionUID = 1L;

	@NotNull
	private List<QueryExpression> inclusions;
	
	@NotNull
	private List<QueryExpression> exclusions;
	
	private Integer limit;

	private Options additionalOptions;
	
	void setInclusions(List<QueryExpression> inclusions) {
		this.inclusions = inclusions;
	}

	void setExclusions(List<QueryExpression> exclusions) {
		this.exclusions = exclusions;
	}
	
	void setLimit(Integer limit) {
		this.limit = limit;
	}

	void setAdditionalOptions(Options additionalOptions) {
		this.additionalOptions = additionalOptions;
	}
	
	@Override
	public QueryExpressionDiffs execute(BranchContext context) {

		// Add entries from "additionalOptions" first so that it has no effect on the rest of the option keys
		final Options params = Options.builder()
			.putAll(additionalOptions)
			.put(QueryOptimizer.OptionKey.INCLUSIONS, inclusions)
			.put(QueryOptimizer.OptionKey.EXCLUSIONS, exclusions)
			.put(QueryOptimizer.OptionKey.LOCALES, locales())
			.put(QueryOptimizer.OptionKey.LIMIT, limit)
			.build();
		
		return context.service(QueryOptimizer.class).optimize(context, params);
	}
}
