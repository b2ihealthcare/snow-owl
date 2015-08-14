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

import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.BytesRef;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.QueryDslIndexQueryAdapter;

/**
 * Abstract index query adapter for retrieving SNOMED&nbsp;CT component from the index.
 * <p>This class has an overridden {@link #createFilter()} method that convert component IDs given as
 * strings to the corresponding {@link BytesRef}s. Also wraps the {@link TermsFilter} instance into a {@link CachingWrapperFilter}
 * which can ensure better performance as it will cache filter results which is useful if one have a set of filters/terms that are used quite often.
 *
 */
public abstract class SnomedDslIndexQueryAdapter<E extends IIndexEntry> extends QueryDslIndexQueryAdapter<E> implements Serializable {

	private static final long serialVersionUID = 4488830512844764269L;

	protected SnomedDslIndexQueryAdapter(final @Nullable String searchString, final int searchFlags, final @Nullable String[] componentIds) {
		super(searchString, searchFlags, componentIds);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.index.QueryDslIndexQueryAdapter#createFilter()
	 */
	@Override
	public Filter createFilter() {
		if (CompareUtils.isEmpty(componentIds)) {
			return null;
		}
		
		final BytesRef[] bytesRefs = new BytesRef[componentIds.length];
		
		for (int i = 0; i < componentIds.length; i++) {
			bytesRefs[i] = IndexUtils.longToPrefixCoded(componentIds[i]);
		}
		
		return new CachingWrapperFilter(new TermsFilter(CommonIndexConstants.COMPONENT_ID, bytesRefs));
	}
	
}