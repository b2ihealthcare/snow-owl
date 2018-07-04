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
package com.b2international.commons.concurrent.equinox;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CommonsActivator;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class ForkJoinUtils {

	public static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinUtils.class);
	
	private static final class ForkJoinJob extends Job {
		
		private final Runnable runnable;

		private ForkJoinJob(final Runnable runnable) {
			super("ForkJoinUtils-" + runnable);
			this.runnable = runnable;

			setPriority(Job.INTERACTIVE);
			setUser(false);
			setSystem(true);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			try {
				runnable.run();
				return Status.OK_STATUS;
			} catch (final Throwable e) {
				return new Status(IStatus.ERROR, CommonsActivator.PLUGIN_ID, "Error when executing runnable.", e);
			}
		}
	}

	private static final class CountDownAdapter extends JobChangeAdapter {
		
		private final CountDownLatch latch;
		private final IProgressMonitor progressMonitor;

		private CountDownAdapter(final CountDownLatch latch, final IProgressMonitor progressMonitor) {
			this.latch = latch;
			this.progressMonitor = progressMonitor;
		}

		@Override 
		public void done(final IJobChangeEvent event) {
			final IStatus result = event.getResult();
			
			if (IStatus.ERROR == result.getSeverity()) {
				LOGGER.error("Parallel task failed: {}", result.getMessage(), result.getException());
			}
			
			progressMonitor.worked(1);
			latch.countDown();
		}
	}

	public static void runInParallel(final Runnable firstRunnable, final Runnable... restRunnables) {
		runInParallel(Lists.asList(firstRunnable, restRunnables));
	}
	
	public static void runInParallel(final Collection<Runnable> runnables) {

		final Collection<Job> jobs = Collections2.transform(runnables, new Function<Runnable, Job>() {
			@Override public Job apply(final Runnable input) { return new ForkJoinJob(input); }
		});
		
		runJobsInParallel(jobs);
	}

	public static void runJobsInParallel(final Job firstJob, final Job... restJobs) {
		runJobsInParallel(Lists.asList(firstJob, restJobs));
	}

	public static void runJobsInParallel(final Collection<? extends Job> jobs) {
		runJobsInParallel(jobs, null);
	}
	
	public static void runJobsInParallel(final Collection<? extends Job> jobs, final IProgressMonitor progressMonitor) {

		final SubMonitor subMonitor = SubMonitor.convert(progressMonitor, jobs.size());
		final CountDownLatch latch = new CountDownLatch(jobs.size());
		
		for (final Job job : jobs) {
			job.addJobChangeListener(new CountDownAdapter(latch, subMonitor));
			job.schedule();
		}
		
		try {
			latch.await();
		} catch (final InterruptedException e) {
			throw new RuntimeException("Interrupted while waiting for parallel tasks to finish.", e);
		}
	}
	
	public static void runJobsInSequence(final Collection<? extends Job> jobs, final IProgressMonitor progressMonitor) {

		final SubMonitor subMonitor = SubMonitor.convert(progressMonitor, jobs.size());
		
		for (final Job job : jobs) {
			job.schedule();
			
			try {
				
				job.join();
				if (null != subMonitor) {
					subMonitor.worked(1);
				}
				
			} catch (final InterruptedException e) {
				throw new RuntimeException("Interrupted while waiting for parallel tasks to finish.", e);
			}
			
		}
		
	}
	
	public static void runJobsInParallelWithErrorHandling(final Collection<? extends Job> jobs, final IProgressMonitor progressMonitor) throws Exception {
		
		final SubMonitor subMonitor = SubMonitor.convert(progressMonitor, jobs.size());
		final CountDownLatch latch = new CountDownLatch(jobs.size());
		
		for (final Job job : jobs) {
			job.addJobChangeListener(new CountDownAdapter(latch, subMonitor));
			job.schedule();
		}
		
		try {
			latch.await();
			for (final Job job : jobs) {
				if (job.getResult().getSeverity() == IStatus.ERROR) {
					throw new RuntimeException(job.getResult().getMessage(), job.getResult().getException());
				}
			}
		} catch (final InterruptedException e) {
			throw new RuntimeException("Interrupted while waiting for parallel tasks to finish.", e);
		}
	}
}