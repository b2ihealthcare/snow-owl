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
package com.b2international.snowowl.core.merge;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import com.b2international.snowowl.core.exceptions.ApiError;

/**
 * @since 4.6
 */
public class MergeImpl implements Merge {

	private final UUID id;
	private final String source;
	private final String target;
	private final Status status;
	private final Date scheduledDate;
	private final Date startDate;
	private final Date endDate;
	private final ApiError apiError;
	private final Collection<MergeConflict> conflicts;
	
	public static Builder builder(String source, String target) {
		return new Builder(source, target);
	}

	MergeImpl(UUID id, String source, String target, Status status, 
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

	@Override
	public Collection<MergeConflict> getConflicts() {
		return conflicts;
	}

	@Override
	public Merge start() {
		return new MergeImpl(id, source, target, Status.IN_PROGRESS, scheduledDate, new Date(), endDate, apiError, conflicts);
	}

	@Override
	public Merge completed() {
		return new MergeImpl(id, source, target, Status.COMPLETED, scheduledDate, startDate, new Date(), apiError, conflicts);
	}

	@Override
	public Merge failed(ApiError newApiError) {
		return new MergeImpl(id, source, target, Status.FAILED, scheduledDate, startDate, new Date(), newApiError, conflicts);
	}

	@Override
	public Merge failedWithConflicts(Collection<MergeConflict> newConflicts, ApiError newApiError) {
		return new MergeImpl(id, source, target, Status.CONFLICTS, scheduledDate, startDate, new Date(), newApiError, newConflicts);
	}

	@Override
	public Merge cancelRequested() {
		return new MergeImpl(id, source, target, Status.CANCEL_REQUESTED, scheduledDate, startDate, endDate, apiError, conflicts);
	}

}
