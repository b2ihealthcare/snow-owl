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
package com.b2international.snowowl.datastore.lock;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @since 3.1
 */
@Singleton
public class ObjectLockManager implements IObjectLockManager, IListener {

	private static final Logger LOG = LoggerFactory.getLogger(ObjectLockManager.class);

	private final ICDOConnection connection;
	private final int currentSessionId;
	private final Collection<IObjectLockChangeListener> listeners = newHashSet();
	private final Table<Long, String, Integer> lockOwners = HashBasedTable.create();

	@Inject
	public ObjectLockManager(final ICDOConnection connection) {
		checkNotNull(connection, "connection");

		LOG.info("Creating lock manager for connection: {}", connection.getUuid());
		this.connection = connection;

		final CDONet4jSession session = connection.getSession();
		session.options().setLockNotificationMode(LockNotificationMode.ALWAYS);
		session.addListener(this);
		this.currentSessionId = session.getSessionID();
	}

	@Override
	public boolean isLocked(final long storageKey, final String branch) {
		synchronized (lockOwners) { return lockOwners.contains(storageKey, branch); }
	}

	@Override
	public boolean isLockedForceUpdate(final long storageKey, final String branch) {
		final IBranchPath branchPath = BranchPathUtils.createPath(branch);
		final CDOView view = connection.createView(connection.getBranch(branchPath));
		final boolean[] lockChanged = new boolean[1];
		try {
			return checkLockOwner(view, storageKey, branch, lockChanged) != null;
		} finally {
			if (lockChanged[0]) { notifyListeners(); }
			LifecycleUtil.deactivate(view);
		}
	}

	private Integer checkLockOwner(final CDOView view, final long storageKey, final String branch, final boolean[] lockChanged) {
		final CDOObject object = CDOUtils.getObjectIfExists(view, storageKey);
		if (object == null) {
			return null;
		}

		final CDOLock writeOption = object.cdoWriteOption();

		if (writeOption.isLocked()) {
			lockChanged[0] |= updateLockAndCheckChange(storageKey, branch, currentSessionId);
			return currentSessionId;
		} else if (writeOption.isLockedByOthers()) {
			final int otherSessionId = object.cdoLockState().getWriteOptionOwner().getSessionID();
			lockChanged[0] |= updateLockAndCheckChange(storageKey, branch, otherSessionId);
			return otherSessionId;
		} else /* not locked */ {
			lockChanged[0] |= removeLockAndCheckChange(storageKey, branch);
			return null;
		}
	}

	private boolean updateLockAndCheckChange(final long storageKey, final String branch, final Integer currentOwner) {
		final Integer previousOwner = updateLock(storageKey, branch, currentOwner);
		return previousOwner != currentOwner;
	}

	private boolean removeLockAndCheckChange(final long storageKey, final String branch) {
		final Integer previousOwner = removeLock(storageKey, branch);
		return previousOwner != null;
	}

	private Integer updateLock(final long storageKey, final String branch, final Integer ownerId) {
		synchronized (lockOwners) { return lockOwners.put(storageKey, branch, ownerId); }
	}

	private Integer removeLock(final long storageKey, final String branch) {
		synchronized (lockOwners) { return lockOwners.remove(storageKey, branch); }
	}

	@Override
	public Collection<Long> getStorageKeysLockedByOthers(final Collection<Long> storageKeys, final String branch) {
		final IBranchPath branchPath = BranchPathUtils.createPath(branch);
		final CDOView view = connection.createView(connection.getBranch(branchPath));
		try {
			final Set<Long> lockedStorageKeys = newHashSet();
			final boolean[] lockChanged = new boolean[1];
			
			synchronized (lockOwners) {
				for (final Long storageKey : storageKeys) {
					final Integer owner = checkLockOwner(view, storageKey, branch, lockChanged);
					if (owner != null && owner != currentSessionId) {
						lockedStorageKeys.add(storageKey);
					}
				}
			}
			
			if (lockChanged[0]) {
				notifyListeners();
			}
			
			return lockedStorageKeys;
		} finally {
			LifecycleUtil.deactivate(view);
		}
	}

