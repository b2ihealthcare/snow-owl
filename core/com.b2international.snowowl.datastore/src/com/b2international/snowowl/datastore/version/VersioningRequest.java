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
package com.b2international.snowowl.datastore.version;

import java.util.Date;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;

/**
 * {@link VersioningRequest} that will create a 
 * 
 * @since 7.0
 */
public class VersioningRequest implements Request<TransactionContext, Boolean> {

	private static final String NEW_VERSION_COMMIT_COMMENT_TEMPLATE = "Created new version '%s' for %s.";
	private static final String ADJUST_EFFECTIVE_TIME_COMMIT_COMMENT_TEMPLATE = "Adjusted effective time to '%s' for %s version '%s'.";
	
	private final VersioningConfiguration config;

	public VersioningRequest(VersioningConfiguration config) {
		this.config = config;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		final Logger log = context.log();
		CodeSystemVersionEntry version = getVersion(context);
		if (version != null) {
			
		}
		
		context.add(createVersion(context, config));
//		final IBranchPath branchPath = BranchPathUtils.createPath(getBranchPathForPublication(configuration));
//		publishTerminologyChanges(editingContext, configuration);
//		logWork(monitor);
//
//		LOGGER.info("Processing terminology metadata changes...");
//		if (couldCreateVersion(configuration)) {
//			createCodeSystemVersion(editingContext, configuration);
//		} else {
//			setFakeLastUpdateTimeOnExistingVersion(editingContext, configuration);
//		}
//		LOGGER.info("Terminology metadata change processing successfully finished.");
//		logWork(monitor);
		return null;
	}
	
//	private void doCommitChanges(final CDOEditingContext editingContext, VersioningConfiguration conf, final IProgressMonitor monitor) throws SnowowlServiceException {
//		try {
//			new CDOServerCommitBuilder(conf.getUser(), getCommitComment(conf), editingContext.getTransaction())
//				.parentContextDescription(DatastoreLockContextDescriptions.CREATE_VERSION)
//				.commit();
//		} catch (final CommitException e) {
//			throw new SnowowlServiceException(e.getMessage(), e);
//		} finally {
//			if (null != monitor) {
//				monitor.worked(1);
//			}
//		}
//	}
	/**Returns with the commit comment for the version operation. */
	private String getCommitComment(final VersioningConfiguration conf) {
//		final Optional<CodeSystemVersionEntry> optional = FluentIterable
//				.from(existingVersions.get(primaryToolingId))
//				.firstMatch(input -> input.getVersionId().equals(versionId));
//		if (optional.isPresent()) {
//			return format(ADJUST_EFFECTIVE_TIME_COMMIT_COMMENT_TEMPLATE, EffectiveTimes.format(effectiveTime), toolingName, versionId);
//		} else {
			return String.format(NEW_VERSION_COMMIT_COMMENT_TEMPLATE, conf.getVersionId(), conf.getCodeSystemShortName());
//		}
	}
	
	private void logWork(final IProgressMonitor monitor) {
		if (null != monitor) {
			monitor.worked(1);
		}
	}

	/**
	 * Adjusts the properties of the given revision for the publication.
	 * <p>
	 * By default this method:
	 * <ul>
	 * <li>Sets the effective time on the component.</li>
	 * <li>Sets the released flag on the component (if supported).</li>
	 * </ul>
	 */
//	protected void adjustComponentForPublication(final CDOEditingContext context, final CDORevision revision, final Object effectiveTime) {
//		// mark components as changed
//		final CDOID cdoId = revision.getID();
//		final Map<CDOID, CDORevisionDelta> revisionDeltas = context.getTransaction().getLastSavepoint().getRevisionDeltas();
//		InternalCDORevisionDelta revisionDelta = (InternalCDORevisionDelta) revisionDeltas.get(cdoId);
//		if (null == revisionDelta) {
//			revisionDelta = (InternalCDORevisionDelta) createDelta(revision);
//			revisionDeltas.put(cdoId, revisionDelta);
//		}
//
//		// adjust values by creating featured deltas for the revision delta
//		setEffectiveTimeOnComponent(revisionDelta, effectiveTime);
//		setReleased(revisionDelta);
//	}

	/**
	 * Returns {@code true} if a component given with its {@link EClass} has to be ignored from the publication process. Otherwise returns with
	 * {@code false}.
	 * <p>
	 * By default this method always returns with {@code false}.
	 */
//	protected boolean isIgnoredType(final EClass eClass) {
//		return false;
//	}

	/**
	 * Performs any arbitrary pre-processing operation before the publication.
	 * <p>
	 * Does nothing by default. Clients may extend this method.
	 */
//	protected void preProcess(final LongSet storageKeys, VersioningConfiguration config) {
//	}

	/**
	 * Performs any arbitrary post-processing operation after adjusting the desired properties of all unversioned components and before committing the
	 * changes.
	 * <p>
	 * Does nothing by default. Clients may extend this method.
	 */
//	protected void postProcess(CDOEditingContext editingContext, VersioningConfiguration config) { }

