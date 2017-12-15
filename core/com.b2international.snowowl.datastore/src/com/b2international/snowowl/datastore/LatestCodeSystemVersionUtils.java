/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.core.api.IBranchPath.MAIN_BRANCH;
import static com.b2international.snowowl.core.date.EffectiveTimes.UNSET_EFFECTIVE_TIME;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.NO_STORAGE_KEY;
import static java.lang.Long.MAX_VALUE;

/**
 * Utility class for the
 */
public abstract class LatestCodeSystemVersionUtils {

	private static final String LATEST_VERSION = "Latest version";

	/**
	 * Creates a fake {@link ICodeSystemVersion} representing the HEAD in the given repository.
	 * @param repositoryUuid the repository UUID.
	 * @return a fake version representing the HEAD in the repository.
	 */
	public static CodeSystemVersionEntry createLatestCodeSystemVersion(final String repositoryUuid) {
		return CodeSystemVersionEntry.builder()
				.repositoryUuid(repositoryUuid)
				.versionId(MAIN_BRANCH)
				.storageKey(NO_STORAGE_KEY)
				.effectiveDate(MAX_VALUE)
				.importDate(MAX_VALUE)
				.latestUpdateDate(UNSET_EFFECTIVE_TIME)
				.description(LATEST_VERSION)
				.codeSystemShortName("SNOMEDCT")
				.build();
	}
	
	/**
	 * Returns {@code true} if the version is a fake {@link ICodeSystemVersion} implementation
	 * representing the HEAD in the repository.
	 * @param version the version to check.
	 * @return {@code true} if the argument is the latest version, otherwise {@code false}
	 */
	public static boolean isLatestVersion(final ICodeSystemVersion version) {
		return MAIN_BRANCH.equals(version.getVersionId());
	}
	
	/**
	 * Returns the latest version from the set of code system version entries.
	 */
	public static CodeSystemVersionEntry getLatestVersion(CodeSystemVersions versions) {
		if (!versions.isEmpty()) {
			CodeSystemVersionEntry latest = versions.first().get();
			
			for (CodeSystemVersionEntry version : versions) {
				if( version.getEffectiveDate() > latest.getEffectiveDate() ) {
					latest = version;
				}
			}
			return latest;
		}
		return null;
	}

}