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
package com.b2international.snowowl.snomed.reasoner.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @since 7.0
 */
public final class ClassificationTask implements Serializable {

	private String id;
	private String userId;
	private String reasonerId;
	private String branch;
	private long timestamp;
	private ClassificationStatus status;
	private Date creationDate;
	private Date completionDate; // populated on status changes to COMPLETED, CANCELED or FAILED
	private Date saveDate; // populate on status change to SAVED
	private Boolean inferredRelationshipChangesFound; // non-null in COMPLETED state only
	private Boolean redundantStatedRelationshipsFound; // non-null in COMPLETED state only
	private Boolean equivalentConceptsFound; // non-null in COMPLETED state only

	// fields below are set only if an appropriate expand option is present
	private EquivalentConceptSets equivalentConceptSets; 
	private RelationshipChanges relationshipChanges;
	private ConcreteDomainChanges concreteDomainChanges;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public String getReasonerId() {
		return reasonerId;
	}

	public void setReasonerId(final String reasonerId) {
		this.reasonerId = reasonerId;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(final String branch) {
		this.branch = branch;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public ClassificationStatus getStatus() {
		return status;
	}

	public void setStatus(final ClassificationStatus status) {
		this.status = status;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(final Date completionDate) {
		this.completionDate = completionDate;
	}

	public Date getSaveDate() {
		return saveDate;
	}

	public void setSaveDate(final Date saveDate) {
		this.saveDate = saveDate;
	}

	public Boolean getInferredRelationshipChangesFound() {
		return inferredRelationshipChangesFound;
	}

	public void setInferredRelationshipChangesFound(final Boolean inferredRelationshipChangesFound) {
		this.inferredRelationshipChangesFound = inferredRelationshipChangesFound;
	}

	public Boolean getRedundantStatedRelationshipsFound() {
		return redundantStatedRelationshipsFound;
	}

	public void setRedundantStatedRelationshipsFound(final Boolean redundantStatedRelationshipsFound) {
		this.redundantStatedRelationshipsFound = redundantStatedRelationshipsFound;
	}

	public Boolean getEquivalentConceptsFound() {
		return equivalentConceptsFound;
	}

	public void setEquivalentConceptsFound(final Boolean equivalentConceptsFound) {
		this.equivalentConceptsFound = equivalentConceptsFound;
	}

	public EquivalentConceptSets getEquivalentConceptSets() {
		return equivalentConceptSets;
	}

	public void setEquivalentConceptSets(final EquivalentConceptSets equivalentConceptSets) {
		this.equivalentConceptSets = equivalentConceptSets;
	}

	public RelationshipChanges getRelationshipChanges() {
		return relationshipChanges;
	}

	public void setRelationshipChanges(final RelationshipChanges relationshipChanges) {
		this.relationshipChanges = relationshipChanges;
	}

	public ConcreteDomainChanges getConcreteDomainChanges() {
		return concreteDomainChanges;
	}

	public void setConcreteDomainChanges(final ConcreteDomainChanges concreteDomainChanges) {
		this.concreteDomainChanges = concreteDomainChanges;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ClassificationRun [id=");
		builder.append(id);
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
		builder.append(", inferredRelationshipChangesFound=");
		builder.append(inferredRelationshipChangesFound);
		builder.append(", redundantStatedRelationshipsFound=");
		builder.append(redundantStatedRelationshipsFound);
		builder.append(", equivalentConceptsFound=");
		builder.append(equivalentConceptsFound);
		builder.append(", equivalentConceptSets=");
		builder.append(equivalentConceptSets);
		builder.append(", relationshipChanges=");
		builder.append(relationshipChanges);
		builder.append(", concreteDomainChanges=");
		builder.append(concreteDomainChanges);
		builder.append("]");
		return builder.toString();
	}
}
