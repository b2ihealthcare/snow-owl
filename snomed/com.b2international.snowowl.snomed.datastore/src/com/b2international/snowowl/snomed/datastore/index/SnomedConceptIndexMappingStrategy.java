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
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_NAMESPACE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PRIMITIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.api.index.IIndexMappingStrategy;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.update.IconIdUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ParentageUpdater;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;

/**
 * Mapping strategy to transform a SNOMED CT concept into a {@link Document document} of the index.
 * 
 * @see IIndexMappingStrategy
 * @see SnomedConceptNameProvider
 */
public abstract class SnomedConceptIndexMappingStrategy extends AbstractIndexMappingStrategy {
	
	public enum DescriptionType {
		FULLY_SPECIFIED_NAME(SnomedIndexBrowserConstants.CONCEPT_FULLY_SPECIFIED_NAME),
		SYNONYM(SnomedIndexBrowserConstants.CONCEPT_SYNONYM),
		OTHER(SnomedIndexBrowserConstants.CONCEPT_OTHER_DESCRIPTION);
		
		private final String fieldName;
		
		private DescriptionType(final String fieldName) {
			this.fieldName = fieldName;
		}
	}
	
	public static final class DescriptionInfo {
		
		private final DescriptionType type;
		private final String term;
		
		public DescriptionInfo(final DescriptionType type, final String term) {
			this.type = type;
			this.term = term;
		}
	}
	
	private final String conceptId;
	private final long storageKey;
	
	private final boolean exhaustive;
	private final boolean active;
	private final boolean primitive;
	private final boolean released;
	private final String moduleId;
	private final String label;
	private final Set<DescriptionInfo> activeDescriptionInfos;
	private final float degreeOfInterest;
	private final Collection<String> predicateKeys;
	private final Collection<String> referringRefSetIds;
	private final Collection<String> mappingRefSetIds;
	private final Date effectiveTime;
	private final boolean indexAsRelevantForCompare;
	private ISnomedTaxonomyBuilder inferredTaxonomyBuilder;
	private ISnomedTaxonomyBuilder statedTaxonomyBuilder;
	
	protected SnomedConceptIndexMappingStrategy(final ISnomedTaxonomyBuilder inferredTaxonomyBuilder, ISnomedTaxonomyBuilder statedTaxonomyBuilder, final String conceptId, 
			final long storageKey, 
			final boolean exhaustive, 
			final boolean active, 
			final boolean primitive, 
			final boolean released, 
			final String moduleId, 
			final String label,
			final Set<DescriptionInfo> activeDescriptionInfos,
			final float degreeOfInterest,
			final Collection<String> predicateKeys, 
			final Collection<String> referringRefSetIds,
			final Collection<String> mappingRefSetIds,
			final Date effectiveTime,
			final boolean indexAsRelevantForCompare) {

		this.inferredTaxonomyBuilder = inferredTaxonomyBuilder;
		this.statedTaxonomyBuilder = statedTaxonomyBuilder;
		this.conceptId = conceptId;
		this.storageKey = storageKey;
		this.exhaustive = exhaustive;
		this.active = active;
		this.primitive = primitive;
		this.released = released;
		this.moduleId = moduleId;
		this.label = label;
		this.activeDescriptionInfos = activeDescriptionInfos;
		this.degreeOfInterest = degreeOfInterest;
		this.predicateKeys = predicateKeys;
		this.referringRefSetIds = referringRefSetIds;
		this.mappingRefSetIds = mappingRefSetIds;
		this.effectiveTime = effectiveTime;
		this.indexAsRelevantForCompare = indexAsRelevantForCompare;
	}

	@Override
	public Document createDocument() {
		final ISnomedComponentService componentService = ApplicationContext.getInstance().getService(ISnomedComponentService.class);
		//XXX intentionally works on MAIN
		final long namespaceId = componentService.getExtensionConceptId(BranchPathUtils.createMainPath(), conceptId);
		
		final SnomedDocumentBuilder doc = SnomedMappings.doc()
				.id(conceptId)
				.storageKey(storageKey)
				.type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.active(active)
				.labelWithSortKey(label)
				.module(moduleId)
				.field(CONCEPT_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(effectiveTime))
				.field(CONCEPT_EXHAUSTIVE, exhaustive ? 1 : 0)
				.field(CONCEPT_PRIMITIVE, primitive ? 1 : 0)
				.field(COMPONENT_RELEASED, released ? 1 : 0)
				.docValuesField(CONCEPT_DEGREE_OF_INTEREST, degreeOfInterest)
				.docValuesField(CommonIndexConstants.COMPONENT_COMPARE_UNIQUE_KEY, indexAsRelevantForCompare ? storageKey : CDOUtils.NO_STORAGE_KEY)
				.searchOnlyField(CONCEPT_NAMESPACE_ID, namespaceId);
		
		if (!indexAsRelevantForCompare) {
			doc.docValuesField(CommonIndexConstants.COMPONENT_IGNORE_COMPARE_UNIQUE_KEY, storageKey);
		}
		
		//this point we have to find the first parent concept that is in the previous state of the taxonomy to specify the icon ID
		new IconIdUpdater(inferredTaxonomyBuilder, conceptId, active, SnomedIconProvider.getInstance().getAvailableIconIds()).update(doc);
		
		addDescriptionFields(doc);

		// update inferred and stated parentage
		new ParentageUpdater(inferredTaxonomyBuilder, conceptId).update(doc);
		new ParentageUpdater(statedTaxonomyBuilder, conceptId, Concepts.STATED_RELATIONSHIP).update(doc);
		
		addPredicateFields(doc, predicateKeys);

		addReferringRefSetFields(doc, referringRefSetIds);
		
		for (String refSetId : mappingRefSetIds) {
			doc.field(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID, Long.valueOf(refSetId));
		}
		
		return doc.build();
	}

	private void addReferringRefSetFields(SnomedDocumentBuilder doc, Collection<String> referringRefSetIds) {
		for (String refSetId : referringRefSetIds) {
			doc.field(CONCEPT_REFERRING_REFERENCE_SET_ID, Long.valueOf(refSetId));
		}
	}

	private void addPredicateFields(final SnomedDocumentBuilder doc, final Collection<String> predicateKeys) {
		for (final String predicateKey : predicateKeys) {
			doc.field(COMPONENT_REFERRING_PREDICATE, predicateKey);
		}
	}

	private void addDescriptionFields(final SnomedDocumentBuilder doc) {
		for (final DescriptionInfo descriptionInfo : activeDescriptionInfos) {
			doc.field(descriptionInfo.type.fieldName, descriptionInfo.term);
		}
	}

	@Override
	protected long getStorageKey() {
		return storageKey;
	}
}
