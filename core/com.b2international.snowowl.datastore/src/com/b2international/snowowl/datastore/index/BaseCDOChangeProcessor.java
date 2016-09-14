/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionIndexWrite;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.RevisionWriter;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.events.metrics.MetricsThreadLocal;
import com.b2international.snowowl.core.events.metrics.Timer;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Base class for updating indexes based on the new, dirty and detached components when a commit is in progress in the primary storage.
 * 
 * @since 5.0
 */
public abstract class BaseCDOChangeProcessor implements ICDOChangeProcessor {

	private final IBranchPath branchPath;
	private final RevisionIndex index;
	private final Logger log;
	private final Set<CodeSystem> newCodeSystems = newHashSet();
	private final Set<CodeSystem> dirtyCodeSystems = newHashSet();
	private final Set<CodeSystemVersion> newCodeSystemVersions = newHashSet();
	private final Set<CodeSystemVersion> dirtyCodeSystemVersions = newHashSet();

	private ICDOCommitChangeSet commitChangeSet;
	private final Map<String, Object> rawMappings = newHashMap();
	private final Map<Long, Revision> revisionMappings = newHashMap();

	private final Multimap<Class<? extends Revision>, Long> deletions = HashMultimap.create();

	public BaseCDOChangeProcessor(IBranchPath branchPath, RevisionIndex index) {
		this.branchPath = branchPath;
		this.index = index;
		this.log = LoggerFactory.getLogger("repository");
	}

	@Override
	public final IBranchPath getBranchPath() {
		return branchPath;
	}

