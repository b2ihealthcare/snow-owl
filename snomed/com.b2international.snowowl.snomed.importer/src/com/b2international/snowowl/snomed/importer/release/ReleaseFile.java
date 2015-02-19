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

import java.util.regex.Pattern;

import com.b2international.snowowl.snomed.common.ContentSubType;

/**
 * Abstract superclass for all release files whose location has to be detected
 * when a zip file or a directory is selected for import.
 * 
 * @since 1.3
 */
public abstract class ReleaseFile {
	
	protected final boolean optional;
	protected final String initialSegment;

	/**
	 * Creates a new release file identifier instance.
	 * 
	 * @param optional <code>true</code> if this file does not need to be
	 * present in the release directory or archive, <code>false</code> otherwise
	 * @param initialSegment the directory segment that must appear before the file name
	 */
	public ReleaseFile(final boolean optional, final String initialSegment) {
		this.optional = optional;
		this.initialSegment = initialSegment;
	}

	/**
	 * @return <code>true</code> if this file does not need to be present in the
	 *         release directory or archive, <code>false</code> otherwise
	 */
	public boolean isOptional() {
		return optional;
	}
	
	/**
	 * Creates a regular expression pattern for this release file.
	 * 
	 * @param testRelease <code>true</code> if the release is considered a test
	 * release, <code>false</code> otherwise
	 * @param contentSubType the subtype to look for (snapshot, delta or full
	 * content)
	 * @param currentReleaseIdentifier the release identifier to use when
	 * creating the pattern (wildcard or already matched, exact)
	 * @param relativeRoot the relative directory root to use (wildcard or
	 * already matched, exact)
	 * @return a compiled pattern for matching against a list of file names
	 */
	public abstract Pattern createPattern(boolean testRelease, ContentSubType contentSubType, ReleaseIdentifier currentReleaseIdentifier, 
			String relativeRoot);
}