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
package com.b2international.snowowl.snomed.reasoner.classification;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.Notifications;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobNotification;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobs;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;
import com.google.common.collect.Queues;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Represents an abstract operation which requires the classification of the
 * SNOMED CT terminology on the active branch path.
 * 
 * @param T the operation return type
 * @since
 */
public abstract class ClassifyOperation<T> {

	private static final long CHECK_JOB_INTERVAL_SECONDS = 2L;

	protected final String reasonerId;
	protected final String userId;
	protected final List<SnomedConcept> additionalConcepts;
	protected final String repositoryId;
	protected final String branch;
	protected final String parentLockContext;

	public ClassifyOperation(final String reasonerId, 
			final String userId, 
			final List<SnomedConcept> additionalConcepts,
			final String repositoryId, 
			final String branch) {
		
		this(reasonerId, 
				userId, 
				additionalConcepts, 
				repositoryId, 
				branch, 
				DatastoreLockContextDescriptions.CLASSIFY_WITH_REVIEW);
	}
	
	public ClassifyOperation(final String reasonerId, 
			final String userId, 
			final List<SnomedConcept> additionalConcepts,
			final String repositoryId, 
			final String branch,
			final String parentLockContext) {	

		this.reasonerId = reasonerId;
		this.userId = userId;
		this.additionalConcepts = additionalConcepts;
		this.repositoryId = repositoryId;
		this.branch = branch;
		this.parentLockContext = parentLockContext;
	}

	/**
	 * Allocates a reasoner instance, performs the requested operation, then releases the borrowed instance back to the pool.
	 * @param monitor an {@link IProgressMonitor} to monitor operation progress
	 * @return the value returned by {@link #processResults(IProgressMonitor, long)}
	 * @throws OperationCanceledException
	 */
	public T run(final IProgressMonitor monitor) throws OperationCanceledException {

		monitor.beginTask("Classification in progress...", IProgressMonitor.UNKNOWN);

		try {

			final String classificationId = UUID.randomUUID().toString(); 
			final Notifications notifications = getServiceForClass(Notifications.class);
			final BlockingQueue<RemoteJobEntry> jobQueue = Queues.newArrayBlockingQueue(1); 
			final Observable<RemoteJobEntry> jobObservable = notifications.ofType(RemoteJobNotification.class)
					.filter(RemoteJobNotification::isChanged)
					.filter(notification -> notification.getJobIds().contains(classificationId))
					.concatMap(notification -> JobRequests.prepareSearch()
							.one()
							.filterById(classificationId)
							.buildAsync()
							.execute(getEventBus()))
					.map(RemoteJobs::first)
					.map(Optional<RemoteJobEntry>::get)
					.filter(RemoteJobEntry::isDone);
			
			// "One-shot" subscription; it should self-destruct after the first notification
			jobObservable.subscribe(new DisposableObserver<RemoteJobEntry>() {
				@Override 
				public void onComplete() { dispose(); }
				
				@Override 
				public void onError(final Throwable t) { dispose(); }
				
				@Override 
				public void onNext(final RemoteJobEntry job) {
					try {
						jobQueue.put(job);
					} catch (InterruptedException e) {
						throw new SnowowlRuntimeException("Interrupted while trying to add a remote job entry to the queue.", e);
					} finally {
						dispose();
					}
				}
			});

			ClassificationRequests.prepareCreateClassification()
				.setClassificationId(classificationId)
				.setReasonerId(reasonerId)
				.setUserId(userId)
				.addAllConcepts(additionalConcepts)
				.setParentLockContext(parentLockContext)
				.build(repositoryId, branch)
				.get();

			while (true) {

				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				try {
					
					final RemoteJobEntry jobEntry = jobQueue.poll(CHECK_JOB_INTERVAL_SECONDS, TimeUnit.SECONDS);
					if (jobEntry == null) {
						continue;
					}
					
					switch (jobEntry.getState()) {
						case SCHEDULED: //$FALL-THROUGH$
						case RUNNING:
						case CANCEL_REQUESTED:
							break;
						case FINISHED:
							try {
								return processResults(classificationId);
							} finally {
								deleteEntry(classificationId);
							}
						case CANCELED:
							deleteEntry(classificationId);
							throw new OperationCanceledException();
						case FAILED:
							deleteEntry(classificationId);
							throw new SnowowlRuntimeException("Failed to retrieve the results of the classification.");
						default:
							throw new IllegalStateException("Unexpected state '" + jobEntry.getState() + "'.");
					}

				} catch (final InterruptedException e) {
					// Nothing to do
				}
			}

		} finally {
			monitor.done();
		}
	}

	private void deleteEntry(final String classificationId) {
		JobRequests.prepareDelete(classificationId)
				.buildAsync()
				.execute(getEventBus());
	}

	protected IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

	/**
	 * Performs an arbitrary operation using the reasoner. Subclasses should implement this method to perform any operation on the
	 * results of a classification run.
	 * 
	 * @param classificationId the classification's unique identifier
	 * @return the extracted results of the classification
	 */
	protected abstract T processResults(String classificationId);
}
