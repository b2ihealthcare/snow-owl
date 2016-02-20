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
package com.b2international.snowowl.core.domain;

import java.util.Date;
import java.util.UUID;

import com.b2international.snowowl.core.exceptions.ApiError;
import com.google.common.base.Objects;

/**
 * @since 4.6
 */
public class MergeQueueEntry {

	public enum Status {
		SCHEDULED,
		IN_PROGRESS,
		COMPLETED,
		FAILED, 
		CANCEL_REQUESTED;
	}
	
	private final UUID id;
	private final String source;
	private final String target;
	
	private Status status;
	private Date scheduledDate;
	private Date startDate;
	private Date endDate;
	
	private ApiError apiError;
	
	public MergeQueueEntry(UUID id, String source, String target) {
		this.id = id;
		this.source = source;
		this.target = target;
	}

	public UUID getId() {
		return id;
	}
	
	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Date getScheduledDate() {
		return scheduledDate;
	}
	
	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public ApiError getApiError() {
		return apiError;
	}

	public void setApiError(ApiError apiError) {
		this.apiError = apiError;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("source", source)
				.add("target", target)
				.add("status", status)
				.add("scheduledDate", scheduledDate)
				.add("startDate", startDate)
				.add("endDate", endDate)
				.add("apiError", apiError)
				.toString();
	}
}
