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
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * Lucene specific query adapter for SNOMED&nbsp;CT concrete domain reference set members.
 * @see SnomedRefSetMemberIndexQueryAdapter
 */
public class SnomedConcreteDataTypeRefSetMemberIndexQueryAdapter extends SnomedRefSetMemberIndexQueryAdapter implements Serializable {

	private static final long serialVersionUID = -3075440075649056538L;

	/**
	 * Creates a new instance of the query adapter based on the specified reference set concept identifier and a query term.
	 * @param refSetId the SNOMED CT identifier concept ID of the reference set.
	 * @param searchString the query term.
	 */
	public SnomedConcreteDataTypeRefSetMemberIndexQueryAdapter(final String refSetId, final String searchString) {
		super(refSetId, searchString, true);
	}
	
	/**
	 * Creates a new instance of the query adapter based on the specified reference set concept identifier, a query term
	 * where the status of the returning members can be specified. 
	 * @param refSetId the SNOMED CT identifier concept ID of the reference set.
	 * @param searchString the query term.
	 * @param excludeInactive indicates whether the inactive members should be excluded or not.
	 */
	public SnomedConcreteDataTypeRefSetMemberIndexQueryAdapter(final String refSetId, final String searchString, final boolean excludeInactive) {
		super(refSetId, searchString, excludeInactive);
	}
	
	@Override
	public SnomedRefSetMemberIndexEntry buildSearchResult(final Document doc, final IBranchPath branchPath, final float score) {
		final DataType dataType = DataType.get(SnomedMappings.memberDataTypeOrdinal().getValue(doc));
		final String serializedValue = SnomedMappings.memberSerializedValue().getValue(doc);
		
		return SnomedRefSetMemberIndexEntry.builder(doc)
				.score(score)
				.additionalField(SnomedMappings.memberSerializedValue().fieldName(), SnomedRefSetUtil.deserializeValue(dataType, serializedValue))
				.build();
	}
}
