/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.net4j.util.StringUtil;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 7.0
 */
public final class OntologyBuilder {

	private static final String[] FIND = new String[] {"%", "(", ")" };
	private static final String[] REPLACE = new String[] { "%25", "%28", "%29" };

	private static final String CONCEPT_MODEL_OBJECT_ATTRIBUTE = "762705008";
	private static final String CONCEPT_MODEL_DATA_ATTRIBUTE = "762706009";

	private static final String ROLE_GROUP_ID = "609096000";
	private static final Set<String> NEVER_GROUPED_TYPE_IDS = ImmutableSet.of(Concepts.PART_OF, 
			Concepts.LATERALITY, 
			Concepts.HAS_DOSE_FORM, 
			Concepts.HAS_ACTIVE_INGREDIENT);

	// The prefix used for SNOMED CT identifiers (also the default)
	private static final String PREFIX_SCT = "sct:";
	private static final String NAMESPACE_SCT = "http://snomed.info/id/";

	// The prefix used for SNOMED CT module ontology identifiers
	private static final String PREFIX_SCTM = "sctm:";
	private static final String NAMESPACE_SCTM = "http://snomed.info/sct/";

	// Prefix for Snow Owl-specific extension IRIs
	private static final String PREFIX_SO = "so:";
	private static final String NAMESPACE_SO = "http://b2i.sg/snowowl/";

	private static final String PREFIX_HAS_UNIT = "so:has_unit";
	private static final String PREFIX_HAS_VALUE = "so:has_value";
	private static final String PREFIX_LABEL = "so:label_";
	private static final String PREFIX_MEASUREMENT = "so:measurement";
	private static final String PREFIX_CANONICAL = "so:canonical_";

	private final OWLOntology ontology;
	private final DefaultPrefixManager prefixManager;
	private final boolean supportConcreteDomains;

	private final BiMap<OWLClassExpression, OWLClass> canonicalRepresentations;

	/**
	 * Creates a {@link DefaultPrefixManager} instance for the specified ontology.
	 * Prefixes will be configured in accordance with the OWL refset specification (draft)
	 * 
	 * @return the created prefix manager
	 */
	private static DefaultPrefixManager createPrefixManager() {
		final DefaultPrefixManager prefixManager = new DefaultPrefixManager();
		prefixManager.setDefaultPrefix(NAMESPACE_SCT);
		prefixManager.setPrefix(PREFIX_SCT, NAMESPACE_SCT);
		prefixManager.setPrefix(PREFIX_SCTM, NAMESPACE_SCTM);
		prefixManager.setPrefix(PREFIX_SO, NAMESPACE_SO);
		return prefixManager;
	}

	public OntologyBuilder(final String ontologyModuleId, final String ontologyVersionId, final boolean supportConcreteDomains) throws OWLOntologyCreationException {
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

		final Optional<IRI> ontologyIRI = Optional.of(IRI.create(
				String.format("%s%s", NAMESPACE_SCTM, ontologyModuleId)));
		final Optional<IRI> versionIRI = Optional.fromNullable(ontologyVersionId)
				.transform(v -> String.format("%s%s/version/%s", NAMESPACE_SCTM, ontologyModuleId, v))
				.transform(IRI::create);

		final OWLOntologyID ontologyID = new OWLOntologyID(ontologyIRI, versionIRI);

		this.ontology = ontologyManager.createOntology(ontologyID);
		this.prefixManager = createPrefixManager();
		this.supportConcreteDomains = supportConcreteDomains;
		this.canonicalRepresentations = HashBiMap.create();
	}

