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
package com.b2international.snowowl.api.rest.codesystem.domain;

import java.util.Date;

/**
 */
public class CodeSystemVersion implements CodeSystemVersionProperties {

	private Date importDate;
	private Date effectiveDate;
	private Date lastModificationDate;
	private String description;
	private String version;
	private String parentBranchPath;
	private boolean patched;

	/**
	 * Returns the date on which this code system version was imported into the server.
	 * 
	 * @return the import date of this code system version
	 */
	public Date getImportDate() {
		return importDate;
	}

	@Override
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	/**
	 * Returns the date on which this code system version was last modified.
	 * 
	 * @return the last modification date of this code system version (can be {@code null})
	 */
	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getVersion() {
		return version;
	}

	/**
	 * Returns the parent branch path where the version branch is forked off
	 * @return parent branch path
	 */
	public String getParentBranchPath() {
		return parentBranchPath;
	}

	/**
	 * Indicates if any modifications have been made on this code system version after releasing it.
	 *  
	 * @return {@code true} if this code system version includes retroactive modifications, {@code false} otherwise
	 */
	public boolean isPatched() {
		return patched;
	}

	public void setImportDate(final Date importDate) {
		this.importDate = importDate;
	}

	public void setEffectiveDate(final Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public void setLastModificationDate(final Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setVersion(final String version) {
		this.version = version;
	}
	
	public void setParentBranchPath(final String parentBranchPath) {
		this.parentBranchPath = parentBranchPath;
	}

	public void setPatched(final boolean patched) {
		this.patched = patched;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CodeSystemVersion [importDate=");
		builder.append(importDate);
		builder.append(", effectiveDate=");
		builder.append(effectiveDate);
		builder.append(", lastModificationDate=");
		builder.append(lastModificationDate);
		builder.append(", description=");
		builder.append(description);
		builder.append(", version=");
		builder.append(version);
		builder.append(", parentBranchPath=");
		builder.append(parentBranchPath);
		builder.append(", patched=");
		builder.append(patched);
		builder.append("]");
		return builder.toString();
	}
}