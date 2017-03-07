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
package com.b2international.snowowl.datastore.server;

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.b2international.commons.platform.Extensions.getExtensions;
import static com.b2international.snowowl.datastore.ICodeSystemVersion.INITIAL_STATE;
import static com.b2international.snowowl.datastore.cdo.CDOCommitInfoConstants.INITIALIZER_COMMIT_COMMENT;
import static com.b2international.snowowl.datastore.cdo.CDOCommitInfoConstants.SYSTEM_USER_ID;
import static com.b2international.snowowl.datastore.cdo.CDORootResourceNameProvider.ROOT_RESOURCE_NAMEPROVIDER_EXTENSION_POINT_ID;
import static com.b2international.snowowl.datastore.server.ServerDbUtils.createCdoCreatedIndexOnTables;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterators.find;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableCollection;
import static org.eclipse.emf.cdo.common.branch.CDOBranchPoint.UNSPECIFIED_DATE;
import static org.eclipse.emf.cdo.common.id.CDOID.NULL;
import static org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES;
import static org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_EXACT_MATCH;
import static org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_FOLDER_ID;
import static org.eclipse.emf.cdo.common.revision.CDORevision.DEPTH_NONE;
import static org.eclipse.emf.cdo.common.revision.CDORevision.UNCHUNKED;
import static org.eclipse.emf.cdo.common.revision.CDORevisionUtil.createDelta;
import static org.eclipse.emf.cdo.eresource.EresourcePackage.Literals.CDO_RESOURCE;
import static org.eclipse.emf.cdo.server.StoreThreadLocal.release;
import static org.eclipse.emf.cdo.server.StoreThreadLocal.setAccessor;
import static org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.NEW_BRANCH;
import static org.eclipse.emf.cdo.spi.server.InternalSession.TEMP_VIEW_ID;
import static org.eclipse.emf.ecore.InternalEObject.EStore.NO_INDEX;
import static org.eclipse.net4j.util.lifecycle.LifecycleUtil.checkActive;
import static org.eclipse.net4j.util.lifecycle.LifecycleUtil.deactivate;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.transaction.TransactionException;
import org.slf4j.Logger;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.cdo.CDORootResourceNameProvider;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ImpersonatingSessionProtocol;
import com.b2international.snowowl.datastore.cdo.WriteAccessHandlerFilteringRepository;
import com.google.common.base.Predicate;

/**
 * Repository initializer implementation responsible for creating the
 * domain specific root resources.
 * @see CDOEditingContext
 */
public class RepositoryInitializer implements IRepositoryInitializer {

	private static final Logger LOGGER = getLogger(RepositoryInitializer.class);
	
	private String repositoryUuid;
	
	@Override
	public void initialize() {

		checkNotNull(repositoryUuid, "repositoryUuid");
		
		
		final ICDORepository repository = find(CDORepositoryManager.getInstance()._iterator(), new Predicate<ICDORepository>() {
			@Override
			public boolean apply(final ICDORepository repository) {
				return repositoryUuid.equals(repository.getUuid());
			}
		});
		
		checkNotNull(repository, "Repository argument cannot be null.");
		checkActive(repository);

		final InternalRepository internalRepository = (InternalRepository) repository.getRepository();
		final String repositoryUuid = repository.getUuid();
		
		final Collection<String> uninitializedResources = newHashSet();
		
		for (final String rootResourceName : getRootResourceNames(repositoryUuid)) {
			
			if (!exists(rootResourceName, repository)) {
				uninitializedResources.add(rootResourceName);
			}
			
		}
		
		//create DB indexes only on first start
		if (repository.getRepository().getStore().isFirstStart()) {
			//create DB indexes if required
			if (shouldCreateDbIndexes()) {
				createtDbIndexs(repository);
			}
			
		}
		
		if (isEmpty(uninitializedResources)) {
			
			//nothing to do
			return;
			
		}
		
		LOGGER.info("Initializing repository on first startup... [" + repository.getRepositoryName() + "]");
		
		initRootResources(uninitializedResources, internalRepository);
		
		if (shouldCreateVersionWithEmptyContent()) {
			createBranchForVersion(internalRepository, repository);
		}
		
		LOGGER.info("Repository initialization successfully finished. [" + repository.getRepositoryName() + "]");
		
	}
	
