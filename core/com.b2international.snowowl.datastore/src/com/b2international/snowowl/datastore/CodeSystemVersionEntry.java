/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.index.query.Expressions.exactMatch;

import java.util.Objects;

import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * CDO independent representation of a {@link CodeSystemVersion}.
 */
@Doc
@JsonDeserialize(builder = CodeSystemVersionEntry.Builder.class)
public class CodeSystemVersionEntry implements ICodeSystemVersion {

	public static class Fields {
		public static final String IMPORT_DATE = "importDate";
		public static final String EFFECTIVE_DATE = "effectiveDate";
		public static final String DESCRIPTION = "description";
		public static final String VERSION_ID = "versionId";
		public static final String LATEST_UPDATE_DATE = "latestUpdateDate";
		public static final String STORAGE_KEY = "storageKey";
		public static final String REPOSITORY_UUID = "repositoryUuid";
		public static final String CODE_SYSTEM_SHORT_NAME = "codeSystemShortName";
	}

	public static class Expressions {

		public static Expression versionId(String versionId) {
			return exactMatch(Fields.VERSION_ID, versionId);
		}

		public static Expression shortName(String shortName) {
			return exactMatch(Fields.CODE_SYSTEM_SHORT_NAME, shortName);
		}
		
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(CodeSystemVersion version) {
		final String codeSystemShortName = version.getCodeSystem().getShortName();
		return builder()
				.storageKey(CDOIDUtils.asLong(version.cdoID()))
				.versionId(version.getVersionId())
				.description(version.getDescription())
				.effectiveDate(EffectiveTimes.getEffectiveTime(version.getEffectiveDate()))
				.importDate(Dates.getTime(version.getImportDate()))
				.latestUpdateDate(EffectiveTimes.getEffectiveTime(version.getLastUpdateDate()))
				.repositoryUuid(version.getCodeSystem().getRepositoryUuid())
				.codeSystemShortName(codeSystemShortName)
				.parentBranchPath(version.getParentBranchPath());
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {
		
		private long importDate;
		private long effectiveDate;
		private String description;
		private String versionId;
		private long latestUpdateDate;
		private boolean patched;
		private long storageKey;
		private String repositoryUuid;
		private String codeSystemShortName;
		private String parentBranchPath = Branch.MAIN_PATH;
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder effectiveDate(long effectiveDate) {
			this.effectiveDate = effectiveDate;
			return this;
		}
		
		public Builder importDate(long importDate) {
			this.importDate = importDate;
			return this;
		}
		
		public Builder latestUpdateDate(long latestUpdateDate) {
			this.latestUpdateDate = latestUpdateDate;
			return this;
		}
		
		public Builder patched(boolean patched) {
			this.patched = patched;
			return this;
		}
		
		public Builder repositoryUuid(String repositoryUuid) {
			this.repositoryUuid = repositoryUuid;
			return this;
		}
		
		public Builder storageKey(long storageKey) {
			this.storageKey = storageKey;
			return this;
		}
		
		public Builder versionId(String versionId) {
			this.versionId = versionId;
			return this;
		}
		
		public Builder codeSystemShortName(String codeSystemShortName) {
			this.codeSystemShortName = codeSystemShortName;
			return this;
		}
		
		public Builder parentBranchPath(String parentBranchPath) {
			this.parentBranchPath = parentBranchPath;
			return this;
		}
		
		public CodeSystemVersionEntry build() {
			return new CodeSystemVersionEntry(importDate, effectiveDate, latestUpdateDate, description, versionId, parentBranchPath, 
					patched, storageKey,
					repositoryUuid, codeSystemShortName);
		}
		
	}
	
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
	
	private CodeSystemVersionEntry(final long importDate, final long effectiveDate, final long latestUpdateDate,
			final String description, final String versionId, final String parentBranchPath, final boolean patched, final long storageKey, final String repositoryUuid, 
			final String codeSystemShortName) {
		this.importDate = importDate;
		this.effectiveDate = effectiveDate;
		this.latestUpdateDate = latestUpdateDate;
		this.codeSystemShortName = codeSystemShortName;
		this.repositoryUuid = repositoryUuid;
		this.description = description;
		this.versionId = versionId;
		this.parentBranchPath = parentBranchPath;
		this.patched = patched;
		this.storageKey = storageKey;
	}
	
	@Override
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}
	
	/**
	 * @param patched
	 * @deprecated - use {@link #builder()} to construct immutable version entry with patch flag set to true 
	 */
	public void setPatched(boolean patched) {
		this.patched = patched;
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
	public long getLatestUpdateDate() {
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

	@JsonIgnore
	@Override
	public String getPath() {
		return parentBranchPath + IBranchPath.SEPARATOR_CHAR + versionId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(repositoryUuid, codeSystemShortName, versionId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof CodeSystemVersionEntry)) { return false; }
		
		final CodeSystemVersionEntry other = (CodeSystemVersionEntry) obj;
		
		if (!Objects.equals(repositoryUuid, other.repositoryUuid)) { return false; }
		if (!Objects.equals(codeSystemShortName, other.codeSystemShortName)) { return false; }
		if (!Objects.equals(versionId, other.versionId)) { return false; }
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder(versionId).append(patched ? "*" : "").toString();
	}
	
}
