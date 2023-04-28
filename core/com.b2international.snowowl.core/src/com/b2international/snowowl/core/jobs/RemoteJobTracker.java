/*
 * Copyright 2017-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.jobs;

import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.*;
import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * @since 5.7
 */
public final class RemoteJobTracker implements IDisposableService {

	private static final Logger LOG = LoggerFactory.getLogger("jobs");
	
	private final AtomicBoolean disposed = new AtomicBoolean(false);
	private final Index index;
	private final RemoteJobChangeAdapter listener;
	private final IEventBus events;
	private final ObjectMapper mapper;

	private final int purgeThreshold;
	private final long staleJobAge;
	
	private final AtomicInteger jobCounter;

	public RemoteJobTracker(Index index, IEventBus events, ObjectMapper mapper, final int purgeThreshold, final long staleJobAge) {
		
		this.index = index;
		this.events = events;
		this.mapper = mapper;
		this.purgeThreshold = purgeThreshold;
		this.staleJobAge = staleJobAge;
		
		this.index.admin().create();
		
		// query all existing remote job entries and set their status to FAILED if they are either in SCHEDULED/RUNNING/CANCEL_REQUESTED state
		// used for jobs either stuck or suspended by a restart
		convertSuspendedJobStatuses();
		
		// get the number of completed jobs to be able to determine when purge is necessary
		jobCounter = new AtomicInteger(getNumberOfCompletedJobs());
		
		LOG.trace("Initialized remote job tracker{}", jobCounter.get() > 0 ? " with " + jobCounter.get() + " jobs in 'DONE' state" : "");
		
		this.listener = new RemoteJobChangeAdapter();
		Job.getJobManager().addJobChangeListener(listener);
		
	}

	public RemoteJobs search(Expression query, int limit) {
		return search(query, List.of(), SortBy.DEFAULT, limit); 
	}
	
	private RemoteJobs search(Expression query, List<String> fields, SortBy sortBy, int limit) {
		final Hits<RemoteJobEntry> hits = searchHits(query, fields, sortBy, limit);
		return new RemoteJobs(hits.getHits(), null, hits.getLimit(), hits.getTotal());
	}
	
	private Hits<RemoteJobEntry> searchHits(Expression query, List<String> fields, SortBy sortBy, int limit) {
		return index.read(searcher -> {
			return searcher.search(
					Query.select(RemoteJobEntry.class)
					.fields(fields)
					.where(Expressions.bool()
							.filter(RemoteJobEntry.Expressions.deleted(false))
							.filter(query)
							.build())
					.sortBy(sortBy)
					.limit(limit)
					.build()
					);
		});
	}
	
	
	public void requestCancel(String jobId) {
		final RemoteJobEntry job = get(jobId);
		if (job != null && !job.isCancelled()) {
			LOG.trace("Cancelling job {}", jobId);
			update(jobId, RemoteJobEntry.WITH_STATE, Map.of("expectedState", RemoteJobState.RUNNING.name(), "newState", RemoteJobState.CANCEL_REQUESTED.name()));
			Job.getJobManager().cancel(SingleRemoteJobFamily.create(jobId));
		}
	}
	
