/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.validation;

import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @since 6.13
 */
public class ValidationRestInput {

	@NotEmpty
	private String branch;
	
	@NotEmpty
	private String codeSystemShortName;

	@NotEmpty
	private Set<String> ruleIds;
	
	private Boolean isUnpublishedValidation;

	public String branch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}
	
	public String codeSystemShortName() {
		return codeSystemShortName;
	}
	
	public void setCodesystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}

	public Set<String> ruleIds() {
		return ruleIds;
	}

	public void setRuleIds(Set<String> ruleIds) {
		this.ruleIds = ruleIds;
	}
	
	public boolean isUnpublishedValidation() {
		return isUnpublishedValidation;
	}
	
	public void setIsUnpublishedValidation(Boolean isUnpublishedValidation) {
		this.isUnpublishedValidation = isUnpublishedValidation;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ValidationRestInput [branch=");
		builder.append(branch);
		builder.append(", ruleIds=");
		builder.append(ruleIds);
		builder.append(", isUnpublishedValidation=");
		builder.append(isUnpublishedValidation);
		builder.append("]");
		return builder.toString();
	}
}
