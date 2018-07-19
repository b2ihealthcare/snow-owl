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
package com.b2international.snowowl.core.repository;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;
import org.slf4j.Logger;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.exceptions.CycleDetectedException;
import com.b2international.commons.exceptions.LockedException;
import com.b2international.index.IndexException;
import com.b2international.index.Searcher;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.events.RepositoryCommitNotification;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.oplock.IOperationLockManager;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Throwables;

/**
 * @since 4.5
 */
public final class RepositoryTransactionContext extends DelegatingBranchContext implements TransactionContext {

	private final String userId;
	private final String commitComment;
	private final String parentContextDescription;
	
	private boolean isNotificationEnabled = true;
	
	@JsonIgnore
	private transient final Map<Pair<String, Class<?>>, Object> resolvedObjectsById = newHashMap();
	
	@JsonIgnore
	private transient final StagingArea staging;

	RepositoryTransactionContext(BranchContext context, String userId, String commitComment, String parentContextDescription) {
		super(context);
		this.userId = userId;
		this.commitComment = commitComment;
		this.parentContextDescription = parentContextDescription;
		this.staging = context.service(RevisionIndex.class).prepareCommit(branchPath());
	}
	
	public <T> T getResolvedObjectById(String componentId, Class<T> type) {
		return type.cast(resolvedObjectsById.get(createComponentKey(componentId, type)));
	}
	
	@Override
	public long commit() {
		return commit(userId(), commitComment, parentContextDescription);
	}
	
	@Override
	public String userId() {
		return userId;
	}
	
	@Override
	public <T> T lookup(String componentId, Class<T> type) {
		final T component = lookupIfExists(componentId, type);
		if (null == component) {
			throw new ComponentNotFoundException(type.getSimpleName(), componentId);
		}
		return component;
	}
	
	@Override
	public <T> T lookupIfExists(String componentId, Class<T> type) {
		return lookup(Collections.singleton(componentId), type).get(componentId);
	}
	
	@Override
	public <T> Map<String, T> lookup(Collection<String> componentIds, Class<T> type) {
		final Map<String, T> resolvedComponentsById = newHashMap();
		final Set<String> unresolvedComponentIds = newHashSet(componentIds);
		
		// check already resolved components first
		for (Iterator<String> it = unresolvedComponentIds.iterator(); it.hasNext();) {
			String componentId = it.next();
			Pair<String, Class<?>> key = createComponentKey(componentId, type);
			if (resolvedObjectsById.containsKey(key)) {
				resolvedComponentsById.put(componentId, type.cast(resolvedObjectsById.get(key)));
				it.remove();
			}
		}
		
		// as last resort, query the index for the storageKey to be able to resolve the class
		if (!unresolvedComponentIds.isEmpty()) {
			for (T object : fetchComponents(unresolvedComponentIds, type)) {
				final String componentId = getObjectId(object);
				resolvedComponentsById.put(componentId, object);
				resolvedObjectsById.put(createComponentKey(componentId, type), object);
			}
		}
		
		// TODO remove detached components???
		
		return resolvedComponentsById;
	}
	
	private String getObjectId(Object component) {
		if (component instanceof CodeSystemEntry) {
			return ((CodeSystemEntry) component).getShortName();
		} else if (component instanceof Revision) {
			return ((Revision) component).getId();
		}
		throw new UnsupportedOperationException("Cannot get objectId for " + component);
	}

	private <T> Pair<String, Class<?>> createComponentKey(final String componentId, Class<T> type) {
		return Tuples.<String, Class<?>>pair(componentId, type);
	}
	
