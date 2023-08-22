/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.resource;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;

/**
 * @since 8.12.0
 * @param <R>
 */
public abstract class BaseTerminologyResourceSearchRequest<R> extends BaseResourceSearchRequest<R> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @since 8.12.0
	 */
	public enum OptionKey {
		
		/**
		 * Filters terminology resources by their listed dependencies.
		 */
		DEPENDENCY
		
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	protected void prepareAdditionalFilters(RepositoryContext context, ExpressionBuilder queryBuilder) {
		super.prepareAdditionalFilters(context, queryBuilder);
		addDependencyFilter(context, queryBuilder);
	}

	private void addDependencyFilter(RepositoryContext context, ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.DEPENDENCY)) {
			String queryString = getString(OptionKey.DEPENDENCY);
			queryBuilder.filter(ResourceDocument.Expressions.dependency(queryString));
		}
	}
	
}
