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

import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_DATA;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * Represents a short version of a concrete domain reference set member for ontology definitions.
 */
public final class ConcreteDomainDefinition implements Definition, Serializable {

	private static final long serialVersionUID = 2L;

	private final String typeId;
	private final OWL2Datatype owl2Datatype;
	private final String serializedValue;

	private static OWL2Datatype getOWL2Datatype(final DataType dataType) {
		switch (dataType) {
			case BOOLEAN: return OWL2Datatype.XSD_BOOLEAN;
			case DATE: return OWL2Datatype.XSD_DATE_TIME;
			case DECIMAL: return OWL2Datatype.XSD_DECIMAL;
			case INTEGER: return OWL2Datatype.XSD_INT;
			case STRING: return OWL2Datatype.RDF_PLAIN_LITERAL;
			default: throw new IllegalStateException(String.format("Unhandled datatype enum '%s'.", dataType));
		}
	}

	public ConcreteDomainDefinition(final String typeId, final DataType dataType, final String serializedValue) {
		this.typeId = typeId;
		this.owl2Datatype = getOWL2Datatype(dataType);
		this.serializedValue = serializedValue;
	}

	@Override 
	public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final Set<OWLClassExpression> terms) {
		final OWLDataProperty dataProperty = df.getOWLDataProperty(PREFIX_DATA + typeId, prefixManager);
		final OWLLiteral valueLiteral = df.getOWLLiteral(serializedValue, owl2Datatype);
		final OWLDataHasValue dataExpression = df.getOWLDataHasValue(dataProperty, valueLiteral);

		terms.add(dataExpression);
	}

	@Override 
	public int hashCode() {
		return Objects.hash(typeId, owl2Datatype, serializedValue);
	}

	@Override 
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof ConcreteDomainDefinition)) { return false; }

		final ConcreteDomainDefinition other = (ConcreteDomainDefinition) obj;

		if (!Objects.equals(typeId, other.typeId)) { return false; }
		if (owl2Datatype != other.owl2Datatype) { return false; }
		if (!Objects.equals(serializedValue, other.serializedValue)) { return false; }

		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ConcreteDomainDefinition [typeId=");
		builder.append(typeId);
		builder.append(", owl2Datatype=");
		builder.append(owl2Datatype);
		builder.append(", serializedValue=");
		builder.append(serializedValue);
		builder.append("]");
		return builder.toString();
	}
}
