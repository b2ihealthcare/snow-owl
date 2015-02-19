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
package com.b2international.snowowl.snomed.reasoner.model;

import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_CONCEPT;
import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_ROLE;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Represents a short form of SNOMED CT relationships for use in OWL ontology axioms.
 */
public final class RelationshipDefinition extends AnnotatedDefinition implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long typeId;
	private final long destinationId;
	private final boolean negated;
	private final boolean universal;

	/**
	 * Creates a new {@link RelationshipDefinition} instance with the specified arguments.
	 * @param concreteDomainDefinitions the set of associated {@link ConcreteDomainDefinition}s for this relationship
	 * @param relationshipId the relationship's SNOMED&nbsp;CT identifier
	 * @param typeId the relationship type identifier
	 * @param destinationId the relationship destination identifier
	 * @param negated {@code true} if the destination should be negated, {@code false} otherwise
	 * @param universal {@code true} it the relationship represents a universal restriction, {@code false} otherwise
	 */
	public RelationshipDefinition(final Set<ConcreteDomainDefinition> concreteDomainDefinitions, final long typeId, final long destinationId,
			final boolean negated, final boolean universal) {

		super(concreteDomainDefinitions);
		this.typeId = typeId;
		this.destinationId = destinationId;
		this.negated = negated;
		this.universal = universal;
	}

	/**
	 * Creats a new {@link RelationshipDefinition} instance for an IS A relationship.
	 * @param destinationId the relationship destination identifier
	 */
	public RelationshipDefinition(final long destinationId) {
		this(WritableEmptySet.<ConcreteDomainDefinition>create(), LongConcepts.IS_A_ID, destinationId, false, false);
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

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.ontology.AnnotatedDefinition#collect(org.semanticweb.owlapi.model.OWLDataFactory,
	 * org.semanticweb.owlapi.util.DefaultPrefixManager, java.util.List, java.util.Set)
	 */
	@Override public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms,
			final Set<OWLClassExpression> terms) {

		final OWLClassExpression relationshipExpression = getRelationshipExpression(df, prefixManager);
		terms.add(relationshipExpression);
		super.collect(df, prefixManager, axioms, terms); // Collect concrete domain definitions for the relationship
	}

	private OWLClassExpression getRelationshipExpression(final OWLDataFactory df, final DefaultPrefixManager prefixManager) {
		final OWLClassExpression valueExpressions = getValueExpression(df, prefixManager);
		return getRelationshipExpression(df, prefixManager, valueExpressions);
	}

	OWLClassExpression getValueExpression(final OWLDataFactory df, final DefaultPrefixManager prefixManager) {
		OWLClassExpression valueExpression = df.getOWLClass(PREFIX_CONCEPT + destinationId, prefixManager);
		if (negated) {
			valueExpression = df.getOWLObjectComplementOf(valueExpression);
		}
		return valueExpression;
	}

	OWLClassExpression getRelationshipExpression(final OWLDataFactory df, final DefaultPrefixManager prefixManager,
			final OWLClassExpression valueExpression) {
		final OWLClassExpression relationshipExpression;

		final OWLObjectProperty attributeProperty = df.getOWLObjectProperty(PREFIX_ROLE + typeId, prefixManager);

		if (universal) {
			relationshipExpression = df.getOWLObjectAllValuesFrom(attributeProperty, valueExpression);
		} else {
			relationshipExpression = df.getOWLObjectSomeValuesFrom(attributeProperty, valueExpression);
		}

		return relationshipExpression;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.ontology.AnnotatedDefinition#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (destinationId ^ (destinationId >>> 32));
		result = prime * result + (int) (typeId ^ (typeId >>> 32));
		result = prime * result + (negated ? 1231 : 1237);
		result = prime * result + (universal ? 1231 : 1237);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.ontology.AnnotatedDefinition#equals(java.lang.Object)
	 */
	@Override public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof RelationshipDefinition)) {
			return false;
		}
		final RelationshipDefinition other = (RelationshipDefinition) obj;
		if (destinationId != other.destinationId) {
			return false;
		}
		if (typeId != other.typeId) {
			return false;
		}
		if (negated != other.negated) {
			return false;
		}
		if (universal != other.universal) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RelationshipDefinition [typeId=");
		builder.append(typeId);
		builder.append(", destinationId=");
		builder.append(destinationId);
		builder.append(", negated=");
		builder.append(negated);
		builder.append(", universal=");
		builder.append(universal);
		builder.append(", getConcreteDomainDefinitions()=");
		builder.append(getConcreteDomainDefinitions());
		builder.append("]");
		return builder.toString();
	}
}