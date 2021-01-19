/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.index;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.match;
import static com.b2international.index.query.Expressions.matchAny;
import static com.b2international.index.query.Expressions.matchAnyEnum;
import static com.b2international.index.query.Expressions.matchRange;

import java.util.Date;

import com.b2international.index.Doc;
import com.b2international.index.Script;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 6.12 (originally introduced in 7.x)
 */
@Doc(type="classificationtask")
@JsonDeserialize(builder=ClassificationTaskDocument.Builder.class)
@Script(name=ClassificationTaskDocument.Scripts.RUNNING, script="ctx._source.status = 'RUNNING'; "
		+ "ctx._source.timestamp = params.timestamp")
@Script(name=ClassificationTaskDocument.Scripts.COMPLETED, script="ctx._source.status = 'COMPLETED'; "
		+ "ctx._source.completionDate = params.completionDate; "
		+ "ctx._source.hasInferredChanges = params.hasInferredChanges; "
		+ "ctx._source.hasRedundantStatedChanges = params.hasRedundantStatedChanges; "
		+ "ctx._source.hasEquivalentConcepts = params.hasEquivalentConcepts")
@Script(name=ClassificationTaskDocument.Scripts.FAILED, script="ctx._source.status = 'FAILED'; "
		+ "ctx._source.completionDate = params.completionDate")
@Script(name=ClassificationTaskDocument.Scripts.DELETED, script="ctx._source.deleted = true")
@Script(name=ClassificationTaskDocument.Scripts.SAVING_IN_PROGRESS, script="ctx._source.status = 'SAVING_IN_PROGRESS'")
@Script(name=ClassificationTaskDocument.Scripts.SAVED, script="ctx._source.status = 'SAVED'; "
		+ "ctx._source.saveDate = params.saveDate")
@Script(name=ClassificationTaskDocument.Scripts.SAVE_FAILED, script="ctx._source.status = 'SAVE_FAILED'")
@Script(name=ClassificationTaskDocument.Scripts.CANCELED, script="ctx._source.status = 'CANCELED'")
public final class ClassificationTaskDocument {

	public static class Scripts {
		public static final String RUNNING = "running";
		public static final String COMPLETED = "completed";
		public static final String FAILED = "failed";
		public static final String DELETED = "deleted";
		public static final String SAVING_IN_PROGRESS = "saving_in_progress";
		public static final String SAVE_FAILED = "save_failed";
		public static final String SAVED = "saved";
		public static final String CANCELED = "canceled";
	}

	public static class Fields {
		public static final String ID = "id";
		public static final String DELETED = "deleted";
		public static final String USER_ID = "userId";
		public static final String BRANCH = "branch";
		public static final String TIMESTAMP = "timestamp";
		public static final String STATUS = "status";
		public static final String CREATION_DATE = "creationDate";
	}

	public static class Expressions {
		public static Expression id(final String id) {
			return exactMatch(Fields.ID, id);
		}

		public static Expression ids(final Iterable<String> ids) {
			return matchAny(Fields.ID, ids);
		}
		
		public static Expression deleted(final boolean deleted) {
			return match(Fields.DELETED, deleted);
		}

		public static Expression userId(final String userId) {
			return exactMatch(Fields.USER_ID, userId);
		}

		public static Expression branch(final String branch) {
			return exactMatch(Fields.BRANCH, branch);
		}

		public static Expression branches(final Iterable<String> branches) {
			return matchAny(Fields.BRANCH, branches);
		}

		public static Expression statuses(final Iterable<ClassificationStatus> statuses) {
			return matchAnyEnum(Fields.STATUS, statuses);
		}

		public static Expression timestampBefore(final long endInclusive) {
			return matchRange(Fields.TIMESTAMP, 0L, endInclusive);
		}