	public void requestDeletes(Collection<String> jobIds) {
		final RemoteJobs jobEntries = search(RemoteJobEntry.Expressions.ids(jobIds), jobIds.size());
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
			writer.removeAll(Map.of(RemoteJobEntry.class, remoteJobsToDelete));
			if (!remoteJobsToCancel.isEmpty()) {
				LOG.trace("Marking deletable jobs {}", remoteJobsToCancel);
				writer.bulkUpdate(new BulkUpdate<>(RemoteJobEntry.class, RemoteJobEntry.Expressions.ids(remoteJobsToCancel), RemoteJobEntry.WITH_DELETED));
			}
			writer.commit();
			return null;
		});
		// finally cancel all jobs that need to be cancelled
		Job.getJobManager().cancel(SingleRemoteJobFamily.create(remoteJobsToCancel));
		notifyRemoved(existingJobIds);
	}
	
	private void put(RemoteJobEntry job) {
		index.write(writer -> {
			writer.put(job);
			writer.commit();
			return null;
		});
		notifyAdded(job.getId());
	}
	
	private void update(String jobId, String script, Map<String, Object> params) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(RemoteJobEntry.class, RemoteJobEntry.Expressions.id(jobId), script, params));
			writer.commit();
			return null;
		});
		notifyChanged(jobId);
	}
	
	private void convertSuspendedJobStatuses() {
		
		this.index.write(writer -> {
			final Expression filter = RemoteJobEntry.Expressions.states(Set.of(RemoteJobState.SCHEDULED, RemoteJobState.RUNNING, RemoteJobState.CANCEL_REQUESTED));
			final BulkUpdate<RemoteJobEntry> update = new BulkUpdate<>(
				RemoteJobEntry.class, 
				filter, 
				RemoteJobEntry.WITH_DONE,
				ImmutableMap.of("state", RemoteJobState.FAILED.name(), "finishDate", System.currentTimeMillis())
			);
			writer.bulkUpdate(update);
			writer.commit();
			return null;
		});
		
	}

	private int getNumberOfCompletedJobs() {
		
		return index.read(searcher -> {
			return searcher.search(
				Query.select(RemoteJobEntry.class)
					.fields(RemoteJobEntry.Fields.STATE)
					.where(Expressions.bool()
							.filter(RemoteJobEntry.Expressions.done())
							.build())
					.limit(0)
					.build()
			);
		})
		.getTotal();
		
	}

	@VisibleForTesting
	public RemoteJobEntry get(String jobId) {
		return index.read(searcher -> searcher.get(RemoteJobEntry.class, jobId));
	}

	@VisibleForTesting
	public int getJobCounter() {
		return jobCounter.get();
	}

	private void purge() {
		
		index.write(writer -> {
			
			final Hits<RemoteJobEntry> hits = writer.searcher().search(Query.select(RemoteJobEntry.class)
					.fields(RemoteJobEntry.Fields.ID, RemoteJobEntry.Fields.DELETED)
					.where(
						Expressions.bool()
							.must(RemoteJobEntry.Expressions.done())
							.must(Expressions.bool()
									.should(RemoteJobEntry.Expressions.deleted(true))
									.should(RemoteJobEntry.Expressions.finishDate(0L, System.currentTimeMillis() - staleJobAge))
									.build()
							)
							.build()
					)
					.limit(Integer.MAX_VALUE)
					.build());
			
			if (hits.getTotal() > 0) {
				
				Set<String> idsToRemove = hits.stream().map(RemoteJobEntry::getId).collect(toSet());
				
				if (!idsToRemove.isEmpty()) {
					
					Set<String> staleEntries = hits.stream()
							.filter(entry -> !entry.isDeleted())
							.map(RemoteJobEntry::getId)
							.collect(toSet());
			
					LOG.trace("Purging {} deleted and {} stale jobs", Sets.difference(idsToRemove, staleEntries).size(), staleEntries.size());
					
					writer.remove(RemoteJobEntry.class, idsToRemove);
					writer.commit();
					
					// send notification for stale entry removal
					notifyRemoved(staleEntries);
					
				}
				
			}
			
			return null;
		});
		
		// reset job counter upon successful purge
		jobCounter.set(0);
		
	}

	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			Job.getJobManager().removeJobChangeListener(listener);
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
		return new RemoteJobProgressMonitor(monitor, percentComplete -> update(jobId, RemoteJobEntry.WITH_COMPLETION_LEVEL, Map.of("completionLevel", percentComplete)));
	}
	
	private class RemoteJobChangeAdapter extends JobChangeAdapter {
		
		@Override
		public void scheduled(IJobChangeEvent event) {
			if (event.getJob() instanceof RemoteJob) {
				final RemoteJob job = (RemoteJob) event.getJob();
				final String jobId = job.getId();
				LOG.trace("Scheduled job {}", jobId);
				// try to convert the request to a param object
				String parameters;
				try {
					parameters = mapper.writeValueAsString(job.getParameters(mapper));
				} catch (Throwable e) {
					parameters = "";
				}
				put(RemoteJobEntry.builder()
						.id(jobId)
						.key(job.getKey())
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
				update(jobId, RemoteJobEntry.WITH_RUNNING, Map.of("state", RemoteJobState.RUNNING.name(), "startDate", System.currentTimeMillis()));
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
				final String response = job.getResponse();
				final RemoteJobState newState;
				if (result == null) {
					newState = RemoteJobState.CANCELED;
				} else if (result.isOK()) {
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
				params.put("state", newState.name());
				params.put("finishDate", System.currentTimeMillis());
				
				update(jobId, RemoteJobEntry.WITH_DONE, params.build());
				
				int numberOfJobs = jobCounter.incrementAndGet();
				LOG.trace("Incrementing job counter to {}", numberOfJobs);
				
				if (numberOfJobs >= purgeThreshold) {
					LOG.trace("Triggering remote job purge ({})", numberOfJobs);
					purge();
				}
				
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
			public <T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) throws IOException {
				throw new UnsupportedOperationException();
			}

			@Override
			public <T> T get(Class<T> type, String key) throws IOException {
				return index.read(searcher -> searcher.get(type, key));
			}

			@Override
			public <T> Iterable<T> get(Class<T> type, Iterable<String> keys) throws IOException {
				return index.read(searcher -> searcher.get(type, keys));
			}
		};
	}
	
}