	/**
	 * (non-API)
	 * 
	 * Sets the repository UUID on the initializer.
	 */
	public void setRepositoryUuid(final String repositoryUuid) {
		this.repositoryUuid = checkNotNull(repositoryUuid, "repositoryUuid");
	}

	/**
	 * Returns with {@code true} if the repository content should be tagged right after the
	 * initialization. Otherwise {@code false}.
	 * <p>If {@code true} one could switch back to a version where the content is not available.
	 * <br>The version ID for the above described version is {@link ICodeSystemVersion#INITIAL_STATE}.
	 * <p>By default it always returns with {@code false}.
	 * @return {@code true} if the repository content should should be version after initialization. Otherwise {@code false}.
	 */
	protected boolean shouldCreateVersionWithEmptyContent() {
		return false;
	}
	
	/**
	 * Returns {@code true} if the DB table indexes should be created on the repository startup, otherwise {@code false}.
	 * <p>This method returns with {@code true} by default, subclasses may override this method.
	 * @return {@code true} if the DB indexes should be created by the current {@link RepositoryInitializer repository initializer}
	 * otherwise {@code false}.
	 */
	protected boolean shouldCreateDbIndexes() {
		return true;
	}
	
	/**
	 * Creates and additional new object revisions for the given root CDO resource revision and returns with them.
	 * <p>Returns with an empty collection by default. 
	 * @param resourceRevision the brand new CDO root resource revision. 
	 * @param metaRoot {@code true} if the new resource revision argument is a meta root. Otherwise {@code false}.
	 * @param repository the underlying repository which performing its very first start up.
	 * @return a collection of revisions representing a bunch of new objects that has to be added to the root revision
	 * argument.
	 */
	protected Collection<InternalCDORevision> createAdditionalRevisionsForResource(
			final InternalCDORevision resourceRevision, final boolean metaRoot, final InternalRepository repository) {
		
		return emptySet();
	}

	/**
	 * Generates and returns with a new unique CDO ID for the given CDO revision argument from the repository. 
	 * @param repository the repository where the new ID has to be generated.
	 * @param newRevision the new revision to generate a new ID for.
	 * @return the new generated CDO ID for the revision. 
	 */
	protected final CDOID getCdoIdForNewRevision(final InternalRepository repository, final InternalCDORevision newRevision) {
		return ((IDBStore) repository.getStore()).getIDHandler().getNextCDOID(newRevision);
	}
	
	/**
	 * Creates and returns with a new ADD CDO feature delta with the given new CDO resource CDO IDs.
	 * @param newRevsionId the new revision ID.
	 * @param index the list index to add the new revision.
	 * @return the new CDO add feature delta.
	 */
	@SuppressWarnings("restriction")
	protected CDOFeatureDelta createAddRevisionDelta(final CDOID newRevsionId, final int index) {
		return new org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl(
				EresourcePackage.eINSTANCE.getCDOResource_Contents(), 
				index, 
				newRevsionId);
	}
	
	/**Returns with an array of desired root resource unique names.*/
	private Collection<String> getRootResourceNames(final String repositoryUuid) {
		checkNotNull(repositoryUuid, "repositoryUuid");
		final Collection<String> rootResourceNames = newHashSet();
		for (final CDORootResourceNameProvider provider : getRootResourceNameProvidersForRepository(repositoryUuid)) {
			rootResourceNames.addAll(provider.getRootResourceNames());
		}
		return unmodifiableCollection(rootResourceNames);
	}
	
	private Collection<CDORootResourceNameProvider> getRootResourceNameProvidersForRepository(final String repsotiryUuid) {
		final Collection<CDORootResourceNameProvider> providers = newHashSet();
		for (final CDORootResourceNameProvider provider : getAllRootResourceProviders()) {
			if (repsotiryUuid.equals(checkNotNull(provider, "provider").getRepositoryUuid())) {
				providers.add(provider);
			}
		}
		return unmodifiableCollection(providers);
	}

	private Collection<CDORootResourceNameProvider> getAllRootResourceProviders() {
		return getExtensions(ROOT_RESOURCE_NAMEPROVIDER_EXTENSION_POINT_ID, CDORootResourceNameProvider.class);
	}

	/*creates the proper DB table indexes for the repository based on the associated Ecore models.*/
	private void createtDbIndexs(final ICDORepository repository) {
		createCdoCreatedIndexOnTables(repository, true);
	}

