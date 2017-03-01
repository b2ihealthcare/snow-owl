/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.request.Branching;
import com.b2international.snowowl.datastore.request.DeleteRequestBuilder;
import com.b2international.snowowl.datastore.request.Merging;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.Reviews;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.core.domain.refset.MemberChange;
import com.b2international.snowowl.snomed.core.ecl.SnomedEclEvaluationRequestBuilder;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * The central class of the SNOMED CT Java API provided by Snow Owl Terminology Server and Authoring Environment Runtime.
 * This class cannot be instantiated or subclassed by clients, all the functionality is accesible
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
 * @since 4.5
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
	 * Returns a SNOMED CT request builder to prepare a request to return a concept.
	 * @return SNOMED CT concept request builder
	 */
	public static SnomedConceptGetRequestBuilder prepareGetConcept() {
		return new SnomedConceptGetRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a description.
	 * @return SNOMED CT description request builder
	 */
	public static SnomedDescriptionGetRequestBuilder prepareGetDescription() {
		return new SnomedDescriptionGetRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a relationship.
	 * @return SNOMED CT relationship request builder
	 */
	public static SnomedRelationshipGetRequestBuilder prepareGetRelationship() {
		return new SnomedRelationshipGetRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a reference set.
	 * @return SNOMED CT reference set request builder
	 */
	public static SnomedRefSetGetRequestBuilder prepareGetReferenceSet() {
		return new SnomedRefSetGetRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request to return a reference set member.
	 * @return SNOMED CT reference set member request builder
	 */
	public static SnomedRefSetMemberGetRequestBuilder prepareGetMember() {
		return new SnomedRefSetMemberGetRequestBuilder();
	}

	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a generic component.
	 * @return SNOMED CT delete request builder
	 */
	private static DeleteRequestBuilder prepareDelete() {
		return new DeleteRequestBuilder(new SnomedRepositoryCommitRequestBuilder());
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
	 * Returns a SNOMED CT request builder to prepare a request that deletes a reference set.
	 * @return SNOMED CT delete reference set request builder
	 */
	public static DeleteRequestBuilder prepareDeleteReferenceSet() {
		return prepareDelete().setType(SnomedRefSet.class);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a reference set member.
	 * @return SNOMED CT create reference set member request builder
	 */
	public static SnomedRefSetMemberCreateRequestBuilder prepareNewMember() {
		return new SnomedRefSetMemberCreateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a reference set.
	 * @return SNOMED CT create reference set request builder
	 */
	public static SnomedRefSetCreateRequestBuilder prepareNewRefSet() {
		return new SnomedRefSetCreateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a concept.
	 * @return SNOMED CT create concept request builder
	 */
	public static SnomedConceptCreateRequestBuilder prepareNewConcept() {
		return new SnomedConceptCreateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a description.
	 * @return SNOMED CT create description request builder
	 */
	public static SnomedDescriptionCreateRequestBuilder prepareNewDescription() {
		return new SnomedDescriptionCreateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that creates a relationship.
	 * @return SNOMED CT create relationship request builder
	 */
	public static SnomedRelationshipCreateRequestBuilder prepareNewRelationship() {
		return new SnomedRelationshipCreateRequestBuilder();
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare the evaluation of an 
	 * Expression Constraint Language (ECL) expression.
	 * @return SNOMED CT create ECL evaluation request builder
	 */
	public static SnomedEclEvaluationRequestBuilder prepareEclEvaluation(String expression) {
		return new SnomedEclEvaluationRequestBuilder(expression);
	}
	
	/**
	 * Returns the central class that provides access the server's branching features.
	 * @return central branching class with access to branching features
	 */
	public static Branching branching() {
		return RepositoryRequests.branching();
	}
	
	/**
	 * Returns the central class that provides access the server's revision control
	 * merging features.
	 * @return central merging class with access to merging features
	 */
	public static Merging merging() {
		return RepositoryRequests.merging();
	}

	/**
	 * Returns the central class that provides access the server's review features
	 * @return central review class with access to review features
	 */
	public static Reviews review() {
		return RepositoryRequests.reviews();
	}
	
	/**
	 * Returns the central class that provides access the server's SNOMED CT identifier services.
	 * @return central SNOMED CT identifier service class
	 */
	public static Identifiers identifiers() {
		return new Identifiers();
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the evaluation of an 
	 * Query type reference set.
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
		return new QueryRefSetUpdateRequestBuilder();
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating reference set members.
	 * @return SNOMED CT reference set member update request builder
	 */
	public static SnomedRefSetMemberUpdateRequestBuilder prepareUpdateMember() {
		return new SnomedRefSetMemberUpdateRequestBuilder();
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

	public static SnomedRepositoryCommitRequestBuilder prepareCommit() {
		return new SnomedRepositoryCommitRequestBuilder();
	}

	public static SnomedRepositoryBulkReadRequestBuilder prepareBulkRead() {
		return new SnomedRepositoryBulkReadRequestBuilder();
	}

	/**
	 * Returns all SYNONYM concept ids including all subtypes of {@value Concepts#SYNONYM}.
	 * @return
	 */
	public static SnomedConceptSearchRequestBuilder prepareGetSynonyms() {
		return prepareSearchConcept().all().filterByActive(true).filterByEscg("<<"+Concepts.SYNONYM);
	}

	/**
	 * Returns all applicable predicates for the given selfIds, ruleParentIds (+fetched ancestorIds), refSetIds. 
	 * @param branch - the branch to query for the constraints
	 * @param selfIds - set of SNOMED CT identifiers to use for getting self rules
	 * @param ruleParentIds - set of parent IDs to use for getting the descendant rules, also fetches and applies all ancestors of these
	 * @param refSetIds - optional reference set identifiers to match
	 * @return
	 */
	public static Promise<Collection<SnomedConstraint>> prepareGetApplicablePredicates(final String branch, final Set<String> selfIds, final Set<String> ruleParentIds, final Set<String> refSetIds) {
		// query constraint domains three times, on for each concept domain set
		final IEventBus bus = ApplicationContext.getInstance().getService(IEventBus.class);
		return SnomedRequests.prepareSearchConcept()
			.all()
			.setComponentIds(ruleParentIds)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
			.execute(bus)
			.then(new Function<SnomedConcepts, Set<String>>() {
				@Override
				public Set<String> apply(SnomedConcepts input) {
					final Set<String> descendantDomainIds = newHashSet();
					for (SnomedConcept concept : input) {
						descendantDomainIds.add(concept.getId());
						// add parents and ancestors of the concept as well
						descendantDomainIds.addAll(SnomedConcept.GET_ANCESTORS.apply(concept));
					}
					return descendantDomainIds;
				}
			})
			.then(new Function<Set<String>, Collection<SnomedConstraint>>() {
				@Override
				public Collection<SnomedConstraint> apply(Set<String> descendantDomainIds) {
					final BulkRequestBuilder<BranchContext> constraintBulkRequestBuilder = BulkRequest.<BranchContext>create();

					if (!CompareUtils.isEmpty(selfIds)) {
						constraintBulkRequestBuilder.add(SnomedRequests.prepareSearchConstraint().all().filterBySelfIds(selfIds));
					}
					
					if (!CompareUtils.isEmpty(descendantDomainIds)) {
						constraintBulkRequestBuilder.add(SnomedRequests.prepareSearchConstraint().all().filterByDescendantIds(descendantDomainIds));
					}
					
					if (!CompareUtils.isEmpty(refSetIds)) {
						constraintBulkRequestBuilder.add(SnomedRequests.prepareSearchConstraint().all().filterByRefSetIds(refSetIds));
					}
					
					return SnomedRequests.prepareBulkRead()
						.setBody(constraintBulkRequestBuilder)
						.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
						.execute(bus)
						.then(input -> ImmutableSet.copyOf(Iterables.concat(input.getResponses(SnomedConstraints.class))))
						.getSync();
				}
			});
	}
	
}
