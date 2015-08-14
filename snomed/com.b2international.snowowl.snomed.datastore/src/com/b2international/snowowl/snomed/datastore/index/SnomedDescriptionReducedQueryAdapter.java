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

import javax.annotation.Nullable;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.google.common.base.Optional;

public class SnomedDescriptionReducedQueryAdapter extends SnomedDescriptionIndexQueryAdapter implements Serializable {

	private static final long serialVersionUID = -1041630591287866356L;
	
	public static final int SEARCH_DESCRIPTION_ID = 1 << 0;
	public static final int SEARCH_DESCRIPTION_TERM = 1 << 1;
	public static final int SEARCH_DESCRIPTION_ACTIVE_ONLY = 1 << 2;
	public static final int SEARCH_DESCRIPTION_CONCEPT_ID = 1 << 3;
	public static final int SEARCH_STORAGE_KEY = 1 << 4;

	private final String descriptionTypeId;
	
	public SnomedDescriptionReducedQueryAdapter(String searchString, int searchFlags) {
		this(searchString, searchFlags, null);
	}
	
	public SnomedDescriptionReducedQueryAdapter(String searchString, int searchFlags, @Nullable String descriptionTypeId) {
		this(searchString, searchFlags, descriptionTypeId, null);
	}
	
	public SnomedDescriptionReducedQueryAdapter(String searchString, int searchFlags, @Nullable String descriptionTypeId, @Nullable String[] componentIds) {
		super(searchString, checkFlags(searchFlags, SEARCH_DESCRIPTION_ID, SEARCH_DESCRIPTION_TERM, SEARCH_DESCRIPTION_ACTIVE_ONLY, SEARCH_DESCRIPTION_CONCEPT_ID), componentIds);
		this.descriptionTypeId = descriptionTypeId;
	}
	
	@Override
	protected IndexQueryBuilder createIndexQueryBuilder() {

		if (StringUtils.isEmpty(searchString)) {
			
			return super.createIndexQueryBuilder()
		        .requireExactTermIf(anyFlagSet(SEARCH_DESCRIPTION_ACTIVE_ONLY), SnomedIndexBrowserConstants.COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))
		        .requireExactTermIf(!StringUtils.isEmpty(descriptionTypeId), SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID, IndexUtils.longToPrefixCoded(descriptionTypeId))
		        .requireExistingTermIf(StringUtils.isEmpty(searchString), CommonIndexConstants.COMPONENT_ID);
			
		} else {
			if (anyFlagSet(SEARCH_DESCRIPTION_ID | SEARCH_DESCRIPTION_CONCEPT_ID)) {
				Optional<Long> parsedSearchStringOptional = IndexUtils.parseLong(searchString);
				if (parsedSearchStringOptional.isPresent()) {
					return createIndexQueryBuilderWithIdTerms(parsedSearchStringOptional);
				} else if (anyFlagSet(SEARCH_DESCRIPTION_TERM)) {
					return createIndexQueryBuilderWithoutIdTerms();
				} else {
					// XXX: Search string could not be parsed into a long, so we query for an invalid ID instead. See SnomedRefSetIndexQueryAdapter.
					return new IndexQueryBuilder().requireExactTerm(CommonIndexConstants.COMPONENT_ID, IndexUtils.longToPrefixCoded(-1L));
				}
			} else {
				return createIndexQueryBuilderWithoutIdTerms();
			}
		}
	}

	private IndexQueryBuilder createIndexQueryBuilderWithIdTerms(Optional<Long> parsedSearchStringOptional) {
		return super.createIndexQueryBuilder()
				.requireExactTermIf(anyFlagSet(SEARCH_DESCRIPTION_ACTIVE_ONLY), SnomedIndexBrowserConstants.COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))
				.requireExactTermIf(!StringUtils.isEmpty(descriptionTypeId), SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID, IndexUtils.longToPrefixCoded(descriptionTypeId))
				.requireExistingTermIf(StringUtils.isEmpty(searchString), CommonIndexConstants.COMPONENT_ID)
				.finishIf(StringUtils.isEmpty(searchString))
				.require(new IndexQueryBuilder()
				.matchExactTermIf(anyFlagSet(SEARCH_DESCRIPTION_ID), CommonIndexConstants.COMPONENT_ID, IndexUtils.longToPrefixCoded(parsedSearchStringOptional.get()))
				.matchParsedTermIf(anyFlagSet(SEARCH_DESCRIPTION_TERM), CommonIndexConstants.COMPONENT_LABEL, searchString)
				.matchExactTermIf(anyFlagSet(SEARCH_DESCRIPTION_CONCEPT_ID), SnomedIndexBrowserConstants.DESCRIPTION_CONCEPT_ID, IndexUtils.longToPrefixCoded(parsedSearchStringOptional.get())));
	}

	private IndexQueryBuilder createIndexQueryBuilderWithoutIdTerms() {
		return super.createIndexQueryBuilder()
				.requireExactTermIf(anyFlagSet(SEARCH_DESCRIPTION_ACTIVE_ONLY), SnomedIndexBrowserConstants.COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))
				.requireExactTermIf(!StringUtils.isEmpty(descriptionTypeId), SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID, IndexUtils.longToPrefixCoded(descriptionTypeId))
				.requireExistingTermIf(StringUtils.isEmpty(searchString), CommonIndexConstants.COMPONENT_ID)
				.finishIf(StringUtils.isEmpty(searchString))
				.require(new IndexQueryBuilder()
				.matchParsedTerm(CommonIndexConstants.COMPONENT_LABEL, searchString));
	}	
}