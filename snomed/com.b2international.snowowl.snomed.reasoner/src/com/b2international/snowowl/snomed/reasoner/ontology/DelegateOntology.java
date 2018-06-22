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
package com.b2international.snowowl.snomed.reasoner.server.ontology;

import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_CONCEPT;
import static com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils.PREFIX_ROLE;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.server.snomed.index.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.reasoner.model.ConceptDefinition;
import com.b2international.snowowl.snomed.reasoner.model.ConcreteDomainDefinition;
import com.b2international.snowowl.snomed.reasoner.model.LongConcepts;
import com.b2international.snowowl.snomed.reasoner.model.RelationshipDefinition;
import com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils;
import com.b2international.snowowl.snomed.reasoner.model.WritableEmptySet;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.primitives.Longs;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectImpl;

public class DelegateOntology extends OWLObjectImpl implements OWLMutableOntology {

	private final OWLOntologyManager manager;
	private final OWLOntologyID ontologyID;
	private final DefaultPrefixManager prefixManager;
	private final RevisionIndex index;
	private final boolean trackingChanges;
	private final OWLOntology plusOntology;
	private final boolean concreteDomainSupport;
	private final IBranchPath branchPath;

	private volatile ReasonerTaxonomyBuilder reasonerTaxonomyBuilder;

	public DelegateOntology(final OWLOntologyManager manager, final OWLOntologyID ontologyID, final IBranchPath branchPath, final RevisionIndex index, boolean concreteDomainSupport) throws OWLOntologyCreationException {
		super();
		this.manager = manager;
		this.ontologyID = ontologyID;
		this.branchPath = branchPath;
		this.index = index;
		this.concreteDomainSupport = concreteDomainSupport;
		this.prefixManager = SnomedOntologyUtils.createPrefixManager(this);
		this.plusOntology = manager.createOntology();
		this.trackingChanges = (null == ontologyID.getVersionIRI());
	}

	@Override public OWLOntologyManager getOWLOntologyManager() {
		return manager;
	}

	@Override public OWLOntologyID getOntologyID() {
		return ontologyID;
	}

	@Override public boolean isAnonymous() {
		return ontologyID.isAnonymous();
	}

	@Override public Set<OWLAnnotation> getAnnotations() {
		// No direct annotations are added to the ontology
		return WritableEmptySet.create();
	}

	@Override public Set<IRI> getDirectImportsDocuments() throws UnknownOWLOntologyException {
		// No direct import IRIs will be present
		return WritableEmptySet.create();
	}

	@Override public Set<OWLOntology> getDirectImports() throws UnknownOWLOntologyException {
		// No directly imported ontologies will be added
		return WritableEmptySet.create();
	}

	@Override public Set<OWLOntology> getImports() throws UnknownOWLOntologyException {
		// No transitive imports will be present, as no direct imports are present
		return WritableEmptySet.create();
	}

	@Override public Set<OWLOntology> getImportsClosure() throws UnknownOWLOntologyException {
		// No imports closure needs to be calculated, as no direct imports are present
		return Sets.<OWLOntology>newHashSet(this);
	}

	@Override public Set<OWLImportsDeclaration> getImportsDeclarations() {
		// No imports declarations are present
		return WritableEmptySet.create();
	}

	@Override public boolean isEmpty() {
		return 0 == getAxiomCount();
	}

	@Override public Set<OWLAxiom> getAxioms() {
		// All axioms are logical axioms
		final Set<OWLAxiom> results = newHashSet();
		results.addAll(getLogicalAxioms());
		return results;
	}

	@Override public int getAxiomCount() {
		// All axioms are logical axioms
		return getLogicalAxiomCount();
	}

	@Override public Set<OWLLogicalAxiom> getLogicalAxioms() {
		final Set<OWLLogicalAxiom> results = newHashSet();
		results.addAll(getAxioms(AxiomType.SUBCLASS_OF));
		results.addAll(getAxioms(AxiomType.EQUIVALENT_CLASSES));
		results.addAll(getAxioms(AxiomType.SUB_OBJECT_PROPERTY));
		results.addAll(getAxioms(AxiomType.DISJOINT_UNION));
		return results;
	}

	@Override public int getLogicalAxiomCount() {
		int totalCount = 0;
		totalCount += getAxiomCount(AxiomType.SUBCLASS_OF);
		totalCount += getAxiomCount(AxiomType.EQUIVALENT_CLASSES);
		totalCount += getAxiomCount(AxiomType.SUB_OBJECT_PROPERTY);
		totalCount += getAxiomCount(AxiomType.DISJOINT_UNION);
		return totalCount;
	}

	@Override @SuppressWarnings("unchecked") public <T extends OWLAxiom> Set<T> getAxioms(final AxiomType<T> axiomType) {
		if (AxiomType.SUBCLASS_OF.equals(axiomType)) {
			return (Set<T>) getSubClassAxioms();
		} else if (AxiomType.EQUIVALENT_CLASSES.equals(axiomType)) {
			return (Set<T>) getEquivalentClassesAxioms();
		} else if (AxiomType.SUB_OBJECT_PROPERTY.equals(axiomType)) {
			return (Set<T>) getObjectSubPropertyAxioms();
		} else if (AxiomType.DISJOINT_UNION.equals(axiomType)) {
			return (Set<T>) getDisjointUnionAxioms();
		} else {
			return WritableEmptySet.create();
		}
	}

