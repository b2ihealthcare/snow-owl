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
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
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
	
	void setModuleId(final String moduleId) {
		this.moduleId = moduleId;
	}
	
	void setNamespace(final String namespace) {
		this.namespace = namespace;
	}
	
	void setFixEquivalences(final boolean fixEquivalences) {
		this.fixEquivalences = fixEquivalences;
	}
	
	void setHandleConcreteDomains(final boolean handleConcreteDomains) {
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
		final Set<String> conceptIdsToSkip = mergeEquivalentConcepts(context, bulkRequestBuilder, assigner);
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
				.setExpand("relationship(inferredOnly:true)")
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

				if (conceptIdsToSkip.contains(relationship.getSourceId()) || conceptIdsToSkip.contains(relationship.getDestinationId())) {
					continue;
				}
				
				// Do not reference concepts which were handled by the equivalent concept merger
				if (ChangeNature.INFERRED.equals(change.getChangeNature())) {
					addComponent(bulkRequestBuilder, namespaceAndModuleAssigner, relationship);
				} else {
					removeOrDeactivate(bulkRequestBuilder, relationship);
				}
			}
		}

		namespaceAndModuleAssigner.clear();
	}

	private void applyConcreteDomainChanges(final BranchContext context, 
			final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final Set<String> conceptIdsToSkip) {

		final ConcreteDomainChangeSearchRequestBuilder concreteDomainRequestBuilder = ClassificationRequests.prepareSearchConcreteDomainChange()
				.setLimit(SCROLL_LIMIT)
				.setScroll(SCROLL_KEEP_ALIVE)
				.setExpand("concreteDomainMember(inferredOnly:true)")
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

				if (conceptIdsToSkip.contains(referenceSetMember.getReferencedComponentId())) {
					continue;
				}
				
				// Do not reference concepts which were handled by the equivalent concept merger
				if (ChangeNature.INFERRED.equals(change.getChangeNature())) {
					addComponent(bulkRequestBuilder, namespaceAndModuleAssigner, referenceSetMember);
				} else {
					removeOrDeactivate(bulkRequestBuilder, referenceSetMember);
				}
			}
		}

		namespaceAndModuleAssigner.clear();
	}

	private Set<String> mergeEquivalentConcepts(final BranchContext context, 
			final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, 
			final SnomedNamespaceAndModuleAssigner assigner) {

		if (!fixEquivalences) {
			return Collections.emptySet();
		}
		
		// XXX: Restrict merging to active components only
		final String expand = "equivalentConcepts(expand("
				+ "descriptions(active:true),"
				+ "relationships(active:true),"
				+ "inboundRelationships(active:true),"
				+ "members(active:true)))";
		
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
		
		final Set<String> conceptIdsToSkip = merger.merge(equivalentConcepts);
		final Set<String> conceptIdsToKeep = equivalentConcepts.keySet()
				.stream()
				.map(SnomedConcept::getId)
				.collect(Collectors.toSet());
		
		assigner.collectRelationshipNamespacesAndModules(conceptIdsToKeep, context);
		
		for (final SnomedConcept conceptToKeep : equivalentConcepts.keySet()) {
			
			for (final SnomedRelationship relationship : conceptToKeep.getInboundRelationships()) {
				if (relationship.getId().startsWith(IEquivalentConceptMerger.PREFIX_NEW)) {
					relationship.setId(null);
					addComponent(bulkRequestBuilder, assigner, relationship);
				} else if (relationship.isActive()) {
					removeOrDeactivate(bulkRequestBuilder, relationship);
				}
			}
			
			for (final SnomedRelationship relationship : conceptToKeep.getRelationships()) {
				if (relationship.getId().startsWith(IEquivalentConceptMerger.PREFIX_NEW)) {
					relationship.setId(null);
					addComponent(bulkRequestBuilder, assigner, relationship);
				} else if (relationship.isActive()) {
					removeOrDeactivate(bulkRequestBuilder, relationship);
				}
			}
		}
		
		assigner.clear();
		assigner.collectConcreteDomainModules(conceptIdsToKeep, context);
		
		for (final SnomedConcept conceptToKeep : equivalentConcepts.keySet()) {
			for (final SnomedReferenceSetMember member : conceptToKeep.getMembers()) {
				if (member.getId().startsWith(IEquivalentConceptMerger.PREFIX_NEW)) {
					member.setId(null);
					addComponent(bulkRequestBuilder, assigner, member);
				} else if (member.getId().startsWith(IEquivalentConceptMerger.PREFIX_UPDATED)) { 
					// Trim the prefix from the ID to restore its original form
					member.setId(member.getId().substring(IEquivalentConceptMerger.PREFIX_UPDATED.length()));
					bulkRequestBuilder.add(member.toUpdateRequest());
				} else if (member.isActive()) {
					removeOrDeactivate(bulkRequestBuilder, member);
				}
			}
		}
		
		assigner.clear();
		
		for (final SnomedConcept conceptToRemove : equivalentConcepts.values()) {
			// Check if the concept needs to be removed or deactivated
			if (!conceptToRemove.isActive()) {
				removeOrDeactivate(bulkRequestBuilder, conceptToRemove);
			}
		}
		
		return conceptIdsToSkip;
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

	private void removeOrDeactivate(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, final SnomedConcept concept) {
		final Request<TransactionContext, Boolean> request;
	
		if (concept.isReleased()) {
			request = SnomedRequests
					.prepareUpdateConcept(concept.getId())
					.setActive(false)
					.setInactivationIndicator(InactivationIndicator.RETIRED)
					.build();
		} else {
			request = SnomedRequests
					.prepareDeleteRelationship(concept.getId())
					.build();
		}
	
		bulkRequestBuilder.add(request);
	}

	private void removeOrDeactivate(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedRelationship relationship) {
		removeOrDeactivateRelationship(bulkRequestBuilder, relationship.isReleased(), relationship.getId());
	}

	private void removeOrDeactivate(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, 
			final ReasonerRelationship relationship) {
		removeOrDeactivateRelationship(bulkRequestBuilder, relationship.isReleased(), relationship.getOriginId());
	}

	private void removeOrDeactivateRelationship(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, 
			final boolean released, final String relationshipId) {
		
		final Request<TransactionContext, Boolean> request;
		
		if (released) {
			request = SnomedRequests
					.prepareUpdateRelationship(relationshipId)
					.setActive(false)
					.build();
		} else {
			request = SnomedRequests
					.prepareDeleteRelationship(relationshipId)
					.build();
		}
	
		bulkRequestBuilder.add(request);
	}

	private void removeOrDeactivate(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedReferenceSetMember member) {
		removeOrDeactivateMember(bulkRequestBuilder, member.isReleased(), member.getId());
	}

	private void removeOrDeactivate(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, 
			final ReasonerConcreteDomainMember member) {
		removeOrDeactivateMember(bulkRequestBuilder, member.isReleased(), member.getOriginMemberId());
	}

	private void removeOrDeactivateMember(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, 
			final boolean released, final String memberId) {
		
		final Request<TransactionContext, Boolean> request;
		
		if (released) {
			request = SnomedRequests
					.prepareUpdateMember()
					.setMemberId(memberId)
					.setSource(ImmutableMap.of(SnomedRf2Headers.FIELD_ACTIVE, false))
					.build();
		} else {
			request = SnomedRequests
					.prepareDeleteMember(memberId)
					.build();
		}
	
		bulkRequestBuilder.add(request);
	}

	private void addComponent(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final ReasonerRelationship relationship) {
	
		final String sourceId = relationship.getSourceId();
		final String typeId = relationship.getTypeId();
		final String destinationId = relationship.getDestinationId();
		final boolean destinationNegated = relationship.isDestinationNegated();
		final CharacteristicType characteristicType = relationship.getCharacteristicType();
		final int group = relationship.getGroup();
		final int unionGroup = relationship.getUnionGroup();
		final RelationshipModifier modifier = relationship.getModifier();
		
		addComponent(bulkRequestBuilder, namespaceAndModuleAssigner, 
				sourceId, typeId, destinationId, destinationNegated,
				characteristicType, group, unionGroup, modifier);
	}
	

	private void addComponent(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final SnomedRelationship relationship) {

		final String sourceId = relationship.getSourceId();
		final String typeId = relationship.getTypeId();
		final String destinationId = relationship.getDestinationId();
		final boolean destinationNegated = relationship.isDestinationNegated();
		final CharacteristicType characteristicType = relationship.getCharacteristicType();
		final int group = relationship.getGroup();
		final int unionGroup = relationship.getUnionGroup();
		final RelationshipModifier modifier = relationship.getModifier();
		
		addComponent(bulkRequestBuilder, namespaceAndModuleAssigner, 
				sourceId, typeId, destinationId, destinationNegated,
				characteristicType, group, unionGroup, modifier);
	}

	private void addComponent(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final String sourceId,
			final String typeId, 
			final String destinationId, 
			final boolean destinationNegated,
			final CharacteristicType characteristicType, 
			final int group, 
			final int unionGroup,
			final RelationshipModifier modifier) {
		
		final String moduleId = namespaceAndModuleAssigner.getRelationshipModuleId(sourceId);
		final String namespace = namespaceAndModuleAssigner.getRelationshipNamespace(sourceId);
	
		final SnomedRelationshipCreateRequestBuilder createRequest = SnomedRequests.prepareNewRelationship()
				.setIdFromNamespace(namespace)
				.setTypeId(typeId)
				.setActive(true)
				.setCharacteristicType(characteristicType)
				.setSourceId(sourceId)
				.setDestinationId(destinationId)
				.setDestinationNegated(destinationNegated)
				.setGroup(group)
				.setUnionGroup(unionGroup)
				.setModifier(modifier)
				.setModuleId(moduleId);
	
		bulkRequestBuilder.add(createRequest);
	}

	private void addComponent(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final ReasonerConcreteDomainMember member) {
	
		final String referencedComponentId = member.getReferencedComponentId();
		final String referenceSetId = member.getReferenceSetId();
		final String typeId = member.getTypeId();
		final String serializedValue = member.getSerializedValue();
		final int group = member.getGroup();
		final String characteristicTypeId = member.getCharacteristicTypeId();
		
		addComponent(bulkRequestBuilder, namespaceAndModuleAssigner, 
				referencedComponentId, referenceSetId, typeId,
				serializedValue, group, characteristicTypeId);
	}

	private void addComponent(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final SnomedReferenceSetMember member) {
		
		final String referencedComponentId = member.getReferencedComponent().getId();
		final String referenceSetId = member.getReferenceSetId();
		final String typeId = (String) member.getProperties().get(SnomedRf2Headers.FIELD_TYPE_ID);
		final String serializedValue = (String) member.getProperties().get(SnomedRf2Headers.FIELD_VALUE);
		final int group = (Integer) member.getProperties().get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP);
		final String characteristicTypeId = (String) member.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);
		
		addComponent(bulkRequestBuilder, namespaceAndModuleAssigner, 
				referencedComponentId, referenceSetId, typeId,
				serializedValue, group, characteristicTypeId);
	}
	
	private void addComponent(final BulkRequestBuilder<TransactionContext> bulkRequestBuilder,
			final SnomedNamespaceAndModuleAssigner namespaceAndModuleAssigner, 
			final String referencedComponentId,
			final String referenceSetId, 
			final String typeId, 
			final String serializedValue, 
			final int group,
			final String characteristicTypeId) {
		
		final String moduleId = namespaceAndModuleAssigner.getConcreteDomainModuleId(referencedComponentId);
	
		final Request<TransactionContext, String> createRequest = SnomedRequests.prepareNewMember()
				.setActive(true)
				.setModuleId(moduleId)
				.setReferencedComponentId(referencedComponentId)
				.setReferenceSetId(referenceSetId)
				.setProperties(ImmutableMap.of(
						SnomedRf2Headers.FIELD_TYPE_ID, typeId,
						SnomedRf2Headers.FIELD_VALUE, serializedValue,
						SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, group,
						SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, characteristicTypeId))
				.build();
	
		bulkRequestBuilder.add(createRequest);
	}
}
