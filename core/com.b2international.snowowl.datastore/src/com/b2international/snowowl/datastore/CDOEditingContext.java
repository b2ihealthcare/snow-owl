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
package com.b2international.snowowl.datastore;

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.commons.exceptions.Exceptions.extractCause;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOPushTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.FileUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.datastore.cdo.CDOQueryUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.tasks.TaskManager;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

/**
 * This class is a thin, generic wrapper around the underlying {@link CDOTransaction}. 
 * In order to make it terminology independent, it uses {@link EObject}s as opposed to 
 * specific classes.
 * 
 */
public abstract class CDOEditingContext implements AutoCloseable {

	/**
	 * Number of retires to successfully commit the changes from an editing context.
	 */
	public static final int COMMIT_STRIKES = 5;
	
	/**
	 * Shared logger instance.
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(CDOEditingContext.class);
	
	/**
	 * Underlying CDO transaction.
	 */
	protected final CDOTransaction transaction;

	/**
	 * @param ePackage - the primary ePackage of this editing context
	 */
	protected CDOEditingContext(EPackage ePackage) {
		this(ePackage, getActivePath(ePackage));
	}
	
	protected CDOEditingContext(final EPackage ePackage, final IBranchPath branchPath) {
		this(createTransaction(checkNotNull(ePackage, "ePackage"), checkNotNull(branchPath, "Branch path argument cannot be null.")));
	}
	
	protected CDOEditingContext(final CDOTransaction cdoTransaction) {
		this.transaction = CDOUtils.check(cdoTransaction);
	}
	
	public final String getBranch() {
		return BranchPathUtils.createPath(getTransaction()).getPath();
	}
	
	public final <T extends CDOObject> Iterable<T> getNewObjects(Class<T> type) {
		return ComponentUtils2.getNewObjects(getTransaction(), type);
	}
	
	public final <T extends CDOObject> Iterable<T> getDetachedObjects(Class<T> type) {
		return ComponentUtils2.getDetachedObjects(getTransaction(), type);
	}
	
	public final <T extends CDOObject> Iterable<T> getChangedObjects(Class<T> type) {
		return ComponentUtils2.getDirtyObjects(getTransaction(), type);
	}
	
	/*
	 * get the index from the database for an EObject on the given table.
	 * If the object is not in the database, a RTE will be thrown.
	 */
	public int getIndexFromDatabase(final CDOObject cdoObject, final CDOObject container, final String tableName)  {
		// the object must be a cdoObject, otherwise the database cannot contain it
		if (tableName == null) {
			throw new RuntimeException("Argument tableName must not be null.");
		}
		
		// params
		final long cdoId = CDOIDUtil.getLong(cdoObject.cdoID());
		final long containerId = CDOIDUtil.getLong(container.cdoID());
		final int version = container.cdoRevision().getVersion();
		
		// query the resources table, to get the index directly
		final String sqlGetIndexFormatted = DatastoreQueries.SQL_GET_INDEX_AND_BRANCH_FOR_VALUE.getQuery(tableName);
		final CDOQuery query = transaction.createQuery("sql", sqlGetIndexFormatted);
		query.setParameter(CDOQueryUtils.CDO_OBJECT_QUERY, false);
		query.setParameter("cdoId", cdoId);
		query.setParameter("containerId", containerId);
		query.setParameter("versionMaxAdded", version);
		// exec query
		final List<Object[]> result = query.getResult(Object[].class);
		// neither 0 nor more than 1 results are acceptable
		if (result.size() <= 0) {			
			throw new RuntimeException("Object not found in database. CDO ID: " + cdoObject.cdoID());
		}
		// search for the right row to delete
		int index = -1;
		for (final Object[] res : result) {
			final int object_index = (Integer) res[0];
			final int branch_id = (Integer) res[1];
			
			final Iterator<CDOBranch> itr = CDOUtils.bottomToTopIterator(getTransaction().getBranch());
			Preconditions.checkState(!CompareUtils.isEmpty(itr), "Cannot traverse to ancestor branch.");
			
			while(itr.hasNext()) {
				
				final CDOBranch branch = itr.next();
				if (branch.getID() == branch_id) {
					index = object_index;
					break;
				}
				
			}
			
		}

		return index;
	}

	
	public EObject lookup(final long storageKey) {
		return transaction.getObject(CDOIDUtil.createLong(storageKey));
	}
	
