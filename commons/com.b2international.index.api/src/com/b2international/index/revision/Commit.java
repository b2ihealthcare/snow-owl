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
package com.b2international.index.revision;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchAny;
import static com.b2international.index.query.Expressions.matchRange;
import static com.b2international.index.query.Expressions.matchTextAll;
import static com.b2international.index.query.Expressions.matchTextPhrase;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Analyzers;
import com.b2international.index.Doc;
import com.b2international.index.Text;
import com.b2international.index.WithScore;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Maps;

/**
 * @since 5.2
 */
@Doc
@JsonDeserialize(builder = Commit.Builder.class)
public final class Commit implements WithScore {

	static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	static final class Builder {

		private String id;
		private String branch;
		private String author;
		private String comment;
		private long timestamp;
		private List<CommitChange> changes;
		private String groupId;

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

		public Builder timestamp(final long timestamp) {
			this.timestamp = timestamp;
			return this;
		}
		
		public Builder groupId(String groupId) {
			this.groupId = groupId;
			return this;
		}
		
		public Builder changes(final List<CommitChange> changes) {
			this.changes = changes;
			return this;
		}
		
		public Commit build() {
			return new Commit(id, branch, author, comment, timestamp, groupId, changes);
		}

	}
	
	public static final class Expressions {
		
		private Expressions() {}
		
		public static final Expression id(String id) {
			return DocumentMapping.matchId(id);
		}
		
		public static final Expression ids(Collection<String> ids) {
			return matchAny(DocumentMapping._ID, ids);
		}
		
		public static Expression branches(final Iterable<String> branchPaths) {
			return matchAny(Fields.BRANCH, branchPaths);
		}
		
		public static Expression author(final String author) {
			return exactMatch(Fields.AUTHOR, author);
		}
		
		public static Expression exactComment(final String comment) {
			return matchTextPhrase(Fields.COMMENT, comment);
		}
		
		public static Expression allCommentPrefixesPresent(final String comment) {
			return matchTextAll(Fields.COMMENT_PREFIX, comment);
		}
		
		public static Expression timestamp(final long timeStamp) {
			return exactMatch(Fields.TIME_STAMP, timeStamp);
		}
		
		public static Expression timestampRange(final long from, final long to) {
			return matchRange(Fields.TIME_STAMP, from, to);
		}

		public static Expression containerId(String containerId) {
			return exactMatch(Fields.CHANGES_CONTAINER_ID, containerId);
		}
		
	}
	
	public static final class Fields {
		public static final String BRANCH = "branch";
		public static final String AUTHOR = "author";
		public static final String COMMENT = "comment";
		public static final String COMMENT_PREFIX = "comment.prefix";
		public static final String TIME_STAMP = "timestamp";
		public static final String GROUP_ID = "groupId";
		public static final String CHANGES_CONTAINER_ID = "changes.containerId";
	}

	private final String id;
	private final String branch;
	private final String author;
	@Text(analyzer=Analyzers.TOKENIZED)
	@Text(alias="prefix", analyzer=Analyzers.PREFIX, searchAnalyzer=Analyzers.TOKENIZED)
	private final String comment;
	private final long timestamp;
	private final List<CommitChange> changes;
	private final String groupId;
	
	private float score = 0.0f;
	private transient Map<String, CommitChange> changesByContainer;
	
	private Commit(
			final String id,
			final String branch,
			final String author,
			final String comment,
			final long timestamp,
			final String groupId,
			final List<CommitChange> changes) {
		this.id = id;
		this.branch = branch;
		this.author = author;
		this.comment = comment;
		this.timestamp = timestamp;
		this.groupId = groupId;
		this.changes = Collections3.toImmutableList(changes);
	}

	public String getId() {
		return id;
	}
	
	@Override
	public void setScore(float score) {
		this.score = score;
	}

	@Override
	@JsonIgnore
	public float getScore() {
		return score;
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

	public long getTimestamp() {
		return timestamp;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	@JsonProperty
	List<CommitChange> getChanges() {
		return changes;
	}
	
	@JsonIgnore
	public Map<String, CommitChange> getChangesByContainer() {
		if (changesByContainer == null) {
			changesByContainer = Maps.uniqueIndex(changes, CommitChange::getContainerId);
		}
		return changesByContainer;
	}
	
	public CommitChange getChangesByContainer(String container) {
		return getChangesByContainer().get(container);
	}
	
}
