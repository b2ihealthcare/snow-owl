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
 * Generates file name matching patterns for description files. 
 * 
 * @since 1.3
 */
public class DescriptionReleaseFile extends ReleaseFile {

	public DescriptionReleaseFile(final String initialSegment) {
		super(false, initialSegment);
	}

	@Override
	public Pattern createPattern(final boolean testRelease, final ContentSubType contentSubType, final ReleaseIdentifier currentReleaseIdentifier, 
			final String relativeRoot) {
		
		final StringBuilder patternBuilder = new StringBuilder("(");
		
		patternBuilder.append(relativeRoot);
		patternBuilder.append(')');
		patternBuilder.append(initialSegment);
		patternBuilder.append('/');
		
		if (testRelease) {
			patternBuilder.append('x');
		}
		
		patternBuilder.append("sct2_Description");
		patternBuilder.append('_');
		patternBuilder.append(contentSubType.getDisplayName());
		patternBuilder.append('-');
		patternBuilder.append("([a-z]{2}|[a-z]{2}-[a-zA-Z]{2})");
		patternBuilder.append('_');
		patternBuilder.append(currentReleaseIdentifier.getCountryNamespacePair());
		patternBuilder.append('_');
		patternBuilder.append(currentReleaseIdentifier.getReleaseDate());
		patternBuilder.append("\\.txt");
		patternBuilder.append('$');
		
		return Pattern.compile(patternBuilder.toString());
	}
}