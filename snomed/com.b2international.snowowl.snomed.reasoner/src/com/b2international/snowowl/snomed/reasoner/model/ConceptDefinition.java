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

import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_CONCEPT;
import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_ROLE_GROUP;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newHashSet;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.ints.IntKeyMap;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Aggregates relationships and concrete domain entries that together form the definition of a concept.
 */
public final class ConceptDefinition implements Serializable {

	private static final long serialVersionUID = 2L;

	private final long conceptId;
	private final boolean primitive;

	private final LongSet classParentIds = PrimitiveSets.newLongOpenHashSet();
	private final IntKeyMap<Set<Definition>> neverGroupedDefinitions = PrimitiveMaps.newIntKeyOpenHashMap();
	private final IntKeyMap<IntKeyMap<Set<Definition>>> groupedDefinitions = PrimitiveMaps.newIntKeyOpenHashMap();

	/**
	 * Creates a new {@link ConceptDefinition} instance with the specified arguments.
	 * 
	 * @param conceptId the concept SCTID
	 * @param primitive {@code true} if the concept is not described in full by its definition, {@code false} otherwise
	 */
	public ConceptDefinition(final long conceptId, final boolean primitive) {
		this.conceptId = conceptId;
		this.primitive = primitive;
	}

	/**
	 * @return the identifier of the concept described in this definition
	 */
	public long getConceptId() {
		return conceptId;
	}

	/**
	 * Registers a parent ID for an ISA relationship, if outside the Concept Model Attribute hierarchy.
	 * 
	 * @param parentId a stated parent SCTID
	 */
	public void addClassParentId(final long parentId) {
		classParentIds.add(parentId);
	}

	/**
	 * Registers a {@link Definition} for a non-ISA relationship/CD
	 * member, which may not participate in a relationship group according to its
	 * type ID.
	 * 
	 * @param definition the definition for a relationship or CD member, which can
	 *                   not participate in a relationship group (may not be
	 *                   {@code null})
	 * @param group      the group identifier for the relationship/CD member (must be 0)
	 * @param unionGroup the union group identifier for the relationship/CD member (may not be negative)
	 */
	public void addNeverGroupedDefinition(final Definition definition, final int group, final int unionGroup) {
		checkArgument(0 == group, "Can't add never grouped relationship definition with non-zero group.");
		checkArgument(0 <= unionGroup, "Union group may not be negative (was %s).", unionGroup);

		final Set<Definition> unionGroupDefinitions = getOrCreate(unionGroup, neverGroupedDefinitions, Sets::newHashSet);
		unionGroupDefinitions.add(definition);
	}

	/**
	 * Registers a {@link Definition} for a non-ISA relationship/CD
	 * member, which may participate in a relationship group.
	 * 
	 * @param definition the relationship definition for a relationship or CD
	 *                   member, which can participate in a relationship group (may
	 *                   not be {@code null})
	 * @param group      the group identifier for the relationship/CD member (may
	 *                   not be negative)
	 * @param unionGroup the union group identifier for the relationship/CD member
	 *                   (may not be negative)
	 */
	public void addGroupDefinition(final Definition definition, final int group, final int unionGroup) {
		checkArgument(0 <= group, "Group may not be negative (was %s).", group);
		checkArgument(0 <= unionGroup, "Union group may not be negative (was %s).", unionGroup);

		final IntKeyMap<Set<Definition>> groupDefinitions = getOrCreate(group, groupedDefinitions, PrimitiveMaps::newIntKeyOpenHashMap);
		final Set<Definition> unionGroupDefinitions = getOrCreate(unionGroup, groupDefinitions, Sets::newHashSet);
		unionGroupDefinitions.add(definition);
	}

	private static <T> T getOrCreate(final int key, final IntKeyMap<T> map, final Supplier<T> valueFactory) {
		T value = map.get(key);

		if (value == null) {
			value = valueFactory.get();
			map.put(key, value);
		}

		return value;
	}

	/**
	 * Computes changes containing the addition of the OWL axiom describing the
	 * concept.
	 * 
	 * @param ontology the ontology to add an axiom to (may not be {@code null})
	 * @return the resulting ontology change, ready to be applied to the ontology
	 */
	public OWLOntologyChange add(final OWLOntology ontology) {
		final OWLAxiom axiom = asOwlAxiom(ontology);
		return asOntologyChange(ontology, axiom, true);
	}

	/**
	 * Computes changes containing the removal of the OWL axiom describing the
	 * concept.
	 * 
	 * @param ontology the ontology to remove an axiom from (may not be {@code null})
	 * @return the resulting ontology change, ready to be applied to the ontology
	 */
	public OWLOntologyChange remove(final OWLOntology ontology) {
		final OWLAxiom axiom = asOwlAxiom(ontology);
		return asOntologyChange(ontology, axiom, false);
	}

