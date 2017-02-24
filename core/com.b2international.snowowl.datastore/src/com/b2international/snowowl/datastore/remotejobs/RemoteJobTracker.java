/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.remotejobs;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.b2international.index.BulkUpdate;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * @since 5.7
 */
public final class RemoteJobTracker implements IDisposableService {

	private static class Holder {
		private static final Timer CLEANUP_TIMER = new Timer("RemoteJob cleanup", true);
	}
	
	private final class CleanUpTask extends TimerTask {
		@Override
		public void run() {
			try {
				index.write(writer -> {
					final Hits<RemoteJobEntry> hits = writer.searcher().search(Query.select(RemoteJobEntry.class)
							.where(
								Expressions.builder()
									.must(RemoteJobEntry.Expressions.deleted(true))
									.must(RemoteJobEntry.Expressions.done())
									.build()
							)
							.offset(0)
							.limit(Integer.MAX_VALUE)
							.build());
					if (hits.getTotal() > 0) {
						final Set<String> ids = FluentIterable.from(hits).transform(RemoteJobEntry::getId).toSet();
						System.err.println("purging " + ids);
						writer.removeAll(ImmutableMap.of(RemoteJobEntry.class, ids));
						writer.commit();
					}
					return null;
				});
			} catch (IllegalStateException e) {
				cancel();
			}
		}
	}
	
	private final AtomicBoolean disposed = new AtomicBoolean(false);
	private final Index index;
	private final RemoteJobChangeAdapter listener;
	private final CleanUpTask cleanUp;
	private final IEventBus events;
	private final ObjectMapper mapper;

	public RemoteJobTracker(Index index, IEventBus events, ObjectMapper mapper, final long remoteJobCleanUpInterval) {
		this.index = index;
		this.events = events;
		this.mapper = mapper;
		this.index.admin().create();
		this.listener = new RemoteJobChangeAdapter();
		Job.getJobManager().addJobChangeListener(listener);
		this.cleanUp = new CleanUpTask();
		Holder.CLEANUP_TIMER.schedule(cleanUp, remoteJobCleanUpInterval, remoteJobCleanUpInterval);
	}
	
	public RemoteJobs search(Expression query, int offset, int limit) {
		return index.read(searcher -> {
			final Hits<RemoteJobEntry> hits = searcher.search(
					Query.select(RemoteJobEntry.class)
						.where(Expressions.builder()
								.must(RemoteJobEntry.Expressions.deleted(false))
								.must(query)
								.build())
						.offset(offset)
						.limit(limit)
						.build()
					);
			return new RemoteJobs(hits.getHits(), hits.getOffset(), hits.getLimit(), hits.getTotal());
		});
	}
	
	@VisibleForTesting
	public RemoteJobEntry get(String jobId) {
		return index.read(searcher -> searcher.get(RemoteJobEntry.class, jobId));
	}
	
	public void requestCancel(String jobId) {
		final RemoteJobEntry job = get(jobId);
		if (job != null && !job.isCancelled()) {
			System.err.println("cancelling " + jobId);
			update(jobId, current -> RemoteJobEntry.from(current).state(RemoteJobState.CANCEL_REQUESTED).build());
			Job.getJobManager().cancel(SingleRemoteJobFamily.create(jobId));
		}
	}
	
	public void requestDeletes(Collection<String> jobIds) {
		final RemoteJobs jobEntries = search(Expressions.matchAny(DocumentMapping._ID, jobIds), 0, jobIds.size());
		final Set<String> existingJobIds = FluentIterable.from(jobEntries).transform(RemoteJobEntry::getId).toSet();
		Job[] existingJobs = Job.getJobManager().find(SingleRemoteJobFamily.create(existingJobIds));
		// mark existing jobs as deleted and cancel them
		final Set<String> remoteJobsToCancel = Sets.newHashSet();
		for (Job existingJob : existingJobs) {
			// cancel remote job
			if (existingJob instanceof RemoteJob) {
				RemoteJob remoteJob = (RemoteJob) existingJob;
				remoteJobsToCancel.add(remoteJob.getId());
			}
		}
		// delete all other jobs, that dont need to be cancelled
		final Set<String> remoteJobsToDelete = Sets.difference(Sets.newHashSet(jobIds), remoteJobsToCancel);
		index.write(writer -> {
			// if the job still running or scheduled, then mark it deleted and the done handler will delete it
			System.err.println("deleting " + remoteJobsToDelete);
			writer.removeAll(ImmutableMap.of(RemoteJobEntry.class, remoteJobsToDelete));
			final Function<RemoteJobEntry, RemoteJobEntry> setDeletedToTrue = current -> RemoteJobEntry.from(current).deleted(true).build();
			System.err.println("marking for deletion " + remoteJobsToCancel);
			writer.bulkUpdate(new BulkUpdate<>(RemoteJobEntry.class, Expressions.matchAny(DocumentMapping._ID, remoteJobsToCancel), RemoteJobEntry::getId, setDeletedToTrue));
			writer.commit();
			return null;
		});
		// finally cancel all jobs that need to be cancelled
		Job.getJobManager().cancel(SingleRemoteJobFamily.create(remoteJobsToCancel));
		notifyRemoved(existingJobIds);
	}
	
