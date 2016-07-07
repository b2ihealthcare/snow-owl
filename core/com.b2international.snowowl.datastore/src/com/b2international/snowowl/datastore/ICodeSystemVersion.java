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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.primitives.Longs;

/**
 * Serializable representation of a code system version.
 */
public interface ICodeSystemVersion extends Serializable {
	
	/**Constant for {@value} artefacts.*/
	String UNVERSIONED = "Unversioned";
	
	/**Constant representing a version in the repository without any content.
	 *<br>Such version branches has to be created while initializing the repository on the very first time.
	 *<p>ID: {@value}
	 *@deprecated will be removed after 3.5*/
	String INITIAL_STATE = "init";

	/**Timestamp indicating that a version branch has not been modified since the corresponding {@link ICodeSystemVersion} has been created.*/
	long NOT_MODIFIED_YET_LAST_UPDATE_TIME = EffectiveTimes.UNSET_EFFECTIVE_TIME;
	
	/**
	 * This effective time has to be manually set on the {@link TerminologymetadataPackage#getCodeSystemVersion_LastUpdateDate()}
	 * feature before re-versioning an already existing version.
	 * <p>This is the workaround to lie to CDO about a change, since the commit timestamp not known at commit time but
	 * on change processing time.
	 */
	Date FAKE_LAST_UPDATE_TIME_DATE = new Date(Dates.MIN_DATE_LONG);
	
	/**
	 * Returns with the import date time.
	 * @return the import date time.
	 */
	long getImportDate();

	/**
	 * Returns with the point in time when the code system version has been modified.
	 * @return the effective time.
	 */
	long getEffectiveDate();

	/**
	 * Returns with the timestamp indicating the last modification time of the current version.
	 * May return with {@link DateUtils#UNSET_EFFECTIVE_TIME} if the current version has not been modified 
	 * yet. 
	 * @return the last update date of the current version.
	 */
	long getLastUpdateDate();
	
	/**
	 * Returns with the description of the version.
	 * @return the description.
	 */
	String getDescription();

	/**
	 * Returns with the ID of the code system version.
	 * @return the version ID.
	 */
	String getVersionId();
	
	/**
	 * Returns the parent branch path where the version branch is forked off
	 * @return parent branch path
	 */
	String getParentBranchPath();
	
	/**
	 * Returns the full path of this version including the MAIN prefix as well as the version tag.
	 * @return
	 */
	String getPath();
	
	/**
	 * Returns with {@code true} if any modifications have been made on 
	 * the branch associated with the current code system version for
	 * the terminology or content. Otherwise {@code false}. 
	 * @return {@code true} if the code system version is patched. Otherwise {@code false}.
	 */
	boolean isPatched();
	
	/**
	 * Returns with the unique storage key of the version.
	 * @return the storage key.
	 */
	long getStorageKey();
	
	/**
	 * Returns with the UUID of the repository where the current version belongs to. 
	 */
	String getRepositoryUuid();
	
	/**
	 * Returns with the short name of the container code system.
	 */
	String getCodeSystemShortName();
	
	/**Predicate producing {@code true} output only and if only the processed {@link ICodeSystemVersion version}
	 * is {@link ICodeSystemVersion#isPatched() patched}. Otherwise {@code false}.*/
	Predicate<ICodeSystemVersion> PATCHED_PREDICATE = new Predicate<ICodeSystemVersion>() {
		public boolean apply(final ICodeSystemVersion version) {
			return checkNotNull(version, "version").isPatched();
		}
	};
	
	/**Function for getting the {@link ICodeSystemVersion#getImportDate() import date} from the {@link ICodeSystemVersion version}.*/
	Function<ICodeSystemVersion, Long> GET_IMPORT_DATE_FUNC = new Function<ICodeSystemVersion, Long>() {
		@Override public Long apply(final ICodeSystemVersion version) {
			return checkNotNull(version, "version").getImportDate();
		}
	}; 
	
	/**Function for getting the {@link ICodeSystemVersion#getEffectiveDate() effective date} from the {@link ICodeSystemVersion version}.*/
	Function<ICodeSystemVersion, Long> GET_EFFECTIVE_DATE_FUNC = new Function<ICodeSystemVersion, Long>() {
		@Override public Long apply(final ICodeSystemVersion version) {
			return checkNotNull(version, "version").getEffectiveDate();
		}
	};
	
	/**Function for extracting the {@link ICodeSystemVersion#getVersionId() ID } of the {@link ICodeSystemVersion code system version}.*/
	Function<ICodeSystemVersion, String> GET_VERSION_ID_FUNC = new Function<ICodeSystemVersion, String>() {
		public String apply(final ICodeSystemVersion version) {
			return checkNotNull(version, "version").getVersionId();
		}
	};
	
	/**Convenient method for transforming a {@link ICodeSystemVersion} into an 
	 *{@link IBranchPath} instance with the {@link BranchPathUtils#createVersionPath(String)} method. */
	Function<ICodeSystemVersion, IBranchPath> TO_BRANCH_PATH_FUNC = new Function<ICodeSystemVersion, IBranchPath>() {
		public IBranchPath apply(final ICodeSystemVersion version) {
			return BranchPathUtils.createPath(version.getPath());
		}
	};
	
	/**Convenient method for transforming a {@link ICodeSystemVersion} into it's parent's
	 *{@link IBranchPath} instance with the {@link BranchPathUtils#createVersionPath(String)} method. */
	Function<ICodeSystemVersion, IBranchPath> TO_PARENT_BRANCH_PATH_FUNC = new Function<ICodeSystemVersion, IBranchPath>() {
		public IBranchPath apply(final ICodeSystemVersion version) {
			return BranchPathUtils.createPath(version.getParentBranchPath());
		}
	};
	
	
	
	/**Function for extracting the {@link ICodeSystemVersion#getRepositoryUuid()} of the {@link ICodeSystemVersion code system version}.*/
	Function<ICodeSystemVersion, String> TO_REPOSITORY_UUID_FUNC = new Function<ICodeSystemVersion, String>() {
		public String apply(final ICodeSystemVersion version) {
			return version.getRepositoryUuid();
		}
	};
	
	

	/**Comparator for comparing {@link ICodeSystemVersion versions} via {@link ICodeSystemVersion#getImportDate() import date}s.*/
	Comparator<ICodeSystemVersion> VERSION_IMPORT_DATE_COMPARATOR = new CodeSystemVersionDateComparator(GET_IMPORT_DATE_FUNC);
	
	
	/**Comparator for comparing {@link ICodeSystemVersion versions} via {@link ICodeSystemVersion#getEffectiveDate() effective date}s.*/
	Comparator<ICodeSystemVersion> VERSION_EFFECTIVE_DATE_COMPARATOR = new CodeSystemVersionDateComparator(GET_EFFECTIVE_DATE_FUNC);

	
	static class CodeSystemVersionDateComparator implements Comparator<ICodeSystemVersion> {
		
		private final Function<ICodeSystemVersion, Long> f;

		protected CodeSystemVersionDateComparator(final Function<ICodeSystemVersion, Long> f) {
			this.f = checkNotNull(f, "f");
		}
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final ICodeSystemVersion o1, final ICodeSystemVersion o2) {
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
	
}