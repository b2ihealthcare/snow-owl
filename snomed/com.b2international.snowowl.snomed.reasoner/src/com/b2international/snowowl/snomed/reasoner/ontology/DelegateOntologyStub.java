/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.AxiomAnnotations;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.parameters.Navigation;
import org.semanticweb.owlapi.util.OWLAxiomSearchFilter;

import com.google.common.base.Joiner;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectImplWithEntityAndAnonCaching;

/**
 * A stub superclass for OWLOntologies. All methods that are not overridden are
 * considered unimplemented and will throw UnsupportedOperationExceptions.
 * 
 * @since 7.0
 */
public abstract class DelegateOntologyStub extends OWLObjectImplWithEntityAndAnonCaching implements OWLOntology {

	protected static Object throwUnsupportedException(final Object proxy, final Method method, final Object[] args) {
		final String className = method.getDeclaringClass().getSimpleName();
		final String methodName = method.getName();
		final List<String> parameterTypes = Arrays.asList(method.getParameterTypes())
				.stream()
				.map(Class::getSimpleName)
				.collect(Collectors.toList());

		final StringBuilder sb = new StringBuilder();
		sb.append(className);
		sb.append("#");
		sb.append(methodName);
		sb.append("(");
		sb.append(Joiner.on(", ").join(parameterTypes));
		sb.append(")");
		throw new UnsupportedOperationException(sb.toString());
	}

	protected final OWLOntology unsupportedOntology;

	protected DelegateOntologyStub() {
		this.unsupportedOntology = (OWLOntology) Proxy.newProxyInstance(getClass().getClassLoader(), 
				new Class<?>[] { OWLOntology.class }, 
				DelegateOntology::throwUnsupportedException);
	}

	@Override
	public final boolean contains(final OWLAxiomSearchFilter filter, final Object key, final Imports includeImportsClosure) {
		return unsupportedOntology.contains(filter, key, includeImportsClosure);
	}

	@Override
	public final boolean containsAnnotationPropertyInSignature(final IRI owlAnnotationPropertyIRI) {
		return unsupportedOntology.containsAnnotationPropertyInSignature(owlAnnotationPropertyIRI);
	}

