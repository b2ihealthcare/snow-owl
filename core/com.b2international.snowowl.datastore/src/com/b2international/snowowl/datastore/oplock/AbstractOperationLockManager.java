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

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.core.runtime.ListenerList;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.oplock.impl.AbstractDatastoreLockTarget;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An abstract superclass of {@link IOperationLockManager} providing common methods.
 * 
 */
public abstract class AbstractOperationLockManager<C extends Serializable> implements IOperationLockManager<C> {

	protected static final String ACQUIRE_FAILED_MESSAGE = "Could not acquire requested lock(s).";

	private static final String RELEASE_FAILED_MESSAGE = "Could not release requested lock(s).";

	private static final String LOCK_EXISTS_BUT_NOT_HELD_MESSAGE = "Lock for target {0} exists, but no lock is held.";

	private static final int EXPECTED_LOCKS = 128;

	private final Object syncObject = new Object();

	private final ListenerList listenerList = new ListenerList();
	
	private final BitSet assignedIds = new BitSet(EXPECTED_LOCKS);
	
	private int lastAssignedId = 0;

	@Override
	public void lock(final C context, final long timeoutMillis, final IOperationLockTarget firstTarget, final IOperationLockTarget... restTargets) throws OperationLockException, InterruptedException {
		lock(context, timeoutMillis, Lists.asList(firstTarget, restTargets));
	}

	@Override
	public void lock(final C context, final long timeoutMillis, final Iterable<? extends IOperationLockTarget> targets) throws OperationLockException, InterruptedException {

		final Map<IOperationLockTarget, C> alreadyLockedTargets = Maps.newHashMap();
		final long startTimeMillis = getCurrentTimeMillis();
		
		synchronized (syncObject) {
			while (true) {
				
				alreadyLockedTargets.clear();
				canContextLockTargets(context, targets, alreadyLockedTargets);
	
				if (alreadyLockedTargets.isEmpty()) {
					for (final IOperationLockTarget newTarget : targets) {
						final IOperationLock<C> existingLock = getOrCreateLock(newTarget);
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
	public void unlock(final C context, final IOperationLockTarget firstTarget, final IOperationLockTarget... restTargets) throws OperationLockException {
		unlock(context, Lists.asList(firstTarget, restTargets));
	}

	@Override
	public void unlock(final C context, final Iterable<? extends IOperationLockTarget> targets) throws OperationLockException {

		final Map<IOperationLockTarget, C> notUnlockedTargets = Maps.newHashMap();

		synchronized (syncObject) {

			for (final IOperationLockTarget targetToUnlock : targets) {
				for (final IOperationLock<C> existingLock : getExistingLocks()) {
					if (existingLock.targetEquals(targetToUnlock) && !canContextUnlock(context, existingLock)) {
						notUnlockedTargets.put(existingLock.getTarget(), existingLock.getContext());
					}
				}
			}

			if (!notUnlockedTargets.isEmpty()) {
				throwLockException(RELEASE_FAILED_MESSAGE, notUnlockedTargets);
			}

			for (final IOperationLockTarget targetToUnlock : targets) {
				
				final IOperationLock<C> existingLock = getOrCreateLock(targetToUnlock);
				
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
			
			for (IOperationLock<C> lockToRemove : getExistingLocks()) {
				
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
			
			for (IOperationLock<C> lockToRemove : getExistingLocks()) {
				
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
	public List<OperationLockInfo<C>> getLocks() {

		final List<OperationLockInfo<C>> result = Lists.newArrayList();
		
		synchronized (syncObject) {
			for (final IOperationLock<C> existingLock : getExistingLocks()) {
				result.add(createLockInfo(existingLock));
			}
		}
		
		Collections.sort(result);
		return result;
	}

	public void addLockTargetListener(final IOperationLockTargetListener<C> listener) {
		listenerList.add(listener);
	}
	
	public void removeLockTargetListener(final IOperationLockTargetListener<C> listener) {
		listenerList.remove(listener);
	}

	protected void throwLockException(final String message, final Map<IOperationLockTarget, C> targets) throws OperationLockException {
		throw new OperationLockException(message);
	}

	protected abstract IOperationLock<C> createLock(final int id, final IOperationLockTarget target);

	protected abstract OperationLockInfo<C> createLockInfo(final IOperationLock<C> existingLock);

	@OverridingMethodsMustInvokeSuper
	protected void canContextLockTargets(final C context, final Iterable<? extends IOperationLockTarget> targets, final Map<IOperationLockTarget, C> alreadyLockedTargets) 
			throws DatastoreOperationLockException {
		
		for (final IOperationLockTarget newTarget : targets) {
			for (final IOperationLock<C> existingLock : getExistingLocks()) {
				if (existingLock.targetConflicts(newTarget) && !canContextLock(context, existingLock)) {
					alreadyLockedTargets.put(newTarget, existingLock.getContext());
				}
			}
		}
	}
	
	protected abstract boolean canContextLock(final C context, final IOperationLock<C> existingLock);

	protected boolean canContextUnlock(final C context, final IOperationLock<C> existingLock) {
		return canContextLock(context, existingLock);
	}

	protected void clearListeners() {
		listenerList.clear();
	}
	
	private long getCurrentTimeMillis() {
		return System.nanoTime() / (1000L * 1000L);
	}

	private IOperationLock<C> getOrCreateLock(final IOperationLockTarget target) {
		final DatastoreLockEntry existingLockEntry = Iterables.getOnlyElement(getLockIndex().search(DatastoreLockEntry.Expressions.lockTarget(target), 1), null);
		
		if (existingLockEntry == null) {
			lastAssignedId = assignedIds.nextClearBit(lastAssignedId);
			final String lockId = Integer.toString(lastAssignedId);
			final IOperationLock<C> newLock = createLock(lastAssignedId, target);
			final DatastoreLockEntry newEntry = buildIndexEntry(lockId, newLock, target);
			getLockIndex().put(lockId, newEntry);
			
			assignedIds.set(lastAssignedId);
			/* 
			 * XXX (apeteri): this makes the lock manager revisit low IDs after every 128 issued locks, but 
			 * it can still assign a number over 128 if all of the early ones are in use, since the BitSet grows unbounded. 
			 */
			lastAssignedId = lastAssignedId % EXPECTED_LOCKS;
		}
		
		return existingLockEntry.getLock();
	}

	private DatastoreLockEntry buildIndexEntry(final String lockId, IOperationLock<C> lock, final IOperationLockTarget target) {
		final DatastoreLockEntry entry = DatastoreLockEntry.builder()
			.id(lockId)
			.lock(lock)
			.lockTarget(target)
			.build();
		
		return entry;
	}

	private void removeLock(final IOperationLock<C> existingLock) {
		// TODO: remove from index
	}

	@SuppressWarnings("unchecked")
	private void fireTargetAcquired(final IOperationLockTarget target, final C context) {
		for (final Object listener : listenerList.getListeners()) {
			((IOperationLockTargetListener<C>) listener).targetAcquired(target, context);
		}
	}

	@SuppressWarnings("unchecked")
	private void fireTargetReleased(final IOperationLockTarget target, final C context) {
		for (final Object listener : listenerList.getListeners()) {
			((IOperationLockTargetListener<C>) listener).targetReleased(target, context);
		}
	}

	private Collection<IOperationLock<C>> getExistingLocks() {
		return getLockIndex().search(null, Integer.MAX_VALUE).stream().map(DatastoreLockEntry::getLock).collect(Collectors.toList());
	}
	
	private DatastoreLockIndex getLockIndex() {
		return ApplicationContext.getServiceForClass(DatastoreLockIndex.class);
	}
}