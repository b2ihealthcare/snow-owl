/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions.SAVE_CLASSIFICATION_RESULTS;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.request.CommitResult;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.assigner.SnomedNamespaceAndModuleAssigner;
import com.b2international.snowowl.snomed.datastore.id.assigner.SnomedNamespaceAndModuleAssignerProvider;
import com.b2international.snowowl.snomed.datastore.request.IdRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.ConcreteDomainChange;
import com.b2international.snowowl.snomed.reasoner.domain.ConcreteDomainChanges;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSet;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSets;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerRelationship;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChange;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChanges;
import com.b2international.snowowl.snomed.reasoner.equivalence.EquivalentConceptMerger;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerApiException;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationTracker;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Longs;

/**
 * @since 7.0
 */
final class SaveJobRequest implements Request<BranchContext, Boolean> {

	private static final Logger LOG = LoggerFactory.getLogger("reasoner");

	private static final long LOCK_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(5L);

	private static final int SCROLL_LIMIT = 10_000;
	private static final String SCROLL_KEEP_ALIVE = "5m";

	private static final long SMP_ROOT = Long.parseLong(Concepts.GENERATED_SINGAPORE_MEDICINAL_PRODUCT);

	@JsonProperty
	private final String classificationId;

	@JsonProperty
	private final String userId;

	private DatastoreLockContext lockContext;
	private IOperationLockTarget lockTarget;

	SaveJobRequest(final String classificationId, final String userId) {
		this.classificationId = classificationId;
		this.userId = userId;
	}

	@Override
	public Boolean execute(final BranchContext context) {
		final IProgressMonitor monitor = context.service(IProgressMonitor.class);

		try {
			lock(context);
			return persistChanges(context, monitor);
		} catch (final Exception e) {
			final ClassificationTracker classificationTracker = context.service(ClassificationTracker.class);
			classificationTracker.classificationSaveFailed(classificationId);
			throw new ReasonerApiException("Error while persisting classification changes on '%s'.", context.branchPath(), e);
		} finally {
			monitor.done();
			unlock(context);
		}
	}

	private void lock(final BranchContext context) {
		final DatastoreLockContext localLockContext = createLockContext(userId);
		final IOperationLockTarget localLockTarget = createLockTarget(BranchPathUtils.createPath(context.branchPath()));

		try {

			final IDatastoreOperationLockManager lockManager = context.service(IDatastoreOperationLockManager.class);
			lockManager.lock(localLockContext, LOCK_TIMEOUT_MILLIS, localLockTarget);
			lockContext = localLockContext;
			lockTarget = localLockTarget;

		} catch (OperationLockException | InterruptedException e) {
			if (e instanceof DatastoreOperationLockException) {
				final DatastoreOperationLockException lockException = (DatastoreOperationLockException) e;
				final DatastoreLockContext otherContext = lockException.getContext(localLockTarget);
				throw new DatastoreOperationLockException(getContextDescription(otherContext));
			} else {
				throw new DatastoreOperationLockException(getDefaultContextDescription());
			}
		}
	}

	private SingleRepositoryAndBranchLockTarget createLockTarget(final IBranchPath branchPath) {
		return new SingleRepositoryAndBranchLockTarget(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath);
	}

	private DatastoreLockContext createLockContext(final String userId) {
		return new DatastoreLockContext(userId, SAVE_CLASSIFICATION_RESULTS);
	}

	private String getDefaultContextDescription() {
		return "of concurrent activity";
	}

	private String getContextDescription(final DatastoreLockContext otherContext) {
		return String.format("%s is currently %s", otherContext.getUserId(), otherContext.getDescription());
	}

