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

import org.eclipse.net4j.util.StringUtil;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
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
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.datastore.server.snomed.index.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * @since
 */
public final class DelegateOntology extends DelegateOntologyStub implements OWLOntology {

	// The prefix used for SNOMED CT identifiers (also the default)
	private static final String PREFIX_SCT = "sct:";
	private static final String NAMESPACE_SCT = "http://snomed.info/id/";

	// The prefix used for SNOMED CT module ontology identifiers
	private static final String PREFIX_SCTM = "sctm:";
	/*package*/ static final String NAMESPACE_SCTM = "http://snomed.info/sct/";

	// Prefix for Snow Owl-specific extension IRIs
	private static final String PREFIX_SO = "so:";
	private static final String NAMESPACE_SO = "http://b2i.sg/snowowl/";

	private static final String PREFIX_HAS_UNIT = "so:has_unit";
	private static final String PREFIX_HAS_VALUE = "so:has_value";
	private static final String PREFIX_HAS_MEASUREMENT = "so:has_measurement";
	private static final String PREFIX_LABEL = "so:label_";

	private static final long CONCEPT_MODEL_OBJECT_ATTRIBUTE = 762705008L;
	private static final long CONCEPT_MODEL_DATA_ATTRIBUTE = 762706009L;
	private static final long ROLE_GROUP = 609096000L;
	private static final long DEFAULT_UNIT = 258666001L; // root of the unit (qualifier) hierarchy

	private static final long PART_OF = 123005000L;
	private static final long LATERALITY = 272741003L;
	private static final long HAS_DOSE_FORM = 411116001L;
	private static final long HAS_ACTIVE_INGREDIENT = 127489000L;

	private static final LongSet NEVER_GROUPED_TYPE_IDS = PrimitiveSets.newLongOpenHashSet(PART_OF, 
			LATERALITY, 
			HAS_DOSE_FORM, 
			HAS_ACTIVE_INGREDIENT);

	private static final String[] FIND = new String[] {"%", "(", ")" };
	private static final String[] REPLACE = new String[] { "%25", "%28", "%29" };

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
			final OWLClassExpression conceptDefinitionExpression = getConceptDefinitionExpression(conceptId);
			return classAxiomFactory.apply(conceptClass, conceptDefinitionExpression);
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
				parentIterator = taxonomyBuilder.getSuperTypeIds(childId).iterator();

