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
package com.b2international.snowowl.snomed.importer.release;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.snomed.common.ContentSubType;

/**
 * Provides a way to validate if a set of relative paths constitute a valid
 * release directory or archive, and converts symbolic component types into path
 * names.
 * 
 * @since 1.3
 */
public class ReleaseFileSet {

	/*
	 * "The Country|Namespace element of the filename helps to identify the
	 * organization responsible for developing and maintaining the file. Its
	 * format is 2-10 alphanumeric characters consisting of 0, 2 or 3
	 * upper-case letters followed by 0 or 7 digits."
	 * 
	 * Note: we're not checking valid ISO country codes and dates at the moment
	 */
	private static final ReleaseIdentifier INITIAL_RELEASE_IDENTIFIER = new ReleaseIdentifier(
			"(INT|SG|INT[0-9]{7}|[A-Z]{2}[0-9]{7}|[0-9]{7}|SG[0-9]{7}|[A-Z]{2}[0-9]{7}|[0-9]{7})",
			"([2-9][0-9]{3}[0-1][0-9][0-3][0-9])");

	private static final String INITIAL_RELEASE_ROOT = ".*";
	
	public enum ReleaseComponentType {
		CONCEPT,
		DESCRIPTION,
		RELATIONSHIP,
		STATED_RELATIONSHIP,
		LANGUAGE_REFERENCE_SET,
		TEXT_DEFINITION
	}
	
	private final Map<ReleaseComponentType, ReleaseFile> releaseFiles;
	private final boolean testRelease;
	private final List<String> refSetPaths;
	
	private ReleaseIdentifier releaseIdentifier = INITIAL_RELEASE_IDENTIFIER;
	private String relativeRoot = INITIAL_RELEASE_ROOT;
	
	/**
	 * Creates a new release file set instance.
	 * 
	 * @param testRelease
	 *            <code>true</code> if this set is considered a test release,
	 *            <code>false</code> otherwise
	 *            
	 * @param releaseFiles
	 *            a map containing {@link ReleaseFile} descriptors for
	 *            {@link ReleaseComponentType} keys
	 *            
	 * @param refSetPaths
	 *            a list of relative paths where reference sets can be located
	 */
	public ReleaseFileSet(boolean testRelease, Map<ReleaseComponentType, ReleaseFile> releaseFiles, List<String> refSetPaths) {
		this.testRelease = testRelease;
		this.releaseFiles = releaseFiles;
		this.refSetPaths = refSetPaths;
	}
	
	/**
	 * Checks if the contents of an archive or directory would be a valid
	 * release by looking for matching file names in the specified list with the
	 * specified content subtype.
	 * 
	 * @param relativeLocations the contents of the archive or directory (a list
	 * of relative paths)
	 * 
	 * @param contentSubType the content subtype to look for (may not be
	 * {@code null})
	 * 
	 * @return <code>true</code> if the items in the list form a valid release
	 * set, <code>false</code> otherwise
	 */
	public boolean isValidReleaseRoot(List<String> relativeLocations, ContentSubType contentSubType) {

		if (relativeLocations.isEmpty()) {
			return false;
		}

		releaseIdentifier = INITIAL_RELEASE_IDENTIFIER;
		relativeRoot = INITIAL_RELEASE_ROOT;
		
		boolean matchFound = true;

		for (ReleaseFile releaseFile : releaseFiles.values()) {

			Pattern patternToMatch = releaseFile.createPattern(testRelease, contentSubType, releaseIdentifier, relativeRoot);
			boolean matchFoundForReleaseFile = false;

			for (String relativeLocation : relativeLocations) {

				Matcher matcher = patternToMatch.matcher(relativeLocation);

				if (matcher.matches()) {

					// This should be set on the first occasion, i.e. when a concept file is matched
					if (relativeRoot == INITIAL_RELEASE_ROOT) {
						relativeRoot = Pattern.quote(matcher.group(1));
					}
					
					if (releaseIdentifier == INITIAL_RELEASE_IDENTIFIER) {
						releaseIdentifier = new ReleaseIdentifier(matcher.group(2), matcher.group(3));
					}

					matchFoundForReleaseFile = true;
					break;
				}
			}

			if (!matchFoundForReleaseFile && !releaseFile.isOptional()) {
				matchFound = false;
				break;
			}
		}

		return matchFound;
	}
	
	/**
	 * Extracts a path name for the specified component type from the list of
	 * paths.
	 * 
	 * @param relativeLocations the locations to check
	 * @param type the requested component type
	 * @param contentSubType the requested subtype
	 * @return a file path for the component, or an empty string if no matching
	 * path could be found
	 */
	public String getFileName(List<String> relativeLocations, ReleaseComponentType type, ContentSubType contentSubType) {
		
		if (CompareUtils.isEmpty(relativeLocations)) {
			return null;
		}
		
		ReleaseFile releaseFile = releaseFiles.get(type);
		
		if (releaseFile == null) {
			return null;
		}
		
		Pattern patternToMatch = releaseFile.createPattern(testRelease, contentSubType, releaseIdentifier, relativeRoot);
		
		for (String relativeLocation : relativeLocations) {
			
			Matcher matcher = patternToMatch.matcher(relativeLocation);
			
			if (matcher.matches()) {
				return matcher.group();
			}
		}

		return null;
	}
	
	/**
	 * Extracts a path names for a specified component type from the list of
	 * paths.
	 * 
	 * @param relativeLocations the locations to check
	 * @param type the requested component type
	 * @param contentSubType the requested subtype
	 * @return a collection file path for the component. could be empty collection if no match could be found 
	 */
	public Collection<String> getAllFileName(Iterable<String> relativeLocations, ReleaseComponentType type, ContentSubType contentSubType) {

		if (CompareUtils.isEmpty(relativeLocations)) {
			return Collections.emptySet();
		}
		
		ReleaseFile releaseFile = releaseFiles.get(type);
		
		if (releaseFile == null) {
			return Collections.emptySet();
		}
		
		Pattern patternToMatch = releaseFile.createPattern(testRelease, contentSubType, releaseIdentifier, relativeRoot);
		
		List<String> fileNames = newArrayList();
		
		for (String relativeLocation : relativeLocations) {
			
			Matcher matcher = patternToMatch.matcher(relativeLocation);
			
			if (matcher.matches()) {
				fileNames.add(matcher.group());
			}
		}

		return fileNames;
	}
	
	
	/**
	 * @return the matched release identifier, if it could be extracted during a
	 *         previous call to {@link #isValidReleaseRoot(List)}
	 *         
	 * @throws IllegalArgumentException
	 *             if the release identifier could not be determined yet
	 */
	public ReleaseIdentifier getReleaseIdentifier() {
		checkState(releaseIdentifier != INITIAL_RELEASE_IDENTIFIER, "Release identifier has not been determined yet.");
		return releaseIdentifier;
	}
	
	public String getRelativeRoot() {
		checkState(relativeRoot != INITIAL_RELEASE_ROOT, "Release root has not been determined yet.");
		
		if (relativeRoot.length() > 4) {
			return relativeRoot.substring(2, relativeRoot.length() - 2); // Remove regexp quote characters
		} else {
			return relativeRoot;
		}
	}

	/**
	 * @return <code>true</code> if this set is considered a test release,
	 *         <code>false</code> otherwise
	 */
	public boolean isTestRelease() {
		return testRelease;
	}

	/**
	 * @return a list of path names from which possible reference set files can
	 *         be collected
	 */
	public List<String> getRefSetPaths() {
		return refSetPaths;
	}
}