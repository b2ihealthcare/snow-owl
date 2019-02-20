/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.request;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.CommitResult;
import com.b2international.snowowl.datastore.request.Locks;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.assigner.DefaultNamespaceAndModuleAssigner;
import com.b2international.snowowl.snomed.datastore.id.assigner.SnomedNamespaceAndModuleAssigner;
import com.b2international.snowowl.snomed.datastore.id.assigner.SnomedNamespaceAndModuleAssignerProvider;
import com.b2international.snowowl.snomed.datastore.request.IdRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationTracker;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.ConcreteDomainChange;
import com.b2international.snowowl.snomed.reasoner.domain.ConcreteDomainChanges;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSet;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSets;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerConcreteDomainMember;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerRelationship;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChange;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChanges;
import com.b2international.snowowl.snomed.reasoner.equivalence.IEquivalentConceptMerger;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerApiException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

/**
 * Represents a request that saves pre-recorded changes of a classification,
 * usually running in a remote job.
 * 
 * @since 6.11 (originally introduced on 7.0)
 */
final class SaveJobRequest implements Request<BranchContext, Boolean> {

	private static final Logger LOG = LoggerFactory.getLogger("reasoner");

	private static final int SCROLL_LIMIT = 10_000;
	private static final String SCROLL_KEEP_ALIVE = "5m";

	@NotEmpty
	private String classificationId;

	@NotEmpty
	private String userId;

	@NotNull
	private String parentLockContext;

	@NotEmpty
	private String commitComment;

	// @Nullable
	private String moduleId;

	// @Nullable
	private String namespace;
	
	private boolean fixEquivalences;
	
	private boolean handleConcreteDomains;
	
	SaveJobRequest() {}
	
	void setClassificationId(final String classificationId) {
		this.classificationId = classificationId;
	}
	
	void setUserId(final String userId) {
		this.userId = userId;
	}
	
	void setParentLockContext(final String parentLockContext) {
		this.parentLockContext = parentLockContext;
	}
	
	void setCommitComment(final String commitComment) {
		this.commitComment = commitComment;
	}
	
	void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	void setFixEquivalences(boolean fixEquivalences) {
		this.fixEquivalences = fixEquivalences;
	}
	
	void setHandleConcreteDomains(boolean handleConcreteDomains) {
		this.handleConcreteDomains = handleConcreteDomains;
	}
	
	@Override
	public Boolean execute(final BranchContext context) {
		final IProgressMonitor monitor = context.service(IProgressMonitor.class);
		final Branch branch = context.branch();
		final ClassificationTracker tracker = context.service(ClassificationTracker.class);

		try (Locks locks = new Locks(context, userId, DatastoreLockContextDescriptions.SAVE_CLASSIFICATION_RESULTS, parentLockContext, branch)) {
			return persistChanges(context, monitor);
		} catch (final OperationLockException e) {
			tracker.classificationFailed(classificationId);
			throw new ReasonerApiException("Couldn't acquire exclusive access to terminology store for persisting classification changes; %s", e.getMessage(), e);
		} catch (final InterruptedException e) {
			tracker.classificationFailed(classificationId);
			throw new ReasonerApiException("Thread interrupted while acquiring exclusive access to terminology store for persisting classification changes.", e);
		} catch (final Exception e) {
			tracker.classificationSaveFailed(classificationId);
			throw new ReasonerApiException("Error while persisting classification changes on '%s'.", context.branchPath(), e);
		} finally {
			monitor.done();
		}
	}

