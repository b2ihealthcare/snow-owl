/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationSettings;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;

/**
 * Represents an abstract operation which requires the classification of the SNOMED&nbsp;CT terminology on the active branch path.
 * 
 * @param T the return type of the custom {@link #processResults(IProgressMonitor, long)} method
 * 
 */
public abstract class ClassifyOperation<T> {

	private static final long CHECK_CANCEL_INTERVAL_MILLIS = 500L;

	protected final ClassificationSettings settings;

	public ClassifyOperation(ClassificationSettings settings) {
		this.settings = settings;
	}

	/**
	 * Allocates a reasoner instance, performs the requested operation, then releases the borrowed instance back to the pool.
	 * @param monitor an {@link IProgressMonitor} to monitor operation progress
	 * @return the value returned by {@link #processResults(IProgressMonitor, long)}
	 * @throws OperationCanceledException
	 */
	public T run(IProgressMonitor monitor) throws OperationCanceledException {

		monitor.beginTask("Classification in progress...", IProgressMonitor.UNKNOWN);

		try {

			String classificationId = getReasonerService().beginClassification(settings);

			while (true) {

				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				RemoteJobEntry jobEntry = JobRequests.prepareGet(classificationId)
						.buildAsync()
						.execute(getEventBus())
						.getSync();

				switch (jobEntry.getState()) {
				case SCHEDULED: //$FALL-THROUGH$
				case RUNNING:
					break;
				case FINISHED:
					return processResults(classificationId);
				case CANCELLED: //$FALL-THROUGH$
				case CANCEL_REQUESTED:
					throw new OperationCanceledException();
				case FAILED:
					throw new SnowowlRuntimeException("Failed to retrieve the results of the classification.");
				default:
					throw new IllegalStateException("Unexpected state '" + jobEntry.getState() + "'.");
				}

				try {
					Thread.sleep(CHECK_CANCEL_INTERVAL_MILLIS);
				} catch (InterruptedException e) {
					// Nothing to do
				}
			}

		} finally {
			monitor.done();
		}
	}

	protected IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

	protected SnomedReasonerService getReasonerService() {
		return ApplicationContext.getServiceForClass(SnomedReasonerService.class);
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
