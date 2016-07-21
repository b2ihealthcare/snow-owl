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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.CDOViewFunction;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @since 3.1
 */
@Singleton
public class ObjectLockManager implements IObjectLockManager, IListener {

	private static final Logger LOG = LoggerFactory.getLogger(ObjectLockManager.class);
	// lock states, the CDOID of the locked object is the key, and the person (the session ID) who locks it the value 
	private Map<CDOIDAndBranch, Integer> lockStates = newHashMap();
	private Collection<IObjectLockChangeListener> lockChangeListeners = newHashSet();
	
	private final ICDOConnection connection;
	private final EPackage context;
	private int currentSessionID;
	
	@Inject
	public ObjectLockManager(final ICDOConnection connection, final EPackage context) {
		this.connection = checkNotNull(connection, "connection");
		this.context = checkNotNull(context, "context");
		this.connection.getSession().options().setLockNotificationMode(LockNotificationMode.ALWAYS);
		this.connection.getSession().addListener(this);
		this.currentSessionID = connection.getSession().getSessionID();
		LOG.info("Creating lock manager for connection/context: {}/{}", connection.getUuid(), context.getNsURI());
	}
	
	@Override
	public Collection<Long> isLockedByOthersUpdate(final Collection<Long> storageKeys) {
		return CDOUtils.apply(new CDOViewFunction<Collection<Long>, CDOView>(getActiveBranch()) { @Override	protected Collection<Long> apply(CDOView view) {
			return isLockedByOthersUpdate(storageKeys, view);
		}});
	}
	
	protected Collection<Long> isLockedByOthersUpdate(Collection<Long> storageKeys, CDOView view) {
		final Collection<Long> lockedEntries = newHashSet();
		for (Long storageKey : storageKeys) {
			final CDOID id = toCDOID(storageKey);
			if (isLockedForceUpdate(id, view, false)) {
				if (getLockOwnerSessionId(id, view.getBranch()) != currentSessionID) {
					lockedEntries.add(storageKey);
				}
			}
		}
		notifyListeners();
		return lockedEntries;
	}

	@Override
	public String getLockOwnerUserId(final CDOID storageKey) {
		return getLockOwnerUserId(storageKey, getActiveBranch());
	}
	
	@Override
	public String getLockOwnerUserId(final CDOID storageKey, final CDOBranch branch) {
		return getLockOwnerUserId(toLong(storageKey), branch);
	}

	@Override
	public String getLockOwnerUserId(final long storageKey) {
		return getLockOwnerUserId(storageKey, getActiveBranch());
	}
	
	@Override
	public String getLockOwnerUserId(final long storageKey, final CDOBranch branch) {
		checkArgument(storageKey > 0, "storageKey must be specified and must be positive");
		// get the actual branch for this user
		final CDOIDAndBranch key = create(toCDOID(storageKey), branch);
		if (lockStates.containsKey(key)) {
			final Integer sessionId = lockStates.get(key);
			if (sessionId != null) {
				for (final CDORemoteSession session : connection.getSession().getRemoteSessionManager().getRemoteSessions()) {
					if (session != null && sessionId == session.getSessionID()) {
						return session.getUserID();
					}
				}
			}
		}
		return UNKNOWN_USER_ID;
	}
	
	@Override
	public boolean isLocked(final CDOID storageKey) {
		return isLocked(storageKey, getActiveBranch());
	}
	
	@Override
	public boolean isLocked(final long storageKey) {
		return isLocked(toCDOID(storageKey));
	}
	
	@Override
	public boolean isLockedForceUpdate(long storageKey) {
		return isLockedForceUpdate(toCDOID(storageKey));
	}
	
	@Override
	public boolean isLockedForceUpdate(final CDOID storageKey) {
		return isLockedForceUpdate(storageKey, getActiveBranch());
	}
	
	@Override
	public boolean isLockedForceUpdate(final CDOID storageKey, final CDOBranch branch) {
		return CDOUtils.apply(new CDOViewFunction<Boolean, CDOView>(branch) { @Override	protected Boolean apply(CDOView view) {
			return isLockedForceUpdate(storageKey, view);
		}});
	}
	
	@Override
	public boolean isLockedForceUpdate(final CDOID storageKey, final CDOView view) {
		return isLockedForceUpdate(storageKey, view, true);
	}

	private boolean isLockedForceUpdate(final CDOID storageKey, final CDOView view, boolean notify) {
		final CDOObject object = getObjectIfExists(view, storageKey);
		if (object != null) {
			final CDOLock writeOption = object.cdoWriteOption();
			final boolean locked = writeOption.isLocked() || writeOption.isLockedByOthers();
			process(object.cdoLockState(), locked ? Operation.LOCK : Operation.UNLOCK);
			if (notify) {
				notifyListeners();
			}
			return locked;
		}
		return false;
	}
	
	@Override
	public int getLockOwnerSessionId(final long storageKey) {
		return getLockOwnerSessionId(toCDOID(storageKey));
	}

	private int getLockOwnerSessionId(CDOID id) {
		return getLockOwnerSessionId(id, getActiveBranch());
	}

