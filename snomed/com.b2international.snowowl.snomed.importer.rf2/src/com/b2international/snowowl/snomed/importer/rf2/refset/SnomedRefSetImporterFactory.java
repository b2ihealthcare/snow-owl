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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Closeables;

public abstract class SnomedRefSetImporterFactory {
	
	private static final List<String> FORCE_SIMPLE_MAP_REFSET_URLS = ImmutableList.of(
			ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20110531_CTV3_ID_REFSET_NAME,
			ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20110531_SNOMED_RT_REFSET_NAME);
	
	private static final List<String> FORCE_LANGUAGE_REFSET_URLS = ImmutableList.of(
			ErroneousAustralianReleaseFileNames.ERRONEOUS_AU_20110531_SNOMED_LANGUAGE_REFSET_NAME);

	/**
	 * Huge hack, but choosing between bad things, this is the less bad.
	 * Have the given reference set release file (given as {@link Reader}), read the header and check the latest column name.
	 * 
	 * Depending on the last column of the read file, the following reference set importers will be created:
	 * <table border="1">
	 * 		<tr><th>Column name</th><th>Importer type</th></tr>
	 * 		<tr><td>referencedComponentId</td><td>{@link SnomedSimpleTypeRefSetImporter}</td></tr>
	 * 		<tr><td>query</td><td>{@link SnomedQueryRefSetImporter}</td></tr>
	 * 		<tr><td>valueId</td><td>{@link SnomedAttributeValueRefSetImporter}</td></tr>
	 * 		<tr><td>mapTarget</td><td>{@link SnomedSimpleMapTypeRefSetImporter}</td></tr>
	 * 		<tr><td>acceptabilityId</td><td>{@link SnomedLanguageRefSetImporter}</td></tr>
	 * 		<tr><td>targetComponent</td><td>{@link SnomedAssociationRefSetImporter}<td></tr>
	 * 		<tr><td><b>targetComponentId</b> (incorrect AU)</td><td>{@link SnomedAssociationRefSetImporter}<td></tr>
	 * 		<tr><td>correlationId</td><td>{@link SnomedComplexMapTypeRefSetImporter}<td></tr>
	 * 		<tr><td>descriptionLength</td><td>{@link SnomedDescriptionTypeRefSetImporter}<td></tr>
	 * 		<tr><td>characteristicTypeId (SG)</td><td>{@link SnomedConcreteDataTypeRefSetImporter}<td></tr>
	 * 		<tr><td>value (AU)</td><td>{@link SnomedConcreteDataTypeRefSetImporter}<td></tr>
	 * </table>
	 * 
	 * @param importContext 
	 * @param urlIdentifier 
	 * @param refSetImportContext 
	 */
	public static AbstractSnomedRefSetImporter<?, ?> createRefSetImporter(final URL url, final SnomedImportContext importContext, String urlIdentifier) 
			throws IOException {
		
		if (url == null) {
			return null;
		}
		
		/* 
		 * Certain release files in the Australian release have an incorrect header which would misguide the heuristics below; assign
		 * an importer for them directly.
		 */
		for (final String forceSimpleMapRefSetUrlPart : FORCE_SIMPLE_MAP_REFSET_URLS) {
			if (url.toString().contains(forceSimpleMapRefSetUrlPart)) {
				importContext.getLogger().info(MessageFormat.format("Forced reference set ''{0}'' to simple type.", url));
				return new SnomedSimpleMapTypeRefSetImporter(importContext, url.openStream(), urlIdentifier);	
			}
		}
		
		for (final String forceLanguageRefSetUrlPart : FORCE_LANGUAGE_REFSET_URLS) {
			if (url.toString().contains(forceLanguageRefSetUrlPart)) {
				importContext.getLogger().info(MessageFormat.format("Forced reference set ''{0}'' to language type.", url));
				return new SnomedLanguageRefSetImporter(importContext, url.openStream(), urlIdentifier);	
			}			
		}

		BufferedReader br = null;
		String lastColumnName;
		
		try {
			
			final InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
			br = new BufferedReader(inputStreamReader);
			final String header = br.readLine();
			
			if (StringUtils.isEmpty(header)) {
				
				return null;
				
			}
			
			lastColumnName = header.substring(header.lastIndexOf("\t") + 1);
			
		} finally {
			Closeables.closeQuietly(br);
		}
		
		if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_QUERY)) {
			return new SnomedQueryRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_VALUE_ID)) {
			return new SnomedAttributeValueRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_COMPONENT) || lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID)) {
			return new SnomedAssociationRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET)) {
			return new SnomedSimpleMapTypeRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION)) {
			return new SnomedSimpleMapWithDescriptionRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID)) {
			return new SnomedLanguageRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID)) {
			return new SnomedSimpleTypeRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_CORRELATION_ID)) {
			return new SnomedComplexMapTypeRefSetImporter(importContext, url.openStream(), false, urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID)) {
			return new SnomedComplexMapTypeRefSetImporter(importContext, url.openStream(), true, urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH)) {
			return new SnomedDescriptionTypeRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID)) {
			return new SnomedConcreteDataTypeRefSetImporter(importContext, url.openStream(), true, urlIdentifier); 
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_VALUE)) {
			return new SnomedConcreteDataTypeRefSetImporter(importContext, url.openStream(), false, urlIdentifier); // AU CDT refset
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
			return new SnomedModuleDependencyRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else {
			return null;
		}	
	}
	
	private SnomedRefSetImporterFactory() {
		// Suppress instantiation
	}
}