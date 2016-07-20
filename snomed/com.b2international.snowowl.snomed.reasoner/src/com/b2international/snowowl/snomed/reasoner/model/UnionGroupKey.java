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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newHashSet;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * A {@link DefinitionNodeKey} implementation collecting OWL definitions for union groups (both in never grouped
 * relationships and in groups).
 * 
 */
public class UnionGroupKey implements DefinitionNodeKey, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final int number;

	/**
	 * Creates a new {@link UnionGroupKey} instance with the specified arguments.
	 * 
	 * @param number the union group number
	 */
	public UnionGroupKey(final int number) {
		checkArgument(number > 0, "number must be a non-negative value");
		this.number = number;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.ontology.DefinitionNodeKey#collect(org.semanticweb.owlapi.model.OWLDataFactory, 
	 * org.semanticweb.owlapi.util.DefaultPrefixManager, java.util.List, java.util.Set, com.b2international.snowowl.snomed.reasoner.ontology.DefinitionNode)
	 */
	@Override
	public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms, 
			final Set<OWLClassExpression> terms, final DefinitionNode definitionNode) {
		
		final Set<OWLClassExpression> unionExpressions = newHashSet();
		RelationshipDefinition firstDefinition = null;
		
		for (final RelationshipDefinition definition : definitionNode.getRelationshipDefinitions()) {
			if (null == firstDefinition) {
				firstDefinition = definition;
			}
			
			final OWLClassExpression valueExpression = definition.getValueExpression(df, prefixManager);
			unionExpressions.add(valueExpression);
		}
		
		// This will throw an exception if no definitions were set on this node
		final OWLClassExpression simplifiedUnion = SnomedOntologyUtils.simplifyUnionOf(df, unionExpressions); 
		final OWLClassExpression unionGroupExpression = firstDefinition.getRelationshipExpression(df, prefixManager, simplifiedUnion);
		terms.add(unionGroupExpression);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
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
		if (!(obj instanceof UnionGroupKey)) {
			return false;
		}
		final UnionGroupKey other = (UnionGroupKey) obj;
		if (number != other.number) {
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
		builder.append("UnionGroupKey [number=");
		builder.append(number);
		builder.append("]");
		return builder.toString();
	}
}