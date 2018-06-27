/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Date;

import com.b2international.index.Doc;
import com.b2international.index.Keyword;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 7.0
 */
@Doc(type="classificationRun")
@JsonDeserialize(builder=ClassificationRunDocument.Builder.class)
public final class ClassificationRunDocument {

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {

		private String id;
		private String userId;
		private String reasonerId;
		private String branch;
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

		public ClassificationRunDocument build() {
			return new ClassificationRunDocument(id, 
					userId, 
					reasonerId, 
					branch, 
					status, 
					creationDate, 
					completionDate, 
					saveDate, 
					hasInferredChanges, 
					hasRedundantStatedChanges, 
					hasEquivalentConcepts);
		}
	}

	@Keyword private final String id;
	@Keyword private final String userId;
	@Keyword private final String reasonerId;
	@Keyword private final String branch;
	private final ClassificationStatus status;
	private final Date creationDate;
	private final Date completionDate;
	private final Date saveDate;
	private final Boolean hasInferredChanges;
	private final Boolean hasRedundantStatedChanges;
	private final Boolean hasEquivalentConcepts;

	private ClassificationRunDocument(final String id, 
			final String userId, 
			final String reasonerId, 
			final String branch,
			final ClassificationStatus status, 
			final Date creationDate, 
			final Date completionDate, 
			final Date saveDate,
			final Boolean hasInferredChanges, 
			final Boolean hasRedundantStatedChanges, 
			final Boolean hasEquivalentConcepts) {

		this.id = id;
		this.userId = userId;
		this.reasonerId = reasonerId;
		this.branch = branch;
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

	public String getUserId() {
		return userId;
	}

	public String getReasonerId() {
		return reasonerId;
	}

	public String getBranch() {
		return branch;
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
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", reasonerId=");
		builder.append(reasonerId);
		builder.append(", branch=");
		builder.append(branch);
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