	private void unlock(final BranchContext context) {
		try {
			if (null != lockContext && null != lockTarget) {
				final IDatastoreOperationLockManager lockManager = context.service(IDatastoreOperationLockManager.class);
				lockManager.unlock(lockContext, lockTarget);
			}
		} finally {
			lockContext = null;
			lockTarget = null;
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
			throw new BadRequestException("Classification '%s' is stale (recorded timestamp: %s, current timestamp of branch '%s': %s).", 
					classificationId, 
					classification.getTimestamp(),
					branchPath,
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
			.setCommitComment("Classified ontology.")
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

		final SnomedNamespaceAndModuleAssigner assigner = context
				.service(SnomedNamespaceAndModuleAssignerProvider.class)
				.get();

		final String assignerName = assigner.getClass()
				.getSimpleName();

		LOG.info("Reasoner service will use {} for relationship/concrete domain namespace and module assignment.", assignerName);

		final Set<String> conceptIdsToSkip = mergeEquivalentConcepts(context, bulkRequestBuilder);
		applyRelationshipChanges(context, bulkRequestBuilder, assigner, conceptIdsToSkip);

		if (isConcreteDomainSupported(context)) {
			applyConcreteDomainChanges(context, bulkRequestBuilder, assigner, conceptIdsToSkip);
		}
	}

	private void applyRelationshipChanges(final BranchContext context, 
			final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final Set<String> conceptIdsToSkip) {

		final RelationshipChangeSearchRequestBuilder relationshipRequestBuilder = ClassificationRequests.prepareSearchRelationshipChange()
				.setLimit(SCROLL_LIMIT)
				.setScroll(SCROLL_KEEP_ALIVE)
				.setExpand("relationship(inferredOnly:true)")
				.filterByClassificationId(classificationId);

		final SearchResourceRequestIterator<RelationshipChangeSearchRequestBuilder, RelationshipChanges> relationshipIterator = 
				new SearchResourceRequestIterator<>(relationshipRequestBuilder, 
						r -> r.build().execute(context));

		while (relationshipIterator.hasNext()) {
			final RelationshipChanges nextChanges = relationshipIterator.next();

			final Set<String> conceptIds = nextChanges.stream()
					.map(RelationshipChange::getRelationship)
					.map(ReasonerRelationship::getSourceId)
					.collect(Collectors.toSet());

			// Concepts which will be inactivated as part of equivalent concept merging should be excluded
			conceptIds.removeAll(conceptIdsToSkip);
			namespaceAndModuleAssigner.collectRelationshipNamespacesAndModules(conceptIds, context);

			for (final RelationshipChange change : nextChanges) {
				final ReasonerRelationship relationship = change.getRelationship();

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

	private void removeOrDeactivate(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, final ReasonerRelationship relationship) {
		final Request<TransactionContext, Boolean> request;

		if (relationship.isReleased()) {
			request = SnomedRequests
					.prepareUpdateRelationship(relationship.getId())
					.setActive(false)
					.build();
		} else {
			request = SnomedRequests
					.prepareDeleteRelationship(relationship.getId())
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
					.map(ConcreteDomainChange::getConcreteDomainMember)
					.map(r -> r.getReferencedComponent().getId())
					.collect(Collectors.toSet());

			// Concepts which will be inactivated as part of equivalent concept merging should be excluded
			conceptIds.removeAll(conceptIdsToSkip);
			namespaceAndModuleAssigner.collectConcreteDomainModules(conceptIds, context);

			for (final ConcreteDomainChange change : nextChanges) {
				final SnomedReferenceSetMember referenceSetMember = change.getConcreteDomainMember();

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
			final SnomedReferenceSetMember referenceSetMember) {

		final String referencedComponentId = referenceSetMember
				.getReferencedComponent()
				.getId();

		final String moduleId = namespaceAndModuleAssigner.getConcreteDomainModuleId(referencedComponentId);

		final Request<TransactionContext, String> createRequest = SnomedRequests.prepareNewMember()
				.setActive(referenceSetMember.isActive())
				.setModuleId(moduleId)
				.setReferencedComponentId(referencedComponentId)
				.setReferenceSetId(referenceSetMember.getReferenceSetId())
				.setProperties(referenceSetMember.getProperties())
				.build();

		bulkRequestBuilder.add(createRequest);
	}

	private void removeOrDeactivate(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, final SnomedReferenceSetMember referenceSetMember) {
		final Request<TransactionContext, Boolean> request;

		if (referenceSetMember.isReleased()) {
			request = SnomedRequests
					.prepareUpdateMember()
					.setMemberId(referenceSetMember.getId())
					.setSource(ImmutableMap.of(SnomedRf2Headers.FIELD_ACTIVE, false))
					.build();
		} else {
			request = SnomedRequests
					.prepareDeleteMember(referenceSetMember.getId())
					.build();
		}

		bulkRequestBuilder.add(request);
	}

	private Set<String> mergeEquivalentConcepts(final BranchContext context, final BulkRequestBuilder<TransactionContext> bulkRequestBuilder) {

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

		final Multimap<SnomedConcept, SnomedConcept> equivalentConcepts = HashMultimap.create();

		while (equivalentConceptIterator.hasNext()) {
			final EquivalentConceptSets nextBatch = equivalentConceptIterator.next();

			for (final EquivalentConceptSet equivalentSet : nextBatch) {
				if (equivalentSet.isUnsatisfiable()) {
					continue;
				}

				final List<SnomedConcept> conceptsToRemove = newArrayList(equivalentSet.getEquivalentConcepts());
				final SnomedConcept conceptToKeep = conceptsToRemove.remove(0);

				// FIXME: make equivalence set to fix user-selectable; currently, only descendants of SMP are auto-merged
				final List<Long> statedParents = Longs.asList(conceptToKeep.getStatedParentIds());
				final List<Long> statedAncestors = Longs.asList(conceptToKeep.getStatedAncestorIds());
				if (statedParents.contains(SMP_ROOT) || statedAncestors.contains(SMP_ROOT)) {
					equivalentConcepts.putAll(conceptToKeep, conceptsToRemove);
				}
			}
		}

		if (!equivalentConcepts.isEmpty()) {
			final EquivalentConceptMerger merger = new EquivalentConceptMerger(bulkRequestBuilder, equivalentConcepts);
			merger.merge();
		}
		
		return equivalentConcepts.values()
				.stream()
				.map(SnomedConcept::getId)
				.collect(Collectors.toSet());
	}

	private boolean isConcreteDomainSupported(final BranchContext context) {
		final SnomedCoreConfiguration snomedCoreConfiguration = context.service(SnomedCoreConfiguration.class);
		return snomedCoreConfiguration.isConcreteDomainSupported();
	}
}
