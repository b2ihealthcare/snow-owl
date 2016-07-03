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
package com.b2international.snowowl.datastore.server.version;

import static com.b2international.commons.collect.LongSets.transform;
import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.ICodeSystemVersion.FAKE_LAST_UPDATE_TIME_DATE;
import static com.b2international.snowowl.datastore.cdo.CDOIDUtils.STORAGE_KEY_TO_CDO_ID_FUNCTION;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.getObjectIfExists;
import static com.b2international.snowowl.datastore.server.CDOServerUtils.getRevisions;
import static com.b2international.snowowl.datastore.server.version.GlobalPublishManagerImpl.ConfigurationThreadLocal.getConfiguration;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static org.eclipse.emf.cdo.common.revision.CDORevisionUtil.createDelta;
import static org.eclipse.emf.ecore.InternalEObject.EStore.NO_INDEX;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.slf4j.Logger;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.b2international.snowowl.datastore.version.IPublishManager;
import com.b2international.snowowl.datastore.version.IPublishOperationConfiguration;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersionGroup;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;


/**
 * Abstract component publish manager implementation.
 * 
 *
 */
public abstract class PublishManager implements IPublishManager {

	/** Shared logger instance. */
	protected static final Logger LOGGER = getLogger(PublishManager.class);

	private static final String NEW_VERSION_CREATED_TEMPLATE = "New version ''{0}'' has been successfully created for {1}.";

	private Supplier<CDOEditingContext> editingContextSupplier = Suppliers.memoize(new Supplier<CDOEditingContext>() {
		@Override
		public CDOEditingContext get() {
			return createEditingContext(getBranchPathForPublication());
		}
	});

	@Override
	public void publish(final ICDOTransactionAggregator aggregator, final String toolingId, final IPublishOperationConfiguration configuration,
			final IProgressMonitor monitor) throws SnowowlServiceException {

		try {

			ToolingIdThreadLocal.setToolingId(toolingId);
			aggregator.add(getTransaction());
			publishTerminologyChanges();
			logWork(monitor);
			publishTerminologyMetadateChanges(configuration);
			logWork(monitor);

		} catch (final SnowowlServiceException e) {
			handleError(e);
		} finally {
			ToolingIdThreadLocal.reset();
		}

		format(NEW_VERSION_CREATED_TEMPLATE, configuration.getVersionId(), getToolingName());

	}
	
	@Override
	public void postCommit() {
		// do nothing by default
	}
	
	/**
	 * Create a terminology specific {@link CDOEditingContext} to use when publishing content in that terminology.
	 * 
	 * @return
	 */
	protected abstract CDOEditingContext createEditingContext(IBranchPath branchPath);

	private void logWork(final IProgressMonitor monitor) {
		if (null != monitor) {
			monitor.worked(1);
		}
	}

	/** Returns with the repository UUID for the underling terminology. */
	protected abstract String getRepositoryUuid();

	/** Returns with the effective time feature for the given EClass. */
	protected abstract EStructuralFeature getEffectiveTimeFeature(final EClass eClass);

	/** Returns with the released structural feature. */
	protected abstract EStructuralFeature getReleasedFeature(final EClass eClass);

	/** Returns with the CDO editing context for the update process. */
	protected final CDOEditingContext getEditingContext() {
		return editingContextSupplier.get();
	}

	/** Creates and returns with the new code systems version. */
	protected abstract CodeSystemVersion createCodeSystemVersion();

	/**
	 * Creates a new code system for the underlying terminology/content. By default returns with {@code null}. If this method returns with
	 * {@code null} the new code system will not get persisted even if its absence.
	 */
	protected CodeSystem createCodeSystem() {
		return null;
	}

	/** Returns with the desired effective time date. */
	@Nullable
	protected Date getEffectiveTime() {
		return getConfiguration().getEffectiveTime();
	};

	/**
	 * Adjusts the properties of the given revision for the publication.
	 * <p>
	 * By default this method:
	 * <ul>
	 * <li>Sets the effective time on the component.</li>
	 * <li>Sets the released flag on the component (if supported).</li>
	 * </ul>
	 */
	protected void adjustComponentForPublication(final CDORevision revision) {

		// mark components as changed
		final CDOID cdoId = revision.getID();
		final Map<CDOID, CDORevisionDelta> revisionDeltas = getTransaction().getLastSavepoint().getRevisionDeltas();
		InternalCDORevisionDelta revisionDelta = (InternalCDORevisionDelta) revisionDeltas.get(cdoId);
		if (null == revisionDelta) {
			revisionDelta = (InternalCDORevisionDelta) createDelta(revision);
			revisionDeltas.put(cdoId, revisionDelta);
		}

		// adjust values by creating featured deltas for the revision delta
		setEffectiveTimeOnComponent(revisionDelta);
		setReleased(revisionDelta);

	}

