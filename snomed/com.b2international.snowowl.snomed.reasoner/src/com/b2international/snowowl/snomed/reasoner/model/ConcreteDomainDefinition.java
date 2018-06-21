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
package com.b2international.snowowl.snomed.reasoner.model;

import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.*;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import org.eclipse.net4j.util.StringUtil;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;

/**
 * Represents a short version of a concrete domain reference set member for ontology definitions.
 */
public final class ConcreteDomainDefinition implements Definition, Serializable {

	private static final String[] FIND = new String[] {"%", "(", ")" };

	private static final String[] REPLACE = new String[] { "%25", "%28", "%29" };

	private static final long serialVersionUID = 1L;

	private static final String UTF_8 = "UTF-8";

	private final String label;
	private final long unitId;
	private final String literal;
	private final OWL2Datatype datatype;

	/**
	 * Creates a new {@link ConcreteDomainDefinition} based on an existing {@link ConcreteDomainFragment}.
	 * @param fragment the fragment to gather information from (may not be {@code null})
	 */
	public ConcreteDomainDefinition(final ConcreteDomainFragment fragment) {
		this(fragment.getLabel(), fragment.getUomId(), fragment.getValue(),
				ConcreteDomainDefinition.getOWL2Datatype(fragment.getDataType()));
	}

	/**
	 * Creates a new {@link ConcreteDomainDefinition} instance with the specified arguments.
	 * @param label the camelCased label of the definition (may not be {@code null})
	 * @param unitId the concept identifier of the UOM concept, or {@link #NOT_APPLICABLE} if no unit is set for this definition
	 * @param literal the serialized value literal of the concrete domain definition
	 * @param datatype the datatype associated with the definition
	 */
	public ConcreteDomainDefinition(final String label, final long unitId, final String literal, final OWL2Datatype datatype) {
		this.label = label;
		this.unitId = unitId;
		try {
			this.literal = URLEncoder.encode(literal, UTF_8);
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Cannot encode literal: '" + literal + "'.", e);
		}
		this.datatype = datatype;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((datatype == null) ? 0 : datatype.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((literal == null) ? 0 : literal.hashCode());
		result = prime * result + (int) (unitId ^ (unitId >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ConcreteDomainDefinition)) {
			return false;
		}
		final ConcreteDomainDefinition other = (ConcreteDomainDefinition) obj;
		if (datatype != other.datatype) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (literal == null) {
			if (other.literal != null) {
				return false;
			}
		} else if (!literal.equals(other.literal)) {
			return false;
		}
		if (unitId != other.unitId) {
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
		builder.append("ConcreteDomainDefinition [label=");
		builder.append(label);
		builder.append(", unitId=");
		builder.append(unitId);
		builder.append(", literal=");
		builder.append(literal);
		builder.append(", datatype=");
		builder.append(datatype);
		builder.append("]");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.server.ontology.Definition#collect(org.semanticweb.owlapi.model.OWLDataFactory,
	 * org.semanticweb.owlapi.util.DefaultPrefixManager, java.util.List, java.util.Set)
	 */
	@Override public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms,
			final Set<OWLClassExpression> terms) {
		final OWLClassExpression hasUnitExpression = createHasUnitExpression(df, prefixManager);
		final OWLClassExpression hasValueExpression = createHasValueExpression(df, prefixManager);
		final OWLClassExpression unitAndValueExpression = df.getOWLObjectIntersectionOf(hasUnitExpression, hasValueExpression);
		final OWLObjectProperty attributeProperty = df.getOWLObjectProperty(PREFIX_LABEL + sanitize(label), prefixManager);
		final OWLClassExpression concreteDomainExpression = df.getOWLObjectSomeValuesFrom(attributeProperty, unitAndValueExpression);

		terms.add(concreteDomainExpression);
	}

	private String sanitize(final String text) {
		// Do limited percent-encoding on incoming text as parentheses may confuse functional OWL parsers
		return StringUtil.replace(text, FIND, REPLACE);
	}

	private OWLClassExpression createHasUnitExpression(final OWLDataFactory df, final DefaultPrefixManager prefixManager) {
		final OWLClass uomClass;
		if (LongConcepts.NOT_APPLICABLE_ID == unitId) {
			uomClass = df.getOWLClass(PREFIX_CONCEPT_UNIT_NOT_APPLICABLE, prefixManager);
		} else {
			uomClass = df.getOWLClass(PREFIX_CONCEPT + unitId, prefixManager);
		}

		final OWLObjectProperty hasUnitProperty = df.getOWLObjectProperty(PREFIX_HAS_UNIT, prefixManager);
		final OWLClassExpression hasUnitExpression = df.getOWLObjectSomeValuesFrom(hasUnitProperty, uomClass);
		return hasUnitExpression;
	}

	private OWLClassExpression createHasValueExpression(final OWLDataFactory df, final DefaultPrefixManager prefixManager) {
		/*
		 * TODO: Replace when reasoners get full OWL2 datatype support
		 *
		 * final OWLDataProperty hasValueProperty = df.getOWLDataProperty(PREFIX_DATA_HAS_VALUE, prefixManager);
		 * final OWLLiteral value = df.getOWLLiteral(literal, datatype);
		 * final OWLDataHasValue hasValueExpression = df.getOWLDataHasValue(hasValueProperty, value);
		 */
		final OWLObjectProperty hasValueProperty = df.getOWLObjectProperty(PREFIX_DATA_HAS_VALUE, prefixManager);
		final OWLClass value = df.getOWLClass(PREFIX_SNOMED + sanitize(literal) + "_" + datatype.getShortName(), prefixManager);
		final OWLClassExpression hasValueExpression = df.getOWLObjectSomeValuesFrom(hasValueProperty, value);
		return hasValueExpression;
	}

	public static OWL2Datatype getOWL2Datatype(final DataType dataType) {
		switch (dataType) {
		case BOOLEAN:
			return OWL2Datatype.XSD_BOOLEAN;
		case DATE:
			return OWL2Datatype.XSD_DATE_TIME;
		case DECIMAL:
			return OWL2Datatype.XSD_DECIMAL;
		case INTEGER:
			return OWL2Datatype.XSD_INT;
		case STRING:
			return OWL2Datatype.RDF_PLAIN_LITERAL;
		default:
			throw new IllegalStateException(MessageFormat.format("Unhandled datatype enum ''{0}''.", dataType));
		}
	}
}