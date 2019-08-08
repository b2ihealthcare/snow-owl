/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.oplock;

import java.text.MessageFormat;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.core.runtime.ListenerList;

import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockTarget;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.identity.domain.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An abstract superclass of {@link IOperationLockManager} providing common methods.
 * 
 */
public final class DatastoreOperationLockManager implements IOperationLockManager, IDisposableService {
	
	protected static final String ACQUIRE_FAILED_MESSAGE = "Could not acquire requested lock(s).";

	private static final String RELEASE_FAILED_MESSAGE = "Could not release requested lock(s).";

	private static final String LOCK_EXISTS_BUT_NOT_HELD_MESSAGE = "Lock for target {0} exists, but no lock is held.";

	private static final int EXPECTED_LOCKS = 128;
	
	private final AtomicBoolean disposed = new AtomicBoolean(false);

	private final Object syncObject = new Object();
	
	private final Index index;

	private final ListenerList listenerList = new ListenerList();
	
	private final BitSet assignedIds = new BitSet(EXPECTED_LOCKS);
	
	private int lastAssignedId = 0;
	
	public DatastoreOperationLockManager(Index index) {
		this.index = index;
		this.index.admin().create();
	}
	
	@Override
	public void lock(final DatastoreLockContext context, final long timeoutMillis, final DatastoreLockTarget firstTarget, final DatastoreLockTarget... restTargets) throws OperationLockException, InterruptedException {
		lock(context, timeoutMillis, Lists.asList(firstTarget, restTargets));
	}

	@Override
	public void lock(final DatastoreLockContext context, final long timeoutMillis, final Iterable<DatastoreLockTarget> targets) throws OperationLockException, InterruptedException {

		final Map<DatastoreLockTarget, DatastoreLockContext> alreadyLockedTargets = Maps.newHashMap();
		final long startTimeMillis = getCurrentTimeMillis();
		
		synchronized (syncObject) {
			while (true) {
				
				alreadyLockedTargets.clear();
				canContextLockTargets(context, targets, alreadyLockedTargets);
	
				if (alreadyLockedTargets.isEmpty()) {
					for (final DatastoreLockTarget newTarget : targets) {
						final IOperationLock existingLock = getOrCreateLock(context, newTarget);
						existingLock.acquire(context);
						fireTargetAcquired(existingLock.getTarget(), context);
					}
					
					syncObject.notifyAll();
					return;
				}
				
				if (NO_TIMEOUT == timeoutMillis) {
					syncObject.wait();
				} else {
					final long remainingTimeoutMillis = timeoutMillis - (getCurrentTimeMillis() - startTimeMillis);
					
					if (remainingTimeoutMillis < 1L) {
						throwLockException(ACQUIRE_FAILED_MESSAGE, alreadyLockedTargets);
					} else {
						syncObject.wait(remainingTimeoutMillis);
					}
				}
			}
		}
	}
	
	@Override
	public void unlock(final DatastoreLockContext context, final DatastoreLockTarget firstTarget, final DatastoreLockTarget... restTargets) throws OperationLockException {
		unlock(context, Lists.asList(firstTarget, restTargets));
	}

	@Override
	public void unlock(final DatastoreLockContext context, final Iterable<DatastoreLockTarget> targets) throws OperationLockException {

		final Map<DatastoreLockTarget, DatastoreLockContext> notUnlockedTargets = Maps.newHashMap();

		synchronized (syncObject) {

			for (final DatastoreLockTarget targetToUnlock : targets) {
				for (final IOperationLock existingLock : getExistingLocks()) {
					if (existingLock.targetEquals(targetToUnlock) && !canContextUnlock(context, existingLock)) {
						notUnlockedTargets.put(existingLock.getTarget(), existingLock.getContext());
					}
				}
			}

			if (!notUnlockedTargets.isEmpty()) {
				throwLockException(RELEASE_FAILED_MESSAGE, notUnlockedTargets);
			}

			for (final DatastoreLockTarget targetToUnlock : targets) {
				
				final IOperationLock existingLock = getOrCreateLock(context, targetToUnlock);
				
				try {
					existingLock.release(context);
					fireTargetReleased(existingLock.getTarget(), context);
				} finally {
					if (!existingLock.isLocked()) {
						removeLock(existingLock);
					}
				}
			}
			
			syncObject.notifyAll();
		}
	}

