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

	int UNKNOWN_SESSION_ID = -1;
	
	String UNKNOWN_USER_ID = "N/A";
	
	/**
	 * Returns whether the {@link CDOObject} identified by the given long {@link CDOID} is locked or not.
	 * <p>
	 * This method will not open a view to check if a lock is present; the lock manager can return a 
	 * false negative answer for locks that it doesn't know about.
	 * 
	 * @param storageKey
	 * @param branch
	 * @return <code>true</code> if locked, <code>false</code> otherwise.
	 */
	boolean isLocked(long storageKey, String branch);

	/**
	 * Returns whether the {@link CDOObject} identified by the given long
	 * {@link CDOID} is locked or not. This method refreshes the current lock
	 * state of the element and returns the new state.
	 * 
	 * @param storageKey
	 * @param branch
	 * @return
	 */
	boolean isLockedForceUpdate(long storageKey, String branch);

	/**
	 * Returns the locked entries from the given collection of storage keys. The
	 * returned collection will contain only the long storage keys of those, who
	 * are actually locked by someone else in this {@link IObjectLockManager}.
	 * Forcefully updates the current state for each given storage key in the
	 * currently specified branch.
	 * 
	 * @param storageKeys
	 * @param branch
	 * @return
	 */
	Collection<Long> getStorageKeysLockedByOthers(Collection<Long> storageKeys, String branch);
	
	/**
	 * Returns lock owner for the specified {@link CDOObject} if it is locked on the specified branch, if not returns {@value #UNKNOWN_USER_ID}.
	 * 
	 * @param storageKey
	 *            - will be converted to {@link CDOID}
	 * @param branch
	 * @return
	 */
	String getLockOwner(long storageKey, String branch);

	/**
	 * Returns the lock owner's session ID if the given entry is locked, otherwise returns <code>-1</code>.
	 * 
	 * @param storageKey
	 * @param branch
	 * @return
	 */
	int getLockOwnerSessionId(long storageKey, String branch);

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
		public boolean isLocked(long storageKey, String branch) {
			return false;
		}

		@Override
		public boolean isLockedForceUpdate(long storageKey, String branch) {
			return false;
		}
		
		@Override
		public Collection<Long> getStorageKeysLockedByOthers(Collection<Long> storageKeys, String branch) {
			return Collections.emptySet();
		}

		@Override
		public String getLockOwner(long storageKey, String branch) {
			return UNKNOWN_USER_ID;
		}

		@Override
		public int getLockOwnerSessionId(long storageKey, String branch) {
			return UNKNOWN_SESSION_ID;
		}

		@Override
		public boolean lock(long storageKey, CDOView view) {
			return false;
		}

		@Override
		public void unlock(long storageKey, CDOView view) {
			return;
		}

		@Override
		public void dispose() {
			return;
		}

		@Override
		public void registerLockChangeListener(IObjectLockChangeListener listener) {
			return;
		}

		@Override
		public void unregisterLockChangeListener(IObjectLockChangeListener listener) {
			return;
		}
	}
}
