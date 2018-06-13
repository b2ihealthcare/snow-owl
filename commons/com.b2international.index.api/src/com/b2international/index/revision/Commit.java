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
import java.util.stream.Collectors;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Analyzers;
import com.b2international.index.Doc;
import com.b2international.index.Text;
import com.b2international.index.WithScore;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
		private List<CommitDetail> details;
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
		
		public Builder details(final List<CommitDetail> details) {
			this.details = details;
			return this;
		}
		
		public Commit build() {
			return new Commit(id, branch, author, comment, timestamp, groupId, details);
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
			return exactMatch(Fields.TIMESTAMP, timeStamp);
		}
		
		public static Expression timestampRange(final long from, final long to) {
			return matchRange(Fields.TIMESTAMP, from, to);
		}

		public static Expression affectedObject(String objectId) {
			return com.b2international.index.query.Expressions.builder()
					.should(exactMatch(Fields.DETAILS_OBJECT, objectId))
					.should(exactMatch(Fields.DETAILS_COMPONENT, objectId))
					.build();
		}
		
	}
	
	public static final class Fields {
		public static final String BRANCH = "branch";
		public static final String AUTHOR = "author";
		public static final String COMMENT = "comment";
		public static final String COMMENT_PREFIX = "comment.prefix";
		public static final String TIMESTAMP = "timestamp";
		public static final String GROUP_ID = "groupId";
		private static final String DETAILS_OBJECT = "details.objects";
		private static final String DETAILS_COMPONENT = "details.components";
	}

	private final String id;
	private final String branch;
	private final String author;
	@Text(analyzer=Analyzers.TOKENIZED)
	@Text(alias="prefix", analyzer=Analyzers.PREFIX, searchAnalyzer=Analyzers.TOKENIZED)
	private final String comment;
	private final long timestamp;
	private final String groupId;
	private final List<CommitDetail> details;
	
	private float score = 0.0f;
	
	@JsonIgnore
	private transient Multimap<String, CommitDetail> detailsByObject;
	
	private Commit(
			final String id,
			final String branch,
			final String author,
			final String comment,
			final long timestamp,
			final String groupId,
			final List<CommitDetail> details) {
		this.id = id;
		this.branch = branch;
		this.author = author;
		this.comment = comment;
		this.timestamp = timestamp;
		this.groupId = groupId;
		this.details = Collections3.toImmutableList(details);
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
	
	public List<CommitDetail> getDetails() {
		return details;
	}

	public Collection<CommitDetail> getDetailsByObject(String objectId) {
		if (detailsByObject == null) {
			detailsByObject = ArrayListMultimap.create();
		}
		if (!detailsByObject.containsKey(objectId)) {
			final List<CommitDetail> objectDetails = details.stream()
				.map(detail -> detail.extract(objectId))
				.filter(detail -> !detail.isEmpty())
				.collect(Collectors.toList());
			detailsByObject.putAll(objectId, objectDetails);
		}
		return detailsByObject.get(objectId);
	}

	public Commit apply(Commit commit) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
}
