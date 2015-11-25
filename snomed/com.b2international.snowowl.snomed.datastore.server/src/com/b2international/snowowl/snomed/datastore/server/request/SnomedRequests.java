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

import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.Branching;
import com.b2international.snowowl.datastore.request.RepositoryCommitRequestBuilder;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.Reviews;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.refset.MemberChange;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 4.5
 */
public abstract class SnomedRequests {

	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;

	private SnomedRequests() {
	}
	
	public static RepositoryCommitRequestBuilder prepareCommit(String userId, String branch) {
		return RepositoryRequests.prepareCommit(userId, REPOSITORY_ID, branch);
	}
	
	public static SnomedConceptSearchRequestBuilder prepareConceptSearch() {
		return new SnomedConceptSearchRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedDescriptionSearchRequestBuilder prepareDescriptionSearch() {
		return new SnomedDescriptionSearchRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedRelationshipSearchRequestBuilder prepareRelationshipSearch() {
		return new SnomedRelationshipSearchRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedConceptGetRequestBuilder prepareGetConcept() {
		return new SnomedConceptGetRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedDescriptionGetRequestBuilder prepareGetDescription() {
		return new SnomedDescriptionGetRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedRelationshipGetRequestBuilder prepareGetRelationship() {
		return new SnomedRelationshipGetRequestBuilder(REPOSITORY_ID);
	}
	
	public static Request<TransactionContext, Void> prepareDeleteComponent(String componentId, Class<? extends EObject> type) {
		return new SnomedComponentDeleteRequest(componentId, type);
	}
	
	public static Request<TransactionContext, Void> prepareDeleteMember(String memberId) {
		return prepareDeleteComponent(memberId, SnomedRefSetMember.class);
	}
	
	public static Request<TransactionContext, Void> prepareDeleteConcept(String conceptId) {
		return prepareDeleteComponent(conceptId, Concept.class);
	}
	
	public static Request<TransactionContext, Void> prepareDeleteDescription(String descriptionId) {
		return prepareDeleteComponent(descriptionId, Description.class);
	}
	
	public static Request<TransactionContext, Void> prepareDeleteRelationship(String relationshipId) {
		return prepareDeleteComponent(relationshipId, Relationship.class);
	}
	
	public static RefSetMemberCreateRequestBuilder prepareNewMember() {
		return new RefSetMemberCreateRequestBuilder();
	}
	
	public static SnomedRefSetCreateRequestBuilder prepareNewRefSet() {
		return new SnomedRefSetCreateRequestBuilder();
	}
	
	public static SnomedConceptCreateRequestBuilder prepareNewConcept() {
		return new SnomedConceptCreateRequestBuilder();
	}
	
	public static SnomedDescriptionCreateRequestBuilder prepareNewDescription() {
		return new SnomedDescriptionCreateRequestBuilder();
	}
	
	public static SnomedRelationshipCreateRequestBuilder prepareNewRelationship() {
		return new SnomedRelationshipCreateRequestBuilder();
	}
	
	public static SnomedRefSetGetRequestBuilder prepareGetReferenceSet() {
		return new SnomedRefSetGetRequestBuilder(REPOSITORY_ID);
	}
	
	// TODO migrate initial API to builders
	public static Request<ServiceProvider, SnomedReferenceSets> prepareRefSetSearch(String branch) {
		return RepositoryRequests.wrap(REPOSITORY_ID, branch, new SnomedRefSetSearchRequest());
	}
	
	public static SnomedRefSetMemberSearchRequestBuilder prepareMemberSearch() {
		return new SnomedRefSetMemberSearchRequestBuilder(REPOSITORY_ID);
	}
	
	public static Branching branching() {
		return RepositoryRequests.branching(REPOSITORY_ID);
	}

	public static Reviews review() {
		return RepositoryRequests.reviews(REPOSITORY_ID);
	}

	public static QueryRefSetEvaluationRequestBuilder prepareQueryRefSetEvaluation(String referenceSetId) {
		return new QueryRefSetEvaluationRequestBuilder(REPOSITORY_ID).setReferenceSetId(referenceSetId);
	}
	
	public static QueryRefSetMemberEvaluationRequestBuilder prepareQueryRefSetMemberEvaluation(String memberId) {
		return new QueryRefSetMemberEvaluationRequestBuilder(REPOSITORY_ID).setMemberId(memberId);
	}

	public static QueryRefSetMemberUpdateRequestBuilder prepareUpdateQueryRefSetMember() {
		return new QueryRefSetMemberUpdateRequestBuilder(REPOSITORY_ID);
	}

	public static SnomedRefSetMemberChangeRequestBuilder prepareMemberChangeRequest(MemberChange change, String moduleId, String referenceSetId) {
		return new SnomedRefSetMemberChangeRequestBuilder(change, moduleId, referenceSetId);
	}

	public static QueryRefSetUpdateRequestBuilder prepareUpdateQueryRefSet() {
		return new QueryRefSetUpdateRequestBuilder(REPOSITORY_ID);
	}

	public static SnomedRefSetMemberUpdateRequestBuilder prepareMemberUpdate() {
		return new SnomedRefSetMemberUpdateRequestBuilder(REPOSITORY_ID);
	}

	public static SnomedConceptUpdateRequestBuilder prepareConceptUpdate(String componentId) {
		return new SnomedConceptUpdateRequestBuilder(REPOSITORY_ID, componentId);
	}

	public static SnomedDescriptionUpdateRequestBuilder prepareDescriptionUpdate(String componentId) {
		return new SnomedDescriptionUpdateRequestBuilder(REPOSITORY_ID, componentId);
	}

	public static SnomedRelationshipUpdateRequestBuilder prepareRelationshipUpdate(String componentId) {
		return new SnomedRelationshipUpdateRequestBuilder(REPOSITORY_ID, componentId);
	}

	public static SnomedRefSetMemberGetRequestBuilder prepareGetMember() {
		return new SnomedRefSetMemberGetRequestBuilder(REPOSITORY_ID);
	}
	
}