	public EObject lookupIfExists(final long storageKey) {
		return CDOUtils.getObjectIfExists(transaction, storageKey);
	}
	
	public <T extends EObject> T lookup(final String componentId, Class<T> type) {
		if (Strings.isNullOrEmpty(componentId)) {
			throw new ComponentNotFoundException(type.getSimpleName(), componentId);
		} else if (CodeSystem.class.isAssignableFrom(type)) {
			return (T) getCodeSystem(componentId);
		}
		
		final T component = getComponentLookupService(type).getComponent(componentId, getTransaction());
		if (null == component) {
			throw new ComponentNotFoundException(type.getSimpleName(), componentId);
		}
		return component;
	}
	
	protected <T> ILookupService<String, T, CDOView> getComponentLookupService(Class<T> type) {
		throw new UnsupportedOperationException("Lookup not supported for type: " + type.getName());
	}

	/**
	 * Commits the content of the transaction into the underlying Snow Owl storage.
	 * 
	 * @throws SnowowlServiceException if either concurrent modification is received from the server of commit failed.
	 */
	public CDOCommitInfo commit(final String commitMessage) throws SnowowlServiceException {
		return commit(commitMessage, new NullProgressMonitor());
	}
	
	/**
	 * Commits the content of the transaction into the underlying Snow Owl storage.
	 * @param commitMessage - the commit message to associate with the commit, if it's longer than 250 characters, truncates it, and reports a warning
	 * @param monitor progress monitor the progress monitor for the commit process. Can be {@code null}.
	 * @throws SnowowlServiceException if either concurrent modification is received from the server of commit failed.
	 */
	public CDOCommitInfo commit(final String commitMessage, final IProgressMonitor monitor) throws SnowowlServiceException {
		
		final SubMonitor subMonitor = SubMonitor.convert(monitor);
		subMonitor.beginTask("Committing transaction", 1);
		
		try {
			
			preCommit();
			
			if (commitMessage == null) {
				transaction.setCommitComment("Snow Owl commit comment on " + new Date());
			} else {
				final String truncatedMessage = StringUtils.truncate(commitMessage, 255);
				if (!truncatedMessage.equals(commitMessage)) {
					LOGGER.warn("Truncated commit message (original message: {})", commitMessage);
				}
				transaction.setCommitComment(truncatedMessage);
			}
			
			final CDOCommitInfo commitInfo = transaction.commit(subMonitor);
			final IBranchPath branchPath = createPath(transaction);
			final String repositoryName = getServiceForClass(ICDOConnectionManager.class).get(transaction).getRepositoryName();
			final String userID = transaction.getSession().getUserID();
			if (isEmpty(commitMessage)) {
				LOGGER.info(format("{0} has committed the transaction on ''{1}'' branch from ''{2}''.", userID, branchPath, repositoryName));
			} else {
				LOGGER.info(format("{0} has committed the transaction on ''{1}'' branch from ''{2}'' with message: ''{3}''", userID, branchPath, repositoryName, commitMessage));
			}
			
			return commitInfo;
			
		} catch (final CommitException e) {

			final RepositoryLockException cause = extractCause(e, this.getClass().getClassLoader(), RepositoryLockException.class);
			
			if (null != cause) {
				
				final ICDOConnection connection = getApplicationContext().getService(ICDOConnectionManager.class).get(transaction);
				final String name = connection.getRepositoryName();
				
				final String message = StringUtils.isEmpty(cause.getMessage()) 
						? (name + " is currently locked on '" + BranchPathUtils.createPath(transaction).getPath() + "' branch. All kind of modifications are temporary disabled.")
						: cause.getMessage();
				
				final SnowowlServiceException sse = new SnowowlServiceException(message, cause);

				LOGGER.info(sse.getMessage());
				throw sse;
				
			}
			
			LOGGER.error(e.getMessage(), e);
			throw new SnowowlServiceException(e);
			
		} catch (final ConcurrentModificationException e) {
			
			LOGGER.error(e.getMessage(), e);
			throw new SnowowlServiceException("Mid-air collision detected; another client is already saving changes. Please try again in a few moments.", e);
			
		} finally {
			
			subMonitor.done();
			
		}
	}
	
	/**
	 * Rolls back the underlying transaction.
	 */
	public void rollback() {
		transaction.rollback();
	}

