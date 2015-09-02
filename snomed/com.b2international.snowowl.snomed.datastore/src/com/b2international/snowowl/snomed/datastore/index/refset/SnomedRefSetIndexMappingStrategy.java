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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_STRUCTURAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_TYPE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.update.IconIdUpdater;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Preconditions;

/**
 * Index mapping strategy for SNOMED&nbsp;CT reference sets.
 */
public class SnomedRefSetIndexMappingStrategy extends AbstractIndexMappingStrategy {
	
	private final SnomedRefSet refSet;
	private final Collection<String> predicateKeys;
	private final boolean indexAsRelevantForCompare;
	private ISnomedTaxonomyBuilder taxonomyBuilder;
	
	/**
	 * Creates a new index mapping strategy instance based on the  specified SNOMED&nbsp;CT reference set.
	 * @param refSet the reference set.
	 * @param predicateKeys 
	 */
	public SnomedRefSetIndexMappingStrategy(final ISnomedTaxonomyBuilder taxonomyBuilder, final SnomedRefSet refSet, final Collection<String> predicateKeys, final boolean indexAsRelevantForCompare) {
		this.taxonomyBuilder = taxonomyBuilder;
		this.indexAsRelevantForCompare = indexAsRelevantForCompare;
		this.predicateKeys = Preconditions.checkNotNull(predicateKeys, "Predicate key argument cannot be null.");
		this.refSet = checkNotNull(refSet, "Reference set argument cannot be null.");
	}

	@Override
	public Document createDocument() {
		final String conceptId = refSet.getIdentifierId();
		final String label = SnomedConceptNameProvider.INSTANCE.getText(conceptId, refSet.cdoView());
		final long storageKey = getStorageKey();
		final SnomedDocumentBuilder doc = SnomedMappings.doc()
				.id(conceptId)
				.type(SnomedTerminologyComponentConstants.REFSET_NUMBER)
				.storageKey(storageKey)
				.field(REFERENCE_SET_TYPE, refSet.getType().getValue())
				.field(REFERENCE_SET_REFERENCED_COMPONENT_TYPE, (int) refSet.getReferencedComponentType())
				.field(REFERENCE_SET_STRUCTURAL, refSet instanceof SnomedStructuralRefSet ? 1 : 0)
				.label(label)
				.docValuesField(CommonIndexConstants.COMPONENT_COMPARE_UNIQUE_KEY, indexAsRelevantForCompare ? storageKey : CDOUtils.NO_STORAGE_KEY);
		
		if (!indexAsRelevantForCompare) {
			doc.docValuesField(CommonIndexConstants.COMPONENT_IGNORE_COMPARE_UNIQUE_KEY, storageKey);
		}
		
		final String moduleId = new SnomedConceptLookupService().getComponent(refSet.getIdentifierId(), refSet.cdoView()).getModule().getId();
		doc.module(moduleId);
		
		final boolean active = true;
		new IconIdUpdater(taxonomyBuilder, conceptId, active, SnomedIconProvider.getInstance().getAvailableIconIds()).update(doc);
		
		for (final String predicateKey : this.predicateKeys) {
			doc.field(SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE, predicateKey);
		}
		
		return doc.build();
	}
	
	@Override
	protected long getStorageKey() {
		return CDOIDUtils.asLong(refSet.cdoID());
	}
}
