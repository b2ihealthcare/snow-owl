/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.oplock;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

import com.b2international.index.Doc;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;

/**
 * @since 7.1.0
 */
@Doc(type = "lock")
@JsonDeserialize(builder=DatastoreLockEntry.Builder.class)
public class DatastoreLockEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final class Fields {
		public static final String ID = "id";
		public static final String USER_ID = "userId";
		public static final String DESCRIPTION = "description";
		public static final String LOCK_TARGET = "lockTarget";
	}
	
	public static class Expressions {
		public static Expression id(final String id) {
			return DocumentMapping.matchId(id);
		}
		
		public static Expression ids(final Collection<String> ids) {
			return com.b2international.index.query.Expressions.matchAny(DocumentMapping._ID, ids);
		}
		
		public static Expression userId(final String userId) {
			return com.b2international.index.query.Expressions.exactMatch(Fields.USER_ID, userId);
		}
		
		public static Expression description(final String description) {
			return com.b2international.index.query.Expressions.exactMatch(Fields.DESCRIPTION, description);
		}
		
		public static Expression lockTarget(final IOperationLockTarget lockTarget) {
			return com.b2international.index.query.Expressions.exactMatch(Fields.LOCK_TARGET, lockTarget.toString());
		}
		
	}
	 
	public static DatastoreLockEntry.Builder from(DatastoreLockEntry source) {
		return builder()
				.id(source.getId())
				.description(source.getDescription())
				.userId(source.getUserId())
				.lockTarget(source.getLockTarget());
	}
	
	public static DatastoreLockEntry.Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder
	public static class Builder {
		
		private String id;
		private String userId;
		private String description;
		private IOperationLockTarget lockTarget;
		private IOperationLock lock;
		
		@JsonCreator
		private Builder() {
		}
		
		public Builder id(final String id) {
			this.id = id;
			return this;
		}
		
		public Builder userId(final String userId) {
			this.userId = userId;
			return this;
		}
		
		public Builder description(final String description) {
			this.description = description;
			return this;
		}
		
		public Builder lockTarget(final IOperationLockTarget lockTarget) {
			this.lockTarget = lockTarget;
			return this;
		}
		
		public Builder lock(final IOperationLock lock) {
			this.lock = lock;
			return this;
		}
		
		public DatastoreLockEntry build() {
			return new DatastoreLockEntry(id, userId, description, lockTarget, lock);
		}
	}
	
	private final String id;
	private final String userId;
	private final String description;
	private final IOperationLockTarget lockTarget;
	private final IOperationLock lock;
	
	private DatastoreLockEntry(final String id, final String userId, final String description, final IOperationLockTarget lockTarget, IOperationLock lock) {
		this.id = id;
		this.userId = userId;
		this.description = description;
		this.lockTarget = lockTarget;
		this.lock = lock;
	}
	
	public String getId() {
		return id;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public IOperationLockTarget getLockTarget() {
		return lockTarget;
	}
	
	public IOperationLock getLock() {
		return lock;
	}
	
	@Override
	public int hashCode() {
		return 31 + id.hashCode() + description.hashCode() + lockTarget.hashCode() + lock.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (other == null) {
			return false;
		}
		
		if (!(other instanceof DatastoreLockEntry)) {
			return false;
		}
		
		final DatastoreLockEntry otherEntry = (DatastoreLockEntry) other;
		return Objects.equals(id, otherEntry.id) 
				&& Objects.equals(description, otherEntry.description)
				&& Objects.equals(lockTarget, otherEntry.lockTarget)
				&& Objects.equals(lock, otherEntry.lock);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("userId", userId)
				.add("description", description)
				.add("lockTarget", lockTarget)
				.toString();
	}
	
 
}
