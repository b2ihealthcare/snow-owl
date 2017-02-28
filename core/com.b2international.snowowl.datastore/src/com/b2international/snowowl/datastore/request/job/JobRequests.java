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
package com.b2international.snowowl.datastore.request.job;

/**
 * @since 5.7
 */
public final class JobRequests {

	private JobRequests() {}
	
	public static ScheduleJobRequestBuilder prepareSchedule() {
		return new ScheduleJobRequestBuilder();
	}

	public static SearchJobRequestBuilder prepareSearch() {
		return new SearchJobRequestBuilder();
	}
	
	public static GetJobRequestBuilder prepareGet(String jobId) {
		return new GetJobRequestBuilder(jobId);
	}

	public static CancelJobRequestBuilder prepareCancel(String jobId) {
		return new CancelJobRequestBuilder(jobId);
	}

	public static DeleteJobRequestBuilder prepareDelete(String jobId) {
		return new DeleteJobRequestBuilder(jobId);
	}
	
}
