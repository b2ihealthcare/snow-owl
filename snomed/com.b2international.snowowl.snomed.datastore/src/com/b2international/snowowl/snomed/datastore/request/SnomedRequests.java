/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.request.DeleteRequestBuilder;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.ecl.SnomedEclEvaluationRequestBuilder;
import com.b2international.snowowl.snomed.core.ql.SnomedQlEvaluationRequestBuilder;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.dsv.SnomedDSVRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.SnomedRf2Requests;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * The central class of the SNOMED CT Java API provided by Snow Owl Terminology Server and Authoring Environment Runtime.
 * This class cannot be instantiated or subclassed by clients, all the functionality is accessible
 * via static methods.  In general, this class provides access to the API via a set of {@link RequestBuilder}
 * classes that follow the <i>Builder</i> pattern focusing on features such as:
 * <ul>
 * <li>searching for SNOMED CT components such as {@link SnomedConcept Concept}s, {@link SnomedRelationship Relationship}s, {@link SnomedDescription Description}s, {@link SnomedReferenceSet Reference Set}s and {@link SnomedReferenceSetMember Reference Set Member}s</li>
 * <li>creating new SNOMED CT components</li>
 * <li>deleting SNOMED CT components</li>
 * <li>updating SNOMED CT components</li>
 * <li>creating custom commit requests</li>
 * </ul>
 * <p>
 * 
 * In general the following steps are required to issue a request to the Snow Owl server:
 * <ol>
 * <li>invoke the proper <i>prepare</i> method to obtain a request builder</li>
 * <li>specify the filter conditions, settings, parameters for the request</li>
 * <li>invoke the <i>build</i> method with the <i>repository identifier</i> and <i>branch path</i> parameters that specifies the repository and a branch within that repository</li>
 * <li>call <i>execute</i> to execute the request (this returns a {@link Promise} that can be used to wait for the response either synchronously with {@link Promise#getSync()}) or asynchronously with {@link Promise#then(com.google.common.base.Function)}</li>
 * </ol>
 * 
 * A representative example:
 * <pre><code>
 * //define the branch to operate on
 * String branch = Branch.MAIN_PATH;
 * 
 * //Obtain the eventbus required to execution of the request
 * IEventBus bus = ApplicationContext.getInstance().getService(IEventBus.class);
 * 
 * //Search for concepts matching the an ECL expression (DescendantsOrSelfOf: 'Clinical finding')
 * SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
 *  .filterByEcl('<<404684003')
 *  .all()
 *  .build(SnomedDatastoreActivator.REPOSITORY_UUID, branch) // the repository UUID is required to identify the target Repository
 *  .execute(bus)
 *  .getSync(); // blocks until the request completes
 *  
 * </code></pre>
 *  
 * @since 4.5
 * @see SnomedDatastoreActivator#REPOSITORY_UUID
 * @see RepositoryRequests
 */
public abstract class SnomedRequests {

	private SnomedRequests() {
	}
	
	public static SnomedConstraintSearchRequestBuilder prepareSearchConstraint() {
		return new SnomedConstraintSearchRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to search for concepts.
	 * @return SNOMED CT concept search request builder
	 */
	public static SnomedConceptSearchRequestBuilder prepareSearchConcept() {
		return new SnomedConceptSearchRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to search for descriptions.
	 * @return SNOMED CT description search request builder
	 */
	public static SnomedDescriptionSearchRequestBuilder prepareSearchDescription() {
		return new SnomedDescriptionSearchRequestBuilder();
	}

	/**
	 * Returns a SNOMED CT request builder to prepare a request to search for reference sets.
	 * @return SNOMED CT reference set search request builder
	 */
	public static SnomedRefSetSearchRequestBuilder prepareSearchRefSet() {
		return new SnomedRefSetSearchRequestBuilder();
	}

	/**
	 * Returns a SNOMED CT request builder to prepare a request to search for reference set members.
	 * @return SNOMED CT reference set member search request builder
	 */
	public static SnomedRefSetMemberSearchRequestBuilder prepareSearchMember() {
		return new SnomedRefSetMemberSearchRequestBuilder();
	}

	/**
	 * Returns a SNOMED CT request builder to prepare a request to search for relationships.
	 * @return SNOMED CT relationship search request builder
	 */
	public static SnomedRelationshipSearchRequestBuilder prepareSearchRelationship() {
		return new SnomedRelationshipSearchRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return an MRCM attribute constraint.
	 * @param constraintId - the identifier of the MRCM constraint to return
	 * @return SNOMED CT constraint get request builder
	 */
	public static SnomedConstraintGetRequestBuilder prepareGetConstraint(String constraintId) {
		return new SnomedConstraintGetRequestBuilder(constraintId);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a concept.
	 * @param conceptId - the identifier of the concept to return
	 * @return SNOMED CT concept get request builder
	 */
	public static SnomedConceptGetRequestBuilder prepareGetConcept(String conceptId) {
		return new SnomedConceptGetRequestBuilder(conceptId);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a description.
	 * @param descriptionId - the identifier of the description to return
	 * @return SNOMED CT description get request builder
	 */
	public static SnomedDescriptionGetRequestBuilder prepareGetDescription(String descriptionId) {
		return new SnomedDescriptionGetRequestBuilder(descriptionId);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a relationship.
	 * @param relationshipId - the identifier of the relationship to return
	 * @return SNOMED CT relationship get request builder
	 */
	public static SnomedRelationshipGetRequestBuilder prepareGetRelationship(String relationshipId) {
		return new SnomedRelationshipGetRequestBuilder(relationshipId);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a reference set.
	 * @param referenceSetId - the identifier of the reference set
	 * @return SNOMED CT reference set get request builder
	 */
	public static SnomedRefSetGetRequestBuilder prepareGetReferenceSet(String referenceSetId) {
		return new SnomedRefSetGetRequestBuilder(referenceSetId);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a reference set member.
	 * @param memberId - the identifier of the member
	 * @return SNOMED CT reference set member get request builder
	 */
	public static SnomedRefSetMemberGetRequestBuilder prepareGetMember(String memberId) {
		return new SnomedRefSetMemberGetRequestBuilder(memberId);
	}

	private static DeleteRequestBuilder prepareDelete(String componentId, Class<? extends EObject> type) {
		return new SnomedDeleteRequestBuilder(componentId, type);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes an MRCM attribute constraint.
	 * @param constraintId - the identifier of the constraint
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given constraint
	 */
	public static DeleteRequestBuilder prepareDeleteConstraint(String constraintId) {
		return prepareDelete(constraintId, AttributeConstraint.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a concept.
	 * @param conceptId - the identifier of the concept
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given concept
	 */
	public static DeleteRequestBuilder prepareDeleteConcept(String conceptId) {
		return prepareDelete(conceptId, Concept.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a description.
	 * @param descriptionId - the identifier of the description
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given description
	 */
	public static DeleteRequestBuilder prepareDeleteDescription(String descriptionId) {
		return prepareDelete(descriptionId, Description.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a relationship.
	 * @param relationshipId - the identifier of the relationship
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given relationship
	 */
	public static DeleteRequestBuilder prepareDeleteRelationship(String relationshipId) {
		return prepareDelete(relationshipId, Relationship.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a reference set.
	 * @param refSetId - the identifier of the reference set
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given reference set
	 */
	public static DeleteRequestBuilder prepareDeleteReferenceSet(String refSetId) {
		return prepareDelete(refSetId, SnomedRefSet.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a reference set member.
	 * @param memberId - the identifier of the member
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given reference set member
	 */
	public static DeleteRequestBuilder prepareDeleteMember(String memberId) {
		return prepareDelete(memberId, SnomedRefSetMember.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates an MRCM attribute constraint.
	 * @return SNOMED CT constraint create request builder
	 */
	public static SnomedConstraintCreateRequestBuilder prepareNewConstraint() {
		return new SnomedConstraintCreateRequestBuilder();
	}

	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a reference set member.
	 * @return SNOMED CT reference set member create request builder
	 */
	public static SnomedRefSetMemberCreateRequestBuilder prepareNewMember() {
		return new SnomedRefSetMemberCreateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a reference set.
	 * @return SNOMED CT reference set create request builder
	 */
	public static SnomedRefSetCreateRequestBuilder prepareNewRefSet() {
		return new SnomedRefSetCreateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a concept.
	 * @return SNOMED CT concept create request builder
	 */
	public static SnomedConceptCreateRequestBuilder prepareNewConcept() {
		return new SnomedConceptCreateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a description.
	 * @return SNOMED CT description create request builder
	 */
	public static SnomedDescriptionCreateRequestBuilder prepareNewDescription() {
		return new SnomedDescriptionCreateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a relationship.
	 * @return SNOMED CT relationship create request builder
	 */
	public static SnomedRelationshipCreateRequestBuilder prepareNewRelationship() {
		return new SnomedRelationshipCreateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare the evaluation of an 
	 * Expression Constraint Language (ECL) expression.
	 * @return SNOMED CT ECL evaluation request builder
	 */
	public static SnomedEclEvaluationRequestBuilder prepareEclEvaluation(String expression) {
		return new SnomedEclEvaluationRequestBuilder(expression);
	}
	
	public static SnomedQlEvaluationRequestBuilder prepareQlEvaluation(String expression) {
		return new SnomedQlEvaluationRequestBuilder(expression);
	}
	
	/**
	 * Returns the central class that provides access the server's SNOMED CT identifier services.
	 * @return central SNOMED CT identifier service client
	 */
	public static Identifiers identifiers() {
		return new Identifiers();
	}
	
	/**
	 * Returns the central class that provides access to the SNOMED CT RF2 services.
	 * @return central SNOMED CT RF2 client
	 */
	public static SnomedRf2Requests rf2() {
		return new SnomedRf2Requests();
	}

	/**
	 * Returns the central class that provides access to IO services, turning the SNOMED CT content to delimiter separated values (DSV), and vice versa.
	 * @return central SNOMED CT RF2 client
	 */
	public static SnomedDSVRequests dsv() {
		return new SnomedDSVRequests();
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the evaluation of an Query type reference set.
	 * @return SNOMED CT Query type reference set evaluation request builder
	 */
	public static QueryRefSetEvaluationRequestBuilder prepareQueryRefSetEvaluation(String referenceSetId) {
		return new QueryRefSetEvaluationRequestBuilder().setReferenceSetId(referenceSetId);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare the evaluation of Query type reference set members.
	 * @return SNOMED CT Query type reference set members evaluation request builder
	 */
	public static QueryRefSetMemberEvaluationRequestBuilder prepareQueryRefSetMemberEvaluation(String memberId) {
		return new QueryRefSetMemberEvaluationRequestBuilder().setMemberId(memberId);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating of Query type reference set members.
	 * @return SNOMED CT Query type reference set members update request builder
	 */
	public static QueryRefSetMemberUpdateRequestBuilder prepareUpdateQueryRefSetMember() {
		return new QueryRefSetMemberUpdateRequestBuilder();
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating of a Query type reference set.
	 * @return SNOMED CT Query type reference set update request builder
	 */
	public static QueryRefSetUpdateRequestBuilder prepareUpdateQueryRefSet() {
		return new QueryRefSetUpdateRequestBuilder();
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating of a single reference set member.
	 * @return SNOMED CT reference set member update request builder
	 */
	public static SnomedRefSetMemberUpdateRequestBuilder prepareUpdateMember() {
		return new SnomedRefSetMemberUpdateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare the updating of a single MRCM attribute constraint.
	 * @return SNOMED CT constraint update request builder
	 */
	public static SnomedConstraintUpdateRequestBuilder prepareUpdateConstraint() {
		return new SnomedConstraintUpdateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare the updating a concept.
	 * @param concept id of the concept to be updated
	 * @return SNOMED CT concept update request builder
	 */
	public static SnomedConceptUpdateRequestBuilder prepareUpdateConcept(String componentId) {
		return new SnomedConceptUpdateRequestBuilder(componentId);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating a description.
	 * @param description id of the description to be updated
	 * @return SNOMED CT description update request builder
	 */
	public static SnomedDescriptionUpdateRequestBuilder prepareUpdateDescription(String componentId) {
		return new SnomedDescriptionUpdateRequestBuilder(componentId);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating a relationship.
	 * @param relationship id of the relationship to be updated
	 * @return SNOMED CT relationship update request builder
	 */
	public static SnomedRelationshipUpdateRequestBuilder prepareUpdateRelationship(String componentId) {
		return new SnomedRelationshipUpdateRequestBuilder(componentId);
	}

	/**
	 * Returns a SNOMED CT Commit request builder to prepare a custom commit in a SNOMED CT repository.
	 * @return SNOMED CT specific commit request builder
	 */
	public static SnomedRepositoryCommitRequestBuilder prepareCommit() {
		return new SnomedRepositoryCommitRequestBuilder();
	}

	/**
	 * Returns all SYNONYM concept ids including all subtypes of {@value Concepts#SYNONYM}.
	 * @return
	 */
	public static SnomedConceptSearchRequestBuilder prepareGetSynonyms() {
		return prepareSearchConcept().all().filterByActive(true).filterByEcl("<<"+Concepts.SYNONYM);
	}

	/**
	 * Returns all applicable predicates for the given selfIds, ruleParentIds (+fetched ancestorIds), refSetIds. 
	 * @param branch - the branch to query for the constraints
	 * @param selfIds - set of SNOMED CT identifiers to use for getting self rules
	 * @param ruleParentIds - set of parent IDs to use for getting the descendant rules, also fetches and applies all ancestors of these
	 * @param refSetIds - reference set identifiers to match
	 * @param relationshipKeys - relationship keys (in "type=value" format) to match
	 * @return
	 */
	public static Promise<Collection<SnomedConstraint>> prepareGetApplicablePredicates(final String branch, 
			final Set<String> selfIds, 
			final Set<String> ruleParentIds, 
			final Set<String> refSetIds,
			final Set<String> relationshipKeys) {
		// query constraint domains three times, on for each concept domain set
		final IEventBus bus = ApplicationContext.getInstance().getService(IEventBus.class);
		return SnomedRequests.prepareSearchConcept()
			.all()
			.filterByIds(ruleParentIds)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
			.execute(bus)
			.then(ruleParents -> {
				final Set<String> descendantDomainIds = newHashSet();
				for (SnomedConcept ruleParent : ruleParents) {
					descendantDomainIds.add(ruleParent.getId());
					// add parents and ancestors of the rule parent concept as well
					descendantDomainIds.addAll(SnomedConcept.GET_ANCESTORS.apply(ruleParent));
				}
				return descendantDomainIds;				
			})
			.thenWith(descendantDomainIds -> {
				final BulkRequestBuilder<BranchContext> constraintBulkRequestBuilder = BulkRequest.<BranchContext>create();

				if (!CompareUtils.isEmpty(selfIds)) {
					constraintBulkRequestBuilder.add(SnomedRequests.prepareSearchConstraint().all().filterBySelfIds(selfIds));
				}
				
				if (!CompareUtils.isEmpty(ruleParentIds)) {
					constraintBulkRequestBuilder.add(SnomedRequests.prepareSearchConstraint().all().filterByChildIds(ruleParentIds));
				}
				
				if (!CompareUtils.isEmpty(descendantDomainIds)) {
					constraintBulkRequestBuilder.add(SnomedRequests.prepareSearchConstraint().all().filterByDescendantIds(descendantDomainIds));
				}
				
				if (!CompareUtils.isEmpty(refSetIds)) {
					constraintBulkRequestBuilder.add(SnomedRequests.prepareSearchConstraint().all().filterByRefSetIds(refSetIds));
				}
				
				if (!CompareUtils.isEmpty(relationshipKeys)) {
					constraintBulkRequestBuilder.add(SnomedRequests.prepareSearchConstraint().all().filterByRelationshipKeys(relationshipKeys));
				}
				
				return RepositoryRequests.prepareBulkRead()
					.setBody(constraintBulkRequestBuilder)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
					.execute(bus);
			})
			.then(input -> ImmutableSet.copyOf(Iterables.concat(input.getResponses(SnomedConstraints.class))));
	}

}
