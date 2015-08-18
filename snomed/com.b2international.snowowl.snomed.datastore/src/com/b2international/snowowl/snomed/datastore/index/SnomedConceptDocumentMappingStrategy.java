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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_ANCESTOR;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_DEGREE_OF_INTEREST;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_EXHAUSTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_OTHER_DESCRIPTION;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PRIMITIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_SYNONYM;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.ROOT_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Date;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.field.ComponentIdLongField;
import com.b2international.snowowl.datastore.index.field.ComponentStorageKeyField;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 */
public class SnomedConceptDocumentMappingStrategy extends SnomedConceptIndexMappingStrategy {

	public SnomedConceptDocumentMappingStrategy(final Document conceptDocument, final boolean indexAsRelevantForCompare) {
		
		super(ComponentIdLongField.getString(checkNotNull(conceptDocument, "SNOMED CT concept document cannot be null.")), 
				ComponentStorageKeyField.getLong(conceptDocument), 
				getAncestorIds(conceptDocument), 
				getParentIds(conceptDocument), 
				isExhaustive(conceptDocument), 
				isActive(conceptDocument), 
				isPrimitive(conceptDocument), 
				isReleased(conceptDocument), 
				getModuleId(conceptDocument), 
				getLabel(conceptDocument), 
				getActiveDescriptionInfos(conceptDocument),
				getDegreeOfInterest(conceptDocument),
				getPredicateKeys(conceptDocument), 
				getIconId(conceptDocument),
				getReferringRefSetIds(conceptDocument),
				getMappingRefSetIds(conceptDocument),
				getEffectiveTime(conceptDocument),
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

	private static String getIconId(Document conceptDocument) {
		return conceptDocument.get(CommonIndexConstants.COMPONENT_ICON_ID);
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

	private static String getLabel(final Document conceptDocument) {
		return conceptDocument.get(CommonIndexConstants.COMPONENT_LABEL);
	}

	private static String getModuleId(final Document conceptDocument) {
		return conceptDocument.get(COMPONENT_MODULE_ID);
	}

	private static boolean isReleased(final Document conceptDocument) {
		return IndexUtils.getBooleanValue(conceptDocument.getField(COMPONENT_RELEASED));
	}

	private static boolean isPrimitive(final Document conceptDocument) {
		return IndexUtils.getBooleanValue(conceptDocument.getField(CONCEPT_PRIMITIVE));
	}

	private static boolean isActive(final Document conceptDocument) {
		return IndexUtils.getBooleanValue(conceptDocument.getField(COMPONENT_ACTIVE));
	}

	private static boolean isExhaustive(final Document conceptDocument) {
		return IndexUtils.getBooleanValue(conceptDocument.getField(CONCEPT_EXHAUSTIVE));
	}

	private static LongSet getParentIds(final Document conceptDocument) {
		return getLongSet(conceptDocument, CommonIndexConstants.COMPONENT_PARENT);
	}

	private static LongSet getAncestorIds(final Document conceptDocument) {
		return getLongSet(conceptDocument, CONCEPT_ANCESTOR);
	}

	private static LongSet getLongSet(final Document conceptDocument, final String fieldName) {
		
		final IndexableField[] fields = conceptDocument.getFields(fieldName);
		final LongSet longIds = new LongOpenHashSet(fields.length + 1);
		
		for (final IndexableField field : fields) {
			long id = IndexUtils.getLongValue(field);
			if (ROOT_ID != id) {
				longIds.add(id);
			}
		}
		
		return longIds;
	}

	private static Date getEffectiveTime(final Document conceptDocument) {
		return new Date(IndexUtils.getLongValue(conceptDocument.getField(CONCEPT_EFFECTIVE_TIME)));
	}
}