	@Override
	public String getLockOwner(final long storageKey, final String branch) {
		final int ownerId = getLockOwnerSessionId(storageKey, branch);

		if (ownerId > UNKNOWN_SESSION_ID) {
			final CDOSession currentSession = connection.getSession();
			final CDORemoteSessionManager remoteSessionManager = currentSession.getRemoteSessionManager();

			for (final CDORemoteSession session : remoteSessionManager.getRemoteSessions()) {
				if (ownerId == session.getSessionID()) {
					return session.getUserID();
				}
			}
		}

		return UNKNOWN_USER_ID;
	}

	@Override
	public int getLockOwnerSessionId(final long storageKey, final String branch) {
		synchronized (lockOwners) {
			if (lockOwners.contains(storageKey, branch)) {
				return lockOwners.get(storageKey, branch);
			} else {
				return UNKNOWN_SESSION_ID;
			}
		}
	}

	@Override
	public boolean lock(final long storageKey, final CDOView view) {
		if (!CDOUtils.checkView(view)) {
			return false;
		}

		final CDOObject object = CDOUtils.getObjectIfExists(view, storageKey);
		if (object == null) {
			return false;
		}

		final CDOLock writeOptionLock = object.cdoWriteOption();
		if (writeOptionLock.isLocked() || writeOptionLock.isLockedByOthers()) {
			return false;
		}

		try {
			LOG.debug("Locking object: {}", storageKey);
			object.cdoWriteOption().lock(2000L);

			final String pathName = view.getBranch().getPathName();
			if (updateLockAndCheckChange(storageKey, pathName, view.getSessionID())) {
				notifyListeners();
			}

			return true;
		} catch (final TimeoutException e) {
			LOG.warn("Can't acquire lock for (timeout happened): {}", storageKey);
			return false;
		}
	}

	@Override
	public void unlock(final long storageKey, final CDOView view) {
		if (!CDOUtils.checkView(view)) {
			return;
		}

		final CDOObject object = CDOUtils.getObjectIfExists(view, storageKey);
		if (object == null) {
			return;
		}

		final CDOLock writeOptionLock = object.cdoWriteOption();
		if (writeOptionLock.isLocked()) {
			LOG.debug("Unlocking object: {}", storageKey);
			writeOptionLock.unlock();

			final String pathName = view.getBranch().getPathName();
			if (removeLockAndCheckChange(storageKey, pathName)) {
				notifyListeners();
			}
		}
	}

	@Override
	public void dispose() {
		if (this.lockOwners != null) {
			this.lockOwners.clear();
		}
	}

	@Override
	public void notifyEvent(final IEvent event) {
		if (event instanceof CDOSessionLocksChangedEvent) {
			notifyEvent((CDOSessionLocksChangedEvent)event);
		}
	}

	private void notifyEvent(final CDOSessionLocksChangedEvent event) {
		// if the event come from the same session, ignore it
		if (event.getLockOwner().getSessionID() == currentSessionId) {
			return;
		}

		LOG.debug("Processing lock change event from: {}", event.getSource().getUserID());

		final String branch = event.getBranch().getPathName();
		boolean lockChanged = false; 
		synchronized (lockOwners) {
			for (final CDOLockState lockState : event.getLockStates()) {
				LOG.debug("Lock state change: {}", lockState);
				final CDOIDAndBranch lockedObject = (CDOIDAndBranch) lockState.getLockedObject();
				final long storageKey = CDOIDUtil.getLong(lockedObject.getID());

				if (Operation.LOCK.equals(event.getOperation())) {
					final int currentOwner = lockState.getWriteOptionOwner().getSessionID();

					lockChanged |= updateLockAndCheckChange(storageKey, branch, currentOwner);
				} else {
					lockChanged |= removeLockAndCheckChange(storageKey, branch);
				}
			}
		}

		if (lockChanged) {
			notifyListeners();
		}
	}

	private void notifyListeners() {
		synchronized (listeners) {
			for (final IObjectLockChangeListener listener : listeners) { listener.locksChanged(); }
		}
	}

	@Override
	public void registerLockChangeListener(final IObjectLockChangeListener listener) {
		synchronized (listeners) { listeners.add(listener); }
	}

	@Override
	public void unregisterLockChangeListener(final IObjectLockChangeListener listener) {
		synchronized (listeners) { listeners.remove(listener); }		
	}
}