		public static Expression created(final long startInclusive, final long endExclusive) {
			return matchRange(Fields.CREATION_DATE, startInclusive, endExclusive, true, false);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(final ClassificationTaskDocument source) {
		return new Builder()
				.id(source.id)
				.deleted(source.deleted)
				.userId(source.userId)
				.reasonerId(source.reasonerId)
				.branch(source.branch)
				.timestamp(source.timestamp)
				.status(source.status)
				.creationDate(source.creationDate)
				.completionDate(source.completionDate)
				.saveDate(source.saveDate)
				.hasInferredChanges(source.hasInferredChanges)
				.hasRedundantStatedChanges(source.hasRedundantStatedChanges)
				.hasEquivalentConcepts(source.hasEquivalentConcepts);
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {

		private String id;
		private boolean deleted;
		private String userId;
		private String reasonerId;
		private String branch;
		private long timestamp;
		private ClassificationStatus status;
		private Date creationDate;
		private Date completionDate;
		private Date saveDate;
		private Boolean hasInferredChanges;
		private Boolean hasRedundantStatedChanges;
		private Boolean hasEquivalentConcepts;

		@JsonCreator
		private Builder() {
			// Disallow instantiation outside static method
		}

		public Builder id(final String id) {
			this.id = id;
			return this;
		}
		
		public Builder deleted(final boolean deleted) {
			this.deleted = deleted;
			return this;
		}

		public Builder userId(final String userId) {
			this.userId = userId;
			return this;
		}

		public Builder reasonerId(final String reasonerId) {
			this.reasonerId = reasonerId;
			return this;
		}

		public Builder branch(final String branch) {
			this.branch = branch;
			return this;
		}

		public Builder timestamp(final long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder status(final ClassificationStatus status) {
			this.status = status;
			return this;
		}

		public Builder creationDate(final Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		public Builder completionDate(final Date completionDate) {
			this.completionDate = completionDate;
			return this;
		}

		public Builder saveDate(final Date saveDate) {
			this.saveDate = saveDate;
			return this;
		}

		public Builder hasInferredChanges(final Boolean hasInferredChanges) {
			this.hasInferredChanges = hasInferredChanges;
			return this;
		}

		public Builder hasRedundantStatedChanges(final Boolean hasRedundantStatedChanges) {
			this.hasRedundantStatedChanges = hasRedundantStatedChanges;
			return this;
		}

		public Builder hasEquivalentConcepts(final Boolean hasEquivalentConcepts) {
			this.hasEquivalentConcepts = hasEquivalentConcepts;
			return this;
		}

		public ClassificationTaskDocument build() {
			return new ClassificationTaskDocument(id, 
					deleted,
					userId, 
					reasonerId, 
					branch, 
					timestamp,
					status, 
					creationDate, 
					completionDate, 
					saveDate, 
					hasInferredChanges, 
					hasRedundantStatedChanges, 
					hasEquivalentConcepts);
		}
	}

	private final String id;
	private final boolean deleted;
	private final String userId;
	private final String reasonerId;
	private final String branch;
	private final long timestamp;
	private final ClassificationStatus status;
	private final Date creationDate;
	private final Date completionDate;
	private final Date saveDate;
	private final Boolean hasInferredChanges;
	private final Boolean hasRedundantStatedChanges;
	private final Boolean hasEquivalentConcepts;

	private ClassificationTaskDocument(final String id, 
			final boolean deleted,
			final String userId, 
			final String reasonerId, 
			final String branch,
			final long timestamp,
			final ClassificationStatus status, 
			final Date creationDate, 
			final Date completionDate, 
			final Date saveDate,
			final Boolean hasInferredChanges, 
			final Boolean hasRedundantStatedChanges, 
			final Boolean hasEquivalentConcepts) {

		this.id = id;
		this.deleted = deleted;
		this.userId = userId;
		this.reasonerId = reasonerId;
		this.branch = branch;
		this.timestamp = timestamp;
		this.status = status;
		this.creationDate = creationDate;
		this.completionDate = completionDate;
		this.saveDate = saveDate;
		this.hasInferredChanges = hasInferredChanges;
		this.hasRedundantStatedChanges = hasRedundantStatedChanges;
		this.hasEquivalentConcepts = hasEquivalentConcepts;
	}

	public String getId() {
		return id;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public String getUserId() {
		return userId;
	}

	public String getReasonerId() {
		return reasonerId;
	}

	public String getBranch() {
		return branch;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public ClassificationStatus getStatus() {
		return status;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getCompletionDate() {
		return completionDate;
	}

	public Date getSaveDate() {
		return saveDate;
	}

	public Boolean getHasInferredChanges() {
		return hasInferredChanges;
	}

	public Boolean getHasRedundantStatedChanges() {
		return hasRedundantStatedChanges;
	}

	public Boolean getHasEquivalentConcepts() {
		return hasEquivalentConcepts;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ClassificationRunDocument [id=");
		builder.append(id);
		builder.append(", deleted=");
		builder.append(deleted);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", reasonerId=");
		builder.append(reasonerId);
		builder.append(", branch=");
		builder.append(branch);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", status=");
		builder.append(status);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append(", completionDate=");
		builder.append(completionDate);
		builder.append(", saveDate=");
		builder.append(saveDate);
		builder.append(", hasInferredChanges=");
		builder.append(hasInferredChanges);
		builder.append(", hasRedundantStatedChanges=");
		builder.append(hasRedundantStatedChanges);
		builder.append(", hasEquivalentConcepts=");
		builder.append(hasEquivalentConcepts);
		builder.append("]");
		return builder.toString();
	}
}
