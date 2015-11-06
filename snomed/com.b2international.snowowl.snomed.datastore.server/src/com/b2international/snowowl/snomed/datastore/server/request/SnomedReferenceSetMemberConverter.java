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
package com.b2international.snowowl.snomed.datastore.server.request;

import java.util.Date;

import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.server.domain.SnomedReferenceSetMemberImpl;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @since 4.5
 */
public class SnomedReferenceSetMemberConverter implements Function<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember> {

	private static final String QUERY_TYPE_LAST_UPDATED_DATE = "lastUpdatedDate";
	
	private final BranchContext context;

	public SnomedReferenceSetMemberConverter(BranchContext context) {
		this.context = context;
	}
	
	@Override
	public SnomedReferenceSetMember apply(SnomedRefSetMemberIndexEntry input) {
		final SnomedReferenceSetMemberImpl member = new SnomedReferenceSetMemberImpl();
		member.setId(input.getId());
		member.setEffectiveTime(EffectiveTimes.toDate(input.getEffectiveTimeAsLong()));
		member.setReleased(input.isReleased());
		member.setActive(input.isActive());
		member.setModuleId(input.getModuleId());
		member.setReferencedComponentId(input.getReferencedComponentId());
		member.setReferenceSetId(input.getRefSetIdentifierId());
		if (SnomedRefSetType.QUERY == input.getRefSetType()) {
			// in case of query type refset the actual ESCG query is stored in the specialFieldId prop
			final Builder<String, Object> props = ImmutableMap.builder();
			props.put(SnomedRf2Headers.FIELD_QUERY, input.getSpecialFieldId());
			props.put(QUERY_TYPE_LAST_UPDATED_DATE, getLastUpdatedDate(input.getReferencedComponentId()));
			member.setProperties(props.build());
		}
		return member;
	}

	private Date getLastUpdatedDate(String referenceSetId) {
		// find the revision date of the simple type reference set
		final ICDOConnection connection = context.service(ICDOConnection.class);
		final CDOView view = connection.createView(context.branch().branchPath());
		try {
			final SnomedRefSet refSet = new SnomedRefSetLookupService().getComponent(referenceSetId, view);
			return new Date(refSet.cdoRevision().getTimeStamp());
		} finally {
			view.close();
		}
	}

	public SnomedReferenceSetMember apply(SnomedRefSetMember input) {
		final SnomedReferenceSetMemberImpl member = new SnomedReferenceSetMemberImpl();
		member.setId(input.getUuid());
		member.setEffectiveTime(input.getEffectiveTime());
		member.setReleased(input.isReleased());
		member.setActive(input.isActive());
		member.setModuleId(input.getModuleId());
		member.setReferencedComponentId(input.getReferencedComponentId());
		member.setReferenceSetId(input.getRefSetIdentifierId());
		if (input instanceof SnomedQueryRefSetMember) {
			final Builder<String, Object> props = ImmutableMap.builder();
			props.put(SnomedRf2Headers.FIELD_QUERY, ((SnomedQueryRefSetMember) input).getQuery());
			props.put(QUERY_TYPE_LAST_UPDATED_DATE, getLastUpdatedDate(input.getReferencedComponentId()));
			member.setProperties(props.build());
		}
		return member;
	}

}
