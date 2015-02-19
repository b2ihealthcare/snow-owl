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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID;

import java.io.Serializable;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.index.IndexAdapterBase;
import com.b2international.snowowl.datastore.index.IndexUtils;

public class SnomedRefSetMemberIndexQueryAdapter extends IndexAdapterBase<SnomedRefSetMemberIndexEntry> implements Serializable {

	private static final long serialVersionUID = 3095110912042606627L;

	private String refSetId;
	private boolean excludeInactive;
	
	protected SnomedRefSetMemberIndexQueryAdapter() {
		super("");
	}
		
	public SnomedRefSetMemberIndexQueryAdapter(final String refSetId, final String searchString) {
		this(refSetId, searchString, true);
	}
	
	public SnomedRefSetMemberIndexQueryAdapter(final String refSetId, final String searchString, final boolean excludeInactive) {
		super(searchString);
		this.refSetId = refSetId;
		this.excludeInactive = excludeInactive;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IIndexQueryAdapter#buildSearchResult(org.apache.lucene.document.Document, float)
	 */
	@Override
	public SnomedRefSetMemberIndexEntry buildSearchResult(final Document doc, final IBranchPath branchPath, final float score) {
		return SnomedRefSetMemberIndexEntry.create(doc, branchPath);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.index.IndexAdapterBase#buildQuery()
	 */
	@Override
	protected Query buildQuery() throws ParseException {
		
		final BooleanQuery main = new BooleanQuery();
		
		final BooleanQuery refSetIdQuery = new BooleanQuery();
		refSetIdQuery.add(new BooleanClause(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(refSetId))), Occur.MUST));
		main.add(refSetIdQuery, Occur.MUST);
		
		if (!StringUtils.isEmpty(searchString)) {
			final BooleanQuery fieldQuery = new BooleanQuery();
			addTermPrefixClause(fieldQuery, COMPONENT_LABEL, searchString.toLowerCase());
			//added by endre, see issue #242
			fieldQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, searchString.toLowerCase())), Occur.SHOULD);
			main.add(fieldQuery, Occur.MUST);
		}
		
		if (excludeInactive) {
			final BooleanQuery inactivityQuery = new BooleanQuery();
			inactivityQuery.add(new BooleanClause(new TermQuery(new Term(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))), Occur.MUST));
			main.add(inactivityQuery, Occur.MUST);
		}
		
		return main;
	}
}