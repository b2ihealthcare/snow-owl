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
import com.b2international.snowowl.snomed.core.domain.refset.MemberChange;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * The central class of the SNOMED CT Java API provided by Snow Owl Terminology Server and Authoring Environment Runtime.
 * This class cannot be instantiated or subclassed by clients, all the functionality is accessible
 * via static methods.  In general, this class provides access to the API via a set of RequestBuilder
 * classes that follow the <i>Builder</i> pattern focusing on features such as:
 * <ul>
 * <li>searching for SNOMED CT artefacts such as concepts, relationships, descriptions and reference sets</li>
 * <li>creating new SNOMED CT artefacts</li>
 * <li>deleting SNOMED CT artefacts</li>
 * <li>updating SNOMED CT artefacts</li>
 * <li>access to Snow Owl's revision control features</li>
 * </ul>
 * <p>
 * 
 * In general the following steps are required to issue a request to the Snow Owl server:
 * <ol>
 * <li>invoke the proper <i>prepare</i> method to obtain a request builder</li>
 * <li>specify the filter conditions, settings, parameters for the request</li>
 * <li>invoke the <i>build</i> method with the <i>branchpath</i> parameter that specifies the target branch for the request</li>
 * <li>call <i>execute</i> for async or <i>executeSync</i> for synchronous execution</li>
 * </ol>
 * 
 * A representative example for a service accessible by this class:
 * <pre><code>
 * //define the branch to operate on
 * IBranchPath branchPath = BranchPathUtils.createMainPath()
 * 
 * //Obtain the eventbus required to execution of the request
 * IEventBus eventBus = ApplicationContext.getInstance().getService(IEventBus.class)
 * 
 * //Return the children of 'Clinical finding'
 * SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
 *  .filterByEscg('<<404684003') //
 *  .all()
 *  .build(branchPath.getPath()).executeSync(eventBus);
 * </code></pre>
 *  
 * @since 4.5
 */
public abstract class SnomedRequests {

	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;

