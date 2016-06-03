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
package com.b2international.snowowl.api.impl.codesystem.domain;

import java.util.Date;

import com.b2international.snowowl.api.codesystem.domain.ICodeSystemVersion;

/**
 */
public class CodeSystemVersion implements ICodeSystemVersion {

	private Date importDate;
	private Date effectiveDate;
	private Date lastModificationDate;
	private String description;
	private String version;
	private String parentBranchPath;
	private boolean patched;

	@Override
	public Date getImportDate() {
		return importDate;
	}

	@Override
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	@Override
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
	
	@Override
	public String getParentBranchPath() {
		return parentBranchPath;
	}

	@Override
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