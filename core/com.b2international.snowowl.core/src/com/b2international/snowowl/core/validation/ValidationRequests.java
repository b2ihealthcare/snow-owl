/*
 * Copyright 2017-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation;

import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.validation.issue.ValidationIssueRequests;
import com.b2international.snowowl.core.validation.rule.ValidationRuleRequests;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteListRequests;

/**
 * @since 6.0
 */
public final class ValidationRequests {
	
	// Prefix for validation job IDs
	public static final String VALIDATION_JOB_ID_PREFIX = "validation-" ; //$NON-NLS-N$
	
	// ID suffix used for "shared" validation jobs (where results are useful for general consumption)
	public static final String SHARED_VALIDATION_RESULT_ID = "shared" ; //$NON-NLS-N$

	private ValidationRequests() {}
	
	public static ValidationIssueRequests issues() {
		return ValidationIssueRequests.INSTANCE;
	}
	
	public static ValidationRuleRequests rules() {
		return ValidationRuleRequests.INSTANCE;
	}

	public static ValidationWhiteListRequests whiteList() {
		return ValidationWhiteListRequests.INSTANCE;
	}

	public static ValidateRequestBuilder prepareValidate() {
		return new ValidateRequestBuilder();
	}
	
	/**
	 * Creates a validation job ID for shared use that includes the given resource URI.
	 * 
	 * @param resourceUri - the URI of the resource to validate
	 * @return the validation job ID
	 */
	public static String createSharedValidationJobKey(String resourceUri) {
		return createValidationJobKey(resourceUri, SHARED_VALIDATION_RESULT_ID);
	}
	
	/**
	 * Creates a validation job ID for local use that includes the given resource URI and the custom suffix.
	 * 
	 * @param resourceUri - the URI of the resource to validate
	 * @param suffix - the custom ID suffix
	 * @return the validation job ID
	 */
	public static String createValidationJobKey(String resourceUri, String suffix) {
		return String.format("%s%s-%s", VALIDATION_JOB_ID_PREFIX, resourceUri, suffix);
	}
	
	public static boolean isValidationJob(RemoteJobEntry job) {
		return job != null && job.getKey().startsWith(VALIDATION_JOB_ID_PREFIX);
	}
	
	public static boolean isRelatedBranch(RemoteJobEntry job, String branch) {
		if (!isValidationJob(job)) {
			return false;
		}
		
		final String[] parts = job.getKey().split("-", 3);
		if (parts.length < 2) {
			return false;
		}
		
		final String branchPart = parts[1];
		return branchPart.equals(branch);
	}
}