				if (parentIterator.hasNext()) {
					return true;
				}
			}

			return false;
		}
	}

	private final class ConcreteDomainAttributeIterator extends AbstractIterator<OWLDeclarationAxiom> {
		private final Iterator<String> attributeNameIterator;

		private ConcreteDomainAttributeIterator(final Iterator<String> attributeNameIterator) {
			this.attributeNameIterator = attributeNameIterator;
		}

		@Override
		protected OWLDeclarationAxiom computeNext() {
			if (!attributeNameIterator.hasNext()) {
				return endOfData();
			}

			final String attributeName = attributeNameIterator.next();
			final OWLObjectProperty attributeProperty = getDataFactory().getOWLObjectProperty(PREFIX_LABEL + sanitize(attributeName), prefixManager);
			return getOWLDeclarationAxiom(attributeProperty);
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

	// Do limited percent-encoding on incoming text as parentheses may confuse functional OWL parsers
	private static String sanitize(final String text) {
		return StringUtil.replace(text, FIND, REPLACE);
	}

	private final OWLOntologyManager manager;
	private final OWLOntologyID ontologyID;
	private final ReasonerTaxonomyBuilder taxonomyBuilder;

	private final DefaultPrefixManager prefixManager;
	private final LongSet nonMetadataConceptIds;

	public DelegateOntology(final OWLOntologyManager manager, 
			final OWLOntologyID ontologyID, 
			final ReasonerTaxonomyBuilder taxonomyBuilder) {

		this.manager = manager;
		this.ontologyID = ontologyID;
		this.taxonomyBuilder = taxonomyBuilder;

		this.prefixManager = createPrefixManager();

		this.nonMetadataConceptIds = PrimitiveSets.newLongOpenHashSet(taxonomyBuilder.getConceptIdSet());
		nonMetadataConceptIds.removeAll(getConceptAndSubTypes(CONCEPT_MODEL_OBJECT_ATTRIBUTE));
		nonMetadataConceptIds.removeAll(getConceptAndSubTypes(CONCEPT_MODEL_DATA_ATTRIBUTE));
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
	public Set<OWLAxiom> getAxioms() {
		return new AbstractSet<OWLAxiom>() {
			@Override
			public Iterator<OWLAxiom> iterator() {
				return Iterators.concat(conceptDeclarationAxioms(),
						conceptSubClassOfAxioms(),
						conceptEquivalentClassesAxioms(),
						objectAttributeDeclarationAxioms(),
						objectAttributeSubPropertyOfAxioms(),
						dataAttributeDeclarationAxioms(),
						dataAttributeSubPropertyOfAxioms(),
						concreteDomainAttributeAxioms(),
						additionalAxioms());
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
				conceptId -> taxonomyBuilder.isPrimitive(conceptId), 
				this::getOWLSubClassOfAxiom);
	}

	private Iterator<OWLEquivalentClassesAxiom> conceptEquivalentClassesAxioms() {
		return new ConceptAxiomIterator<>(conceptIdIterator(),
				conceptId -> !taxonomyBuilder.isPrimitive(conceptId), 
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

	private Iterator<OWLDeclarationAxiom> additionalAxioms() {
		return ImmutableList.of(getOWLDeclarationAxiom(getOWLObjectProperty(PREFIX_HAS_MEASUREMENT)),
				getOWLDeclarationAxiom(getOWLObjectProperty(PREFIX_HAS_UNIT)),
				getOWLDeclarationAxiom(getOWLObjectProperty(PREFIX_HAS_VALUE)))
				.iterator();
	}

	private LongIterator conceptIdIterator() {
		return nonMetadataConceptIds.iterator();
	}

	private LongIterator objectAttributeIdIterator() {
		return getConceptAndSubTypes(CONCEPT_MODEL_OBJECT_ATTRIBUTE).iterator();
	}

	private LongIterator dataAttributeIdIterator() {
		return getConceptAndSubTypes(CONCEPT_MODEL_DATA_ATTRIBUTE).iterator();
	}

	private Iterator<String> concreteDomainLabelIterator() {
		return taxonomyBuilder.getAllStatedConcreteDomainLabels().iterator();
	}

	@Override
	public int getAxiomCount() {
		return 2 * conceptCount()
				+ objectAttributeCount()
				+ objectHierarchyCount()
				+ dataAttributeCount()
				+ dataHierarchyCount()
				+ concreteDomainAttributeCount()
				+ additionalAxiomCount();
	}

	private int conceptCount() {
		return taxonomyBuilder.getConceptIdSet().size()
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
		return taxonomyBuilder.getAllStatedConcreteDomainLabels().size();
	}

	private int additionalAxiomCount() {
		// See additionalAxioms() for the source of "3" 
		return 3;
	}

	private int getConceptAndSubTypesCount(final long ancestorId) {
		if (taxonomyBuilder.isActive(ancestorId)) {
			return taxonomyBuilder.getAllSubTypesIds(ancestorId).size() + 1;
		} else {
			return 0;
		}
	}

	private LongSet getConceptAndSubTypes(final long ancestorId) {
		final LongSet conceptAndSubTypes = PrimitiveSets.newLongOpenHashSet();

		if (taxonomyBuilder.isActive(ancestorId)) {
			conceptAndSubTypes.add(ancestorId);
			conceptAndSubTypes.addAll(taxonomyBuilder.getAllSubTypesIds(ancestorId));
		}

		return conceptAndSubTypes;
	}

	private int hierarchyCount(final long ancestorId) {
		int parentsCount = 0;

		if (taxonomyBuilder.isActive(ancestorId)) {
			for (final LongIterator itr = getConceptAndSubTypes(ancestorId).iterator(); itr.hasNext(); /* empty */) {
				final int parents = taxonomyBuilder.getSuperTypeIds(itr.next()).size();
				parentsCount += parents;
			}
		}

		return parentsCount;
	}

	private OWLDataFactory getDataFactory() {
		return manager.getOWLDataFactory();
	}

	private OWLClass getConceptClass(final long conceptId) {
		return getDataFactory().getOWLClass(PREFIX_SCT + conceptId, prefixManager);
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

	private OWLClassExpression getMeasurementExpression(final OWLClassExpression filler) {
		final OWLObjectProperty property = getDataFactory().getOWLObjectProperty(PREFIX_HAS_MEASUREMENT, prefixManager);
		return getDataFactory().getOWLObjectSomeValuesFrom(property, filler);
	}

	private OWLClassExpression getConceptDefinitionExpression(final long conceptId) {
		final Set<OWLClassExpression> intersection = Sets.newHashSet();

		for (final LongIterator itr = taxonomyBuilder.getSuperTypeIds(conceptId).iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			addParent(parentId, intersection);
		}

		final Collection<StatementFragment> statedNonIsAFragments = taxonomyBuilder.getStatedNonIsAFragments(conceptId);

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

		final Collection<ConcreteDomainFragment> conceptConcreteDomainFragments = taxonomyBuilder.getStatedConcreteDomainFragments(conceptId);

		conceptConcreteDomainFragments.stream()
		.forEach(c -> addConcreteDomainMember(c, intersection));

		return getOWLObjectIntersectionOf(intersection);
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
		.flatMap(r -> taxonomyBuilder.getStatedConcreteDomainFragments(r.getStatementId()).stream())
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

			final OWLClassExpression groupExpression;

			if (activeIngredientRelationships.size() > 1) {
				throw new IllegalStateException(String.format("Multiple 'has active ingredient' relationships were found in group %s of concept %s.", group.getKey(), conceptId));
			} else if (activeIngredientRelationships.size() == 1) {
				// Add it in never-grouped form to the outer intersection 
				final StatementFragment r = Iterables.getOnlyElement(activeIngredientRelationships);
				intersection.add(getRelationshipExpression(r.getTypeId(), r.getDestinationId(), r.isDestinationNegated(), r.isUniversal()));
				groupExpression = getMeasurementExpression(getOWLObjectIntersectionOf(groupIntersection));
			} else {
				groupExpression = getRoleGroupExpression(getOWLObjectIntersectionOf(groupIntersection));
			}

			intersection.add(groupExpression);

		} else {
			groupIntersection.forEach(ce -> {
				final OWLClassExpression singleGroupExpression = getRoleGroupExpression(ce);
				intersection.add(singleGroupExpression);
			});
		}
	}

	private void addConcreteDomainMember(final ConcreteDomainFragment member, final Set<OWLClassExpression> intersection) {
		final long uomId = member.getUomId();
		final String attributeName = member.getLabel();
		final String serializedValue = member.getValue();
		final DataType sctDataType = member.getDataType();

		final OWL2Datatype owl2Datatype = getOWL2Datatype(sctDataType);

		final OWLClassExpression hasUnitExpression = createHasUnitExpression(uomId);
		final OWLClassExpression hasValueExpression = createHasValueExpression(owl2Datatype, serializedValue);
		final OWLClassExpression unitAndValueExpression = getOWLObjectIntersectionOf(ImmutableSet.of(hasUnitExpression, hasValueExpression));
		final OWLObjectProperty attributeProperty = getDataFactory().getOWLObjectProperty(PREFIX_LABEL + sanitize(attributeName), prefixManager);
		// TODO: add declaration axiom for all object properties declared above 
		final OWLClassExpression concreteDomainExpression = getDataFactory().getOWLObjectSomeValuesFrom(attributeProperty, unitAndValueExpression);

		intersection.add(concreteDomainExpression);
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

	private OWLClassExpression createHasUnitExpression(long uomId) {
		if (uomId == -1L) {
			uomId = DEFAULT_UNIT;
		}

		final OWLObjectProperty hasUnitProperty = getDataFactory().getOWLObjectProperty(PREFIX_HAS_UNIT, prefixManager);
		final OWLClassExpression hasUnitExpression = getDataFactory().getOWLObjectSomeValuesFrom(hasUnitProperty, getConceptClass(uomId));
		return hasUnitExpression;
	}

	private OWLClassExpression createHasValueExpression(final OWL2Datatype dataType, final String serializedValue) {
		/*
		 * TODO: Replace when reasoners get full OWL2 datatype support
		 *
		 * final OWLDataProperty hasValueProperty = df.getOWLDataProperty(PREFIX_DATA_HAS_VALUE, prefixManager);
		 * final OWLLiteral value = df.getOWLLiteral(literal, datatype);
		 * final OWLDataHasValue hasValueExpression = df.getOWLDataHasValue(hasValueProperty, value);
		 */
		final OWLObjectProperty hasValueProperty = getDataFactory().getOWLObjectProperty(PREFIX_HAS_VALUE, prefixManager);
		final OWLClass value = getDataFactory().getOWLClass(PREFIX_SO + sanitize(serializedValue) + "_" + dataType.getShortForm(), prefixManager);
		final OWLClassExpression hasValueExpression = getDataFactory().getOWLObjectSomeValuesFrom(hasValueProperty, value);
		return hasValueExpression;
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

	private OWLSubObjectPropertyOfAxiom getOWLSubObjectPropertyOfAxiom(final OWLObjectPropertyExpression child, final OWLObjectPropertyExpression parent) {
		return getDataFactory().getOWLSubObjectPropertyOfAxiom(child, parent);
	}

	private OWLSubDataPropertyOfAxiom getOWLSubDataPropertyOfAxiom(final OWLDataPropertyExpression child, final OWLDataPropertyExpression parent) {
		return getDataFactory().getOWLSubDataPropertyOfAxiom(child, parent);
	}
}
