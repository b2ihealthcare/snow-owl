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
package com.b2international.snowowl.snomed.reasoner.classification.operation;

import static com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils.getJobSpecificAddress;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.remotejobs.IRemoteJobManager;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEventBusHandler;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationRequest;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Represents an abstract operation which requires the classification of the SNOMED&nbsp;CT terminology on the active branch path.
 * 
 * @param T the return type of the custom {@link #processResults(IProgressMonitor, long)} method
 * 
 */
public abstract class ClassifyOperation<T> {

	private static final long CHECK_CANCEL_INTERVAL_MILLIS = 500L;
	
	protected final ClassificationRequest classificationRequest;

	public ClassifyOperation(final ClassificationRequest classificationRequest) {
		this.classificationRequest = classificationRequest;
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
			
			final SettableFuture<T> result = SettableFuture.create();
			final UUID classificationId = classificationRequest.getClassificationId();
			
			getEventBus().registerHandler(getJobSpecificAddress(IRemoteJobManager.ADDRESS_REMOTE_JOB_COMPLETED, classificationId), new RemoteJobEventBusHandler(classificationId) {
				@Override
				protected void handleResult(final UUID jobId, final boolean cancelRequested) {
					if (cancelRequested) {
						result.setException(new OperationCanceledException());
					} else {
						
						try {
							result.set(processResults(jobId));
						} catch (final RuntimeException e) {
							result.setException(e);
						}
						
						getRemoteJobManager().cancelRemoteJob(jobId);
					}
				}
			});
			
			getReasonerService().beginClassification(classificationRequest);
			
			try {
				while (true) {
					try {
						return result.get(CHECK_CANCEL_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
					} catch (final InterruptedException | TimeoutException e) {
						if (monitor.isCanceled()) {
							throw new OperationCanceledException();
						}
					}
				}
			} catch (final ExecutionException e) {
				throw new SnowowlRuntimeException("Failed to retrieve the results of the classification.", e);
			}
			
		} finally {
			monitor.done();
		}
	}

	private IEventBus getEventBus() {
		return getApplicationContext().getService(IEventBus.class);
	}

	private IRemoteJobManager getRemoteJobManager() {
		return getApplicationContext().getService(IRemoteJobManager.class);
	}

	protected SnomedReasonerService getReasonerService() {
		return getApplicationContext().getService(SnomedReasonerService.class);
	}

	private ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}

	/**
	 * Performs an arbitrary operation using the reasoner. Subclasses should implement this method to perform any operation on the
	 * results of a classification run.
	 * 
	 * @param classificationId the classification's unique identifier
	 * @return the extracted results of the classification
	 */
	protected abstract T processResults(final UUID classificationId);
}