	/**
	 * (non-API)
	 * <p> 
	 * Releases all lock targets tracked by this lock manager.
	 */
	public void unlockAll() {

		synchronized (syncObject) {
			
			for (IOperationLock lockToRemove : getExistingLocks()) {
				
				if (!lockToRemove.isLocked()) {
					throw new IllegalStateException(MessageFormat.format(LOCK_EXISTS_BUT_NOT_HELD_MESSAGE, lockToRemove.getTarget()));
				} else {
					removeLock(lockToRemove);
				}
			}
			
			syncObject.notifyAll();
		}
	}

	/**
	 * (non-API)
	 * <p>
	 * Forces lock removal for the target with the specified identifier.
	 * 
	 * @param id the lock identifier to forcefully unlock
	 * @return 
	 */
	public boolean unlockById(final int id) {
		
		synchronized (syncObject) {
			
			for (IOperationLock lockToRemove : getExistingLocks()) {
				
				if (!lockToRemove.isLocked()) {
					throw new IllegalStateException(MessageFormat.format(LOCK_EXISTS_BUT_NOT_HELD_MESSAGE, lockToRemove.getTarget()));
				} 
				
				if (id == lockToRemove.getId()) {
					removeLock(lockToRemove);
					syncObject.notifyAll();
					return true;
				}
			}				
		}
		
		return false;
	}

	/**
	 * (non-API)
	 * <p>
	 * Collects a snapshot of currently granted locks.
	 * <p>
	 * @return a list of granted locks information objects, sorted by lock identifer (never {@code null})
	 */
	public List<OperationLockInfo> getLocks() {

		final List<OperationLockInfo> result = Lists.newArrayList();
		
		synchronized (syncObject) {
			for (final IOperationLock existingLock : getExistingLocks()) {
				result.add(createLockInfo(existingLock));
			}
		}
		
		Collections.sort(result);
		return result;
	}

	public void addLockTargetListener(final IOperationLockTargetListener listener) {
		listenerList.add(listener);
	}
	
	public void removeLockTargetListener(final IOperationLockTargetListener listener) {
		listenerList.remove(listener);
	}

	private void throwLockException(final String message, final Map<DatastoreLockTarget, DatastoreLockContext> targets) throws OperationLockException {
		throw new OperationLockException(message);
	}

	@OverridingMethodsMustInvokeSuper
	protected void canContextLockTargets(final DatastoreLockContext context, final Iterable<DatastoreLockTarget> targets, final Map<DatastoreLockTarget, DatastoreLockContext> alreadyLockedTargets) 
			throws DatastoreOperationLockException {
		if (!isDisposed()) {
			for (final DatastoreLockTarget newTarget : targets) {
				for (final IOperationLock existingLock : getExistingLocks()) {
					if (existingLock.targetConflicts(newTarget) && !canContextLock(context, existingLock)) {
						alreadyLockedTargets.put(newTarget, existingLock.getContext());
					}
				}
			}
		} else {
			final DatastoreLockContext disposedContext = new DatastoreLockContext(User.SYSTEM.getUsername(), DatastoreLockContextDescriptions.DISPOSE_LOCK_MANAGER);
			for (final DatastoreLockTarget target : targets) {
				alreadyLockedTargets.put(target, disposedContext);
			}
			
		}
		
		throwLockException(ACQUIRE_FAILED_MESSAGE, alreadyLockedTargets);
	}
	
	private boolean canContextLock(final DatastoreLockContext context, final IOperationLock existingLock) {
		return context.isCompatible(existingLock.getContext());
	}
	
	private boolean canContextUnlock(DatastoreLockContext context, IOperationLock existingLock) {
		return context.userMatches(existingLock.getContext());
	}


	private void clearListeners() {
		listenerList.clear();
	}
	
	private long getCurrentTimeMillis() {
		return System.nanoTime() / (1000L * 1000L);
	}