	private Boolean persistChanges(final BranchContext context, final IProgressMonitor monitor) {
		// Repeat the same checks as in ClassificationSaveRequest, now within the lock
		final ClassificationTask classification = ClassificationRequests.prepareGetClassification(classificationId)
				.build()
				.execute(context);

		final String branchPath = classification.getBranch();
		final Branch branch = RepositoryRequests.branching()
				.prepareGet(branchPath)
				.build()
				.execute(context);

		if (!ClassificationSaveRequest.SAVEABLE_STATUSES.contains(classification.getStatus())) {
			throw new BadRequestException("Classification '%s' is not in the expected state to start saving changes.", classificationId);
		}

		if (classification.getTimestamp() < branch.headTimestamp()) {
			throw new BadRequestException("Classification '%s' on branch '%s' is stale (classification timestamp: %s, head timestamp: %s).", 
					classificationId,
					branchPath,
					classification.getTimestamp(),
					branch.headTimestamp());
		}

		final ClassificationTracker classificationTracker = context.service(ClassificationTracker.class);

		// Signal the state change
		classificationTracker.classificationSaving(classificationId);

		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Persisting changes", 6);
		final BulkRequestBuilder<TransactionContext> bulkRequestBuilder = BulkRequest.create();

		applyChanges(subMonitor, context, bulkRequestBuilder);

		final Request<BranchContext, CommitResult> commitRequest = SnomedRequests.prepareCommit()
			.setBody(bulkRequestBuilder)
			.setCommitComment(commitComment)
			.setParentContextDescription(DatastoreLockContextDescriptions.SAVE_CLASSIFICATION_RESULTS)
			.setUserId(userId)
			.build();

		final CommitResult commitResult = new IdRequest<>(commitRequest)  
			.execute(context);

		classificationTracker.classificationSaved(classificationId, commitResult.getCommitTimestamp());
		return Boolean.TRUE;
	}

	private void applyChanges(final SubMonitor subMonitor, 
			final BranchContext context,
			final BulkRequestBuilder<TransactionContext> bulkRequestBuilder) {

		final SnomedNamespaceAndModuleAssigner assigner = createNamespaceAndModuleAssigner(context);
		final Set<String> conceptIdsToSkip = mergeEquivalentConcepts(context, bulkRequestBuilder);
		applyRelationshipChanges(context, bulkRequestBuilder, assigner, conceptIdsToSkip);

		if (handleConcreteDomains) {
			// CD member support in configuration overrides the flag on the save request
			final SnomedCoreConfiguration snomedCoreConfiguration = context.service(SnomedCoreConfiguration.class);
			if (snomedCoreConfiguration.isConcreteDomainSupported()) {
				applyConcreteDomainChanges(context, bulkRequestBuilder, assigner, conceptIdsToSkip);
			}
		}
	}

	private void applyRelationshipChanges(final BranchContext context, 
			final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final Set<String> conceptIdsToSkip) {

		final RelationshipChangeSearchRequestBuilder relationshipRequestBuilder = ClassificationRequests.prepareSearchRelationshipChange()
				.setLimit(SCROLL_LIMIT)
				.setScroll(SCROLL_KEEP_ALIVE)
				.setExpand("relationship()")
				.filterByClassificationId(classificationId);

		final SearchResourceRequestIterator<RelationshipChangeSearchRequestBuilder, RelationshipChanges> relationshipIterator = 
				new SearchResourceRequestIterator<>(relationshipRequestBuilder, 
						r -> r.build().execute(context));

		while (relationshipIterator.hasNext()) {
			final RelationshipChanges nextChanges = relationshipIterator.next();

			final Set<String> conceptIds = nextChanges.stream()
					.filter(change -> ChangeNature.INFERRED.equals(change.getChangeNature()))
					.map(RelationshipChange::getRelationship)
					.map(ReasonerRelationship::getSourceId)
					.collect(Collectors.toSet());

			// Concepts which will be inactivated as part of equivalent concept merging should be excluded
			conceptIds.removeAll(conceptIdsToSkip);
			namespaceAndModuleAssigner.collectRelationshipNamespacesAndModules(conceptIds, context);

			for (final RelationshipChange change : nextChanges) {
				final ReasonerRelationship relationship = change.getRelationship();

				// Do not reference concepts which were handled by the equivalent concept merger
				if (conceptIdsToSkip.contains(relationship.getSourceId()) 
						|| conceptIdsToSkip.contains(relationship.getDestinationId())) {
					continue;
				}
				
				if (ChangeNature.INFERRED.equals(change.getChangeNature())) {
					addComponent(bulkRequestBuilder, namespaceAndModuleAssigner, relationship);
				} else {
					removeOrDeactivate(bulkRequestBuilder, relationship);
				}
			}
		}

		namespaceAndModuleAssigner.clear();
	}

