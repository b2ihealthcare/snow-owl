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
package com.b2international.commons.async;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterators;

/**
 *         pool size of {@link AsyncTask}s can run in parallel (avoiding
 *         resource starvation)
 * @since 2.5
 */
public abstract class AsyncTask<Params, Result> extends Job {

	private static final Logger LOG = LoggerFactory.getLogger(AsyncTask.class);
	final static int poolSize = 25; // This is a magic number for the
									// serialization pool. It is not correlated
									// to the number of processors, as AsyncTask
									// is rather used for Network related
									// background work.
	private static final Iterator<ISchedulingRule> serializerPool;

	static {
		final List<ISchedulingRule> serializers = new ArrayList<ISchedulingRule>();
		for (int i = 0; i < poolSize; ++i) {
			serializers.add(new SerializationRule(i));
		}
		serializerPool = Iterators.cycle(serializers);
	}

	private IJobChangeListener changeListener = new JobChangeAdapter() {

		@Override
		public void done(final IJobChangeEvent event) {
			if (IStatus.CANCEL == event.getResult().getCode())
				return;
			if (event.getResult().isOK() && failure == null) {
				onResult(output);
			} else {
				onFailure(failure);
			}
		};

	};

	private Params[] input;
	private Result output;
	private Exception failure;
	private boolean executed = false;
	protected IProgressMonitor monitor;
	private ResultHandler<Result> resultHandler;

	public AsyncTask() {
		this("", false);
	}

	public AsyncTask(final String name) {
		this(name, false);
	}

	public AsyncTask(final String name, final boolean user) {
		super(name);
		addJobChangeListener(changeListener);
		setSystem(!user);
		setUser(user);
		synchronized (this) {
			setRule(serializerPool.next());
		}
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {
			final IProgressMonitor customMonitor = configureMonitor(monitor);
			this.monitor = customMonitor == null ? monitor : customMonitor;
			output = doInBackGround(input);
		} catch (final OperationCanceledException e) {
			LOG.warn("Async task cancelled: " + getName());
			return Status.CANCEL_STATUS;
		} catch (final Exception e) {
			failure = e;
		} finally {
			this.monitor.done();
		}
		// always returning ok status, error handled in a different way
		return Status.OK_STATUS;
	}

	/**
	 * Configures the given monitor.
	 * 
	 * @param monitor
	 * @return
	 */
	protected IProgressMonitor configureMonitor(final IProgressMonitor monitor) {
		return monitor;
	}

	public IProgressMonitor getMonitor() {
		return monitor;
	}

	/**
	 * Executes this asynchronous task.
	 * 
	 * @param input
	 */
	public void execute(final Params... input) {
		execute(null, input);
	}
	
	/**
	 * Executes this asynchronous task.
	 * 
	 * @param resultHandler
	 * @param input
	 */
	public void execute(final ResultHandler<Result> resultHandler, final Params...input) {
		checkState(!executed, "Asynchronous tasks can only executed once.");
		executed = true;
		this.input = input;
		this.resultHandler = resultHandler;
		schedule();
	}

	/**
	 * Returns <code>true</code> if the job is cancelled while it is performing
	 * the asynchronous operation.
	 * 
	 * @return
	 */
	protected boolean isCancelled() {
		return executed && monitor.isCanceled();
	}

	/**
	 * Performs a long running operation on the background thread.
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 *             - if doInBackground method fails to complete
	 */
	abstract protected Result doInBackGround(Params... input) throws Exception;

	/**
	 * When the asynchronous task successfully completes this method is to
	 * provide result. The call is made on the background thread.
	 * 
	 * @param result
	 */
	protected void onResult(final Result result) {
		if (resultHandler != null) {
			resultHandler.handle(result);
		}
	}

	/**
	 * Override if you want to handle failure during the asynchronous load.
	 * 
	 * @param exception
	 */
	protected void onFailure(final Exception exception) {
		LOG.error(String.format("'%s' failed to finish the task:", getClass().getName()), exception);
	}

	/**
	 * A special type of {@link ISchedulingRule}, which doesn't permit jobs to
	 * run parallel with the same {@link ISchedulingRule} instance. Create a
	 * pool of instances from this, and assign each job one of the pool objects.
	 * The scheduling of jobs receiving same rule instance are serialized by the
	 * job manager.
	 */
	private final static class SerializationRule implements ISchedulingRule {

		final int index;

		public SerializationRule(final int index) {
			this.index = index;
		}

		@Override
		public boolean contains(final ISchedulingRule rule) {
			return rule == this;
		}

		@Override
		public boolean isConflicting(final ISchedulingRule rule) {
			return rule == this;
		}

		@Override
		public String toString() {
			return "Serialization rule #" + (index + 1);
		}
	};

}