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
package com.b2international.snowowl.snomed.reasoner.server.classification;

import static com.b2international.commons.status.Statuses.error;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions.CLASSIFY_WITH_REVIEW;
import static com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions.SAVE_CLASSIFICATION_RESULTS;
import static org.eclipse.core.runtime.Status.OK_STATUS;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.server.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.server.remotejobs.AbstractRemoteJob;
import com.b2international.snowowl.datastore.server.snomed.index.AbstractReasonerTaxonomyBuilder.Type;
import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndModuleAssigner;
import com.b2international.snowowl.snomed.reasoner.server.SnomedReasonerServerActivator;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeRecorder;
import com.b2international.snowowl.snomed.reasoner.server.diff.concretedomain.ConcreteDomainPersister;
import com.b2international.snowowl.snomed.reasoner.server.diff.relationship.RelationshipPersister;
import com.b2international.snowowl.snomed.reasoner.server.normalform.ConceptConcreteDomainNormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.server.normalform.RelationshipNormalFormGenerator;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

import bak.pcj.set.LongSet;

/**
 * Represents a remote job responsible for saving changes to the repository.
 * 
 */
public class PersistChangesRemoteJob extends AbstractRemoteJob {

	private static final String NAMESPACE_ASSIGNER_EXTENSION = "com.b2international.snowowl.snomed.reasoner.server.namespaceAssigner";
	private static final long LOCK_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(5L);
	
	private final IBranchPath branchPath;
	private final String userId;

	private ReasonerTaxonomy taxonomy;
	private NamespaceAndModuleAssigner namespaceAndModuleAssigner;
	private DatastoreLockContext lockContext;
	private IOperationLockTarget lockTarget;

	/**
	 * @param name
	 * @param taxonomy
	 * @param branchPath
	 * @param userId
	 */
	public PersistChangesRemoteJob(String name, ReasonerTaxonomy taxonomy, IBranchPath branchPath, String userId) {
		super(name);
		this.branchPath = branchPath;
		this.userId = userId;
		
		this.taxonomy = taxonomy;
		this.namespaceAndModuleAssigner = Extensions.getFirstPriorityExtension(NAMESPACE_ASSIGNER_EXTENSION, NamespaceAndModuleAssigner.class);
		if (namespaceAndModuleAssigner == null) {
			throw new NullPointerException("Could not find a namespace and module allocator in the extension registry");
		}
	}

	private static IDatastoreOperationLockManager getLockManager() {
		return getServiceForClass(IDatastoreOperationLockManager.class);
	}

	@Override
	protected IStatus runWithListenableMonitor(final IProgressMonitor monitor) {

		try {
			lockBeforeChanges();
			return persistChanges(monitor);
		} catch (final Exception e) {
			return error(SnomedReasonerServerActivator.PLUGIN_ID, "Error while persisting classification changes on '" + branchPath + "'.", e);
		} finally {
			monitor.done();
			cleanup();
		}
	}

	private void lockBeforeChanges() {

		final DatastoreLockContext localLockContext = createLockContext(userId);
		final IOperationLockTarget localLockTarget = createLockTarget(branchPath);

		try {

			getLockManager().lock(localLockContext, LOCK_TIMEOUT_MILLIS, localLockTarget);
			lockContext = localLockContext;
			lockTarget = localLockTarget;

		} catch (final OperationLockException | InterruptedException e) {
			DatastoreLockContext otherContext = null;
			if (e instanceof DatastoreOperationLockException) {
				otherContext = ((DatastoreOperationLockException) e).getContext(localLockTarget);
			}
	
			final String reason = (null == otherContext) ? getDefaultContextDescription() : getContextDescription(otherContext);
			throw new DatastoreOperationLockException(reason);
		}
	}

	private IStatus persistChanges(final IProgressMonitor monitor) throws CommitException {

		if (null == taxonomy) {
			throw new IllegalStateException("Tried to run the same persist changes job twice.");
		}
		
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Persisting changes", 4);
		SnomedEditingContext editingContext = null;
		
		try {

			editingContext = new SnomedEditingContext(branchPath);
			
			final OntologyChangeRecorder<StatementFragment> relationshipRecorder = new OntologyChangeRecorder<>();
			final OntologyChangeRecorder<ConcreteDomainFragment> concreteDomainRecorder = new OntologyChangeRecorder<>();
			recordChanges(subMonitor, relationshipRecorder, concreteDomainRecorder);
		
			namespaceAndModuleAssigner.allocateRelationshipIdsAndModules(relationshipRecorder.getAddedSubjects().keys(), editingContext);
			applyRelationshipChanges(editingContext, relationshipRecorder);
			
			namespaceAndModuleAssigner.allocateConcreteDomainModules(concreteDomainRecorder.getAddedSubjects().keySet(), editingContext);
			applyConcreteDomainChanges(editingContext, concreteDomainRecorder);

			final List<LongSet> equivalenciesToFix = Lists.newArrayList();
			
			Set<String> firstEquivalentConceptIds = FluentIterable.from(taxonomy.getEquivalentConceptIds()).transform(new Function<LongSet, String>() {
				@Override
				public String apply(LongSet input) {
					return String.valueOf(input.iterator().next());
				}
			}).toSet();
			
			Set<String> generatedConceptIds = SnomedRequests.prepareSearchConcept()
				.all()
				.setComponentIds(firstEquivalentConceptIds)
				.setExpand("parentIds(), ancestorIds()")
				.build(branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedConcepts, Set<String>>() {
					@Override
					public Set<String> apply(SnomedConcepts concepts) {
						return FluentIterable.from(concepts).filter(new Predicate<ISnomedConcept>() {
							@Override
							public boolean apply(ISnomedConcept concept) {
								return isGeneratedConcept(concept);
							}
						})
						.transform(IComponent.ID_FUNCTION)
						.toSet();
					}
				}).getSync();
			
			for (final LongSet equivalentSet : taxonomy.getEquivalentConceptIds()) {
				String firstConceptIdString = Long.toString(equivalentSet.iterator().next());
				if (generatedConceptIds.contains(firstConceptIdString)) {
					equivalenciesToFix.add(equivalentSet);
				}
			}

			if (!equivalenciesToFix.isEmpty()) {
				new EquivalentConceptMerger(editingContext, equivalenciesToFix).fixEquivalencies();
			}

			final CDOTransaction editingContextTransaction = editingContext.getTransaction();
			editingContext.preCommit();
			
			new CDOServerCommitBuilder(userId, "Classified ontology.", editingContextTransaction)
				.parentContextDescription(SAVE_CLASSIFICATION_RESULTS)
				.commitOne(subMonitor.newChild(2));

			return OK_STATUS;
		} catch (CommitException e) {
			throw e;
		} finally {
			if (editingContext != null) {
				editingContext.close();
			}
		}
	}

