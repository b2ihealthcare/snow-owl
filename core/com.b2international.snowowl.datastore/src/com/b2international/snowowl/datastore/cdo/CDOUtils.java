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
package com.b2international.snowowl.datastore.cdo;

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.transaction.CDOPushTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.internal.cdo.CDOObjectImpl.CDOStoreSettingsImpl;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.net4j.util.lifecycle.LifecycleState;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Utility class for CDO objects and for their underlying CDO views.
 */
public abstract class CDOUtils {

	private static final int MAX_REVISION_LIMIT = 1_000_000;
	
	/**
	 * Storage key representing object with temporary storage key. Value: {@value}.
	 */
	public static final long NO_STORAGE_KEY = -1L;

	/**
   * CDO reference adjuster to replace an actual CDO object instance with its CDO ID. 
   */
	public static enum CDOObjectToCDOIDAdjuster implements CDOReferenceAdjuster {
		INSTANCE;

		@Override
		public Object adjustReference(final Object id, final EStructuralFeature feature, final int index) {
			return id instanceof CDOObject ? ((CDOObject) id).cdoID() : id;
		}
	}
	
	/**Opens and returns with a new session with the given user ID for a repository.*/
	public static InternalSession openSession(final String userID, final String uuid) {
		return getSessionManager(uuid).openSession(new ImpersonatingSessionProtocol(userID));
	}
	
	/*returns with the session manager*/
	private static InternalSessionManager getSessionManager(final String uuid) {
		return getRepositoryByUuid(uuid).getSessionManager();
	}
	
	/**Returns with the repository identified by its unique ID.*/
	public static InternalRepository getRepositoryByUuid(final String uuid) {
		return (InternalRepository) getRepositoryManager().getByUuid(uuid).getRepository();
	}
	
	/*returns with the repository manager service*/
	private static ICDORepositoryManager getRepositoryManager() {
		return ApplicationContext.getInstance().getService(ICDORepositoryManager.class);
	}
	
	/*returns with the repository*/
	public static IRepository getRepository(final CDOID cdoId) {
		return getRepositoryManager().get(cdoId).getRepository();
	}
	
	/*returns with the repository*/
	public static IRepository getRepository(final long cdoId) {
		return getRepositoryManager().get(cdoId).getRepository();
	}
	
	/*returns with the revision manager sticked with the underlying repository instance*/
	public static InternalCDORevisionManager getRevisionManager(final CDOID cdoId) {
		return (InternalCDORevisionManager) getRepository(cdoId).getRevisionManager();
	}
	
	/**
	 * Returns with the revisions for the given CDO IDs on a specified {@link CDOBranchPoint branch point}.
	 * @param branchPoint the branch point to get the revisions.
	 * @param ids the CDO IDs.
	 * @return a list of CDO revisions.
	 */
	public static List<CDORevision> getRevisions(final CDOBranchPoint branchPoint, final Collection<CDOID> ids) {
		
		if (isEmpty(ids)) {
			return emptyList();
		}
		
		try {

			final CDOID cdoId = Iterables.get(ids, 0);
			StoreThreadLocal.setAccessor(getAccessor(cdoId));
			
			final List<CDORevision> revisions = newArrayList();
			final Iterator<List<CDOID>> itr = Iterators.partition(ids.iterator(), MAX_REVISION_LIMIT);
			final InternalCDORevisionManager revisionManager = getRevisionManager(cdoId);
			
			while (itr.hasNext()) {
				//get revisions at once but at most 1,000,000
				revisions.addAll(revisionManager.getRevisions(
						newArrayList(itr.next()), 
						branchPoint, 
						CDORevision.UNCHUNKED,
						CDORevision.DEPTH_NONE,
						true,
						null));
				
			}
			
			return revisions;
			
		} finally {
			
			//release resources
			StoreThreadLocal.release();
			
		}
		
	}
	
	/*returns with the DB store*/
	private static IDBStore getDbStore(final CDOID cdoId) {
		return (IDBStore) getRepository(cdoId).getStore();
	}
	
