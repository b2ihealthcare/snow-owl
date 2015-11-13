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

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.snomed.core.domain.refset.MemberChange;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluation;

/**
 * @since 4.5
 */
public class QueryRefSetMemberUpdateRequest extends BaseRequest<TransactionContext, Void> {

	@NotEmpty
	private final String memberId;
	
	@NotEmpty
	private final String moduleId;

	QueryRefSetMemberUpdateRequest(String memberId, String moduleId) {
		this.memberId = memberId;
		this.moduleId = moduleId;
	}

	@Override
	public Void execute(TransactionContext context) {
		// evaluate query member
		final QueryRefSetMemberEvaluation evaluation = SnomedRequests.prepareQueryRefSetMemberEvaluation(memberId).build().execute(context);
		// apply all change as request on the target reference set
		for (MemberChange change : evaluation.getChanges()) {
			SnomedRequests.prepareMemberChangeRequest(change, moduleId, evaluation.getReferenceSetId()).build().execute(context);
		}
		return null;
	}

	@Override
	protected Class<Void> getReturnType() {
		return Void.class;
	}

}