	/*creates the new revisions and the revision deltas representing new CDO root resource initialization.*/
	private void initRootResources(final Iterable<String> uninitializedResources, final InternalRepository repository) {
	
		checkNotNull(repository, "Repository argument cannot be null.");
		checkNotNull(!isEmpty(uninitializedResources), "Nothing to initialize.");
		
		final InternalCDORevisionManager revisionManager = repository.getRevisionManager();
		
		final Set<InternalCDORevision> newObjects = newHashSet();
		
		final String repositoryUuid = repository.getUUID();
		final Collection<CDORootResourceNameProvider> rootResourceNameProviders = getRootResourceNameProvidersForRepository(repositoryUuid);
		
		final CDOID rootCdoResourceCdoId = repository.getRootResourceID();
		final CDOBranchPoint branchPoint = getMainHead(repository);
		List<CDORevision> revisions = null;
		
		try {
			
			final IDBStore store = (IDBStore) repository.getStore();
			final IDBStoreAccessor accessor = store.getWriter(null);
			setAccessor(accessor);
			
			revisions = revisionManager.getRevisions(singletonList(rootCdoResourceCdoId), branchPoint, UNCHUNKED, DEPTH_NONE, true);
			
		} finally {
			
			release();
			
		}
		
		 
		checkState(!CompareUtils.isEmpty(revisions), "Cannot find CDO root resource revision.");
		checkState(1 == revisions.size(), "Expecting only one root resource revisions. Got '" + revisions.size() + "' instead.");		
		
		final InternalCDORevision rootCdoResourceRevision = (InternalCDORevision) getOnlyElement(revisions);
		final InternalCDORevisionDelta rootRevisionDelta = (InternalCDORevisionDelta) createDelta(rootCdoResourceRevision);
		
		for (final String resourceName : uninitializedResources) {
		
			//create revision for new domain specific root resource
			final InternalCDORevision newResourceRevision = createResourceRevsion(revisionManager);
			newResourceRevision.setBranchPoint(branchPoint);
			newResourceRevision.setContainerID(NULL);
			newResourceRevision.setContainingFeatureID(0);
			newResourceRevision.set(EresourcePackage.eINSTANCE.getCDOResourceNode_Name(), NO_INDEX, resourceName);
			
			final CDOID resourceCdoId = getCdoIdForNewRevision(repository, newResourceRevision);
			newResourceRevision.setID(resourceCdoId);
			newResourceRevision.setResourceID(rootCdoResourceCdoId);
			
			newObjects.add(newResourceRevision);
			
			final CDOList rootCdoResourceContentsCdoList = rootCdoResourceRevision.getList(EresourcePackage.eINSTANCE.getCDOResource_Contents());
			rootRevisionDelta.addFeatureDelta(createAddRevisionDelta(newResourceRevision.getID(), rootCdoResourceContentsCdoList.size()));
			
			final boolean metaRoot = any(rootResourceNameProviders, new Predicate<CDORootResourceNameProvider>() {
				@Override
				public boolean apply(final CDORootResourceNameProvider provider) {
					return provider.isMetaRootResource(resourceName);
				}
			});
			
			newObjects.addAll(createAdditionalRevisionsForResource(newResourceRevision, metaRoot, repository));
			
			
		}
		
		InternalSession session = null;
		InternalTransaction transaction = null;
		
		try {
			
			session = openSession(repository.getSessionManager());
			transaction = openTransaction(session, repository);
			
			final InternalCommitContext commitContext = createCommitContext(transaction);
			commitContext.setNewObjects(toArray(newObjects, InternalCDORevision.class));
			commitContext.setDirtyObjectDeltas(new InternalCDORevisionDelta[] { rootRevisionDelta });
			
			commitContext.preWrite();
			
	    commitContext.write(new Monitor());
	    commitContext.commit(new Monitor());


	    final String rollbackMessage = commitContext.getRollbackMessage();
			if (null != rollbackMessage) {
				throw new TransactionException(rollbackMessage);
			}

	    commitContext.postCommit(true);
			
		} finally {
			
			deactivate(session);
			deactivate(transaction);
			
			
		}
		
	}

	/*initialize a new CDO revision delta representing a CDO root resource.*/
	private InternalCDORevision createResourceRevsion(final InternalCDORevisionManager revisionManager) {
		return (InternalCDORevision) revisionManager.getFactory().createRevision(CDO_RESOURCE);
	}

