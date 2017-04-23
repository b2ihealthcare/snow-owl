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
package com.b2international.snowowl.datastore.commitinfo;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchAny;
import static com.b2international.index.query.Expressions.matchTextAll;
import static com.b2international.index.query.Expressions.matchTextPhrase;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.b2international.index.Analyzed;
import com.b2international.index.Analyzers;
import com.b2international.index.Doc;
import com.b2international.index.WithId;
import com.b2international.index.WithScore;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 5.2
 */
@Doc
@JsonDeserialize(builder = CommitInfoDocument.Builder.class)
public final class CommitInfoDocument implements WithId, WithScore {

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static final class Builder {

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

		public CommitInfoDocument build() {
			return new CommitInfoDocument(id, branch, userId, comment, timeStamp);
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
		
		public static Expression branch(final String branch) {
			return exactMatch(Fields.BRANCH, branch);
		}
		
		public static Expression userId(final String userId) {
			return exactMatch(Fields.USER_ID, userId);
		}
		
		public static Expression exactComment(final String comment) {
			return matchTextPhrase(Fields.COMMENT, comment);
		}
		
		public static Expression allCommentPrefixesPresent(final String comment) {
			return matchTextAll(Fields.COMMENT+".prefix", comment);
		}
		
		public static Expression timeStamp(final long timeStamp) {
			return exactMatch(Fields.TIME_STAMP, timeStamp);
		}
		
	}
	
	public static final class Fields {
		public static final String BRANCH = "branch";
		public static final String USER_ID = "userId";
		public static final String COMMENT = "comment";
		public static final String TIME_STAMP = "timeStamp";
	}

	private String _id;
	
	private final String branch;
	private final String userId;
	@Analyzed(analyzer=Analyzers.TOKENIZED)
	@Analyzed(alias="prefix", analyzer=Analyzers.PREFIX, searchAnalyzer=Analyzers.TOKENIZED)
	private final String comment;
	private final long timeStamp;
	
	private float score = 0.0f;
	
	private CommitInfoDocument(
			final String id,
			final String branch,
			final String userId,
			final String comment,
			final long timeStamp) {
		this._id = id;
		this.branch = branch;
		this.userId = userId;
		this.comment = comment;
		this.timeStamp = timeStamp;
	}
	
	@Override
	public final void set_id(String _id) {
		this._id = _id;
	}
	
	@Override
	@JsonIgnore
	public final String _id() {
		return checkNotNull(_id);
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
