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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.BulkUpdate;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.Scroll;
import com.b2international.index.Searcher;
import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * @since 5.7
 */
public final class RemoteJobTracker implements IDisposableService {

	private static final Logger LOG = LoggerFactory.getLogger("jobs");
	
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
									.filter(RemoteJobEntry.Expressions.deleted(true))
									.filter(RemoteJobEntry.Expressions.done())
									.build()
							)
							.limit(Integer.MAX_VALUE)
							.build());
					if (hits.getTotal() > 0) {
						final Set<String> ids = FluentIterable.from(hits).transform(RemoteJobEntry::getId).toSet();
						LOG.trace("Purging job entries {}", ids);
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
	
	public RemoteJobs search(Expression query, int limit) {
		return search(query, ImmutableList.of(), SortBy.DOC_ID, limit); 
	}
	
	private RemoteJobs search(Expression query, List<String> fields, SortBy sortBy, int limit) {
		final Hits<RemoteJobEntry> hits = searchHits(query, fields, sortBy, limit);
		return new RemoteJobs(hits.getHits(), null, null, hits.getLimit(), hits.getTotal());
	}
	
	private Hits<RemoteJobEntry> searchHits(Expression query, List<String> fields, SortBy sortBy, int limit) {
		return index.read(searcher -> {
			return searcher.search(
					Query.select(RemoteJobEntry.class)
					.fields(fields)
					.where(Expressions.builder()
							.filter(RemoteJobEntry.Expressions.deleted(false))
							.filter(query)
							.build())
					.sortBy(sortBy)
					.limit(limit)
					.build()
					);
		});
	}
	
	
	@VisibleForTesting
	public RemoteJobEntry get(String jobId) {
		return index.read(searcher -> searcher.get(RemoteJobEntry.class, jobId));
	}
	
	public void requestCancel(String jobId) {
		final RemoteJobEntry job = get(jobId);
		if (job != null && !job.isCancelled()) {
			LOG.trace("Cancelling job {}", jobId);
			update(jobId, RemoteJobEntry.WITH_STATE, ImmutableMap.of("expectedState", RemoteJobState.RUNNING, "newState", RemoteJobState.CANCEL_REQUESTED));
			Job.getJobManager().cancel(SingleRemoteJobFamily.create(jobId));
		}
	}
	
	public void requestDeletes(Collection<String> jobIds) {
		final RemoteJobs jobEntries = search(Expressions.matchAny(DocumentMapping._ID, jobIds), jobIds.size());
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
			LOG.trace("Deleting jobs {}", remoteJobsToDelete);
			writer.removeAll(ImmutableMap.of(RemoteJobEntry.class, remoteJobsToDelete));
			if (!remoteJobsToCancel.isEmpty()) {
				LOG.trace("Marking deletable jobs {}", remoteJobsToCancel);
				writer.bulkUpdate(new BulkUpdate<>(RemoteJobEntry.class, Expressions.matchAny(DocumentMapping._ID, remoteJobsToCancel), RemoteJobEntry.Fields.ID, RemoteJobEntry.WITH_DELETED));
			}
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
	
	private void update(String jobId, String script, Map<String, Object> params) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(RemoteJobEntry.class, DocumentMapping.matchId(jobId), RemoteJobEntry.Fields.ID, script, params));
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
		return new RemoteJobProgressMonitor(monitor, percentComplete -> update(jobId, RemoteJobEntry.WITH_COMPLETION_LEVEL, ImmutableMap.of("completionLevel", percentComplete)));
	}
	
	private class RemoteJobChangeAdapter extends JobChangeAdapter {
		
		@Override
		public void scheduled(IJobChangeEvent event) {
			if (event.getJob() instanceof RemoteJob) {
				final RemoteJob job = (RemoteJob) event.getJob();
				final String jobId = job.getId();
				LOG.trace("Scheduled job {}", jobId);
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
				LOG.trace("Running job {}", jobId);
				update(jobId, RemoteJobEntry.WITH_RUNNING, ImmutableMap.of("state", RemoteJobState.RUNNING, "startDate", System.currentTimeMillis()));
			}
		}
		
		@Override
		public void done(IJobChangeEvent event) {
			if (event.getJob() instanceof RemoteJob) {
				final RemoteJob job = (RemoteJob) event.getJob();
				final String jobId = job.getId();
				LOG.trace("Completed job {}", jobId);
				final RemoteJobEntry jobEntry = get(jobId);
				if (jobEntry == null) {
					LOG.warn("Missing job entry in RemoteJobTracker#done '{}'", jobId);
					return;
				}
				final IStatus result = job.getResult();
				final Object response = job.getResponse();
				final RemoteJobState newState;
				if (result.isOK()) {
					newState = RemoteJobState.FINISHED;
				} else if (result.matches(IStatus.CANCEL)) {
					newState = RemoteJobState.CANCELED;
				} else {
					newState = RemoteJobState.FAILED;
				}
				
				ImmutableMap.Builder<String, Object> params = ImmutableMap.builder();
				if (response != null) {
					params.put("result", response);
				}
				params.put("state", newState);
				params.put("finishDate", System.currentTimeMillis());
				update(jobId, RemoteJobEntry.WITH_DONE, params.build());
			}
		}
		
	}

	public Searcher searcher() {
		return new Searcher() {
			@Override
			public <T> Hits<T> search(Query<T> query) throws IOException {
				return (Hits<T>) searchHits(query.getWhere(), query.getFields(), query.getSortBy(), query.getLimit());
			}
			
			@Override
			public <T> Hits<T> scroll(Scroll<T> scroll) throws IOException {
				return index.read(searcher -> searcher.scroll(scroll));
			}
			
			@Override
			public void cancelScroll(String scrollId) {
				index.read(searcher -> {
					searcher.cancelScroll(scrollId);
					return null;
				});
			}
			
			@Override
			public <T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) throws IOException {
				throw new UnsupportedOperationException();
			}
		};
	}

}
