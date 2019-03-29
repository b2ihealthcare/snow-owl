/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import com.google.common.base.MoreObjects;

/**
 * @since 4.7
 */
public final class InvalidRelationship {

	public enum MissingConcept {
		
		SOURCE("source"), DESTINATION("destination");
		
		private String label;
		
		MissingConcept(String label) {
			this.label = label;
		}
		
		public String getLabel() {
			return label;
		}
		
	}

	private final long relationshipId;
	private final long sourceId;
	private final long destinationId;
	private final MissingConcept missingConcept; 

	public InvalidRelationship(final long relationshipId, final long sourceId, final long destinationId, final MissingConcept missingConcept) {
		this.relationshipId = relationshipId;
		this.sourceId = sourceId;
		this.destinationId = destinationId;
		this.missingConcept = missingConcept;
	}

	public long getRelationshipId() {
		return relationshipId;
	}

	public long getSourceId() {
		return sourceId;
	}

	public long getDestinationId() {
		return destinationId;
	}

	public MissingConcept getMissingConcept() {
		return missingConcept;
	}

	public long getMissingConceptId() {
		switch (missingConcept) {
			case DESTINATION:
				return destinationId;
			case SOURCE:
				return sourceId;
			default:
				throw new IllegalStateException("Unexpected missing concept type '" + missingConcept + "'.");
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("relationshipId", relationshipId)
				.add("sourceId", sourceId)
				.add("destinationId", destinationId)
				.add("missingConcept", missingConcept)
				.toString();
	}
}
