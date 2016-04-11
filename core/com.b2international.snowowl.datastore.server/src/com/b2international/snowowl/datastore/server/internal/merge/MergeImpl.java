/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.internal.merge;

import java.util.Date;
import java.util.UUID;

import com.b2international.snowowl.core.exceptions.ApiError;
import com.b2international.snowowl.core.merge.Merge;

/**
 * @since 4.6
 */
public class MergeImpl implements Merge {

	private UUID id = UUID.randomUUID();
	private String source;
	private String target;
	private Status status = Status.SCHEDULED;
	private Date scheduledDate = new Date();
	private Date startDate;
	private Date endDate;
	private ApiError apiError;

	public MergeImpl(String source, String target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Date getScheduledDate() {
		return scheduledDate;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public ApiError getApiError() {
		return apiError;
	}
	
	public void start() {
		status = Status.IN_PROGRESS;
		startDate = new Date();
	}
	
	public void completed() {
		status = Status.COMPLETED;
		endDate = new Date();
	}
	
	public void failed(ApiError error) {
		status = Status.FAILED;
		endDate = new Date();
		apiError = error;
	}
	
	public void cancelRequested() {
		status = Status.CANCEL_REQUESTED;
	}
}
