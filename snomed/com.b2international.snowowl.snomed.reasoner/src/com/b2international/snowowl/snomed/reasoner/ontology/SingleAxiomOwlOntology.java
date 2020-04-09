/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.AxiomAnnotations;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.parameters.Navigation;
import org.semanticweb.owlapi.util.OWLAxiomSearchFilter;

/**
 * @since 6.16
 */
final class SingleAxiomOwlOntology implements OWLMutableOntology {

	private final OWLOntologyID owlOntologyID = new OWLOntologyID();
	private final OWLOntologyManager manager;
	
	private OWLAxiom axiom;

	public SingleAxiomOwlOntology(OWLOntologyManager manager) {
		this.manager = manager;
	}
	
	@Override
	public ChangeApplied addAxiom(OWLAxiom axiom) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ChangeApplied addAxioms(Set<? extends OWLAxiom> axioms) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ChangeApplied applyChanges(List<? extends OWLOntologyChange> changes) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ChangeApplied applyChange(OWLOntologyChange change) {
		if (change instanceof AddAxiom) {
			axiom = change.getAxiom();
		}
		return ChangeApplied.SUCCESSFULLY;
	}
	
	@Override
	public ChangeDetails applyChangesAndGetDetails(List<? extends OWLOntologyChange> changes) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Set<OWLClassExpression> getNestedClassExpressions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void accept(OWLObjectVisitor visitor) {
	}

