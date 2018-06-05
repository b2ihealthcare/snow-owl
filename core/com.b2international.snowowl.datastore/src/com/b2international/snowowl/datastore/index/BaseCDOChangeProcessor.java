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
package com.b2international.snowowl.datastore.index;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.events.metrics.MetricsThreadLocal;
import com.b2international.snowowl.core.events.metrics.Timer;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Base class for updating indexes based on the new, dirty and detached components when a commit is in progress in the primary storage.
 * 
 * @since 5.0
 */
public abstract class BaseCDOChangeProcessor implements ICDOChangeProcessor {

	private final IBranchPath branchPath;
	private final RevisionIndex index;
	private final Logger log;

	private ICDOCommitChangeSet commitChangeSet;
	private IndexCommitChangeSet indexChangeSet;

	public BaseCDOChangeProcessor(IBranchPath branchPath, RevisionIndex index) {
		this.branchPath = branchPath;
		this.index = index;
		this.log = LoggerFactory.getLogger("repository");
	}

	protected final IBranchPath getBranchPath() {
		return branchPath;
	}

	protected final Logger log() {
		return log;
	}

	protected final RevisionIndex index() {
		return index;
	}

	@Override
	public final void process(final ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException {
		final Timer indexTimer = MetricsThreadLocal.get().timer("indexing");
		try {
			indexTimer.start();
			this.commitChangeSet = checkNotNull(commitChangeSet, "CDO commit change set argument cannot be null.");

			index.read(branchPath.getPath(), index -> {
				updateDocuments(commitChangeSet, index);
				return null;
			});
		} finally {
			indexTimer.stop();
		}
	}

	private void processNewCodeSystemsAndVersions(final ICDOCommitChangeSet commitChangeSet, final ImmutableIndexCommitChangeSet.Builder indexCommitChangeSet) {
		for (CDOObject object : commitChangeSet.getNewComponents()) {
			if (TerminologymetadataPackage.eINSTANCE.getCodeSystem().isSuperTypeOf(object.eClass())) {
				CodeSystem newCodeSystem  = (CodeSystem) object;
				final CodeSystemEntry entry = CodeSystemEntry.builder(newCodeSystem).build();
				indexCommitChangeSet.putNewObject(ComponentIdentifier.of(CodeSystemEntry.TERMINOLOGY_COMPONENT_ID, entry.getShortName()), entry);
			} else if (TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(object.eClass())) {
				CodeSystemVersion newCodeSystemVersion = (CodeSystemVersion) object;
				final CodeSystemVersionEntry entry = CodeSystemVersionEntry.builder(newCodeSystemVersion).build();
				indexCommitChangeSet.putNewObject(ComponentIdentifier.of(CodeSystemVersionEntry.TERMINOLOGY_COMPONENT_ID, entry.getVersionId()), entry);
			}
		}
	}

	private void processDirtyCodeSystemsAndVersions(ICDOCommitChangeSet commitChangeSet, final ImmutableIndexCommitChangeSet.Builder indexCommitChangeSet) {
		for (final CDOObject dirtyObject : commitChangeSet.getDirtyComponents()) {
			if (TerminologymetadataPackage.eINSTANCE.getCodeSystem().isSuperTypeOf(dirtyObject.eClass())) {
				final CodeSystemEntry entry = CodeSystemEntry.builder((CodeSystem) dirtyObject).build();
				indexCommitChangeSet.putChangedObject(ComponentIdentifier.of(CodeSystemEntry.TERMINOLOGY_COMPONENT_ID, entry.getShortName()), entry);
			} else if (TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(dirtyObject.eClass())) {
				checkAndSetCodeSystemLastUpdateTime(dirtyObject, indexCommitChangeSet);
			}
		}
	}

	private final void updateDocuments(ICDOCommitChangeSet commitChangeSet, RevisionSearcher index) throws IOException {
		log.info("Processing changes...");
		final ImmutableIndexCommitChangeSet.Builder indexCommitChangeSet = ImmutableIndexCommitChangeSet.builder();
		
		processNewCodeSystemsAndVersions(commitChangeSet, indexCommitChangeSet);
		processDirtyCodeSystemsAndVersions(commitChangeSet, indexCommitChangeSet);

		// apply code system and version deletions
		commitChangeSet.getDetachedComponents(TerminologymetadataPackage.Literals.CODE_SYSTEM, CodeSystemEntry.class, CodeSystemEntry.Expressions::storageKeys)
			.forEach(removed -> {
				indexCommitChangeSet.putRemovedComponent(ComponentIdentifier.of(CodeSystemEntry.TERMINOLOGY_COMPONENT_ID, removed.getShortName()), removed);
			});
		commitChangeSet.getDetachedComponents(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION, CodeSystemVersionEntry.class, CodeSystemVersionEntry.Expressions::storageKeys)
			.forEach(removed -> {
				indexCommitChangeSet.putRemovedComponent(ComponentIdentifier.of(CodeSystemVersionEntry.TERMINOLOGY_COMPONENT_ID, removed.getVersionId()), removed);
			});
		
		preUpdateDocuments(commitChangeSet, index);

		for (ChangeSetProcessor processor : getChangeSetProcessors()) {
			log.trace("Collecting {}...", processor.description());
			processor.process(commitChangeSet, index);
			// register additions, deletions from the sub processor
			for (RevisionDocument revision : processor.getNewMappings().values()) {
				indexCommitChangeSet.putNewObject(getComponentIdentifier(revision), revision);
			}
			
			for (RevisionDocument revision : processor.getChangedMappings().values()) {
				indexCommitChangeSet.putChangedObject(getComponentIdentifier(revision), revision);
			}
			
			final Multimap<Class<? extends Revision>, String> deletions = processor.getDeletions();
			for (Class<? extends Revision> type : deletions.keySet()) {
				// TODO remove index.get call here, we already fetched the doc, now we do it again do delete it, see CDOCommitChangeSet
				for (RevisionDocument revision : Iterables.filter(index.get(type, deletions.get(type)), RevisionDocument.class)) {
					indexCommitChangeSet.putRemovedComponent(getComponentIdentifier(revision), revision);
				}
			}
		}

		indexChangeSet = indexCommitChangeSet.build();
		postUpdateDocuments(indexChangeSet);
		log.info("Processing changes successfully finished.");
	}

	private ComponentIdentifier getComponentIdentifier(RevisionDocument revision) {
		return ComponentIdentifier.of(getTerminologyComponentId(revision), revision.getId());
	}

	protected abstract short getTerminologyComponentId(RevisionDocument revision);

	/**
	 * Return a list of {@link ChangeSetProcessor}s to process the commit changeset.
	 * 
	 * @return
	 */
	protected abstract Collection<ChangeSetProcessor> getChangeSetProcessors();

	/**
	 * Subclasses may override this method to execute additional logic before the processing of the changeset.
	 * 
	 * @param commitChangeSet
	 * @param index
	 * @throws IOException
	 */
	protected void preUpdateDocuments(ICDOCommitChangeSet commitChangeSet, RevisionSearcher index) throws IOException {
	}

	/**
	 * Subclasses may override this method to execute additional logic after the processing of the changeset, but before committing it.
	 * 
	 * @param commitChangeSet - the commit change set about to be committed by this processor
	 */
	protected void postUpdateDocuments(IndexCommitChangeSet commitChangeSet) {
	}

	@Override
	public final IndexCommitChangeSet commit() throws SnowowlServiceException {
		final Timer indexTimer = MetricsThreadLocal.get().timer("indexing");
		try {
			checkState(commitChangeSet.getTimestamp() > 0, "Commit timestamp should be greater than zero");
			indexTimer.start();
			log.info("Persisting changes to revision index...");
			
			StagingArea stagingArea = index.prepareCommit(branchPath.getPath());

			indexChangeSet.apply(stagingArea);
			
			final String commitComment = commitChangeSet.getCommitComment();
			final String commitGroupId = CDOCommitInfoUtils.getUuid(commitComment);
			final String comment = CDOCommitInfoUtils.removeUuidPrefix(commitComment);
			stagingArea.commit(commitGroupId, commitChangeSet.getTimestamp(), commitChangeSet.getUserId(), comment);
			return indexChangeSet;
		} finally {
			log.info("Changes have been successfully persisted.");
			indexTimer.stop();
		}
	}
	
	@Override
	public final void afterCommit() {
	}

	@Override
	public final void rollback() throws SnowowlServiceException {
		// XXX nothing to do, just don't commit the writer
	}

	@Override
	public final String getName() {
		return String.format("%s.changeProcessor", index.name());
	}

	@SuppressWarnings("restriction")
	private void checkAndSetCodeSystemLastUpdateTime(final CDOObject component, ImmutableIndexCommitChangeSet.Builder indexCommitChangeSet) {
		final CodeSystemVersion version = (CodeSystemVersion) component;
		final CDOFeatureDelta lastUpdateFeatureDelta = commitChangeSet.getRevisionDeltas().get(component.cdoID())
				.getFeatureDelta(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate());
		if (lastUpdateFeatureDelta instanceof org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) {
			((org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) lastUpdateFeatureDelta)
					.setValue(new Date(commitChangeSet.getTimestamp()));
			((InternalCDORevision) component.cdoRevision()).set(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate(),
					CDOStore.NO_INDEX, new Date(commitChangeSet.getTimestamp()));
			final CodeSystemVersionEntry entry = CodeSystemVersionEntry.builder(version).build();
			indexCommitChangeSet.putChangedObject(ComponentIdentifier.of(CodeSystemVersionEntry.TERMINOLOGY_COMPONENT_ID, entry.getVersionId()), entry);
		}
	}

}
