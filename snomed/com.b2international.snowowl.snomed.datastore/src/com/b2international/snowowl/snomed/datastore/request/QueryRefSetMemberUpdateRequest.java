/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.MemberChange;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluation;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.5
 */
public final class QueryRefSetMemberUpdateRequest implements Request<TransactionContext, Boolean> {

	@NotEmpty
	private final String memberId;
	
	@NotEmpty
	private final String moduleId;

	QueryRefSetMemberUpdateRequest(String memberId, String moduleId) {
		this.memberId = memberId;
		this.moduleId = moduleId;
	}

	@Override
	public Boolean execute(TransactionContext context) {
		// evaluate query member
		final QueryRefSetMemberEvaluation evaluation = SnomedRequests.prepareQueryRefSetMemberEvaluation(memberId).build().execute(context);
		
		// lookup IDs before applying change to speed up query member update
		final Set<String> referencedComponents = evaluation.getChanges().stream().map(MemberChange::getReferencedComponent).map(SnomedConcept::getId).collect(Collectors.toSet());
		context.lookup(referencedComponents, Concept.class);
		
		// apply all change as request on the target reference set
		for (MemberChange change : evaluation.getChanges()) {
			switch (change.getChangeKind()) {
			case ADD:
				SnomedRequests
					.prepareNewMember()
					.setModuleId(moduleId)
					.setReferencedComponentId(change.getReferencedComponent().getId())
					.setReferenceSetId(evaluation.getReferenceSetId())
					.buildNoContent()
					.execute(context);
				break;
			case REMOVE:
				final SnomedReferenceSetMember member = SnomedRequests.prepareGetMember(change.getMemberId())
					.build()
					.execute(context);
				if (member.isReleased()) {
					SnomedRequests.prepareUpdateMember()
						.setMemberId(change.getMemberId())
						.setSource(ImmutableMap.of(SnomedRf2Headers.FIELD_ACTIVE, Boolean.FALSE))
						.build()
						.execute(context);
				} else {
					SnomedRequests.prepareDeleteMember(change.getMemberId())
						.build()
						.execute(context);
				}
				break;
			case CHANGE:
				final SnomedReferenceSetMember memberToChange = SnomedRequests.prepareGetMember(change.getMemberId())
					.build()
					.execute(context);
				if(!memberToChange.isActive()) {
					SnomedRequests.prepareUpdateMember()
					.setMemberId(change.getMemberId())
					.setSource(ImmutableMap.of(SnomedRf2Headers.FIELD_ACTIVE, Boolean.TRUE))
					.build()
					.execute(context);
				}
				break;	
			default: 
				throw new UnsupportedOperationException("Not implemented case: " + change.getChangeKind()); 
			}
		}
		return Boolean.TRUE;
	}

}
