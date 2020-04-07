/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.core.validation.issue.ValidationIssueSearchRequest.OptionKey;

/**
 * @since 6.0
 */
public final class ValidationIssueSearchRequestBuilder
		extends SearchResourceRequestBuilder<ValidationIssueSearchRequestBuilder, ServiceProvider, ValidationIssues>
		implements SystemRequestBuilder<ValidationIssues> {

	ValidationIssueSearchRequestBuilder() {}
	
	public ValidationIssueSearchRequestBuilder filterByRule(final String ruleId) {
		return addOption(OptionKey.RULE_ID, ruleId);
	}
	
	public ValidationIssueSearchRequestBuilder filterByRules(final Iterable<? extends String> ruleIds) {
		return addOption(OptionKey.RULE_ID, ruleIds);
	}
	
	public ValidationIssueSearchRequestBuilder filterByBranchPath(final String branchPath) {
		return addOption(OptionKey.BRANCH_PATH, branchPath);
	}
	
	public ValidationIssueSearchRequestBuilder filterByBranchPaths(final Iterable<? extends String> branchPaths) {
		return addOption(OptionKey.BRANCH_PATH, branchPaths);
	}
	
	public ValidationIssueSearchRequestBuilder filterByTooling(String toolingId) {
		return addOption(OptionKey.TOOLING_ID, toolingId);
	}
	
	public ValidationIssueSearchRequestBuilder filterByTooling(Iterable<String> toolingIds) {
		return addOption(OptionKey.TOOLING_ID, toolingIds);
	}
	
	public ValidationIssueSearchRequestBuilder filterByAffectedComponentId(String affectedComponentId) {
		return addOption(OptionKey.AFFECTED_COMPONENT_ID, affectedComponentId);
	}
	
	public ValidationIssueSearchRequestBuilder filterByAffectedComponentId(Iterable<String> affectedComponentIds) {
		return addOption(OptionKey.AFFECTED_COMPONENT_ID, affectedComponentIds);
	}
	
	public ValidationIssueSearchRequestBuilder filterByAffectedComponentType(short affectedComponentType) {
		return addOption(OptionKey.AFFECTED_COMPONENT_TYPE, affectedComponentType);
	}
	
	public ValidationIssueSearchRequestBuilder filterByAffectedComponentType(Iterable<Short> affectedComponentTypes) {
		return addOption(OptionKey.AFFECTED_COMPONENT_TYPE, affectedComponentTypes);
	}
	
	public ValidationIssueSearchRequestBuilder filterByAffectedComponentLabel(String affectedComponentLabel) {
		return addOption(OptionKey.AFFECTED_COMPONENT_LABEL, affectedComponentLabel);
	}
	
	public ValidationIssueSearchRequestBuilder isWhitelisted(boolean whitelisted) {
		return addOption(OptionKey.WHITELISTED, whitelisted);
	}
	
	public ValidationIssueSearchRequestBuilder filterByDetails(Map<String, Object> details) {
		final Options options = Options.from(details);
		return addOption(OptionKey.DETAILS, options);
	}
	
	@Override
	protected SearchResourceRequest<ServiceProvider, ValidationIssues> createSearch() {
		return new ValidationIssueSearchRequest();
	}

}