	/*returns with the DB accessor*/
	public static IDBStoreAccessor getAccessor(final CDOID cdoId) {
		return getDbStore(cdoId).getWriter(null);
	}
	
	/**
	 * Returns the value of the given feature of the object even if the object has been detached.
	 * <br>The returned value is always resolved.
	 * @param object the object where the value is required. Cannot be {@code null}.
	 * @param feature the feature of the value to fetch. Cannot be {@code null}.
	 * @param clazz the class of required return type. Cannot be {@code null}.
	 * @return the value of the given feature of the object.
	 * @param <T> type of the return value. 
	 */
	public static final <T> T getAttribute(@Nonnull final CDOObject object, @Nonnull final EStructuralFeature feature, @Nonnull final Class<T> clazz) {
		checkNotNull(object, "CDO object argument cannot be null.");
		checkNotNull(feature, "Structural feature argument cannot be null.");
		checkNotNull(clazz, "Class argument cannot be null.");
		checkArgument(object instanceof InternalEObject, "CDO object must be an org.eclipse.emf.spi.cdo.InternalCDOObject instance.");
		if (isTransient(object)) {
			return clazz.cast(CDOStoreSettingsImpl.INSTANCE.get((InternalEObject) object, feature, InternalEObject.EStore.NO_INDEX));
		}
		return clazz.cast(object.eGet(feature, true));
	}

	/**
	 * Returns {@code true} only and if only the specified object is not {@code null}. The object is not detached. (In other words its state is NOT {@link CDOState#TRANSIENT transient}.)
	 * And the underlying {@link CDOView CDO view} can be referenced. 
	 * @param object the CDO object to check. Can be {@code null}.
	 * @return {@code true} if the object can be referenced, otherwise returns with {@code false}.
	 * @see #checkView(CDOView)
	 */
	public static boolean checkObject(@Nullable final CDOObject object) {
		return null != object && !isTransient(object) && checkView(object.cdoView());
	}

	/**
	 * Returns {@code true} only and if only the specified CDO view instance can be referenced (it is not {@code null}) and
	 * is NOT closed.
	 * @param view the CDO view to check. Can be {@code null}.
	 * @return {@code true} if the view can be referenced. Otherwise this method returns with {@code false}.
	 */
	public static boolean checkView(@Nullable final CDOView view) {
		return null != view && !view.isClosed();
	}

	/**
	 * Ensures that an {@link CDOView} reference passed as a parameter to the calling method is not {@code null} AND active.
	 * @param view the {@link CDOView} to check.
	 * @return the non-null and active {@link CDOView} that was validated.
	 * @param <T> type of the {@link CDOView}
	 */
	public static <T extends CDOView> T check(final T view) {
		Preconditions.checkNotNull(view, "CDO view argument cannot be null.");
		if (view.isClosed()) {
			final StringBuilder sb = new StringBuilder();
			sb.append("Not active CDO view. [");
			sb.append(view);
			sb.append("].");
			throw new IllegalStateException(sb.toString());
		}
		return view;
	}
	
	/**
	 * Ensures that all element of the specified iterable and *NOT* {@code null} AND {@link LifecycleState#ACTIVE active}.
	 * @param views the views to check.
	 * @return the argument.
	 */
	public static <T extends CDOView> Iterable<T> check(final Iterable<T> views) {
		Preconditions.checkNotNull(views, "CDO views argument cannot be null.");
		Collections3.forEach(views, new Procedure<T>() {
			@Override protected void doApply(final T view) {
				check(view);
			}
		});
		return views;
	}
	
	/**
	 * Ensures that an {@link CDOObject} reference passed as a parameter to the calling method fulfills the followings:
	 * <ul>
	 * <li>*NOT* {@code null}.</li>
	 * <li>the associated CDO view is *NOT* {@code null}.</li>
	 * <li>the associated CDO view is active.</li>
	 * <li>the object is not transient.</li>
	 * </ul>
	 * </ul>
	 * @param object the {@link CDOObject} to check.
	 * @return the {@link CDOObject} that was validated against above described.
	 * @param <T> type of the {@link CDOObject}
	 */
	public static <T extends CDOObject> T check(final T object) {
		Preconditions.checkNotNull(object, "CDO object argument cannot be null.");
		check(object.cdoView());
		Preconditions.checkState(!FSMUtil.isTransient(object), "CDO object is transient: " + object);
		return object;
	}

