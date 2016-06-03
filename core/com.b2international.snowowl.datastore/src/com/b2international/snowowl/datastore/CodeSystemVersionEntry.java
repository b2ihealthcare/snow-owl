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
package com.b2international.snowowl.datastore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;

/**
 * CDO independent representation of a {@link CodeSystemVersion}.
 */
	
public class CodeSystemVersionEntry implements Serializable, ICodeSystemVersion {
	private static final long serialVersionUID = 5948841680432090865L;

	private final long importDate;
	private final long effectiveDate;
	private final String description;
	private final String versionId;
	private final long latestUpdateDate;
	private final String parentBranchPath;
	private boolean patched;
	private final long storageKey;
	private final String repositoryUuid;
	private final String codeSystemShortName;

	public CodeSystemVersionEntry(final long importDate, final long effectiveDate, final long latestUpdateDate, final String description,
			final String versionId, final String parentBranchPath, final long storageKey, final String repositoryUuid) {
		this(importDate, effectiveDate, latestUpdateDate, nullToEmpty(description), checkNotNull(versionId, "versionId"),
				checkNotNull(parentBranchPath), false, storageKey, checkNotNull(repositoryUuid, "repositoryUuid"), null);
	}
	
	public CodeSystemVersionEntry(final long importDate, final long effectiveDate, final long latestUpdateDate, final String description,
			final String versionId, final String parentBranchPath, final long storageKey, final String repositoryUuid, final String codeSystemShortName) {
		this(importDate, effectiveDate, latestUpdateDate, nullToEmpty(description), checkNotNull(versionId, "versionId"),
				checkNotNull(parentBranchPath), false, storageKey, checkNotNull(repositoryUuid, "repositoryUuid"), codeSystemShortName);
	}

	public CodeSystemVersionEntry(final long importDate, final long effectiveDate, final long latestUpdateDate, final String description,
			final String versionId, final String parentBranchPath, final boolean patched, final long storageKey,
			final String repositoryUuid, final String codeSystemShortName) {
		this.importDate = importDate;
		this.effectiveDate = effectiveDate;
		this.latestUpdateDate = latestUpdateDate;
		this.repositoryUuid = checkNotNull(repositoryUuid, "repositoryUuid");
		this.description = nullToEmpty(description);
		this.versionId = checkNotNull(versionId, "versionId");
		this.parentBranchPath = parentBranchPath;
		this.patched = patched;
		this.storageKey = storageKey;
		this.codeSystemShortName = codeSystemShortName;
	}

	@Override
	public long getImportDate() {
		return importDate;
	}

	@Override
	public long getEffectiveDate() {
		return effectiveDate;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getVersionId() {
		return versionId;
	}
	
	@Override
	public String getParentBranchPath() {
		return parentBranchPath;
	}

	@Override
	public boolean isPatched() {
		return patched;
	}

	@Override
	public long getLastUpdateDate() {
		return latestUpdateDate;
	}

	@Override
	public long getStorageKey() {
		return storageKey;
	}

	@Override
	public String getRepositoryUuid() {
		return repositoryUuid;
	}
	
	@Override
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}
	
	/**
	 * (non-API)
	 * 
	 * Sets the patched flag on the code system version to {@code true}.
	 */
	public void setPatched() {
		this.patched = true;
	}
	
	/**
	 * Returns the full path of this version including the MAIN prefix as well as the version tag.
	 * @return
	 */
	@Override
	public String getPath() {
		return parentBranchPath + IBranchPath.SEPARATOR_CHAR + versionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((repositoryUuid == null) ? 0 : repositoryUuid.hashCode());
		result = prime * result + ((versionId == null) ? 0 : versionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CodeSystemVersionEntry))
			return false;
		final CodeSystemVersionEntry other = (CodeSystemVersionEntry) obj;
		if (repositoryUuid == null) {
			if (other.repositoryUuid != null)
				return false;
		} else if (!repositoryUuid.equals(other.repositoryUuid))
			return false;
		if (versionId == null) {
			if (other.versionId != null)
				return false;
		} else if (!versionId.equals(other.versionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder(versionId).append(patched ? "*" : "").toString();
	}
	

}