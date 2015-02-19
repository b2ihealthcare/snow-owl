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
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CORRELATION_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_ADVICE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_PRIORITY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_RULE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_CATEGORY_ID;

import java.io.Serializable;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.core.api.IBranchPath;


/**
 * Lucene specific query adapter for SNOMED CT complex map type reference set members.
 */
public class SnomedComplexMapRefSetMemberIndexQueryAdapter extends SnomedRefSetMemberIndexQueryAdapter implements Serializable {

	private static final long serialVersionUID = 5964635969349231764L;

	/**
	 * Creates a new instance of the query adapter based on the specified reference set concept identifier and a query term.
	 * @param refSetId the SNOMED CT identifier concept ID of the reference set.
	 * @param searchString the query term.
	 */
	public SnomedComplexMapRefSetMemberIndexQueryAdapter(final String refSetId, final String searchString) {
		super(refSetId, searchString, true);
	}
	
	/**
	 * Creates a new instance of the query adapter based on the specified reference set concept identifier, a query term
	 * where the status of the returning members can be specified. 
	 * @param refSetId the SNOMED CT identifier concept ID of the reference set.
	 * @param searchString the query term.
	 * @param excludeInactive indicates whether the inactive members should be excluded or not.
	 */
	public SnomedComplexMapRefSetMemberIndexQueryAdapter(final String refSetId, final String searchString, final boolean excludeInactive) {
		super(refSetId, searchString, excludeInactive);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter#buildSearchResultDTO(org.apache.lucene.document.Document, float)
	 */
	@Override
	public SnomedComplexMapRefSetMemberIndexEntry buildSearchResult(Document doc, final IBranchPath branchPath, float score) {
		SnomedComplexMapRefSetMemberIndexEntry member = 
				new SnomedComplexMapRefSetMemberIndexEntry(super.buildSearchResult(doc, branchPath, score));
		member.setMapPriority(Integer.valueOf(doc.get(REFERENCE_SET_MEMBER_MAP_PRIORITY)));
		member.setMapGroup(Integer.valueOf(doc.get(REFERENCE_SET_MEMBER_MAP_GROUP)));
		member.setCorrelationId(doc.get(REFERENCE_SET_MEMBER_CORRELATION_ID));
		member.setMapAdvice(doc.get(REFERENCE_SET_MEMBER_MAP_ADVICE));
		member.setMapRule(doc.get(REFERENCE_SET_MEMBER_MAP_RULE));
		member.setMapCategoryId(doc.get(REFERENCE_SET_MEMBER_MAP_CATEGORY_ID));
		return member;
	}
	
}