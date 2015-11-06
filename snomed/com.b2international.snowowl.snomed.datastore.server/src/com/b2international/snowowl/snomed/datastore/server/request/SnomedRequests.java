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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.server.request.Branching;
import com.b2international.snowowl.datastore.server.request.RepositoryCommitRequestBuilder;
import com.b2international.snowowl.datastore.server.request.RepositoryRequests;
import com.b2international.snowowl.datastore.server.request.Reviews;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 * @since 4.5
 */
public abstract class SnomedRequests {

	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;

	private SnomedRequests() {
	}
	
	public static <B> RepositoryCommitRequestBuilder prepareCommit(String userId, String branch) {
		return RepositoryRequests.prepareCommit(userId, REPOSITORY_ID, branch);
	}
	
	public static SnomedConceptSearchRequestBuilder prepareSearch(String branch) {
		return new SnomedConceptSearchRequestBuilder(branch);
	}
	
	public static SnomedConceptGetRequestBuilder prepareGet(String branch) {
		return new SnomedConceptGetRequestBuilder(branch);
	}
	
	public static Request<TransactionContext, Void> prepareDeleteComponent(String componentId, Class<? extends Component> type) {
		return new SnomedComponentDeleteRequest(componentId, type);
	}
	
	public static Request<TransactionContext, SnomedReferenceSetMember> prepareNewMember(String moduleId, String referencedComponentId, String referenceSetId) {
		final SnomedRefSetMemberCreateRequest req = new SnomedRefSetMemberCreateRequest();
		req.setModuleId(moduleId);
		req.setReferencedComponentId(referencedComponentId);
		req.setReferenceSetId(referenceSetId);
		return req;
	}
	
	// TODO migrate initial API to builders
	public static Request<ServiceProvider, SnomedReferenceSets> prepareGetReferenceSets(String branch) {
		return RepositoryRequests.wrap(REPOSITORY_ID, branch, new SnomedRefSetReadAllRequest());
	}
	
	public static Request<ServiceProvider, SnomedReferenceSet> prepareGetReferenceSet(String branch, String referenceSetId) {
		return RepositoryRequests.wrap(REPOSITORY_ID, branch, new SnomedRefSetReadRequest(referenceSetId));
	}
	
	public static Request<ServiceProvider, SnomedReferenceSetMembers> prepareGetReferenceSetMembers(String branch, int offset, int limit) {
		return RepositoryRequests.wrap(REPOSITORY_ID, branch, new SnomedRefSetMemberReadAllRequest(offset, limit));
	}
	
	public static Request<ServiceProvider, SnomedReferenceSetMember> prepareGetReferenceSetMember(String branch, String memberId) {
		return RepositoryRequests.wrap(REPOSITORY_ID, branch, new SnomedRefSetMemberReadRequest(memberId));
	}

	public static Branching branching() {
		return RepositoryRequests.branching(REPOSITORY_ID);
	}

	public static Reviews review() {
		return RepositoryRequests.reviews(REPOSITORY_ID);
	}

}