	public void build(final BranchContext context, final List<SnomedConcept> additionalDefinitions, final IProgressMonitor monitor) {
		addAxiom(getOWLDeclarationAxiom(getDataFactory().getOWLObjectProperty(PREFIX_HAS_UNIT, prefixManager)));
		addAxiom(getOWLDeclarationAxiom(getDataFactory().getOWLObjectProperty(PREFIX_HAS_VALUE, prefixManager)));
		addAxiom(getOWLDeclarationAxiom(getDataFactory().getOWLObjectProperty(PREFIX_MEASUREMENT, prefixManager)));

		addOWLClassAxioms(context, additionalDefinitions, CONCEPT_MODEL_OBJECT_ATTRIBUTE, CONCEPT_MODEL_DATA_ATTRIBUTE);
		addOWLObjectPropertyAxioms(context, CONCEPT_MODEL_OBJECT_ATTRIBUTE);
		addOWLObjectPropertyAxioms(context, CONCEPT_MODEL_DATA_ATTRIBUTE);
	}

	private void addOWLClassAxioms(final BranchContext context, 
			final List<SnomedConcept> additionalDefinitions,
			final String... propertyRootIds) {
		
		final Map<String, SnomedConcept> additionalConceptsById = Maps.uniqueIndex(additionalDefinitions, SnomedConcept::getId);
		final Set<String> emptySet = ImmutableSet.of(); 
		
		for (final SnomedConcept concept : additionalConceptsById.values()) {
			addConceptAxioms(concept, emptySet, propertyRootIds);
		}
		
		final SnomedConceptSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByActive(true)
				.setExpand(getExpandString())
				.setScroll("15m")
				.setLimit(10_000);

		final SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> searchIterator = new SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts>(
				requestBuilder, b -> b.build().execute(context));

		final Set<String> idsToIgnore = additionalConceptsById.keySet();
		
		searchIterator.forEachRemaining(chunk -> {
			for (final SnomedConcept concept : chunk) {
				addConceptAxioms(concept, idsToIgnore, propertyRootIds);
			}
		});
	}

	private void addConceptAxioms(final SnomedConcept concept, Set<String> idsToIgnore, final String... propertyRootIds) {
		if (idsToIgnore.contains(concept.getId())) {
			return;
		}
		
		if (isProperty(concept, propertyRootIds)) {
			return;
		}

		final String conceptId = concept.getId();
		final Set<OWLClassExpression> intersection = Sets.newHashSet();

		concept.getRelationships()
				.stream()
				.filter(r -> isParentRelationship(r))
				.map(SnomedRelationship::getDestinationId)
				.forEachOrdered(p -> addParent(p, intersection));

		concept.getRelationships()
				.stream()
				.filter(r -> isNeverGroupedRelationship(r))
				.collect(Collectors.groupingBy(SnomedRelationship::getUnionGroup))
				.entrySet()
				.stream()
				.forEachOrdered(ug -> addUnionGroup(ug, intersection, true));

		concept.getRelationships()
				.stream()
				.filter(r -> isGroupedRelationship(r))
				.collect(Collectors.groupingBy(SnomedRelationship::getGroup))
				.entrySet()
				.stream()
				.forEachOrdered(g -> addGroup(conceptId, g, intersection));

		if (supportConcreteDomains) {
			concept.getMembers()
					.stream()
					.filter(m -> isConcreteDomainMember(m))
					.forEachOrdered(m -> addConcreteDomainMember(m, intersection));
		}

		final OWLClass conceptClass = getOWLClass(conceptId);
		addAxiom(getOWLDeclarationAxiom(conceptClass));

		if (concept.getDefinitionStatus().isPrimitive()) {
			addAxiom(getOWLSubClassOfAxiom(conceptClass, getOWLObjectIntersectionOf(intersection)));
		} else {
			addAxiom(getOWLEquivalentClassesAxiom(conceptClass, getOWLObjectIntersectionOf(intersection)));
		}
	}

	private String getExpandString() {
		if (supportConcreteDomains) {
			return String.format("members(active:true,refSetType:\"%s\"),"
					+ "relationships(active:true,characteristicType:\"%s\",expand(members(active:true,refSetType:\"%s\")))", 
					SnomedRefSetType.CONCRETE_DATA_TYPE.name(),
					Concepts.STATED_RELATIONSHIP,
					SnomedRefSetType.CONCRETE_DATA_TYPE.name());
		} else {
			return String.format("relationships(active:true,characteristicType:\"%s\")", Concepts.STATED_RELATIONSHIP);
		}
	}