	private int getLockOwnerSessionId(final CDOID id, final CDOBranch branch) {
		if (isLocked(id, branch)) {
			synchronized (lockStates) {
				return lockStates.get(create(id, branch));				
			}
		}
		return -1;
	}
	
	@Override
	public boolean isLocked(final CDOID storageKey, final CDOBranch branch) {
		checkNotNull(storageKey, "storageKey");
		final CDOIDAndBranch key = create(storageKey, branch);
		synchronized (lockStates) {
			if (this.lockStates.containsKey(key)) {
				// locked if someone locks it
				return this.lockStates.get(key) >= 0;
			} else {
				// put not locked when we don't know about it
				this.lockStates.put(key, -1);
			}
			return false;
		}
	}
	
	/* returns the object with the specified storageKey or null if not exists */
	private CDOObject getObjectIfExists(final CDOView view, final CDOID storageKey) {
		try {
			return view.getObject(storageKey);
		} catch (final ObjectNotFoundException e) {
			return null;
		}
	}

	@Override
	public boolean isLocked(final long storageKey, final CDOView view) {
		return isLocked(toCDOID(storageKey), view.getBranch());
	}

	@Override
	public boolean lock(final CDOID storageKey, final CDOView view) {
		checkNotNull(storageKey, "storageKey");
		if (!CDOUtils.checkView(view)) {
			return false;
		}
		synchronized (lockStates) {
			// if not locked try to lock it
			if (!isLocked(storageKey, view.getBranch())) {
				final CDOObject object = getObjectIfExists(view, storageKey);
				if (object != null) {
					try {
						LOG.debug("Locking object: {}", storageKey);
						object.cdoWriteOption().lock(2000L);
						lockStates.put(create(storageKey, view.getBranch()), view.getSessionID());
						notifyListeners();
						return true;
					} catch (final TimeoutException e) {
						LOG.warn("Can't acquire lock for (timeout happened): {}", storageKey);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean lock(final long storageKey, final CDOView view) {
		return lock(toCDOID(storageKey), view);
	}

	@Override
	public void unlock(final CDOID storageKey, final CDOView view) {
		checkNotNull(storageKey, "storageKey");
		if (!CDOUtils.checkView(view)) {
			return;
		}
		synchronized (this.lockStates) {
			// unlock in the view
			final CDOObject object = getObjectIfExists(view, storageKey);
			if (object != null) {
				if (isLocked(storageKey, view.getBranch()) && object.cdoWriteOption().isLocked()) {
					LOG.debug("Unlocking object: {}", storageKey);
					object.cdoWriteOption().unlock();
				}
			}
			final Integer previousValue = this.lockStates.remove(create(storageKey, view.getBranch()));
			if (previousValue != null) {
				notifyListeners();
			}
		}
	}

	@Override
	public void unlock(final long storageKey, final CDOView view) {
		unlock(toCDOID(storageKey), view);
	}
	
	@Override
	public void dispose() {
		if (this.lockStates != null) {
			this.lockStates.clear();
		}
	}
	
	@Override
	public void notifyEvent(final IEvent event) {
		if (event instanceof CDOSessionLocksChangedEvent) {
			process((CDOSessionLocksChangedEvent)event);
		}
	}

	private void process(final CDOSessionLocksChangedEvent event) {
		// if the event come from the same session, ignore it
		if (event.getLockOwner().getSessionID() == connection.getSession().getSessionID()) {
			return;
		}
		LOG.debug("Processing lock change event from: {}", event.getSource().getUserID());
		synchronized (lockStates) {
			for (final CDOLockState state : event.getLockStates()) {
				process(state, event.getOperation());
			}
			notifyListeners();
		}
	}
	
	private void process(final CDOLockState state, final Operation operation) {
		LOG.debug("LockState change: {}, {}", state, operation);			
		final Object lockedObject = state.getLockedObject();
		if (lockedObject instanceof CDOIDAndBranch) {
			lockStates.put((CDOIDAndBranch) lockedObject, Operation.LOCK == operation ? state.getWriteOptionOwner().getSessionID() : -1);
		} else {
			throw new IllegalArgumentException("Unknown type: " + lockedObject);
		}
	}

	private void notifyListeners() {
		synchronized (lockChangeListeners) {
			for (final IObjectLockChangeListener listener : lockChangeListeners) {
				listener.locksChanged();
			}
		}
	}

	private CDOID toCDOID(final long storageKey) {
		checkArgument(storageKey > 0, "StorageKey must be higher than zero");
		return CDOIDUtil.createLong(storageKey);
	}
	
	private long toLong(final CDOID storageKey) {
		return CDOIDUtil.getLong(storageKey);
	}

	@Override
	public void registerLockChangeListener(final IObjectLockChangeListener listener) {
		synchronized (lockChangeListeners) {
			lockChangeListeners.add(listener);
		}
	}

	@Override
	public void unregisterLockChangeListener(final IObjectLockChangeListener listener) {
		synchronized (lockChangeListeners) {
			lockChangeListeners.remove(listener);
		}		
	}
	
	private CDOIDAndBranch create(final CDOID id, final CDOBranch branch) {
		return CDOIDUtil.createIDAndBranch(id, branch);
	}
	
	private CDOBranch getActiveBranch() {
		throw new UnsupportedOperationException("Not supported on the server side");
	}
	
}