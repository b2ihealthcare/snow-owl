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

import java.util.Collection;

import jakarta.validation.constraints.NotNull;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.request.IndexResourceRequest;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluations;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public final class EvaluateQueryRefSetRequest extends IndexResourceRequest<BranchContext, QueryRefSetMemberEvaluations> implements AccessControl {

	@NotNull
	@JsonProperty
	private final String referenceSetId;
	
	EvaluateQueryRefSetRequest(String referenceSetId) {
		this.referenceSetId = referenceSetId;
	}
	
	@Override
	public QueryRefSetMemberEvaluations execute(final BranchContext context) {
		final SnomedReferenceSet referenceSet = SnomedRequests.prepareGetReferenceSet(referenceSetId).build().execute(context);
		return new QueryRefSetMemberEvaluations(
			getQueryMembers(context, referenceSet)
				.stream()
				.map(input -> SnomedRequests.prepareQueryRefSetMemberEvaluation(input.getId()).setLocales(locales()).setExpand(expand()).build().execute(context))
				.toList()
		);
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

	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}
	
}