/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.primitives.Ints;

/**
 * Comparator for version strings conforming to the <code>\d+(\.\d+)+</code> format.
 * 
 */
public final class VersionNumberComparator implements Comparator<String> {
	private static final Pattern VERSION_PATTERN = Pattern.compile("\\d+(\\.\\d+)+");

	/**
	 * @throws IllegalArgumentException if either of the arguments are not in the expected format
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(String version1, String version2) {
		checkArgument(checkVersionFormat(version1), "Version string format is invalid: " + version1);
		checkArgument(checkVersionFormat(version2), "Version string format is invalid: " + version2);
		List<Integer> version1Parts = Version.parseParts(version1);
		List<Integer> version2Parts = Version.parseParts(version2);
		int minLength = Math.min(version1Parts.size(), version2Parts.size());
		int maxLength = Math.max(version1Parts.size(), version2Parts.size());

		int i = 0;
		for (; i < minLength; i++) {
			Integer integer1 = version1Parts.get(i);
			Integer integer2 = version2Parts.get(i);
			int compareResult = integer1.compareTo(integer2);
			if (compareResult != 0) {
				return compareResult;
			}
		}
		
		if (minLength != maxLength) {
			return Ints.compare(version1Parts.size(), version2Parts.size());
		} else {
			return 0;
		}
	}
	
	/**
	 * Checks the specified version string.
	 * 
	 * @param version the specified version string
	 * @return true if the specified version format is acceptable by this comparator
	 */
	public static boolean checkVersionFormat(String version) {
		return VERSION_PATTERN.matcher(version).matches();
	}
}