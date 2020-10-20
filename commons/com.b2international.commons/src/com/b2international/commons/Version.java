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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

/**
 * Represents a multi-part version string conforming to the <code>\d+(\.\d+)+</code> format.
 * 
 */
public class Version implements Comparable<Version> {

	private static final char VERSION_PART_SEPARATOR = '.';
	private static final Pattern VERSION_PATTERN = Pattern.compile("\\d+(\\.\\d+)+");
	
	private final List<Integer> parts;
	
	public Version(List<Integer> parts) {
		this.parts = ImmutableList.copyOf(parts);
	}
	
	@Override
	public String toString() {
		Joiner joiner = Joiner.on(VERSION_PART_SEPARATOR);
		return joiner.join(parts.iterator());
	}

	/**
	 * Creates a new version by incrementing the last part of this version.
	 * 
	 * @return the incremented version
	 */
	public Version increment() {
		List<Integer> newParts = Lists.newArrayList(parts);
		newParts.set(newParts.size() - 1, newParts.get(newParts.size()- 1) + 1);
		return new Version(newParts);
	}
	
	/**
	 * Parses the specified {@link String} into a {@link Version} object.
	 * The version string must be non-null and conform to the <code>\d+(\.\d+)+</code> format.
	 * 
	 * @param versionString the version string to parse
	 * @return the parsed {@link Version}
	 */
	public static Version parseVersion(String versionString) {
		checkNotNull(versionString, "Version string must not be null.");
		checkArgument(VERSION_PATTERN.matcher(versionString).matches(), "Version string format is invalid: " + versionString);
		return new Version(parseParts(versionString));
	}

	@Override
	public int hashCode() {
		return Objects.hash(parts);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Version other = (Version) obj;
		return Objects.equals(parts, other.parts);
	}

	@Override
	public int compareTo(Version other) {
		int minLength = Math.min(this.parts.size(), other.parts.size());
		int maxLength = Math.max(this.parts.size(), other.parts.size());

		int i = 0;
		for (; i < minLength; i++) {
			Integer integer1 = this.parts.get(i);
			Integer integer2 = other.parts.get(i);
			int compareResult = integer1.compareTo(integer2);
			if (compareResult != 0) {
				return compareResult;
			}
		}
		
		if (minLength != maxLength) {
			return Ints.compare(this.parts.size(), other.parts.size());
		} else {
			return 0;
		}
	}

	public static List<Integer> parseParts(String version) {
		return Splitter.on(VERSION_PART_SEPARATOR).splitToList(version).stream().map(Integer::valueOf).collect(Collectors.toList());
	}
	
}