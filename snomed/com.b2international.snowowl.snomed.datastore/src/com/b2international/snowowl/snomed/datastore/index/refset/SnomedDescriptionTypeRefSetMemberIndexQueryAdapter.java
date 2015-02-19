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

import java.io.Serializable;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;

/**
 * Lucene specific query adapter for SNOMED CT description type reference set members.
 * 
 */
public class SnomedDescriptionTypeRefSetMemberIndexQueryAdapter extends SnomedRefSetMemberIndexQueryAdapter implements Serializable {

	private static final long serialVersionUID = -206157154220099963L;

	/**
	 * Creates a new instance of the query adapter based on the specified reference set concept identifier and a query term.
	 * @param refSetId the SNOMED CT identifier concept ID of the reference set.
	 * @param searchString the query term.
	 */
	public SnomedDescriptionTypeRefSetMemberIndexQueryAdapter(final String refSetId, final String searchString) {
		super(refSetId, searchString, true);
	}
	
	/**
	 * Creates a new instance of the query adapter based on the specified reference set concept identifier, a query term
	 * where the status of the returning members can be specified. 
	 * @param refSetId the SNOMED CT identifier concept ID of the reference set.
	 * @param searchString the query term.
	 * @param excludeInactive indicates whether the inactive members should be excluded or not.
	 */
	public SnomedDescriptionTypeRefSetMemberIndexQueryAdapter(final String refSetId, final String searchString, final boolean excludeInactive) {
		super(refSetId, searchString, excludeInactive);
	}
	
	@Override
	public SnomedDescriptionTypeRefSetMemberIndexEntry buildSearchResult(Document doc, final IBranchPath branchPath, float score) {
		SnomedDescriptionTypeRefSetMemberIndexEntry member = new SnomedDescriptionTypeRefSetMemberIndexEntry(super.buildSearchResult(doc, branchPath, score));
		member.setDescriptionLength(Integer.valueOf(doc.get(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH)));
		
		return member;
	}

}