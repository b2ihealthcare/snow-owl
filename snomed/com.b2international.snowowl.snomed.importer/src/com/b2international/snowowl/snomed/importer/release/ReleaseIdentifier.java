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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Stores the country-namespace pair (if present) and the release date found in
 * release file name parts.
 * 
 * @since 1.3
 */
public class ReleaseIdentifier {

	public static final ReleaseIdentifier UNKNOWN_IDENTIFIER = new ReleaseIdentifier("?", "?");
	
	public static ReleaseIdentifier createFromFile(File releaseFile) {
		Pattern test = Pattern.compile(".*_(INT|INT[0-9]{7}|[A-Z]{2}[0-9]{7}|[0-9]{7})_([2-9][0-9]{3}[0-1][0-9][0-3][0-9])\\.txt$");
		Matcher matcher = test.matcher(releaseFile.getPath());
		
		if (!matcher.matches()) {
			return UNKNOWN_IDENTIFIER; 
		}
		
		return new ReleaseIdentifier(matcher.group(1), matcher.group(2));
	}
	
	private final String countryNamespacePair;
	private final String releaseDate;

	public ReleaseIdentifier(String countryNamespacePair, String releaseDate) {
		
		checkNotNull(countryNamespacePair, "countryNamespacePair");
		checkArgument(!countryNamespacePair.isEmpty(), "countryNamespacePair is empty.");
		checkNotNull(releaseDate, "releaseDate");
		checkArgument(!releaseDate.isEmpty(), "releaseDate is empty.");
		
		this.countryNamespacePair = countryNamespacePair;
		this.releaseDate = releaseDate;
	}
	
	public String getCountryNamespacePair() {
		return countryNamespacePair;
	}
	
	public String getReleaseDate() {
		return releaseDate;
	}
	
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof ReleaseIdentifier)) {
			return false;
		}
		
		ReleaseIdentifier other = (ReleaseIdentifier) obj;
		return countryNamespacePair.equals(other.countryNamespacePair) && releaseDate.equals(other.releaseDate);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(countryNamespacePair, releaseDate);
	}
	
	@Override
	public String toString() {
		
		return MoreObjects.toStringHelper(this)
				.add("countryNamespacePair", countryNamespacePair)
				.add("releaseDate", releaseDate)
				.toString();
	}
}