	/**
	 * Returns {@code true} if a component given with its {@link EClass} has to be ignored from the publication process. Otherwise returns with
	 * {@code false}.
	 * <p>
	 * By default this method always returns with {@code false}.
	 */
	protected boolean isIgnoredType(final EClass eClass) {
		return false;
	}

	/**
	 * Performs any arbitrary pre-processing operation before the publication.
	 * <p>
	 * Does nothing by default. Clients may extend this method.
	 */
	protected void preProcess(final LongSet storageKeys) {
	}

	/**
	 * Performs any arbitrary post-processing operation after adjusting the desired properties of all unversioned components and before committing the
	 * changes.
	 * <p>
	 * Does nothing by default. Clients may extend this method.
	 */
	protected void postProcess() {
	}

	/** Returns with the {@link CodeSystemVersionGroup code system version group} instance for the underlying repository. */
	protected CodeSystemVersionGroup getCodeSystemVersionGroup() {
		return check(getEditingContext().getCodeSystemVersionGroup());
	}

	/** Adjusts all un.versioned components given as a set of component storage keys. */
	protected void adjustComponents(final LongSet storageKeys) {
		LOGGER.info("Adjusting effective time on components...");
		for (final CDORevision revision : getRevisions(getTransaction(), transform(storageKeys, STORAGE_KEY_TO_CDO_ID_FUNCTION))) {
			publishComponent(revision);
		}
		LOGGER.info("Effective time adjustment successfully finished.");
	}

	/** Return with the version name. */
	protected String getVersionName() {
		return getConfiguration().getVersionId();
	}

	/** Returns with the primary component ID for the underlying tooling feature. */
	protected String getPrimaryComponentId() {
		return CoreTerminologyBroker.getInstance().getPrimaryComponentIdByTerminologyId(getToolingId());
	}

	/** Loads and returns with the CDO object given by the unique CDO ID argument. */
	protected CDOObject loadObject(final long storageKey) {
		return checkNotNull(getObjectIfExists(getTransaction(), storageKey), "Component cannot be found in " + getTransaction() + " with CDOID: "
				+ storageKey + ".");
	}

	/** Returns with the branch path for the given transaction to perform the publication. This is version dependent. */
	protected final IBranchPath getBranchPathForPublication() {
		return null == getConfiguration() ? getMainPath() : couldCreateVersion(getConfiguration()) ? getMainPath() : createPath(getMainPath(),
				getConfiguration().getVersionId());
	}

	/** Returns with the underlying transaction for the publication process. */
	protected CDOTransaction getTransaction() {
		return getEditingContext().getTransaction();
	}

	/** Returns with a value representing that an object is released. This value will be dynamically set on the object's feature. */
	protected Object getReleasedValue() {
		return TRUE;
	}

	/** Returns with a value representing the effective time of the object. This value will be dynamically set on the object's feature. */
	protected Object getEffectiveTimeValue() {
		return getEffectiveTime();
	}

	/** Processes all changes for the given terminology as a part of the publication. */
	private void processTerminologyChanges() {
		checkNotNull(getConfiguration(), "Publish operation configuration was null.");
		LOGGER.info("Collecting unversioned components...");
		final LongSet storageKeys = getUnversionedComponentStorageKeys(getBranchPathForPublication());
		LOGGER.info("Unversioned components have been successfully collected.");
		preProcess(storageKeys);
		adjustComponents(storageKeys);
		postProcess();
	}

	protected abstract LongSet getUnversionedComponentStorageKeys(IBranchPath branchPath);

	private boolean couldCreateVersion(final IPublishOperationConfiguration configuration) {
		return !newHashSet(transform(getAllVersions(createMainPath()), new Function<ICodeSystemVersion, String>() {
			@Override
			public String apply(final ICodeSystemVersion version) {
				return checkNotNull(version).getVersionId();
			}
		})).contains(configuration.getVersionId());
	}

