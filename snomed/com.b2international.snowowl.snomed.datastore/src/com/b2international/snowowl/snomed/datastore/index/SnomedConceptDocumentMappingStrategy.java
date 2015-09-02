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
package com.b2international.snowowl.snomed.datastore.index;

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_DEGREE_OF_INTEREST;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_EXHAUSTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_OTHER_DESCRIPTION;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PRIMITIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_SYNONYM;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Date;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class SnomedConceptDocumentMappingStrategy extends SnomedConceptIndexMappingStrategy {

	public SnomedConceptDocumentMappingStrategy(ISnomedTaxonomyBuilder inferredTaxonomyBuilder, ISnomedTaxonomyBuilder statedTaxonomyBuilder, final Document doc, final boolean indexAsRelevantForCompare) {
		super(inferredTaxonomyBuilder, statedTaxonomyBuilder, 
				SnomedMappings.id().getValueAsString(doc), 
				Mappings.storageKey().getValue(doc), 
				isExhaustive(doc), 
				isActive(doc), 
				isPrimitive(doc), 
				isReleased(doc), 
				getModuleId(doc), 
				getLabel(doc), 
				getActiveDescriptionInfos(doc),
				getDegreeOfInterest(doc),
				getPredicateKeys(doc), 
				getReferringRefSetIds(doc),
				getMappingRefSetIds(doc),
				getEffectiveTime(doc),
				indexAsRelevantForCompare);
	}

	private static Set<String> getReferringRefSetIds(Document conceptDocument) {
		IndexableField[] indexableFields = conceptDocument.getFields(CONCEPT_REFERRING_REFERENCE_SET_ID);
		Builder<String> builder = ImmutableSet.builder();
		for (IndexableField indexableField : indexableFields) {
			builder.add(indexableField.stringValue());
		}
		return builder.build();
	}
	
	private static Set<String> getMappingRefSetIds(Document conceptDocument) {
		IndexableField[] indexableFields = conceptDocument.getFields(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID);
		Builder<String> builder = ImmutableSet.builder();
		for (IndexableField indexableField : indexableFields) {
			builder.add(indexableField.stringValue());
		}
		return builder.build();
	}

	private static Set<String> getPredicateKeys(Document conceptDocument) {
		return ImmutableSet.copyOf(conceptDocument.getValues(COMPONENT_REFERRING_PREDICATE));
	}

	private static float getDegreeOfInterest(Document conceptDocument) {
		return IndexUtils.getFloatValue(conceptDocument.getField(CONCEPT_DEGREE_OF_INTEREST));
	}

	private static Set<DescriptionInfo> getActiveDescriptionInfos(final Document conceptDocument) {
		final Set<DescriptionInfo> result = newHashSet();
		addDescriptionInfos(conceptDocument, result, CONCEPT_FULLY_SPECIFIED_NAME, DescriptionType.FULLY_SPECIFIED_NAME);
		addDescriptionInfos(conceptDocument, result, CONCEPT_SYNONYM, DescriptionType.SYNONYM);
		addDescriptionInfos(conceptDocument, result, CONCEPT_OTHER_DESCRIPTION, DescriptionType.OTHER);
		return result;
	}

	private static void addDescriptionInfos(final Document conceptDocument, final Set<DescriptionInfo> result, final String fieldName, final DescriptionType descriptionType) {
		for (final String term : conceptDocument.getValues(fieldName)) {
			result.add(new DescriptionInfo(descriptionType, term));
		}
	}

	private static String getLabel(final Document doc) {
		return Mappings.label().getValue(doc);
	}

	private static String getModuleId(final Document doc) {
		return SnomedMappings.module().getValueAsString(doc);
	}

	private static boolean isReleased(final Document doc) {
		return IndexUtils.getBooleanValue(doc.getField(COMPONENT_RELEASED));
	}

	private static boolean isPrimitive(final Document doc) {
		return IndexUtils.getBooleanValue(doc.getField(CONCEPT_PRIMITIVE));
	}

	private static boolean isActive(final Document doc) {
		return SnomedMappings.active().getValue(doc) == 1;
	}

	private static boolean isExhaustive(final Document doc) {
		return IndexUtils.getBooleanValue(doc.getField(CONCEPT_EXHAUSTIVE));
	}

	private static Date getEffectiveTime(final Document doc) {
		return new Date(Mappings.longField(CONCEPT_EFFECTIVE_TIME).getValue(doc));
	}
}