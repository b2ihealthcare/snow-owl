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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

public abstract class SnomedRefSetImporterFactory {
	
	private static final Splitter TAB_SPLITTER = Splitter.on("\t");
	
	public static AbstractSnomedRefSetImporter<?, ?> createRefSetImporter(final URL url, final SnomedImportContext importContext, String urlIdentifier) 
			throws IOException {
		
		if (url == null) {
			return null;
		}
		
		String header;
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), Charsets.UTF_8))) {
			
			header = reader.readLine();
			
			if (Strings.isNullOrEmpty(header)) {
				return null;
			}
			
		}
		
		List<String> headerElements = TAB_SPLITTER.splitToList(header);
		String lastColumnName = Iterables.getLast(headerElements);
		
		if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_QUERY)) {
			return new SnomedQueryRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_VALUE_ID)) {
			return new SnomedAttributeValueRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_COMPONENT) || lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID)) {
			return new SnomedAssociationRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET)) {
			return new SnomedSimpleMapTypeRefSetImporter(importContext, url.openStream(), false, urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION)) {
			return new SnomedSimpleMapTypeRefSetImporter(importContext, url.openStream(), true, urlIdentifier);
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
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
			return new SnomedModuleDependencyRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_OWL_EXPRESSION)) {
			return new SnomedOWLAxiomRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE)) {
			return new SnomedMRCMDomainRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equalsIgnoreCase(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID)) {
			return new SnomedMRCMModuleScopeRefSetImporter(importContext, url.openStream(), urlIdentifier);
		} else if (lastColumnName.equals(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID)) {
			if (headerElements.contains(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID)) {
				return new SnomedMRCMAttributeDomainRefSetImporter(importContext, url.openStream(), urlIdentifier);
			} else if (headerElements.contains(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT)) {
				return new SnomedMRCMAttributeRangeRefSetImporter(importContext, url.openStream(), urlIdentifier);
			}
		}
		
		return null;
	}
	
	private SnomedRefSetImporterFactory() {
		// Suppress instantiation
	}
}