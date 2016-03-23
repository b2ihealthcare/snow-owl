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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import java.util.List;

import com.b2international.snowowl.snomed.datastore.taxonomy.InvalidRelationship.MissingConcept;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Thrown when active relationships are found with an inactive or deleted source or target concept.
 */
public final class IncompleteTaxonomyException extends RuntimeException {

	private static final long serialVersionUID = -8805505705355299796L;
	
	private final List<InvalidRelationship> invalidRelationships;
	
	public IncompleteTaxonomyException(final List<InvalidRelationship> invalidRelationships) {
		Preconditions.checkNotNull(invalidRelationships, "List of invalid relationships may not be null.");
		Preconditions.checkArgument(!invalidRelationships.isEmpty(), "List of invalid relationships may not be empty.");
		this.invalidRelationships = ImmutableList.copyOf(invalidRelationships);
	}
	
	/**
	 * @return the list of invalid relationships found while building the taxonomy (at least a single element is guaranteed to be present)
	 */
	public List<InvalidRelationship> getInvalidRelationships() {
		return invalidRelationships;
	}
	
	@Override
	public String getMessage() {
		// Similar to the output in SnomedTaxonomyValidator, but we don't want to fetch labels here
		StringBuilder builder = new StringBuilder("The following IS A relationships have a missing or inactive concept: ");
		
		for (InvalidRelationship invalidRelationship : invalidRelationships) {
			builder.append(System.lineSeparator());
			builder.append('\t');
			builder.append(invalidRelationship.getRelationshipId());
			builder.append(": ");
			builder.append(invalidRelationship.getSourceId());
			if (MissingConcept.SOURCE.equals(invalidRelationship.getMissingConcept())) {
				builder.append('*');
			}
			builder.append(" --> ");
			builder.append(invalidRelationship.getDestinationId());
			if (MissingConcept.DESTINATION.equals(invalidRelationship.getMissingConcept())) {
				builder.append('*');
			}
		}
		
		builder.append(System.lineSeparator());
		return builder.toString();
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("invalidRelationships", invalidRelationships).toString();
	}
}
