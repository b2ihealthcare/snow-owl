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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkResponse;
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
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.core.domain.refset.MemberChange;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @since 4.5
 */
public abstract class SnomedRequests {

	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;

	private SnomedRequests() {
	}
	
	public static SnomedConstraintSearchRequestBuilder prepareSearchConstraint() {
		return new SnomedConstraintSearchRequestBuilder(REPOSITORY_ID);
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

	/**
	 * Returns all applicable predicates for the given selfIds, ruleParentIds (+fetched ancestorIds), refSetIds. 
	 * @param branch - the branch to query for the constraints
	 * @param selfIds - set of SNOMED CT identifiers to use for getting self rules
	 * @param ruleParentIds - set of parent IDs to use for getting the descendant rules, also fetches and applies all ancestors of these
	 * @param refSetIds - optional reference set identifiers to match
	 * @return
	 */
	public static Promise<Collection<SnomedConstraintDocument>> prepareGetApplicablePredicates(final String branch, final Set<String> selfIds, final Set<String> ruleParentIds, final Set<String> refSetIds) {
		// query constraint domains three times, on for each concept domain set
		final IEventBus bus = ApplicationContext.getInstance().getService(IEventBus.class);
		return SnomedRequests.prepareSearchConcept()
			.all()
			.setComponentIds(ruleParentIds)
			.build(branch)
			.execute(bus)
			.then(new Function<SnomedConcepts, Set<String>>() {
				@Override
				public Set<String> apply(SnomedConcepts input) {
					final Set<String> descendantDomainIds = newHashSet();
					for (ISnomedConcept concept : input) {
						descendantDomainIds.add(concept.getId());
						// add parents and ancestors of the concept as well
						descendantDomainIds.addAll(ISnomedConcept.GET_ANCESTORS.apply(concept));
					}
					return descendantDomainIds;
				}
			})
			.then(new Function<Set<String>, Collection<SnomedConstraintDocument>>() {
				@Override
				public Collection<SnomedConstraintDocument> apply(Set<String> descendantDomainIds) {
					return SnomedRequests.prepareBulkRead()
						.setBody(BulkRequest.<BranchContext>create()
								.add(SnomedRequests.prepareSearchConstraint().filterBySelfIds(selfIds))
								.add(SnomedRequests.prepareSearchConstraint().filterByDescendantIds(descendantDomainIds))
								.add(SnomedRequests.prepareSearchConstraint().filterByRefSetIds(refSetIds)))
						.build(branch)
						.execute(bus)
						.then(new Function<BulkResponse, Collection<SnomedConstraintDocument>>() {
							@Override
							public Collection<SnomedConstraintDocument> apply(BulkResponse input) {
								return ImmutableSet.copyOf(Iterables.concat(input.getResponses(SnomedConstraints.class)));
							}
						})
						.getSync();
				}
			});
	}

	
	
}
