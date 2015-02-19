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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.google.common.collect.ImmutableSet;

/**
 * Common superclass for {@link ConceptDefinition}s and {@link RelationshipDefinition}s, providing additional {@link ConcreteDomainDefinition}s to
 * their general ontology descriptions.
 */
public abstract class AnnotatedDefinition implements Definition, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Set<ConcreteDomainDefinition> concreteDomainDefinitions;

	/**
	 * Creates a new {@link AnnotatedDefinition} instance with the specified concrete domain definition set.
	 * @param concreteDomainDefinitions the concrete domain definitions to associate this annotated definition with (may not be {@code null})
	 */
	public AnnotatedDefinition(final Set<ConcreteDomainDefinition> concreteDomainDefinitions) {
		checkNotNull(concreteDomainDefinitions, "concreteDomainDefinitions");
		this.concreteDomainDefinitions = ImmutableSet.copyOf(concreteDomainDefinitions);
	}

	protected Set<ConcreteDomainDefinition> getConcreteDomainDefinitions() {
		return concreteDomainDefinitions;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.server.ontology.Collectable#collect(org.semanticweb.owlapi.model.OWLDataFactory,
	 * org.semanticweb.owlapi.util.DefaultPrefixManager, java.util.List, java.util.Set)
	 */
	@Override public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms,
			final Set<OWLClassExpression> terms) {
		for (final ConcreteDomainDefinition definition : concreteDomainDefinitions) {
			definition.collect(df, prefixManager, axioms, terms);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((concreteDomainDefinitions == null) ? 0 : concreteDomainDefinitions.hashCode());
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
		if (!(obj instanceof AnnotatedDefinition)) {
			return false;
		}
		final AnnotatedDefinition other = (AnnotatedDefinition) obj;
		if (concreteDomainDefinitions == null) {
			if (other.concreteDomainDefinitions != null) {
				return false;
			}
		} else if (!concreteDomainDefinitions.equals(other.concreteDomainDefinitions)) {
			return false;
		}
		return true;
	}
}