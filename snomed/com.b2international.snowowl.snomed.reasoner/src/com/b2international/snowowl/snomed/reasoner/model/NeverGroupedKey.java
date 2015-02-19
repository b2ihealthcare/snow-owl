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

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.google.common.collect.Iterables;

/**
 * A singleton {@link DefinitionNodeKey} implementation collecting OWL definitions for never grouped relationships.
 */
public enum NeverGroupedKey implements DefinitionNodeKey {
	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.ontology.DefinitionNodeKey#collect(org.semanticweb.owlapi.model.OWLDataFactory,
	 * org.semanticweb.owlapi.util.DefaultPrefixManager, java.util.List, java.util.Set,
	 * com.b2international.snowowl.snomed.reasoner.ontology.DefinitionNode)
	 */
	@Override public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms,
			final Set<OWLClassExpression> terms, final DefinitionNode definitionNode) {

		collectDirectDefinitions(df, prefixManager, axioms, terms, definitionNode);

		for (final UnionGroupKey unionKey : Iterables.filter(definitionNode.getSubNodeKeys(), UnionGroupKey.class)) {
			unionKey.collect(df, prefixManager, axioms, terms, definitionNode.getSubNode(unionKey));
		}
	}

	private void collectDirectDefinitions(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms,
			final Set<OWLClassExpression> terms, final DefinitionNode definitionNode) {

		for (final RelationshipDefinition definition : definitionNode.getRelationshipDefinitions()) {
			definition.collect(df, prefixManager, axioms, terms);
		}
	}
}