	/**
	 * Deactivates the underlying CDO transaction.
	 */
	@Override
	public void close() {
		transaction.close();
	}
	
	/**
	 * Returns whether this {@link CDOEditingContext} is already closed (<code>false</code>) or it's still open (<code>true</code>).
	 * @return
	 */
	public boolean isClosed() {
		return transaction.isClosed();
	}
	
	/**
	 * @return true if the underlying transaction is dirty, false otherwise
	 */
	public boolean isDirty() {
		return transaction.isDirty();
	}

	/**
	 * @return the underlying transaction
	 */
	public CDOTransaction getTransaction() {
		return transaction;
	}
	
	/**
	 * Returns with contents for the context as a collection of 
	 * domain objects.
	 * <p>By default delegates to the {@link CDOResource#getContents() contents}
	 * list of the root {@link CDOResource} for the underlying transaction.
	 * @return the contents list.
	 */
	@SuppressWarnings("unchecked")
	public <E extends EObject> List<E> getContents() {
		return (List<E>) getEditingContextRootResource().getContents();
	}
	
	/**
	 * @return the editing context's root resource
	 */
	public CDOResource getEditingContextRootResource() {
		return transaction.getOrCreateResource(getRootResourceName());
	}
	
	/**
	 * Returns with an immutable list of the available code systems for the
	 * repository where the current editing context works on.
	 * 
	 * @return an immutable list of the available code systems.
	 */
	public List<CodeSystem> getCodeSystems() {
		final CDOResource cdoResource = transaction.getOrCreateResource(getMetaRootResourceName());
		return FluentIterable.from(cdoResource.getContents()).filter(CodeSystem.class).toList();
	}
	
	public CodeSystem getCodeSystem(final String uniqueId) {
		if (!getBranch().equals(IBranchPath.MAIN_BRANCH)) {
			throw new IllegalStateException(String.format("Snomed releases are maintained on MAIN branch, this editing context uses %s", getBranch()));
		}
		
		final Optional<CodeSystem> optional = FluentIterable.from(getCodeSystems()).firstMatch(new Predicate<CodeSystem>() {
			@Override
			public boolean apply(final CodeSystem input) {
				return input.getShortName().equals(uniqueId) || input.getCodeSystemOID().equals(uniqueId);
			}
		});

		return optional.isPresent() ? optional.get() : null;
	}

	/**
	 * Adds the given code system to the available code systems.
	 * 
	 * @return <code>true</code> if the code system collection changed as a
	 *         result of the call.
	 * @deprecated use {@link #add(EObject)} instead
	 */
	public boolean addCodeSystem(final CodeSystem codeSystem) {
		final CDOResource cdoResource = transaction.getOrCreateResource(getMetaRootResourceName());
		return cdoResource.getContents().add(codeSystem);
	}

	/**
	 * Removes the given code system from the available code systems.
	 * 
	 * @return true if the code system was removed as a result of this call.
	 * @deprecated use {@link #delete(EObject)} instead
	 */
	public boolean removeCodeSystem(final CodeSystem codeSystem) {
		final CDOResource cdoResource = transaction.getOrCreateResource(getMetaRootResourceName());
		return cdoResource.getContents().remove(codeSystem);
	}
	
	/**
	 * Template method for performing additional validation or processing before commit.
	 * Default implementation is empty, subclasses can override.
	 */
	public void preCommit() { }

	/**
	 * Clears the content of the {@link #getContents() contents} of the 
	 * current editing context instance.
	 */
	public void clearContents() {
		getContents().clear();
	}
	
	/**
	 * Returns with {@code true} if the {@link #getContents() contents}
	 * of the editing context is either empty of {@code null}. Otherwise {@code false}.
	 * @return {@code true} if the contents is empty. Otherwise {@code false}.
	 */
	public boolean isContentsEmpty() {
		return isEmpty(getContents());
	}
	
	/**
	 * Adds the specified object to {@link #getContents() contents} of the 
	 * current editing context.
	 * @param object the object to be added to the contents of the editing context.
	 */
	public void add(final EObject object) {
		if (object instanceof CodeSystem) {
			transaction.getOrCreateResource(getMetaRootResourceName()).getContents().add(object);
		} else {
			getContents().add(checkNotNull(object, "object"));
		}
	}
	
