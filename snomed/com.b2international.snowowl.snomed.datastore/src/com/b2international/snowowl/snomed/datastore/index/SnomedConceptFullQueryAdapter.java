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

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Optional;

/**
 * This adater is for searching in the index with full Lucene capabilities.
 * It uses queryparser for parsing user input. Wildcard and fuzzy search is available.
 * 
 * It uses <i>AND</i> operator within the same terms and <i>OR</i> operator between different terms.
 * @deprecated - UNSUPPORTED, use {@link SnomedRequests#prepareSearchConcept()} and friends instead
 */
public class SnomedConceptFullQueryAdapter extends SnomedConceptIndexQueryAdapter {
	
	private static final long serialVersionUID = 7868151322225338848L;

	public SnomedConceptFullQueryAdapter(final String searchString, final int searchFlags) {
		this(searchString, searchFlags, null);
	}
	
	public SnomedConceptFullQueryAdapter(final String searchString, final int searchFlags, final String[] componentIds) {
		super(searchString, searchFlags, componentIds);
	}

	@Override
	protected IndexQueryBuilder createIndexQueryBuilder() {
		IndexQueryBuilder builder = super.createIndexQueryBuilder();
		if (anyFlagSet(SEARCH_BY_CONCEPT_ID)) {
			Optional<Long> parsedSearchStringOptional = IndexUtils.parseLong(searchString);
			if (parsedSearchStringOptional.isPresent()) {
				return createIndexQueryBuilderWithIdTerms(builder, parsedSearchStringOptional.get());
			} else if (anyFlagSet(SEARCH_BY_LABEL | SEARCH_BY_FSN | SEARCH_BY_SYNONYM | SEARCH_BY_OTHER)) {
				return createIndexQueryBuilderWithoutIdTerms(builder);
			} else {
				// XXX: Search string could not be parsed into a long, so we query for an invalid ID instead. See SnomedRefSetIndexQueryAdapter.
				return new IndexQueryBuilder().require(SnomedMappings.newQuery().id(-1L).matchAll());
			}
		} else {
			return createIndexQueryBuilderWithoutIdTerms(builder);
		}
	}

	private IndexQueryBuilder createIndexQueryBuilderWithIdTerms(IndexQueryBuilder builder, Long id) {
		return builder
				.finishIf(StringUtils.isEmpty(searchString))
				.require(new IndexQueryBuilder()
				.matchIf(anyFlagSet(SEARCH_BY_CONCEPT_ID), SnomedMappings.newQuery().id(id).matchAll()))
//				.matchParsedTermIf(anyFlagSet(SEARCH_BY_LABEL), Mappings.label().fieldName(), searchString)
//				.matchParsedTermIf(anyFlagSet(SEARCH_BY_FSN), SnomedIndexBrowserConstants.CONCEPT_FULLY_SPECIFIED_NAME, searchString)
//				.matchParsedTermIf(anyFlagSet(SEARCH_BY_SYNONYM), SnomedIndexBrowserConstants.CONCEPT_SYNONYM, searchString)
//				.matchParsedTermIf(anyFlagSet(SEARCH_BY_OTHER), SnomedIndexBrowserConstants.CONCEPT_OTHER_DESCRIPTION, searchString))
				;
	}

	private IndexQueryBuilder createIndexQueryBuilderWithoutIdTerms(IndexQueryBuilder builder) {
		return builder
				.finishIf(StringUtils.isEmpty(searchString))
//				.require(new IndexQueryBuilder()
//				.matchParsedTermIf(anyFlagSet(SEARCH_BY_LABEL), Mappings.label().fieldName(), searchString))
//				.matchParsedTermIf(anyFlagSet(SEARCH_BY_FSN), SnomedIndexBrowserConstants.CONCEPT_FULLY_SPECIFIED_NAME, searchString)
//				.matchParsedTermIf(anyFlagSet(SEARCH_BY_SYNONYM), SnomedIndexBrowserConstants.CONCEPT_SYNONYM, searchString)
//				.matchParsedTermIf(anyFlagSet(SEARCH_BY_OTHER), SnomedIndexBrowserConstants.CONCEPT_OTHER_DESCRIPTION, searchString))
				;
	}
}