	@Override
	public final boolean containsAnnotationPropertyInSignature(final IRI owlAnnotationPropertyIRI, final boolean includeImportsClosure) {
		return unsupportedOntology.containsAnnotationPropertyInSignature(owlAnnotationPropertyIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsAnnotationPropertyInSignature(final IRI owlAnnotationPropertyIRI, final Imports includeImportsClosure) {
		return unsupportedOntology.containsAnnotationPropertyInSignature(owlAnnotationPropertyIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsAxiom(final OWLAxiom axiom) {
		return unsupportedOntology.containsAxiom(axiom);
	}

	@Override
	public final boolean containsAxiom(final OWLAxiom axiom, final boolean includeImportsClosure) {
		return unsupportedOntology.containsAxiom(axiom, includeImportsClosure);
	}

	@Override
	public final boolean containsAxiom(final OWLAxiom axiom, final Imports includeImportsClosure, final AxiomAnnotations ignoreAnnotations) {
		return unsupportedOntology.containsAxiom(axiom, includeImportsClosure, ignoreAnnotations);
	}

	@Override
	public final boolean containsAxiomIgnoreAnnotations(final OWLAxiom axiom) {
		return unsupportedOntology.containsAxiomIgnoreAnnotations(axiom);
	}

	@Override
	public final boolean containsAxiomIgnoreAnnotations(final OWLAxiom axiom, final boolean includeImportsClosure) {
		return unsupportedOntology.containsAxiomIgnoreAnnotations(axiom, includeImportsClosure);
	}

	@Override
	public final boolean containsClassInSignature(final IRI owlClassIRI) {
		return unsupportedOntology.containsClassInSignature(owlClassIRI);
	}

	@Override
	public final boolean containsClassInSignature(final IRI owlClassIRI, final boolean includeImportsClosure) {
		return unsupportedOntology.containsClassInSignature(owlClassIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsClassInSignature(final IRI owlClassIRI, final Imports includeImportsClosure) {
		return unsupportedOntology.containsClassInSignature(owlClassIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsDataPropertyInSignature(final IRI owlDataPropertyIRI) {
		return unsupportedOntology.containsDataPropertyInSignature(owlDataPropertyIRI);
	}

	@Override
	public final boolean containsDataPropertyInSignature(final IRI owlDataPropertyIRI, final boolean includeImportsClosure) {
		return unsupportedOntology.containsDataPropertyInSignature(owlDataPropertyIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsDataPropertyInSignature(final IRI owlDataPropertyIRI, final Imports includeImportsClosure) {
		return unsupportedOntology.containsDataPropertyInSignature(owlDataPropertyIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsDatatypeInSignature(final IRI owlDatatypeIRI) {
		return unsupportedOntology.containsDatatypeInSignature(owlDatatypeIRI);
	}

	@Override
	public final boolean containsDatatypeInSignature(final IRI owlDatatypeIRI, final boolean includeImportsClosure) {
		return unsupportedOntology.containsDatatypeInSignature(owlDatatypeIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsDatatypeInSignature(final IRI owlDatatypeIRI, final Imports includeImportsClosure) {
		return unsupportedOntology.containsDatatypeInSignature(owlDatatypeIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsEntityInSignature(final IRI entityIRI) {
		return unsupportedOntology.containsEntityInSignature(entityIRI);
	}

	@Override
	public final boolean containsEntityInSignature(final IRI entityIRI, final boolean includeImportsClosure) {
		return unsupportedOntology.containsEntityInSignature(entityIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsEntityInSignature(final IRI entityIRI, final Imports includeImportsClosure) {
		return unsupportedOntology.containsEntityInSignature(entityIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsEntityInSignature(final OWLEntity owlEntity, final boolean includeImportsClosure) {
		return unsupportedOntology.containsEntityInSignature(owlEntity, includeImportsClosure);
	}

	@Override
	public final boolean containsEntityInSignature(final OWLEntity owlEntity, final Imports includeImportsClosure) {
		return unsupportedOntology.containsEntityInSignature(owlEntity, includeImportsClosure);
	}

	@Override
	public final boolean containsIndividualInSignature(final IRI owlIndividualIRI) {
		return unsupportedOntology.containsIndividualInSignature(owlIndividualIRI);
	}

	@Override
	public final boolean containsIndividualInSignature(final IRI owlIndividualIRI, final boolean includeImportsClosure) {
		return unsupportedOntology.containsIndividualInSignature(owlIndividualIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsIndividualInSignature(final IRI owlIndividualIRI, final Imports includeImportsClosure) {
		return unsupportedOntology.containsIndividualInSignature(owlIndividualIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsObjectPropertyInSignature(final IRI owlObjectPropertyIRI) {
		return unsupportedOntology.containsObjectPropertyInSignature(owlObjectPropertyIRI);
	}

	@Override
	public final boolean containsObjectPropertyInSignature(final IRI owlObjectPropertyIRI, final boolean includeImportsClosure) {
		return unsupportedOntology.containsObjectPropertyInSignature(owlObjectPropertyIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsObjectPropertyInSignature(final IRI owlObjectPropertyIRI, final Imports includeImportsClosure) {
		return unsupportedOntology.containsObjectPropertyInSignature(owlObjectPropertyIRI, includeImportsClosure);
	}

	@Override
	public final boolean containsReference(final OWLEntity entity) {
		return unsupportedOntology.containsReference(entity);
	}

	@Override
	public final boolean containsReference(final OWLEntity entity, final boolean includeImportsClosure) {
		return unsupportedOntology.containsReference(entity, includeImportsClosure);
	}

	@Override
	public final boolean containsReference(final OWLEntity entity, final Imports includeImportsClosure) {
		return unsupportedOntology.containsReference(entity, includeImportsClosure);
	}

	@Override
	public final <T extends OWLAxiom> Collection<T> filterAxioms(final OWLAxiomSearchFilter filter, final Object key, final Imports includeImportsClosure) {
		return unsupportedOntology.filterAxioms(filter, key, includeImportsClosure);
	}

	@Override
	public final Set<OWLAxiom> getABoxAxioms(final Imports includeImportsClosure) {
		return unsupportedOntology.getABoxAxioms(includeImportsClosure);
	}

	@Override
	public final Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(final OWLAnnotationSubject entity) {
		return unsupportedOntology.getAnnotationAssertionAxioms(entity);
	}

	@Override
	public final Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature(final boolean includeImportsClosure) {
		return unsupportedOntology.getAnnotationPropertiesInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature(final Imports includeImportsClosure) {
		return unsupportedOntology.getAnnotationPropertiesInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(final OWLAnnotationProperty property) {
		return unsupportedOntology.getAnnotationPropertyDomainAxioms(property);
	}

	@Override
	public final Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(final OWLAnnotationProperty property) {
		return unsupportedOntology.getAnnotationPropertyRangeAxioms(property);
	}

	@Override
	public final Set<OWLAnnotation> getAnnotations() {
		return unsupportedOntology.getAnnotations();
	}

	@Override
	public final Set<OWLAsymmetricObjectPropertyAxiom> getAsymmetricObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getAsymmetricObjectPropertyAxioms(property);
	}

	@Override
	public <T extends OWLAxiom> int getAxiomCount(final AxiomType<T> axiomType) {
		return unsupportedOntology.getAxiomCount(axiomType);
	}

	@Override
	public final <T extends OWLAxiom> int getAxiomCount(final AxiomType<T> axiomType, final boolean includeImportsClosure) {
		return unsupportedOntology.getAxiomCount(axiomType, includeImportsClosure);
	}

	@Override
	public final <T extends OWLAxiom> int getAxiomCount(final AxiomType<T> axiomType, final Imports includeImportsClosure) {
		return unsupportedOntology.getAxiomCount(axiomType, includeImportsClosure);
	}

	@Override
	public final int getAxiomCount(final boolean includeImportsClosure) {
		return unsupportedOntology.getAxiomCount(includeImportsClosure);
	}

	@Override
	public final int getAxiomCount(final Imports includeImportsClosure) {
		return unsupportedOntology.getAxiomCount(includeImportsClosure);
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(final AxiomType<T> axiomType) {
		return unsupportedOntology.getAxioms(axiomType);
	}

	@Override
	public final <T extends OWLAxiom> Set<T> getAxioms(final AxiomType<T> axiomType, final boolean includeImportsClosure) {
		return unsupportedOntology.getAxioms(axiomType, includeImportsClosure);
	}

	@Override
	public final <T extends OWLAxiom> Set<T> getAxioms(final AxiomType<T> axiomType, final Imports includeImportsClosure) {
		return unsupportedOntology.getAxioms(axiomType, includeImportsClosure);
	}

	@Override
	public final Set<OWLAxiom> getAxioms(final boolean b) {
		return unsupportedOntology.getAxioms(b);
	}

	@Override
	public final <T extends OWLAxiom> Set<T> getAxioms(final Class<T> type, final Class<? extends OWLObject> explicitClass, final OWLObject entity, final Imports includeImports, final Navigation forSubPosition) {
		return unsupportedOntology.getAxioms(type, explicitClass, entity, includeImports, forSubPosition);
	}

	@Override
	public final <T extends OWLAxiom> Set<T> getAxioms(final Class<T> type, final OWLObject entity, final Imports includeImports, final Navigation forSubPosition) {
		return unsupportedOntology.getAxioms(type, entity, includeImports, forSubPosition);
	}

	@Override
	public final Set<OWLAxiom> getAxioms(final Imports includeImportsClosure) {
		return unsupportedOntology.getAxioms(includeImportsClosure);
	}

	@Override
	public final Set<OWLAnnotationAxiom> getAxioms(final OWLAnnotationProperty property) {
		return unsupportedOntology.getAxioms(property);
	}

	@Override
	public final Set<OWLAnnotationAxiom> getAxioms(final OWLAnnotationProperty property, final boolean includeImportsClosure) {
		return unsupportedOntology.getAxioms(property, includeImportsClosure);
	}

	@Override
	public final Set<OWLAnnotationAxiom> getAxioms(final OWLAnnotationProperty property, final Imports includeImportsClosure) {
		return unsupportedOntology.getAxioms(property, includeImportsClosure);
	}

	@Override
	public final Set<OWLClassAxiom> getAxioms(final OWLClass cls) {
		return unsupportedOntology.getAxioms(cls);
	}

	@Override
	public final Set<OWLClassAxiom> getAxioms(final OWLClass cls, final boolean includeImportsClosure) {
		return unsupportedOntology.getAxioms(cls, includeImportsClosure);
	}

	@Override
	public final Set<OWLClassAxiom> getAxioms(final OWLClass cls, final Imports includeImportsClosure) {
		return unsupportedOntology.getAxioms(cls, includeImportsClosure);
	}

	@Override
	public final Set<OWLDataPropertyAxiom> getAxioms(final OWLDataProperty property) {
		return unsupportedOntology.getAxioms(property);
	}

	@Override
	public final Set<OWLDataPropertyAxiom> getAxioms(final OWLDataProperty property, final boolean includeImportsClosure) {
		return unsupportedOntology.getAxioms(property, includeImportsClosure);
	}

	@Override
	public final Set<OWLDataPropertyAxiom> getAxioms(final OWLDataProperty property, final Imports includeImportsClosure) {
		return unsupportedOntology.getAxioms(property, includeImportsClosure);
	}

	@Override
	public final Set<OWLDatatypeDefinitionAxiom> getAxioms(final OWLDatatype datatype) {
		return unsupportedOntology.getAxioms(datatype);
	}

	@Override
	public final Set<OWLDatatypeDefinitionAxiom> getAxioms(final OWLDatatype datatype, final boolean includeImportsClosure) {
		return unsupportedOntology.getAxioms(datatype, includeImportsClosure);
	}

	@Override
	public final Set<OWLDatatypeDefinitionAxiom> getAxioms(final OWLDatatype datatype, final Imports includeImportsClosure) {
		return unsupportedOntology.getAxioms(datatype, includeImportsClosure);
	}

	@Override
	public final Set<OWLIndividualAxiom> getAxioms(final OWLIndividual individual) {
		return unsupportedOntology.getAxioms(individual);
	}

	@Override
	public final Set<OWLIndividualAxiom> getAxioms(final OWLIndividual individual, final boolean includeImportsClosure) {
		return unsupportedOntology.getAxioms(individual, includeImportsClosure);
	}

	@Override
	public final Set<OWLIndividualAxiom> getAxioms(final OWLIndividual individual, final Imports includeImportsClosure) {
		return unsupportedOntology.getAxioms(individual, includeImportsClosure);
	}

	@Override
	public final Set<OWLObjectPropertyAxiom> getAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getAxioms(property);
	}

	@Override
	public final Set<OWLObjectPropertyAxiom> getAxioms(final OWLObjectPropertyExpression property, final boolean includeImportsClosure) {
		return unsupportedOntology.getAxioms(property, includeImportsClosure);
	}

	@Override
	public final Set<OWLObjectPropertyAxiom> getAxioms(final OWLObjectPropertyExpression property, final Imports includeImportsClosure) {
		return unsupportedOntology.getAxioms(property, includeImportsClosure);
	}

	@Override
	public final Set<OWLAxiom> getAxiomsIgnoreAnnotations(final OWLAxiom axiom) {
		return unsupportedOntology.getAxiomsIgnoreAnnotations(axiom);
	}

	@Override
	public final Set<OWLAxiom> getAxiomsIgnoreAnnotations(final OWLAxiom axiom, final boolean includeImportsClosure) {
		return unsupportedOntology.getAxiomsIgnoreAnnotations(axiom, includeImportsClosure);
	}

	@Override
	public final Set<OWLAxiom> getAxiomsIgnoreAnnotations(final OWLAxiom axiom, final Imports includeImportsClosure) {
		return unsupportedOntology.getAxiomsIgnoreAnnotations(axiom, includeImportsClosure);
	}

	@Override
	public final Set<OWLClassAssertionAxiom> getClassAssertionAxioms(final OWLClassExpression ce) {
		return unsupportedOntology.getClassAssertionAxioms(ce);
	}

	@Override
	public final Set<OWLClassAssertionAxiom> getClassAssertionAxioms(final OWLIndividual individual) {
		return unsupportedOntology.getClassAssertionAxioms(individual);
	}

	@Override
	public final Set<OWLClass> getClassesInSignature(final boolean includeImportsClosure) {
		return unsupportedOntology.getClassesInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLClass> getClassesInSignature(final Imports includeImportsClosure) {
		return unsupportedOntology.getClassesInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLDataProperty> getDataPropertiesInSignature(final boolean includeImportsClosure) {
		return unsupportedOntology.getDataPropertiesInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLDataProperty> getDataPropertiesInSignature(final Imports includeImportsClosure) {
		return unsupportedOntology.getDataPropertiesInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms(final OWLIndividual individual) {
		return unsupportedOntology.getDataPropertyAssertionAxioms(individual);
	}

	@Override
	public final Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(final OWLDataProperty property) {
		return unsupportedOntology.getDataPropertyDomainAxioms(property);
	}

	@Override
	public final Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(final OWLDataProperty property) {
		return unsupportedOntology.getDataPropertyRangeAxioms(property);
	}

	@Override
	public final Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSubProperty(final OWLDataProperty subProperty) {
		return unsupportedOntology.getDataSubPropertyAxiomsForSubProperty(subProperty);
	}

	@Override
	public final Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSuperProperty(final OWLDataPropertyExpression superProperty) {
		return unsupportedOntology.getDataSubPropertyAxiomsForSuperProperty(superProperty);
	}

	@Override
	public final Set<OWLDatatypeDefinitionAxiom> getDatatypeDefinitions(final OWLDatatype datatype) {
		return unsupportedOntology.getDatatypeDefinitions(datatype);
	}

	@Override
	public final Set<OWLDatatype> getDatatypesInSignature(final boolean includeImportsClosure) {
		return unsupportedOntology.getDatatypesInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLDatatype> getDatatypesInSignature(final Imports includeImportsClosure) {
		return unsupportedOntology.getDatatypesInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLDeclarationAxiom> getDeclarationAxioms(final OWLEntity subject) {
		return unsupportedOntology.getDeclarationAxioms(subject);
	}

	@Override
	public final Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms(final OWLIndividual individual) {
		return unsupportedOntology.getDifferentIndividualAxioms(individual);
	}

	@Override
	public final Set<OWLOntology> getDirectImports() {
		return unsupportedOntology.getDirectImports();
	}

	@Override
	public final Set<IRI> getDirectImportsDocuments() {
		return unsupportedOntology.getDirectImportsDocuments();
	}

	@Override
	public final Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms(final OWLClass cls) {
		return unsupportedOntology.getDisjointClassesAxioms(cls);
	}

	@Override
	public final Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms(final OWLDataProperty property) {
		return unsupportedOntology.getDisjointDataPropertiesAxioms(property);
	}

	@Override
	public final Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getDisjointObjectPropertiesAxioms(property);
	}

	@Override
	public final Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms(final OWLClass owlClass) {
		return unsupportedOntology.getDisjointUnionAxioms(owlClass);
	}

	@Override
	public final Set<OWLEntity> getEntitiesInSignature(final IRI entityIRI) {
		return unsupportedOntology.getEntitiesInSignature(entityIRI);
	}

	@Override
	public final Set<OWLEntity> getEntitiesInSignature(final IRI iri, final boolean includeImportsClosure) {
		return unsupportedOntology.getEntitiesInSignature(iri, includeImportsClosure);
	}

	@Override
	public final Set<OWLEntity> getEntitiesInSignature(final IRI iri, final Imports includeImportsClosure) {
		return unsupportedOntology.getEntitiesInSignature(iri, includeImportsClosure);
	}

	@Override
	public final Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms(final OWLClass cls) {
		return unsupportedOntology.getEquivalentClassesAxioms(cls);
	}

	@Override
	public final Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxioms(final OWLDataProperty property) {
		return unsupportedOntology.getEquivalentDataPropertiesAxioms(property);
	}

	@Override
	public final Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getEquivalentObjectPropertiesAxioms(property);
	}

	@Override
	public final Set<OWLFunctionalDataPropertyAxiom> getFunctionalDataPropertyAxioms(final OWLDataPropertyExpression property) {
		return unsupportedOntology.getFunctionalDataPropertyAxioms(property);
	}

	@Override
	public final Set<OWLFunctionalObjectPropertyAxiom> getFunctionalObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getFunctionalObjectPropertyAxioms(property);
	}

	@Override
	public final Set<OWLClassAxiom> getGeneralClassAxioms() {
		return unsupportedOntology.getGeneralClassAxioms();
	}

	@Override
	public final Set<OWLHasKeyAxiom> getHasKeyAxioms(final OWLClass cls) {
		return unsupportedOntology.getHasKeyAxioms(cls);
	}

	@Override
	public final Set<OWLOntology> getImports() {
		return unsupportedOntology.getImports();
	}

	@Override
	public final Set<OWLImportsDeclaration> getImportsDeclarations() {
		return unsupportedOntology.getImportsDeclarations();
	}

	@Override
	public final Set<OWLNamedIndividual> getIndividualsInSignature(final boolean includeImportsClosure) {
		return unsupportedOntology.getIndividualsInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLNamedIndividual> getIndividualsInSignature(final Imports includeImportsClosure) {
		return unsupportedOntology.getIndividualsInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseFunctionalObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getInverseFunctionalObjectPropertyAxioms(property);
	}

	@Override
	public final Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getInverseObjectPropertyAxioms(property);
	}

	@Override
	public final Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getIrreflexiveObjectPropertyAxioms(property);
	}

	@Override
	public int getLogicalAxiomCount() {
		return unsupportedOntology.getLogicalAxiomCount();
	}

	@Override
	public final int getLogicalAxiomCount(final boolean includeImportsClosure) {
		return unsupportedOntology.getLogicalAxiomCount(includeImportsClosure);
	}

	@Override
	public final int getLogicalAxiomCount(final Imports includeImportsClosure) {
		return unsupportedOntology.getLogicalAxiomCount(includeImportsClosure);
	}

	@Override
	public Set<OWLLogicalAxiom> getLogicalAxioms() {
		return unsupportedOntology.getLogicalAxioms();
	}

	@Override
	public final Set<OWLLogicalAxiom> getLogicalAxioms(final boolean includeImportsClosure) {
		return unsupportedOntology.getLogicalAxioms(includeImportsClosure);
	}

	@Override
	public final Set<OWLLogicalAxiom> getLogicalAxioms(final Imports includeImportsClosure) {
		return unsupportedOntology.getLogicalAxioms(includeImportsClosure);
	}

	@Override
	public final Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms(final OWLIndividual individual) {
		return unsupportedOntology.getNegativeDataPropertyAssertionAxioms(individual);
	}

	@Override
	public final Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms(final OWLIndividual individual) {
		return unsupportedOntology.getNegativeObjectPropertyAssertionAxioms(individual);
	}

	@Override
	public final Set<OWLObjectProperty> getObjectPropertiesInSignature(final boolean includeImportsClosure) {
		return unsupportedOntology.getObjectPropertiesInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLObjectProperty> getObjectPropertiesInSignature(final Imports includeImportsClosure) {
		return unsupportedOntology.getObjectPropertiesInSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms(final OWLIndividual individual) {
		return unsupportedOntology.getObjectPropertyAssertionAxioms(individual);
	}

	@Override
	public final Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getObjectPropertyDomainAxioms(property);
	}

	@Override
	public final Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getObjectPropertyRangeAxioms(property);
	}

	@Override
	public final Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSubProperty(final OWLObjectPropertyExpression subProperty) {
		return unsupportedOntology.getObjectSubPropertyAxiomsForSubProperty(subProperty);
	}

	@Override
	public final Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSuperProperty(final OWLObjectPropertyExpression superProperty) {
		return unsupportedOntology.getObjectSubPropertyAxiomsForSuperProperty(superProperty);
	}

	@Override
	public final Set<IRI> getPunnedIRIs(final Imports includeImportsClosure) {
		return unsupportedOntology.getPunnedIRIs(includeImportsClosure);
	}

	@Override
	public final Set<OWLAxiom> getRBoxAxioms(final Imports includeImportsClosure) {
		return unsupportedOntology.getRBoxAxioms(includeImportsClosure);
	}

	@Override
	public final Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals(final boolean includeImportsClosure) {
		return unsupportedOntology.getReferencedAnonymousIndividuals(includeImportsClosure);
	}

	@Override
	public final Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals(final Imports includeImportsClosure) {
		return unsupportedOntology.getReferencedAnonymousIndividuals(includeImportsClosure);
	}

	@Override
	public final Set<OWLAxiom> getReferencingAxioms(final OWLPrimitive owlEntity) {
		return unsupportedOntology.getReferencingAxioms(owlEntity);
	}

	@Override
	public final Set<OWLAxiom> getReferencingAxioms(final OWLPrimitive owlEntity, final boolean includeImportsClosure) {
		return unsupportedOntology.getReferencingAxioms(owlEntity, includeImportsClosure);
	}

	@Override
	public final Set<OWLAxiom> getReferencingAxioms(final OWLPrimitive owlEntity, final Imports includeImportsClosure) {
		return unsupportedOntology.getReferencingAxioms(owlEntity, includeImportsClosure);
	}

	@Override
	public final Set<OWLReflexiveObjectPropertyAxiom> getReflexiveObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getReflexiveObjectPropertyAxioms(property);
	}

	@Override
	public final Set<OWLSameIndividualAxiom> getSameIndividualAxioms(final OWLIndividual individual) {
		return unsupportedOntology.getSameIndividualAxioms(individual);
	}

	@Override
	public final Set<OWLEntity> getSignature(final Imports includeImportsClosure) {
		return unsupportedOntology.getSignature(includeImportsClosure);
	}

	@Override
	public final Set<OWLSubAnnotationPropertyOfAxiom> getSubAnnotationPropertyOfAxioms(final OWLAnnotationProperty subProperty) {
		return unsupportedOntology.getSubAnnotationPropertyOfAxioms(subProperty);
	}

	@Override
	public final Set<OWLSubClassOfAxiom> getSubClassAxiomsForSubClass(final OWLClass cls) {
		return unsupportedOntology.getSubClassAxiomsForSubClass(cls);
	}

	@Override
	public final Set<OWLSubClassOfAxiom> getSubClassAxiomsForSuperClass(final OWLClass cls) {
		return unsupportedOntology.getSubClassAxiomsForSuperClass(cls);
	}

	@Override
	public final Set<OWLSymmetricObjectPropertyAxiom> getSymmetricObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getSymmetricObjectPropertyAxioms(property);
	}

	@Override
	public final Set<OWLAxiom> getTBoxAxioms(final Imports includeImportsClosure) {
		return unsupportedOntology.getTBoxAxioms(includeImportsClosure);
	}

	@Override
	public final Set<OWLTransitiveObjectPropertyAxiom> getTransitiveObjectPropertyAxioms(final OWLObjectPropertyExpression property) {
		return unsupportedOntology.getTransitiveObjectPropertyAxioms(property);
	}

	@Override
	public final boolean isDeclared(final OWLEntity owlEntity) {
		return unsupportedOntology.isDeclared(owlEntity);
	}

	@Override
	public final boolean isDeclared(final OWLEntity owlEntity, final Imports includeImportsClosure) {
		return unsupportedOntology.isDeclared(owlEntity, includeImportsClosure);
	}

	@Override
	public final boolean isEmpty() {
		return unsupportedOntology.isEmpty();
	}

	@Override
	public final void saveOntology() throws OWLOntologyStorageException {
		unsupportedOntology.saveOntology();
	}

	@Override
	public final void saveOntology(final IRI documentIRI) throws OWLOntologyStorageException {
		unsupportedOntology.saveOntology(documentIRI);
	}

	@Override
	public final void saveOntology(final OutputStream outputStream) throws OWLOntologyStorageException {
		unsupportedOntology.saveOntology(outputStream);
	}

	@Override
	public final void saveOntology(final OWLDocumentFormat ontologyFormat) throws OWLOntologyStorageException {
		unsupportedOntology.saveOntology(ontologyFormat);
	}

	@Override
	public final void saveOntology(final OWLDocumentFormat ontologyFormat, final IRI documentIRI) throws OWLOntologyStorageException {
		unsupportedOntology.saveOntology(ontologyFormat, documentIRI);
	}

	@Override
	public final void saveOntology(final OWLDocumentFormat ontologyFormat, final OutputStream outputStream) throws OWLOntologyStorageException {
		unsupportedOntology.saveOntology(ontologyFormat, outputStream);
	}

	@Override
	public final void saveOntology(final OWLDocumentFormat ontologyFormat, final OWLOntologyDocumentTarget documentTarget) throws OWLOntologyStorageException {
		unsupportedOntology.saveOntology(ontologyFormat, documentTarget);
	}

	@Override
	public final void saveOntology(final OWLOntologyDocumentTarget documentTarget) throws OWLOntologyStorageException {
		unsupportedOntology.saveOntology(documentTarget);
	}

	@Override
	public final void setOWLOntologyManager(final OWLOntologyManager manager) {
		unsupportedOntology.setOWLOntologyManager(manager);
	}
}