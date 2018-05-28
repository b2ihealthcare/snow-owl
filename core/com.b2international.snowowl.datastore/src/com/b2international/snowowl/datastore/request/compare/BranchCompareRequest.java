/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.compare;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionCompare;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.9
 */
final class BranchCompareRequest implements Request<RepositoryContext, CompareResult> {

	@JsonProperty
	private String base;
	
	@NotEmpty
	@JsonProperty
	private String compare;
	
	@Min(0)
	@JsonProperty
	private int limit;
	
	BranchCompareRequest() {
	}
	
	void setBaseBranch(String baseBranch) {
		this.base = baseBranch;
	}
	
	void setCompareBranch(String compareBranch) {
		this.compare = compareBranch;
	}
	
	void setLimit(int limit) {
		this.limit = limit;
	}
	
	@Override
	public CompareResult execute(RepositoryContext context) {
		final RevisionIndex index = context.service(RevisionIndex.class);
		final CoreTerminologyBroker terminologyBroker = context.service(CoreTerminologyBroker.class);
		final Branch branchToCompare = RepositoryRequests.branching().prepareGet(compare).build().execute(context);
		final long compareHeadTimestamp = branchToCompare.headTimestamp();
		
		final RevisionCompare compareResult;
		final String baseBranchPath;
		if (base != null) {
			compareResult = index.compare(base, compare, limit);
			baseBranchPath = base;
		} else {
			compareResult = index.compare(compare, limit);
			baseBranchPath = branchToCompare.parentPath();
		}
		
		final CompareResult.Builder result = CompareResult.builder(baseBranchPath, compare, compareHeadTimestamp);
		
		for (Class<? extends Revision> revisionType : compareResult.getNewRevisionTypes()) {
			final short terminologyComponentId = terminologyBroker.getTerminologyComponentIdShort(revisionType);
			if (RevisionDocument.class.isAssignableFrom(revisionType)) {
				result.addTotalNew(compareResult.getNewTotals(revisionType));
				compareResult.getNewComponents(revisionType)
					.stream()
					.map(id -> ComponentIdentifier.of(terminologyComponentId, id))
					.forEach(result::putNewComponent);
			}
		}

		for (Class<? extends Revision> revisionType : compareResult.getChangedRevisionTypes()) {
			final short terminologyComponentId = terminologyBroker.getTerminologyComponentIdShort(revisionType);
			if (RevisionDocument.class.isAssignableFrom(revisionType)) {
				result.addTotalChanged(compareResult.getChangedTotals(revisionType));
				compareResult.getChangedComponents(revisionType)
					.stream()
					.map(id -> ComponentIdentifier.of(terminologyComponentId, id))
					.forEach(result::putChangedComponent);
			}
		}

		for (Class<? extends Revision> revisionType : compareResult.getDeletedRevisionTypes()) {
			final short terminologyComponentId = terminologyBroker.getTerminologyComponentIdShort(revisionType);
			if (RevisionDocument.class.isAssignableFrom(revisionType)) {
				result.addTotalDeleted(compareResult.getDeletedTotals(revisionType));
				compareResult.getDeletedComponents(revisionType)
					.stream()
					.map(id -> ComponentIdentifier.of(terminologyComponentId, id))
					.forEach(result::putDeletedComponent);
			}
		}
		
		return result.build();
	}

}