	private boolean isProperty(final SnomedConcept concept, final String... propertyRootIds) {
		final LongSet ancestorSet = PrimitiveSets.newLongOpenHashSet(concept.getStatedAncestorIds());
		final LongSet parentSet = PrimitiveSets.newLongOpenHashSet(concept.getStatedParentIds());

		for (final String propertyRootId : propertyRootIds) {
			if (concept.getId().equals(propertyRootId)) { return true; }

			final long longRootId = Long.parseLong(propertyRootId);
			if (parentSet.contains(longRootId)) { return true; }
			if (ancestorSet.contains(longRootId)) { return true; }
		}

		return false;
	}

	private boolean isParentRelationship(final SnomedRelationship r) {
		return r.isActive() 
				&& Concepts.IS_A.equals(r.getTypeId())
				&& CharacteristicType.STATED_RELATIONSHIP.equals(r.getCharacteristicType());
	}

	private boolean isNeverGroupedRelationship(final SnomedRelationship r) {
		return r.isActive() 
				&& NEVER_GROUPED_TYPE_IDS.contains(r.getTypeId()) 
				&& r.getGroup() == 0
				&& CharacteristicType.STATED_RELATIONSHIP.equals(r.getCharacteristicType());
	}

	private boolean isGroupedRelationship(final SnomedRelationship r) {
		/*
		 * The relationship is either:
		 * 
		 * - "not-never grouped", in which case the group number doesn't matter
		 * - never grouped, and it is in a non-zero group
		 */
		return r.isActive() 
				&& (!NEVER_GROUPED_TYPE_IDS.contains(r.getTypeId()) || r.getGroup() > 0)
				&& !Concepts.IS_A.equals(r.getTypeId()) 
				&& CharacteristicType.STATED_RELATIONSHIP.equals(r.getCharacteristicType());
	}

