/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Sets.newHashSet;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;

/**
 * Aggregates relationships and concrete domain entries that together form the definition of a concept.
 */
public final class ConceptDefinition extends AnnotatedDefinition implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long conceptId;

	private final boolean primitive;

	private final LongSet disjointUnionIds;

	private final Node rootNode = new Node();

	/**
	 * Creates a new {@link ConceptDefinition} instance with the specified arguments.
	 * @param concreteDomainDefinitions the concrete domain definitions to associate this annotated definition with (may not be {@code null})
	 * @param conceptId the concept identifier
	 * @param primitive {@code true} if the concept is not described in full by its definition, {@code false} otherwise
	 * @param disjointUnionIds the concept's direct children which form a disjoint union (if any)
	 * @param disjointSubstances "has active ingredient"-typed relationship destination IDs
	 */
	public ConceptDefinition(final Set<ConcreteDomainDefinition> concreteDomainDefinitions, final long conceptId, final boolean primitive,
			final LongSet disjointUnionIds) {

		super(concreteDomainDefinitions);
		this.conceptId = conceptId;
		this.primitive = primitive;
		this.disjointUnionIds = disjointUnionIds;
	}

	/**
	 * @return the identifier of the concept described in this definition
	 */
	public long getConceptId() {
		return conceptId;
	}

	/**
	 * Registers a {@link RelationshipDefinition} for an ISA relationship.
	 * @param definition the relationship definition for an ISA relationship to add (may not be {@code null})
	 */
	public void addIsaDefinition(final RelationshipDefinition definition) {
		rootNode.getSubNode(IsaKey.INSTANCE).addRelationshipDefinition(definition);
	}

	/**
	 * Registers a {@link RelationshipDefinition} for an ISA relationship representing a role inclusion.
	 * @param definition the relationship definition for an ISA relationship that refers to a concept model attribute to add (may not be {@code null})
	 */
	public void addRoleInclusionDefinition(final RelationshipDefinition definition) {
		rootNode.getSubNode(new RoleInclusionKey(conceptId)).addRelationshipDefinition(definition);
	}

	/**
	 * Registers a {@link RelationshipDefinition} for a non-ISA relationship which may not participate in a relationship group.
	 * @param definition the relationship definition for a relationship which can not participate in a relationship group to add (may not be
	 *            {@code null})
	 * @param group the group identifier for the relationship (must be 0)
	 * @param unionGroup the union group identifier for the relationship (may not be negative)
	 */
	public void addNeverGroupedDefinition(final RelationshipDefinition definition, final int group, final int unionGroup) {
		checkArgument(0 == group, "Can't add never grouped relationship definition with non-zero group.");
		checkArgument(0 <= unionGroup, "Union group may not be negative (was %s).", unionGroup);

		DefinitionNode targetNode = rootNode.getSubNode(NeverGroupedKey.INSTANCE);
		targetNode = getUnionGroupSubNode(unionGroup, targetNode);
		targetNode.addRelationshipDefinition(definition);
	}

	/**
	 * Registers a {@link RelationshipDefinition} for a non-ISA relationship which may participate in a relationship group.
	 * @param definition the relationship definition for a relationship which can participate in a relationship group to add (may not be {@code null})
	 * @param group the group identifier for the relationship (may not be negative)
	 * @param unionGroup the union group identifier for the relationship (may not be negative)
	 */
	public void addGroupDefinition(final RelationshipDefinition definition, final int group, final int unionGroup) {
		checkArgument(0 <= group, "Group may not be negative (was %s).", group);
		checkArgument(0 <= unionGroup, "Union group may not be negative (was %s).", unionGroup);

		DefinitionNode targetNode = getGroupSubNode(definition, group, rootNode);
		targetNode = getUnionGroupSubNode(unionGroup, targetNode);
		targetNode.addRelationshipDefinition(definition);
	}

	private DefinitionNode getGroupSubNode(final RelationshipDefinition definition, final int group, final Node targetNode) {
		return (0 < group) ? targetNode.getSubNode(new NonZeroGroupKey(group)) : targetNode.getSubNode(ZeroGroupKey.INSTANCE);
	}

	private DefinitionNode getUnionGroupSubNode(final int unionGroup, final DefinitionNode targetNode) {
		return (0 < unionGroup) ? targetNode.getSubNode(new UnionGroupKey(unionGroup)) : targetNode;
	}

	/**
	 * Computes changes containing the addition of the OWL axiom describing the concept as well as other supporting axioms to the specified ontology.
	 * @param ontology the ontology to add axioms to (may not be {@code null})
	 * @return the resulting ontology changes, ready to be applied to the ontology
	 */
	public List<OWLOntologyChange> add(final OWLOntology ontology) {
		final List<OWLAxiom> axioms = collect(ontology);
		return convertAxioms(ontology, axioms, true);
	}

	/**
	 * Computes changes containing the removal of the OWL axiom describing the concept as well as other supporting axioms to the specified ontology.
	 * @param ontology the ontology to add axioms to (may not be {@code null})
	 * @return the resulting ontology changes, ready to be applied to the ontology
	 */
	public List<OWLOntologyChange> remove(final OWLOntology ontology) {
		final List<OWLAxiom> axioms = collect(ontology);
		return convertAxioms(ontology, axioms, false);
	}

	public List<OWLAxiom> collect(final OWLOntology ontology) {
		final OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		final DefaultPrefixManager prefixManager = SnomedOntologyUtils.createPrefixManager(ontology);
		final List<OWLAxiom> axioms = newArrayList();
		final Set<OWLClassExpression> terms = newHashSet();
		collect(df, prefixManager, axioms, terms);
		return axioms;
	}

	private List<OWLOntologyChange> convertAxioms(final OWLOntology ontology, final List<OWLAxiom> axioms, final boolean addition) {
		final List<OWLOntologyChange> changes = newArrayListWithExpectedSize(axioms.size());
		for (final OWLAxiom axiom : axioms) {
			changes.add(createAxiomChange(ontology, addition, axiom));
		}
		return changes;
	}

	private OWLAxiomChange createAxiomChange(final OWLOntology ontology, final boolean addition, final OWLAxiom axiom) {
		return (addition) ? new AddAxiom(ontology, axiom) : new RemoveAxiom(ontology, axiom);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.server.ontology.AnnotatedDefinition#collect(org.semanticweb.owlapi.model.OWLDataFactory,
	 * org.semanticweb.owlapi.util.DefaultPrefixManager, java.util.List, java.util.Set)
	 */
	@Override public void collect(final OWLDataFactory df, final DefaultPrefixManager prefixManager, final List<OWLAxiom> axioms,
			final Set<OWLClassExpression> terms) {
		rootNode.collect(df, prefixManager, axioms, terms); // Collects relationship definitions
		super.collect(df, prefixManager, axioms, terms); // Collects concrete domain definitions on the concept

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

		axioms.add(conceptAxiom);

		if (null != disjointUnionIds && !disjointUnionIds.isEmpty()) {
			final Set<OWLClass> disjointUnionClasses = newHashSet();
			for (final LongIterator itr = disjointUnionIds.iterator(); itr.hasNext(); /* empty */) {
				final long disjointUnionId = itr.next();
				final OWLClass disjointUnionMember = df.getOWLClass(PREFIX_CONCEPT + disjointUnionId, prefixManager);
				disjointUnionClasses.add(disjointUnionMember);
			}

			final OWLClassAxiom disjointUnionAxiom = df.getOWLDisjointUnionAxiom(conceptClass, disjointUnionClasses);
			axioms.add(disjointUnionAxiom);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.ontology.AnnotatedDefinition#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (conceptId ^ (conceptId >>> 32));
		result = prime * result + (primitive ? 1231 : 1237);
		result = prime * result + ((rootNode == null) ? 0 : rootNode.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.ontology.AnnotatedDefinition#equals(java.lang.Object)
	 */
	@Override public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ConceptDefinition)) {
			return false;
		}
		final ConceptDefinition other = (ConceptDefinition) obj;
		if (conceptId != other.conceptId) {
			return false;
		}
		if (primitive != other.primitive) {
			return false;
		}
		if (rootNode == null) {
			if (other.rootNode != null) {
				return false;
			}
		} else if (!rootNode.equals(other.rootNode)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.ontology.AnnotatedDefinition#toString()
	 */
	@Override public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ConceptDefinition [conceptId=");
		builder.append(conceptId);
		builder.append(", primitive=");
		builder.append(primitive);
		builder.append(", rootNode=");
		builder.append(rootNode);
		builder.append(", getConcreteDomainDefinitions()=");
		builder.append(getConcreteDomainDefinitions());
		builder.append("]");
		return builder.toString();
	}

	public DefinitionNode getNeverGroupedNodes() {
		return rootNode.getSubNode(NeverGroupedKey.INSTANCE);
	}
	
	public DefinitionNode getZeroGroupNodes() {
		return rootNode.getSubNode(ZeroGroupKey.INSTANCE);
	}

	public Set<NonZeroGroupKey> getGroupedNodes() {
		return rootNode.getSubNodeKeys()
				.stream()
				.filter(NonZeroGroupKey.class::isInstance)
				.map(NonZeroGroupKey.class::cast)
				.collect(Collectors.toSet());
	}
	
	public DefinitionNode getSubNode(DefinitionNodeKey key) {
		return rootNode.getSubNode(key);
	}
}