	private Set<OWLSubClassOfAxiom> getSubClassAxioms() {
		final Set<OWLSubClassOfAxiom> result = newHashSet();

		final LongSet conceptIdSet = getReasonerTaxonomyBuilder().getConceptIdSet();
		for (final LongIterator itr = conceptIdSet.iterator(); itr.hasNext(); /* empty */) {
			final long conceptId = itr.next();
			if (!getReasonerTaxonomyBuilder().isPrimitive(conceptId)) {
				continue;
			}

			collectSubClassAxiomsForConceptId(conceptId, result);
		}

		result.addAll(plusOntology.getAxioms(AxiomType.SUBCLASS_OF));
		return result;
	}

	private Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms() {
		final Set<OWLEquivalentClassesAxiom> result = newHashSet();

		final LongSet conceptIdSet = getReasonerTaxonomyBuilder().getConceptIdSet();
		for (final LongIterator itr = conceptIdSet.iterator(); itr.hasNext(); /* empty */) {
			final long conceptId = itr.next();
			if (getReasonerTaxonomyBuilder().isPrimitive(conceptId)) {
				continue;
			}

			collectEquivalentClassesAxiomForLHS(conceptId, result);
		}

		result.addAll(plusOntology.getAxioms(AxiomType.EQUIVALENT_CLASSES));
		return result;
	}

