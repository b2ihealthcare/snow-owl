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

import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.*

import com.b2international.snowowl.core.ApplicationContext
import com.b2international.snowowl.core.CoreTerminologyBroker
import com.b2international.snowowl.core.date.DateFormats
import com.b2international.snowowl.core.date.Dates
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider
import com.b2international.snowowl.snomed.datastore.LanguageConfiguration
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet
import com.google.common.base.Preconditions


/**
 * Utility class for building core release file names when publishing SNOMED&nbsp;CT terminology. Also supports reference set, cross map and subset 
 * file name building for both supported release formats.
 * @groovy
 */
class SnomedRfFileNameBuilder {
	
	public static String buildCoreRf1FileName(final ComponentExportType type, final SnomedExportConfiguration configuration) {
		return new StringBuilder("sct1_")
		.append(String.valueOf(type))
		.append("s_")
		.append(ComponentExportType.DESCRIPTION.equals(type) ? getLanguageCode() : "Core")
		.append("_INT_")
		.append(getExportTime())
		.append(".txt")
		.toString();
	}

	public static String buildCoreRf2FileName(final ComponentExportType type, final SnomedExportConfiguration configuration) {
		return new StringBuilder("sct2_")
		.append(String.valueOf(type))
		.append("_")
		.append(String.valueOf(configuration.contentSubType))
		.append(ComponentExportType.DESCRIPTION.equals(type) ? "-" : "")
		.append(ComponentExportType.DESCRIPTION.equals(type) ? getLanguageCode() : "")
		.append("_INT_")
		.append(getExportTime())
		.append(".txt")
		.toString();
	}
	
	public static String buildRefSetFileName(final SnomedExportConfiguration configuration, final SnomedRefSet refSet) {
		return buildRefSetFileName(configuration, toCamelCase(getPreferredTerm(refSet)), refSet)
	}
	
	public static String buildRefSetFileName(final SnomedExportConfiguration configuration, final String refSetName, final SnomedRefSet refSet) {
		return new StringBuilder("der2_")
		.append(getPrefix(refSet))
		.append("Refset_")
		.append(toCamelCase(refSetName))
		.append(String.valueOf(configuration.contentSubType))
		.append(isLanguageType(refSet) ? "-" : "")
		.append(isLanguageType(refSet) ? getLanguageCode(refSet) : "")
		.append("_INT_")
		.append(getExportTime())
		.append(".txt")
		.toString();
	}
	
	/**
	* Turns the specified string into camel case format.
	* @param orig the original string.
	* @return the camel case format of the original string.
	*/
   public static def toCamelCase(orig) {
	   orig.toLowerCase().replaceAll(/(_)+/, " ").replaceAll(/(\W)+/, "_").replaceFirst(/(\w)/, {
		   wholeMatch, firstLetter -> firstLetter.toUpperCase()}).replaceAll(/_(\w)?/) {
			   wholeMatch, firstLetter -> firstLetter?.toUpperCase() ?: ""
	   }
   }

	/*returns with the preferred term of the reference set identifier concept*/	
	private static String getPreferredTerm(final SnomedRefSet refSet) {
		return SnomedConceptNameProvider.INSTANCE.getText(refSet.getIdentifierId(), refSet.cdoView()); 
	}
	
	/*returns with the language code for the reference set*/
	private static String getLanguageCode(final SnomedRefSet refSet) {
		return com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping.getLanguageCode(refSet.getIdentifierId()); 
	}
	
	/*returns true if the reference set is a language type*/
	private static boolean isLanguageType(final SnomedRefSet refSet) {
		return LANGUAGE.equals(refSet.getType()); 
	}
	
	/*returns with the RF2 file name prefix for the reference set*/
	private static String getPrefix(final SnomedRefSet refSet) {
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
			case SIMPLE_MAP: return ((SnomedMappingRefSet) refSet).getMapTargetComponentType() == CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT ? "s" : "c";
			case MODULE_DEPENDENCY: return "ss";
		}
		throw new IllegalArgumentException ("Unknown reference set type. Type: " + refSet.getType());
	}

	/*returns with the previously configured release time in yyyyMMdd format*/
	private static String getExportTime() {
		return Dates.formatByHostTimeZone(new Date(), DateFormats.SHORT)
	}

	/*returns with the language code*/
	private static String getLanguageCode() {
		return getLanguageConfiguration().getLanguageCode();
	}

	/*returns with the current language configuration for the SNOMED CT terminology*/
	private static LanguageConfiguration getLanguageConfiguration() {
		return ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration();
	}

	/*returns with the terminology browser for the SNOMED CT terminology*/
	private static SnomedClientTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
	}

}
	