	/** Adjusts all un.versioned components given as a set of component storage keys. */
//	protected void adjustComponents(final CDOEditingContext editingContext, final LongSet storageKeys, final Object effectiveTime) {
//		LOGGER.info("Adjusting effective time on components...");
//		for (final CDORevision revision : CDOUtils.getRevisions(editingContext.getTransaction(), transform(storageKeys, STORAGE_KEY_TO_CDO_ID_FUNCTION))) {
//			publishComponent(editingContext, revision, effectiveTime);
//		}
//		LOGGER.info("Effective time adjustment successfully finished.");
//	}

//	/** Loads and returns with the CDO object given by the unique CDO ID argument. */
//	protected CDOObject loadObject(final long storageKey) {
//		return checkNotNull(getObjectIfExists(getTransaction(), storageKey), "Component cannot be found in " + getTransaction() + " with CDOID: "
//				+ storageKey + ".");
//	}

	/** 
	 * Returns with the branch path for the given transaction to perform the publication. This is version dependent. 
	 * @param config 
	 */
//	protected final String getBranchPathForPublication(VersioningConfiguration config) {
//		return couldCreateVersion(config) ? config.getParentBranchPath() : String.format("%s%s%s", config.getParentBranchPath(), Branch.SEPARATOR, config.getVersionId());
//	}

	/** Returns with a value representing that an object is released. This value will be dynamically set on the object's feature. */
//	protected Object getReleasedValue() {
//		return TRUE;
//	}

//	protected abstract LongSet getUnversionedComponentStorageKeys(String branch);

//	private boolean couldCreateVersion(final VersioningConfiguration configuration) {
//		return CodeSystemRequests
//				.prepareSearchCodeSystemVersion()
//				.setLimit(0)
//				.filterByCodeSystemShortName(configuration.getCodeSystemShortName())
//				.filterByVersionId(configuration.getVersionId())
//				.build(getRepositoryUuid())
//				.execute(getEventBus())
//				.getSync().getTotal() == 0;
//	}
	
//	private void setFakeLastUpdateTimeOnExistingVersion(final CDOEditingContext editingContext, final VersioningConfiguration configuration) {
//		final CodeSystemVersionEntry version = getVersion(configuration);
//		checkNotNull(version, String.format("Code system version cannot be found with ID: %s.", configuration.getVersionId()));
//
//		final CodeSystemVersion codeSystemVersion = (CodeSystemVersion) editingContext.lookupIfExists(version.getStorageKey());
//		checkNotNull(codeSystemVersion, String.format("Code System version does not exist in store. ID: %s, Version ID: %s.",
//				version.getStorageKey(), configuration.getVersionId()));
//		
//		codeSystemVersion.setLastUpdateDate(CodeSystemVersionEntry.FAKE_LAST_UPDATE_TIME_DATE);
//	}

	@Nullable
	private CodeSystemVersionEntry getVersion(TransactionContext context) {
		return CodeSystemRequests
				.prepareSearchCodeSystemVersion()
				.setLimit(2)
				.filterByCodeSystemShortName(config.getCodeSystemShortName())
				.filterByVersionId(config.getVersionId())
				.build()
				.execute(context)
				.first()
				.orElse(null);
	}
	
	/** Processes all changes for the given terminology as a part of the publication. */
//	private void publishTerminologyChanges(CDOEditingContext editingContext, VersioningConfiguration config) throws SnowowlServiceException {
//		LOGGER.info("Collecting unversioned components...");
//		final LongSet storageKeys = getUnversionedComponentStorageKeys(getBranchPathForPublication(config));
//		LOGGER.info("Unversioned components have been successfully collected.");
//		preProcess(storageKeys, config);
//		adjustComponents(editingContext, storageKeys, config.getEffectiveTime());
//		postProcess(editingContext, config);
//	}

	/** Published a component given by its unique storage key. */
//	private void publishComponent(CDOEditingContext editingContext, final CDORevision revision, final Object effectiveTime) {
//		if (!isIgnoredType(revision.getEClass())) {
//			adjustComponentForPublication(editingContext, revision, effectiveTime);
//		}
//	}

	/** Sets the released flag on the given component to {@code true}. */
//	private void setReleased(final InternalCDORevisionDelta revisionDelta) {
//		final EStructuralFeature releasedFeature = getReleasedFeature(revisionDelta.getEClass());
//		revisionDelta.addFeatureDelta(createSetFeatureDelta(releasedFeature, NO_INDEX, getReleasedValue()));
//	}

	/** Sets the effective time on the object for the desired value. */
//	private void setEffectiveTimeOnComponent(final InternalCDORevisionDelta revisionDelta, final Object effectiveTime) {
//		final EStructuralFeature effectiveTimeFeature = getEffectiveTimeFeature(revisionDelta.getEClass());
//		revisionDelta.addFeatureDelta(createSetFeatureDelta(effectiveTimeFeature, NO_INDEX, effectiveTime));
//	}

	private final CodeSystemVersionEntry createVersion(final TransactionContext context, final VersioningConfiguration config) {
		return CodeSystemVersionEntry.builder()
			.description(config.getDescription())
			.effectiveDate(config.getEffectiveTime().getTime())
			.importDate(new Date().getTime())
			.parentBranchPath(context.branchPath())
			.versionId(config.getVersionId())
			.codeSystemShortName(config.getCodeSystemShortName())
			.repositoryUuid(context.id())
			.build();
	}

}