	private void put(String jobId, RemoteJobEntry job) {
		index.write(writer -> {
			writer.put(jobId, job);
			writer.commit();
			return null;
		});
		notifyAdded(jobId);
	}
	
	private void update(String jobId, Function<RemoteJobEntry, RemoteJobEntry> mutator) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(RemoteJobEntry.class, DocumentMapping.matchId(jobId), RemoteJobEntry::getId, mutator));
			writer.commit();
			return null;
		});
		notifyChanged(jobId);
	}

	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			this.cleanUp.cancel();
			Job.getJobManager().removeJobChangeListener(listener);
			this.index.admin().close();
		}
	}

	@Override
	public boolean isDisposed() {
		return disposed.get();
	}
	
	private void notifyAdded(String jobId) {
		RemoteJobNotification.added(jobId).publish(events);
	}
	
	private void notifyChanged(String jobId) {
		RemoteJobNotification.changed(jobId).publish(events);
	}
	
	private void notifyRemoved(Set<String> jobIds) {
		RemoteJobNotification.removed(jobIds).publish(events);
	}
	
	IProgressMonitor createMonitor(String jobId, IProgressMonitor monitor) {
		return new RemoteJobProgressMonitor(monitor, percentComplete -> update(jobId, current -> RemoteJobEntry.from(current).completionLevel(percentComplete).build()));
	}
	
	private class RemoteJobChangeAdapter extends JobChangeAdapter {
		
		@Override
		public void scheduled(IJobChangeEvent event) {
			if (event.getJob() instanceof RemoteJob) {
				final RemoteJob job = (RemoteJob) event.getJob();
				final String jobId = job.getId();
				System.err.println("scheduled " + jobId);
				// try to convert the request to a param object
				Map<String, Object> parameters;
				try {
					parameters = mapper.convertValue(job.getRequest(), Map.class);
				} catch (Throwable e) {
					parameters = Collections.emptyMap();
				}
				put(jobId, RemoteJobEntry.builder()
						.id(jobId)
						.description(job.getDescription())
						.user(job.getUser())
						.parameters(parameters)
						.scheduleDate(new Date())
						.build());
			}
		}
		
		@Override
		public void running(IJobChangeEvent event) {
			if (event.getJob() instanceof RemoteJob) {
				final RemoteJob job = (RemoteJob) event.getJob();
				final String jobId = job.getId();
				final Date startDate = new Date();
				System.err.println("running " + jobId);
				update(jobId, current -> {
					return RemoteJobEntry.from(current)
							.state(RemoteJobState.RUNNING)
							.startDate(startDate)
							.build();
				});
			}
		}
		
		@Override
		public void done(IJobChangeEvent event) {
			if (event.getJob() instanceof RemoteJob) {
				final RemoteJob job = (RemoteJob) event.getJob();
				final String jobId = job.getId();
				System.err.println("done " + jobId);
				final RemoteJobEntry jobEntry = get(jobId);
				if (jobEntry == null) {
					return;
				}
				final IStatus result = job.getResult();
				final Object response = job.getResponse();
				final Date finishDate = new Date();
				final RemoteJobState newState;
				if (result.isOK()) {
					newState = RemoteJobState.FINISHED;
				} else if (result.matches(IStatus.CANCEL)) {
					newState = RemoteJobState.CANCELLED;
				} else {
					newState = RemoteJobState.FAILED;
				}
				update(jobId, current -> {
					return RemoteJobEntry.from(current)
							.result(response)
							.finishDate(finishDate)
							.state(newState)
							.build();
				});
			}
		}
		
	}

}
