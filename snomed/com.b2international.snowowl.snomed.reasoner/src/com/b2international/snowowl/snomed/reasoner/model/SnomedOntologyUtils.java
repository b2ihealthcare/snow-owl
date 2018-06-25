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
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.google.common.collect.Iterables;

/**
 * Utility class that holds OWL object identifier prefixes as well as methods for creating class expressions for object intersections and unions.
 * 
 * @since 
 */
public abstract class SnomedOntologyUtils {

	



	/**
	 * Creates an {@link OWLObjectUnionOf} expression for the passed in class
	 * expression set, or returns a single expression if the set has only one
	 * element.
	 * 
	 * @param df    the {@link OWLDataFactory} to use for creating OWL objects
	 * @param terms the set of terms to convert
	 * @return the converted class expression
	 */
	public static OWLClassExpression simplifyUnionOf(final OWLDataFactory df, final Set<OWLClassExpression> terms) {
		if (terms.size() > 1) {
			return df.getOWLObjectUnionOf(terms);
		} else {
			return Iterables.getOnlyElement(terms);
		}
	}

	private SnomedOntologyUtils() {
		// Prevent instantiation
	}
}
