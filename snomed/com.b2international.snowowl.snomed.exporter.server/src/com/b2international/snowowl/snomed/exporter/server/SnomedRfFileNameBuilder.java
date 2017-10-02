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
package com.b2international.snowowl.snomed.exporter.server;

import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.LANGUAGE;

import java.util.Date;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.LanguageConfiguration;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Preconditions;

/**
 * Utility class for building core release file names when publishing
 * SNOMED CT terminology. Also supports reference set, cross map and subset
 * file name building for both supported release formats.
 */
public class SnomedRfFileNameBuilder {

	public static String buildCoreRf1FileName(final ComponentExportType type, final SnomedExportConfiguration configuration) {
		return new StringBuilder("sct1_")
				.append(String.valueOf(type))
				.append("s_")
				.append(ComponentExportType.DESCRIPTION.equals(type) ? getLanguageCode() : "Core")
				.append("_" + configuration.getCountryAndNamespaceElement() + "_")
				.append(getReleaseDate(configuration))
				.append(".txt")
				.toString();
	}

	public static String buildCoreRf2FileName(final ComponentExportType type, final SnomedExportConfiguration configuration) {
		return new StringBuilder("sct2_")
				.append(String.valueOf(type))
				.append("_")
				.append(String.valueOf(configuration.getContentSubType()))
				.append(ComponentExportType.DESCRIPTION.equals(type) ? "-" : "")
				.append(ComponentExportType.DESCRIPTION.equals(type) ? getLanguageCode() : "")
				.append("_" + configuration.getCountryAndNamespaceElement() + "_")
				.append(getReleaseDate(configuration))
				.append(".txt")
				.toString();
	}

	/*
	 * return the transient effective time if set, otherwise today's date
	 */
	private static String getReleaseDate(final SnomedExportConfiguration config) {
		
		String releaseDate = getExportTime();
		if (!config.getUnsetEffectiveTimeLabel().isEmpty() && !config.getUnsetEffectiveTimeLabel().equals(EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL)) {
			releaseDate = config.getUnsetEffectiveTimeLabel();
		}
		return releaseDate;
	}

	public static String buildRefSetFileName(final SnomedExportConfiguration configuration, final String refSetName, final SnomedRefSet refSet) {
		return buildRefSetFileName(configuration, refSetName, refSet, false);
	}

	public static String buildRefSetFileName(final SnomedExportConfiguration configuration, final String refSetName, final SnomedRefSet refSet, final boolean includeMapTargetDescription) {
		return new StringBuilder("der2_")
				.append(getPrefix(refSet, includeMapTargetDescription))
				.append("Refset_")
				.append(toCamelCase(refSetName))
				.append(String.valueOf(configuration.getContentSubType()))
				.append(isLanguageType(refSet) ? "-" : "")
				.append(isLanguageType(refSet) ? getLanguageCode(refSet) : "")
				.append("_" + configuration.getCountryAndNamespaceElement() + "_")
				.append(getReleaseDate(configuration))
				.append(".txt")
				.toString();
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

	/*returns with the language code for the reference set*/
	private static String getLanguageCode(final SnomedRefSet refSet) {
		String languageCode = com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping.getLanguageCode(refSet.getIdentifierId());
		return languageCode.length() > 2 ? languageCode.substring(0, 3) + languageCode.substring(3, 5).toUpperCase() : languageCode;
	}

	/*returns true if the reference set is a language type*/
	private static boolean isLanguageType(final SnomedRefSet refSet) {
		return LANGUAGE.equals(refSet.getType()); 
	}

	/*returns with the RF2 file name prefix for the reference set*/
	private static String getPrefix(final SnomedRefSet refSet, final boolean includeMapTargetDescription) {
		switch (Preconditions.checkNotNull(refSet, "SNOMED CT reference set argument cannot be null.").getType()) {
		case CONCRETE_DATA_TYPE: return "ccss";
		case QUERY: return "s";
		case SIMPLE: return "";
		case LANGUAGE:
		case ATTRIBUTE_VALUE:
		case ASSOCIATION: return "c";
		case DESCRIPTION_TYPE: return "ci";
		case COMPLEX_MAP: return "iisssc";
		case EXTENDED_MAP: return "iissscc";
		case SIMPLE_MAP: return getSimpleMapPrefix(refSet, includeMapTargetDescription);
		case MODULE_DEPENDENCY: return "ss";
		}
		throw new IllegalArgumentException ("Unknown reference set type. Type: " + refSet.getType());
	}
	
	private static String getSimpleMapPrefix(final SnomedRefSet refSet, final boolean includeMapTargetDescription) {
		return includeMapTargetDescription ? "ss" : "s";
	}

	/*returns with the previously configured release time in yyyyMMdd format*/
	private static String getExportTime() {
		return Dates.formatByHostTimeZone(new Date(), DateFormats.SHORT);
	}

	/*returns with the language code*/
	private static String getLanguageCode() {
		String languageCode = getLanguageConfiguration().getLanguageCode();
		return languageCode.length() > 2 ? languageCode.substring(0, 3) + languageCode.substring(3, 5).toUpperCase() : languageCode;
	}

	/*returns with the current language configuration for the SNOMED CT terminology*/
	private static LanguageConfiguration getLanguageConfiguration() {
		return ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration();
	}

	private SnomedRfFileNameBuilder() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
