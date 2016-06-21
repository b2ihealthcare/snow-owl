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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.datastore.request.Branching;
import com.b2international.snowowl.datastore.request.DeleteRequestBuilder;
import com.b2international.snowowl.datastore.request.Merging;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.Reviews;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.MemberChange;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 4.5
 */
public abstract class SnomedRequests {

	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;

	private SnomedRequests() {
	}
	
	public static SnomedConceptSearchRequestBuilder prepareSearchConcept() {
		return new SnomedConceptSearchRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedDescriptionSearchRequestBuilder prepareSearchDescription() {
		return new SnomedDescriptionSearchRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedRefSetSearchRequestBuilder prepareSearchRefSet() {
		return new SnomedRefSetSearchRequestBuilder(REPOSITORY_ID);
	}

	public static SnomedRefSetMemberSearchRequestBuilder prepareSearchMember() {
		return new SnomedRefSetMemberSearchRequestBuilder(REPOSITORY_ID);
	}

	public static SnomedRelationshipSearchRequestBuilder prepareSearchRelationship() {
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

	private static DeleteRequestBuilder prepareDelete() {
		return RepositoryRequests.prepareDelete(REPOSITORY_ID);
	}
	
	public static DeleteRequestBuilder prepareDeleteMember() {
		return prepareDelete().setType(SnomedRefSetMember.class);
	}

	public static DeleteRequestBuilder prepareDeleteConcept() {
		return prepareDelete().setType(Concept.class);
	}
	
	public static DeleteRequestBuilder prepareDeleteDescription() {
		return prepareDelete().setType(Description.class);
	}
	
	public static DeleteRequestBuilder prepareDeleteRelationship() {
		return prepareDelete().setType(Relationship.class);
	}
	
	public static SnomedRefSetMemberCreateRequestBuilder prepareNewMember() {
		return new SnomedRefSetMemberCreateRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedRefSetCreateRequestBuilder prepareNewRefSet() {
		return new SnomedRefSetCreateRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedConceptCreateRequestBuilder prepareNewConcept() {
		return new SnomedConceptCreateRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedDescriptionCreateRequestBuilder prepareNewDescription() {
		return new SnomedDescriptionCreateRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedRelationshipCreateRequestBuilder prepareNewRelationship() {
		return new SnomedRelationshipCreateRequestBuilder(REPOSITORY_ID);
	}
	
	public static SnomedRefSetGetRequestBuilder prepareGetReferenceSet() {
		return new SnomedRefSetGetRequestBuilder(REPOSITORY_ID);
	}
	
	public static Branching branching() {
		return RepositoryRequests.branching(REPOSITORY_ID);
	}
	
	public static Merging merging() {
		return RepositoryRequests.merging(REPOSITORY_ID);
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

	public static SnomedRefSetMemberUpdateRequestBuilder prepareUpdateMember() {
		return new SnomedRefSetMemberUpdateRequestBuilder(REPOSITORY_ID);
	}

	public static SnomedConceptUpdateRequestBuilder prepareUpdateConcept(String componentId) {
		return new SnomedConceptUpdateRequestBuilder(REPOSITORY_ID, componentId);
	}

	public static SnomedDescriptionUpdateRequestBuilder prepareUpdateDescription(String componentId) {
		return new SnomedDescriptionUpdateRequestBuilder(REPOSITORY_ID, componentId);
	}

	public static SnomedRelationshipUpdateRequestBuilder prepareUpdateRelationship(String componentId) {
		return new SnomedRelationshipUpdateRequestBuilder(REPOSITORY_ID, componentId);
	}

	public static SnomedRefSetMemberGetRequestBuilder prepareGetMember() {
		return new SnomedRefSetMemberGetRequestBuilder(REPOSITORY_ID);
	}

	public static SnomedRepositoryCommitRequestBuilder prepareCommit() {
		return new SnomedRepositoryCommitRequestBuilder(REPOSITORY_ID);
	}

	public static SnomedRepositoryBulkReadRequestBuilder prepareBulkRead() {
		return new SnomedRepositoryBulkReadRequestBuilder(REPOSITORY_ID);
	}

	/**
	 * Returns all SYNONYM concept ids including all subtypes of {@value Concepts#SYNONYM}.
	 * @return
	 */
	public static SnomedConceptSearchRequestBuilder prepareGetSynonyms() {
		return prepareSearchConcept().all().filterByActive(true).filterByEscg("<<"+Concepts.SYNONYM);
	}
}