	private boolean isConcreteDomainMember(final SnomedReferenceSetMember m) {
		return m.isActive() 
				&& SnomedRefSetUtil.isConcreteDomain(m.getReferenceSetId()) 
				&& Concepts.STATED_RELATIONSHIP.equals(m.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID));
	}

	private boolean isActiveIngredientRelationship(final SnomedRelationship r) {
		return r.isActive() 
				&& Concepts.HAS_ACTIVE_INGREDIENT.equals(r.getTypeId())
				&& r.getUnionGroup() == 0
				&& CharacteristicType.STATED_RELATIONSHIP.equals(r.getCharacteristicType());
	}

	private void addParent(final String parentId, final Set<OWLClassExpression> intersection) {
		final OWLClass parentClass = getOWLClass(parentId);
		intersection.add(parentClass);
	}

	private void addUnionGroup(final Entry<Integer, List<SnomedRelationship>> unionGroup, final Set<OWLClassExpression> intersection, final boolean useCanonicalForm) {
		if (unionGroup.getKey() > 0) {
			final String commonTypeId = unionGroup.getValue().get(0).getTypeId();
			final RelationshipModifier commonModifier = unionGroup.getValue().get(0).getModifier();

			checkState(unionGroup.getValue()
					.stream()
					.allMatch(r -> commonTypeId.equals(r.getTypeId()) && commonModifier.equals(r.getModifier())));

			final Set<OWLClassExpression> unionGroupDisjuncts = unionGroup.getValue()
					.stream()
					.map(ugr -> getOWLClass(ugr.getDestinationId()))
					.collect(Collectors.toSet());

			final OWLClassExpression unionGroupExpression = getOWLObjectUnionOf(unionGroupDisjuncts);
			final OWLQuantifiedObjectRestriction relationshipExpression = getRelationshipExpression(commonTypeId, unionGroupExpression, commonModifier);
			intersection.add(useCanonicalForm ? getCanonicalForm(relationshipExpression) : relationshipExpression);
		} else {
			unionGroup.getValue()
			.stream()
			.map(ugr -> getRelationshipExpression(ugr.getTypeId(), 
					ugr.getDestinationId(), 
					ugr.isDestinationNegated(), 
					ugr.getModifier()))
			.map(ce -> useCanonicalForm ? getCanonicalForm(ce) : ce)
			.forEachOrdered(intersection::add);
		}

		// Add relationship-referenced concrete domain members alongside the relationships
		if (supportConcreteDomains) {
			unionGroup.getValue()
					.stream()
					.flatMap(r -> r.getMembers().stream())
					.filter(m -> isConcreteDomainMember(m))
					.forEachOrdered(m -> addConcreteDomainMember(m, intersection));
		}
	}

	private void addGroup(final String conceptId, final Entry<Integer, List<SnomedRelationship>> group, final Set<OWLClassExpression> intersection) {
		final Set<OWLClassExpression> groupIntersection = Sets.newHashSet();

		group.getValue()
				.stream()
				.collect(Collectors.groupingBy(SnomedRelationship::getUnionGroup))
				.entrySet()
				.stream()
				.forEachOrdered(ug -> addUnionGroup(ug, groupIntersection, false));

		if (group.getKey() > 0) {

			final Set<SnomedRelationship> activeIngredientRelationships = group.getValue()
					.stream()
					.filter(r -> isActiveIngredientRelationship(r))
					.collect(Collectors.toSet());

			final OWLClassExpression groupExpression;

			if (activeIngredientRelationships.size() > 1) {
				throw new IllegalStateException(String.format("Multiple 'has active ingredient' relationships were found in group %s of concept %s.", group.getKey(), conceptId));
			} else if (activeIngredientRelationships.size() == 1) {
				// Add it in never-grouped form to the outer intersection 
				final SnomedRelationship r = Iterables.getOnlyElement(activeIngredientRelationships);
				intersection.add(getCanonicalForm(getRelationshipExpression(r.getTypeId(), r.getDestinationId(), r.isDestinationNegated(), r.getModifier())));
				groupExpression = getMeasurementExpression(getOWLObjectIntersectionOf(groupIntersection));
			} else {
				groupExpression = getRoleGroupExpression(getOWLObjectIntersectionOf(groupIntersection));
			}

			intersection.add(getCanonicalForm(groupExpression));

		} else {
			groupIntersection.forEach(ce -> {
				final OWLClassExpression singleGroupExpression = getRoleGroupExpression(ce);
				intersection.add(singleGroupExpression);
			});
		}
	}

	private void addConcreteDomainMember(final SnomedReferenceSetMember member, final Set<OWLClassExpression> intersection) {
		final String uomId = (String) member.getProperties().getOrDefault(SnomedRf2Headers.FIELD_UNIT_ID, Concepts.DEFAULT_UNIT);
		final String attributeName = (String) member.getProperties().get(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME);
		final String serializedValue = (String) member.getProperties().get(SnomedRf2Headers.FIELD_VALUE);

		final DataType sctDataType = SnomedRefSetUtil.getDataType(member.getReferenceSetId());
		final OWL2Datatype owl2Datatype = getOWL2Datatype(sctDataType);

		final OWLClassExpression hasUnitExpression = createHasUnitExpression(uomId);
		final OWLClassExpression hasValueExpression = createHasValueExpression(owl2Datatype, serializedValue);
		final OWLClassExpression unitAndValueExpression = getOWLObjectIntersectionOf(ImmutableSet.of(hasUnitExpression, hasValueExpression));
		final OWLObjectProperty attributeProperty = getDataFactory().getOWLObjectProperty(PREFIX_LABEL + sanitize(attributeName), prefixManager);
		// TODO: add declaration axiom for all object properties declared above 
		final OWLClassExpression concreteDomainExpression = getDataFactory().getOWLObjectSomeValuesFrom(attributeProperty, unitAndValueExpression);

		intersection.add(getCanonicalForm(concreteDomainExpression));
	}

	// Do limited percent-encoding on incoming text as parentheses may confuse functional OWL parsers
	private String sanitize(final String text) {
		return StringUtil.replace(text, FIND, REPLACE);
	}

	private OWLClassExpression createHasUnitExpression(final String uomId) {
		final OWLObjectProperty hasUnitProperty = getDataFactory().getOWLObjectProperty(PREFIX_HAS_UNIT, prefixManager);
		final OWLClassExpression hasUnitExpression = getDataFactory().getOWLObjectSomeValuesFrom(hasUnitProperty, getOWLClass(uomId));
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

	private void addOWLObjectPropertyAxioms(final BranchContext context, final String propertyAncestorId) {
		final SnomedConcepts propertyConcepts = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByActive(true)
				.filterByStatedAncestor(propertyAncestorId)
				.build()
				.execute(context);

		propertyConcepts.forEach(child -> {
			final OWLObjectProperty childProperty = getOWLObjectProperty(child.getId());
			addAxiom(getOWLDeclarationAxiom(childProperty));

			final long[] parentIds = child.getStatedParentIds();
			for (final long parentId : parentIds) {
				final OWLObjectProperty parentProperty = getOWLObjectProperty(Long.toString(parentId));
				addAxiom(getOWLDeclarationAxiom(parentProperty));
				addAxiom(getOWLSubObjectPropertyOfAxiom(childProperty, parentProperty));
			}
		});
	}

	private OWLClass getOWLClass(final String conceptId) {
		return getDataFactory().getOWLClass(":" + conceptId, prefixManager);
	}

	private OWLObjectProperty getOWLObjectProperty(final String typeId) {
		return getDataFactory().getOWLObjectProperty(":" + typeId, prefixManager);
	}

	private OWLQuantifiedObjectRestriction getRelationshipExpression(final String typeId, final String destinationId, final boolean destinationNegated, final RelationshipModifier modifier) {
		final OWLClass destinationClass = getOWLClass(destinationId);
		final OWLClassExpression filler = destinationNegated 
				? getOWLObjectComplementOf(destinationClass) 
				: destinationClass;

		return getRelationshipExpression(typeId, filler, modifier);
	}

	private OWLClassExpression getOWLObjectComplementOf(final OWLClass owlClass) {
		return getDataFactory().getOWLObjectComplementOf(owlClass);
	}

	private OWLQuantifiedObjectRestriction getRelationshipExpression(final String typeId, final OWLClassExpression filler, final RelationshipModifier modifier) {
		final OWLObjectProperty property = getOWLObjectProperty(typeId);

		if (RelationshipModifier.UNIVERSAL.equals(modifier)) {
			return getDataFactory().getOWLObjectAllValuesFrom(property, filler);
		} else {
			return getDataFactory().getOWLObjectSomeValuesFrom(property, filler);
		}
	}

	private OWLObjectSomeValuesFrom getRoleGroupExpression(final OWLClassExpression filler) {
		final OWLObjectProperty property = getOWLObjectProperty(ROLE_GROUP_ID);
		return getDataFactory().getOWLObjectSomeValuesFrom(property, filler);
	}

	private OWLClassExpression getMeasurementExpression(final OWLClassExpression filler) {
		final OWLObjectProperty property = getDataFactory().getOWLObjectProperty(PREFIX_MEASUREMENT, prefixManager);
		return getDataFactory().getOWLObjectSomeValuesFrom(property, filler);
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

	private OWLClassExpression getCanonicalForm(final OWLClassExpression expression) {
		if (canonicalRepresentations.containsKey(expression)) {
			return canonicalRepresentations.get(expression);
		} else {
			final OWLClass canonicalClass = getDataFactory().getOWLClass(PREFIX_CANONICAL + canonicalRepresentations.size(), prefixManager);
			addAxiom(getOWLDeclarationAxiom(canonicalClass));
			addAxiom(getDataFactory().getOWLEquivalentClassesAxiom(canonicalClass, expression));
			canonicalRepresentations.put(expression, canonicalClass);
			return canonicalClass;
		}
	}

	private void addAxiom(final OWLAxiom axiom) {
		getOntologyManager().addAxiom(ontology, axiom);
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public OWLOntologyManager getOntologyManager() {
		return getOntology().getOWLOntologyManager();
	}

	public OWLDataFactory getDataFactory() {
		return getOntologyManager().getOWLDataFactory();
	}
}
