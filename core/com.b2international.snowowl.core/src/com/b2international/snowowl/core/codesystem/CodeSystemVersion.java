/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import static com.b2international.snowowl.core.api.IBranchPath.MAIN_BRANCH;

import java.time.LocalDate;
import java.util.Date;

import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 1.0
 */
public class CodeSystemVersion implements CodeSystemVersionProperties {

	private Date importDate;
	private String effectiveDate;
	private Date lastModificationDate;
	private String description;
	private String version;
	private String path;
	private CodeSystemURI uri;
	private String repositoryId;
	
	/**
	 * Returns the date on which this code system version was imported into the server.
	 * 
	 * @return the import date of this code system version
	 */
	public Date getImportDate() {
		return importDate;
	}

	@Override
	public String getEffectiveDate() {
		return effectiveDate;
	}
	
	@JsonIgnore
	public LocalDate getEffectiveTime() {
		return EffectiveTimes.parse(effectiveDate, DateFormats.SHORT);
	}
	
	@Deprecated
	@JsonIgnore
	public String getParentBranchPath() {
		return BranchPathUtils.createPath(getPath()).getParentPath();
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
	
	@Deprecated
	public String getRepositoryId() {
		return repositoryId;
	}
	
	public String getPath() {
		return path;
	}

	@Override
	public String getVersion() {
		return version;
	}

	public CodeSystemURI getUri() {
		return uri;
	}
	
	@JsonIgnore
	public String getCodeSystem() {
		return getUri().getCodeSystem();
	}
	
	public void setImportDate(final Date importDate) {
		this.importDate = importDate;
	}

	public void setEffectiveDate(final String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public void setLastModificationDate(final Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	@Deprecated
	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public void setVersion(final String version) {
		this.version = version;
	}
	
	public void setUri(CodeSystemURI uri) {
		this.uri = uri;
	}
	
	/**
	 * @return {@code true} if this version represents the HEAD in the repository.
	 */
	@JsonIgnore
	public boolean isLatestVersion() {
		return MAIN_BRANCH.equals(getVersion());
	}

}