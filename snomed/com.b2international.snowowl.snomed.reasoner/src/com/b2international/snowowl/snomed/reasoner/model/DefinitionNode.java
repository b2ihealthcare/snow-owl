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

import static com.google.common.collect.Sets.newHashSet;

import java.io.Serializable;
import java.util.Set;

/**
 * Represents a composite node of the definition which carries a set of {@link RelationshipDefinition}s as well as optional sub-{@link DefinitionNode}s.
 */
public final class DefinitionNode extends Node implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Set<RelationshipDefinition> relationshipDefinitions = newHashSet();

	/**
	 * Registers a {@link RelationshipDefinition} on this node.
	 * @param relationshipDefinition the relationship definition to add (may not be {@code null})
	 */
	public void addRelationshipDefinition(final RelationshipDefinition relationshipDefinition) {
		relationshipDefinitions.add(relationshipDefinition);
	}

	/**
	 * @return the set of stored relationship definitions
	 */
	public Set<RelationshipDefinition> getRelationshipDefinitions() {
		return relationshipDefinitions;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.server.ontology.Node#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((relationshipDefinitions == null) ? 0 : relationshipDefinitions.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.server.ontology.Node#equals(java.lang.Object)
	 */
	@Override public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof DefinitionNode)) {
			return false;
		}
		final DefinitionNode other = (DefinitionNode) obj;
		if (relationshipDefinitions == null) {
			if (other.relationshipDefinitions != null) {
				return false;
			}
		} else if (!relationshipDefinitions.equals(other.relationshipDefinitions)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.server.ontology.Node#toString()
	 */
	@Override public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DefinitionNode [relationshipDefinitions=");
		builder.append(relationshipDefinitions);
		builder.append(", getSubNodes()=");
		builder.append(getSubNodes());
		builder.append("]");
		return builder.toString();
	}
}