	public OWLAxiom asOwlAxiom(final OWLOntology ontology) {
		final OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		final DefaultPrefixManager prefixManager = SnomedOntologyUtils.createPrefixManager(ontology);
		final Set<OWLClassExpression> terms = newHashSet();

		// Parent classes
		for (final LongIterator itr = classParentIds.iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			final OWLClassExpression parentExpression = df.getOWLClass(PREFIX_CONCEPT + parentId, prefixManager); // negation not supported for parents
			terms.add(parentExpression);
		}

		// Never-grouped definitions are added directly 
		collectGroupTerms(df, prefixManager, terms, neverGroupedDefinitions);

		// Grouped definitions are wrapped in a roleGroup existential restriction
		final Set<OWLClassExpression> groupTerms = newHashSet();

		// Add the non-zero (numbered) groups last
		for (final IntIterator itr = groupedDefinitions.keySet().iterator(); itr.hasNext(); /* empty */) {
			final int group = itr.next();
			final IntKeyMap<Set<Definition>> definitions = groupedDefinitions.get(group);
			
			if (group == 0) {
				groupTerms.clear(); // Re-use the set in each iteration
				collectGroupTerms(df, prefixManager, groupTerms, definitions);
				
				// Add definitions to groupTerms as their own role group expression
				groupTerms.forEach(groupTerm -> {
					collectGroupExpression(df, prefixManager, terms, ImmutableSet.of(groupTerm));	
				});
				
			} else {
				groupTerms.clear(); // Re-use the set in each iteration
				collectGroupTerms(df, prefixManager, groupTerms, definitions); // Add _all_ definitions to numbered groups
				collectGroupExpression(df, prefixManager, terms, groupTerms);
			}
		}

		// Holds true for SNOMED CT root concept
		if (terms.isEmpty()) {
			terms.add(df.getOWLThing());
		}

		final OWLClassExpression termsIntersection = SnomedOntologyUtils.simplifyIntersectionOf(df, terms);
		final OWLClass conceptClass = df.getOWLClass(PREFIX_CONCEPT + conceptId, prefixManager);
		final OWLClassAxiom conceptAxiom;

		if (primitive) {
			conceptAxiom = df.getOWLSubClassOfAxiom(conceptClass, termsIntersection);
		} else {
			conceptAxiom = df.getOWLEquivalentClassesAxiom(conceptClass, termsIntersection);
		}

		// XXX: Disjoint union and role inclusion axioms are handled in DelegateOntology
		return conceptAxiom;
	}

	private void collectGroupTerms(final OWLDataFactory df, 
			final DefaultPrefixManager prefixManager,
			final Set<OWLClassExpression> terms, 
			final IntKeyMap<Set<Definition>> definitionsByUnionGroup) {

		for (final IntIterator itr = definitionsByUnionGroup.keySet().iterator(); itr.hasNext(); /* empty */) {
			final int unionGroup = itr.next();
			final Set<Definition> definitions = definitionsByUnionGroup.get(unionGroup);
			
			if (unionGroup == 0) { // Zero-numbered union groups are interpreted on their own
				collectTerms(df, prefixManager, terms, definitions);
			} else {
				collectUnionGroupTerms(df, prefixManager, terms, definitions);
			}
		}
	}

	private void collectTerms(final OWLDataFactory df, 
			final DefaultPrefixManager prefixManager,
			final Set<OWLClassExpression> terms, 
			final Set<Definition> definitions) {

		definitions.forEach(d -> d.collect(df, prefixManager, terms));
	}

	private void collectUnionGroupTerms(final OWLDataFactory df, 
			final DefaultPrefixManager prefixManager,
			final Set<OWLClassExpression> terms, 
			final Set<Definition> unionGroupDefinitions) {

		/* 
		 * FIXME: while not currently enforced in a precondition check, all definitions in a union group 
		 * should be RelationshipDefinitions, and have the same typeId!
		 */

		final Set<OWLClassExpression> unionTerms = newHashSet();
		RelationshipDefinition firstDefinition = null;

		for (final Definition definition : unionGroupDefinitions) {
			if (null == firstDefinition) {
				firstDefinition = (RelationshipDefinition) definition;
			}

			final OWLClassExpression valueExpression = ((RelationshipDefinition) definition).getValueExpression(df, prefixManager);
			unionTerms.add(valueExpression);
		}

		// This will throw an exception if no definitions were set on this node
		final OWLClassExpression simplifiedUnion = SnomedOntologyUtils.simplifyUnionOf(df, unionTerms); 
		final OWLClassExpression unionGroupExpression = firstDefinition.getRelationshipExpression(df, prefixManager, simplifiedUnion);
		terms.add(unionGroupExpression);
	}

	private void collectGroupExpression(final OWLDataFactory df,
			final DefaultPrefixManager prefixManager,
			final Set<OWLClassExpression> terms, 
			final Set<OWLClassExpression> groupTerms) {

		final OWLObjectProperty roleGroup = df.getOWLObjectProperty(PREFIX_ROLE_GROUP, prefixManager);
		final OWLObjectSomeValuesFrom zeroGroupExpression = df.getOWLObjectSomeValuesFrom(roleGroup, SnomedOntologyUtils.simplifyIntersectionOf(df, groupTerms));
		terms.add(zeroGroupExpression);
	}

	private OWLOntologyChange asOntologyChange(final OWLOntology ontology, final OWLAxiom axiom, final boolean addition) {
		return (addition) ? new AddAxiom(ontology, axiom) : new RemoveAxiom(ontology, axiom);
	}

	@Override
	public int hashCode() {
		return Objects.hash(conceptId, 
				primitive,
				classParentIds,
				neverGroupedDefinitions,
				groupedDefinitions);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final ConceptDefinition other = (ConceptDefinition) obj;

		if (conceptId != other.conceptId) { return false; }
		if (primitive != other.primitive) { return false; }
		if (!Objects.equals(classParentIds, other.classParentIds)) { return false; }
		if (!Objects.equals(neverGroupedDefinitions, other.neverGroupedDefinitions)) { return false; }
		if (!Objects.equals(groupedDefinitions, other.groupedDefinitions)) { return false; }

		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ConceptDefinition [conceptId=");
		builder.append(conceptId);
		builder.append(", primitive=");
		builder.append(primitive);
		builder.append(", classParentIds=");
		builder.append(classParentIds);
		builder.append(", neverGroupedDefinitions=");
		builder.append(neverGroupedDefinitions);
		builder.append(", groupedDefinitions=");
		builder.append(groupedDefinitions);
		builder.append("]");
		return builder.toString();
	}
}
