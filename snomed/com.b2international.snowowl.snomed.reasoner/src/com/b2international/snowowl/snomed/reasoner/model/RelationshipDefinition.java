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
package com.b2international.snowowl.snomed.reasoner.model;

import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_CONCEPT;
import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_ROLE;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Represents a short form of SNOMED CT relationships for use in OWL ontology axioms.
 */
public final class RelationshipDefinition implements Definition, Serializable {

	private static final long serialVersionUID = 3L;

	private final long typeId;
	private final long destinationId;
	private final boolean negated;
	private final boolean universal;

	/**
	 * Creates a new {@link RelationshipDefinition} instance with the specified arguments.
	 * 
	 * @param typeId the relationship type SCTID
	 * @param destinationId the relationship destination SCTID
	 * @param negated {@code true} if the destination should be negated, {@code false} otherwise
	 * @param universal {@code true} it the relationship represents a universal restriction, {@code false} otherwise
	 */
	public RelationshipDefinition(final long typeId, final long destinationId, final boolean negated, final boolean universal) {
		this.typeId = typeId;
		this.destinationId = destinationId;
		this.negated = negated;
		this.universal = universal;
	}

	/**
	 * @return the relationship destination identifier
	 */
	public long getDestinationId() {
		return destinationId;
	}

	/**
	 * @return the relationship type identifier
	 */
	public long getTypeId() {
		return typeId;
	}

	@Override 
	public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final Set<OWLClassExpression> terms) {
		final OWLClassExpression valueExpression = getValueExpression(df, prefixManager);
		final OWLClassExpression relationshipExpression = getRelationshipExpression(df, prefixManager, valueExpression);
		terms.add(relationshipExpression);
	}

	OWLClassExpression getValueExpression(final OWLDataFactory df, final DefaultPrefixManager prefixManager) {
		OWLClassExpression valueExpression = df.getOWLClass(PREFIX_CONCEPT + destinationId, prefixManager);

		if (negated) {
			valueExpression = df.getOWLObjectComplementOf(valueExpression);
		}

		return valueExpression;
	}

	OWLClassExpression getRelationshipExpression(final OWLDataFactory df, 
			final DefaultPrefixManager prefixManager,
			final OWLClassExpression valueExpression) {

		final OWLObjectProperty attributeProperty = df.getOWLObjectProperty(PREFIX_ROLE + typeId, prefixManager);
		final OWLClassExpression relationshipExpression;

		if (universal) {
			relationshipExpression = df.getOWLObjectAllValuesFrom(attributeProperty, valueExpression);
		} else {
			relationshipExpression = df.getOWLObjectSomeValuesFrom(attributeProperty, valueExpression);
		}

		return relationshipExpression;
	}

	@Override 
	public int hashCode() {
		return Objects.hash(typeId, destinationId, negated, universal);
	}

	@Override 
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (!(obj instanceof RelationshipDefinition)) { return false; }

		final RelationshipDefinition other = (RelationshipDefinition) obj;

		if (destinationId != other.destinationId) { return false; }
		if (typeId != other.typeId) { return false; }
		if (negated != other.negated) { return false; }
		if (universal != other.universal) { return false; }

		return true;
	}

	@Override 
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RelationshipDefinition [typeId=");
		builder.append(typeId);
		builder.append(", destinationId=");
		builder.append(destinationId);
		builder.append(", negated=");
		builder.append(negated);
		builder.append(", universal=");
		builder.append(universal);
		builder.append("]");
		return builder.toString();
	}
}
