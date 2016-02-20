/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.jobs.MultiRule;

import com.b2international.commons.options.Options;
import com.b2international.commons.status.Statuses;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.MergeQueueEntry;
import com.b2international.snowowl.core.domain.MergeQueueEntry.Status;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ApiException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.remotejobs.AbstractRemoteJobEvent;
import com.b2international.snowowl.datastore.remotejobs.IRemoteJobManager;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobChangedEvent;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEventSwitch;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobState;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils;
import com.b2international.snowowl.datastore.server.remotejobs.RemoteJobKey;
import com.b2international.snowowl.datastore.server.remotejobs.RemoteJobResultRegistry;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.api.ISnomedBranchMergingService;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class SnomedBranchMergingService implements ISnomedBranchMergingService {

	public static class SourcePredicate implements Predicate<MergeQueueEntry> {
	
		private final String source;

		public SourcePredicate(final String source) {
			this.source = source;
		}
	
		@Override
		public boolean apply(final MergeQueueEntry input) {
			return input.getSource().equals(source);
		}
	}

	public static class TargetPredicate implements Predicate<MergeQueueEntry> {
	
		private final String target;

		public TargetPredicate(final String target) {
			this.target = target;
		}
	
		@Override
		public boolean apply(final MergeQueueEntry input) {
			return input.getTarget().equals(target);
		}
	}

	public static class StatusPredicate implements Predicate<MergeQueueEntry> {

		private final Status status;

		public StatusPredicate(final Status status) {
			this.status = status;
		}

		@Override
		public boolean apply(final MergeQueueEntry input) {
			return input.getStatus().equals(status);
		}
	}

	private static final int MAX_RESULTS = 100;
	private static final String PLUGIN_ID = "com.b2international.snowowl.snomed.api.impl";
	
	public static class MergeRemoteJob extends Job {
		
		private final Request<ServiceProvider, Branch> snomedRequest;

		public MergeRemoteJob(final Request<ServiceProvider, Branch> snomedRequest, final String source, final String target) {
			super(String.format("Applying change set from %s to %s", source, target));
			this.snomedRequest = snomedRequest;
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final IEventBus eventBus = ApplicationContext.getInstance().getServiceChecked(IEventBus.class);
			
			try {
				snomedRequest.executeSync(eventBus);
				return Statuses.ok();
			} catch (RuntimeException e) {
				return Statuses.error(PLUGIN_ID, "Exception caught while running merge job.", e);
			}
		}
	}

	public static class MergeRemoteJobKey extends RemoteJobKey {
		public MergeRemoteJobKey(final String path) {
			super(SnomedPackage.eNS_URI, BranchPathUtils.createPath(path));
		}
	}

	private final class MergeRemoteJobChangeHandler implements IHandler<IMessage> {
		@Override
		public void handle(final IMessage message) {
			new RemoteJobEventSwitch() {

				@Override
				protected void caseChanged(final RemoteJobChangedEvent event) {
					final UUID id = event.getId();
					final MergeQueueEntry entry = entries.get(id);

					if (entry == null) {
						return;
					}

					if (RemoteJobEntry.PROP_STATE.equals(event.getPropertyName())) {
						final RemoteJobState newState = (RemoteJobState) event.getNewValue();

						switch (newState) {
						case CANCEL_REQUESTED:
							entry.setStatus(Status.CANCEL_REQUESTED);
							break;
						case FAILED:
							entry.setStatus(Status.FAILED);
							break;
						case FINISHED: 
							entry.setStatus(Status.COMPLETED);
							break;
						case RUNNING:
							entry.setStatus(Status.IN_PROGRESS);
							break;
						case SCHEDULED:
							entry.setStatus(Status.SCHEDULED);
							break;
						default:
							throw new IllegalStateException(MessageFormat.format("Unexpected remote job state ''{0}''.", newState));
						}
					} else if (RemoteJobEntry.PROP_START_DATE.equals(event.getPropertyName())) {
						final Date newDate = (Date) event.getNewValue();
						entry.setStartDate(newDate);
					}
				}

			}.doSwitch(message.body(AbstractRemoteJobEvent.class));
		}
	}

	@Resource
	private IRemoteJobManager jobManager;

	@Resource
	private IEventBus eventBus;

	private RemoteJobResultRegistry<MergeQueueEntry> entries;
	private MergeRemoteJobChangeHandler changeHandler;

	@PostConstruct
	protected void init() {
		entries = new RemoteJobResultRegistry<>(MAX_RESULTS);
		entries.registerListeners();

		changeHandler = new MergeRemoteJobChangeHandler();
		eventBus.registerHandler(IRemoteJobManager.ADDRESS_REMOTE_JOB_CHANGED, changeHandler);
	}

	@PreDestroy
	protected void destroy() {
		if (changeHandler != null) {
			eventBus.unregisterHandler(IRemoteJobManager.ADDRESS_REMOTE_JOB_CHANGED, changeHandler);
			changeHandler = null;
		}

		if (entries != null) {
			entries.dispose();
			entries = null;
		}
	}

	@Override
	public UUID enqueue(final Request<ServiceProvider, Branch> snomedRequest, final String source, final String target) {

		final MergeRemoteJob remoteJob = new MergeRemoteJob(snomedRequest, source, target);

		// TODO: Make reasoner remote job rule conflicting and add it as additional items below 
		remoteJob.setRule(MultiRule.combine(
				new MergeRemoteJobKey(source), 
				new MergeRemoteJobKey(target)));

		RemoteJobUtils.configureProperties(remoteJob, SpecialUserStore.SYSTEM_USER_NAME, null, null);

		final UUID id = RemoteJobUtils.getRemoteJobId(remoteJob);
		final MergeQueueEntry entry = new MergeQueueEntry(id, source, target);
		entry.setStatus(Status.SCHEDULED);
		entry.setScheduledDate(new Date());
		entries.put(id, entry);

		remoteJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				MergeQueueEntry entry = entries.get(id);
				
				if (entry != null) {
					entry.setEndDate(new Date());
					final IStatus result = event.getResult();
					
					if (result.isOK()) {
						entry.setStatus(Status.COMPLETED);
					} else {
						entry.setStatus(Status.FAILED);
						if (result.getException() instanceof ApiException) {
							entry.setApiError(((ApiException) result.getException()).toApiError());
						}
					}
				}
			}
		});
		
		remoteJob.schedule();
		return id;
	}

	@Override
	public MergeQueueEntry findEntryById(final UUID id) {
		final MergeQueueEntry entry = entries.get(id);

		if (entry == null) {
			throw new NotFoundException("Merge queue entry", id.toString());
		} else {
			return entry;
		}
	}

	@Override
	public CollectionResource<MergeQueueEntry> findEntryByProperties(final Options options) {

		final List<Predicate<MergeQueueEntry>> predicates = Lists.newArrayList();

		if (options.containsKey("source")) { predicates.add(new SourcePredicate(options.getString("source"))); }
		if (options.containsKey("target")) { predicates.add(new TargetPredicate(options.getString("target"))); }
		if (options.containsKey("status")) { predicates.add(new StatusPredicate(options.get("status", Status.class))); }

		final List<MergeQueueEntry> results = FluentIterable.from(entries.getAllResults())
				.filter(Predicates.and(predicates))
				.toSortedList(Ordering.natural().onResultOf(new Function<MergeQueueEntry, Date>() {
					@Override public Date apply(MergeQueueEntry input) {
						return input.getScheduledDate();
					}
				}));

		return CollectionResource.of(results);
	}

	@Override
	public void cancel(final UUID id) {
		final MergeQueueEntry entry = entries.get(id);

		if (entry == null) {
			throw new NotFoundException("Merge queue entry", id.toString());
		} else {
			jobManager.cancelRemoteJob(id);
		}
	}
}
