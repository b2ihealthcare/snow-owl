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
 * Generates file name matching patterns for core terminology files (excluding
 * descriptions).
 * 
 * @since 1.3
 */
public class SimpleReleaseFile extends ReleaseFile {

	public enum SimpleReleaseComponentType {
		
		CONCEPT("Concept"), 
		RELATIONSHIP("Relationship"), 
		STATEDRELATIONSHIP("StatedRelationship");
		
		private final String displayName;
		
		private SimpleReleaseComponentType(final String displayName) {
			this.displayName = displayName;
		}

		/**
		 * @return the display name of this component type, which is the Capitalized
		 *         version of the item name
		 */
		public String getDisplayName() {
			return displayName;
		}
	}
	
	private final SimpleReleaseComponentType componentType;
	
	public SimpleReleaseFile(final String initialSegment, final SimpleReleaseComponentType componentType) {
		super(false, initialSegment);
		this.componentType = componentType;
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
		
		patternBuilder.append("sct2_");
		patternBuilder.append(componentType.getDisplayName());
		patternBuilder.append('_');
		patternBuilder.append(contentSubType.getDisplayName());
		patternBuilder.append('_');
		patternBuilder.append(currentReleaseIdentifier.getCountryNamespacePair());
		patternBuilder.append('_');
		patternBuilder.append(currentReleaseIdentifier.getReleaseDate());
		patternBuilder.append("\\.txt");
		patternBuilder.append('$');
		
		return Pattern.compile(patternBuilder.toString());
	}
}