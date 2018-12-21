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
package com.b2international.snowowl.snomed.reasoner.ontology;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

import java.text.MessageFormat;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitor;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLObjectTypeIndexProvider;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.AbstractLongIterator;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.core.taxonomy.InternalIdMap;
import com.b2international.snowowl.snomed.core.taxonomy.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

/**
 * @since
 */
public final class DelegateOntology extends DelegateOntologyStub implements OWLOntology {

	// The prefix used for SNOMED CT identifiers (also the default)
	private static final String PREFIX_SCT = "sct:";
	private static final String NAMESPACE_SCT = "http://snomed.info/id/";

	// The prefix used for SNOMED CT module ontology identifiers
	private static final String PREFIX_SCTM = "sctm:";
	public static final String NAMESPACE_SCTM = "http://snomed.info/sct/";

	// Prefix for Snow Owl-specific extension IRIs
	private static final String PREFIX_SO = "so:";
	private static final String NAMESPACE_SO = "http://b2i.sg/snowowl/";

	private static final long CONCEPT_MODEL_OBJECT_ATTRIBUTE = 762705008L;
	private static final long CONCEPT_MODEL_DATA_ATTRIBUTE = 762706009L;
	private static final long ROLE_GROUP = 609096000L;

	private static final long PART_OF = 123005000L;
	private static final long LATERALITY = 272741003L;
	private static final long HAS_DOSE_FORM = 411116001L;
	private static final long HAS_ACTIVE_INGREDIENT = 127489000L;
	
	public static final String PREFIX_DATA = PREFIX_SO + "data_";

	private static final LongSet NEVER_GROUPED_TYPE_IDS = PrimitiveSets.newLongOpenHashSet(PART_OF, 
			LATERALITY, 
			HAS_DOSE_FORM, 
			HAS_ACTIVE_INGREDIENT);

	private final class EntityDeclarationAxiomIterator extends AbstractIterator<OWLDeclarationAxiom> {
		private final LongIterator idIterator;
		private final LongFunction<OWLEntity> entityFactory;

		public EntityDeclarationAxiomIterator(final LongIterator idIterator, final LongFunction<OWLEntity> entityFactory) {
			this.idIterator = idIterator;
			this.entityFactory = entityFactory;
		}

		@Override
		protected OWLDeclarationAxiom computeNext() {
			if (!idIterator.hasNext()) {
				return endOfData();
			}

			final long conceptId = idIterator.next();
			final OWLEntity entity = entityFactory.apply(conceptId);
			return getOWLDeclarationAxiom(entity);
		}
	}

	private final class ConceptAxiomIterator<T extends OWLClassAxiom> extends AbstractIterator<T> {
		private final LongIterator idIterator;
		private final LongPredicate idPredicate;
		private final BiFunction<OWLClassExpression, OWLClassExpression, T> classAxiomFactory;

		public ConceptAxiomIterator(final LongIterator idIterator,
				final LongPredicate idPredicate, 
				final BiFunction<OWLClassExpression, OWLClassExpression, T> classAxiomFactory) {

			this.idIterator = idIterator;
			this.idPredicate = idPredicate;
			this.classAxiomFactory = classAxiomFactory;
		}

		@Override
		protected T computeNext() {
			final long conceptId = nextApplicableConceptId();
			if (conceptId == -1L) {
				return endOfData();
			}

			final OWLClass conceptClass = getConceptClass(conceptId);
			final Set<OWLClassExpression> conceptDefinitionExpression = getConceptDefinitionExpression(conceptId);
			
			if (!conceptDefinitionExpression.isEmpty()) {
				return classAxiomFactory.apply(conceptClass, getOWLObjectIntersectionOf(conceptDefinitionExpression));
			} else {
				// TODO: Filter concepts that don't produce a superclass expression in advance (SNOMED CT Root is a known one)
				return classAxiomFactory.apply(conceptClass, getOWLThing());
			}
		}

		private long nextApplicableConceptId() {
			while (idIterator.hasNext()) {
				final long conceptId = idIterator.next();
				if (idPredicate.test(conceptId)) {
					return conceptId;
				}
			}

			return -1L;
		}
	}