	private boolean isGeneratedConcept(ISnomedConcept concept) {
		
		final ImmutableSet.Builder<Long> longAncestorIds = ImmutableSet.builder();
		
		if (concept.getParentIds() != null) {
			longAncestorIds.addAll(Longs.asList(concept.getParentIds()));
		}
		
		if (concept.getAncestorIds() != null) {
			longAncestorIds.addAll(Longs.asList(concept.getAncestorIds()));
		}
		
		if (concept.getStatedParentIds() != null) {
			longAncestorIds.addAll(Longs.asList(concept.getStatedParentIds()));
		}
		
		if (concept.getStatedAncestorIds() != null) {
			longAncestorIds.addAll(Longs.asList(concept.getStatedAncestorIds()));
		}
		
		final Set<String> stringAncestorIds = FluentIterable.from(longAncestorIds.build())
			.transform(Functions.toStringFunction())
			.toSet();
		
		if (stringAncestorIds.contains(Concepts.GENERATED_SINGAPORE_MEDICINAL_PRODUCT)) {
			return true;
		}
		
		return false;
	}

	private void recordChanges(final SubMonitor subMonitor,
			final OntologyChangeRecorder<StatementFragment> relationshipRecorder,
			final OntologyChangeRecorder<ConcreteDomainFragment> concreteDomainRecorder) {
		
		final InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder = new InitialReasonerTaxonomyBuilder(branchPath, Type.REASONER);
	
		final RelationshipNormalFormGenerator relationshipGenerator = new RelationshipNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder);
		relationshipGenerator.collectNormalFormChanges(subMonitor.newChild(1), relationshipRecorder);
	
		final ConceptConcreteDomainNormalFormGenerator conceptConcreteDomainGenerator = new ConceptConcreteDomainNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder);
		conceptConcreteDomainGenerator.collectNormalFormChanges(subMonitor.newChild(1), concreteDomainRecorder);
	}

	private void applyRelationshipChanges(SnomedEditingContext editingContext, OntologyChangeRecorder<StatementFragment> relationshipRecorder) {
		final RelationshipPersister relationshipPersister = new RelationshipPersister(editingContext, namespaceAndModuleAssigner);
		
		for (Entry<String, StatementFragment> addedFragments : relationshipRecorder.getAddedSubjects().entries()) {
			relationshipPersister.handleAddedSubject(addedFragments.getKey(), addedFragments.getValue());
		}
		
		for (Entry<String, StatementFragment> removedFragments : relationshipRecorder.getRemovedSubjects().entries()) {
			relationshipPersister.handleRemovedSubject(removedFragments.getKey(), removedFragments.getValue());
		}
	}

	private void applyConcreteDomainChanges(SnomedEditingContext editingContext, OntologyChangeRecorder<ConcreteDomainFragment> concreteDomainRecorder) {
		final ConcreteDomainPersister concreteDomainPersister = new ConcreteDomainPersister(editingContext, namespaceAndModuleAssigner);
		
		for (Entry<String, ConcreteDomainFragment> addedFragments : concreteDomainRecorder.getAddedSubjects().entries()) {
			concreteDomainPersister.handleAddedSubject(addedFragments.getKey(), addedFragments.getValue());
		}
		
		for (Entry<String, ConcreteDomainFragment> removedFragments : concreteDomainRecorder.getRemovedSubjects().entries()) {
			concreteDomainPersister.handleRemovedSubject(removedFragments.getKey(), removedFragments.getValue());
		}
	}

	private void cleanup() {
		try {

			if (null != lockContext && null != lockTarget) {
				getLockManager().unlock(lockContext, lockTarget);
			}

		} finally {
			lockTarget = null;
			lockContext = null;
			namespaceAndModuleAssigner = null;
			taxonomy = null;
		}		
	}

	private String getDefaultContextDescription() {
		return "of concurrent activity";
	}

	private String getContextDescription(DatastoreLockContext otherContext) {
		return otherContext.getUserId() + " is currently " + otherContext.getDescription();
	}

	private SingleRepositoryAndBranchLockTarget createLockTarget(final IBranchPath branchPath) {
		return new SingleRepositoryAndBranchLockTarget(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath);
	}

	private DatastoreLockContext createLockContext(final String userId) {
		return new DatastoreLockContext(userId, SAVE_CLASSIFICATION_RESULTS, CLASSIFY_WITH_REVIEW);
	}
}
