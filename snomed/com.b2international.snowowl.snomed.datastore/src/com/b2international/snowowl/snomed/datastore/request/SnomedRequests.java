/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.ALL_PRECOORDINATED_CONTENT;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields.ID;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields.MRCM_RULE_REFSET_ID;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields.MRCM_CONTENT_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields.MRCM_RANGE_CONSTRAINT;

import java.util.*;
import java.util.stream.Collectors;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.DeleteRequestBuilder;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.cis.Identifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.ecl.SnomedEclEvaluationRequestBuilder;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.dsv.SnomedDSVRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.SnomedRf2Requests;

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
 * // AuthorizedEventBus instance could be required in scenarios where an authentication token is required to execute the request
 * bus = new AuthorizedEventBus(bus, ImmutableMap.of("Authorization", authorizationToken));
 * // the token can be `Basic Base64.encode("user:pass") or a JWT APIkey provdided by the server`
 * 
 * //Search for concepts matching the an ECL expression (DescendantsOrSelfOf: 'Clinical finding')
 * SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
 *  .filterByEcl('<<404684003')
 *  .all()
 *  .build(path) // path expression or ResourceURI 
 *  .execute(bus)
 *  .getSync(); // blocks until the request completes
 *  
 * </code></pre>
 *  
 * @since 4.5
 * @see RepositoryRequests
 */
public abstract class SnomedRequests {

