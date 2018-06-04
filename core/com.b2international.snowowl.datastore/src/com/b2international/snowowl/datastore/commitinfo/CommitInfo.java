/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.commitinfo;

import java.io.Serializable;

import com.b2international.index.revision.Commit;
import com.b2international.snowowl.datastore.events.RepositoryCommitNotification;

/**
 * @since 5.2
 */
public final class CommitInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(final Commit doc) {
		return builder()
			.id(doc.getId())
			.branch(doc.getBranch())
			.userId(doc.getAuthor())
			.comment(doc.getComment())
			.timeStamp(doc.getTimestamp());
	}
	
	public static Builder builder(final RepositoryCommitNotification notification) {
		return builder()
				.id(notification.getCommitId())
				.branch(notification.getBranchPath())
				.userId(notification.getUserId())
				.comment(notification.getComment())
				.timeStamp(notification.getCommitTimestamp());
	}
	
	public static class Builder {
		
		private String id;
		private String branch;
		private String userId;
		private String comment;
		private long timeStamp;
		
		public Builder id(final String id) {
			this.id = id;
			return this;
		}
		
		public Builder branch(final String branch) {
			this.branch = branch;
			return this;
		}
		
		public Builder userId(final String userId) {
			this.userId = userId;
			return this;
		}
		
		public Builder comment(final String comment) {
			this.comment = comment;
			return this;
		}
		
		public Builder timeStamp(final long timeStamp) {
			this.timeStamp = timeStamp;
			return this;
		}
		
		public CommitInfo build() {
			return new CommitInfo(id, branch, userId, comment, timeStamp);
		}
		
	}
	
	private final String id;
	private final String branch;
	private final String userId;
	private final String comment;
	private final long timeStamp;
	
	CommitInfo(
			final String id,
			final String branch,
			final String userId,
			final String comment,
			final long timeStamp) {
		this.id = id;
		this.branch = branch;
		this.userId = userId;
		this.comment = comment;
		this.timeStamp = timeStamp;
	}

	public String getId() {
		return id;
	}
	
	public String getBranch() {
		return branch;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getComment() {
		return comment;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}

}
