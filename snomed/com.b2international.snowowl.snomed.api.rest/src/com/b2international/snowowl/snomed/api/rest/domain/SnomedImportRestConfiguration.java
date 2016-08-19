/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.domain;

import com.b2international.snowowl.snomed.api.impl.domain.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;

/**
 * @since 1.0
 */
public class SnomedImportRestConfiguration {

	private Rf2ReleaseType type;
	private String branchPath;
	private Boolean createVersions = Boolean.FALSE;
	private String languageRefSetId;

	public String getBranchPath() {
		return branchPath;
	}
	
	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public Rf2ReleaseType getType() {
		return type;
	}

	public Boolean getCreateVersions() {
		return createVersions;
	}

	public void setType(final Rf2ReleaseType type) {
		this.type = type;
	}

	public void setCreateVersions(final Boolean createVersions) {
		this.createVersions = createVersions;
	}

	/**
	 * Returns with the language reference set identifier concept ID for the import configuration.
	 * @return the language reference set ID for the preferred language.
	 */
	public String getLanguageRefSetId() {
		return languageRefSetId;
	}

	/**
	 * Sets the language reference set identifier concept ID based on
	 * the language reference set identifier concept ID argument.
	 * @param languageRefSetId the language reference set ID for the preferred language. 
	 */
	public void setLanguageRefSetId(final String languageRefSetId) {
		this.languageRefSetId = languageRefSetId;
	}
	
	public ISnomedImportConfiguration toConfig() {
		return new SnomedImportConfiguration(
				getType(), 
				getBranchPath(),
				getLanguageRefSetId(), 
				getCreateVersions());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedImportRestConfiguration [type=");
		builder.append(type);
		builder.append(", branchPath=");
		builder.append(branchPath);
		builder.append(", createVersions=");
		builder.append(createVersions);
		builder.append(", languageRefSetId=");
		builder.append(languageRefSetId);
		builder.append("]");
		return builder.toString();
	}
}
