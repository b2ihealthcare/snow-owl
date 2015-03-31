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

import org.apache.lucene.search.Filter;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;

/**
 * Query adapter for querying descriptions based on their label sort key field.
 */
public class SnomedDescriptionSortKeyQueryAdapter extends SnomedDescriptionIndexQueryAdapter implements Serializable {

	private static final long serialVersionUID = 2478821101399126060L;
	
	public static final int SEARCH_DESCRIPTION_ACTIVE_ONLY = 1 << 0;

	private final String descriptionTypeId;
	private final Filter filter;
	
	public SnomedDescriptionSortKeyQueryAdapter(String searchString, int searchFlags) {
		this(searchString, searchFlags, null);
	}
	
	public SnomedDescriptionSortKeyQueryAdapter(String searchString, int searchFlags, @Nullable String descriptionTypeId) {
		this(searchString, searchFlags, descriptionTypeId, null);
	}
	
	public SnomedDescriptionSortKeyQueryAdapter(String searchString, int searchFlags, @Nullable String descriptionTypeId, @Nullable Filter filter) {
		super(searchString, checkFlags(searchFlags, SEARCH_DESCRIPTION_ACTIVE_ONLY), null); // TODO: component id filtering
		this.descriptionTypeId = descriptionTypeId;
		this.filter = filter;
	}
	
	@Override
	public @Nullable Filter createFilter() {
		return filter;
	}
	
	@Override
	protected IndexQueryBuilder createIndexQueryBuilder() {
		return super.createIndexQueryBuilder()
			.requireExactTermIf(anyFlagSet(SEARCH_DESCRIPTION_ACTIVE_ONLY), SnomedIndexBrowserConstants.COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))
			.requireExactTermIf(!StringUtils.isEmpty(descriptionTypeId), SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID, IndexUtils.longToPrefixCoded(descriptionTypeId))
			.finishIf(StringUtils.isEmpty(searchString))
			.requireExactTerm(CommonIndexConstants.COMPONENT_LABEL_SORT_KEY, IndexUtils.getSortKey(searchString));
	}	
}
