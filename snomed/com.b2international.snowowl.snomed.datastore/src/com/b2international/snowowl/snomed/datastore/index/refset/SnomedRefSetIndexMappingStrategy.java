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
package com.b2international.snowowl.snomed.datastore.index.refset;

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_COMPARE_UNIQUE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_IGNORE_COMPARE_UNIQUE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_STRUCTURAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_TYPE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Preconditions;

/**
 * Index mapping strategy for SNOMED&nbsp;CT reference sets.
 */
public class SnomedRefSetIndexMappingStrategy extends AbstractIndexMappingStrategy {
	
	private final SnomedRefSet refSet;
	private final long iconId;
	private final Collection<String> predicateKeys;
	private final boolean indexAsRelevantForCompare;
	
	/**
	 * Creates a new index mapping strategy instance based on the  specified SNOMED&nbsp;CT reference set.
	 * @param refSet the reference set.
	 * @param predicateKeys 
	 */
	public SnomedRefSetIndexMappingStrategy(final SnomedRefSet refSet, final long iconId, final Collection<String> predicateKeys, final boolean indexAsRelevantForCompare) {
		this.indexAsRelevantForCompare = indexAsRelevantForCompare;
		this.predicateKeys = Preconditions.checkNotNull(predicateKeys, "Predicate key argument cannot be null.");
		this.refSet = checkNotNull(refSet, "Reference set argument cannot be null.");
		this.iconId = iconId;
	}

	@Override
	public Document createDocument() {
		final Document doc = new Document();
		
		final Long id = Long.valueOf(refSet.getIdentifierId());
		doc.add(new IntField(COMPONENT_TYPE, SnomedTerminologyComponentConstants.REFSET_NUMBER, Store.YES));
		doc.add(new LongField(COMPONENT_ID, id, Store.YES));
		doc.add(new IntField(REFERENCE_SET_TYPE, refSet.getType().getValue(), Store.YES));
		doc.add(new IntField(REFERENCE_SET_REFERENCED_COMPONENT_TYPE, refSet.getReferencedComponentType(), Store.YES));
		final long storageKey = CDOIDUtils.asLong(refSet.cdoID());
		doc.add(new LongField(COMPONENT_STORAGE_KEY, storageKey, Store.YES));
		doc.add(new IntField(REFERENCE_SET_STRUCTURAL, refSet instanceof SnomedStructuralRefSet ? 1 : 0, Store.YES));
		
		final String label = SnomedConceptNameProvider.INSTANCE.getText(refSet.getIdentifierId(), refSet.cdoView());
		doc.add(new TextField(COMPONENT_LABEL, label, Store.YES));
		doc.add(new NumericDocValuesField(COMPONENT_COMPARE_UNIQUE_KEY, indexAsRelevantForCompare ? storageKey : CDOUtils.NO_STORAGE_KEY));
		
		if (!indexAsRelevantForCompare) {
			doc.add(new NumericDocValuesField(COMPONENT_IGNORE_COMPARE_UNIQUE_KEY, storageKey));
		}
		
		final String moduleId = new SnomedConceptLookupService().getComponent(refSet.getIdentifierId(), refSet.cdoView()).getModule().getId();
		doc.add(new LongField(SnomedIndexBrowserConstants.REFERENCE_SET_MODULE_ID, Long.parseLong(moduleId), Store.YES));
		doc.add(new LongField(SnomedIndexBrowserConstants.COMPONENT_ICON_ID, iconId, Store.YES));
		
		for (final String predicateKey : this.predicateKeys) {
			doc.add(new StringField(SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE, predicateKey, Store.YES));
		}
		
		return doc;
	}
	
	@Override
	protected long getStorageKey() {
		return CDOIDUtils.asLong(refSet.cdoID());
	}
}
