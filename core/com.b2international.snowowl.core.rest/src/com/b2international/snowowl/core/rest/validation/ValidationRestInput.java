/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.validation;

import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @since 6.13
 */
public class ValidationRestInput {

	@NotEmpty
	private String codeSystemURI;
	
	@NotEmpty
	private Set<String> ruleIds;
	
	private boolean unpublishedOnly = true;
	
	public String getCodeSystemURI() {
		return codeSystemURI;
	}
	
	public void setCodeSystemURI(String codeSystemURI) {
		this.codeSystemURI = codeSystemURI;
	}
	
	public Set<String> getRuleIds() {
		return ruleIds;
	}

	public void setRuleIds(Set<String> ruleIds) {
		this.ruleIds = ruleIds;
	}

	public boolean isUnpublishedOnly() {
		return unpublishedOnly;
	}
	
	public void setUnpublishedOnly(boolean unpublishedOnly) {
		this.unpublishedOnly = unpublishedOnly;
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ValidationRestInput [codeSystemURI=");
		builder.append(codeSystemURI);
		builder.append(", ruleIds=");
		builder.append(ruleIds);
		builder.append(", unpublishedOnly=");
		builder.append(unpublishedOnly);
		builder.append("]");
		return builder.toString();
	}
}
