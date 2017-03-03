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

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.match;
import static com.b2international.index.query.Expressions.matchAny;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.ClassUtils;
import com.b2international.index.Doc;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

@Doc(type = "job")
@JsonDeserialize(builder=RemoteJobEntry.Builder.class)
public final class RemoteJobEntry implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Set<RemoteJobState> DONE_STATES = ImmutableSet.of(RemoteJobState.FINISHED, RemoteJobState.FAILED, RemoteJobState.CANCELED);

	public static final int MIN_COMPLETION_LEVEL = 0;
	public static final int MAX_COMPLETION_LEVEL = 100;

	public static class Fields {
		public static final String ID = "id";
		public static final String DELETED = "deleted";
		public static final String USER = "user";
		public static final String STATE = "state";
	}
	
	public static class Expressions {
		public static Expression id(String id) {
			return DocumentMapping.matchId(id);
		}

		public static Expression ids(Collection<String> ids) {
			return matchAny(DocumentMapping._ID, ids);
		}
		
		public static Expression deleted(boolean deleted) {
			return match(Fields.DELETED, deleted);
		}
		
		public static Expression user(String user) {
			return exactMatch(Fields.USER, user);
		}

		public static Expression done() {
			return matchAny(Fields.STATE, DONE_STATES.stream().map(Enum::name).collect(Collectors.toSet()));
		}
		
	}
	
	public static RemoteJobEntry.Builder from(RemoteJobEntry from) {
		return builder()
				.id(from.getId())
				.description(from.getDescription())
				.user(from.getUser())
				.scheduleDate(from.getScheduleDate())
				.startDate(from.getStartDate())
				.finishDate(from.getFinishDate())
				.state(from.getState())
				.completionLevel(from.getCompletionLevel())
				.parameters(from.getParameters())
				.result(from.getResult())
				.deleted(from.isDeleted());
	}
	
	public static RemoteJobEntry.Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {

		private String id;
		private String description;
		private String user;
		private Date scheduleDate;
		private Date startDate;
		private Date finishDate;
		private RemoteJobState state = RemoteJobState.SCHEDULED;
		private int completionLevel = MIN_COMPLETION_LEVEL;
		private boolean deleted;
		private Map<String, Object> parameters;
		private Object result;
		
		@JsonCreator
		private Builder() {
		}
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder user(String user) {
			this.user = user;
			return this;
		}
		
		public Builder scheduleDate(Date scheduleDate) {
			this.scheduleDate = scheduleDate;
			return this; 
		}
		
		public Builder startDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}
		
		public Builder finishDate(Date finishDate) {
			this.finishDate = finishDate;
			return this;
		}
		
		public Builder state(RemoteJobState state) {
			this.state = state;
			return this;
		}
		
		public Builder completionLevel(int completionLevel) {
			this.completionLevel = completionLevel;
			return this;
		}

		public Builder deleted(boolean deleted) {
			this.deleted = deleted;
			return this;
		}
		
		public Builder result(Object result) {
			this.result = result;
			return this;
		}
		
		public Builder parameters(Map<String, Object> parameters) {
			this.parameters = parameters;
			return this;
		}
		
		public RemoteJobEntry build() {
			return new RemoteJobEntry(id, description, user, scheduleDate, startDate, finishDate, state, completionLevel, deleted, result, parameters);
		}
		
	}
	

	private final String id;
	private final String description;
	private final String user;
	private final Date scheduleDate;
	private final Date startDate;
	private final Date finishDate;
	private final RemoteJobState state;
	private final int completionLevel;
	private final Object result;
	private final boolean deleted;
	private final Map<String, Object> parameters;

	private RemoteJobEntry(
			final String id, 
			final String description, 
			final String user, 
			final Date scheduleDate, 
			final Date startDate,
			final Date finishDate, 
			final RemoteJobState state, 
			final int completionLevel,
			final boolean deleted,
			final Object result,
			final Map<String, Object> parameters) {
		
		Preconditions.checkNotNull(id, "Remote job identifier may not be null.");
		Preconditions.checkNotNull(description, "Description may not be null.");
		Preconditions.checkNotNull(user, "Requesting user identifier not be null.");
		Preconditions.checkNotNull(scheduleDate, "Scheduling date may not be null.");
		Preconditions.checkNotNull(state, "Remote job state may not be null.");
		
		this.id = id;
		this.description = description;
		this.user = user;
		this.scheduleDate = scheduleDate;
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.state = state;
		this.completionLevel = completionLevel;
		this.deleted = deleted;
		this.result = result;
		this.parameters = parameters;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	
	public String getUser() {
		return user;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getFinishDate() {
		return finishDate;
	}
	
	public RemoteJobState getState() {
		return state;
	}
	
	public int getCompletionLevel() {
		return completionLevel;
	}

	public boolean isDeleted() {
		return deleted;
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	public Object getResult() {
		return result;
	}
	
	public <T> T getResultAs(Class<T> type) {
		return ClassUtils.checkAndCast(result, type);
	}
	
	// Frequently used domain specific methods

	@JsonIgnore
	public boolean isDone() {
		return DONE_STATES.contains(getState());
	}
	
	@JsonIgnore
	public boolean isCancelled() {
		return getState().oneOf(RemoteJobState.CANCELED, RemoteJobState.CANCEL_REQUESTED);
	}
	
//	public String getFormattedScheduleDate() {
//		return getScheduleDate() == null ? "Unknown" : Dates.formatByHostTimeZone(getScheduleDate(), DateFormats.MEDIUM);
//	}
	
//	public String getFormattedStartDate() {
//		return getStartDate() == null ? "" : Dates.formatByHostTimeZone(getStartDate(), DateFormats.MEDIUM);
//	}
	
//	public String getFormattedFinishDate() {
//		return getFinishDate() == null ? "N/A" : Dates.formatByHostTimeZone(getFinishDate(), DateFormats.MEDIUM);
//	}

//	@JsonIgnore
//	public void setCompletionPercent(final int newCompletionPercent) {
//		final int limitedNewCompletionLevel = limitLevel(newCompletionPercent);
//		if (limitedNewCompletionLevel > completionLevel) {
//			completionLevel = limitedNewCompletionLevel;
//		}
//	}
	
//	public void setStartDate(final Date newStartDate) {
//		this.startDate = newStartDate;
//	}

//	public void setState(final RemoteJobState newState) {
//		this.state = newState;
//	}
	
	// XXX (apeteri): this setter does not broadcast notifications
//	public void setFinishDate(final Date newFinishDate) {
//		if (dateSetFirst(finishDate, newFinishDate)) {
//			finishDate = newFinishDate;
//		}
//	}

//	private boolean dateSetFirst(final Date oldStartDate, final Date newStartDate) {
//		return null == oldStartDate && null != newStartDate;
//	}
	
	/**
	 * Cancels this job entry by setting its state to CANCEL_REQUESTED and its finish date to now.
	 */
//	public void cancel() {
//		setState(RemoteJobState.CANCEL_REQUESTED);
//		setFinishDate(new Date());
//	}
	
//	private int limitLevel(final int completionLevel) {
//		return Math.max(MIN_COMPLETION_LEVEL, Math.min(MAX_COMPLETION_LEVEL, completionLevel));
//	}

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
				.add("user", user)
				.add("scheduleDate", scheduleDate)
				.add("startDate", startDate)
				.add("finishDate", finishDate)
				.add("state", state)
				.add("deleted", deleted)
				.add("completionLevel", completionLevel)
				.add("result", result)
				.add("parameters", parameters)
				.toString();
	}

}