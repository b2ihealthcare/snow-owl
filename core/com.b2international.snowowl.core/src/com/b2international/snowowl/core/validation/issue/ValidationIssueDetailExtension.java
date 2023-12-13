/*
 * Copyright 2018-2023 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.Map;

import com.b2international.snowowl.core.domain.BranchContext;

/**
 * @since 6.4
 */
public interface ValidationIssueDetailExtension {

	/**
	 * Customize each issue by adding new fields to them via the {@link ValidationIssue#putDetails(String, Object)},
	 * {@link ValidationIssue#setAffectedComponentLabels(java.util.List)} methods to support domain specific searching on it later via the
	 * {@link ValidationIssueSearchRequestBuilder#filterByDetails(java.util.Map)} and
	 * {@link ValidationIssueSearchRequestBuilder#filterByAffectedComponentLabel(String)} methods.
	 * 
	 * @param context
	 *                    - the context to use for extension
	 * @param issues
	 *                    - the issues to extend
	 */
	void extendIssues(BranchContext context, Collection<ValidationIssue> issues, Map<String, Object> ruleParameters);

	/**
	 * @return the tooling identifier
	 */
	String getToolingId();

}
