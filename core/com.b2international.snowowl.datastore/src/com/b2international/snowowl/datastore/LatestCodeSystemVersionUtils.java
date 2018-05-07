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
	
}