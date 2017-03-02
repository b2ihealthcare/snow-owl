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

import org.eclipse.emf.ecore.EObject;

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
	
	public static SnomedConceptSearchRequestBuilder prepareSearchConcept() {
		return new SnomedConceptSearchRequestBuilder();
	}
	
	public static SnomedDescriptionSearchRequestBuilder prepareSearchDescription() {
		return new SnomedDescriptionSearchRequestBuilder();
	}
	
	public static SnomedRefSetSearchRequestBuilder prepareSearchRefSet() {
		return new SnomedRefSetSearchRequestBuilder();
	}

	public static SnomedRefSetMemberSearchRequestBuilder prepareSearchMember() {
		return new SnomedRefSetMemberSearchRequestBuilder();
	}

	public static SnomedRelationshipSearchRequestBuilder prepareSearchRelationship() {
		return new SnomedRelationshipSearchRequestBuilder();
	}
	
	public static SnomedConceptGetRequestBuilder prepareGetConcept(String conceptId) {
		return new SnomedConceptGetRequestBuilder(conceptId);
	}
	
	public static SnomedDescriptionGetRequestBuilder prepareGetDescription(String descriptionId) {
		return new SnomedDescriptionGetRequestBuilder(descriptionId);
	}
	
	public static SnomedRelationshipGetRequestBuilder prepareGetRelationship(String relationshipId) {
		return new SnomedRelationshipGetRequestBuilder(relationshipId);
	}

	private static DeleteRequestBuilder prepareDelete(String componentId, Class<? extends EObject> type) {
		return new DeleteRequestBuilder(componentId, type);
	}
	
	public static DeleteRequestBuilder prepareDeleteMember(String memberId) {
		return prepareDelete(memberId, SnomedRefSetMember.class);
	}

	public static DeleteRequestBuilder prepareDeleteConcept(String conceptId) {
		return prepareDelete(conceptId, Concept.class);
	}
	
	public static DeleteRequestBuilder prepareDeleteDescription(String descriptionId) {
		return prepareDelete(descriptionId, Description.class);
	}
	
	public static DeleteRequestBuilder prepareDeleteRelationship(String relationshipId) {
		return prepareDelete(relationshipId, Relationship.class);
	}
	
	public static DeleteRequestBuilder prepareDeleteReferenceSet(String refSetId) {
		return prepareDelete(refSetId, SnomedRefSet.class);
	}

	public static SnomedRefSetMemberCreateRequestBuilder prepareNewMember() {
		return new SnomedRefSetMemberCreateRequestBuilder();
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
	
	public static SnomedRefSetGetRequestBuilder prepareGetReferenceSet(String referenceSetId) {
		return new SnomedRefSetGetRequestBuilder(referenceSetId);
	}
	
	public static SnomedEclEvaluationRequestBuilder prepareEclEvaluation(String expression) {
		return new SnomedEclEvaluationRequestBuilder(expression);
	}
	
	public static Branching branching() {
		return RepositoryRequests.branching();
	}
	
	public static Merging merging() {
		return RepositoryRequests.merging();
	}

	public static Reviews review() {
		return RepositoryRequests.reviews();
	}
	
	public static Identifiers identifiers() {
		return new Identifiers();
	}

	public static QueryRefSetEvaluationRequestBuilder prepareQueryRefSetEvaluation(String referenceSetId) {
		return new QueryRefSetEvaluationRequestBuilder().setReferenceSetId(referenceSetId);
	}
	
	public static QueryRefSetMemberEvaluationRequestBuilder prepareQueryRefSetMemberEvaluation(String memberId) {
		return new QueryRefSetMemberEvaluationRequestBuilder().setMemberId(memberId);
	}

	public static QueryRefSetMemberUpdateRequestBuilder prepareUpdateQueryRefSetMember() {
		return new QueryRefSetMemberUpdateRequestBuilder();
	}

	public static SnomedRefSetMemberChangeRequestBuilder prepareMemberChangeRequest(MemberChange change, String moduleId, String referenceSetId) {
		return new SnomedRefSetMemberChangeRequestBuilder(change, moduleId, referenceSetId);
	}

	public static QueryRefSetUpdateRequestBuilder prepareUpdateQueryRefSet() {
		return new QueryRefSetUpdateRequestBuilder();
	}

	public static SnomedRefSetMemberUpdateRequestBuilder prepareUpdateMember() {
		return new SnomedRefSetMemberUpdateRequestBuilder();
	}

	public static SnomedConceptUpdateRequestBuilder prepareUpdateConcept(String componentId) {
		return new SnomedConceptUpdateRequestBuilder(componentId);
	}

	public static SnomedDescriptionUpdateRequestBuilder prepareUpdateDescription(String componentId) {
		return new SnomedDescriptionUpdateRequestBuilder(componentId);
	}

	public static SnomedRelationshipUpdateRequestBuilder prepareUpdateRelationship(String componentId) {
		return new SnomedRelationshipUpdateRequestBuilder(componentId);
	}

	public static SnomedRefSetMemberGetRequestBuilder prepareGetMember(String memberId) {
		return new SnomedRefSetMemberGetRequestBuilder(memberId);
	}

	public static SnomedRepositoryCommitRequestBuilder prepareCommit() {
		return new SnomedRepositoryCommitRequestBuilder();
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
			.filterByIds(ruleParentIds)
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
					
					return RepositoryRequests.prepareBulkRead()
						.setBody(constraintBulkRequestBuilder)
						.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
						.execute(bus)
						.then(input -> ImmutableSet.copyOf(Iterables.concat(input.getResponses(SnomedConstraints.class))))
						.getSync();
				}
			});
	}

	
	
}