	private Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxioms() {
		final Set<OWLSubObjectPropertyOfAxiom> result = newHashSet();

		final LongSet conceptIdSet = getReasonerTaxonomyBuilder().getConceptIdSet();

		for (final LongIterator itr = conceptIdSet.iterator(); itr.hasNext(); /* empty */) {
			final long conceptId = itr.next();
			if (!getReasonerTaxonomyBuilder().getAllSuperTypeIds(conceptId).contains(LongConcepts.CONCEPT_MODEL_ATTRIBUTE_ID)) {
				continue;
			}

			collectSubPropertyAxiomsForConceptId(conceptId, result);
		}

		result.addAll(plusOntology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY));
		return result;
	}

	private void collectSubPropertyAxiomsForConceptId(final long conceptId, final Set<OWLSubObjectPropertyOfAxiom> result) {

		final LongSet superTypeIds = getReasonerTaxonomyBuilder().getSuperTypeIds(conceptId);

		for (final LongIterator itr2 = superTypeIds.iterator(); itr2.hasNext(); /* empty */) {
			final long superTypeId = itr2.next();
			if (getReasonerTaxonomyBuilder().getAllSuperTypeIds(superTypeId).contains(LongConcepts.CONCEPT_MODEL_ATTRIBUTE_ID)) {
				final OWLObjectProperty subProperty = manager.getOWLDataFactory().getOWLObjectProperty(PREFIX_ROLE + conceptId, prefixManager);
				final OWLObjectProperty superProperty = manager.getOWLDataFactory().getOWLObjectProperty(PREFIX_ROLE + superTypeId, prefixManager);
				final OWLSubObjectPropertyOfAxiom propertyAxiom = manager.getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(subProperty, superProperty);
				result.add(propertyAxiom);
			}
		}
	}

	private Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms() {
		final Set<OWLDisjointUnionAxiom> result = newHashSet();

		final LongSet conceptIdSet = getReasonerTaxonomyBuilder().getConceptIdSet();

		for (final LongIterator itr = conceptIdSet.iterator(); itr.hasNext(); /* empty */) {
			final long conceptId = itr.next();
			if (!getReasonerTaxonomyBuilder().isExhaustive(conceptId)) {
				continue;
			}

			collectDisjointUnionAxiomForConceptId(conceptId, result);
		}

		result.addAll(plusOntology.getAxioms(AxiomType.DISJOINT_UNION));
		return result;
	}

	private List<OWLAxiom> createRawAxioms(final long conceptId, final boolean primitive) {

		final Collection<ConcreteDomainFragment> conceptDomainFragments = getReasonerTaxonomyBuilder().getStatedConcreteDomainFragments(conceptId);
		final Set<ConcreteDomainDefinition> conceptDomainDefinitions = newHashSet();

		for (final ConcreteDomainFragment conceptFragment : conceptDomainFragments) {
			conceptDomainDefinitions.add(new ConcreteDomainDefinition(conceptFragment));
		}

		final ConceptDefinition definition = new ConceptDefinition(conceptDomainDefinitions, conceptId, primitive, null);
		final Collection<StatementFragment> statementFragments = getReasonerTaxonomyBuilder().getStatedStatementFragments(conceptId);

		for (final StatementFragment statementFragment : statementFragments) {
			final long statementId = statementFragment.getStatementId();
			final Collection<ConcreteDomainFragment> relationshipDomainFragments = getReasonerTaxonomyBuilder().getStatedConcreteDomainFragments(statementId);
			final Set<ConcreteDomainDefinition> relationshipDomainDefinitions = newHashSet();

			for (final ConcreteDomainFragment relationshipDomainFragment : relationshipDomainFragments) {
				relationshipDomainDefinitions.add(new ConcreteDomainDefinition(relationshipDomainFragment));
			}

			final RelationshipDefinition relationshipDefinition = new RelationshipDefinition(relationshipDomainDefinitions,
					statementFragment.getTypeId(), statementFragment.getDestinationId(), statementFragment.isDestinationNegated(),
					statementFragment.isUniversal());

			if (LongConcepts.IS_A_ID == statementFragment.getTypeId()) {
				definition.addIsaDefinition(relationshipDefinition);
			} else if (Longs.contains(LongConcepts.NEVER_GROUPED_ROLE_IDS, statementFragment.getTypeId()) && 0 == statementFragment.getGroup()) {
				definition.addNeverGroupedDefinition(relationshipDefinition, statementFragment.getGroup(), statementFragment.getUnionGroup());
			} else {
				definition.addGroupDefinition(relationshipDefinition, statementFragment.getGroup(), statementFragment.getUnionGroup());
			}
		}

		final List<OWLAxiom> axioms = newArrayList();
		definition.collect(manager.getOWLDataFactory(), prefixManager, axioms, Sets.<OWLClassExpression>newHashSet());
		return axioms;
	}

	@Override public <T extends OWLAxiom> Set<T> getAxioms(final AxiomType<T> axiomType, final boolean includeImportsClosure) {
		// No need to consider imports closure
		return getAxioms(axiomType);
	}

	@Override public Set<OWLAxiom> getTBoxAxioms(final boolean includeImportsClosure) {
		final Set<OWLAxiom> tboxAxioms = newHashSet();
		tboxAxioms.addAll(getAxioms(AxiomType.SUBCLASS_OF));
		tboxAxioms.addAll(getAxioms(AxiomType.EQUIVALENT_CLASSES));
		tboxAxioms.addAll(getAxioms(AxiomType.DISJOINT_UNION));
		return tboxAxioms;
	}

	@Override public Set<OWLAxiom> getABoxAxioms(final boolean includeImportsClosure) {
		// Not handling individuals
		return WritableEmptySet.create();
	}

	@Override public Set<OWLAxiom> getRBoxAxioms(final boolean includeImportsClosure) {
		final Set<OWLAxiom> rboxAxioms = newHashSet();
		rboxAxioms.addAll(getAxioms(AxiomType.SUB_OBJECT_PROPERTY));
		return rboxAxioms;
	}

	@Override public <T extends OWLAxiom> int getAxiomCount(final AxiomType<T> axiomType) {
		/*
		 * TODO more optimal solution exists: 
		 * 
		 * - subclassof --> number of primitive classes 
		 * - equivalentclasses --> number of defined classes
		 * - subobjectpropertyof --> number of role inclusions 
		 * - disjoint union --> number of exhaustive classes 
		 */
		return getAxioms(axiomType).size() + plusOntology.getAxiomCount(axiomType);
	}

	@Override public <T extends OWLAxiom> int getAxiomCount(final AxiomType<T> axiomType, final boolean includeImportsClosure) {
		// No need to consider imports closure
		return getAxiomCount(axiomType);
	}

	@Override public boolean containsAxiom(final OWLAxiom axiom) {
		return baseContainsAxiom(axiom) || plusOntology.containsAxiom(axiom);
	}

	private boolean baseContainsAxiom(final OWLAxiom axiom) {
		final AxiomType<?> axiomType = axiom.getAxiomType();
		if (AxiomType.SUBCLASS_OF.equals(axiomType)) {
			return containsSubClassAxiom((OWLSubClassOfAxiom) axiom);
		} else if (AxiomType.EQUIVALENT_CLASSES.equals(axiomType)) {
			return containsEquivalentClassesAxiom((OWLEquivalentClassesAxiom) axiom);
		} else if (AxiomType.SUB_OBJECT_PROPERTY.equals(axiomType)) {
			return containsObjectSubPropertyAxiom((OWLSubObjectPropertyOfAxiom) axiom);
		} else if (AxiomType.DISJOINT_UNION.equals(axiomType)) {
			return containsDisjointUnionAxiom((OWLDisjointUnionAxiom) axiom);
		} else {
			return false;
		}
	}

	private boolean containsSubClassAxiom(final OWLSubClassOfAxiom axiom) {

		final OWLClassExpression subClass = axiom.getSubClass();
		if (subClass.isAnonymous()) {
			return false;
		}

		final OWLClass namedSubClass = subClass.asOWLClass();
		if (!isConceptClass(namedSubClass)) {
			return false;
		}

		final long conceptId = getConceptId(namedSubClass);

		if (!getReasonerTaxonomyBuilder().isPrimitive(conceptId)) {
			return false;
		}

		return createRawAxioms(conceptId, true).contains(axiom);
	}

	private boolean containsEquivalentClassesAxiom(final OWLEquivalentClassesAxiom axiom) {

		for (final OWLClassExpression expression : axiom.getClassExpressions()) {
			if (expression.isAnonymous()) {
				continue;
			}

			final OWLClass namedSubClass = expression.asOWLClass();
			if (!isConceptClass(namedSubClass)) {
				continue;
			}

			final long conceptId = getConceptId(namedSubClass);
			if (getReasonerTaxonomyBuilder().isPrimitive(conceptId)) {
				continue;
			}

			if (createRawAxioms(conceptId, false).contains(axiom)) {
				return true;
			}
		}

		return false;
	}

	private boolean containsObjectSubPropertyAxiom(final OWLSubObjectPropertyOfAxiom axiom) {

		final OWLObjectPropertyExpression subProperty = axiom.getSubProperty();
		final OWLObjectPropertyExpression superProperty = axiom.getSuperProperty();
		return isConceptModelAttribute(subProperty) && isConceptModelAttribute(superProperty);
	}

	private boolean isConceptModelAttribute(final OWLObjectPropertyExpression propertyExpression) {
		if (propertyExpression.isAnonymous()) {
			return false;
		}

		final OWLObjectProperty namedSubProperty = propertyExpression.asOWLObjectProperty();
		if (!isRole(namedSubProperty)) {
			return false;
		}

		final long conceptId = getConceptId(namedSubProperty);
		return getReasonerTaxonomyBuilder().getAllSuperTypeIds(conceptId).contains(LongConcepts.CONCEPT_MODEL_ATTRIBUTE_ID);
	}

	private boolean containsDisjointUnionAxiom(final OWLDisjointUnionAxiom axiom) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override public boolean containsAxiom(final OWLAxiom axiom, final boolean includeImportsClosure) {
		// No need to consider imports closure
		return containsAxiom(axiom);
	}

	private boolean isConceptClass(final OWLClass owlClass) {
		return hasPrefix(owlClass, SnomedOntologyUtils.PREFIX_CONCEPT);
	}

	private boolean isRole(final OWLObjectProperty objectProperty) {
		return hasPrefix(objectProperty, SnomedOntologyUtils.PREFIX_ROLE);
	}

	private boolean hasPrefix(final OWLEntity entity, final String prefix) {
		return prefixManager.getShortForm(entity.getIRI()).startsWith(prefix);
	}

	private long getConceptId(final OWLEntity entity) {
		final String strippedShortForm = prefixManager.getShortForm(entity.getIRI()).substring(SnomedOntologyUtils.PREFIX_SNOMED.length());
		return Long.parseLong(Iterables.get(Splitter.on('_').split(strippedShortForm), 1));
	}

	@Override public boolean containsAxiomIgnoreAnnotations(final OWLAxiom axiom) {
		// Strip the annotation from the incoming axiom and check
		return containsAxiom(axiom.getAxiomWithoutAnnotations());
	}

	@Override public Set<OWLAxiom> getAxiomsIgnoreAnnotations(final OWLAxiom axiom) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override public Set<OWLAxiom> getAxiomsIgnoreAnnotations(final OWLAxiom axiom, final boolean includeImportsClosure) {
		// No need to consider imports closure
		return getAxiomsIgnoreAnnotations(axiom);
	}

	@Override public boolean containsAxiomIgnoreAnnotations(final OWLAxiom axiom, final boolean includeImportsClosure) {
		// No need to consider imports closure
		return containsAxiomIgnoreAnnotations(axiom);
	}

	@Override public Set<OWLClassAxiom> getGeneralClassAxioms() {
		// No GCIs are present in the ontology
		return WritableEmptySet.create();
	}

	@Override public Set<OWLEntity> getSignature() {
		final Set<OWLEntity> results = newHashSet();
		results.addAll(getClassesInSignature());
		results.addAll(getObjectPropertiesInSignature());
		return results;
	}

	@Override public Set<OWLEntity> getSignature(final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return getSignature();
	}

	@Override public Set<OWLClass> getClassesInSignature() {
		final Set<OWLClass> result = newHashSet();
		if (isEmpty()) {
			return result;
		}

		final OWLDataFactory df = manager.getOWLDataFactory();
		result.add(df.getOWLClass(SnomedOntologyUtils.PREFIX_CONCEPT_UNIT_NOT_APPLICABLE, prefixManager));

		final LongSet conceptIdSet = getReasonerTaxonomyBuilder().getConceptIdSet();
		for (final LongIterator itr = conceptIdSet.iterator(); itr.hasNext(); /* empty */) {
			final long conceptId = itr.next();
			result.add(df.getOWLClass(SnomedOntologyUtils.PREFIX_CONCEPT + conceptId, prefixManager));
		}

		result.addAll(plusOntology.getClassesInSignature());
		return result;
	}

	@Override public Set<OWLClass> getClassesInSignature(final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return getClassesInSignature();
	}

	@Override public Set<OWLObjectProperty> getObjectPropertiesInSignature() {
		final Set<OWLObjectProperty> result = newHashSet();
		if (isEmpty()) {
			return result;
		}

		final OWLDataFactory df = manager.getOWLDataFactory();
		result.add(df.getOWLObjectProperty(SnomedOntologyUtils.PREFIX_HAS_UNIT, prefixManager));
		result.add(df.getOWLObjectProperty(SnomedOntologyUtils.PREFIX_ROLE_GROUP, prefixManager));
		result.add(df.getOWLObjectProperty(SnomedOntologyUtils.PREFIX_ROLE_HAS_MEASUREMENT, prefixManager));
		result.add(df.getOWLObjectProperty(SnomedOntologyUtils.PREFIX_DATA_HAS_VALUE, prefixManager));

		final LongSet conceptIdSet = getReasonerTaxonomyBuilder().getConceptIdSet();
		for (final LongIterator itr = conceptIdSet.iterator(); itr.hasNext(); /* empty */) {
			final long conceptId = itr.next();
			if (getReasonerTaxonomyBuilder().getAllSuperTypeIds(conceptId).contains(LongConcepts.CONCEPT_MODEL_ATTRIBUTE_ID)) {
				result.add(df.getOWLObjectProperty(SnomedOntologyUtils.PREFIX_CONCEPT + conceptId, prefixManager));
			}
		}

		result.addAll(plusOntology.getObjectPropertiesInSignature());
		return result;
	}

	@Override public Set<OWLObjectProperty> getObjectPropertiesInSignature(final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return getObjectPropertiesInSignature();
	}

	@Override public Set<OWLDataProperty> getDataPropertiesInSignature() {
		// TODO Consider switching to proper data properties (dataHasValue should be here)
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDataProperty> getDataPropertiesInSignature(final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return getDataPropertiesInSignature();
	}

	@Override public Set<OWLNamedIndividual> getIndividualsInSignature() {
		// No individuals are present in the ontology
		return WritableEmptySet.create();
	}

	@Override public Set<OWLNamedIndividual> getIndividualsInSignature(final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return getIndividualsInSignature();
	}

	@Override public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals() {
		// No anonymous individuals are present in the ontology
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDatatype> getDatatypesInSignature() {
		// TODO Consider switching to proper datatypes (all used OWL datatypes should be returned here)
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDatatype> getDatatypesInSignature(final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return getDatatypesInSignature();
	}

	@Override public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature() {
		// No annotations
		return WritableEmptySet.create();
	}

	@Override public Set<OWLAxiom> getReferencingAxioms(final OWLEntity owlEntity) {
		// TODO implement this method
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override public Set<OWLAxiom> getReferencingAxioms(final OWLEntity owlEntity, final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return getReferencingAxioms(owlEntity);
	}

	@Override public Set<OWLAxiom> getReferencingAxioms(final OWLAnonymousIndividual individual) {
		// No anonymous individuals
		return WritableEmptySet.create();
	}

	@Override public boolean containsEntityInSignature(final OWLEntity owlEntity) {
		// There is no "punning" (overlap between entity IRIs) in the ontology
		return containsEntityInSignature(owlEntity.getIRI());
	}

	@Override public boolean containsEntityInSignature(final OWLEntity owlEntity, final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return containsEntityInSignature(owlEntity);
	}

	@Override public boolean containsEntityInSignature(final IRI entityIRI) {
		return containsClassInSignature(entityIRI) 
				|| containsObjectPropertyInSignature(entityIRI)
				|| containsAnnotationPropertyInSignature(entityIRI);
	}

	@Override public boolean containsEntityInSignature(final IRI entityIRI, final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return containsEntityInSignature(entityIRI);
	}

	@Override public boolean isDeclared(final OWLEntity owlEntity) {
		// TODO Add declaration axioms if needed
		return false;
	}

	@Override public boolean isDeclared(final OWLEntity owlEntity, final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return isDeclared(owlEntity);
	}

	@Override public boolean containsClassInSignature(final IRI owlClassIRI) {
		// TODO Implement this method
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override public boolean containsClassInSignature(final IRI owlClassIRI, final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return containsClassInSignature(owlClassIRI);
	}

	@Override public boolean containsObjectPropertyInSignature(final IRI owlObjectPropertyIRI) {
		// TODO Implement this method
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override public boolean containsObjectPropertyInSignature(final IRI owlObjectPropertyIRI, final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return containsObjectPropertyInSignature(owlObjectPropertyIRI);
	}

	@Override public boolean containsDataPropertyInSignature(final IRI owlDataPropertyIRI) {
		// TODO Add data properties in the future
		return false;
	}

	@Override public boolean containsDataPropertyInSignature(final IRI owlDataPropertyIRI, final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return containsDataPropertyInSignature(owlDataPropertyIRI);
	}

	@Override public boolean containsAnnotationPropertyInSignature(final IRI owlAnnotationPropertyIRI) {
		// TODO Implement this method
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override public boolean containsAnnotationPropertyInSignature(final IRI owlAnnotationPropertyIRI, final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return containsAnnotationPropertyInSignature(owlAnnotationPropertyIRI);
	}

	@Override public boolean containsIndividualInSignature(final IRI owlIndividualIRI) {
		// No individuals in the ontology
		return false;
	}

	@Override public boolean containsIndividualInSignature(final IRI owlIndividualIRI, final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return containsIndividualInSignature(owlIndividualIRI);
	}

	@Override public boolean containsDatatypeInSignature(final IRI owlDatatypeIRI) {
		// TODO Add datatype support in the future (maybe the storage key long needs to be returned here?)
		return false;
	}

	@Override public boolean containsDatatypeInSignature(final IRI owlDatatypeIRI, final boolean includeImportsClosure) {
		// No need to consider an imports closure
		return containsDatatypeInSignature(owlDatatypeIRI);
	}

	@Override public Set<OWLEntity> getEntitiesInSignature(final IRI iri) {
		// TODO Implement this method
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override public Set<OWLEntity> getEntitiesInSignature(final IRI iri, final boolean includeImportsClosure) {
		// No need to consider imports closure
		return getEntitiesInSignature(iri);
	}

	@Override public Set<OWLClassAxiom> getAxioms(final OWLClass cls) {
		final Set<OWLClassAxiom> results = newHashSet();
		results.addAll(getSubClassAxiomsForSubClass(cls));
		results.addAll(getEquivalentClassesAxioms(cls));
		results.addAll(getDisjointUnionAxioms(cls));
		return results;
	}

	@Override public Set<OWLObjectPropertyAxiom> getAxioms(final OWLObjectPropertyExpression prop) {
		// The only definition axiom an object property can participate in is the SubObjectPropertyOfAxiom
		final Set<OWLObjectPropertyAxiom> results = newHashSet();
		results.addAll(getObjectSubPropertyAxiomsForSubProperty(prop));
		return results;
	}

	@Override public Set<OWLDataPropertyAxiom> getAxioms(final OWLDataProperty prop) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLIndividualAxiom> getAxioms(final OWLIndividual individual) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLAnnotationAxiom> getAxioms(final OWLAnnotationProperty property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDatatypeDefinitionAxiom> getAxioms(final OWLDatatype datatype) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLSubAnnotationPropertyOfAxiom> getSubAnnotationPropertyOfAxioms(final OWLAnnotationProperty subProperty) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(final OWLAnnotationProperty property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(final OWLAnnotationProperty property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDeclarationAxiom> getDeclarationAxioms(final OWLEntity subject) {
		// TODO Add class, object property, data property declarations if necessary
		return WritableEmptySet.create();
	}

	@Override public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(final OWLAnnotationSubject entity) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSubClass(final OWLClass cls) {

		if (!isConceptClass(cls)) {
			return WritableEmptySet.create();
		}

		final long conceptId = getConceptId(cls);

		if (!getReasonerTaxonomyBuilder().isPrimitive(conceptId)) {
			return WritableEmptySet.create();
		}

		final Set<OWLSubClassOfAxiom> result = newHashSet();
		collectSubClassAxiomsForConceptId(conceptId, result);
		
		result.addAll(plusOntology.getSubClassAxiomsForSubClass(cls));
		return result;
	}

	@Override public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSuperClass(final OWLClass cls) {

		if (!isConceptClass(cls)) {
			return WritableEmptySet.create();
		}

		final long conceptId = getConceptId(cls);

		if (!getReasonerTaxonomyBuilder().isPrimitive(conceptId)) {
			return WritableEmptySet.create();
		}

		final LongSet subTypeIds = getReasonerTaxonomyBuilder().getSubTypeIds(conceptId);
		final Set<OWLSubClassOfAxiom> result = newHashSet();

		for (final LongIterator itr = subTypeIds.iterator(); itr.hasNext(); /* empty */) {
			final long subTypeId = itr.next();

			// Subtype must be _primitive_, if not, continue
			if (!getReasonerTaxonomyBuilder().isPrimitive(subTypeId)) {
				continue;
			}

			// The only supertype should be the current concept
			if (getReasonerTaxonomyBuilder().getSuperTypeIds(subTypeId).size() > 1) {
				continue;
			}

			// No non-ISA relationships can be present on the subtype
			if (getReasonerTaxonomyBuilder().getStatedNonIsAFragments(subTypeId).size() > 0) {
				continue;
			}

			collectSubClassAxiomsForConceptId(subTypeId, result);
		}

		result.addAll(plusOntology.getSubClassAxiomsForSuperClass(cls));
		return result;
	}

	private void collectSubClassAxiomsForConceptId(final long conceptId, final Set<OWLSubClassOfAxiom> result) {

		final List<OWLAxiom> rawAxioms = createRawAxioms(conceptId, true);

		for (final OWLAxiom axiom : rawAxioms) {
			if (AxiomType.SUBCLASS_OF.equals(axiom.getAxiomType())) {
				result.add((OWLSubClassOfAxiom) axiom);
				break;
			}
		}
	}

	@Override public Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms(final OWLClass cls) {

		if (!isConceptClass(cls)) {
			return WritableEmptySet.create();
		}

		final long conceptId = getConceptId(cls);
		final Set<OWLEquivalentClassesAxiom> result = newHashSet();

		collectEquivalentClassesAxiomForRHS(conceptId, result);

		if (!getReasonerTaxonomyBuilder().isPrimitive(conceptId)) {
			collectEquivalentClassesAxiomForLHS(conceptId, result);
		}

		result.addAll(plusOntology.getEquivalentClassesAxioms(cls));
		return result;
	}

	private void collectEquivalentClassesAxiomForLHS(final long conceptId, final Set<OWLEquivalentClassesAxiom> result) {

		final List<OWLAxiom> rawAxioms = createRawAxioms(conceptId, false);

		for (final OWLAxiom axiom : rawAxioms) {
			if (AxiomType.EQUIVALENT_CLASSES.equals(axiom.getAxiomType())) {
				result.add((OWLEquivalentClassesAxiom) axiom);
				break;
			}
		}
	}

	private void collectEquivalentClassesAxiomForRHS(final long conceptId, final Set<OWLEquivalentClassesAxiom> result) {

		final LongSet subTypeIds = getReasonerTaxonomyBuilder().getSubTypeIds(conceptId);

		for (final LongIterator itr = subTypeIds.iterator(); itr.hasNext(); /* empty */) {
			final long subTypeId = itr.next();

			// Subtype must be _defined_, if not, continue
			if (getReasonerTaxonomyBuilder().isPrimitive(subTypeId)) {
				continue;
			}

			// The only supertype should be the current concept
			if (getReasonerTaxonomyBuilder().getSuperTypeIds(subTypeId).size() > 1) {
				continue;
			}

			// No non-ISA relationships can be present on the subtype
			if (getReasonerTaxonomyBuilder().getStatedNonIsAFragments(subTypeId).size() > 0) {
				continue;
			}

			collectEquivalentClassesAxiomForLHS(subTypeId, result);
		}
	}

	@Override public Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms(final OWLClass cls) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms(final OWLClass cls) {

		if (!isConceptClass(cls)) {
			return WritableEmptySet.create();
		}

		final long conceptId = getConceptId(cls);

		if (!getReasonerTaxonomyBuilder().isExhaustive(conceptId)) {
			return WritableEmptySet.create();
		}

		final Set<OWLDisjointUnionAxiom> result = newHashSet();
		collectDisjointUnionAxiomForConceptId(conceptId, result);
		
		result.addAll(plusOntology.getDisjointUnionAxioms(cls));
		return result;
	}

	private void collectDisjointUnionAxiomForConceptId(final long conceptId, final Set<OWLDisjointUnionAxiom> result) {

		final OWLClass conceptClass = manager.getOWLDataFactory().getOWLClass(PREFIX_CONCEPT + conceptId, prefixManager);
		final Set<OWLClass> disjointUnionClasses = newHashSet();

		final LongSet subTypeIds = getReasonerTaxonomyBuilder().getSubTypeIds(conceptId);

		for (final LongIterator itr2 = subTypeIds.iterator(); itr2.hasNext(); /* empty */) {
			final long subTypeId = itr2.next();
			final OWLClass disjointUnionMember = manager.getOWLDataFactory().getOWLClass(PREFIX_CONCEPT + subTypeId, prefixManager);
			disjointUnionClasses.add(disjointUnionMember);
		}

		final OWLDisjointUnionAxiom disjointUnionAxiom = manager.getOWLDataFactory().getOWLDisjointUnionAxiom(conceptClass, disjointUnionClasses);
		result.add(disjointUnionAxiom);
	}

	@Override public Set<OWLHasKeyAxiom> getHasKeyAxioms(final OWLClass cls) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSubProperty(final OWLObjectPropertyExpression subProperty) {

		if (subProperty.isAnonymous()) {
			return WritableEmptySet.create();
		}

		final OWLObjectProperty objectProperty = subProperty.asOWLObjectProperty();

		if (!isRole(objectProperty)) {
			return WritableEmptySet.create();
		}

		final long conceptId = getConceptId(objectProperty);

		if (!getReasonerTaxonomyBuilder().getAllSuperTypeIds(conceptId).contains(LongConcepts.CONCEPT_MODEL_ATTRIBUTE_ID)) {
			return WritableEmptySet.create();
		}

		final Set<OWLSubObjectPropertyOfAxiom> result = newHashSet();
		collectSubPropertyAxiomsForConceptId(conceptId, result);
		
		result.addAll(plusOntology.getObjectSubPropertyAxiomsForSubProperty(subProperty));
		return result;
	}

	@Override public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSuperProperty(final OWLObjectPropertyExpression superProperty) {
		if (superProperty.isAnonymous()) {
			return WritableEmptySet.create();
		}

		final OWLObjectProperty objectProperty = superProperty.asOWLObjectProperty();

		if (!isRole(objectProperty)) {
			return WritableEmptySet.create();
		}

		final long conceptId = getConceptId(objectProperty);

		if (!getReasonerTaxonomyBuilder().getAllSuperTypeIds(conceptId).contains(LongConcepts.CONCEPT_MODEL_ATTRIBUTE_ID)) {
			return WritableEmptySet.create();
		}

		final LongSet subTypeIds = getReasonerTaxonomyBuilder().getSubTypeIds(conceptId);
		final Set<OWLSubObjectPropertyOfAxiom> result = newHashSet();

		for (final LongIterator itr = subTypeIds.iterator(); itr.hasNext(); /* empty */) {
			final long subTypeId = itr.next();
			collectSubPropertyAxiomsForConceptId(subTypeId, result);
		}

		result.addAll(plusOntology.getObjectSubPropertyAxiomsForSuperProperty(superProperty));
		return result;
	}

	@Override public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLFunctionalObjectPropertyAxiom> getFunctionalObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseFunctionalObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLSymmetricObjectPropertyAxiom> getSymmetricObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLAsymmetricObjectPropertyAxiom> getAsymmetricObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLReflexiveObjectPropertyAxiom> getReflexiveObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLTransitiveObjectPropertyAxiom> getTransitiveObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSubProperty(final OWLDataProperty subProperty) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSuperProperty(final OWLDataPropertyExpression superProperty) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(final OWLDataProperty property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(final OWLDataProperty property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxioms(final OWLDataProperty property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms(final OWLDataProperty property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLFunctionalDataPropertyAxiom> getFunctionalDataPropertyAxioms(final OWLDataPropertyExpression property) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(final OWLIndividual individual) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(final OWLClassExpression ce) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms(final OWLIndividual individual) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms(final OWLIndividual individual) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms(final OWLIndividual individual) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms(final OWLIndividual individual) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLSameIndividualAxiom> getSameIndividualAxioms(final OWLIndividual individual) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms(final OWLIndividual individual) {
		return WritableEmptySet.create();
	}

	@Override public Set<OWLDatatypeDefinitionAxiom> getDatatypeDefinitions(final OWLDatatype datatype) {
		return WritableEmptySet.create();
	}

	@Override public void accept(final OWLObjectVisitor visitor) {
		visitor.visit(this);
	}

	@Override public <O> O accept(final OWLObjectVisitorEx<O> visitor) {
		return visitor.visit(this);
	}

	@Override protected int compareObjectOfSameType(final OWLObject object) {
		if (object == this) {
			return 0;
		}
		final OWLOntology other = (OWLOntology) object;
		return ontologyID.compareTo(other.getOntologyID());
	}

	@Override public List<OWLOntologyChange> applyChange(final OWLOntologyChange change) throws OWLOntologyChangeException {
		return applyChanges(ImmutableList.of(change));
	}

	@Override public List<OWLOntologyChange> applyChanges(final List<OWLOntologyChange> changes) throws OWLOntologyChangeException {

		/*
		 * If this is the "official" tracking ontology for a branch, we just dispose the previous taxonomy builder and pretend that changes were
		 * applied.
		 */
		if (trackingChanges) {
			reasonerTaxonomyBuilder = null;
			return newArrayList(changes);
		}
		
		final List<OWLOntologyChange> plusChanges = newArrayList();
		
		for (final OWLOntologyChange change : changes) {
			if (change instanceof AddAxiom) {
				plusChanges.add(new AddAxiom(plusOntology, change.getAxiom()));
			} else if (change instanceof RemoveAxiom) {
				plusChanges.add(new RemoveAxiom(plusOntology, change.getAxiom()));
			}
		}
		
		final List<OWLOntologyChange> actuallyAppliedChanges = newArrayList();
		actuallyAppliedChanges.addAll(manager.applyChanges(plusChanges));
		
		if (changes.size() != actuallyAppliedChanges.size()) {
			final Set<OWLOntologyChange> notAppliedChanges = Sets.difference(ImmutableSet.copyOf(changes), ImmutableSet.copyOf(actuallyAppliedChanges));
			
			for (final OWLOntologyChange notAppliedChange : notAppliedChanges) {
				if (notAppliedChange instanceof RemoveAxiom) {
					throw new IllegalStateException("One or more removals could not be applied: " + notAppliedChanges);
				}
			}
		}
		
		return actuallyAppliedChanges;
	}

	public ReasonerTaxonomyBuilder getReasonerTaxonomyBuilder() {

		if (null == reasonerTaxonomyBuilder) {
			reasonerTaxonomyBuilder = index.read(branchPath.getPath(), new RevisionIndexRead<ReasonerTaxonomyBuilder>() {
				@Override
				public ReasonerTaxonomyBuilder execute(RevisionSearcher searcher) throws IOException {
					return new ReasonerTaxonomyBuilder(searcher, concreteDomainSupport);
				}
			});
		}

		return reasonerTaxonomyBuilder;
	}

	@Override public String toString() {
		return "DelegateOntology[branch=" + branchPath + "]";
	}

	public void dispose() {
		manager.removeOntology(plusOntology);
		manager.removeOntology(this);
	}
}