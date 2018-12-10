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

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Interface for definitions that can provide class expressions and axioms to a concept definition.
 */
public interface Definition {

	/**
	 * Adds terms to an OWL object intersection which will be part of a concept
	 * definition. If the class expressions needs to be described using equivalence
	 * axioms, adds those axioms to the set of collected axioms as well.
	 * 
	 * @param df            the {@link OWLDataFactory} to use for creating OWL objects
	 * @param prefixManager the {@link DefaultPrefixManager} for retrieving fully qualified OWL object identifiers
	 * @param terms         the concept definition terms under collection (out parameter)
	 */
	void collect(OWLDataFactory df, DefaultPrefixManager prefixManager, Set<OWLClassExpression> terms);
}