	private SnomedRequests() {
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to search for concepts.
	 * @return SNOMED CT concept search request builder
	 */
	public static SnomedConceptSearchRequestBuilder prepareSearchConcept() {
		return new SnomedConceptSearchRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to search for descriptions.
	 * @return SNOMED CT description search request builder
	 */
	public static SnomedDescriptionSearchRequestBuilder prepareSearchDescription() {
		return new SnomedDescriptionSearchRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to search for reference sets.
	 * @return SNOMED CT reference set search request builder
	 */
	public static SnomedRefSetSearchRequestBuilder prepareSearchRefSet() {
		return new SnomedRefSetSearchRequestBuilder(REPOSITORY_ID);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare a request to search for reference set members.
	 * @return SNOMED CT reference set member search request builder
	 */
	public static SnomedRefSetMemberSearchRequestBuilder prepareSearchMember() {
		return new SnomedRefSetMemberSearchRequestBuilder(REPOSITORY_ID);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare a request to search for relationships.
	 * @return SNOMED CT relationship search request builder
	 */
	public static SnomedRelationshipSearchRequestBuilder prepareSearchRelationship() {
		return new SnomedRelationshipSearchRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a concept.
	 * @return SNOMED CT concept request builder
	 */
	public static SnomedConceptGetRequestBuilder prepareGetConcept() {
		return new SnomedConceptGetRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a description.
	 * @return SNOMED CT description request builder
	 */
	public static SnomedDescriptionGetRequestBuilder prepareGetDescription() {
		return new SnomedDescriptionGetRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a relationship.
	 * @return SNOMED CT relationship request builder
	 */
	public static SnomedRelationshipGetRequestBuilder prepareGetRelationship() {
		return new SnomedRelationshipGetRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a reference set.
	 * @return SNOMED CT reference set request builder
	 */
	public static SnomedRefSetGetRequestBuilder prepareGetReferenceSet() {
		return new SnomedRefSetGetRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a reference set member.
	 * @return SNOMED CT reference set member request builder
	 */
	public static SnomedRefSetMemberGetRequestBuilder prepareGetMember() {
		return new SnomedRefSetMemberGetRequestBuilder(REPOSITORY_ID);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a generic component.
	 * @return SNOMED CT delete request builder
	 */
	private static DeleteRequestBuilder prepareDelete() {
		return RepositoryRequests.prepareDelete(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a reference set member.
	 * @return SNOMED CT reference set member delete request builder
	 */
	public static DeleteRequestBuilder prepareDeleteMember() {
		return prepareDelete().setType(SnomedRefSetMember.class);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a concept.
	 * @return SNOMED CT delete concept request builder
	 */
	public static DeleteRequestBuilder prepareDeleteConcept() {
		return prepareDelete().setType(Concept.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a description.
	 * @return SNOMED CT delete description request builder
	 */
	public static DeleteRequestBuilder prepareDeleteDescription() {
		return prepareDelete().setType(Description.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a relationship.
	 * @return SNOMED CT delete relationship request builder
	 */
	public static DeleteRequestBuilder prepareDeleteRelationship() {
		return prepareDelete().setType(Relationship.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a reference set member.
	 * @return SNOMED CT create reference set member request builder
	 */
	public static SnomedRefSetMemberCreateRequestBuilder prepareNewMember() {
		return new SnomedRefSetMemberCreateRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a reference set.
	 * @return SNOMED CT create reference set request builder
	 */
	public static SnomedRefSetCreateRequestBuilder prepareNewRefSet() {
		return new SnomedRefSetCreateRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a concept.
	 * @return SNOMED CT create concept request builder
	 */
	public static SnomedConceptCreateRequestBuilder prepareNewConcept() {
		return new SnomedConceptCreateRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a description.
	 * @return SNOMED CT create description request builder
	 */
	public static SnomedDescriptionCreateRequestBuilder prepareNewDescription() {
		return new SnomedDescriptionCreateRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a relationship.
	 * @return SNOMED CT create relationship request builder
	 */
	public static SnomedRelationshipCreateRequestBuilder prepareNewRelationship() {
		return new SnomedRelationshipCreateRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns the central class that provides access the server's branching features.
	 * @return central branching class with access to branching features
	 */
	public static Branching branching() {
		return RepositoryRequests.branching(REPOSITORY_ID);
	}
	
	/**
	 * Returns the central class that provides access the server's revision control
	 * merging features.
	 * @return central merging class with access to merging features
	 */
	public static Merging merging() {
		return RepositoryRequests.merging(REPOSITORY_ID);
	}

	/**
	 * Returns the central class that provides access the server's review features
	 * @return central review class with access to review features
	 */
	public static Reviews review() {
		return RepositoryRequests.reviews(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare the evaluation of an 
	 * Query type reference set.
	 * @return SNOMED CT Query type reference set evaluation request builder
	 */
	public static QueryRefSetEvaluationRequestBuilder prepareQueryRefSetEvaluation(String referenceSetId) {
		return new QueryRefSetEvaluationRequestBuilder(REPOSITORY_ID).setReferenceSetId(referenceSetId);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare the evaluation of Query type reference set members.
	 * @return SNOMED CT Query type reference set members evaluation request builder
	 */
	public static QueryRefSetMemberEvaluationRequestBuilder prepareQueryRefSetMemberEvaluation(String memberId) {
		return new QueryRefSetMemberEvaluationRequestBuilder(REPOSITORY_ID).setMemberId(memberId);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating of Query type reference set members.
	 * @return SNOMED CT Query type reference set members update request builder
	 */
	public static QueryRefSetMemberUpdateRequestBuilder prepareUpdateQueryRefSetMember() {
		return new QueryRefSetMemberUpdateRequestBuilder(REPOSITORY_ID);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the changing of reference set members.
	 * @return SNOMED CT reference set members change request builder
	 */
	public static SnomedRefSetMemberChangeRequestBuilder prepareMemberChangeRequest(MemberChange change, String moduleId, String referenceSetId) {
		return new SnomedRefSetMemberChangeRequestBuilder(change, moduleId, referenceSetId);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating of a Query type reference set.
	 * @return SNOMED CT Query type reference set update request builder
	 */
	public static QueryRefSetUpdateRequestBuilder prepareUpdateQueryRefSet() {
		return new QueryRefSetUpdateRequestBuilder(REPOSITORY_ID);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare the updating reference set members.
	 * @return SNOMED CT reference set member update request builder
	 */
	public static SnomedRefSetMemberUpdateRequestBuilder prepareUpdateMember() {
		return new SnomedRefSetMemberUpdateRequestBuilder(REPOSITORY_ID);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating a concept.
	 * @param concept id of the concept to be updated
	 * @return SNOMED CT concept update request builder
	 */
	public static SnomedConceptUpdateRequestBuilder prepareUpdateConcept(String componentId) {
		return new SnomedConceptUpdateRequestBuilder(REPOSITORY_ID, componentId);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating a description.
	 * @param description id of the description to be updated
	 * @return SNOMED CT description update request builder
	 */
	public static SnomedDescriptionUpdateRequestBuilder prepareUpdateDescription(String componentId) {
		return new SnomedDescriptionUpdateRequestBuilder(REPOSITORY_ID, componentId);
	}

	public static SnomedRelationshipUpdateRequestBuilder prepareUpdateRelationship(String componentId) {
		return new SnomedRelationshipUpdateRequestBuilder(REPOSITORY_ID, componentId);
	}

	public static SnomedRepositoryCommitRequestBuilder prepareCommit() {
		return new SnomedRepositoryCommitRequestBuilder(REPOSITORY_ID);
	}

	public static SnomedRepositoryBulkReadRequestBuilder prepareBulkRead() {
		return new SnomedRepositoryBulkReadRequestBuilder(REPOSITORY_ID);
	}
}
