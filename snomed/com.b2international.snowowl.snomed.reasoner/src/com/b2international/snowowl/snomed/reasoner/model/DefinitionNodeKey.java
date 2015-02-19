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

/**
 * Interface for definition node keys that can provide class expressions and axioms to a concept definition.
 */
public interface DefinitionNodeKey {

	/**
	 * Adds terms to an OWL object intersection which will be part of a concept definition. If the class expressions needs to be described using
	 * equivalence axioms, adds those axioms to the set of collected axioms as well.
	 * @param df the {@link OWLDataFactory} to use for creating OWL objects
	 * @param prefixManager the {@link DefaultPrefixManager} for retrieving fully qualified OWL object identifiers
	 * @param axioms the axioms under collection (out parameter)
	 * @param terms the concept definition terms under collection (out parameter)
	 * @param definitionNode the definition node to process
	 */
	void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms, final Set<OWLClassExpression> terms,
			final DefinitionNode definitionNode);
}