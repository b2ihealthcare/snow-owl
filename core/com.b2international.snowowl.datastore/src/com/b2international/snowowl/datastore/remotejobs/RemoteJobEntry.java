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
package com.b2international.snowowl.datastore.remotejobs;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Nullable;

import com.b2international.commons.beans.BeanPropertyChangeSupporter;
import com.b2international.index.Doc;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 */
@Doc(type = "job")
public class RemoteJobEntry extends BeanPropertyChangeSupporter implements Serializable {

	public static final String PROP_DESCRIPTION = "description";
	public static final String PROP_COMPLETION_LEVEL = "completionLevel";
	public static final String PROP_START_DATE = "startDate";
	public static final String PROP_STATE = "state";

	private static final long serialVersionUID = 1L;

	public static final int MIN_COMPLETION_LEVEL = 0;
	public static final int MAX_COMPLETION_LEVEL = 100;

	private final UUID id;
	
	private String description;
	private String requestingUserId;
	private Date scheduleDate;
	private @Nullable Date startDate;
	private @Nullable Date finishDate;
	private RemoteJobState state;
	private int completionLevel;
	private @Nullable String userCommandId;

	public RemoteJobEntry(final UUID id, final String description, final String requestingUserId) {
		this(id, description, requestingUserId, null);
	}
	
	public RemoteJobEntry(final UUID id, final String description, final String requestingUserId, final String userCommandId) {
		this(id, description, requestingUserId, new Date(), null, RemoteJobState.SCHEDULED, 0, userCommandId);
	}
	
	private RemoteJobEntry(final UUID id, final String description, final String requestingUserId, 
			final Date scheduleDate, 
			final @Nullable Date startDate, 
			final RemoteJobState state, 
			final int completionLevel, 
			final @Nullable String userCommandId) {
		
		Preconditions.checkNotNull(id, "Remote job identifier may not be null.");
		Preconditions.checkNotNull(description, "Description may not be null.");
		Preconditions.checkNotNull(requestingUserId, "Requesting user identifier not be null.");
		Preconditions.checkNotNull(scheduleDate, "Scheduling date may not be null.");
		Preconditions.checkNotNull(state, "Remote job state may not be null.");
		
		this.id = id;
		this.description = description;
		this.requestingUserId = requestingUserId;
		this.scheduleDate = scheduleDate;
		this.startDate = startDate;
		this.state = state;
		this.completionLevel = limitLevel(completionLevel);
		this.userCommandId = userCommandId;
	}

	private int limitLevel(final int completionLevel) {
		return Math.max(MIN_COMPLETION_LEVEL, Math.min(MAX_COMPLETION_LEVEL, completionLevel));
	}
	
	public UUID getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	
	public String getRequestingUserId() {
		return requestingUserId;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}
	
	public String getFormattedScheduleDate() {
		return getScheduleDate() == null ? "Unknown" : Dates.formatByHostTimeZone(getScheduleDate(), DateFormats.MEDIUM);
	}
	
	public @Nullable Date getStartDate() {
		return startDate;
	}
	
	public String getFormattedStartDate() {
		return getStartDate() == null ? "" : Dates.formatByHostTimeZone(getStartDate(), DateFormats.MEDIUM);
	}
	
	public @Nullable Date getFinishDate() {
		return finishDate;
	}
	
	public String getFormattedFinishDate() {
		return getFinishDate() == null ? "N/A" : Dates.formatByHostTimeZone(getFinishDate(), DateFormats.MEDIUM);
	}
	
	public RemoteJobState getState() {
		return state;
	}
	
	public int getCompletionPercent() {
		return completionLevel;
	}

	public String getUserCommandId() {
		return userCommandId;
	}

	public void setCompletionLevel(final int newCompletionLevel) {
		final int oldCompletionLevel = completionLevel;
		final int limitedNewCompletionLevel = limitLevel(newCompletionLevel);
		if (limitedNewCompletionLevel > completionLevel) {
			completionLevel = limitedNewCompletionLevel;
			firePropertyChange(PROP_COMPLETION_LEVEL, oldCompletionLevel, limitedNewCompletionLevel);
		}
	}
	
	public void setStartDate(final Date newStartDate) {
		if (dateSetFirst(startDate, newStartDate)) {
			final Date oldStartDate = startDate;
			startDate = newStartDate;
			firePropertyChange(PROP_START_DATE, oldStartDate, newStartDate);
		}
	}

	public void setState(final RemoteJobState newState) {
		if (newState.compareTo(state) > 0) {
			final RemoteJobState oldState = state;
			state = newState;
			firePropertyChange(PROP_STATE, oldState, newState);
		}
	}
	
	// XXX (apeteri): this setter does not broadcast notifications
	public void setFinishDate(final Date newFinishDate) {
		if (dateSetFirst(finishDate, newFinishDate)) {
			finishDate = newFinishDate;
		}
	}

	private boolean dateSetFirst(final Date oldStartDate, final Date newStartDate) {
		return null == oldStartDate && null != newStartDate;
	}

	public boolean isDone() {
		return getState().oneOf(RemoteJobState.FINISHED, RemoteJobState.FAILED);
	}
	
	/**
	 * Cancels this job entry by setting its state to CANCEL_REQUESTED and its finish date to now.
	 */
	public void cancel() {
		setState(RemoteJobState.CANCEL_REQUESTED);
		setFinishDate(new Date());
	}

	@Override
	public int hashCode() {
		return 31 + id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof RemoteJobEntry)) {
			return false;
		}
		
		final RemoteJobEntry other = (RemoteJobEntry) obj;
		return id.equals(other.id);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("description", description)
				.add("requestingUserId", requestingUserId)
				.add("scheduleDate", scheduleDate)
				.add("startDate", startDate)
				.add("finishDate", finishDate)
				.add("state", state)
				.add("completionLevel", completionLevel)
				.add("userCommandId", userCommandId)
				.toString();
	}

}