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
package com.b2international.snowowl.core.jobs;

import static com.b2international.commons.StringUtils.EMPTY_STRING;
import static com.b2international.commons.collections.Collections3.forEach;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.commons.collections.Procedure;
import com.google.common.collect.Lists;

/**
 * Job for polling and periodically checking the state of an observed {@link IProgressMonitor monitor} instance.
 * Whenever the observed monitor is canceled then all listeners will be notified.
 * <p>Clients should always cancel this job after using it.
 * <blockquote><pre>
 *
 * static void m(final IProgressMonitor monitorToObserve) {
 *
 *   ProgressMonitorStateObserverJob observerJob = null;
 *   ProgressMonitorCanceledListener listener = createListener();
 *   
 *   try {
 *      observerJob = new ProgressMonitorStateObserverJob(monitorToObserve, listener);
 *      observerJob.schedule();
 *      doYourOwnLogic(monitorToObserve);
 *   } finally {
 *      if (null != observerJob) {
 *         observerJob.cancel();
 *      }
 *   }
 *
 * }
 * </pre></blockquote>
 *  
 *
 */
public class ProgressMonitorStateObserverJob extends Job {

	private final IProgressMonitor observedMonitor;
	private final Iterable<ProgressMonitorCanceledListener> listeners;
	
	public ProgressMonitorStateObserverJob(final IProgressMonitor observedMonitor, final Iterable<ProgressMonitorCanceledListener> listeners) {
		super(EMPTY_STRING);
		this.observedMonitor = checkNotNull(observedMonitor, "observedMonitor");
		this.listeners = checkNotNull(listeners, "listeners");
		setUser(false);
		setSystem(true);
	}
	
	public ProgressMonitorStateObserverJob(final IProgressMonitor observedMonitor, final ProgressMonitorCanceledListener listener, final ProgressMonitorCanceledListener... others) {
		this(observedMonitor, Lists.asList(listener, others));
	}
	
	@Override
	protected IStatus run(final IProgressMonitor originalMonitor) {
		
		while (!originalMonitor.isCanceled()) {

			try {
				Thread.sleep(200L);
			} catch (final InterruptedException e) {
				return Status.CANCEL_STATUS;
			}
			
			if (observedMonitor.isCanceled()) {
				fireMonitorCanceled(originalMonitor); 
				return Status.OK_STATUS;
			}
			
		}
		
		return Status.OK_STATUS;
	}

	private void fireMonitorCanceled(final IProgressMonitor originalMonitor) {
		forEach(listeners, new Procedure<ProgressMonitorCanceledListener>() {
			protected void doApply(final ProgressMonitorCanceledListener callback) {
				callback.notify(observedMonitor, originalMonitor);
			}
		});
	}

}