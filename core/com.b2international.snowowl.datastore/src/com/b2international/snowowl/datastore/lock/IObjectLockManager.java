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
package com.b2international.snowowl.datastore.lock;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.view.CDOView;

import com.google.inject.ImplementedBy;

/**
 * Manages local client {@link CDOLock}s to be acquired for storageKeys. The implementor should be responsible to enable
 * lock change event delivery in the local client. This manager is available only on the client side, no server side
 * representation.
 * 
 * @since 3.1
 */
@ImplementedBy(IObjectLockManager.NullImpl.class)
public interface IObjectLockManager {

	public static final String UNKNOWN_USER_ID = "N/A";
	
	/**
	 * Returns the locked entries from the given collection of storage keys. The
	 * returned collection will contain only the long storagekeys of those, who
	 * are actually locked by someone else in this {@link IObjectLockManager}.
	 * Forcefully updates the current state for each given storage key in the
	 * currently active branch.
	 * 
	 * @param storageKeys
	 * @return
	 */
	Collection<Long> isLockedByOthersUpdate(Collection<Long> storageKeys);
	
	/**
	 * Returns whether the {@link CDOObject} identified by the given {@link CDOID} is locked or not.
	 * 
	 * @param storageKey
	 * @return <code>true</code> if locked, <code>false</code> otherwise.
	 */
	boolean isLocked(CDOID storageKey);

	/**
	 * Returns whether the {@link CDOObject} identified by the given long {@link CDOID} is locked or not.
	 * 
	 * @param storageKey
	 *            - will be converted to {@link CDOID}
	 * @return <code>true</code> if locked, <code>false</code> otherwise.
	 */
	boolean isLocked(long storageKey);
	
	/**
	 * Returns whether the {@link CDOObject} identified by the given long
	 * {@link CDOID} is locked or not. This method refreshes the current lock
	 * state of the element and returns the new state.
	 * 
	 * @param storageKey
	 * @return
	 */
	boolean isLockedForceUpdate(long storageKey);
	
	/**
	 * Returns whether the {@link CDOObject} identified by the given
	 * {@link CDOID} is locked or not. This method refreshes the current lock
	 * state of the element and returns the new state.
	 * 
	 * @param storageKey
	 * @return
	 */
	boolean isLockedForceUpdate(CDOID storageKey);
	
	/**
	 * Returns whether the {@link CDOObject} identified by the given
	 * {@link CDOID} is locked or not. This method refreshes the current lock
	 * state of the element and returns the new state by using the given
	 * {@link CDOBranch} as reference point.
	 * 
	 * @param storageKey - locked state of this element
	 * @param branch - to check locked state on this branch
	 * @return
	 */
	boolean isLockedForceUpdate(CDOID storageKey, CDOBranch branch);
	
	/**
	 * Returns whether the {@link CDOObject} identified by the given
	 * {@link CDOID} is locked or not. This method refreshes the current lock
	 * state of the element and returns the new state by using the given
	 * {@link CDOView} as reference.
	 * 
	 * @param storageKey - locked state of this element
	 * @param branch - to check locked state on this branch
	 * @return
	 */
	boolean isLockedForceUpdate(CDOID storageKey, CDOView view);

	/**
	 * Returns whether the {@link CDOObject} identified by the given {@link CDOID} is locked or not, in the given
	 * branch.
	 * 
	 * @param storageKey
	 * @param view
	 * @return
	 */
	boolean isLocked(CDOID storageKey, CDOBranch view);

	/**
	 * Returns whether the {@link CDOObject} identified by the given long {@link CDOID} is locked or not.
	 * 
	 * @param storageKey
	 *            - will be converted to {@link CDOID}
	 * @param view
	 *            - as the lock context
	 * @return <code>true</code> if locked, <code>false</code> otherwise.
	 */
	boolean isLocked(long storageKey, CDOView view);

	/**
	 * Returns the lock owner's session ID if the given entry is locked, otherwise returns <code>-1</code>.
	 * 
	 * @param storageKey
	 * @return
	 */
	int getLockOwnerSessionId(long storageKey);

	/**
	 * Tries to lock the {@link CDOObject} identified by the given {@link CDOID}.
	 * 
	 * @param storageKey
	 * @param view
	 *            - as the lock context
	 * @return - if the locking was successful.
	 */
	boolean lock(CDOID storageKey, CDOView view);

	/**
	 * Tries to lock the {@link CDOObject} identified by the given {@link CDOID}.
	 * 
	 * @param storageKey
	 *            - will be converted to {@link CDOID}
	 * @param view
	 *            - as the lock context
	 * @return - if the locking was successful.
	 */
	boolean lock(long storageKey, CDOView view);

