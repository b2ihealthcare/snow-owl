/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.request;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;

/**
 * @since 7.0
 */
public final class ClassificationJobRequestBuilder 
		extends BaseRequestBuilder<ClassificationJobRequestBuilder, BranchContext, Boolean>
		implements RevisionIndexRequestBuilder<Boolean> {

	private String reasonerId;
	private final List<SnomedConcept> additionalConcepts = newArrayList();
	private String parentLockContext = DatastoreLockContextDescriptions.ROOT;

	ClassificationJobRequestBuilder() {}

	public ClassificationJobRequestBuilder setReasonerId(final String reasonerId) {
		this.reasonerId = reasonerId;
		return this;
	}

	public ClassificationJobRequestBuilder addAllConcepts(final List<SnomedConcept> additionalConcepts) {
		this.additionalConcepts.addAll(additionalConcepts);
		return this;
	}
	
	public ClassificationJobRequestBuilder setParentLockContext(final String parentLockContext) {
		this.parentLockContext = parentLockContext;
		return this;
	}

	@Override
	protected Request<BranchContext, Boolean> doBuild() {
		final ClassificationJobRequest request = new ClassificationJobRequest();
		request.setReasonerId(reasonerId);
		request.setAdditionalConcepts(additionalConcepts);
		request.setParentLockContext(parentLockContext);
		return request;
	}
}
