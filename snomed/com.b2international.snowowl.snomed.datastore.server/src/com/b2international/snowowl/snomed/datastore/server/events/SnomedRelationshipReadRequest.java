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
package com.b2international.snowowl.snomed.datastore.server.events;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;

/**
 * @since 4.5
 */
final class SnomedRelationshipReadRequest extends SnomedRelationshipRequest<RepositoryContext, ISnomedRelationship> {

	private String componentId;

	public SnomedRelationshipReadRequest(String componentId) {
		this.componentId = componentId;
	}
	
	@Override
	public ISnomedRelationship execute(RepositoryContext context) {
		final IBranchPath branchPath = context.branch().branchPath();
		final SnomedRelationshipLookupService lookupService = new SnomedRelationshipLookupService();
		final SnomedRelationshipIndexEntry relationship = lookupService.getComponent(branchPath, componentId);
		return getConverter(branchPath).apply(relationship);
	}

	@Override
	protected Class<ISnomedRelationship> getReturnType() {
		return ISnomedRelationship.class;
	}

}
