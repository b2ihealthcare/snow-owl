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

import static com.b2international.snowowl.core.date.EffectiveTimes.UNSET_EFFECTIVE_TIME;
import static com.b2international.snowowl.datastore.ICodeSystemVersion.UNVERSIONED;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.NO_STORAGE_KEY;
import static java.lang.Long.MAX_VALUE;

import com.google.common.base.Predicate;

/**
 * Utility class for the
 */
public abstract class LatestCodeSystemVersionUtils {

	private static final String LATEST_VERSION = "Latest version";

	/**
	 * Creates a fake {@link ICodeSystemVersion} representing the HEAD in the given repository.
	 * @param repositoryUuid the repository UUID.
	 * @param branchPath: on which branch the code system version is created, eg.: on an extension branch.
	 * @return a fake version representing the HEAD in the repository.
	 */
	public static ICodeSystemVersion createLatestCodeSystemVersion(final String repositoryUuid, final String branchPath) {

		return new CodeSystemVersionEntry(MAX_VALUE, MAX_VALUE, UNSET_EFFECTIVE_TIME, LATEST_VERSION, UNVERSIONED, branchPath, NO_STORAGE_KEY, repositoryUuid) {
			
			private static final long serialVersionUID = 3431197771869140761L;

			@Override
			public boolean isPatched() {
				return false;
			}
		};
		
	}
	
	private static class LatestCodeSystemVersionPredicate implements Predicate<ICodeSystemVersion> {

		@Override
		public boolean apply(ICodeSystemVersion input) {
			return isLatestVersion(input);
		}
		
	}
	
	/**
	 * Returns {@code true} if the version is a fake {@link ICodeSystemVersion} implementation
	 * representing the HEAD in the repository.
	 * @param version the version to check.
	 * @return {@code true} if the argument is the latest version, otherwise {@code false}
	 */
	public static boolean isLatestVersion(final ICodeSystemVersion version) {
		return UNVERSIONED.equals(version.getVersionId());
	}
	
	public static Predicate<ICodeSystemVersion> latestCodeSystemVersionPredicate()  {
		return new LatestCodeSystemVersionPredicate();
	}

}