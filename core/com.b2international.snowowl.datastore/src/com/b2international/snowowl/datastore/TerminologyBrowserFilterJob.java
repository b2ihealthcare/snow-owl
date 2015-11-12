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
package com.b2international.snowowl.datastore;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.EmptyTerminologyBrowser;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.browser.FilterTerminologyBrowserType;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Job for creating a filtered terminology browser instance based on a previously performed component search.
 * @param <K> - type of the unique identifier of the terminology independent component.
 * @param <C> - type of the terminology independent components.
 * @see IFilterJobCallback
 * @see FilterJobState
 */
public abstract class TerminologyBrowserFilterJob<K, C extends IComponent<K>> extends Job {
	
	/**
	 * Callback to notify receivers that search result count exceeded the threshold limit.
	 */
	public static interface IFilterJobCallback {
		/**
		 * Indicates the client that search result has exceeded the threshold limit.
		 */
		void indicateResultsOverThreashold();
	}
	
	/**
	 * Enumeration for indicating the current state of the {@link TerminologyBrowserFilterJob terminology browser job}.
	 * The following types are available:
	 * <p>
	 * <ul>
	 *   <li>{@link FilterJobState#NONE <em>NONE</em>}</li>
	 *   <li>{@link FilterJobState#EMPTY_SEARCH_TERM <em>EMPTY_SEARCH_TERM</em>}</li>
	 *   <li>{@link FilterJobState#NO_RESULT <em>NO_RESULT</em>}</li>
	 *   <li>{@link FilterJobState#OK <em>OK</em>}</li>
	 * </ul>
	 * </p>
	 */
	public static enum FilterJobState {
		
		/**
		 * The job has no state. Has not been scheduled.
		 * @see FilterJobState
		 */
		NONE,
		/**
		 * The job has been successfully finished. The filtered terminology browser has been initialized.
		 * @see FilterJobState
		 */
		OK,
		/**
		 * The job has been finished. Empty or {@code null} query term has been specified.
		 * @see FilterJobState
		 */
		EMPTY_SEARCH_TERM,
		/**
		 * The job has been finished. No matching component has been found for the specified query term.
		 * @see FilterJobState
		 */
		NO_RESULT;
	}
	
	/**
	 * Threshold constant. Clients may hook up some business logic that will be invoked whenever the 
	 * search and hierarchy building time is greater than this in milliseconds.
	 * <br>Value: {@value}.
	 * @see #getSearchResultThreashold()
	 */
	private static final long SEARCH_RESULT_THRESHOLD = 300L; //XXX Madness? THIS IS SPARTA!

	/**
	 * Delay before scheduling the current filter job.
	 * <br>Default value in millisecond: {@value}.
	 * @see Job#schedule(long)
	 * @see #getDelay()
	 */
	private static final long SCHEDULE_DELAY = 300L; //XXX Madness? THIS IS SPARTA!

	/**
	 * The unique identifier of the terminology component.
	 */
	private final String terminologyComponentId;

	/**
	 * A variable representing the number of matching items
	 */
	private int count;
	
	/**
	 * The query string.
	 */
	private String searchString;

	/**
	 * The state of the job. {@link FilterJobState#NONE NONE} by default.
	 */
	private FilterJobState state = FilterJobState.NONE;
	
	/**
	 * The encapsulated filtered terminology browser instance.
	 */
	private IFilterClientTerminologyBrowser<C, K> filteredBrowser = EmptyTerminologyBrowser.<C, K>getInstance();

	/**
	 * Set of callbacks listening for notifications if the number of matching search results exceeds a specified threshold level. 
	 */
	private final Set<IFilterJobCallback> callbacks;

	private IClientTerminologyBrowser<C, K> terminologyBrowser;
	
	/**
	 * Creates a new terminology browser filter job.
	 * @param terminologyComponentId the unique identifier of the terminology component.
	 * @param jobName the name of the job.
	 * @param callbacks the callbacks for the filter job.
	 */
	protected TerminologyBrowserFilterJob(final String terminologyComponentId, final String jobName, final IFilterJobCallback... callbacks) {
		super(Preconditions.checkNotNull(jobName, "Job name argument cannot be null."));
		this.terminologyComponentId = Preconditions.checkNotNull(terminologyComponentId, "Terminology component ID argument cannot be null.");
		this.callbacks = Sets.newHashSet(callbacks);
	}

