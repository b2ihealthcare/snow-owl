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
package com.b2international.snowowl.datastore.request.job;

import java.util.Collection;
import java.util.Collections;

import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;

/**
 * The central class for managing and manipulating long running operations aka {@link RemoteJobEntry}s.
 * 
 * @since 5.7
 */
public final class JobRequests {

	private JobRequests() {}
	
	/**
	 * Returns a job scheduling request builder to schedule a new remote job.
	 * @return {@link ScheduleJobRequestBuilder}
	 */
	public static ScheduleJobRequestBuilder prepareSchedule() {
		return new ScheduleJobRequestBuilder();
	}

	/**
	 * Returns a request builder to search for remote jobs.
	 * @return {@link SearchJobRequestBuilder}
	 */
	public static SearchJobRequestBuilder prepareSearch() {
		return new SearchJobRequestBuilder();
	}
	
	/**
	 * Returns a request builder to get a single remote job by its identifier.
	 * @param jobId - the identifier of the job
	 * @return {@link GetJobRequestBuilder}
	 */
	public static GetJobRequestBuilder prepareGet(String jobId) {
		return new GetJobRequestBuilder(jobId);
	}

	/**
	 * Returns a request builder to cancel a running remote job.
	 * @param jobId - the identifier of the job to be cancelled
	 * @return {@link CancelJobRequestBuilder}
	 */
	public static CancelJobRequestBuilder prepareCancel(String jobId) {
		return new CancelJobRequestBuilder(jobId);
	}

	/**
	 * Returns a request builder to delete a remote job. If the remote job is currently in RUNNING state, it will be cancelled and deleted.
	 * @param jobId - the identifier of the job to be cancelled and deleted
	 * @return {@link DeleteJobRequestBuilder}
	 */
	public static DeleteJobRequestBuilder prepareDelete(String jobId) {
		return new DeleteJobRequestBuilder(Collections.singleton(jobId));
	}
	
	public static DeleteJobRequestBuilder prepareDelete(Collection<String> jobIds) {
		return new DeleteJobRequestBuilder(jobIds);
	}
	
}