	/**
	 * Adds the specified objects to {@link #getContents() contents} of the 
	 * current editing context.
	 * @param objects the objects to be added to the contents.
	 */
	public void addAll(final Collection<? extends EObject> objects) {
		getContents().addAll(checkNotNull(objects, "objects"));
	}
	
	/**
	 * Deletes the object from this editing context and from his parent.
	 * @param object
	 * @throws ConflictException - if the component cannot be deleted
	 */
	public final void delete(EObject object) {
		delete(object, false);
	}
	
	/**
	 * Deletes the object from this editing context and from his parent.
	 * @param object - the object to be deleted
	 * @param force - whether forcefully delete the object even if regular delete would not be possible
	 * @throws ConflictException - if the component cannot be deleted
	 */
	public void delete(EObject object, boolean force) throws ConflictException {
		if (object instanceof CodeSystem) {
			transaction.getOrCreateResource(getMetaRootResourceName()).getContents().remove(object);
		} else {
			EcoreUtil.remove(object);
		}
	}
	
	/**
	 * @return the repository-unique root resource name where the editing context contents are stored (may not be {@code null} or empty)
	 */
	protected abstract String getRootResourceName();
	
	/**
	 * @return the meta root resource name that stores the one and only {@link CodeSystemVersionGroup}. 
	 */
	protected abstract String getMetaRootResourceName();
	
	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), getTransaction());
	}
	
	/**
	 * 
	 * Convenience method for committing the object graph found in the {@link CDOEditingContext} to file. 
	 * Note: the returned file will be deleted on VM shutdown, 
	 * thus the client of this method may want to persist it to avoid information loss.
	 * 
	 * @param context
	 * @param componentId
	 * @return
	 * @throws CommitException
	 */
	public static File commitToFile(final CDOEditingContext context) throws CommitException {
		Preconditions.checkNotNull(context, "Editing context argument cannot be null.");
		
		File pushFile = null;
		try {
			 
			final String tempDirPath = System.getProperty(FileUtils.TEMP_DIR_PROPERTY);
			final File tempDir = new File(tempDirPath);
			
			if (tempDir.exists()) {
				
				if (tempDir.canWrite()) {
					
					
					pushFile = File.createTempFile(UUID.randomUUID().toString(), null, tempDir);
					pushFile.deleteOnExit(); //clean up resources
					
					final CDOPushTransaction pushTransaction = new CDOPushTransaction(context.transaction, pushFile);
					
					try {
						
						pushTransaction.commit(new NullProgressMonitor());
						
						return pushFile;
						
					} catch (final CommitException e) {
						
						throw new CommitException("An error occurred while saving.", e);
						
					} finally {
						
						LifecycleUtil.deactivate(pushTransaction);
						
					}
					
					
				} else {
					
					LOGGER.error("Cannot write the temporary directory for the OS. Path: '" + tempDirPath + "'.");
					throw new RuntimeException("Cannot write the temporary directory for the OS. Path: '" + tempDirPath + "'.");
					
				}
				
				
			} else {
				
				LOGGER.error("Cannot find the temporary directory for the OS. Path: '" + tempDirPath + "'.");
				throw new RuntimeException("Cannot find the temporary directory for the OS. Path: '" + tempDirPath + "'.");
			}
			
			
		} catch (final IOException e) {
			
			LOGGER.error("Cannot create temporary file. Reason: " + e.getMessage());
			throw new RuntimeException("Cannot create temporary file. Reason: " + e.getMessage(), e);
			
		}
	}
	
	private static IBranchPath getActivePath(EPackage ePackage) {
		return getTaskManager().getActiveBranch(getConnection(ePackage).getUuid());
	}
	
	/*returns with the proper CDO connection service*/
	private static ICDOConnection getConnection(EPackage ePackage) {
		return getConnectionManager().get(ePackage);
	}
	
	/*creates an new CDO transaction instance on the HEAD of the given branch*/
	private static CDOTransaction createTransaction(final EPackage ePackage, final IBranchPath branchPath) {
		final CDOTransaction transaction = getConnection(ePackage).createTransaction(branchPath);
		transaction.options().setLockNotificationEnabled(true);
		return transaction;
	}

	private static TaskManager getTaskManager() {
		return getApplicationContext().getService(TaskManager.class);
	}

	private static ICDOConnectionManager getConnectionManager() {
		return getApplicationContext().getService(ICDOConnectionManager.class);
	}
	
	private static ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
	
}