	private final class SubPropertyOfAxiomIterator<P extends OWLPropertyExpression, A extends OWLSubPropertyAxiom<P>> extends AbstractIterator<A> {
		private final LongIterator childIterator;
		private final LongFunction<P> propertyFactory;
		private final BiFunction<P, P, A> subPropertyAxiomFactory;

		private long childId = -1L;
		private LongIterator parentIterator;

		public SubPropertyOfAxiomIterator(final LongIterator childIterator,
				final LongFunction<P> propertyFactory,
				final BiFunction<P, P, A> subPropertyAxiomFactory) {

			this.childIterator = childIterator;
			this.propertyFactory = propertyFactory;
			this.subPropertyAxiomFactory = subPropertyAxiomFactory;
		}

		@Override
		protected A computeNext() {
			if (computeNextApplicableChildId()) {
				return endOfData();
			}

			final long parentId = parentIterator.next();
			final P childProperty = propertyFactory.apply(childId);
			final P parentProperty = propertyFactory.apply(parentId);
			return subPropertyAxiomFactory.apply(childProperty, parentProperty);	
		}

		private boolean computeNextApplicableChildId() {
			// Current ID is good if there are more parents to iterate over
			if (parentIterator != null && parentIterator.hasNext()) {
				return true;
			}

			// Otherwise, return the next child which has parents
			while (childIterator.hasNext()) {
				childId = childIterator.next();
				parentIterator = taxonomy.getStatedAncestors()
						.getDestinations(childId, true)
						.iterator();

				if (parentIterator.hasNext()) {
					return true;
				}
			}

			return false;
		}
	}

	private final class ConcreteDomainAttributeIterator extends AbstractIterator<OWLDeclarationAxiom> {
		private final Iterator<Long> attributeNameIterator;

		private ConcreteDomainAttributeIterator(final Iterator<Long> attributeNameIterator) {
			this.attributeNameIterator = attributeNameIterator;
		}

		@Override
		protected OWLDeclarationAxiom computeNext() {
			if (!attributeNameIterator.hasNext()) {
				return endOfData();
			}

			final Long typeId = attributeNameIterator.next();
			final OWLDataProperty attributeProperty = getDataFactory().getOWLDataProperty(PREFIX_DATA + typeId, prefixManager);
			return getOWLDeclarationAxiom(attributeProperty);
		}
	}

	private final class DisjointUnionAxiomIterator extends AbstractIterator<OWLDisjointUnionAxiom> {
		private final LongIterator idIterator;

		public DisjointUnionAxiomIterator(final LongIterator idIterator) {
			this.idIterator = idIterator;
		}

		@Override
		protected OWLDisjointUnionAxiom computeNext() {
			if (!idIterator.hasNext()) {
				return endOfData();
			}

			final long parentId = idIterator.next();
			final OWLClass parentClass = getConceptClass(parentId);
			final LongSet directChildIds = taxonomy.getStatedDescendants()
					.getDestinations(parentId, true);

			final Set<OWLClass> childClasses = newHashSet();

			for (final LongIterator itr = directChildIds.iterator(); itr.hasNext(); /*empty*/) {
				final long childId = itr.next();
				final OWLClass childClass = getConceptClass(childId);
				childClasses.add(childClass);
			}

			return getOWLDisjointUnionAxiom(parentClass, childClasses);
		}
	}

	/**
	 * Creates a {@link DefaultPrefixManager} instance for the specified ontology.
	 * Prefixes will be configured in accordance with the OWL reference set
	 * specification, with the exception of the default prefix.
	 * 
	 * @see <a href="https://docs.google.com/document/d/1jn9jiWe2cKNA5WrbQZ2MGWV_VoKU6Una6ZpQLskeRXA/">OWL
	 *      Reference Sets Specification (draft)</a>
	 * @return the created prefix manager
	 */
	private static DefaultPrefixManager createPrefixManager() {
		final DefaultPrefixManager prefixManager = new DefaultPrefixManager();
		// prefixManager.setDefaultPrefix(NAMESPACE_SCT);
		prefixManager.setPrefix(PREFIX_SCT, NAMESPACE_SCT);
		prefixManager.setPrefix(PREFIX_SCTM, NAMESPACE_SCTM);
		prefixManager.setPrefix(PREFIX_SO, NAMESPACE_SO);
		return prefixManager;
	}