	@Override
	public <O> O accept(OWLObjectVisitorEx<O> visitor) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTopEntity() {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBottomEntity() {

		throw new UnsupportedOperationException();
	}

	@Override
	public int compareTo(OWLObject o) {

		return 0;
	}

	@Override
	public boolean containsEntityInSignature(OWLEntity owlEntity) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnonymousIndividual> getAnonymousIndividuals() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLClass> getClassesInSignature() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLObjectProperty> getObjectPropertiesInSignature() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDataProperty> getDataPropertiesInSignature() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLNamedIndividual> getIndividualsInSignature() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDatatype> getDatatypesInSignature() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getAxioms(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getAxiomCount(Imports includeImportsClosure) {

		return 0;
	}

	@Override
	public Set<OWLLogicalAxiom> getLogicalAxioms(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getLogicalAxiomCount(Imports includeImportsClosure) {

		return 0;
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType, Imports includeImportsClosure) {

		return 0;
	}

	@Override
	public boolean containsAxiom(OWLAxiom axiom, Imports includeImportsClosure, AxiomAnnotations ignoreAnnotations) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getAxiomsIgnoreAnnotations(OWLAxiom axiom, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getReferencingAxioms(OWLPrimitive owlEntity, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLClassAxiom> getAxioms(OWLClass cls, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLObjectPropertyAxiom> getAxioms(OWLObjectPropertyExpression property, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty property, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLIndividualAxiom> getAxioms(OWLIndividual individual, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnnotationAxiom> getAxioms(OWLAnnotationProperty property, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDatatypeDefinitionAxiom> getAxioms(OWLDatatype datatype, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getAxioms() {
		return Collections.singleton(axiom);
	}

	@Override
	public Set<OWLLogicalAxiom> getLogicalAxioms() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAxiom(OWLAxiom axiom) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getAxioms(boolean b) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getAxiomCount(boolean includeImportsClosure) {

		return 0;
	}

	@Override
	public Set<OWLLogicalAxiom> getLogicalAxioms(boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getLogicalAxiomCount(boolean includeImportsClosure) {

		return 0;
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType, boolean includeImportsClosure) {

		return 0;
	}

	@Override
	public boolean containsAxiom(OWLAxiom axiom, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAxiomIgnoreAnnotations(OWLAxiom axiom, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getAxiomsIgnoreAnnotations(OWLAxiom axiom, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getReferencingAxioms(OWLPrimitive owlEntity, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLClassAxiom> getAxioms(OWLClass cls, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLObjectPropertyAxiom> getAxioms(OWLObjectPropertyExpression property, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty property, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLIndividualAxiom> getAxioms(OWLIndividual individual, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnnotationAxiom> getAxioms(OWLAnnotationProperty property, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDatatypeDefinitionAxiom> getAxioms(OWLDatatype datatype, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getAxiomCount() {

		return 0;
	}

	@Override
	public int getLogicalAxiomCount() {

		return 0;
	}

	@Override
	public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType) {

		return 0;
	}

	@Override
	public boolean containsAxiomIgnoreAnnotations(OWLAxiom axiom) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getAxiomsIgnoreAnnotations(OWLAxiom axiom) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getReferencingAxioms(OWLPrimitive owlEntity) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLClassAxiom> getAxioms(OWLClass cls) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLObjectPropertyAxiom> getAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLIndividualAxiom> getAxioms(OWLIndividual individual) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnnotationAxiom> getAxioms(OWLAnnotationProperty property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDatatypeDefinitionAxiom> getAxioms(OWLDatatype datatype) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLClass> getClassesInSignature(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLObjectProperty> getObjectPropertiesInSignature(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDataProperty> getDataPropertiesInSignature(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLNamedIndividual> getIndividualsInSignature(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDatatype> getDatatypesInSignature(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsEntityInSignature(OWLEntity owlEntity, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsEntityInSignature(IRI entityIRI, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsClassInSignature(IRI owlClassIRI, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsObjectPropertyInSignature(IRI owlObjectPropertyIRI, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsDataPropertyInSignature(IRI owlDataPropertyIRI, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAnnotationPropertyInSignature(IRI owlAnnotationPropertyIRI, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsDatatypeInSignature(IRI owlDatatypeIRI, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsIndividualInSignature(IRI owlIndividualIRI, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsDatatypeInSignature(IRI owlDatatypeIRI) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsEntityInSignature(IRI entityIRI) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsClassInSignature(IRI owlClassIRI) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsObjectPropertyInSignature(IRI owlObjectPropertyIRI) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsDataPropertyInSignature(IRI owlDataPropertyIRI) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAnnotationPropertyInSignature(IRI owlAnnotationPropertyIRI) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsIndividualInSignature(IRI owlIndividualIRI) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLEntity> getEntitiesInSignature(IRI iri, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<IRI> getPunnedIRIs(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsReference(OWLEntity entity, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsReference(OWLEntity entity) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLEntity> getEntitiesInSignature(IRI entityIRI) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLClass> getClassesInSignature(boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLObjectProperty> getObjectPropertiesInSignature(boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDataProperty> getDataPropertiesInSignature(boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLNamedIndividual> getIndividualsInSignature(boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals(boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDatatype> getDatatypesInSignature(boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature(boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsEntityInSignature(OWLEntity owlEntity, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsEntityInSignature(IRI entityIRI, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsClassInSignature(IRI owlClassIRI, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsObjectPropertyInSignature(IRI owlObjectPropertyIRI, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsDataPropertyInSignature(IRI owlDataPropertyIRI, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAnnotationPropertyInSignature(IRI owlAnnotationPropertyIRI, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsDatatypeInSignature(IRI owlDatatypeIRI, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsIndividualInSignature(IRI owlIndividualIRI, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLEntity> getEntitiesInSignature(IRI iri, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsReference(OWLEntity entity, boolean includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(Class<T> type, OWLObject entity, Imports includeImports, Navigation forSubPosition) {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends OWLAxiom> Collection<T> filterAxioms(OWLAxiomSearchFilter filter, Object key, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(OWLAxiomSearchFilter filter, Object key, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(Class<T> type, Class<? extends OWLObject> explicitClass, OWLObject entity, Imports includeImports,
			Navigation forSubPosition) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLSubAnnotationPropertyOfAxiom> getSubAnnotationPropertyOfAxioms(OWLAnnotationProperty subProperty) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(OWLAnnotationProperty property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(OWLAnnotationProperty property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDeclarationAxiom> getDeclarationAxioms(OWLEntity subject) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(OWLAnnotationSubject entity) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSubClass(OWLClass cls) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSuperClass(OWLClass cls) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms(OWLClass cls) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms(OWLClass cls) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms(OWLClass owlClass) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLHasKeyAxiom> getHasKeyAxioms(OWLClass cls) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSubProperty(OWLObjectPropertyExpression subProperty) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSuperProperty(OWLObjectPropertyExpression superProperty) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLFunctionalObjectPropertyAxiom> getFunctionalObjectPropertyAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseFunctionalObjectPropertyAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLSymmetricObjectPropertyAxiom> getSymmetricObjectPropertyAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAsymmetricObjectPropertyAxiom> getAsymmetricObjectPropertyAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLReflexiveObjectPropertyAxiom> getReflexiveObjectPropertyAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLTransitiveObjectPropertyAxiom> getTransitiveObjectPropertyAxioms(OWLObjectPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSubProperty(OWLDataProperty subProperty) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSuperProperty(OWLDataPropertyExpression superProperty) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(OWLDataProperty property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(OWLDataProperty property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxioms(OWLDataProperty property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms(OWLDataProperty property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLFunctionalDataPropertyAxiom> getFunctionalDataPropertyAxioms(OWLDataPropertyExpression property) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLIndividual individual) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLClassExpression ce) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms(OWLIndividual individual) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms(OWLIndividual individual) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms(OWLIndividual individual) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms(OWLIndividual individual) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLSameIndividualAxiom> getSameIndividualAxioms(OWLIndividual individual) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms(OWLIndividual individual) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLDatatypeDefinitionAxiom> getDatatypeDefinitions(OWLDatatype datatype) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void accept(OWLNamedObjectVisitor visitor) {


	}

	@Override
	public <O> O accept(OWLNamedObjectVisitorEx<O> visitor) {

		throw new UnsupportedOperationException();
	}

	@Override
	public OWLOntologyManager getOWLOntologyManager() {
		return manager;
	}

	@Override
	public void setOWLOntologyManager(OWLOntologyManager manager) {


	}

	@Override
	public OWLOntologyID getOntologyID() {
		return owlOntologyID;
	}

	@Override
	public boolean isAnonymous() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAnnotation> getAnnotations() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<IRI> getDirectImportsDocuments() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLOntology> getDirectImports() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLOntology> getImports() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLOntology> getImportsClosure() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLImportsDeclaration> getImportsDeclarations() {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getTBoxAxioms(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getABoxAxioms(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLAxiom> getRBoxAxioms(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLClassAxiom> getGeneralClassAxioms() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLEntity> getSignature() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OWLEntity> getSignature(Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDeclared(OWLEntity owlEntity) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDeclared(OWLEntity owlEntity, Imports includeImportsClosure) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void saveOntology() throws OWLOntologyStorageException {


	}

	@Override
	public void saveOntology(IRI documentIRI) throws OWLOntologyStorageException {


	}

	@Override
	public void saveOntology(OutputStream outputStream) throws OWLOntologyStorageException {


	}

	@Override
	public void saveOntology(OWLDocumentFormat ontologyFormat) throws OWLOntologyStorageException {


	}

	@Override
	public void saveOntology(OWLDocumentFormat ontologyFormat, IRI documentIRI) throws OWLOntologyStorageException {


	}

	@Override
	public void saveOntology(OWLDocumentFormat ontologyFormat, OutputStream outputStream) throws OWLOntologyStorageException {


	}

	@Override
	public void saveOntology(OWLOntologyDocumentTarget documentTarget) throws OWLOntologyStorageException {


	}

	@Override
	public void saveOntology(OWLDocumentFormat ontologyFormat, OWLOntologyDocumentTarget documentTarget) throws OWLOntologyStorageException {


	}

}
