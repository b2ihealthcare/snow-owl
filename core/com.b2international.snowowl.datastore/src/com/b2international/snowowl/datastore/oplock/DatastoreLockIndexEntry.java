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
@JsonDeserialize(builder=DatastoreLockIndexEntry.Builder.class)
public class DatastoreLockIndexEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final class Fields {
		public static final String ID = "id";
		public static final String USER_ID = "userId";
		public static final String DESCRIPTION = "description";
		public static final String REPOSITORYUUID= "repositoryUuid";
		public static final String BRANCHPATH = "branchPath";
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
		
		public static Expression repositoryUuid(final String repositoryUuid) {
			return com.b2international.index.query.Expressions.exactMatch(Fields.REPOSITORYUUID, repositoryUuid);
		}
		
		public static Expression branchPath(final String branchPath) {
			return com.b2international.index.query.Expressions.exactMatch(Fields.BRANCHPATH, branchPath);
		}
		
	}
	 
	public static DatastoreLockIndexEntry.Builder from(DatastoreLockIndexEntry source) {
		return builder()
				.id(source.getId())
				.userId(source.getUserId())
				.description(source.getDescription())
				.repositoryUuid(source.getRepositoryUuid())
				.branchPath(source.getBranchPath());
	}
	
	public static DatastoreLockIndexEntry.Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {
		
		private String id;
		private String userId;
		private String description;
		private String repositoryUuid;
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
		
		public Builder description(final String description) {
			this.description = description;
			return this;
		}
		
		public Builder repositoryUuid(final String repositoryUuid) {
			this.repositoryUuid = repositoryUuid;
			return this;
		}
		
		public Builder branchPath(final String branchPath) {
			this.branchPath = branchPath;
			return this;
		}
		
		public DatastoreLockIndexEntry build() {
			return new DatastoreLockIndexEntry(id, userId, description, repositoryUuid, branchPath);
		}
	}
	
	private final String id;
	private final String userId;
	private final String description;
	private final String repositoryUuid;
	private final String branchPath;
	
	private DatastoreLockIndexEntry(final String id, final String userId, final String description, final String repositoryUUid, final String branchPath) {
		this.id = id;
		this.userId = userId;
		this.description = description;
		this.repositoryUuid = repositoryUUid;
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
	
	public String getRepositoryUuid() {
		return repositoryUuid;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	@Override
	public int hashCode() {
		return 31 + id.hashCode() + description.hashCode() + branchPath.hashCode() + repositoryUuid.hashCode() + userId.hashCode();
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
				&& Objects.equals(repositoryUuid, otherEntry.getRepositoryUuid())
				&& Objects.equals(branchPath, otherEntry.getBranchPath());
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("userId", userId)
				.add("description", description)
				.add("repositoryUuid", repositoryUuid)
				.add("branchPath", branchPath)
				.toString();
	}
	
 
}