	/**
	 * Returns with a filter result count representing as the status of the outcome of this job execution.
	 * @see #count
	 * @return the filter job status.
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * Schedules this job instance to run with a specified query string.
	 * @param searchString the query string for the filter job.
	 */
	public void schedule(final String searchString) {
		this.searchString = searchString;
		schedule(getDelay());
	}

	/**
	 * Returns with the filtered terminology browser instance as the outcome of this job instance execution.
	 * @return the filtered terminology browser instance.
	 */
	public IFilterClientTerminologyBrowser<C, K> getFilteredBrowser() {
		return filteredBrowser;
	}
	
	/**
	 * Returns with the current {@link FilterJobState state} of this filter job instance.
	 * @return the filter job's current state.
	 * @see FilterJobState 
	 */
	public FilterJobState getJobState() {
		return state;
	}

	public void setTerminologyBrowser(IClientTerminologyBrowser<C, K> terminologyBrowser) {
		this.terminologyBrowser = terminologyBrowser;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		
		if (StringUtils.isEmpty(searchString)) {
			state = FilterJobState.EMPTY_SEARCH_TERM;
			return Status.OK_STATUS;
		}
		
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}

		//job to trigger some UI feedback if the search and hierarchy building did not finish in 300 ms
		Job timerJob = new Job("Building hierarchy...") {
			@Override protected IStatus run(final IProgressMonitor monitor) {
				for (final IFilterJobCallback callback : callbacks) {
					callback.indicateResultsOverThreashold();
				}
				return Status.OK_STATUS;
			}
		};
		timerJob.schedule(getSearchResultThreashold());
		
		filteredBrowser = getTerminologyBrowser().filterTerminologyBrowser(searchString, monitor);
		
		//we can cancel the job here as this is waiting
		timerJob.cancel();
		
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		
		count = filteredBrowser.size();
		
		if (isEmpty(filteredBrowser)) {
			filteredBrowser = EmptyTerminologyBrowser.<C, K>getInstance();
			state = FilterJobState.NO_RESULT;
			return Status.OK_STATUS;
		}

		state = FilterJobState.OK;
		
		return Status.OK_STATUS;
	}

	protected boolean isEmpty(IFilterClientTerminologyBrowser<C, K> filteredTerminologyBrowser) {
		return filteredTerminologyBrowser.size() < 1;
	}
	
	/**
	 * Returns with the delay before scheduling the current job instance.
	 * <p>Clients may specify some different value.
	 * @see #SCHEDULE_DELAY
	 * @return the delay.
	 */
	protected long getDelay() {
		return SCHEDULE_DELAY;
	}

	/**
	 * Returns with the {@link #SEARCH_RESULT_THRESHOLD threshold} associated with the current filter job.
	 * <p>Clients may override to specify other value.
	 * @see #SEARCH_RESULT_THRESHOLD
	 * @return the threshold.
	 */
	protected long getSearchResultThreashold() {
		return SEARCH_RESULT_THRESHOLD;
	}

	/**
	 * Returns with the type of the terminology browser. Can be either {@link FilterTerminologyBrowserType#HIERARCHICAL hierarchical} or {@link FilterTerminologyBrowserType#FLAT flat}.
	 * <p>By default this method always returns with {@link FilterTerminologyBrowserType#HIERARCHICAL hierarchical} type. Clients may override this method.
	 * @return
	 */
	protected FilterTerminologyBrowserType getTerminologyBrowserType() {
		return FilterTerminologyBrowserType.HIERARCHICAL;
	}

	/**
	 * Returns with the component hierarchy browser service. This service contains all component.
	 * @return the service for browsing the component hierarchy. 
	 */
	@SuppressWarnings("unchecked")
	protected IClientTerminologyBrowser<C, K> getTerminologyBrowser() {
		if (terminologyBrowser == null) {
			terminologyBrowser = (IClientTerminologyBrowser<C, K>) CoreTerminologyBroker.getInstance().getTerminologyBrowserFactory(terminologyComponentId).getTerminologyBrowser();
		}
		return terminologyBrowser;
	}
}