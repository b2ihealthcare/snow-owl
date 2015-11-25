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

import java.util.Collection;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluation;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluations;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.5
 */
public class EvaluateQueryRefSetRequest extends BaseRequest<BranchContext, QueryRefSetMemberEvaluations> {

	private final String referenceSetId;

	EvaluateQueryRefSetRequest(String referenceSetId) {
		this.referenceSetId = referenceSetId;
	}
	
	@Override
	public QueryRefSetMemberEvaluations execute(final BranchContext context) {
		final SnomedReferenceSet referenceSet = SnomedRequests.prepareGetReferenceSet().setComponentId(referenceSetId).build().execute(context);
		return new QueryRefSetMemberEvaluations(
				FluentIterable
				.from(getQueryMembers(context, referenceSet))
				.transform(new Function<SnomedReferenceSetMember, Request<BranchContext, QueryRefSetMemberEvaluation>>() {
					@Override
					public Request<BranchContext, QueryRefSetMemberEvaluation> apply(SnomedReferenceSetMember input) {
						return new EvaluateQueryRefSetMemberRequest(input.getId());
					}
				})
				.transform(new Function<Request<BranchContext, QueryRefSetMemberEvaluation>, QueryRefSetMemberEvaluation>() {
					@Override
					public QueryRefSetMemberEvaluation apply(Request<BranchContext, QueryRefSetMemberEvaluation> input) {
						return input.execute(context);
					}
				})
				.toList());
	}
	
	@Override
	protected Class<QueryRefSetMemberEvaluations> getReturnType() {
		return QueryRefSetMemberEvaluations.class;
	}
	
	private Collection<SnomedReferenceSetMember> getQueryMembers(BranchContext context, SnomedReferenceSet referenceSet) {
		if (!SnomedRefSetType.QUERY.equals(referenceSet.getType())) {
			throw new BadRequestException("Cannot reevaluate reference set '%s'", referenceSet.getId());
		}
		return SnomedRequests.prepareMemberSearch()
				.all()
				.filterByRefSet(referenceSet.getId())
				.build()
				.execute(context)
				.getItems();
	}
	
}