	private SnomedRequests() {
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

	private static DeleteRequestBuilder prepareDelete(String componentId, Class<? extends RevisionDocument> type) {
		return new SnomedDeleteRequestBuilder(componentId, type);
	}
		
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a concept.
	 * @param conceptId - the identifier of the concept
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given concept
	 */
	public static DeleteRequestBuilder prepareDeleteConcept(String conceptId) {
		return prepareDelete(conceptId, SnomedConceptDocument.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a description.
	 * @param descriptionId - the identifier of the description
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given description
	 */
	public static DeleteRequestBuilder prepareDeleteDescription(String descriptionId) {
		return prepareDelete(descriptionId, SnomedDescriptionIndexEntry.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a relationship.
	 * @param relationshipId - the identifier of the relationship
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given relationship
	 */
	public static DeleteRequestBuilder prepareDeleteRelationship(String relationshipId) {
		return prepareDelete(relationshipId, SnomedRelationshipIndexEntry.class);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a reference set.
	 * @param refSetId - the identifier of the reference set
	 * @param force 
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given reference set
	 */
	public static SnomedTransactionalRequestBuilder<Boolean> prepareDeleteReferenceSet(String refSetId, boolean force) {
		return prepareUpdateConcept(refSetId).clearRefSet(force);
	}
	
	/**
	 * Returns a SNOMED CT request builder to prepare a request that deletes a reference set member.
	 * @param memberId - the identifier of the member
	 * @return a {@link DeleteRequestBuilder} that can build a {@link Request} to delete the given reference set member
	 */
	public static DeleteRequestBuilder prepareDeleteMember(String memberId) {
		return prepareDelete(memberId, SnomedRefSetMemberIndexEntry.class);
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
	 * Returns a SNOMED CT request builder to prepare the evaluation of an Expression Constraint Language (ECL) expression.
	 * @param expression - the ECL expression to evaluate
	 * @return SNOMED CT ECL evaluation request builder
	 */
	public static SnomedEclEvaluationRequestBuilder prepareEclEvaluation(String expression) {
		return new SnomedEclEvaluationRequestBuilder(expression);
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
	 * @return central SNOMED CT DSV client
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
	 * Returns a SNOMED CT request builder to prepare the updating of a Map type reference set.
	 * @return SNOMED CT Map type reference set update request builder
	 */
	public static MapTypeRefSetUpdateRequestBuilder prepareUpdateMapTypeRefSet() {
		return new MapTypeRefSetUpdateRequestBuilder();
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
	 * @param memberId - the member to update
	 * @return SNOMED CT reference set member update request builder
	 */
	public static SnomedRefSetMemberUpdateRequestBuilder prepareUpdateMember(String memberId) {
		return new SnomedRefSetMemberUpdateRequestBuilder(memberId);
	}
		
	/**
	 * Returns a SNOMED CT request builder to prepare the updating a concept.
	 * @param componentId - id of the concept to be updated
	 * @return SNOMED CT concept update request builder
	 */
	public static SnomedConceptUpdateRequestBuilder prepareUpdateConcept(String componentId) {
		return new SnomedConceptUpdateRequestBuilder(componentId);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating a description.
	 * @param componentId - description id of the description to be updated
	 * @return SNOMED CT description update request builder
	 */
	public static SnomedDescriptionUpdateRequestBuilder prepareUpdateDescription(String componentId) {
		return new SnomedDescriptionUpdateRequestBuilder(componentId);
	}

	/**
	 * Returns a SNOMED CT request builder to prepare the updating a relationship.
	 * @param componentId - relationship id of the relationship to be updated
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
	
	public static Promise<Collection<String>> getApplicableTypes(final IEventBus bus, final String resourcePath, 
			final Set<String> selfIds, 
			final Set<String> ruleParentIds, 
			final Set<String> refSetIds,
			final List<String> moduleIds,
			boolean needsDataAttributes,
			boolean needsObjectAttributes) {
		return SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByRefSetType(SnomedRefSetType.MRCM_MODULE_SCOPE)
			.filterByReferencedComponent(moduleIds)
			.setFields(ID, MRCM_RULE_REFSET_ID)
			.build(resourcePath)
			.execute(bus)
			.then(members -> members.stream()
					.map(m -> (String) m.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID))
					.collect(Collectors.toSet()))
			.thenWith(inScopeRefSetIds -> {
				Set<String> domainIds = new HashSet<>();
				
				if (selfIds != null) {
					domainIds.addAll(selfIds);
				}
				
				if (ruleParentIds != null) {
					domainIds.addAll(ruleParentIds);
				}
				
				if (refSetIds != null) {
					domainIds.addAll(refSetIds);
				}
				
				return SnomedRequests.prepareSearchMember()
					.all()
					.filterByActive(true)
					.filterByRefSetType(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN)
					.filterByRefSet(inScopeRefSetIds)
					.filterByProps(Options.from(Map.of(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, domainIds)))
					.setFields(REFERENCED_COMPONENT_ID, ID)
					.build(resourcePath)
					.execute(bus);
			}).thenWith(members -> {
				Set<String> typeIds = members.stream().map(m -> m.getReferencedComponentId()).collect(Collectors.toSet());
				if ((needsDataAttributes && needsObjectAttributes) || typeIds.isEmpty()) {
					return Promise.immediate(typeIds);
				}
				
				SnomedConceptSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchConcept()
						.filterByActive(true)
						.filterByIds(typeIds)
						.setFields(SnomedConceptDocument.Fields.ID);
				
				if (needsDataAttributes && !needsObjectAttributes) {
					requestBuilder.filterByAncestor(SnomedConstants.Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE);
				} else if (!needsDataAttributes && needsObjectAttributes) {
					requestBuilder.filterByAncestor(SnomedConstants.Concepts.CONCEPT_MODEL_OBJECT_ATTRIBUTE);
				} else {
					//If the boolean flags are not different from each other include both types of attributes
				}
				
				return requestBuilder.build(resourcePath).execute(bus)
						.then(types -> types.stream().map(SnomedConcept::getId).toList());
			});
	}
	
	public static Promise<SnomedReferenceSetMembers> getApplicableRanges(final IEventBus bus, 
			final String resourcePath, 
			final Set<String> selfIds,
			final Set<String> ruleParentIds, 
			final Set<String> refSetIds,
			final List<String> moduleIds,
			boolean needsDataAttributes,
			boolean needsObjectAttributes) {
		return getApplicableTypes(bus, resourcePath, selfIds, ruleParentIds, refSetIds, moduleIds, needsDataAttributes, needsObjectAttributes)
				.thenWith(typeIds -> {
					
					if (typeIds.isEmpty()) {
						return Promise.immediate(new SnomedReferenceSetMembers(0, 0));
					}
					
					return SnomedRequests.prepareSearchMember()
							.all()
							.filterByActive(true)
							.filterByRefSetType(SnomedRefSetType.MRCM_ATTRIBUTE_RANGE)
							.filterByReferencedComponent(typeIds)
							.setFields(ID, REFERENCED_COMPONENT_ID, MRCM_RANGE_CONSTRAINT, MRCM_CONTENT_TYPE_ID)
							.build(resourcePath)
							.execute(bus)
							.then(members -> {
								Map<String, SnomedReferenceSetMember> rangeConstraintMembers = new HashMap<>();
								members.forEach( m -> {
									String contentType = (String) m.getProperties().get(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID);
									if (!rangeConstraintMembers.containsKey(m.getReferencedComponentId()) || ALL_PRECOORDINATED_CONTENT.equals(contentType)) {
										rangeConstraintMembers.put(m.getReferencedComponentId(), m);
									}
								});
								
								return new SnomedReferenceSetMembers(List.copyOf(rangeConstraintMembers.values()), null, rangeConstraintMembers.size(), rangeConstraintMembers.size());
							});
				});
	}
	
}
