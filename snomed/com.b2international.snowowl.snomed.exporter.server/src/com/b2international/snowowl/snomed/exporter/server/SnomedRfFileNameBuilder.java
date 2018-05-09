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
package com.b2international.snowowl.snomed.exporter.server;

import java.util.Date;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * Utility class for building core release file names when publishing
 * SNOMED CT terminology. Also supports reference set, cross map and subset
 * file name building for both supported release formats.
 */
public class SnomedRfFileNameBuilder {

	public static String buildCoreRf1FileName(final ComponentExportType type, final SnomedExportContext exportContext) {
		return new StringBuilder("sct1_")
				.append(String.valueOf(type))
				.append("s_")
				.append(ComponentExportType.DESCRIPTION.equals(type) ? "en" : "Core")
				.append('_')
				.append(exportContext.getNamespaceId())
				.append('_')
				.append(getReleaseDate(exportContext))
				.append(".txt")
				.toString();
	}

	public static String buildCoreRf2FileName(final ComponentExportType type, final SnomedExportContext exportContext) {
		return new StringBuilder("sct2_")
				.append(String.valueOf(type))
				.append('_')
				.append(String.valueOf(exportContext.getContentSubType()))
				.append('_')
				.append(exportContext.getNamespaceId())
				.append('_')
				.append(getReleaseDate(exportContext))
				.append(".txt")
				.toString();
	}
	
	public static String buildRefSetFileName(final SnomedExportContext exportContext, final String refSetName, final SnomedReferenceSet refSet) {
		return buildRefSetFileName(exportContext, refSetName, refSet, false);
	}

	public static String buildRefSetFileName(final SnomedExportContext exportContext, final String refSetName, final SnomedReferenceSet refSet,
			final boolean includeMapTargetDescription) {
		return new StringBuilder("der2_")
				.append(getPrefix(refSet.getType()))
				.append("Refset_")
				.append(toCamelCase(refSetName))
				.append(String.valueOf(exportContext.getContentSubType()))
				.append('_')
				.append(exportContext.getNamespaceId())
				.append('_')
				.append(getReleaseDate(exportContext))
				.append(".txt")
				.toString();
	}

	/**
	 * Returns the transient effective time if set, otherwise today's date
	 */
	public static String getReleaseDate(final SnomedExportContext exportContext) {
		if (!exportContext.getUnsetEffectiveTimeLabel().isEmpty()
				&& !exportContext.getUnsetEffectiveTimeLabel().equals(EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL)) {
			return exportContext.getUnsetEffectiveTimeLabel();
		}
		return Dates.formatByHostTimeZone(new Date(), DateFormats.SHORT);
	}

	/**
	 * Turns the specified string into camel case format.
	 * 
	 * @param orig the original string
	 * @return the camel case format of the original string
	 */
	public static String toCamelCase(final String orig) {
		if (StringUtils.isEmpty(orig)) {
			return orig;
		}

		boolean lastCharacterSpace = true;
		int writeIdx = 0;
		final StringBuilder result = new StringBuilder(orig);

		for (int readIdx = 0; readIdx < result.length(); readIdx++) {
			final char readCharacter = result.charAt(readIdx);
			final boolean currentCharacterSpace = Character.isWhitespace(readCharacter);

			if (!currentCharacterSpace) {
				final char writtenCharacter = lastCharacterSpace ? Character.toUpperCase(readCharacter) : readCharacter;
				result.setCharAt(writeIdx++, writtenCharacter);
			}

			lastCharacterSpace = currentCharacterSpace;
		}

		return result.substring(0, writeIdx);
	}

	/**
	 * Returns the column prefix for reference sets
	 */
	public static String getPrefix(final SnomedRefSetType type) {
		switch (type) {
			case CONCRETE_DATA_TYPE: return "ccss";
			case QUERY: return "s";
			case SIMPLE: return "";
			case LANGUAGE:
			case ATTRIBUTE_VALUE:
			case ASSOCIATION: return "c";
			case DESCRIPTION_TYPE: return "ci";
			case COMPLEX_MAP: return "iisssc";
			case EXTENDED_MAP: return "iissscc";
			case SIMPLE_MAP: return "s";
			case SIMPLE_MAP_WITH_DESCRIPTION: return "ss";
			case MODULE_DEPENDENCY: return "ss";
			case OWL_AXIOM: return "s";
		}
		throw new IllegalArgumentException ("Unknown reference set type. Type: " + type);
	}
	
	private SnomedRfFileNameBuilder() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
