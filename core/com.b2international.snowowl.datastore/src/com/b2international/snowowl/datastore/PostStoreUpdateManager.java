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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.IPostStoreUpdateManager;
import com.b2international.snowowl.datastore.tasks.TaskManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;


/**
 * Class for notifying subscribed client about post store update events.
 * <br>This service is registered to the {@link ApplicationContext application context} via
 * the {@link IPostStoreUpdateManager} interface.
 */
public class PostStoreUpdateManager implements IPostStoreUpdateManager {

	private final class SessionInvalidationListener implements IListener {
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.net4j.util.event.IListener#notifyEvent(org.eclipse.net4j.util.event.IEvent)
		 */
		@Override 
		public void notifyEvent(final IEvent event) {
			if (event instanceof CDOSessionInvalidationEvent) {
				notifyListeners((CDOSessionInvalidationEvent) event);
			}
		}
	}

	/**Listeners listening for {@link CDOSessionInvalidationEvent} on all branches in all repositories.*/
	private final Set<IPostStoreUpdateListener> listeners = Sets.newHashSet();
	/**Listeners listening for {@link CDOSessionInvalidationEvent} only for active branch in all repositories.*/
	private final Set<IPostStoreUpdateListener> activeBranchListeners = Sets.newHashSet();
	/**Listeners listening for {@link CDOSessionInvalidationEvent} on all branches in a given repository.*/
	private final Multimap<String, IPostStoreUpdateListener> repositoryAwareListeners = 
			HashMultimap.<String, IPostStoreUpdateListener>create();
	/**Listeners listening for {@link CDOSessionInvalidationEvent} only for active branch in a given repository.*/
	private final Multimap<String, IPostStoreUpdateListener> repositoryAwareActiveBranchListeners = 
			HashMultimap.<String, IPostStoreUpdateListener>create();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PostStoreUpdateManager.class);

	public PostStoreUpdateManager(final Iterable<? extends CDOSession> sessions) {
		for (final CDOSession session : sessions) {
			
			Preconditions.checkNotNull(session, "Session argument cannot be null.");
			session.addListener(new SessionInvalidationListener());
			
		}
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.ICDOChangeManager#addPostStoreUpdateListener(com.b2international.snowowl.datastore.IPostStoreUpdateListener)
	 */
	@Override
	public void addPostStoreUpdateListener(final IPostStoreUpdateListener listener) {
		addPostStoreUpdateListener(listener, true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.IPostStoreUpdateManager#addPostStoreUpdateListener(com.b2international.snowowl.datastore.IPostStoreUpdateListener, boolean)
	 */
	@Override
	public void addPostStoreUpdateListener(final IPostStoreUpdateListener listener, final boolean activeBranchOnly) {
		
		if (listener instanceof IPostStoreUpdateListener2) {
			
			if (activeBranchOnly) {
				
				repositoryAwareActiveBranchListeners.put(((IPostStoreUpdateListener2) listener).getRepositoryUuid(), listener);
				
			} else {
				
				repositoryAwareListeners.put(((IPostStoreUpdateListener2) listener).getRepositoryUuid(), listener);
				
			}
			
		} else {
			
			if (activeBranchOnly) {
				activeBranchListeners.add(listener);
			} else {
				listeners.add(listener);
			}
			
		}
		
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.ICDOChangeManager#removePostStoreUpdateListener(com.b2international.snowowl.datastore.IPostStoreUpdateListener)
	 */
	@Override
	public void removePostStoreUpdateListener(final IPostStoreUpdateListener listener) {
		if (null == listener) {
			return;
		}
		
		if (listener instanceof IPostStoreUpdateListener2) {
			
			repositoryAwareActiveBranchListeners.remove(((IPostStoreUpdateListener2) listener).getRepositoryUuid(), listener);
			repositoryAwareListeners.remove(((IPostStoreUpdateListener2) listener).getRepositoryUuid(), listener);
			
		} else {
		
			activeBranchListeners.remove(listener);
			listeners.remove(listener);
		
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.IPostStoreUpdateManager#notifyListeners()
	 */
	@Override
	public void notifyListeners() {
		notifyListeners(null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.IPostStoreUpdateManager#notifyListeners(org.eclipse.emf.cdo.common.commit.CDOCommitInfo)
	 */
	@Override
	public void notifyListeners(final CDOCommitInfo invalidationEvent) {
		
		if (null != invalidationEvent) {
			
			final CDOBranch branch = invalidationEvent.getBranch();
			if (null != branch) {
				final IBranchPath branchPath = createPath(branch);
				final String repositoryName = getServiceForClass(ICDOConnectionManager.class).get(branch).getRepositoryName();
				LOGGER.info(MessageFormat.format("Commit notification received for user {0} on ''{1}'' branch from ''{2}''.", invalidationEvent.getUserID(), branchPath, repositoryName));
			} else {
				LOGGER.info(MessageFormat.format("Commit notification received for user {0}.", invalidationEvent.getUserID()));
			}

			final TaskManager taskManager = ApplicationContext.getInstance().getService(TaskManager.class);
			// XXX: notification might arrive during startup, before task manager is registered
			if (null != taskManager && isCommitOnActiveBranch(invalidationEvent, taskManager)) {
				doNotifyListeners(activeBranchListeners, invalidationEvent);
				doNotifyListeners(repositoryAwareActiveBranchListeners, invalidationEvent);
			}
			
		} else {
			doNotifyListeners(activeBranchListeners, invalidationEvent);
			doNotifyListeners(repositoryAwareActiveBranchListeners, invalidationEvent);
		}
		
		doNotifyListeners(listeners, invalidationEvent);
		doNotifyListeners(repositoryAwareListeners, invalidationEvent);
	}

	private boolean isCommitOnActiveBranch(final CDOCommitInfo commitInfo, final TaskManager taskManager) {
		final ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(commitInfo.getBranch());
		final String repositoryId = connection.getUuid();
		return BranchPathUtils.createPath(commitInfo.getBranch()).equals(taskManager.getActiveBranch(repositoryId));
	}

	private void doNotifyListeners(final Multimap<String, IPostStoreUpdateListener> listeners, final CDOCommitInfo commitInfo) {
		
		// Shortcut: notify all repository-aware, branch specific listeners on a null commitinfo, as it is intended as a general notification
		if (null == commitInfo) {
			doNotifyListeners(ImmutableSet.copyOf(listeners.values()), commitInfo);
			return;
		}
		
		final String uuid = getRepositoryUuid(commitInfo);

		if (null != uuid) {
			
			final Collection<IPostStoreUpdateListener> $ = listeners.get(uuid);
			if (!CompareUtils.isEmpty($)) {
				doNotifyListeners(ImmutableSet.copyOf($), commitInfo);
			}
		}
	}
	
	private void doNotifyListeners(final Set<IPostStoreUpdateListener> listenerSet, final CDOCommitInfo commitInfo) {
		//copy listeners collection to avoid concurrent modification exception
		for (final IPostStoreUpdateListener listener : ImmutableSet.copyOf(listenerSet)) {
			if (null != listener) {
				listener.storeUpdated(commitInfo);
			}
		}
	}
	
	/*returns with the unique ID of the repository from where the current commit info came from.*/
	@Nullable private String getRepositoryUuid(@Nullable final CDOCommitInfo commitInfo) {
		
		if (null == commitInfo) {
			return null;
		}
		
		final CDOBranch branch = commitInfo.getBranch();
		//for failure commit info
		if (null == branch) {
			return null;
		}
		
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		return connectionManager.get(branch).getUuid();
		
	}
}