	/**
	 * Unlocks the {@link CDOObject} identified by the given {@link CDOID} if it was locked.
	 * 
	 * @param storageKey
	 * @param view
	 *            - as the lock context
	 */
	void unlock(CDOID storageKey, CDOView view);

	/**
	 * Unlocks the {@link CDOObject} if it was locked.
	 * 
	 * @param storageKey
	 *            - will be converted to {@link CDOID}
	 * @param view
	 *            - as the lock context
	 * @throws IllegalArgumentException
	 *             if the {@link CDOObject} was not locked.
	 */
	void unlock(long storageKey, CDOView view);

	/**
	 * Disposes the lock manager instance.
	 */
	void dispose();

	/**
	 * Returns lock owner for the specified {@link CDOObject} if it is locked on the active branch, if not returns {@value #UNKNOWN_USER_ID}.
	 * 
	 * @param storageKey
	 * @return
	 */
	String getLockOwnerUserId(CDOID storageKey);

	/**
	 * Returns lock owner for the specified {@link CDOObject} if it is locked on the given branch, if not returns {@value #UNKNOWN_USER_ID}.
	 * 
	 * @param storageKey
	 * @param branch
	 * @return
	 */
	String getLockOwnerUserId(CDOID storageKey, CDOBranch branch);

	/**
	 * Returns lock owner for the specified {@link CDOObject} if it is locked on the active branch, if not returns {@value #UNKNOWN_USER_ID}.
	 * 
	 * @param storageKey
	 *            - will be converted to {@link CDOID}
	 * @return
	 */
	String getLockOwnerUserId(long storageKey);

	/**
	 * Returns lock owner for the specified {@link CDOObject} if it is locked on the given branch, if not returns {@value #UNKNOWN_USER_ID}.
	 * 
	 * @param storageKey
	 * @param branch
	 * @return
	 */
	String getLockOwnerUserId(long storageKey, CDOBranch branch);

	/**
	 * Registers the given listener in this {@link ISddEntryLockManager}.
	 * 
	 * @param listener
	 */
	void registerLockChangeListener(IObjectLockChangeListener listener);

	/**
	 * Unregisters the given listener from this {@link ISddEntryLockManager}.
	 * 
	 * @param listener
	 */
	void unregisterLockChangeListener(IObjectLockChangeListener listener);

	/**
	 * A very simple null implementation to be the default implementor.
	 * 
	 * @since 3.1
	 */
	public class NullImpl implements IObjectLockManager {

		@Override
		public Collection<Long> isLockedByOthersUpdate(Collection<Long> storageKeys) {
			return Collections.emptySet();
		}
		
		@Override
		public boolean isLocked(long storageKey, CDOView view) {
			return false;
		}
		
		@Override
		public boolean isLockedForceUpdate(long storageKey) {
			return false;
		}
		
		@Override
		public boolean isLockedForceUpdate(CDOID storageKey) {
			return false;
		}
		
		@Override
		public boolean isLockedForceUpdate(CDOID storageKey, CDOBranch branch) {
			return false;
		}
		
		@Override
		public boolean isLockedForceUpdate(CDOID storageKey, CDOView view) {
			return false;
		}

		@Override
		public boolean lock(CDOID storageKey, CDOView view) {
			return false;
		}

		@Override
		public boolean lock(long storageKey, CDOView view) {
			return false;
		}

		@Override
		public void unlock(CDOID storageKey, CDOView view) {
		}

		@Override
		public void unlock(long storageKey, CDOView view) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLocked(CDOID storageKey) {
			return false;
		}

		@Override
		public boolean isLocked(long storageKey) {
			return false;
		}

		@Override
		public String getLockOwnerUserId(CDOID storageKey, CDOBranch branch) {
			return UNKNOWN_USER_ID;
		}

		@Override
		public String getLockOwnerUserId(long storageKey, CDOBranch branch) {
			return UNKNOWN_USER_ID;
		}

		@Override
		public void registerLockChangeListener(IObjectLockChangeListener listener) {
		}

		@Override
		public void unregisterLockChangeListener(IObjectLockChangeListener listener) {
		}

		@Override
		public boolean isLocked(CDOID storageKey, CDOBranch view) {
			return false;
		}

		@Override
		public String getLockOwnerUserId(CDOID storageKey) {
			return UNKNOWN_USER_ID;
		}

		@Override
		public String getLockOwnerUserId(long storageKey) {
			return UNKNOWN_USER_ID;
		}

		@Override
		public int getLockOwnerSessionId(long storageKey) {
			return -1;
		}

	}

}