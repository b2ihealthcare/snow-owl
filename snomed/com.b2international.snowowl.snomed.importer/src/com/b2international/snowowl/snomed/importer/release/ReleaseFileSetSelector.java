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
package com.b2international.snowowl.snomed.importer.release;

import java.util.List;

import com.b2international.snowowl.snomed.common.ContentSubType;
import com.google.common.collect.ImmutableList;

/**
 * Provides a method for querying multiple release file set configurations, and
 * return the first one which reports a list of path names to be a valid release
 * root.
 * 
 * @since 1.3
 */
public class ReleaseFileSetSelector {

	private final List<ReleaseFileSet> fileSets;

	/**
	 * Creates a new release file set selector instance.
	 * 
	 * @param fileSets the file sets to check, listed in order of preference
	 */
	public ReleaseFileSetSelector(final List<ReleaseFileSet> fileSets) {
		this.fileSets = ImmutableList.copyOf(fileSets);
	}
	
	/**
	 * @param relativeLocations the list of relative path names to check
	 * @param contentSubType the content subtype to use 
	 * @return the first release file set which reports a valid release root, or
	 * <code>null</code> if no such set could be found
	 */
	public ReleaseFileSet getFirstApplicable(final List<String> relativeLocations, final ContentSubType contentSubType) {
		
		for (final ReleaseFileSet fileSet : fileSets) {
			if (fileSet.isValidReleaseRoot(relativeLocations, contentSubType)) {
				return fileSet;
			}
		}
		
		return null;
	}
}