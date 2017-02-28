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
package com.b2international.snowowl.snomed.reasoner.server.classification;

import static com.b2international.commons.status.Statuses.error;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions.CLASSIFY_WITH_REVIEW;
import static com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions.SAVE_CLASSIFICATION_RESULTS;
import static org.eclipse.core.runtime.Status.OK_STATUS;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.server.remotejobs.AbstractRemoteJob;
import com.b2international.snowowl.datastore.server.snomed.index.AbstractReasonerTaxonomyBuilder.Type;
import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.server.SnomedReasonerServerActivator;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChange;
import com.b2international.snowowl.snomed.reasoner.server.diff.concretedomain.ConcreteDomainPersister;
import com.b2international.snowowl.snomed.reasoner.server.diff.relationship.RelationshipPersister;
import com.b2international.snowowl.snomed.reasoner.server.normalform.ConceptConcreteDomainNormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.server.normalform.RelationshipNormalFormGenerator;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Represents a remote job responsible for saving changes to the repository.
 * 
 */
public class PersistChangesRemoteJob extends AbstractRemoteJob {

	private static final long LOCK_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(5L);
	
	private ReasonerTaxonomy taxonomy;
	private final IBranchPath branchPath;
	private final String userId;

	private DatastoreLockContext lockContext;
	private IOperationLockTarget lockTarget;
	
	private LongSet statedDescendantsOfSmp;

	/**
	 * @param name
	 * @param taxonomy
	 * @param branchPath
	 * @param userId
	 */
	public PersistChangesRemoteJob(String name, ReasonerTaxonomy taxonomy, IBranchPath branchPath, String userId) {
		super(name);
		this.taxonomy = taxonomy;
		this.branchPath = branchPath;
		this.userId = userId;
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
	
	private RevisionIndex getIndex() {
		return ApplicationContext.getInstance().getService(RepositoryManager.class).get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
	}

	private IStatus persistChanges(final IProgressMonitor monitor) throws CommitException {

		if (null == taxonomy) {
			throw new IllegalStateException("Tried to run the same persist changes job twice.");
		}
		
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Persisting changes", 6);
		SnomedEditingContext editingContext = null;
		
		try {

			editingContext = new SnomedEditingContext(branchPath);
			final InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder = getIndex().read(branchPath.getPath(), new RevisionIndexRead<InitialReasonerTaxonomyBuilder>() {
				@Override
				public InitialReasonerTaxonomyBuilder execute(RevisionSearcher searcher) throws IOException {
					return new InitialReasonerTaxonomyBuilder(searcher, Type.REASONER);
				}
			});

			final RelationshipNormalFormGenerator relationshipGenerator = new RelationshipNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder);
			final RelationshipPersister relationshipAddPersister = new RelationshipPersister(editingContext, OntologyChange.Nature.ADD);
			final RelationshipPersister relationshipRemovePersister = new RelationshipPersister(editingContext, OntologyChange.Nature.REMOVE);
			
			relationshipGenerator.collectNormalFormChanges(subMonitor.newChild(1), relationshipAddPersister);
			relationshipGenerator.collectNormalFormChanges(subMonitor.newChild(1), relationshipRemovePersister);
			
			final ConceptConcreteDomainNormalFormGenerator conceptConcreteDomainGenerator = new ConceptConcreteDomainNormalFormGenerator(taxonomy, reasonerTaxonomyBuilder);
			conceptConcreteDomainGenerator.collectNormalFormChanges(subMonitor.newChild(1), new ConcreteDomainPersister(editingContext, OntologyChange.Nature.ADD));
			conceptConcreteDomainGenerator.collectNormalFormChanges(subMonitor.newChild(1), new ConcreteDomainPersister(editingContext, OntologyChange.Nature.REMOVE));

			final List<LongSet> equivalenciesToFix = Lists.newArrayList();

			for (final LongSet equivalentSet : taxonomy.getEquivalentConceptIds()) {
				long firstConceptId = equivalentSet.iterator().next();
				String firstConceptIdString = Long.toString(firstConceptId);

				// FIXME: make equivalence set to fix user-selectable, only subtype of SMP can be auto-merged
				if (isSubTypeOfSMP(branchPath, firstConceptIdString)) {
					equivalenciesToFix.add(equivalentSet);
				}
			}

			new EquivalentConceptMerger(editingContext, equivalenciesToFix).fixEquivalencies();

			final CDOTransaction editingContextTransaction = editingContext.getTransaction();
			editingContext.preCommit();

			new CDOServerCommitBuilder(userId, "Classified ontology.", editingContextTransaction)
				.parentContextDescription(SAVE_CLASSIFICATION_RESULTS)
				.commitOne(subMonitor.newChild(2));

			return OK_STATUS;
		} catch (CommitException e) {
			if (editingContext != null) {
				editingContext.releaseIds();
			}
			throw e;
		} finally {
			if (editingContext != null) {
				editingContext.close();
			}
		}
	}

	private boolean isSubTypeOfSMP(IBranchPath branchPath, String subTypeId) {
		if (statedDescendantsOfSmp == null) {
			statedDescendantsOfSmp = SnomedRequests.prepareGetConcept()
				.setComponentId(Concepts.GENERATED_SINGAPORE_MEDICINAL_PRODUCT)
				.setExpand("descendants(limit:"+Integer.MAX_VALUE+",direct:false,form:\"stated\")")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedConcept, LongSet>() {
					@Override
					public LongSet apply(SnomedConcept input) {
						final LongSet descendantIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(input.getDescendants().getTotal());
						for (SnomedConcept descendant : input.getDescendants()) {
							descendantIds.add(Long.parseLong(descendant.getId()));
						}
						return descendantIds;
					}
				})
				.fail(new Function<Throwable, LongSet>() {
					@Override
					public LongSet apply(Throwable input) {
						return PrimitiveSets.newLongOpenHashSet();
					}
				})
				.getSync();
		}
		return statedDescendantsOfSmp.contains(Long.parseLong(subTypeId));
	}

	private void cleanup() {
		try {

			if (null != lockContext && null != lockTarget) {
				getLockManager().unlock(lockContext, lockTarget);
			}

		} finally {
			lockContext = null;
			lockTarget = null;
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
