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

import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_ROLE;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * A {@link DefinitionNodeKey} implementation for role inclusion nodes.
 */
public class RoleInclusionKey implements DefinitionNodeKey, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final long conceptId;
	
	/**
	 * Creates a {@link RoleInclusionKey} instance with the specified concept identifier.
	 * @param conceptId
	 */
	public RoleInclusionKey(final long conceptId) {
		this.conceptId = conceptId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.ontology.DefinitionNodeKey#collect(org.semanticweb.owlapi.model.OWLDataFactory, 
	 * org.semanticweb.owlapi.util.DefaultPrefixManager, java.util.List, java.util.Set, com.b2international.snowowl.snomed.reasoner.ontology.DefinitionNode)
	 */
	@Override
	public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms,
			final Set<OWLClassExpression> terms, final DefinitionNode definitionNode) {

		for (final RelationshipDefinition definition : definitionNode.getRelationshipDefinitions()) {
			final OWLObjectProperty subProperty = df.getOWLObjectProperty(PREFIX_ROLE + conceptId, prefixManager);
			final OWLObjectProperty superProperty = df.getOWLObjectProperty(PREFIX_ROLE + definition.getDestinationId(), prefixManager);
			final OWLSubObjectPropertyOfAxiom propertyAxiom = df.getOWLSubObjectPropertyOfAxiom(subProperty, superProperty);
			axioms.add(propertyAxiom);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (conceptId ^ (conceptId >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RoleInclusionKey)) {
			return false;
		}
		final RoleInclusionKey other = (RoleInclusionKey) obj;
		if (conceptId != other.conceptId) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RoleInclusionKey [conceptId=");
		builder.append(conceptId);
		builder.append("]");
		return builder.toString();
	}
}