	/**
	 * Executes the given CDO function and return with the output of the function.
	 */
	public static <T> T apply(final CDOFunction<T> function) {
		return Preconditions.checkNotNull(function, "CDO function argument cannot be null.").apply();
	}
	
	/**
	 * Returns {@code true} if the CDO state of the specified object is {@link CDOState#TRANSIENT transient} otherwise returns with {@code false}.
	 * @param object CDO object to check. Cannot be {@code null}.
	 * @return {@code true} if the object has {@link CDOState#TRANSIENT transient} state.
	 * */
	public static boolean isTransient(@Nonnull final CDOObject object) {
		return CDOState.TRANSIENT.equals(object.cdoState());
	}

	public static boolean isUnpersisted(@Nonnull final CDOObject object) {
		return CDOState.NEW.equals(object.cdoState());
	}

	/**
	 * @param object the CDO object to get the storage key for (may not be {@code null})
	 * @return {@code -1} if the object has a temporary CDOID, the long storage key of the object otherwise
	 */
	public static long getStorageKey(@Nonnull final CDOObject object) {
		return isUnpersisted(object) ? NO_STORAGE_KEY : CDOIDUtil.getLong(object.cdoID());
	}

	/**
	 * Returns with the CDO object looked up in the specified CDO view with a unique CDO ID.
	 * @param view the view where the lookup should be performed. Cannot be {@code null}. Cannot be closed. 
	 * @param cdoId the unique CDO ID of the object as long. 
	 * @return the CDO object if found. May return with {@code null} if the object was not found with the specified CDO ID.
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T extends CDOObject> T getObjectIfExists(@Nonnull final CDOView view, final long cdoId) {
		checkNotNull(view, "CDO view argument cannot be null.");
		checkState(!view.isClosed(), "CDO view was closed.");
		try {
			return (T) view.getObject(CDOIDUtil.createLong(cdoId));
		} catch (final ObjectNotFoundException ex) {
			return null;
		}
	}

	/**
	 * Returns with the CDO object looked up in the specified CDO view with a unique CDO ID.
	 * @param view the view where the lookup should be performed. Cannot be {@code null}. Cannot be closed. 
	 * @param cdoId the unique CDO ID of the object. Cannot be {@code null}. 
	 * @param <T> type of the CDO object.
	 * @return the CDO object if found. May return with {@code null} if the object was not found with the specified CDO ID.
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T extends CDOObject> T getObjectIfExists(@Nonnull final CDOView view, @Nonnull final CDOID cdoId) {
		checkNotNull(view, "CDO view argument cannot be null.");
		checkState(!view.isClosed(), "CDO view was closed.");
		checkNotNull(cdoId, "CDO ID argument cannot be null.");
		try {
			return (T) view.getObject(cdoId);
		} catch (final ObjectNotFoundException ex) {
			return null;
		}
	}

	/**
	 * This method checks whether the specified object can be referenced and all the references can be referenced as well.<br>
	 * Returns with {@code true} only and if only the specified state of the object fulfill the following constraints:
	 * <ol>
	 * <li>Specified object is *NOT* {@code null}.</li>
	 * <li>The CDO state of the object is *NOT* {@link CDOState#TRANSIENT transient}.</li>
	 * <li>The underlying CDO view is *NOT* {@code null}.</li>
	 * <li>The underlying CDO view is *NOT* closed.</li>
	 * <li>All the references of the specified object are *NOT* {@code null}.</li>
	 * <li>If any of the reference of the specified object is a {@link CDOObject CDO object}, then the reference will be investigated according to the {@code 1.}&nbsp;-&nbsp;{@code 4.} items.</li>  
	 * </ol>
	 * @param object the CDO object to check. Can be {@code null}.
	 * @return {@code true} if the object can be referenced. Otherwise returns {@code false}.
	 */
	public static boolean checkObjectRefernces(@Nullable final CDOObject object) {
		if (!checkObject(object)) {
			return false;
		}

		for (final EStructuralFeature structuralFeature : object.eClass().getEAllReferences()) {

			Object reference = null;

			try {
				reference = object.eGet(structuralFeature);
			} catch (final ObjectNotFoundException e) { //non existing object will cause runtime exception
				return false;
			}

			if (null == reference) {
				return false;
			}

			if (reference instanceof CDOObject) {
				if (!checkObject((CDOObject) reference)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Merges all the changes made on the old transaction to the other, clean transaction.
	 * @param oldTransaction the old transaction which changes should be merged onto the new one.
	 * @param newTransaction the new transaction. Cannot be {@link CDOTransaction#isDirty() dirty}.
	 */
	public static void mergeTransaction(final CDOTransaction oldTransaction, final CDOTransaction newTransaction) {
		checkState(!newTransaction.isDirty(), "New CDO transaction cannot be dirty.");
		if (!oldTransaction.isDirty()) {
			return; //nothing to do
		}
		((InternalCDOTransaction) newTransaction).applyChangeSetData(oldTransaction.getChangeSetData());
	}
	
	/**Resolves all the element proxies on every persisted many valued feature for the given CDO revision argument.*/
	@SuppressWarnings("restriction")
	public static void resolveElementProxies(final CDOObject objectToResolve) {

		checkNotNull(objectToResolve, "objectToResolve");
		final CDORevision revision = objectToResolve.cdoRevision();
		if (null == revision) {
			return;
		}

		final InternalCDORevision internalRevision = (InternalCDORevision) revision;
		final CDOClassInfo classInfo = internalRevision.getClassInfo();
		final ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(revision.getID());
		final org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl session = // 
		(org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl) connection.getSession();

		for (final EStructuralFeature feature : classInfo.getAllPersistentFeatures()) {

			if (feature.isMany()) {

				final CDOList list = internalRevision.getList(feature);

				if (list instanceof org.eclipse.emf.cdo.internal.common.revision.CDOListWithElementProxiesImpl) {

					for (int i = 0; i < list.size(); i++) {

						final Object object = list.get(i, true);

						if (object instanceof CDOElementProxy) {

							session.resolveElementProxy(internalRevision, feature, i, ((CDOElementProxy) object).getIndex());

						}

					}

				}

			}

		}

	}
	
	/***
	 * Applies the changes to the given transaction..
	 * <br>The object graph changes are given as a input stream.<br>Although the transaction will return with a brand
	 * new transaction instance, clients should not take care of closing both specified and returning transactions.
	 * Closing one of them will close both transaction, and properly releases the resources. 
	 * <br>The specified stream will be closed, cannot be referenced after invoking this method.
	 * @param transaction the transaction to apply changes. Should be clean transaction.
	 * @param is the object graph changes as a stream. Will be closed in anyway.
	 * @return the transaction containing the object graph changes read from the stream.
	 * @throws SnowowlServiceException if the stream cannot be referenced.
	 */
	public static CDOTransaction applyChanges(final CDOTransaction transaction, final InputStream is) throws SnowowlServiceException {
		
		Preconditions.checkNotNull(transaction, "CDO transaction argument cannot be null.");
		Preconditions.checkState(!transaction.isDirty(), "Applying changes to a dirty transaction is not supported.");

		try {
		
			final CDOPushTransaction $ = new CDOPushTransaction(transaction);
			$.importChanges(is, true);
			
			return $;
			
		} catch (final IOException e) {
			
			throw new SnowowlServiceException("Cannot apply changes to the given transaction. Reason: " + e.getMessage());
			
		} finally {
			
			if (null != is) {
				
				try {
					is.close();
				} catch (final IOException e) {
					try {
						is.close();
					} catch (final IOException e1) {
						e.addSuppressed(e1);
					}
				}
				
			}
			
		}
		
	}
	
	/**
	 * Adjusts all revisions of the given {@link CDOTransaction transaction} with the given {@link CDOReferenceAdjuster adjuster}.
	 * The following revisions will be adjusted:<p>
	 * <ul>
	 * <li>All revision deltas of the last save point of the CDO transaction.</li>
	 * <li>All new objects in the transaction.</li>
	 * <li>All detached objects in the transaction.</li>
	 * </ul> 
	 * @param transaction the transaction where the revision adjustment has to be performed.
	 * @param adjuster the reference adjuster.
	 * @see  CDOTransaction#getLastSavepoint()
	 * @see CDOReferenceAdjuster
	 */
	public static void adjustRevsions(final CDOTransaction transaction, final CDOReferenceAdjuster adjuster) {
		
		Preconditions.checkNotNull(transaction, "CDO transaction argument cannot be null.");
		Preconditions.checkNotNull(adjuster, "Reference adjuster argument cannot be null.");
		
		//adjust references at revision level for revision deltas
		final Map<CDOID, CDORevisionDelta> revisionDeltas = transaction.getLastSavepoint().getAllRevisionDeltas();
		for (final CDORevisionDelta dirtyObjectDelta : revisionDeltas.values()) {
			
			((InternalCDORevisionDelta) dirtyObjectDelta).adjustReferences(adjuster);
			
		}
		
		//adjust CDO IDs for new objects as well. e.g.: reference set member container
		for (final CDOIDAndVersion newObjectIdAndVersion : transaction.getChangeSetData().getNewObjects()) {

			if (newObjectIdAndVersion instanceof InternalCDORevision) {

				((InternalCDORevision) newObjectIdAndVersion).adjustReferences(adjuster);

			}

		}
		
		//adjust CDO IDs for detached objects as well. e.g.: reference set member container
		for (final CDOIDAndVersion detachedObjectIdAndVersion : transaction.getChangeSetData().getDetachedObjects()) {

			if (detachedObjectIdAndVersion instanceof InternalCDORevision) {

				((InternalCDORevision) detachedObjectIdAndVersion).adjustReferences(adjuster);

			}

		}
		
		
	}
	
	/**
	 * Copies the given object and all its related object graph to the destination editing context. Clients may decide whether the copied 
	 * object has to be added to its original container/resource or not. Clients may also enable fail fast behavior when performing the copy.
	 * @param object the object to copy.
	 * @param destinationContext the destination editing context,
	 * @param addToContainer {@code true} if the replicated object has to be added to its container or not.
	 * @param checkExistance flag to enable fail fast behavior. Client should consider performance drop when flag is {@code true}.
	 * @return the copied object.
	 * @throws SnowowlServiceException the copy operation failed.
	 */
	public static <T extends CDOObject, E extends CDOEditingContext> T copy(final T object, final E destinationContext, final boolean addToContainer, final boolean checkExistance) throws SnowowlServiceException {

		Preconditions.checkNotNull(object, "Object argument cannot be null.");
		Preconditions.checkNotNull(destinationContext, "Destination editing context argument cannot be null.");
		
		final CDOTransaction transaction = destinationContext.getTransaction();
		check(transaction);

		final Copier copier = new CDOViewAwareCopier(transaction);
		final EObject result = copier.copy(object);
		copier.copyReferences();

		@SuppressWarnings("unchecked")
		final T copy = (T) result;

		adjustReferences(copy, destinationContext, checkExistance);

		//attach replicated object to editing context's contents list.
		if (addToContainer) {
			destinationContext.add(copy);
		}

		return copy;

	}
	
	/**
	 * Creates and returns with a human readable representation of the given {@link CDOView view} as a {@link StringBuilder} instance.
	 */
	public static StringBuilder toString(final CDOView view) {
		
		Preconditions.checkNotNull(view, "CDO view argument cannot be null.");
		
		final StringBuilder sb = new StringBuilder();
		
		if (view instanceof CDOTransaction) {
			
			sb.append("\n\n======================CDO transaction\n======================\n");
			
		} else {
			
			sb.append("\n\n======================CDO view\n======================\n");
			
		}
		
		sb.append("\nID: ");
		sb.append(view.getViewID());
		
		sb.append("\nUser ID: ");
		sb.append(view.getSession().getUserID());
		
		sb.append("\nBranch point: ");
		sb.append(view);
		
		if (view instanceof CDOTransaction) {
			
			sb.append(((CDOTransaction) view).getLastSavepoint());
			
		}
		
		return sb;
		
	}
	
	/**
	 * Returns with an iterator for traversing the branch hierarchy from bottom to top. First element of the returning iterator 
	 * will be the branch give with the ID argument. The following items are the ancestor ones if any. If the given branch ID argument is 
	 * the MAIN branch ID, then this method returns with a single element iterator. 
	 * @param repositoryUuid the unique ID of the repository.
	 * @param branchId the unique CDOish ID of the branch.
	 * @return an iterator for traversing the branch hierarchy from bottom one to the top most MAIN branch. 
	 */
	public static Iterator<CDOBranch> bottomToTopIterator(final String repositoryUuid, final int branchId) {
		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
		final ICDOConnection connection = getConnection(repositoryUuid);
		final CDOBranch branch = Preconditions.checkNotNull(
				connection.getSession().getBranchManager().getBranch(branchId), 
				"Cannot find branch in '" + connection.getRepositoryName() + "' with ID: " + branchId);
		return bottomToTopIterator(branch);
	}
	
	/**
	 * Same as {@link #bottomToTopIterator(EPackage, int)}. {@link EPackage} argument is resolved to the corresponding 
	 * Application specific unique repository ID.
	 */
	public static Iterator<CDOBranch> bottomToTopIterator(final EPackage ePackage, final int branchId) {
		Preconditions.checkNotNull(ePackage, "EPackage argument cannot be null.");
		final ICDOConnection connection = getConnection(ePackage);
		final CDOBranch branch = Preconditions.checkNotNull(
				connection.getSession().getBranchManager().getBranch(branchId), 
				"Cannot find branch in '" + connection.getRepositoryName() + "' with ID: " + branchId);
		return bottomToTopIterator(branch);
	}
	
	/**
	 * Sugar for {@link #bottomToTopIterator(String, int)}.
	 */
	public static Iterator<CDOBranch> bottomToTopIterator(final CDOBranch branch) {
		Preconditions.checkNotNull(branch, "CDO branch argument cannot be null.");
		return bottomToTopIterator0(branch);
	}

	/**
	 * Creates and returns with a human readable representation of the given {@link CDOChangeSetData change set} as a {@link StringBuilder} instance.
	 */
	public static StringBuilder toString(final CDOChangeSetData changeSetData) {
		
		Preconditions.checkNotNull(changeSetData, "CDO change set data argument cannot be null.");
		
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\n======================CDO change set data\n======================\n");
		
		sb.append("\nNew objects: ");
		sb.append(Arrays.toString(changeSetData.getNewObjects().toArray()));
		
		sb.append("\nDetached objects: ");
		sb.append(Arrays.toString(changeSetData.getDetachedObjects().toArray()));
		
		sb.append("\nChanged objects: ");
		sb.append(Arrays.toString(changeSetData.getChangedObjects().toArray()));
		
		return sb;
	}
	
	/*returns with an iterator for traversing CDO branch hierarchy from bottom to top.*/
	private static Iterator<CDOBranch> bottomToTopIterator0(final CDOBranch branch) {
		Preconditions.checkNotNull(branch, "CDO branch argument cannot be null.");
		if (branch.isMainBranch()) {
			return Collections.singleton(branch).iterator();
		}
		
		final List<CDOBranch> $ = Lists.newArrayList();
		$.add(branch);
		CDOBranch ancestor = branch.getBase().getBranch();
		
		for (;;) {
			
			if (null == ancestor) {
				
				//reached repository creation time
				break;
				
			}
			
			$.add(ancestor);
			final CDOBranchPoint base = ancestor.getBase();
			ancestor = base.getBranch();
			
		}
		
		return Iterators.unmodifiableIterator($.iterator());
		
	}

	/*returns with the connection for the given repository UUID.*/
	private static ICDOConnection getConnection(final String repositoryUuid) {
		return Preconditions.checkNotNull(getConnectionManager()).getByUuid(Preconditions.checkNotNull(repositoryUuid));
	}
	
	/*returns with the connection associated with the epackage argument.*/
	private static ICDOConnection getConnection(final EPackage ePackage) {
		return Preconditions.checkNotNull(getConnectionManager()).get(Preconditions.checkNotNull(ePackage));
	} 
	
	/*returns with the connection manager.*/
	private static ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
	}
	

	/*Adjusts the references on the given object.*/
	private static <E extends CDOEditingContext> void adjustReferences(final EObject object, final E destinationContext, final boolean checkExistance) throws SnowowlServiceException {
		
		Preconditions.checkNotNull(object, "Object argument cannot be null.");
		Preconditions.checkNotNull(destinationContext, "CDO transaction argument cannot be null.");

		if (object instanceof CDOObject) {
		
			final EClass eClass = object.eClass();
			
			for (final EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
				
				if (feature.isMany()) {
					
					final Object values = object.eGet(feature);
					
					if (values instanceof Iterable<?>) {
						
						for (final EObject value : Iterables.filter((Iterable<?>) values, EObject.class)) {
							
							adjustReferences(value, destinationContext, checkExistance);
							
						}
						
					}
					
				} else {
					
					final Object attribute = CDOUtils.getAttribute((CDOObject) object, feature, Object.class);
					
					if (attribute instanceof CDOObject) {
						
						final CDOID cdoId = ((CDOObject) attribute).cdoID();
						
						//CDO ID could be null when setting cross references among transient objects
						//consider regular reference set and its member -> refSet feature of the member should be the refSet itself 
						if (null == cdoId) {
							
							object.eSet(feature, attribute);
							
						} else {
							
							if (checkExistance) {
								
								destinationContext.getTransaction().getObject(cdoId);
								
							}
							
							//CDO ID cannot be set as reference on transient objects
							if (FSMUtil.isTransient((CDOObject) object)) {
								
								//if the CDO object attribute is living in a different view and not in the destination transaction, copy that as well and
								//adjust references
								if (!((CDOObject) attribute).cdoView().equals(destinationContext.getTransaction())) {
									
									//recursively copy referenced till all CDO object reference is moved to the destination view
									final CDOObject copy = copy((CDOObject) attribute, destinationContext, true, checkExistance);
									
									object.eSet(feature, copy);
									
								} else {
								
									//already in the proper view
									object.eSet(feature, attribute);
									
								}
								
							} else {
								
								object.eSet(feature, cdoId);
								
							}
							
						}

						
					}
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Customized copier with overridden {@link Copier#get(Object)} method. 
	 * If copier instance does not found referenced object while building the 
	 * graph of objects among the processed ones, it checks its existence in
	 * the underlying {@link CDOView}.
	 *
	 */
	private static final class CDOViewAwareCopier extends Copier {

		private static final long serialVersionUID = 4362323745876748121L;
		private final CDOView view;

		private CDOViewAwareCopier(final CDOView view) {
			this.view = check(view);
		}
		
		/* (non-Javadoc)
		 * @see java.util.LinkedHashMap#get(java.lang.Object)
		 */
		@Override
		public EObject get(final Object key) {
			
			final EObject value = super.get(key);
			
			if (null != value) {
				
				return value;
				
			}
			
			if (key instanceof CDOObject) {
			
				final CDOObject object = CDOUtils.getObjectIfExists(view, ((CDOObject) key).cdoID());
				if (null != object) {
					return object;
				}
				
			} else if (key instanceof CDOID) {
				
				final CDOObject object = CDOUtils.getObjectIfExists(view, (CDOID) key);
				if (null != object) {
					return object;
				}
				
			}
			
			return null;
		}
		
		
	}

	private CDOUtils() { /*Suppress instantiation*/
	}

}