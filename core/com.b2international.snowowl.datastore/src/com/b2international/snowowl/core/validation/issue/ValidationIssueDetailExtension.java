/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.issue;

import java.util.Collection;

import com.b2international.commons.options.Options;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;

/**
 * @since 6.4
 */
public interface ValidationIssueDetailExtension {

	/**
	 * Prepares the given queryBuilder with domain specific filters on fields mainly added by the {@link #extendIssues(BranchContext, Collection)}
	 * method during issue indexing.
	 * 
	 * @param queryBuilder
	 * @param options
	 */
	void prepareQuery(ExpressionBuilder queryBuilder, Options options);

	/**
	 * Customize each issue by adding new fields to them via the {@link ValidationIssue#setDetails(String, Object)},
	 * {@link ValidationIssue#setAffectedComponentLabels(java.util.List)} methods to support domain specific searching on it later via the
	 * {@link ValidationIssueSearchRequestBuilder#filterByDetails(java.util.Map)} and
	 * {@link ValidationIssueSearchRequestBuilder#filterByAffectedComponentLabel(String)} methods.
	 * 
	 * @param context
	 *                    - the context to use for extension
	 * @param issues
	 *                    - the issues to extend
	 */
	void extendIssues(BranchContext context, Collection<ValidationIssue> issues);

	/**
	 * @return the tooling identifier
	 */
	String getToolingId();

}
