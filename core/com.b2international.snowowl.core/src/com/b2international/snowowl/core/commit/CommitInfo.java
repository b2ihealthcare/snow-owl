/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.commit;

import java.io.Serializable;

import com.b2international.index.revision.Commit;
import com.b2international.index.revision.RevisionBranchPoint;
import com.b2international.snowowl.core.repository.RepositoryCommitNotification;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 5.2
 */
@JsonDeserialize(builder=CommitInfo.Builder.class)
public final class CommitInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final class Expand {
		public static final String DETAILS = "details";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(final Commit doc) {
		return builder()
			.id(doc.getId())
			.branch(doc.getBranch())
			.author(doc.getAuthor())
			.comment(doc.getComment())
			.timestamp(doc.getTimestamp())
			.groupId(doc.getGroupId())
			.mergeSource(doc.getMergeSource());
	}
	
	public static Builder builder(final RepositoryCommitNotification notification) {
		return builder()
				.id(notification.getCommitId())
				.branch(notification.getBranchPath())
				.author(notification.getUserId())
				.comment(notification.getComment())
				.timestamp(notification.getCommitTimestamp())
				.groupId(notification.getGroupId())
				.mergeSource(notification.getMergeSource());
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder {
		
		private String id;
		private String branch;
		private String author;
		private String comment;
		private Long timestamp;
		private String groupId;
		private RevisionBranchPoint mergeSource;
		private CommitInfoDetails details;
		
		public Builder id(final String id) {
			this.id = id;
			return this;
		}
		
		public Builder branch(final String branch) {
			this.branch = branch;
			return this;
		}
		
		public Builder author(final String author) {
			this.author = author;
			return this;
		}
		
		public Builder comment(final String comment) {
			this.comment = comment;
			return this;
		}
		
		public Builder timestamp(final Long timestamp) {
			this.timestamp = timestamp;
			return this;
		}
		
		public Builder groupId(final String groupId) {
			this.groupId = groupId;
			return this;
		}
		
		public Builder mergeSource(RevisionBranchPoint mergeSource) {
			this.mergeSource = mergeSource;
			return this;
		}
		
		public Builder details(CommitInfoDetails details) {
			this.details = details;
			return this;
		}
		
		public CommitInfo build() {
			return new CommitInfo(id, branch, author, comment, timestamp, groupId, mergeSource, details);
		}

	}
	
	private final String id;
	private final String branch;
	private final String author;
	private final String comment;
	private final Long timestamp;
	private final String groupId;
	private final RevisionBranchPoint mergeSource;
	private final CommitInfoDetails details;
	
	CommitInfo(
			final String id,
			final String branch,
			final String author,
			final String comment,
			final Long timestamp, 
			final String groupId,
			final RevisionBranchPoint mergeSource,
			final CommitInfoDetails details) {
		this.id = id;
		this.branch = branch;
		this.author = author;
		this.comment = comment;
		this.timestamp = timestamp;
		this.groupId = groupId;
		this.mergeSource = mergeSource;
		this.details = details;
	}

	public String getId() {
		return id;
	}
	
	public String getBranch() {
		return branch;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getComment() {
		return comment;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	public String getTimestampString() {
		return timestamp == null ? null : Long.toString(timestamp);
	}
	
	public String getGroupId() {
		return groupId;
	}

	public RevisionBranchPoint getMergeSource() {
		return mergeSource;
	}
	
	public CommitInfoDetails getDetails() {
		return details;
	}

}
