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
package com.b2international.snowowl.datastore.server.remotejobs;

import static com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils.getJobSpecificAddress;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.datastore.remotejobs.IRemoteJobManager;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobAddedEvent;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobChangedEvent;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobRemovedEvent;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobState;
import com.b2international.snowowl.datastore.remotejobs.SingleRemoteJobFamily;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

/**
 */
public class RemoteJobManager implements IRemoteJobManager, IDisposableService {

	private final class RemoteJobEntryChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			final RemoteJobEntry changedEntry = (RemoteJobEntry) evt.getSource();
			firePropertyChanged(changedEntry.getId(), evt.getPropertyName(), (Serializable) evt.getNewValue());
		}
	}
	
	private final class DonePredicate implements Predicate<RemoteJobEntry> {
		@Override
		public boolean apply(final RemoteJobEntry input) {
			return input.isDone();
		}
	}
	
	private static final class UserPredicate implements Predicate<RemoteJobEntry> {
		private final String userId;
		
		public UserPredicate(final String userId) {
			this.userId = userId;
		}

		@Override
		public boolean apply(final RemoteJobEntry input) {
			return userId.equals(input.getRequestingUserId());
		}
	}

	private static final int MAXIMUM_FINISHED_JOBS_TO_KEEP = 50;

	private final AtomicBoolean disposed = new AtomicBoolean(false);
	
	private final RemoteJobChangeListener remoteJobChangeListener = new RemoteJobChangeListener(this);
	private final PropertyChangeListener remoteJobEntryChangeListener = new RemoteJobEntryChangeListener();
	
	private final ConcurrentMap<UUID, RemoteJobEntry> jobs = new MapMaker().makeMap();
	private final ConcurrentMap<UUID, ListenableProgressMonitor> monitors = new MapMaker().makeMap();

	public RemoteJobManager() {
		Job.getJobManager().addJobChangeListener(remoteJobChangeListener);
	}

	@Override
	public Set<RemoteJobEntry> getAllRemoteJobs() {
		return ImmutableSet.copyOf(jobs.values());
	}

	@Override
	public Set<RemoteJobEntry> getRemoteJobsByUser(final String userId) {
		Preconditions.checkNotNull(userId, "User identifier may not be null.");
		return ImmutableSet.copyOf(Iterables.filter(jobs.values(), new UserPredicate(userId)));
	}

	@Override
	public void removeFinishedRemoteJobs() {
		removeIf(new DonePredicate());
	}

	@Override
	public void removeFinishedRemoteJobsByUser(final String userId) {
		Preconditions.checkNotNull(userId, "User identifier may not be null.");
		removeIf(Predicates.and(new DonePredicate(), new UserPredicate(userId)));
	}

	private void trimOldFinishedJobs() {
		final List<RemoteJobEntry> knownFinishedJobs = Lists.newArrayList(Iterables.filter(getAllRemoteJobs(), new DonePredicate()));
		
		Collections.sort(knownFinishedJobs, new Comparator<RemoteJobEntry>() { @Override public int compare(final RemoteJobEntry o1, final RemoteJobEntry o2) {
				return o1.getFinishDate().compareTo(o2.getFinishDate());
		}});
		
		for (int i = 0; i < Math.max(0, knownFinishedJobs.size() - MAXIMUM_FINISHED_JOBS_TO_KEEP); i++) {
			cancelRemoteJob(knownFinishedJobs.get(i).getId());
		}
	}
	
	private void removeIf(final Predicate<RemoteJobEntry> predicate) {
		removeIf(predicate, Integer.MAX_VALUE);
	}
	
	private void removeIf(final Predicate<RemoteJobEntry> predicate, final int limit) {
		for (final RemoteJobEntry existingEntry : getAllRemoteJobs()) {
			if (predicate.apply(existingEntry)) {
				remove(existingEntry.getId());
			}
		}
	}

	@Override
	public void cancelRemoteJob(final UUID id) {
		Preconditions.checkNotNull(id, "Remote job identifier may not be null.");
		final RemoteJobEntry existingEntry = jobs.get(id);

		if (null == existingEntry) {
			return;
		} else {
			if (existingEntry.isDone()) {
				remove(id);
			} else {
				canceling(id);
			}
		}
		
		Job.getJobManager().cancel(SingleRemoteJobFamily.create(id.toString()));
	}

	@Override
	public boolean isDisposed() {
		return disposed.get();
	}
	
	@Override
	public void dispose() {
		disposed.compareAndSet(false, true);
	}

	/**
	 * (non-API)
	 */
	public void addMonitor(final UUID id, final ListenableProgressMonitor monitor) {
		Preconditions.checkNotNull(id, "Remote job identifier may not be null.");
		Preconditions.checkNotNull(monitor, "Progress monitor may not be null.");

		if (null != monitors.putIfAbsent(id, monitor)) {
			throw new IllegalStateException(MessageFormat.format("Remote job with identifier {0} is already registered.", id));
		}
	}
	
	private void removeMonitor(final UUID id) {
		final ListenableProgressMonitor monitor = monitors.remove(id);
		if (null != monitor) {
			monitor.done();
			monitor.clearListeners();
		}
	}
	
	/**
	 * (non-API)
	 */
	public void add(final UUID id, final String description, final String requestingUserId, final String customCommandId) {
		Preconditions.checkNotNull(id, "Remote job identifier may not be null.");
		Preconditions.checkNotNull(description, "Description may not be null.");
		Preconditions.checkNotNull(requestingUserId, "Requesting user identifier not be null.");
		
		final RemoteJobEntry newEntry = new RemoteJobEntry(id, description, requestingUserId, customCommandId);
		
		if (null != jobs.putIfAbsent(id, newEntry)) {
			throw new IllegalStateException(MessageFormat.format("Remote job with identifier {0} is already registered.", id));
		}

		newEntry.addPropertyChangeListener(remoteJobEntryChangeListener);
		fireAdded(newEntry);
	}

	/**
	 * (non-API)
	 */
	public void running(final UUID id) {
		update(id, new Procedure<RemoteJobEntry>() { @Override public void doApply(final RemoteJobEntry input) {
			input.setState(RemoteJobState.RUNNING);
			input.setStartDate(new Date());
		}});
	}
	
	private void canceling(final UUID id) {
		final RemoteJobState cancelRequested = RemoteJobState.CANCEL_REQUESTED;
		
		update(id, new Procedure<RemoteJobEntry>() { @Override public void doApply(final RemoteJobEntry input) {
			input.setState(cancelRequested);
			input.setFinishDate(new Date());
		}});
		
		fireDone(id, cancelRequested);
	}
	
	/**
	 * (non-API)
	 */
	public void updateUnits(final UUID id, final int newUnits) {
		update(id, new Procedure<RemoteJobEntry>() { @Override public void doApply(final RemoteJobEntry input) {
			input.setCompletionLevel(newUnits);
		}});
	}

	/**
	 * (non-API)
	 */
	public void finished(final UUID id, final IStatus result) {
		if (IStatus.CANCEL == result.getSeverity()) {
			remove(id);
		} else {
			final RemoteJobState finishState = (result.isOK()) ? RemoteJobState.FINISHED : RemoteJobState.FAILED;
			update(id, new Procedure<RemoteJobEntry>() { @Override public void doApply(final RemoteJobEntry input) {
				input.setState(finishState);
				input.setFinishDate(new Date());
			}});
			
			fireDone(id, finishState);
		}
		
		trimOldFinishedJobs();
	}
	
	private void remove(final UUID id) {
		removeMonitor(id);
		final RemoteJobEntry removedEntry = jobs.remove(id);
		if (null != removedEntry) {
			removedEntry.removePropertyChangeListener(remoteJobEntryChangeListener);
			fireRemoved(removedEntry);
		}
	}
	
	private void update(final UUID id, final Procedure<RemoteJobEntry> updateProcedure) {
		Preconditions.checkNotNull(id, "Remote job identifier may not be null.");
		Preconditions.checkNotNull(updateProcedure, "Transformation function may not be null.");
		
		final RemoteJobEntry existingEntry = jobs.get(id);
		if (null != existingEntry) {
			updateProcedure.apply(existingEntry);
		}
	}

	private void fireAdded(final RemoteJobEntry entry) {
		getEventBus().publish(ADDRESS_REMOTE_JOB_CHANGED, new RemoteJobAddedEvent(entry));
	}

	private void firePropertyChanged(final UUID id, final String propertyName, final Serializable newValue) {
		getEventBus().publish(ADDRESS_REMOTE_JOB_CHANGED, new RemoteJobChangedEvent(id, propertyName, newValue));
	}

	private void fireRemoved(final RemoteJobEntry entry) {
		getEventBus().publish(ADDRESS_REMOTE_JOB_CHANGED, new RemoteJobRemovedEvent(entry.getId()));
	}

	private void fireDone(final UUID id, final RemoteJobState finishState) {
		getEventBus().publish(getJobSpecificAddress(ADDRESS_REMOTE_JOB_COMPLETED, id), finishState);
	}

	private IEventBus getEventBus() {
		return getApplicationContext().getService(IEventBus.class);
	}

	private ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
}