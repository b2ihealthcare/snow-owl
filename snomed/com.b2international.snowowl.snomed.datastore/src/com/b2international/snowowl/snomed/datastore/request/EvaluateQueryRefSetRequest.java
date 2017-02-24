/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.request.BaseResourceRequest;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluation;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluations;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.5
 */
public final class EvaluateQueryRefSetRequest extends BaseResourceRequest<BranchContext, QueryRefSetMemberEvaluations> {

	@NotNull
	@JsonProperty
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
						return SnomedRequests.prepareQueryRefSetMemberEvaluation(input.getId()).setLocales(locales()).setExpand(expand()).build();
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
	
	private Collection<SnomedReferenceSetMember> getQueryMembers(BranchContext context, SnomedReferenceSet referenceSet) {
		if (!SnomedRefSetType.QUERY.equals(referenceSet.getType())) {
			throw new BadRequestException("Cannot evaluate non-query type reference set '%s'", referenceSet.getId());
		}
		return SnomedRequests.prepareSearchMember()
				.all()
				.filterByRefSet(referenceSet.getId())
				.build()
				.execute(context)
				.getItems();
	}
	
}