	private void addComponent(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final ReasonerRelationship relationship) {

		final String sourceId = relationship.getSourceId();
		final String moduleId = namespaceAndModuleAssigner.getRelationshipModuleId(sourceId);
		final String namespace = namespaceAndModuleAssigner.getRelationshipNamespace(sourceId);

		final SnomedRelationshipCreateRequestBuilder createRequest = SnomedRequests.prepareNewRelationship()
				.setIdFromNamespace(namespace)
				.setTypeId(relationship.getTypeId())
				.setActive(true)
				.setCharacteristicType(relationship.getCharacteristicType())
				.setSourceId(relationship.getSourceId())
				.setDestinationId(relationship.getDestinationId())
				.setDestinationNegated(relationship.isDestinationNegated())
				.setGroup(relationship.getGroup())
				.setUnionGroup(relationship.getUnionGroup())
				.setModifier(relationship.getModifier())
				.setModuleId(moduleId);

		bulkRequestBuilder.add(createRequest);
	}

	private void removeOrDeactivate(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, 
			final ReasonerRelationship relationship) {

		final Request<TransactionContext, Boolean> request;

		if (relationship.isReleased()) {
			request = SnomedRequests
					.prepareUpdateRelationship(relationship.getOriginId())
					.setActive(false)
					.build();
		} else {
			request = SnomedRequests
					.prepareDeleteRelationship(relationship.getOriginId())
					.build();
		}

		bulkRequestBuilder.add(request);
	}

	private void applyConcreteDomainChanges(final BranchContext context, 
			final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final Set<String> conceptIdsToSkip) {

		final ConcreteDomainChangeSearchRequestBuilder concreteDomainRequestBuilder = ClassificationRequests.prepareSearchConcreteDomainChange()
				.setLimit(SCROLL_LIMIT)
				.setScroll(SCROLL_KEEP_ALIVE)
				.setExpand("concreteDomainMember()")
				.filterByClassificationId(classificationId);

		final SearchResourceRequestIterator<ConcreteDomainChangeSearchRequestBuilder, ConcreteDomainChanges> concreteDomainIterator =
				new SearchResourceRequestIterator<>(concreteDomainRequestBuilder, 
						r -> r.build().execute(context));

		while (concreteDomainIterator.hasNext()) {
			final ConcreteDomainChanges nextChanges = concreteDomainIterator.next();

			final Set<String> conceptIds = nextChanges.stream()
					.filter(c -> ChangeNature.INFERRED.equals(c.getChangeNature()))
					.map(ConcreteDomainChange::getConcreteDomainMember)
					.map(m -> m.getReferencedComponentId())
					.collect(Collectors.toSet());

			// Concepts which will be inactivated as part of equivalent concept merging should be excluded
			conceptIds.removeAll(conceptIdsToSkip);
			namespaceAndModuleAssigner.collectConcreteDomainModules(conceptIds, context);

			for (final ConcreteDomainChange change : nextChanges) {
				final ReasonerConcreteDomainMember referenceSetMember = change.getConcreteDomainMember();

				// Do not reference concepts which were handled by the equivalent concept merger
				if (conceptIdsToSkip.contains(referenceSetMember.getReferencedComponentId())) { 
					continue;
				}

				if (ChangeNature.INFERRED.equals(change.getChangeNature())) {
					addComponent(bulkRequestBuilder, namespaceAndModuleAssigner, referenceSetMember);
				} else {
					removeOrDeactivate(bulkRequestBuilder, referenceSetMember);
				}
			}
		}

		namespaceAndModuleAssigner.clear();
	}

	private void addComponent(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final ReasonerConcreteDomainMember member) {

		final String referencedComponentId = member.getReferencedComponentId();
		final String moduleId = namespaceAndModuleAssigner.getConcreteDomainModuleId(referencedComponentId);

		final Request<TransactionContext, String> createRequest = SnomedRequests.prepareNewMember()
				.setActive(true)
				.setModuleId(moduleId)
				.setReferencedComponentId(referencedComponentId)
				.setReferenceSetId(member.getReferenceSetId())
				.setProperties(ImmutableMap.of(
						SnomedRf2Headers.FIELD_TYPE_ID, member.getTypeId(),
						SnomedRf2Headers.FIELD_VALUE, member.getSerializedValue(),
						SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, member.getGroup(),
						SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, member.getCharacteristicTypeId()))
				.build();

		bulkRequestBuilder.add(createRequest);
	}

