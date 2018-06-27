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
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.events.metrics.MetricsThreadLocal;
import com.b2international.snowowl.core.events.metrics.Timer;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Base {@link Repository} pre-commit hook.    
 * 
 * @since 5.0
 */
public abstract class BaseCDOChangeProcessor implements Hooks.PreCommitHook {

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
		final ImmutableIndexCommitChangeSet.Builder indexCommitChangeSet = ImmutableIndexCommitChangeSet.builder();
		
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
				indexCommitChangeSet.putNewObject(getComponentIdentifier(revision), revision);
			}
			
			for (RevisionDocumentChange revisionChange : processor.getChangedMappings().values()) {
				indexCommitChangeSet.putChangedObject(getComponentIdentifier(revisionChange.getNewRevision()), revisionChange);
			}
			
			final Multimap<Class<? extends Revision>, String> deletions = processor.getDeletions();
			for (Class<? extends Revision> type : deletions.keySet()) {
				// TODO remove index.get call here, we already fetched the doc, now we do it again do delete it, see CDOCommitChangeSet
				for (RevisionDocument revision : Iterables.filter(index.get(type, deletions.get(type)), RevisionDocument.class)) {
					indexCommitChangeSet.putRemovedComponent(getComponentIdentifier(revision), revision);
				}
			}
		}

		IndexCommitChangeSet indexChangeSet = indexCommitChangeSet.build();
		postUpdateDocuments(indexChangeSet);
		indexChangeSet.apply(staging);
		LOG.info("Processing changes successfully finished.");
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
	protected Collection<ChangeSetProcessor> getChangeSetProcessors(StagingArea stagingArea, RevisionSearcher index) throws IOException {
		return Collections.emptySet();
	}

	/**
	 * Subclasses may override this method to execute additional logic after the processing of the changeset, but before committing it.
	 * 
	 * @param commitChangeSet - the commit change set about to be committed by this processor
	 */
	protected void postUpdateDocuments(IndexCommitChangeSet commitChangeSet) {
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