	private Collection<ICodeSystemVersion> getAllVersions(final IBranchPath branchPath) {
		return ImmutableList.<ICodeSystemVersion>copyOf(new CodeSystemRequests(getRepositoryUuid()).prepareSearchCodeSystemVersion()
				.all()
				.build(createMainPath().getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync().getItems());
	}

	private void publishTerminologyMetadateChanges(final IPublishOperationConfiguration configuration) throws SnowowlServiceException {
		if (couldCreateVersion(configuration)) {
			processTerminologyMetadataChanges();
		} else {
			setFakeLastUpdateTimeOnExistingVersion(configuration);
		}
	}

	private void setFakeLastUpdateTimeOnExistingVersion(final IPublishOperationConfiguration configuration) {
		final String versionId = configuration.getVersionId();
		final ICodeSystemVersion version = getVersion(getBranchPathForPublication(), versionId);
		checkNotNull(version, "Code system version cannot be found with ID: " + versionId);
		final long versionStorageKey = version.getStorageKey();
		final CodeSystemVersion codeSystemVersion = (CodeSystemVersion) getObjectIfExists(getTransaction(), versionStorageKey);
		checkNotNull(codeSystemVersion, "Code system version does not exist in store. ID: " + versionStorageKey + " Version ID: " + versionId);
		codeSystemVersion.setLastUpdateDate(FAKE_LAST_UPDATE_TIME_DATE);
	}

	@Nullable
	private ICodeSystemVersion getVersion(final IBranchPath branchPath, final String versionId) {
		final ICodeSystemVersion version = uniqueIndex(getAllVersions(branchPath), new Function<ICodeSystemVersion, String>() {
			public String apply(final ICodeSystemVersion version) {
				return checkNotNull(version, "version").getVersionId();
			}
		}).get(versionId);
		return version;
	}

	private void publishTerminologyChanges() throws SnowowlServiceException {
		processTerminologyChanges();
	}

	private IBranchPath getMainPath() {
		return createMainPath();
	}

	/** Returns with the current tooling feature ID. */
	private String getToolingId() {
		return ToolingIdThreadLocal.getToolingId();
	}

	private String getToolingName() {
		return CoreTerminologyBroker.getInstance().getTerminologyName(getToolingId());
	}

	/** Published a component given by its unique storage key. */
	private void publishComponent(final CDORevision revision) {
		if (!isIgnoredType(revision.getEClass())) {
			adjustComponentForPublication(revision);
		}
	}

	/** Sets the released flag on the given component to {@code true}. */
	private void setReleased(final InternalCDORevisionDelta revisionDelta) {
		final EStructuralFeature releasedFeature = getReleasedFeature(revisionDelta.getEClass());
		revisionDelta.addFeatureDelta(createSetFeatureDelta(releasedFeature, NO_INDEX, getReleasedValue()));
	}

	/** Sets the effective time on the object for the desired value. */
	private void setEffectiveTimeOnComponent(final InternalCDORevisionDelta revisionDelta) {
		final EStructuralFeature effectiveTimeFeature = getEffectiveTimeFeature(revisionDelta.getEClass());
		revisionDelta.addFeatureDelta(createSetFeatureDelta(effectiveTimeFeature, NO_INDEX, getEffectiveTimeValue()));
	}

	@SuppressWarnings("restriction")
	private CDOSetFeatureDelta createSetFeatureDelta(final EStructuralFeature feature, final int index, final Object value) {
		return new org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl(feature, NO_INDEX, value);
	}

	/** Applies the code system changes for the publication process. */
	private void processTerminologyMetadataChanges() {
		LOGGER.info("Processing terminology metadata changes...");
		addCodeSystemVersion(getCodeSystemVersionGroup());
		if (!hasCodeSystem()) {
			final CodeSystem codeSystem = createCodeSystem();
			if (null != codeSystem) {
				getCodeSystemVersionGroup().getCodeSystems().add(codeSystem);
			}
		}
		LOGGER.info("Terminology metadata change processing successfully finished.");
	}

	private boolean hasCodeSystem() {
		return !isEmpty(getCodeSystemVersionGroup().getCodeSystems());
	}

	/** Adds a brand new code system version to the given code system argument. */
	private boolean addCodeSystemVersion(final CodeSystemVersionGroup group) {
		return group.getCodeSystemVersions().add(createAndInitializeCodeSystemVersion());
	}

	/** Creates a new code system version instance based on the publish configuration. */
	private CodeSystemVersion createAndInitializeCodeSystemVersion() {
		final CodeSystemVersion version = createCodeSystemVersion();
		version.setEffectiveDate(getEffectiveTime());
		version.setImportDate(new Date());
		version.setVersionId(getVersionName());
		version.setDescription(getCodeSystemVersionDescription());
		return version;
	}

	/** Returns with the code system description. */
	private String getCodeSystemVersionDescription() {
		return nullToEmpty(getConfiguration().getDescription());
	}

	private void handleError(final SnowowlServiceException e) throws SnowowlServiceException {
		LOGGER.error("Error while performing the publication.", e);
		throw e;
	}

	/** Class for storing the configuration in the thread local. */
	static final class ToolingIdThreadLocal {
		private static final ThreadLocal<String> TOOLING_ID_THREAD_LOCAL = new ThreadLocal<String>();

		static void setToolingId(final String toolingId) {
			TOOLING_ID_THREAD_LOCAL.set(toolingId);
		}

		static String getToolingId() {
			return TOOLING_ID_THREAD_LOCAL.get();
		}

		static void reset() {
			TOOLING_ID_THREAD_LOCAL.set(null);
		}
	}

}