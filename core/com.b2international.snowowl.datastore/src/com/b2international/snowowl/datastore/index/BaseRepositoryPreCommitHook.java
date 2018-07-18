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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.revision.Hooks;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.events.metrics.MetricsThreadLocal;
import com.b2international.snowowl.core.events.metrics.Timer;

/**
 * Base {@link Repository} pre-commit hook. It allows terminology plugin developers to attach custom precommit hooks to the underlying
 * {@link Repository}. These hooks will be executed in order and applied to the current {@link StagingArea}.
 * 
 * @since 5.0
 */
public abstract class BaseRepositoryPreCommitHook implements Hooks.PreCommitHook {

	protected static final Logger LOG = LoggerFactory.getLogger("repository");

	@Override
	public void run(StagingArea staging) {
		final Timer indexTimer = MetricsThreadLocal.get().timer("pre-commit");
		try {
			indexTimer.start();
			staging.read(index -> {
				updateDocuments(staging, index);
				return null;
			});
		} finally {
			indexTimer.stop();
		}
	}
	
//	private void processNewCodeSystemsAndVersions(final StagingArea commitChangeSet, final ImmutableIndexCommitChangeSet.Builder indexCommitChangeSet) {
//		for (Object object : commitChangeSet.getNewObjects().values()) {
//			if (object instanceof CodeSystemEntry) {
//				CodeSystem newCodeSystem  = (CodeSystem) object;
//				final CodeSystemEntry entry = CodeSystemEntry.builder(newCodeSystem).build();
//				indexCommitChangeSet.putNewObject(ComponentIdentifier.of(CodeSystemEntry.TERMINOLOGY_COMPONENT_ID, entry.getShortName()), entry);
//			} else if (TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(object.eClass())) {
//				CodeSystemVersion newCodeSystemVersion = (CodeSystemVersion) object;
//				final CodeSystemVersionEntry entry = CodeSystemVersionEntry.builder(newCodeSystemVersion).build();
//				indexCommitChangeSet.putNewObject(ComponentIdentifier.of(CodeSystemVersionEntry.TERMINOLOGY_COMPONENT_ID, entry.getVersionId()), entry);
//			}
//		}
//	}

//	private void processDirtyCodeSystemsAndVersions(ICDOCommitChangeSet commitChangeSet, final ImmutableIndexCommitChangeSet.Builder indexCommitChangeSet) {
//		for (final CDOObject dirtyObject : commitChangeSet.getDirtyComponents()) {
//			if (TerminologymetadataPackage.eINSTANCE.getCodeSystem().isSuperTypeOf(dirtyObject.eClass())) {
//				final CodeSystemEntry entry = CodeSystemEntry.builder((CodeSystem) dirtyObject).build();
//				indexCommitChangeSet.putChangedObject(ComponentIdentifier.of(CodeSystemEntry.TERMINOLOGY_COMPONENT_ID, entry.getShortName()), entry);
//			} else if (TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(dirtyObject.eClass())) {
//				checkAndSetCodeSystemLastUpdateTime(dirtyObject, indexCommitChangeSet);
//			}
//		}
//	}

	private final void updateDocuments(StagingArea staging, RevisionSearcher index) throws IOException {
		LOG.info("Processing changes...");
//		processNewCodeSystemsAndVersions(commitChangeSet, indexCommitChangeSet);
//		processDirtyCodeSystemsAndVersions(commitChangeSet, indexCommitChangeSet);

		// apply code system and version deletions
//		commitChangeSet.getDetachedComponents(TerminologymetadataPackage.Literals.CODE_SYSTEM, CodeSystemEntry.class, CodeSystemEntry.Expressions::storageKeys)
//			.forEach(removed -> {
//				indexCommitChangeSet.putRemovedComponent(ComponentIdentifier.of(CodeSystemEntry.TERMINOLOGY_COMPONENT_ID, removed.getShortName()), removed);
//			});
//		commitChangeSet.getDetachedComponents(TerminologymetadataPackage.Literals.CODE_SYSTEM_VERSION, CodeSystemVersionEntry.class, CodeSystemVersionEntry.Expressions::storageKeys)
//			.forEach(removed -> {
//				indexCommitChangeSet.putRemovedComponent(ComponentIdentifier.of(CodeSystemVersionEntry.TERMINOLOGY_COMPONENT_ID, removed.getVersionId()), removed);
//			});
		
		for (ChangeSetProcessor processor : getChangeSetProcessors(staging, index)) {
			LOG.trace("Collecting {}...", processor.description());
			processor.process(staging, index);
			// register additions, deletions from the sub processor
			
			for (RevisionDocument revision : processor.getNewMappings().values()) {
				staging.stageNew(revision);
			}
			
			for (RevisionDocumentChange revisionChange : processor.getChangedMappings().values()) {
				staging.stageChange(((RevisionDocumentChange) revisionChange).getOldRevision(), ((RevisionDocumentChange) revisionChange).getNewRevision());
			}
			
			processor.getDeletions().forEach(staging::stageRemove);
		}

		postUpdateDocuments(staging);
		LOG.info("Processing changes successfully finished.");
	}

	protected abstract short getTerminologyComponentId(RevisionDocument revision);

	/**
	 * Return a list of {@link ChangeSetProcessor}s to process the commit changeset.
	 * 
	 * @return
	 */
	protected Collection<ChangeSetProcessor> getChangeSetProcessors(StagingArea stagingArea, RevisionSearcher index) throws IOException {
		return Collections.emptySet();
	}

	/**
	 * Subclasses may override this method to execute additional logic after the processing of the changeset, but before committing it.
	 * 
	 * @param staging - the staging area before committing it to the repository
	 */
	protected void postUpdateDocuments(StagingArea staging) {
	}

//	@SuppressWarnings("restriction")
//	private void checkAndSetCodeSystemLastUpdateTime(final CDOObject component, ImmutableIndexCommitChangeSet.Builder indexCommitChangeSet) {
//		final CodeSystemVersion version = (CodeSystemVersion) component;
//		final CDOFeatureDelta lastUpdateFeatureDelta = commitChangeSet.getRevisionDeltas().get(component.cdoID())
//				.getFeatureDelta(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate());
//		if (lastUpdateFeatureDelta instanceof org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) {
//			((org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) lastUpdateFeatureDelta)
//					.setValue(new Date(commitChangeSet.getTimestamp()));
//			((InternalCDORevision) component.cdoRevision()).set(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate(),
//					CDOStore.NO_INDEX, new Date(commitChangeSet.getTimestamp()));
//			final CodeSystemVersionEntry entry = CodeSystemVersionEntry.builder(version).build();
//			indexCommitChangeSet.putChangedObject(ComponentIdentifier.of(CodeSystemVersionEntry.TERMINOLOGY_COMPONENT_ID, entry.getVersionId()), entry);
//		}
//	}

}
