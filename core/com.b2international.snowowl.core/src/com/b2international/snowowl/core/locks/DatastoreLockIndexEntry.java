/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.locks;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchAny;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

import com.b2international.index.Doc;
import com.b2international.index.ID;
import com.b2international.index.query.Expression;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * @since 7.1.0
 */
@Doc(type = "lock")
@JsonDeserialize(builder=DatastoreLockIndexEntry.Builder.class)
public final class DatastoreLockIndexEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final class Fields {
		public static final String ID = "id";
		public static final String USER_ID = "userId";
		public static final String DESCRIPTION = "description";
		public static final String PARENT_DESCRIPTION = "parentDescription";
		public static final String REPOSITORY_ID= "repositoryId";
		public static final String BRANCHPATH = "branchPath";
	}
	
	public static class Expressions {

		public static Expression id(final String id) {
			return exactMatch(Fields.ID, id);
		}
		
		public static Expression ids(final Collection<String> ids) {
			return matchAny(Fields.ID, ids);
		}
		
		public static Expression userId(final String userId) {
			return exactMatch(Fields.USER_ID, userId);
		}
		
		public static Expression description(final String description) {
			return exactMatch(Fields.DESCRIPTION, description);
		}
		
		public static Expression parentDescription(final String parentDescription) {
			return exactMatch(Fields.PARENT_DESCRIPTION, parentDescription);
		}
		
		public static Expression repositoryId(final String repositoryId) {
			return exactMatch(Fields.REPOSITORY_ID, repositoryId);
		}
		
		public static Expression branchPath(final String branchPath) {
			return exactMatch(Fields.BRANCHPATH, branchPath);
		}
		
	}
	 
	public static DatastoreLockIndexEntry.Builder from(DatastoreLockIndexEntry source) {
		return builder()
				.id(source.getId())
				.userId(source.getUserId())
				.description(source.getDescription())
				.parentDescription(source.getParentDescription())
				.repositoryId(source.getRepositoryId())
				.branchPath(source.getBranchPath());
	}
	
	public static DatastoreLockIndexEntry.Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {
		
		@ID
		private String id;
		private String userId;
		private String description;
		private String parentDescription;
		private String repositoryId;
		private String branchPath;
		
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
		
		public Builder parentDescription(final String parentDescription) {
			this.parentDescription = parentDescription;
			return this;
		}
		
		public Builder description(final String description) {
			this.description = description;
			return this;
		}
		
		public Builder repositoryId(final String repositoryId) {
			Preconditions.checkNotNull(repositoryId);
			this.repositoryId = repositoryId;
			return this;
		}
		
		public Builder branchPath(final String branchPath) {
			this.branchPath = branchPath;
			return this;
		}
		
		public DatastoreLockIndexEntry build() {
			return new DatastoreLockIndexEntry(id, userId, description, parentDescription, repositoryId, branchPath);
		}
	}
	
	private final String id;
	private final String userId;
	private final String description;
	private final String parentDescription;
	private final String repositoryId;
	private final String branchPath;
	
	private DatastoreLockIndexEntry(final String id, final String userId, final String description, final String parentDescription, final String repositoryId, final String branchPath) {
		this.id = id;
		this.userId = userId;
		this.description = description;
		this.parentDescription = parentDescription;
		this.repositoryId = repositoryId;
		this.branchPath = branchPath;
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
	
	public String getParentDescription() {
		return parentDescription;
	}
	
	public String getRepositoryId() {
		return repositoryId;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, description, parentDescription, branchPath, repositoryId, userId);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (other == null) {
			return false;
		}
		
		if (!(other instanceof DatastoreLockIndexEntry)) {
			return false;
		}
		
		final DatastoreLockIndexEntry otherEntry = (DatastoreLockIndexEntry) other;
		return Objects.equals(id, otherEntry.getId()) 
				&& Objects.equals(userId, otherEntry.getUserId())
				&& Objects.equals(description, otherEntry.getDescription())
				&& Objects.equals(parentDescription, otherEntry.getParentDescription())
				&& Objects.equals(repositoryId, otherEntry.getRepositoryId())
				&& Objects.equals(branchPath, otherEntry.getBranchPath());
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("userId", userId)
				.add("description", description)
				.add("parentDescription", parentDescription)
				.add("repositoryId", repositoryId)
				.add("branchPath", branchPath)
				.toString();
	}
	
 
}