	private <T> Iterable<T> fetchComponents(Set<String> componentIds, Class<T> type) {
		try {
			if (Revision.class.isAssignableFrom(type)) {
				return service(RevisionSearcher.class).get(type, componentIds);
			} else {
				return service(Searcher.class).get(type, componentIds);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	@Override
	public void add(Object o) {
		if (o instanceof CodeSystemEntry) {
			final CodeSystemEntry cs = (CodeSystemEntry) o;
			staging.stageNew(cs.getShortName(), cs);
			resolvedObjectsById.put(createComponentKey(cs.getShortName(), cs.getClass()), cs);
		} else if (o instanceof CodeSystemVersionEntry) { 
			final CodeSystemVersionEntry cs = (CodeSystemVersionEntry) o;
			staging.stageNew(cs.getVersionId(), cs);
			resolvedObjectsById.put(createComponentKey(cs.getVersionId(), cs.getClass()), cs);
		} else if (o instanceof Revision) {
			Revision rev = (Revision) o;
			staging.stageNew(rev);
			resolvedObjectsById.put(createComponentKey(rev.getId(), rev.getClass()), rev);
		} else {
			throw new UnsupportedOperationException("Cannot add object to this repository: " + o);
		}
	}
	
	@Override
	public void update(Revision oldRevision, Revision changedRevision) {
		// check if the oldRevision is present as new object in the staging area and update the new object, otherwise stage an update
		if (staging.isNew(oldRevision)) {
			staging.stageNew(changedRevision);
		} else {
			staging.stageChange(oldRevision, changedRevision);
		}
		resolvedObjectsById.put(createComponentKey(changedRevision.getId(), changedRevision.getClass()), changedRevision);
	}
	
	@Override
	public void delete(Object o) {
		delete(o, false);
	}
	
	@Override
	public void delete(Object o, boolean force) {
		RevisionDocument doc = ClassUtils.checkAndCast(o, RevisionDocument.class);
		staging.stageRemove(doc);
		// TODO add deletion policy and use force flag to circumvent it
	}
	
	@Override
	public void close() throws Exception {
		// TODO is it always okay to clear when closing tx???
		resolvedObjectsById.clear();
	}

	@Override
	public long commit(String userId, String commitComment, String parentContextDescription) {
		final Logger log = service(Logger.class);
		final DatastoreLockContext lockContext = createLockContext(userId, parentContextDescription);
		final SingleRepositoryAndBranchLockTarget lockTarget = createLockTarget(id(), branchPath());
		IOperationLockManager<DatastoreLockContext> locks = service(IDatastoreOperationLockManager.class);
		Commit commit = null;
		try {
			acquireLock(locks, lockContext, lockTarget);
			final long timestamp = service(TimestampProvider.class).getTimestamp();
			log.info("Persisting changes to {}@{}", branchPath(), timestamp);
			commit = staging.commit(null, timestamp, userId, commitComment);
			log.info("Changes have been successfully persisted to {}@{}.", branchPath(), timestamp);
			return commit.getTimestamp();
		} catch (RepositoryLockException e) {
			throw new LockedException(e.getMessage());
		} catch (final IndexException e) {
			Throwable rootCause = Throwables.getRootCause(e);
			if (rootCause instanceof CycleDetectedException) {
				throw (CycleDetectedException) rootCause;
			}
			throw new SnowowlRuntimeException(e.getMessage(), e);
		} finally {
			locks.unlock(lockContext, lockTarget);
			// send a commit notification
			if (commit != null) {
				new RepositoryCommitNotification(id(),
						commit.getId(),						
						commit.getGroupId(),
						commit.getBranch(),
						commit.getTimestamp(),
						commit.getAuthor(),
						commit.getComment(),
						Collections.emptyList(),
						Collections.emptyList(),
						Collections.emptyList()).publish(service(IEventBus.class));
			}
		}
	}

	private void acquireLock(IOperationLockManager<DatastoreLockContext> locks, DatastoreLockContext lockContext, IOperationLockTarget lockTarget) {
		try {
			locks.lock(lockContext, 1000L, lockTarget);
		} catch (final DatastoreOperationLockException e) {
			final DatastoreLockContext lockOwnerContext = e.getContext(lockTarget);
			throw new RepositoryLockException(MessageFormat.format("Write access to {0} was denied because {1} is {2}. Please try again later.", 
					lockTarget,
					lockOwnerContext.getUserId(), 
					lockOwnerContext.getDescription()));
		} catch (InterruptedException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	@Override
	public boolean isNotificationEnabled() {
		return isNotificationEnabled;
	}
	
	@Override
	public void setNotificationEnabled(boolean isNotificationEnabled) {
		this.isNotificationEnabled = isNotificationEnabled;
	}

	@Override
	public void clearContents() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	private static DatastoreLockContext createLockContext(String userId, String parentContextDescription) {
		return new DatastoreLockContext(userId, DatastoreLockContextDescriptions.COMMIT, parentContextDescription);
	}
	
	private static SingleRepositoryAndBranchLockTarget createLockTarget(String repositoryId, String branch) {
		return new SingleRepositoryAndBranchLockTarget(repositoryId, BranchPathUtils.createPath(branch));
	}

}
