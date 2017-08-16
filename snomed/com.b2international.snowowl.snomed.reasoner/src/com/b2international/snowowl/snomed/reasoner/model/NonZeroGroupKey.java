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
import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_ROLE_GROUP;
import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_ROLE_HAS_MEASUREMENT;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newHashSet;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * A {@link DefinitionNodeKey} implementation collecting OWL definitions for "regular" relationship groups.
 */
public class NonZeroGroupKey implements DefinitionNodeKey, Serializable {

	private static final long serialVersionUID = 1L;

	private final byte number;

	/**
	 * Creates a new {@link NonZeroGroupKey} instance with the specified arguments.
	 * @param number the group number (must be non-negative)
	 */
	public NonZeroGroupKey(final byte number) {
		checkArgument(number > 0, "number must be a non-negative value");
		this.number = number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.b2international.snowowl.snomed.reasoner.ontology.DefinitionNodeKey#collect(org.semanticweb.owlapi.model.OWLDataFactory,
	 * org.semanticweb.owlapi.util.DefaultPrefixManager, java.util.List, java.util.Set,
	 * com.b2international.snowowl.snomed.reasoner.ontology.DefinitionNode)
	 */
	@Override public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms,
			final Set<OWLClassExpression> terms, final DefinitionNode definitionNode) {

		final Set<OWLClassExpression> groupTerms = newHashSet();
		final Set<OWLClassExpression> singleRelationshipTerms = newHashSet();
		OWLClassExpression hasActiveIngredientExpression = null;
		
		for (final RelationshipDefinition definition : definitionNode.getRelationshipDefinitions()) {
			
			singleRelationshipTerms.clear();
			definition.collect(df, prefixManager, axioms, singleRelationshipTerms);
			
			if (LongConcepts.HAS_ACTIVE_INGREDIENT_ID == definition.getTypeId()) {
				if (null != hasActiveIngredientExpression) {
					throw new IllegalStateException(MessageFormat.format("Multiple ''has active ingredient'' relationships were found in group {0}.", number));
				} else {
					final IRI hasActiveIngredientIRI = prefixManager.getIRI(PREFIX_ROLE + Concepts.HAS_ACTIVE_INGREDIENT);
					
					hasActiveIngredientExpression = Iterables.tryFind(singleRelationshipTerms, new Predicate<OWLClassExpression>() {
						@Override
						public boolean apply(OWLClassExpression input) {
							if (!(input instanceof OWLObjectSomeValuesFrom)) {
								return false;
							}
							
							OWLObjectPropertyExpression propertyExpression = ((OWLObjectSomeValuesFrom) input).getProperty();
							
							if (propertyExpression.isAnonymous()) {
								return false;
							}
							
							OWLObjectProperty property = propertyExpression.asOWLObjectProperty();
							IRI propertyIRI = property.getIRI();
							return hasActiveIngredientIRI.equals(propertyIRI);
						}
					}).get();
				}
			}
			
			groupTerms.addAll(singleRelationshipTerms);
		}

		for (final UnionGroupKey unionKey : Iterables.filter(definitionNode.getSubNodeKeys(), UnionGroupKey.class)) {
			unionKey.collect(df, prefixManager, axioms, groupTerms, definitionNode.getSubNode(unionKey));
		}

		addTermsAndAxioms(df, prefixManager, axioms, terms, groupTerms, hasActiveIngredientExpression);
	}

	private void addTermsAndAxioms(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms,
			final Set<OWLClassExpression> terms, final Set<OWLClassExpression> groupTerms, final OWLClassExpression hasActiveIngredientExpression) {

		final OWLObjectProperty role;
		
		/* 
		 * This group represents a measurement if a (originally never grouped) HAI-relationship is present;
		 * we also add this relationship separately to the collection, as if we really found it as a never grouped one. 
		 */
		if (null != hasActiveIngredientExpression) {
			terms.add(hasActiveIngredientExpression);
			role = df.getOWLObjectProperty(PREFIX_ROLE_HAS_MEASUREMENT, prefixManager);
		} else {
			role = df.getOWLObjectProperty(PREFIX_ROLE_GROUP, prefixManager);
		}
		
		final OWLObjectSomeValuesFrom groupExpression = df.getOWLObjectSomeValuesFrom(role, SnomedOntologyUtils.simplifyIntersectionOf(df, groupTerms));
		terms.add(groupExpression);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
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
		if (!(obj instanceof NonZeroGroupKey)) {
			return false;
		}
		final NonZeroGroupKey other = (NonZeroGroupKey) obj;
		if (number != other.number) {
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
		builder.append("NonZeroGroupKey [number=");
		builder.append(number);
		builder.append("]");
		return builder.toString();
	}
}