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
package com.b2international.snowowl.datastore.server.index;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.cdo.CDOObject;
import org.slf4j.Logger;

import com.b2international.commons.CancelableProgressMonitorWrapper;
import com.b2international.commons.LogProgressMonitorWrapper;
import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexMappingStrategy;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;

/**
 * {@link AbstractReIndexJob} to reIndex element on terminologies.
 * 
 * @param T
 *            - the persisted {@link CDOObject} element type for the terminology
 * @param E
 *            - the {@link IIndexEntry} type for the terminology
 * @since 2.7
 */
public abstract class AbstractReIndexJob<T extends CDOObject, E extends IIndexEntry> extends Job {

	private final IBranchPath branchPath = BranchPathUtils.createMainPath();

	public AbstractReIndexJob(final String name) {
		super(name);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {
			getLogger().info(String.format("%s has started.", getName()));
			final Stopwatch watch = Stopwatch.createStarted();
			final IStatus result = reIndex(createSubMonitor(monitor, getLogger()));
			getLogger().info(String.format("%s has finished in %s.", getName(), TimeUtil.toString(watch)));
			return result;
		} catch (final OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		} catch (final SnowowlServiceException e) {
			return new Status(Status.ERROR, getPluginId(), String.format("Caught exception while reIndexing %s",
					getIndexName()), e);
		} finally {
			monitor.done();
		}
	}

	/**
	 * Returns the {@link IBranchPath}.
	 * 
	 * @return
	 */
	public IBranchPath getBranchPath() {
		return branchPath;
	}

	protected abstract String getIndexName();

	protected abstract String getPluginId();

	protected abstract Logger getLogger();

	/**
	 * Do the reindex job within this method.
	 * 
	 * @param monitor
	 * @return
	 * @throws SnowowlServiceException
	 */
	protected IStatus reIndex(final SubMonitor monitor) throws SnowowlServiceException {
		CDOEditingContext context = null;
		try {
			context = createEditingContext();
			doReIndex(context, getIndexUpdater(), monitor);
		} finally {
			if (context != null) {
				context.close();
			}
		}
		// create editing context
		return Status.OK_STATUS;
	}

	protected void doReIndex(final CDOEditingContext context, final IIndexUpdater<E> indexUpdater, final SubMonitor monitor) {
		cleanIndex(context, indexUpdater, monitor.newChild(15));
		reIndex(context, indexUpdater, monitor.newChild(80));
		postProcess(indexUpdater, monitor.newChild(5));
	}
	
	protected void reIndex(final CDOEditingContext context, final IIndexUpdater<E> indexUpdater, final SubMonitor monitor) {
		monitor.setWorkRemaining(getTargetClasses().length);
		for (final Class<? extends T> targetClass : getTargetClasses()) {			
			reIndex(context, indexUpdater, targetClass, monitor.newChild(1));
		}
	}
	
	protected abstract Class<? extends T>[] getTargetClasses();

	/**
	 * Reindexes each desired target component in the containment list of the given {@link CDOEditingContext}'s
	 * rootResource by using the given {@link IIndexUpdater} with {@link #getMappingStrategy(CDOObject)}.
	 * 
	 * @param context
	 * @param indexUpdater
	 * @param monitor
	 */
	protected void reIndex(final CDOEditingContext context, final IIndexUpdater<E> indexUpdater, final Class<? extends T> targetClass, final SubMonitor monitor) {
		
		final Iterable<? extends T> filteredContents = Iterables.filter(context.getContents(), targetClass);
		monitor.setWorkRemaining(Iterables.size(filteredContents));
		
		for (final T component : filteredContents) {
			reIndex(component, indexUpdater);
			monitor.worked(1);
		}
		monitor.done();
	}

	protected void reIndex(final T component, final IIndexUpdater<E> indexUpdater) {
		indexUpdater.index(branchPath, getMappingStrategy(component));
	}

	/**
	 * Executes post process steps at the end of the reIndex task.
	 * 
	 * @param indexUpdater
	 * @param monitor
	 */
	protected void postProcess(final IIndexUpdater<E> indexUpdater, final SubMonitor monitor) {
		monitor.setWorkRemaining(1);
		indexUpdater.commit(branchPath);
		monitor.done();
	}

	/**
	 * Cleans the actual index content using the given {@link IIndexUpdater}.
	 * 
	 * @param context
	 * @param indexUpdater
	 * @param monitor
	 */
	protected void cleanIndex(final CDOEditingContext context, final IIndexUpdater<E> indexUpdater, final SubMonitor monitor) {
		monitor.setWorkRemaining(1);
		indexUpdater.deleteAll(branchPath);
		monitor.worked(1);
		monitor.done();
	}

	/**
	 * Create new EditingContext based on which terminology you want to reIndex.
	 * 
	 * @return
	 * @throws SnowowlServiceException
	 */
	protected abstract CDOEditingContext createEditingContext() throws SnowowlServiceException;

	/**
	 * Returns the {@link IIndexUpdater} implementation for this {@link AbstractReIndexJob}.
	 * 
	 * @return
	 */
	protected abstract IIndexUpdater<E> getIndexUpdater();

	/**
	 * Returns an {@link IIndexMappingStrategy} for the given component.
	 * 
	 * @return
	 */
	protected abstract IIndexMappingStrategy getMappingStrategy(T component);

	/**
	 * Converts the given monitor to log progress into the given {@link Logger} instance.
	 * 
	 * @param monitor
	 * @return
	 */
	private SubMonitor createSubMonitor(final IProgressMonitor monitor, final Logger logger) {
		final IProgressMonitor loggingMonitor = new LogProgressMonitorWrapper(monitor, logger);
		final IProgressMonitor cancelableMonitor = new CancelableProgressMonitorWrapper(loggingMonitor);
		final SubMonitor subMonitor = SubMonitor.convert(cancelableMonitor, getName(), 100);
		return subMonitor;
	}

}