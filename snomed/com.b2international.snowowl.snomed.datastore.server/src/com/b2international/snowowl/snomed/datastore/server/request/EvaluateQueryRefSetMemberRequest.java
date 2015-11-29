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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.MemberChange;
import com.b2international.snowowl.snomed.core.domain.refset.MemberChangeImpl;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluation;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluationImpl;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.google.common.collect.Maps;

/**
 * @since 4.5
 */
public final class EvaluateQueryRefSetMemberRequest extends BaseRequest<BranchContext, QueryRefSetMemberEvaluation> {

	@NotEmpty
	private String memberId;

	EvaluateQueryRefSetMemberRequest(String memberId) {
		this.memberId = memberId;
	}
	
	@Override
	public QueryRefSetMemberEvaluation execute(BranchContext context) {
		// TODO support pre-population???
		final SnomedReferenceSetMember member = SnomedRequests
				.prepareGetMember()
				.setComponentId(memberId)
				.build()
				.execute(context);
		final String query = (String) member.getProperties().get(SnomedRf2Headers.FIELD_QUERY);
		final String targetReferenceSet = member.getReferencedComponent().getId();

		// GET matching members of a query
		final SnomedConcepts matchingConcepts = SnomedRequests.prepareSearchConcept().filterByEscg(query).all().build().execute(context);
		
		final Map<String, ISnomedConcept> conceptsToAdd = newHashMap();
		final Collection<SnomedReferenceSetMember> membersToRemove = newHashSet();
		final Map<String, String> conceptsToActivate = Maps.newHashMap();

		// add all matching first
		for (ISnomedConcept matchedConcept : matchingConcepts.getItems()) {
			if (matchedConcept.isActive()) {
				conceptsToAdd.put(matchedConcept.getId(), matchedConcept);
			}
		}
		
		// then re-evaluate all current members of the target simple type reference set
		final Collection<SnomedReferenceSetMember> curretMembersOfTarget = SnomedRequests.prepareSearchMember()
					.all()
					.filterByRefSet(targetReferenceSet)
					.build()
					.execute(context)
					.getItems();
		
		for (SnomedReferenceSetMember currentMember : curretMembersOfTarget) {
			final String referencedComponentId = currentMember.getReferencedComponent().getId();
			if (conceptsToAdd.containsKey(referencedComponentId)) {
				if (!currentMember.isActive()) {
					// TODO fix label???
					conceptsToActivate.put(referencedComponentId, referencedComponentId);
				} else {
					conceptsToAdd.remove(referencedComponentId);
				}
			} else {
				membersToRemove.add(currentMember);
			}
		}
		
		final Collection<MemberChange> changes = newArrayList();
		for (String id : conceptsToAdd.keySet()) {
			// TODO label???
			changes.add(MemberChangeImpl.added(id));
		}

		for (SnomedReferenceSetMember memberToRemove : membersToRemove) {
			// TODO label???
			changes.add(MemberChangeImpl.removed(memberToRemove.getReferencedComponent().getId(), memberToRemove.getId()));
		}

//		for (String id : conceptsToActivate.keySet()) {
//			changes.add(new Diff(MemberChangeKind.ACTIVATE, id, conceptsToActivate.get(id)));
//		}
		return new QueryRefSetMemberEvaluationImpl(targetReferenceSet, changes);
	}

	@Override
	protected Class<QueryRefSetMemberEvaluation> getReturnType() {
		return QueryRefSetMemberEvaluation.class;
	}
	
}
