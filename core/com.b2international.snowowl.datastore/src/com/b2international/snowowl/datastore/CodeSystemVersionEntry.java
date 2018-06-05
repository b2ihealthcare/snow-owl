/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.b2international.index.query.Expressions.matchAnyLong;
import static com.b2international.snowowl.core.api.IBranchPath.MAIN_BRANCH;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.primitives.Longs;

/**
 * CDO independent representation of a {@link CodeSystemVersion}.
 */
@Doc
@JsonDeserialize(builder = CodeSystemVersionEntry.Builder.class)
public final class CodeSystemVersionEntry implements Serializable {

	/**
	 * Unique terminology component identifier for versions.
	 */
	public static final short TERMINOLOGY_COMPONENT_ID = 2;
	
	/**Constant for {@value} artefacts.*/
	public static final  String UNVERSIONED = "Unversioned";
	
	/**Timestamp indicating that a version branch has not been modified since the corresponding {@link ICodeSystemVersion} has been created.*/
	public static final long NOT_MODIFIED_YET_LAST_UPDATE_TIME = EffectiveTimes.UNSET_EFFECTIVE_TIME;
	
	/**
	 * This effective time has to be manually set on the {@link TerminologymetadataPackage#getCodeSystemVersion_LastUpdateDate()}
	 * feature before re-versioning an already existing version.
	 * <p>This is the workaround to lie to CDO about a change, since the commit timestamp not known at commit time but
	 * on change processing time.
	 */
	public static final Date FAKE_LAST_UPDATE_TIME_DATE = new Date(Dates.MIN_DATE_LONG);
	
	/**Comparator for comparing {@link ICodeSystemVersion versions} via {@link ICodeSystemVersion#getEffectiveDate() effective date}s.*/
	public static final Comparator<CodeSystemVersionEntry> VERSION_EFFECTIVE_DATE_COMPARATOR = new CodeSystemVersionDateComparator(CodeSystemVersionEntry::getEffectiveDate);

	public static final class CodeSystemVersionDateComparator implements Comparator<CodeSystemVersionEntry> {
		
		private final Function<CodeSystemVersionEntry, Long> f;

		protected CodeSystemVersionDateComparator(final Function<CodeSystemVersionEntry, Long> f) {
			this.f = f;
		}
		
		@Override
		public int compare(final CodeSystemVersionEntry o1, final CodeSystemVersionEntry o2) {
			if (null == o1) {
				return null == o2 ? 0 : 1;
			}
			if (null == o2) {
				return -1;
			}
			final long time1 = Longs.max(f.apply(o1), Dates.MIN_DATE_LONG);
			final long time2 = Longs.max(f.apply(o2), Dates.MIN_DATE_LONG);
			return Longs.compare(time1, time2);
		}
	}
	
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
		
		public static Expression storageKeys(Iterable<Long> storageKeys) {
			return matchAnyLong(Fields.STORAGE_KEY, storageKeys);
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
	
	/**
	 * @return the code system short name where this version belongs to.
	 */
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}
	
	/**
	 * Returns with the import date time.
	 * @return the import date time.
	 */
	public long getImportDate() {
		return importDate;
	}

	/**
	 * Returns with the point in time when the code system version has been modified.
	 * @return the effective time.
	 */
	public long getEffectiveDate() {
		return effectiveDate;
	}

	/**
	 * Returns with the description of the version.
	 * @return the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns with the ID of the code system version.
	 * @return the version ID.
	 */
	public String getVersionId() {
		return versionId;
	}

	/**
	 * Returns the parent branch path where the version branch is forked off
	 * @return parent branch path
	 */
	public String getParentBranchPath() {
		return parentBranchPath;
	}

	/**
	 * Returns with {@code true} if any modifications have been made on 
	 * the branch associated with the current code system version for
	 * the terminology or content. Otherwise {@code false}. 
	 * @return {@code true} if the code system version is patched. Otherwise {@code false}.
	 */
	public boolean isPatched() {
		return patched;
	}

	/**
	 * Returns with the timestamp indicating the last modification time of the current version.
	 * May return with {@link DateUtils#UNSET_EFFECTIVE_TIME} if the current version has not been modified 
	 * yet. 
	 * @return the last update date of the current version.
	 */
	public long getLatestUpdateDate() {
		return latestUpdateDate;
	}

	/**
	 * Returns with the unique storage key of the version.
	 * @return the storage key.
	 */
	public long getStorageKey() {
		return storageKey;
	}

	/**
	 * Returns with the UUID of the repository where the current version belongs to. 
	 */
	public String getRepositoryUuid() {
		return repositoryUuid;
	}

	/**
	 * Returns the full path of this version including the MAIN prefix as well as the version tag.
	 * @return
	 */
	@JsonIgnore
	public String getPath() {
		return parentBranchPath + IBranchPath.SEPARATOR_CHAR + versionId;
	}
	
	/**
	 * Returns {@code true} if the version is a fake {@link ICodeSystemVersion} implementation
	 * representing the HEAD in the repository.
	 * @param version the version to check.
	 * @return {@code true} if the argument is the latest version, otherwise {@code false}
	 */
	@JsonIgnore
	public boolean isLatestVersion() {
		return MAIN_BRANCH.equals(getVersionId());
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
