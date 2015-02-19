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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.google.common.base.Optional;

/**
 * Using this query adapter only reduced Lucene query capability is available.
 * This uses prefix query consequently wildcard and fuzzy matching is <b>not</b> available.
 * 
 * It uses <i>AND</i> operator within the same terms and <i>OR</i> operator between different terms.
 * 
 * <b>Note</b> it uses an additional query clause to filter out all the inactive concepts from the result.
 * 
 */
public class SnomedConceptReducedQueryAdapter extends SnomedConceptIndexQueryAdapter {
	
	private static final long serialVersionUID = 2760532104678762360L;

	public static SnomedConceptIndexQueryAdapter findByStorageKey(final long storageKey) {
		return new SnomedConceptReducedQueryAdapter(String.valueOf(storageKey), SEARCH_STORAGE_KEY) {
			private static final long serialVersionUID = 8790511308540662874L;
			@Override protected IndexQueryBuilder createIndexQueryBuilder() {
				IndexQueryBuilder builder = super.createIndexQueryBuilder();
				return builder
					.requireExactTermIf(anyFlagSet(SEARCH_STORAGE_KEY), COMPONENT_STORAGE_KEY, IndexUtils.longToPrefixCoded(Long.valueOf(searchString)));
			}
		};
	} 
	
	public static SnomedConceptIndexQueryAdapter findConceptId(final String conceptId) {
		return new SnomedConceptReducedQueryAdapter(conceptId, SEARCH_BY_CONCEPT_ID) {
			private static final long serialVersionUID = 2045863595340360998L;
			@Override protected IndexQueryBuilder createIndexQueryBuilder() {
				IndexQueryBuilder builder = super.createIndexQueryBuilder();
				return builder
					.requireExactTermIf(anyFlagSet(SEARCH_BY_CONCEPT_ID), SnomedIndexBrowserConstants.COMPONENT_ID, IndexUtils.longToPrefixCoded(searchString));
			}
		};
	} 
	
	public SnomedConceptReducedQueryAdapter(final String searchString, final int searchFlags) {
		this(searchString, searchFlags, null);
	}
	
	public SnomedConceptReducedQueryAdapter(final String searchString, final int searchFlags, final String[] componentIds) {
		super(searchString, searchFlags, componentIds);
	}

	@Override
	protected IndexQueryBuilder createIndexQueryBuilder() {
		IndexQueryBuilder builder = super.createIndexQueryBuilder();
		if (anyFlagSet(SEARCH_BY_CONCEPT_ID)) {
			Optional<Long> parsedSearchStringOptional = IndexUtils.parseLong(searchString);
			if (parsedSearchStringOptional.isPresent()) {
				return createIndexQueryBuilderWithIdTerms(builder);
			} else {
				return createIndexQueryBuilderWithoutIdTerms(builder);
			}
		} else {
			return createIndexQueryBuilderWithoutIdTerms(builder);			
		}
		
		
	}

	private IndexQueryBuilder createIndexQueryBuilderWithIdTerms(IndexQueryBuilder builder) {
		return builder
				.requireExistingTermIf(StringUtils.isEmpty(searchString), SnomedIndexBrowserConstants.COMPONENT_ID)
				.finishIf(StringUtils.isEmpty(searchString))
				.require(new IndexQueryBuilder()
				.matchExactTerm(SnomedIndexBrowserConstants.COMPONENT_ID, IndexUtils.longToPrefixCoded(searchString))
				.matchAllTokenizedTermsIf(anyFlagSet(SEARCH_BY_LABEL), SnomedIndexBrowserConstants.COMPONENT_LABEL, searchString.toLowerCase())
				.matchAllTokenizedTermPrefixesIf(anyFlagSet(SEARCH_BY_LABEL), SnomedIndexBrowserConstants.COMPONENT_LABEL, searchString.toLowerCase())
				.matchAllTokenizedTermPrefixesIf(anyFlagSet(SEARCH_BY_FSN), SnomedIndexBrowserConstants.CONCEPT_FULLY_SPECIFIED_NAME, searchString.toLowerCase())
				.matchAllTokenizedTermPrefixesIf(anyFlagSet(SEARCH_BY_SYNONYM), SnomedIndexBrowserConstants.CONCEPT_SYNONYM, searchString.toLowerCase())
				.matchAllTokenizedTermPrefixesIf(anyFlagSet(SEARCH_BY_OTHER), SnomedIndexBrowserConstants.CONCEPT_OTHER_DESCRIPTION, searchString.toLowerCase()));
	}

	private IndexQueryBuilder createIndexQueryBuilderWithoutIdTerms(IndexQueryBuilder builder) {
		return builder
				.requireExistingTermIf(StringUtils.isEmpty(searchString), SnomedIndexBrowserConstants.COMPONENT_ID)
				.finishIf(StringUtils.isEmpty(searchString))
				.require(new IndexQueryBuilder()
				.matchAllTokenizedTermsIf(anyFlagSet(SEARCH_BY_LABEL), SnomedIndexBrowserConstants.COMPONENT_LABEL, searchString.toLowerCase())
				.matchAllTokenizedTermPrefixesIf(anyFlagSet(SEARCH_BY_LABEL), SnomedIndexBrowserConstants.COMPONENT_LABEL, searchString.toLowerCase())
				.matchAllTokenizedTermPrefixesIf(anyFlagSet(SEARCH_BY_FSN), SnomedIndexBrowserConstants.CONCEPT_FULLY_SPECIFIED_NAME, searchString.toLowerCase())
				.matchAllTokenizedTermPrefixesIf(anyFlagSet(SEARCH_BY_SYNONYM), SnomedIndexBrowserConstants.CONCEPT_SYNONYM, searchString.toLowerCase())
				.matchAllTokenizedTermPrefixesIf(anyFlagSet(SEARCH_BY_OTHER), SnomedIndexBrowserConstants.CONCEPT_OTHER_DESCRIPTION, searchString.toLowerCase()));
	}
}