	@Override
	public final String getUserId() {
		return commitChangeSet.getUserId();
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

			processNewCodeSystemsAndVersions(commitChangeSet);
			processDirtyCodeSystemsAndVersions(commitChangeSet);

			index.read(branchPath.getPath(), new RevisionIndexRead<Void>() {
				@Override
				public Void execute(RevisionSearcher index) throws IOException {
					updateDocuments(commitChangeSet, index);
					return null;
				}
			});
		} finally {
			indexTimer.stop();
		}
	}

	private void processNewCodeSystemsAndVersions(final ICDOCommitChangeSet commitChangeSet) {
		for (CDOObject object : commitChangeSet.getNewComponents()) {
			if (TerminologymetadataPackage.eINSTANCE.getCodeSystem().isSuperTypeOf(object.eClass())) {
				newCodeSystems.add((CodeSystem) object);
			} else if (TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(object.eClass())) {
				newCodeSystemVersions.add((CodeSystemVersion) object);
			}
		}
	}

	private void processDirtyCodeSystemsAndVersions(ICDOCommitChangeSet commitChangeSet2) {
		for (final CDOObject dirtyObject : commitChangeSet.getDirtyComponents()) {
			if (TerminologymetadataPackage.eINSTANCE.getCodeSystem().isSuperTypeOf(dirtyObject.eClass())) {
				dirtyCodeSystems.add((CodeSystem) dirtyObject);
			} else if (TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(dirtyObject.eClass())) {
				checkAndSetCodeSystemLastUpdateTime(dirtyObject);
			}
		}
	}

	private final void updateDocuments(ICDOCommitChangeSet commitChangeSet, RevisionSearcher index) throws IOException {
		log.info("Processing changes...");

		for (final CodeSystem newCodeSystem : newCodeSystems) {
			final CodeSystemEntry entry = CodeSystemEntry.builder(newCodeSystem).build();
			rawMappings.put(Long.toString(entry.getStorageKey()), entry);
		}

		for (final CodeSystemVersion newCodeSystemVersion : newCodeSystemVersions) {
			final CodeSystemVersionEntry entry = CodeSystemVersionEntry.builder(newCodeSystemVersion).build();
			rawMappings.put(Long.toString(entry.getStorageKey()), entry);
		}

		for (final CodeSystem dirtyCodeSystem : dirtyCodeSystems) {
			final CodeSystemEntry entry = CodeSystemEntry.builder(dirtyCodeSystem).build();
			rawMappings.put(Long.toString(entry.getStorageKey()), entry);
		}

		for (final CodeSystemVersion dirtyCodeSystemVersion : dirtyCodeSystemVersions) {
			final CodeSystemVersionEntry entry = CodeSystemVersionEntry.builder(dirtyCodeSystemVersion).build();
			rawMappings.put(Long.toString(entry.getStorageKey()), entry);
		}

		preUpdateDocuments(commitChangeSet, index);

		for (ChangeSetProcessor processor : getChangeSetProcessors()) {
			log.info("Collecting {}...", processor.description());
			processor.process(commitChangeSet, index);
			// register additions, deletions from the sub processor
			revisionMappings.putAll(processor.getMappings());
			deletions.putAll(processor.getDeletions());

		}

		postUpdateDocuments(Collections.unmodifiableMap(revisionMappings), Multimaps.unmodifiableMultimap(deletions));
		log.info("Processing changes successfully finished.");
	}

	/**
	 * Return a list of {@link ChangeSetProcessor}s to process the commit changeset.
	 * 
	 * @return
	 */
	protected abstract Collection<ChangeSetProcessor> getChangeSetProcessors();

	/**
	 * Subclasses may override this method to execute additional logic
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
	 * @param mappings
	 *            - the new and updated revisions
	 * @param deletions
	 *            - deleted revisions
	 */
	protected void postUpdateDocuments(Map<Long, Revision> mappings, Multimap<Class<? extends Revision>, Long> deletions) {
	}

	@Override
	public final void commit() throws SnowowlServiceException {
		final Timer indexTimer = MetricsThreadLocal.get().timer("indexing");
		try {
			indexTimer.start();
			checkState(commitChangeSet.getTimestamp() > 0, "Commit timestamp should be greater than zero");
			index.write(branchPath.getPath(), commitChangeSet.getTimestamp(), new RevisionIndexWrite<Void>() {
				@Override
				public Void execute(RevisionWriter writer) throws IOException {
					log.info("Persisting changes...");
					commitChanges(writer);
					log.info("Changes have been successfully persisted.");
					return null;
				}
			});
		} finally {
			indexTimer.stop();
		}
	}

	private void commitChanges(final RevisionWriter writer) throws IOException {
		for (Entry<String, Object> doc : rawMappings.entrySet()) {
			writer.writer().put(doc.getKey(), doc.getValue());
		}

		final Multimap<Class<? extends Revision>, Long> copiedRevision = ImmutableMultimap.copyOf(deletions);
		for (Class<? extends Revision> type : copiedRevision.keySet()) {
			writer.remove(type, copiedRevision.get(type));
		}

		for (Entry<Long, Revision> doc : revisionMappings.entrySet()) {
			if (!deletions.containsValue(doc.getKey())) {
				writer.put(doc.getKey(), doc.getValue());
			}
		}

		writer.commit();
	}

	/**
	 * @deprecated - would be great to use a single revision searcher
	 */
	protected final <T extends RevisionDocument> Iterable<T> getRevisions(final Class<T> type, final Iterable<Long> releasableStorageKeys) {
		return index.read(branchPath.getPath(), new RevisionIndexRead<Iterable<T>>() {
			@Override
			public Iterable<T> execute(RevisionSearcher index) throws IOException {
				return index.get(type, releasableStorageKeys);
			}
		});
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

	@Override
	public final boolean hadChangesToProcess() {
		return !commitChangeSet.isEmpty();
	}

	@Override
	public final String getChangeDescription() {
		return String.format("Updated documents %d, deleted documents %d.", revisionMappings.size() + rawMappings.size(), deletions.values().size());
	}

	@SuppressWarnings("restriction")
	private void checkAndSetCodeSystemLastUpdateTime(final CDOObject component) {
		final CodeSystemVersion version = (CodeSystemVersion) component;
		final CDOFeatureDelta lastUpdateFeatureDelta = commitChangeSet.getRevisionDeltas().get(component.cdoID())
				.getFeatureDelta(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate());
		if (lastUpdateFeatureDelta instanceof org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) {
			((org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) lastUpdateFeatureDelta)
					.setValue(new Date(commitChangeSet.getTimestamp()));
			((InternalCDORevision) component.cdoRevision()).set(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate(),
					CDOStore.NO_INDEX, new Date(commitChangeSet.getTimestamp()));
			dirtyCodeSystemVersions.add(version);
		}
	}

}
