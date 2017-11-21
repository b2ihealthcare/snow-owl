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
package com.b2international.snowowl.snomed.core.domain.refset;

import java.util.Collection;

/**
 * @since 4.5
 */
public class QueryRefSetMemberEvaluationImpl implements QueryRefSetMemberEvaluation {

	private final String memberId;
	private final String referenceSetId;
	private final Collection<MemberChange> changes;

	public QueryRefSetMemberEvaluationImpl(String memberId, String referenceSetId, Collection<MemberChange> changes) {
		this.memberId = memberId;
		this.referenceSetId = referenceSetId;
		this.changes = changes;
	}
	
	@Override
	public String getMemberId() {
		return memberId;
	}
	
	@Override
	public String getReferenceSetId() {
		return referenceSetId;
	}

	@Override
	public Collection<MemberChange> getChanges() {
		return changes;
	}
	
}