	private void removeOrDeactivate(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, 
			final ReasonerConcreteDomainMember concreteDomain) {
		
		final Request<TransactionContext, Boolean> request;

		if (concreteDomain.isReleased()) {
			request = SnomedRequests
					.prepareUpdateMember()
					.setMemberId(concreteDomain.getOriginMemberId())
					.setSource(ImmutableMap.of(SnomedRf2Headers.FIELD_ACTIVE, false))
					.build();
		} else {
			request = SnomedRequests
					.prepareDeleteMember(concreteDomain.getOriginMemberId())
					.build();
		}

		bulkRequestBuilder.add(request);
	}

	private Set<String> mergeEquivalentConcepts(final BranchContext context, final BulkRequestBuilder<TransactionContext> bulkRequestBuilder) {
		if (!fixEquivalences) {
			return Collections.emptySet();
		}
		
		// XXX: Restrict merging to active components only
		final String expand = "equivalentConcepts(expand("
				+ "members(active:true),"
				+ "relationships(active:true),"
				+ "inboundRelationships(active:true)))";
		
		final EquivalentConceptSetSearchRequestBuilder equivalentConceptRequest = ClassificationRequests.prepareSearchEquivalentConceptSet()
				.setLimit(SCROLL_LIMIT)
				.setScroll(SCROLL_KEEP_ALIVE)
				.setExpand(expand)
				.filterByClassificationId(classificationId);

		final SearchResourceRequestIterator<EquivalentConceptSetSearchRequestBuilder, EquivalentConceptSets> equivalentConceptIterator = 
				new SearchResourceRequestIterator<>(equivalentConceptRequest, 
						r -> r.build().execute(context));

		// Are there any equivalent concepts present?
		if (!equivalentConceptIterator.hasNext()) {
			return Collections.emptySet();
		}
		
		final Multimap<SnomedConcept, SnomedConcept> equivalentConcepts = HashMultimap.create();

		while (equivalentConceptIterator.hasNext()) {
			final EquivalentConceptSets nextBatch = equivalentConceptIterator.next();

			for (final EquivalentConceptSet equivalentSet : nextBatch) {
				if (equivalentSet.isUnsatisfiable()) {
					continue;
				}

				final List<SnomedConcept> conceptsToRemove = newArrayList(equivalentSet.getEquivalentConcepts());
				final SnomedConcept conceptToKeep = conceptsToRemove.remove(0);
				equivalentConcepts.putAll(conceptToKeep, conceptsToRemove);
			}
		}

		// Were all equivalent concepts unsatisfiable?
		if (equivalentConcepts.isEmpty()) {
			return Collections.emptySet();
		}
		
		final IEquivalentConceptMerger merger = Extensions.getFirstPriorityExtension(
				IEquivalentConceptMerger.EXTENSION_POINT, 
				IEquivalentConceptMerger.class);
		
		final String mergerName = merger.getClass().getSimpleName();
		LOG.info("Reasoner service will use {} for equivalent concept merging.", mergerName);
		return merger.merge(equivalentConcepts, bulkRequestBuilder);
	}

	private SnomedNamespaceAndModuleAssigner createNamespaceAndModuleAssigner(final BranchContext context) {
		final SnomedNamespaceAndModuleAssigner assigner;
		if (namespace != null || moduleId != null) {
			assigner = new DefaultNamespaceAndModuleAssigner(namespace, moduleId);
		} else {
			assigner = context.service(SnomedNamespaceAndModuleAssignerProvider.class).get();
		}

		final String assignerName = assigner.getClass().getSimpleName();
		LOG.info("Reasoner service will use {} for relationship/concrete domain namespace and module assignment.", assignerName);
		return assigner;
	}
}