	private final OWLOntologyManager manager;
	private final OWLOntologyID ontologyID;
	private final ReasonerTaxonomy taxonomy;

	private final DefaultPrefixManager prefixManager;

	public DelegateOntology(final OWLOntologyManager manager, 
			final OWLOntologyID ontologyID, 
			final ReasonerTaxonomy taxonomy) {

		this.manager = manager;
		this.ontologyID = ontologyID;
		this.taxonomy = taxonomy;

		this.prefixManager = createPrefixManager();
	}

	@Override
	protected int index() {
		return OWLObjectTypeIndexProvider.ONTOLOGY;
	}

	@Override
	protected int compareObjectOfSameType(final OWLObject object) {
		if (object == this) {
			return 0;
		}
		final OWLOntology other = (OWLOntology) object;
		return ontologyID.compareTo(other.getOntologyID());
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("DelegateOntology(");
		sb.append(ontologyID);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public OWLOntologyManager getOWLOntologyManager() {
		return manager;
	}

	@Override
	public OWLOntologyID getOntologyID() {
		return ontologyID;
	}

	@Override
	public boolean isAnonymous() {
		return ontologyID.isAnonymous();
	}

	@Override
	public Set<OWLOntology> getImportsClosure() {
		return newHashSet(this);
	}

	@Override
	public final void accept(final OWLNamedObjectVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public final <O> O accept(final OWLNamedObjectVisitorEx<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final void accept(final OWLObjectVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public final <O> O accept(final OWLObjectVisitorEx<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public Set<OWLAxiom> getAxioms() {
		return new AbstractSet<OWLAxiom>() {
			@Override
			@SuppressWarnings("unchecked")
			public Iterator<OWLAxiom> iterator() {
				return Iterators.concat(conceptDeclarationAxioms(),
						conceptSubClassOfAxioms(),
						conceptEquivalentClassesAxioms(),
						objectAttributeDeclarationAxioms(),
						objectAttributeSubPropertyOfAxioms(),
						dataAttributeDeclarationAxioms(),
						dataAttributeSubPropertyOfAxioms(),
						concreteDomainAttributeAxioms(),
						disjointUnionAxioms());
			}

			@Override
			public int size() {
				return getAxiomCount();
			}
		};
	}

	private Iterator<OWLDeclarationAxiom> conceptDeclarationAxioms() {
		return new EntityDeclarationAxiomIterator(conceptIdIterator(), this::getConceptClass);
	}

	private Iterator<OWLSubClassOfAxiom> conceptSubClassOfAxioms() {
		return new ConceptAxiomIterator<>(conceptIdIterator(),
				conceptId -> !taxonomy.getFullyDefinedConcepts()
				.contains(conceptId), 
				this::getOWLSubClassOfAxiom);
	}

	private Iterator<OWLEquivalentClassesAxiom> conceptEquivalentClassesAxioms() {
		return new ConceptAxiomIterator<>(conceptIdIterator(),
				conceptId -> taxonomy.getFullyDefinedConcepts()
				.contains(conceptId), 
				this::getOWLEquivalentClassesAxiom);
	}

	private Iterator<? extends OWLAxiom> objectAttributeDeclarationAxioms() {
		return new EntityDeclarationAxiomIterator(objectAttributeIdIterator(), this::getConceptObjectProperty);
	}

	private Iterator<? extends OWLAxiom> objectAttributeSubPropertyOfAxioms() {
		return new SubPropertyOfAxiomIterator<>(objectAttributeIdIterator(), this::getConceptObjectProperty, this::getOWLSubObjectPropertyOfAxiom);
	}

	private Iterator<? extends OWLAxiom> dataAttributeDeclarationAxioms() {
		return new EntityDeclarationAxiomIterator(dataAttributeIdIterator(), this::getConceptDataProperty);
	}

	private Iterator<? extends OWLAxiom> dataAttributeSubPropertyOfAxioms() {
		return new SubPropertyOfAxiomIterator<>(dataAttributeIdIterator(), this::getConceptDataProperty, this::getOWLSubDataPropertyOfAxiom);
	}

	private Iterator<OWLDeclarationAxiom> concreteDomainAttributeAxioms() {
		return new ConcreteDomainAttributeIterator(concreteDomainLabelIterator());
	}

	private Iterator<OWLDisjointUnionAxiom> disjointUnionAxioms() {
		return new DisjointUnionAxiomIterator(exhaustiveIdIterator());
	}

	private LongIterator conceptIdIterator() {
		return new AbstractLongIterator() {
			private final LongIterator delegate = taxonomy.getConceptMap().getSctIds();

			@Override
			protected long computeNext() {
				while (delegate.hasNext()) {
					final long candidate = delegate.next();
					final LongSet allAncestors = taxonomy.getStatedAncestors()
							.getDestinations(candidate, false);

					if (!allAncestors.contains(CONCEPT_MODEL_OBJECT_ATTRIBUTE) 
							&& !allAncestors.contains(CONCEPT_MODEL_DATA_ATTRIBUTE)) {
						return candidate;
					}
				}

				return endOfData();
			}
		};
	}

	private LongIterator objectAttributeIdIterator() {
		return getConceptAndSubTypes(CONCEPT_MODEL_OBJECT_ATTRIBUTE).iterator();
	}

	private LongIterator dataAttributeIdIterator() {
		return getConceptAndSubTypes(CONCEPT_MODEL_DATA_ATTRIBUTE).iterator();
	}

	private Iterator<Long> concreteDomainLabelIterator() {
		return taxonomy.getStatedConcreteDomainMembers()
				.values()
				.stream()
				.map(ConcreteDomainFragment::getTypeId)
				.distinct()
				.iterator();
	}

	private LongIterator exhaustiveIdIterator() {
		return taxonomy.getExhaustiveConcepts().iterator();
	}

	@Override
	public int getAxiomCount() {
		return 2 * conceptCount()
				+ objectAttributeCount()
				+ objectHierarchyCount()
				+ dataAttributeCount()
				+ dataHierarchyCount()
				+ concreteDomainAttributeCount()
				+ disjointUnionCount()
				+ additionalAxiomCount();
	}

	private int conceptCount() {
		return taxonomy.getConceptMap().size()
				- objectAttributeCount()
				- dataAttributeCount();
	}

	private int objectAttributeCount() {
		return getConceptAndSubTypesCount(CONCEPT_MODEL_OBJECT_ATTRIBUTE);
	}

	private int objectHierarchyCount() {
		return hierarchyCount(CONCEPT_MODEL_OBJECT_ATTRIBUTE);
	}

	private int dataAttributeCount() {
		return getConceptAndSubTypesCount(CONCEPT_MODEL_DATA_ATTRIBUTE);
	}

	private int dataHierarchyCount() {
		return hierarchyCount(CONCEPT_MODEL_DATA_ATTRIBUTE);
	}

	private int concreteDomainAttributeCount() {
		return Ints.checkedCast(taxonomy.getStatedConcreteDomainMembers()
				.values()
				.stream()
				.map(ConcreteDomainFragment::getTypeId)
				.distinct()
				.count());
	}

	private int disjointUnionCount() {
		return taxonomy.getExhaustiveConcepts().size();
	}

	private int additionalAxiomCount() {
		// See additionalAxioms() for the source of "3" 
		return 3;
	}

	private int getConceptAndSubTypesCount(final long ancestorId) {
		if (taxonomy.getConceptMap().getInternalId(ancestorId) != InternalIdMap.NO_INTERNAL_ID) {
			return taxonomy.getStatedDescendants()
					.getDestinations(ancestorId, false)
					.size() + 1;
		} else {
			return 0;
		}
	}

	private LongSet getConceptAndSubTypes(final long ancestorId) {
		final LongSet conceptAndSubTypes = PrimitiveSets.newLongOpenHashSet();

		if (taxonomy.getConceptMap().getInternalId(ancestorId) != InternalIdMap.NO_INTERNAL_ID) {
			conceptAndSubTypes.add(ancestorId);
			conceptAndSubTypes.addAll(taxonomy.getStatedDescendants()
					.getDestinations(ancestorId, false));
		}

		return conceptAndSubTypes;
	}

	private int hierarchyCount(final long ancestorId) {
		int parentsCount = 0;

		if (taxonomy.getConceptMap().getInternalId(ancestorId) != InternalIdMap.NO_INTERNAL_ID) {
			for (final LongIterator itr = getConceptAndSubTypes(ancestorId).iterator(); itr.hasNext(); /* empty */) {
				final int parents = taxonomy.getStatedAncestors()
						.getDestinations(itr.next(), true)
						.size();

				parentsCount += parents;
			}
		}

		return parentsCount;
	}

	private OWLDataFactory getDataFactory() {
		return manager.getOWLDataFactory();
	}

	public long getConceptId(final OWLClass conceptClass) {
		final IRI iri = conceptClass.getIRI();
		if (iri.toString().startsWith(NAMESPACE_SCT)) {
			return Long.parseLong(iri.getShortForm()); 
		} else {
			return -1L;
		}
	}

	public OWLClass getConceptClass(final long conceptId) {
		return getDataFactory().getOWLClass(PREFIX_SCT + conceptId, prefixManager);
	}

	public OWLClassExpression getOWLThing() {
		return getDataFactory().getOWLThing();
	}

	private OWLObjectProperty getConceptObjectProperty(final long conceptId) {
		return getOWLObjectProperty(PREFIX_SCT + conceptId);
	}

	private OWLDataProperty getConceptDataProperty(final long conceptId) {
		return getDataFactory().getOWLDataProperty(PREFIX_SCT + conceptId, prefixManager);
	}

	private OWLQuantifiedObjectRestriction getRelationshipExpression(final long typeId, final long destinationId, final boolean destinationNegated, final boolean universal) {
		final OWLClass destinationClass = getConceptClass(destinationId);
		final OWLClassExpression filler = destinationNegated 
				? getOWLObjectComplementOf(destinationClass) 
						: destinationClass;

				return getRelationshipExpression(typeId, filler, universal);
	}

	private OWLQuantifiedObjectRestriction getRelationshipExpression(final long typeId, final OWLClassExpression filler, final boolean universal) {
		final OWLObjectProperty property = getConceptObjectProperty(typeId);

		if (universal) {
			return getDataFactory().getOWLObjectAllValuesFrom(property, filler);
		} else {
			return getDataFactory().getOWLObjectSomeValuesFrom(property, filler);
		}
	}

	private OWLObjectSomeValuesFrom getRoleGroupExpression(final OWLClassExpression filler) {
		final OWLObjectProperty property = getConceptObjectProperty(ROLE_GROUP);
		return getDataFactory().getOWLObjectSomeValuesFrom(property, filler);
	}

	private Set<OWLClassExpression> getConceptDefinitionExpression(final long conceptId) {
		final Set<OWLClassExpression> intersection = Sets.newHashSet();
		final LongSet superTypeIds = taxonomy.getStatedAncestors()
				.getDestinations(conceptId, true);

		for (final LongIterator itr = superTypeIds.iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			addParent(parentId, intersection);
		}

		final Collection<StatementFragment> statedNonIsAFragments = taxonomy.getStatedNonIsARelationships()
				.get(conceptId);

		statedNonIsAFragments.stream()
			.filter(r -> isNeverGrouped(r))
			.collect(Collectors.groupingBy(StatementFragment::getUnionGroup))
			.entrySet()
			.stream()
			.forEachOrdered(ug -> addUnionGroup(ug, intersection));

		statedNonIsAFragments.stream()
			.filter(r -> !isNeverGrouped(r))
			.collect(Collectors.groupingBy(StatementFragment::getGroup))
			.entrySet()
			.stream()
			.forEachOrdered(g -> addGroup(conceptId, g, intersection));

		final Collection<ConcreteDomainFragment> conceptConcreteDomainFragments = taxonomy.getStatedConcreteDomainMembers()
				.get(Long.toString(conceptId));

		conceptConcreteDomainFragments.stream()
			.forEach(c -> addConcreteDomainMember(c, intersection));

		return intersection;
	}

	private void addParent(final long parentId, final Set<OWLClassExpression> intersection) {
		final OWLClass parentClass = getConceptClass(parentId);
		intersection.add(parentClass);
	}

	private boolean isNeverGrouped(final StatementFragment r) {
		return NEVER_GROUPED_TYPE_IDS.contains(r.getTypeId()) && r.getGroup() == 0;
	}

	private boolean isActiveIngredient(final StatementFragment r) {
		return HAS_ACTIVE_INGREDIENT == r.getTypeId() && r.getUnionGroup() == 0;
	}

	private void addUnionGroup(final Entry<Integer, List<StatementFragment>> unionGroup, final Set<OWLClassExpression> intersection) {
		if (unionGroup.getKey() > 0) {
			final long commonTypeId = unionGroup.getValue().get(0).getTypeId();
			final boolean isUniversal = unionGroup.getValue().get(0).isUniversal();

			checkState(unionGroup.getValue()
					.stream()
					.allMatch(r -> commonTypeId == r.getTypeId() && isUniversal == r.isUniversal()));

			final Set<OWLClassExpression> unionGroupDisjuncts = unionGroup.getValue()
					.stream()
					.map(ugr -> getConceptClass(ugr.getDestinationId()))
					.collect(Collectors.toSet());

			final OWLClassExpression unionGroupExpression = getOWLObjectUnionOf(unionGroupDisjuncts);
			final OWLQuantifiedObjectRestriction relationshipExpression = getRelationshipExpression(commonTypeId, unionGroupExpression, isUniversal);
			intersection.add(relationshipExpression);
		} else {
			unionGroup.getValue()
			.stream()
			.map(ugr -> getRelationshipExpression(ugr.getTypeId(), 
					ugr.getDestinationId(), 
					ugr.isDestinationNegated(), 
					ugr.isUniversal()))
			.forEachOrdered(intersection::add);
		}

		// Add relationship-referenced concrete domain members alongside the relationships
		unionGroup.getValue()
			.stream()
			.flatMap(r -> taxonomy.getStatedConcreteDomainMembers()
					.get(Long.toString(r.getStatementId()))
					.stream())
			.forEachOrdered(m -> addConcreteDomainMember(m, intersection));
	}

	private void addGroup(final long conceptId, final Entry<Integer, List<StatementFragment>> group, final Set<OWLClassExpression> intersection) {
		final Set<OWLClassExpression> groupIntersection = Sets.newHashSet();

		group.getValue()
			.stream()
			.collect(Collectors.groupingBy(StatementFragment::getUnionGroup))
			.entrySet()
			.stream()
			.forEachOrdered(ug -> addUnionGroup(ug, groupIntersection));

		if (group.getKey() > 0) {

			final Set<StatementFragment> activeIngredientRelationships = group.getValue()
					.stream()
					.filter(r -> isActiveIngredient(r))
					.collect(Collectors.toSet());


			if (activeIngredientRelationships.size() > 1) {
				throw new IllegalStateException(String.format("Multiple 'has active ingredient' relationships were found in group %s of concept %s.", group.getKey(), conceptId));
			} else if (activeIngredientRelationships.size() == 1) {
				// Add it in never-grouped form to the outer intersection 
				final StatementFragment r = Iterables.getOnlyElement(activeIngredientRelationships);
				intersection.add(getRelationshipExpression(r.getTypeId(), r.getDestinationId(), r.isDestinationNegated(), r.isUniversal()));
			}

			intersection.add(getRoleGroupExpression(getOWLObjectIntersectionOf(groupIntersection)));

		} else {
			groupIntersection.forEach(ce -> {
				final OWLClassExpression singleGroupExpression = getRoleGroupExpression(ce);
				intersection.add(singleGroupExpression);
			});
		}
	}

	private void addConcreteDomainMember(final ConcreteDomainFragment member, final Set<OWLClassExpression> intersection) {
		final long typeId = member.getTypeId();
		final String serializedValue = member.getSerializedValue();
		final DataType sctDataType = member.getDataType();

		final OWL2Datatype owl2Datatype = getOWL2Datatype(sctDataType);
		
		final OWLDataProperty dataProperty = getDataFactory().getOWLDataProperty(PREFIX_DATA + typeId, prefixManager);
		final OWLLiteral valueLiteral = getDataFactory().getOWLLiteral(serializedValue, owl2Datatype);
		final OWLDataHasValue dataExpression = getDataFactory().getOWLDataHasValue(dataProperty, valueLiteral);

		intersection.add(dataExpression);
	}

	private OWL2Datatype getOWL2Datatype(final DataType dataType) {
		switch (dataType) {
		case BOOLEAN: return OWL2Datatype.XSD_BOOLEAN;
		case DATE: return OWL2Datatype.XSD_DATE_TIME;
		case DECIMAL: return OWL2Datatype.XSD_DECIMAL;
		case INTEGER: return OWL2Datatype.XSD_INT;
		case STRING: return OWL2Datatype.RDF_PLAIN_LITERAL;
		default: throw new IllegalStateException(MessageFormat.format("Unhandled datatype enum ''{0}''.", dataType));
		}
	}

	private OWLObjectProperty getOWLObjectProperty(final String abbreviatedIRI) {
		return getDataFactory().getOWLObjectProperty(abbreviatedIRI, prefixManager);
	}

	private OWLClassExpression getOWLObjectIntersectionOf(final Set<OWLClassExpression> conjuncts) {
		if (conjuncts.size() > 1) {
			return getDataFactory().getOWLObjectIntersectionOf(conjuncts);
		} else {
			return Iterables.getOnlyElement(conjuncts);
		}
	}

	private OWLClassExpression getOWLObjectUnionOf(final Set<OWLClassExpression> disjuncts) {
		if (disjuncts.size() > 1) {
			return getDataFactory().getOWLObjectUnionOf(disjuncts);
		} else {
			return Iterables.getOnlyElement(disjuncts);
		}
	}

	private OWLClassExpression getOWLObjectComplementOf(final OWLClass owlClass) {
		return getDataFactory().getOWLObjectComplementOf(owlClass);
	}

	private OWLDeclarationAxiom getOWLDeclarationAxiom(final OWLEntity e) {
		return getDataFactory().getOWLDeclarationAxiom(e);
	}

	private OWLSubClassOfAxiom getOWLSubClassOfAxiom(final OWLClassExpression child, final OWLClassExpression parent) {
		return getDataFactory().getOWLSubClassOfAxiom(child, parent);
	}

	private OWLEquivalentClassesAxiom getOWLEquivalentClassesAxiom(final OWLClassExpression... expressions) {
		return getDataFactory().getOWLEquivalentClassesAxiom(expressions);
	}

	private OWLDisjointUnionAxiom getOWLDisjointUnionAxiom(final OWLClass parent, final Set<OWLClass> children) {
		return getDataFactory().getOWLDisjointUnionAxiom(parent, children);
	}

	private OWLSubObjectPropertyOfAxiom getOWLSubObjectPropertyOfAxiom(final OWLObjectPropertyExpression child, final OWLObjectPropertyExpression parent) {
		return getDataFactory().getOWLSubObjectPropertyOfAxiom(child, parent);
	}

	private OWLSubDataPropertyOfAxiom getOWLSubDataPropertyOfAxiom(final OWLDataPropertyExpression child, final OWLDataPropertyExpression parent) {
		return getDataFactory().getOWLSubDataPropertyOfAxiom(child, parent);
	}
}
