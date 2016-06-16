/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.impl.domain.classification;

import java.util.Date;

import com.b2international.snowowl.snomed.api.domain.classification.ClassificationStatus;
import com.b2international.snowowl.snomed.api.domain.classification.IClassificationRun;

/**
 */
public class ClassificationRun extends ClassificationInput implements IClassificationRun {

	private String id;
	private ClassificationStatus status;
	private Date creationDate;
	private Date completionDate;
	private Date saveDate;
	private Boolean inferredRelationshipChangesFound;
	private Boolean redundantStatedRelationshipsFound;
	private Boolean equivalentConceptsFound;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ClassificationStatus getStatus() {
		return status;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public Date getCompletionDate() {
		return completionDate;
	}
	
	@Override
	public Date getSaveDate() {
		return saveDate;
	}

	@Override
	public Boolean getInferredRelationshipChangesFound() {
		return inferredRelationshipChangesFound;
	}
	
	public Boolean getRedundantStatedRelationshipsFound() {
		return redundantStatedRelationshipsFound;
	}

	public Boolean getEquivalentConceptsFound() {
		return equivalentConceptsFound;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setStatus(final ClassificationStatus status) {
		this.status = status;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setCompletionDate(final Date completionDate) {
		this.completionDate = completionDate;
	}

	public void setSaveDate(final Date saveDate) {
		this.saveDate = saveDate;
	}
	
	public void setInferredRelationshipChangesFound(Boolean relationshipChangesFound) {
		this.inferredRelationshipChangesFound = relationshipChangesFound;
	}
	
	public void setRedundantStatedRelationshipsFound(Boolean redundantStatedRelationshipsFound) {
		this.redundantStatedRelationshipsFound = redundantStatedRelationshipsFound;
	}

	public void setEquivalentConceptsFound(Boolean equivalentConceptsFound) {
		this.equivalentConceptsFound = equivalentConceptsFound;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ClassificationRun [id=");
		builder.append(id);
		builder.append(", status=");
		builder.append(status);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append(", completionDate=");
		builder.append(completionDate);
		builder.append(", saveDate=");
		builder.append(saveDate);
		builder.append(", relationshipChangesFound=");
		builder.append(inferredRelationshipChangesFound);
		builder.append(", redundantStatedRelationshipsFound=");
		builder.append(redundantStatedRelationshipsFound);
		builder.append(", equivalentConceptsFound=");
		builder.append(equivalentConceptsFound);
		builder.append("]");
		return builder.toString();
	}
}