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

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.lucene.document.Document;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.google.common.collect.ImmutableSet;

/**
 * Query adapter for SNOMED CT descriptions.
 * 
 */
public abstract class SnomedDescriptionIndexQueryAdapter extends SnomedDslIndexQueryAdapter<SnomedDescriptionIndexEntry> implements Serializable {

	private static final long serialVersionUID = 3190941508040033075L;

	/**
	 * Creates a description query adapter instance where the query term should match with the container SNOMED CT concept's
	 * identifier.
	 * @param conceptId the identifier of the SNOMED CT concept.
	 * @return the description query adapter instance.
	 */
	public static SnomedDescriptionIndexQueryAdapter findByConceptId(final String conceptId) {
		return new SnomedDescriptionReducedQueryAdapter(conceptId, SnomedDescriptionReducedQueryAdapter.SEARCH_DESCRIPTION_CONCEPT_ID);
	}
	
	/**
	 * Creates a description query adapter instance where the query term should match with the container SNOMED CT concept's
	 * identifier. Excludes the inactive descriptions.
	 * @param conceptId the identifier of the SNOMED CT concept.
	 * @return the description query adapter instance.
	 */
	public static SnomedDescriptionIndexQueryAdapter findActivesByConceptId(final String conceptId) {
		return new SnomedDescriptionReducedQueryAdapter(conceptId, 
				SnomedDescriptionReducedQueryAdapter.SEARCH_DESCRIPTION_CONCEPT_ID | 
				SnomedDescriptionReducedQueryAdapter.SEARCH_DESCRIPTION_ACTIVE_ONLY);
	}
	
	public static final SnomedDescriptionIndexQueryAdapter findActiveDescriptionsByType(final String conceptId, final String typeId) {
		return new SnomedDescriptionByTypeIndexQueryAdapter(conceptId, typeId);
	}
	
	public static final SnomedDescriptionIndexQueryAdapter createFindByConceptIds(final String... conceptIds) {
		return createFindByConceptIds(ImmutableSet.copyOf(conceptIds));
	}
	
	public static final SnomedDescriptionIndexQueryAdapter createFindSynonymsByConceptIds(final String... conceptIds) {
		return createFindSynonymsByConceptIds(ImmutableSet.copyOf(conceptIds));
	}

	public static final SnomedDescriptionIndexQueryAdapter createFindSynonymsByConceptIds(final Collection<String> conceptIds) {
		return new SnomedDescriptionContainerQueryAdapter(conceptIds, 
				SnomedDescriptionContainerQueryAdapter.SEARCH_DESCRIPTION_CONTAINER_SYNYONYMS_ONLY);
	}
	
	public static final SnomedDescriptionIndexQueryAdapter createFindFsnByConceptIds(final Collection<String> conceptIds) {
		return new SnomedDescriptionContainerQueryAdapter(conceptIds, 
				SnomedDescriptionContainerQueryAdapter.SEARCH_DESCRIPTION_CONTAINER_FSN_ONLY);
	}
	
	public static final SnomedDescriptionContainerQueryAdapter createFindByConceptIds(final Collection<String> conceptIds) {
		return new SnomedDescriptionContainerQueryAdapter(conceptIds, 
				SnomedDescriptionContainerQueryAdapter.SEARCH_DEFAULT);
	}
	
	protected SnomedDescriptionIndexQueryAdapter(final String searchString, final int searchFlags, final String[] componentIds) {
		super(searchString, searchFlags, componentIds);
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	protected IndexQueryBuilder createIndexQueryBuilder() {
		return super.createIndexQueryBuilder().require(SnomedMappings.newQuery().description().matchAll());
	}
	
	@Override
	public SnomedDescriptionIndexEntry buildSearchResult(final Document doc, final IBranchPath branchPath, final float score) {
		return SnomedDescriptionIndexEntry.builder()
				.id(SnomedMappings.id().getValueAsString(doc)) 
				.term(Mappings.label().getValue(doc)) 
				.moduleId(SnomedMappings.module().getValueAsString(doc)) 
				.score(score)
				.storageKey(Mappings.storageKey().getValue(doc))
				.released(BooleanUtils.valueOf(doc.getField(SnomedIndexBrowserConstants.COMPONENT_RELEASED).numericValue().intValue()))
				.active(BooleanUtils.valueOf(SnomedMappings.active().getValue(doc)))
				.typeId(SnomedMappings.descriptionType().getValueAsString(doc))
				.conceptId(SnomedMappings.descriptionConcept().getValueAsString(doc))
				.caseSignificanceId(doc.getField(SnomedIndexBrowserConstants.DESCRIPTION_CASE_SIGNIFICANCE_ID).stringValue())
				.effectiveTimeLong(SnomedMappings.effectiveTime().getValue(doc))
				.build();
	}
}
