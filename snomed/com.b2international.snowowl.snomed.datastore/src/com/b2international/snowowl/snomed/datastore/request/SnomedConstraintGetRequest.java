/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.authorization.BranchAccessControl;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.request.GetResourceRequest;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;

/**
 * @since 6.5
 */
final class SnomedConstraintGetRequest 
		extends GetResourceRequest<SnomedConstraintSearchRequestBuilder, BranchContext, SnomedConstraints, SnomedConstraint>
		implements BranchAccessControl {

	private static final long serialVersionUID = 1L;
	
	SnomedConstraintGetRequest(String constraintId) {
		super(constraintId);
	}

	@Override
	protected SnomedConstraintSearchRequestBuilder createSearchRequestBuilder() {
		return new SnomedConstraintSearchRequestBuilder();
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}
	
}