	/*checks whether a CDO root resource already exists in the given repository with the unique root resource name.*/
	private boolean exists(final String rootResourceName, final ICDORepository repository) {
		
		checkNotNull(repository, "Repository argument cannot be null.");
		checkActive(repository);
		
		final InternalRepository internalRepository = (InternalRepository) repository.getRepository();
		final InternalSessionManager internalSessionManager = internalRepository.getSessionManager();
		final InternalQueryManager queryManager = internalRepository.getQueryManager();
		
		InternalSession session = null;
		InternalTransaction transaction = null;
		
		try {
			
			session = openSession(internalSessionManager);
			transaction = openTransaction(session, internalRepository);
			
			final InternalQueryResult result = queryManager.execute(transaction, getQueryInfo(rootResourceName));

			return result.hasNext();
			
		} finally {
			
			LifecycleUtil.deactivate(session);
			LifecycleUtil.deactivate(transaction);
			
		}
		
		
	}

	/*open a new internal transaction*/
	@SuppressWarnings("restriction")
	private InternalTransaction openTransaction(final InternalSession session, final InternalRepository internalRepository) {
		return new org.eclipse.emf.cdo.internal.server.Transaction(session, TEMP_VIEW_ID, getMainHead(internalRepository)) {
			@Override public InternalRepository getRepository() {
				return new WriteAccessHandlerFilteringRepository(internalRepository, false);
			}
		};
	}

	/*returns with a branch point representing the HEAD of the MAIN branch*/
	private CDOBranchPoint getMainHead(final InternalRepository internalRepository) {
		return getMainBranch(internalRepository).getHead();
	}

	/*returns with the MAIN branch*/
	private CDOBranch getMainBranch(final InternalRepository internalRepository) {
		return getBranchManager(internalRepository).getMainBranch();
	}

	/*returns with the branch manager*/
	private InternalCDOBranchManager getBranchManager(final InternalRepository internalRepository) {
		return internalRepository.getBranchManager();
	}
	
	/*opens and returns with a brand new internal session*/
	private InternalSession openSession(final InternalSessionManager internalSessionManager) {
		return internalSessionManager.openSession(new ImpersonatingSessionProtocol(SYSTEM_USER_ID));
	}
	
	/*creates and configures the query info instance*/
	@SuppressWarnings("restriction")
	private CDOQueryInfo getQueryInfo(final String rootResourceName) {
		
		checkNotNull(rootResourceName, "Root resource name argument cannot be null.");

		final org.eclipse.emf.cdo.internal.common.CDOQueryInfoImpl queryInfo = createQueryInfo(rootResourceName);
		queryInfo.addParameter(QUERY_LANGUAGE_RESOURCES_FOLDER_ID, NULL);
		queryInfo.addParameter(QUERY_LANGUAGE_RESOURCES_EXACT_MATCH, true);
		
		return queryInfo;
		
	}

	/*creates the query info instance*/
	@SuppressWarnings("restriction")
	private org.eclipse.emf.cdo.internal.common.CDOQueryInfoImpl createQueryInfo(final String rootResourceName) {
		checkNotNull(rootResourceName, "Root resource name argument cannot be null.");
		return new org.eclipse.emf.cdo.internal.common.CDOQueryInfoImpl(QUERY_LANGUAGE_RESOURCES, rootResourceName, null);
	}

	@SuppressWarnings("restriction")
	private org.eclipse.emf.cdo.internal.server.TransactionCommitContext createCommitContext(final InternalTransaction transaction) {
		return new org.eclipse.emf.cdo.internal.server.TransactionCommitContext(transaction) {
			@Override public String getUserID() { return SYSTEM_USER_ID; }
			@Override public String getCommitComment() { return INITIALIZER_COMMIT_COMMENT; }
		};
	}
	
	private void createBranchForVersion(final InternalRepository internalRepository, final ICDORepository repository) {
		final InternalCDOBranch mainBranch = internalRepository.getBranchManager().getMainBranch();
		final BranchInfo branchInfo = new BranchInfo(INITIAL_STATE, mainBranch.getID(), UNSPECIFIED_DATE);
		try {
			final IDBStoreAccessor accessor = repository.getDbStore().getWriter(null);
			StoreThreadLocal.setAccessor(accessor);
			internalRepository.createBranch(NEW_BRANCH, branchInfo);
		} finally {
			StoreThreadLocal.release();
		}
	}
	
}