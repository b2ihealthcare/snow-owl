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
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_COMPARE_UNIQUE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ICON_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_IGNORE_COMPARE_UNIQUE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_ANCESTOR;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_DEGREE_OF_INTEREST;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_EXHAUSTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_NAMESPACE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PARENT;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PRIMITIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.ROOT_ID;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;

import bak.pcj.LongIterator;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.index.IIndexMappingStrategy;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.SortKeyMode;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;

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
	private final LongSet ancestorIds;
	private final LongSet parentIds;
	private final boolean exhaustive;
	private final boolean active;
	private final boolean primitive;
	private final boolean released;
	private final String moduleId;
	private final String label;
	private final Set<DescriptionInfo> activeDescriptionInfos;
	private final float degreeOfInterest;
	private final Collection<String> predicateKeys;
	private final String iconId;
	private final Collection<String> referringRefSetIds;
	private final Collection<String> mappingRefSetIds;
	private final Date effectiveTime;
	private final boolean indexAsRelevantForCompare;
	
	protected SnomedConceptIndexMappingStrategy(final String conceptId, 
			final long storageKey, 
			final LongSet ancestorIds, 
			final LongSet parentIds,
			final boolean exhaustive, 
			final boolean active, 
			final boolean primitive, 
			final boolean released, 
			final String moduleId, 
			final String label,
			final Set<DescriptionInfo> activeDescriptionInfos,
			final float degreeOfInterest,
			final Collection<String> predicateKeys, 
			final String iconId,
			final Collection<String> referringRefSetIds,
			final Collection<String> mappingRefSetIds,
			final Date effectiveTime,
			final boolean indexAsRelevantForCompare) {

		this.conceptId = conceptId;
		this.storageKey = storageKey;
		this.ancestorIds = ancestorIds;
		this.parentIds = parentIds;
		this.exhaustive = exhaustive;
		this.active = active;
		this.primitive = primitive;
		this.released = released;
		this.moduleId = moduleId;
		this.label = label;
		this.activeDescriptionInfos = activeDescriptionInfos;
		this.degreeOfInterest = degreeOfInterest;
		this.predicateKeys = predicateKeys;
		this.iconId = iconId;
		this.referringRefSetIds = referringRefSetIds;
		this.mappingRefSetIds = mappingRefSetIds;
		this.effectiveTime = effectiveTime;
		this.indexAsRelevantForCompare = indexAsRelevantForCompare;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy#createDocument()
	 */
	@Override
	public Document createDocument() {
		
		final Document doc = new Document();
		
		doc.add(new LongField(COMPONENT_ID, Long.valueOf(conceptId), Store.YES));
		doc.add(new LongField(COMPONENT_STORAGE_KEY, storageKey, Store.YES));
		doc.add(new IntField(COMPONENT_TYPE, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Store.YES));
		doc.add(new IntField(CONCEPT_EXHAUSTIVE, exhaustive ? 1 : 0, Store.YES));
		doc.add(new IntField(COMPONENT_ACTIVE, active ? 1 : 0, Store.YES));
		doc.add(new IntField(CONCEPT_PRIMITIVE, primitive ? 1 : 0, Store.YES));
		doc.add(new StoredField(COMPONENT_RELEASED, released ? 1 : 0));
		doc.add(new TextField(COMPONENT_LABEL, label, Store.YES));
		doc.add(new BinaryDocValuesField(COMPONENT_LABEL, new BytesRef(label)));
		SortKeyMode.SORT_ONLY.add(doc, label);
		doc.add(new StoredField(CONCEPT_DEGREE_OF_INTEREST, degreeOfInterest));
		doc.add(new FloatDocValuesField(CONCEPT_DEGREE_OF_INTEREST, degreeOfInterest));
		doc.add(new LongField(CONCEPT_MODULE_ID, Long.valueOf(moduleId), Store.YES));
		doc.add(new LongField(COMPONENT_ICON_ID, Long.valueOf(iconId), Store.YES));
		doc.add(new NumericDocValuesField(COMPONENT_STORAGE_KEY, storageKey));
		doc.add(new NumericDocValuesField(COMPONENT_ID, Long.valueOf(conceptId)));
		doc.add(new NumericDocValuesField(COMPONENT_COMPARE_UNIQUE_KEY, indexAsRelevantForCompare ? storageKey : CDOUtils.NO_STORAGE_KEY));
		doc.add(new NumericDocValuesField(COMPONENT_ICON_ID, Long.valueOf(iconId)));
		if (!indexAsRelevantForCompare) {
			doc.add(new NumericDocValuesField(COMPONENT_IGNORE_COMPARE_UNIQUE_KEY, storageKey));
		}
		doc.add(new LongField(CONCEPT_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(effectiveTime), Store.YES));
		
		final ISnomedComponentService componentService = ApplicationContext.getInstance().getService(ISnomedComponentService.class);
		//XXX intentionally works on MAIN
		final long namespaceId = componentService.getExtensionConceptId(BranchPathUtils.createMainPath(), conceptId);
		
		doc.add(new LongField(CONCEPT_NAMESPACE_ID, namespaceId, Store.NO));
		
		addDescriptionFields(doc);

		addHierarchicalFields(doc, parentIds, CONCEPT_PARENT);
		addHierarchicalFields(doc, ancestorIds, CONCEPT_ANCESTOR);
		
		addPredicateFields(doc, predicateKeys);

		addReferringRefSetFields(doc, referringRefSetIds);
		
		for (String refSetId : mappingRefSetIds) {
			doc.add(new LongField(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID, Long.valueOf(refSetId), Store.YES));
		}
		
		
		return doc;
	}

	private void addReferringRefSetFields(Document doc, Collection<String> referringRefSetIds) {
		for (String refSetId : referringRefSetIds) {
			doc.add(new LongField(CONCEPT_REFERRING_REFERENCE_SET_ID, Long.valueOf(refSetId), Store.YES));
		}
	}

	private void addPredicateFields(final Document doc, final Collection<String> predicateKeys) {
		for (final String predicateKey : predicateKeys) {
			doc.add(new StringField(COMPONENT_REFERRING_PREDICATE, predicateKey, Store.YES));
		}
	}

	private void addDescriptionFields(final Document doc) {
		
		for (final DescriptionInfo descriptionInfo : activeDescriptionInfos) {
			doc.add(new TextField(descriptionInfo.type.fieldName, descriptionInfo.term, Store.YES));
		}
	}

	private void addHierarchicalFields(final Document doc, final LongSet idSet, final String fieldName) {

		final LongIterator idIterator = idSet.iterator();
		
		if (!idIterator.hasNext()) {
			//happens when processing new concept. this time we ignore parentage changes. associated source IS_A relationship processing will trigger taxonomic updates.
			doc.add(new LongField(fieldName, ROOT_ID, Store.YES));
		} else {
			while (idIterator.hasNext()) {
				final long id = idIterator.next();
				doc.add(new LongField(fieldName, id, Store.YES));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy#getStorageKey()
	 */
	@Override
	protected long getStorageKey() {
		return storageKey;
	}
}
