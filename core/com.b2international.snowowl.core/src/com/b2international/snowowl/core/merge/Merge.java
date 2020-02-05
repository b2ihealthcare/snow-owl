/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.merge;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import com.b2international.commons.exceptions.ApiError;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 4.6
 */
@JsonDeserialize(builder=Merge.Builder.class)
public final class Merge implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static enum Status {
		SCHEDULED,
		IN_PROGRESS,
		COMPLETED,
		CONFLICTS,
		FAILED, 
		CANCEL_REQUESTED;
	}
	
	private final String id;
	private final String source;
	private final String target;
	private final Status status;
	private final Date scheduledDate;
	private final Date startDate;
	private final Date endDate;
	private final ApiError apiError;
	private final Collection<MergeConflict> conflicts;
	
	public static Builder builder() {
		return new Builder();
	}

	Merge(String id, 
			String source, 
			String target, 
			Status status, 
			Date scheduledDate, 
			Date startDate,
			Date endDate, 
			ApiError apiError, 
			Collection<MergeConflict> conflicts) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.status = status;
		this.scheduledDate = scheduledDate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.apiError = apiError;
		this.conflicts = conflicts;
	}

	public String getId() {
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

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public ApiError getApiError() {
		return apiError;
	}

	public Collection<MergeConflict> getConflicts() {
		return conflicts;
	}
	
	public Merge start() {
		return new Merge(id, source, target, Status.IN_PROGRESS, scheduledDate, new Date(), endDate, apiError, conflicts);
	}

	public Merge completed() {
		return new Merge(id, source, target, Status.COMPLETED, scheduledDate, startDate, new Date(), apiError, conflicts);
	}

	public Merge failed(ApiError newApiError) {
		return new Merge(id, source, target, Status.FAILED, scheduledDate, startDate, new Date(), newApiError, conflicts);
	}

	public Merge failedWithConflicts(Collection<MergeConflict> newConflicts) {
		return new Merge(id, source, target, Status.CONFLICTS, scheduledDate, startDate, new Date(), apiError, newConflicts);
	}

	public Merge cancelRequested() {
		return new Merge(id, source, target, Status.CANCEL_REQUESTED, scheduledDate, startDate, endDate, apiError, conflicts);
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static final class Builder {

		private String id;
		private String source;
		private String target;
		private Status status = Status.SCHEDULED;
		private Date scheduledDate = new Date();
		private Date startDate;
		private Date endDate;
		private ApiError apiError;
		private Collection<MergeConflict> conflicts = newArrayList();

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder source(String source) {
			this.source = source;
			return this;
		}
		
		public Builder target(String target) {
			this.target = target;
			return this;
		}
		
		public Builder status(Status status) {
			this.status = status;
			return this;
		}

		public Builder scheduledDate(Date scheduledDate) {
			this.scheduledDate = scheduledDate;
			return this;
		}

		public Builder startDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder endDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}

		public Builder apiError(ApiError apiError) {
			this.apiError = apiError;
			return this;
		}

		public Builder conflicts(Collection<MergeConflict> conflicts) {
			this.conflicts.addAll(conflicts);
			return this;
		}

		public Merge build() {
			return new Merge(id == null ? UUID.randomUUID().toString() : id, source, target, status, scheduledDate, startDate, endDate, apiError, conflicts);
		}
	}
}
