/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.validation.issue.ValidationIssueRequests;
import com.b2international.snowowl.core.validation.rule.ValidationRuleRequests;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteListRequests;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;

/**
 * @since 6.0
 */
public final class ValidationRequests {
	
	public static final String VALIDATION_JOB_ID_PREFIX = "validation-" ; //$NON-NLS-N$

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
	 * Creates a unique validation id
	 * 
	 * @param codeSystemEntry
	 * @return the unique id.
	 */
	public static String createUniqueValidationId(String shortName, String branch) {
		return String.format("%s%s%s%s", VALIDATION_JOB_ID_PREFIX, shortName, Branch.SEPARATOR, branch);
	}
	
	public static boolean isValidationJob(RemoteJobEntry job) {
		return job != null && job.getId().startsWith(ValidationRequests.VALIDATION_JOB_ID_PREFIX);
	}
	
	public static boolean isRelatedBranch(RemoteJobEntry job, String branch) {
		return isValidationJob(job) && job.getId().endsWith(branch);
	}
	
}