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

import static com.google.common.collect.Maps.newHashMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Represents the root node of the concept definition, capable of storing sub-nodes keyed by {@link DefinitionNodeKey}s.
 */
public class Node implements Definition, Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<DefinitionNodeKey, DefinitionNode> subNodes = newHashMap();

	protected Map<DefinitionNodeKey, DefinitionNode> getSubNodes() {
		return subNodes;
	}

	/**
	 * @return the set of available {@link DefinitionNodeKey}s
	 */
	public Set<DefinitionNodeKey> getSubNodeKeys() {
		return subNodes.keySet();
	}

	/**
	 * Retrieves a sub-node with the specified key, or creates a new one if it didn't exist.
	 * @param key the search definition key (may not be {@code null})
	 * @return the sub-node corresponding to the search key
	 */
	public DefinitionNode getSubNode(final DefinitionNodeKey key) {
		return subNodes.containsKey(key) ? subNodes.get(key) : createSubNode(key);
	}

	private DefinitionNode createSubNode(final DefinitionNodeKey key) {
		final DefinitionNode node = new DefinitionNode();
		subNodes.put(key, node);
		return node;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.server.ontology.Collectable#collect(org.semanticweb.owlapi.model.OWLDataFactory,
	 * org.semanticweb.owlapi.util.DefaultPrefixManager, java.util.List, java.util.Set)
	 */
	@Override public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms,
			final Set<OWLClassExpression> terms) {

		for (final DefinitionNodeKey subNodeKey : subNodes.keySet()) {
			subNodeKey.collect(df, prefixManager, axioms, terms, subNodes.get(subNodeKey));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subNodes == null) ? 0 : subNodes.hashCode());
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
		if (!(obj instanceof Node)) {
			return false;
		}
		final Node other = (Node) obj;
		if (subNodes == null) {
			if (other.subNodes != null) {
				return false;
			}
		} else if (!subNodes.equals(other.subNodes)) {
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
		builder.append("Node [subNodes=");
		builder.append(subNodes);
		builder.append("]");
		return builder.toString();
	}
}