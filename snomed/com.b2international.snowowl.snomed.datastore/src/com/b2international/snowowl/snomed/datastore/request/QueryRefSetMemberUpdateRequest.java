/*
 * Copyright 2011-2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluation;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

/**
 * @since 4.5
 */
public final class QueryRefSetMemberUpdateRequest implements Request<TransactionContext, Boolean>, AccessControl {

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
		final QueryRefSetMemberEvaluation evaluation = SnomedRequests.prepareQueryRefSetMemberEvaluation(memberId)
			.build()
			.execute(context);
		
		// lookup IDs before applying change to speed up query member update
		evaluation.getChangesAsStream().forEachOrdered(batch -> {
			final Set<String> referencedComponentIds = batch.stream()
				.map(change -> change.getReferencedComponent().getId())
				.collect(Collectors.toSet());
			
			context.lookup(referencedComponentIds, SnomedConceptDocument.class);
			
			final Set<String> memberIds = batch.stream()
				.map(change -> change.getMemberId())
				.filter(Predicates.notNull())
				.collect(Collectors.toSet());
			
			final Map<String, SnomedReferenceSetMember> membersById = Maps.uniqueIndex(SnomedRequests.prepareSearchMember()
				.filterByRefSet(evaluation.getReferenceSetId())
				.filterByIds(memberIds)
				.setLimit(memberIds.size())
				.build()
				.execute(context), SnomedReferenceSetMember::getId);
			
			batch.forEach(change -> {
				final SnomedReferenceSetMember member = membersById.get(change.getMemberId());
				switch (change.getChangeKind()) {
					case ADD:
						SnomedRequests.prepareNewMember()
							.setModuleId(moduleId)
							.setReferencedComponentId(change.getReferencedComponent().getId())
							.setRefsetId(evaluation.getReferenceSetId())
							.buildNoContent()
							.execute(context);
						break;

					case REMOVE:
						if (member.isReleased()) {
							SnomedRequests.prepareUpdateMember(change.getMemberId())
								.setSource(Map.of(SnomedRf2Headers.FIELD_ACTIVE, Boolean.FALSE))
								.build()
								.execute(context);
						} else {
							SnomedRequests.prepareDeleteMember(change.getMemberId())
								.build()
								.execute(context);
						}
						break;
						
					case CHANGE:
						if (!member.isActive()) {
							SnomedRequests.prepareUpdateMember(change.getMemberId())
								.setSource(Map.of(SnomedRf2Headers.FIELD_ACTIVE, Boolean.TRUE))
								.build()
								.execute(context);
						}
						break;
						
					default: 
						throw new UnsupportedOperationException("Unexpected change kind: " + change.getChangeKind()); 
				}
			});
			
			// Commit changes at the end of each batch (the context will also be committed at the end of the request)
			context.commit();
		});

		return Boolean.TRUE;
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}
}