	private IOperationLock getOrCreateLock(DatastoreLockContext context, final DatastoreLockTarget target) {
		final String repositoryUuid = target.getRepositoryUuid();
		final String branchPath = target.getBranchPath();
		
		final Expression searchExpression = Expressions.builder()
			.filter(DatastoreLockIndexEntry.Expressions.repositoryUuid(repositoryUuid))
			.filter(DatastoreLockIndexEntry.Expressions.branchPath(branchPath))
			.build();
		
		final DatastoreLockIndexEntry existingLockEntry = Iterables.getOnlyElement(search(searchExpression, 2), null);
		IOperationLock lock = new OperationLock(lastAssignedId, target);
		if (existingLockEntry == null) {
			lastAssignedId = assignedIds.nextClearBit(lastAssignedId);
			final String lockId = Integer.toString(lastAssignedId);
			lock = createLock(lastAssignedId, target);
			final DatastoreLockIndexEntry newEntry = buildIndexEntry(lockId, branchPath, repositoryUuid, context);
			put(lockId, newEntry);
			
			assignedIds.set(lastAssignedId);
			/* 
			 * XXX (apeteri): this makes the lock manager revisit low IDs after every 128 issued locks, but 
			 * it can still assign a number over 128 if all of the early ones are in use, since the BitSet grows unbounded. 
			 */
			lastAssignedId = lastAssignedId % EXPECTED_LOCKS;
		}
		
		return lock;
	}

	private DatastoreLockIndexEntry buildIndexEntry(final String lockId, final String branchPath, final String repositoryUuid, final DatastoreLockContext context) {
		final DatastoreLockIndexEntry entry = DatastoreLockIndexEntry.builder()
			.id(lockId)
			.userId(context.getUserId())
			.description(context.getDescription())
			.repositoryUuid(repositoryUuid)
			.branchPath(branchPath)
			.build();
		
		return entry;
	}

	private void removeLock(final IOperationLock existingLock) {
		final String lockId = Iterables.getOnlyElement(search(DatastoreLockIndexEntry.Expressions.id(Integer.toString(existingLock.getId())), 2), null).getId();
		if (lockId != null) {
			remove(lockId);
		}
	}

	@SuppressWarnings("unchecked")
	private void fireTargetAcquired(final DatastoreLockTarget target, final DatastoreLockContext context) {
		for (final Object listener : listenerList.getListeners()) {
			((IOperationLockTargetListener) listener).targetAcquired(target, context);
		}
	}

	@SuppressWarnings("unchecked")
	private void fireTargetReleased(final DatastoreLockTarget target, final DatastoreLockContext context) {
		for (final Object listener : listenerList.getListeners()) {
			((IOperationLockTargetListener) listener).targetReleased(target, context);
		}
	}
	
	private OperationLock createLock(final int id, final DatastoreLockTarget target) {
		return new OperationLock(id, target);
	}

	private OperationLockInfo createLockInfo(final IOperationLock existingLock) {
		final OperationLock lock = (OperationLock) existingLock;
		return new OperationLockInfo(lock.getId(), lock.getLevel(), lock.getCreationDate(), lock.getTarget(), lock.getContext());
	}
	
	private Collection<IOperationLock> getExistingLocks() {
		return search(Expressions.matchAll(), Integer.MAX_VALUE).stream().map(entry -> {
			return new OperationLock(Integer.parseInt(entry.getId()), new DatastoreLockTarget(entry.getRepositoryUuid(), entry.getBranchPath()));
		}).collect(Collectors.toSet());
	}
	
	private DatastoreLocks search(Expression query, int limit) {
		return search(query, ImmutableList.of(), SortBy.DOC_ID, limit); 
	}
	
	private DatastoreLocks search(Expression query, List<String> fields, SortBy sortBy, int limit) {
		final Hits<DatastoreLockIndexEntry> hits = searchHits(query, fields, sortBy, limit);
		return new DatastoreLocks(hits.getHits(), null, null, hits.getLimit(), hits.getTotal());
	}
	
	private Hits<DatastoreLockIndexEntry> searchHits(Expression query, List<String> fields, SortBy sortBy, int limit) {
		return index.read(searcher -> {
			return searcher.search(
					Query.select(DatastoreLockIndexEntry.class)
					.fields(fields)
					.where(Expressions.builder()
							.filter(query)
							.build())
					.sortBy(sortBy)
					.limit(limit)
					.build()
					);
		});
	}
	
	private DatastoreLockIndexEntry get(String lockId) {
		return index.read(searcher -> searcher.get(DatastoreLockIndexEntry.class, lockId));
	}
	
	private void put(String lockId, DatastoreLockIndexEntry lock) {
		index.write(writer -> {
			writer.put(lockId, lock);
			writer.commit();
			return null;
		});
	}
	
	private void remove(String lockId) {
		index.write(writer -> {
			writer.remove(DatastoreLockIndexEntry.class, lockId);
			writer.commit();
			
			return null;
		});
		
	}
	
	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			unlockAll();
			clearListeners();
		}
	}

	@Override
	public boolean isDisposed() {
		return disposed.get();
	}
	
}