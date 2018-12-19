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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.*;

import com.google.common.base.Joiner;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectImpl;

/**
 * A stub superclass for OWLOntologies. All methods that are not overridden are
 * considered unimplemented and will throw UnsupportedOperationExceptions.
 * 
 * @since 7.0
 */
public abstract class DelegateOntologyStub extends OWLObjectImpl implements OWLOntology {

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

	public void accept(OWLObjectVisitor arg0) {
		unsupportedOntology.accept(arg0);
	}

	public <O> O accept(OWLObjectVisitorEx<O> arg0) {
		return unsupportedOntology.accept(arg0);
	}

	public boolean containsAnnotationPropertyInSignature(IRI arg0, boolean arg1) {
		return unsupportedOntology.containsAnnotationPropertyInSignature(arg0, arg1);
	}

	public boolean containsAnnotationPropertyInSignature(IRI arg0) {
		return unsupportedOntology.containsAnnotationPropertyInSignature(arg0);
	}

	public boolean containsAxiom(OWLAxiom arg0, boolean arg1) {
		return unsupportedOntology.containsAxiom(arg0, arg1);
	}

	public boolean containsAxiom(OWLAxiom arg0) {
		return unsupportedOntology.containsAxiom(arg0);
	}

	public boolean containsAxiomIgnoreAnnotations(OWLAxiom arg0, boolean arg1) {
		return unsupportedOntology.containsAxiomIgnoreAnnotations(arg0, arg1);
	}

	public boolean containsAxiomIgnoreAnnotations(OWLAxiom arg0) {
		return unsupportedOntology.containsAxiomIgnoreAnnotations(arg0);
	}

	public boolean containsClassInSignature(IRI arg0, boolean arg1) {
		return unsupportedOntology.containsClassInSignature(arg0, arg1);
	}

	public boolean containsClassInSignature(IRI arg0) {
		return unsupportedOntology.containsClassInSignature(arg0);
	}

	public boolean containsDataPropertyInSignature(IRI arg0, boolean arg1) {
		return unsupportedOntology.containsDataPropertyInSignature(arg0, arg1);
	}

	public boolean containsDataPropertyInSignature(IRI arg0) {
		return unsupportedOntology.containsDataPropertyInSignature(arg0);
	}

	public boolean containsDatatypeInSignature(IRI arg0, boolean arg1) {
		return unsupportedOntology.containsDatatypeInSignature(arg0, arg1);
	}

	public boolean containsDatatypeInSignature(IRI arg0) {
		return unsupportedOntology.containsDatatypeInSignature(arg0);
	}

	public boolean containsEntityInSignature(IRI arg0, boolean arg1) {
		return unsupportedOntology.containsEntityInSignature(arg0, arg1);
	}

	public boolean containsEntityInSignature(IRI arg0) {
		return unsupportedOntology.containsEntityInSignature(arg0);
	}

	public boolean containsEntityInSignature(OWLEntity arg0, boolean arg1) {
		return unsupportedOntology.containsEntityInSignature(arg0, arg1);
	}

	public boolean containsEntityInSignature(OWLEntity arg0) {
		return unsupportedOntology.containsEntityInSignature(arg0);
	}

	public boolean containsIndividualInSignature(IRI arg0, boolean arg1) {
		return unsupportedOntology.containsIndividualInSignature(arg0, arg1);
	}

	public boolean containsIndividualInSignature(IRI arg0) {
		return unsupportedOntology.containsIndividualInSignature(arg0);
	}

	public boolean containsObjectPropertyInSignature(IRI arg0, boolean arg1) {
		return unsupportedOntology.containsObjectPropertyInSignature(arg0, arg1);
	}

	public boolean containsObjectPropertyInSignature(IRI arg0) {
		return unsupportedOntology.containsObjectPropertyInSignature(arg0);
	}

	public Set<OWLAxiom> getABoxAxioms(boolean arg0) {
		return unsupportedOntology.getABoxAxioms(arg0);
	}

	public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(OWLAnnotationSubject arg0) {
		return unsupportedOntology.getAnnotationAssertionAxioms(arg0);
	}

	public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature() {
		return unsupportedOntology.getAnnotationPropertiesInSignature();
	}

	public Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(OWLAnnotationProperty arg0) {
		return unsupportedOntology.getAnnotationPropertyDomainAxioms(arg0);
	}

	public Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(OWLAnnotationProperty arg0) {
		return unsupportedOntology.getAnnotationPropertyRangeAxioms(arg0);
	}

	public Set<OWLAnnotation> getAnnotations() {
		return unsupportedOntology.getAnnotations();
	}

	public Set<OWLAnonymousIndividual> getAnonymousIndividuals() {
		return unsupportedOntology.getAnonymousIndividuals();
	}

	public Set<OWLAsymmetricObjectPropertyAxiom> getAsymmetricObjectPropertyAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getAsymmetricObjectPropertyAxioms(arg0);
	}

	public int getAxiomCount() {
		return unsupportedOntology.getAxiomCount();
	}

	public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> arg0, boolean arg1) {
		return unsupportedOntology.getAxiomCount(arg0, arg1);
	}

	public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> arg0) {
		return unsupportedOntology.getAxiomCount(arg0);
	}

	public Set<OWLAxiom> getAxioms() {
		return unsupportedOntology.getAxioms();
	}

	public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> arg0, boolean arg1) {
		return unsupportedOntology.getAxioms(arg0, arg1);
	}

	public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> arg0) {
		return unsupportedOntology.getAxioms(arg0);
	}

	public Set<OWLAnnotationAxiom> getAxioms(OWLAnnotationProperty arg0) {
		return unsupportedOntology.getAxioms(arg0);
	}

	public Set<OWLClassAxiom> getAxioms(OWLClass arg0) {
		return unsupportedOntology.getAxioms(arg0);
	}

	public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty arg0) {
		return unsupportedOntology.getAxioms(arg0);
	}

	public Set<OWLDatatypeDefinitionAxiom> getAxioms(OWLDatatype arg0) {
		return unsupportedOntology.getAxioms(arg0);
	}

	public Set<OWLIndividualAxiom> getAxioms(OWLIndividual arg0) {
		return unsupportedOntology.getAxioms(arg0);
	}

	public Set<OWLObjectPropertyAxiom> getAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getAxioms(arg0);
	}

	public Set<OWLAxiom> getAxiomsIgnoreAnnotations(OWLAxiom arg0, boolean arg1) {
		return unsupportedOntology.getAxiomsIgnoreAnnotations(arg0, arg1);
	}

	public Set<OWLAxiom> getAxiomsIgnoreAnnotations(OWLAxiom arg0) {
		return unsupportedOntology.getAxiomsIgnoreAnnotations(arg0);
	}

	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLClassExpression arg0) {
		return unsupportedOntology.getClassAssertionAxioms(arg0);
	}

	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLIndividual arg0) {
		return unsupportedOntology.getClassAssertionAxioms(arg0);
	}

	public Set<OWLClass> getClassesInSignature() {
		return unsupportedOntology.getClassesInSignature();
	}

	public Set<OWLClass> getClassesInSignature(boolean arg0) {
		return unsupportedOntology.getClassesInSignature(arg0);
	}

	public Set<OWLDataProperty> getDataPropertiesInSignature() {
		return unsupportedOntology.getDataPropertiesInSignature();
	}

	public Set<OWLDataProperty> getDataPropertiesInSignature(boolean arg0) {
		return unsupportedOntology.getDataPropertiesInSignature(arg0);
	}

	public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms(OWLIndividual arg0) {
		return unsupportedOntology.getDataPropertyAssertionAxioms(arg0);
	}

	public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(OWLDataProperty arg0) {
		return unsupportedOntology.getDataPropertyDomainAxioms(arg0);
	}

	public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(OWLDataProperty arg0) {
		return unsupportedOntology.getDataPropertyRangeAxioms(arg0);
	}

	public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSubProperty(OWLDataProperty arg0) {
		return unsupportedOntology.getDataSubPropertyAxiomsForSubProperty(arg0);
	}

	public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSuperProperty(OWLDataPropertyExpression arg0) {
		return unsupportedOntology.getDataSubPropertyAxiomsForSuperProperty(arg0);
	}

	public Set<OWLDatatypeDefinitionAxiom> getDatatypeDefinitions(OWLDatatype arg0) {
		return unsupportedOntology.getDatatypeDefinitions(arg0);
	}

	public Set<OWLDatatype> getDatatypesInSignature() {
		return unsupportedOntology.getDatatypesInSignature();
	}

	public Set<OWLDatatype> getDatatypesInSignature(boolean arg0) {
		return unsupportedOntology.getDatatypesInSignature(arg0);
	}

	public Set<OWLDeclarationAxiom> getDeclarationAxioms(OWLEntity arg0) {
		return unsupportedOntology.getDeclarationAxioms(arg0);
	}

	public Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms(OWLIndividual arg0) {
		return unsupportedOntology.getDifferentIndividualAxioms(arg0);
	}

	public Set<OWLOntology> getDirectImports() throws UnknownOWLOntologyException {
		return unsupportedOntology.getDirectImports();
	}

	public Set<IRI> getDirectImportsDocuments() throws UnknownOWLOntologyException {
		return unsupportedOntology.getDirectImportsDocuments();
	}

	public Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms(OWLClass arg0) {
		return unsupportedOntology.getDisjointClassesAxioms(arg0);
	}

	public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms(OWLDataProperty arg0) {
		return unsupportedOntology.getDisjointDataPropertiesAxioms(arg0);
	}

	public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getDisjointObjectPropertiesAxioms(arg0);
	}

	public Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms(OWLClass arg0) {
		return unsupportedOntology.getDisjointUnionAxioms(arg0);
	}

	public Set<OWLEntity> getEntitiesInSignature(IRI arg0, boolean arg1) {
		return unsupportedOntology.getEntitiesInSignature(arg0, arg1);
	}

	public Set<OWLEntity> getEntitiesInSignature(IRI arg0) {
		return unsupportedOntology.getEntitiesInSignature(arg0);
	}

	public Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms(OWLClass arg0) {
		return unsupportedOntology.getEquivalentClassesAxioms(arg0);
	}

	public Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxioms(OWLDataProperty arg0) {
		return unsupportedOntology.getEquivalentDataPropertiesAxioms(arg0);
	}

	public Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms(
			OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getEquivalentObjectPropertiesAxioms(arg0);
	}

	public Set<OWLFunctionalDataPropertyAxiom> getFunctionalDataPropertyAxioms(OWLDataPropertyExpression arg0) {
		return unsupportedOntology.getFunctionalDataPropertyAxioms(arg0);
	}

	public Set<OWLFunctionalObjectPropertyAxiom> getFunctionalObjectPropertyAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getFunctionalObjectPropertyAxioms(arg0);
	}

	public Set<OWLClassAxiom> getGeneralClassAxioms() {
		return unsupportedOntology.getGeneralClassAxioms();
	}

	public Set<OWLHasKeyAxiom> getHasKeyAxioms(OWLClass arg0) {
		return unsupportedOntology.getHasKeyAxioms(arg0);
	}

	public Set<OWLOntology> getImports() throws UnknownOWLOntologyException {
		return unsupportedOntology.getImports();
	}

	public Set<OWLOntology> getImportsClosure() throws UnknownOWLOntologyException {
		return unsupportedOntology.getImportsClosure();
	}

	public Set<OWLImportsDeclaration> getImportsDeclarations() {
		return unsupportedOntology.getImportsDeclarations();
	}

	public Set<OWLNamedIndividual> getIndividualsInSignature() {
		return unsupportedOntology.getIndividualsInSignature();
	}

	public Set<OWLNamedIndividual> getIndividualsInSignature(boolean arg0) {
		return unsupportedOntology.getIndividualsInSignature(arg0);
	}

	public Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseFunctionalObjectPropertyAxioms(
			OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getInverseFunctionalObjectPropertyAxioms(arg0);
	}

	public Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getInverseObjectPropertyAxioms(arg0);
	}

	public Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getIrreflexiveObjectPropertyAxioms(arg0);
	}

	public int getLogicalAxiomCount() {
		return unsupportedOntology.getLogicalAxiomCount();
	}

	public Set<OWLLogicalAxiom> getLogicalAxioms() {
		return unsupportedOntology.getLogicalAxioms();
	}

	public Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms(OWLIndividual arg0) {
		return unsupportedOntology.getNegativeDataPropertyAssertionAxioms(arg0);
	}

	public Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms(OWLIndividual arg0) {
		return unsupportedOntology.getNegativeObjectPropertyAssertionAxioms(arg0);
	}

	public Set<OWLClassExpression> getNestedClassExpressions() {
		return unsupportedOntology.getNestedClassExpressions();
	}

	public OWLOntologyManager getOWLOntologyManager() {
		return unsupportedOntology.getOWLOntologyManager();
	}

	public Set<OWLObjectProperty> getObjectPropertiesInSignature() {
		return unsupportedOntology.getObjectPropertiesInSignature();
	}

	public Set<OWLObjectProperty> getObjectPropertiesInSignature(boolean arg0) {
		return unsupportedOntology.getObjectPropertiesInSignature(arg0);
	}

	public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms(OWLIndividual arg0) {
		return unsupportedOntology.getObjectPropertyAssertionAxioms(arg0);
	}

	public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getObjectPropertyDomainAxioms(arg0);
	}

	public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getObjectPropertyRangeAxioms(arg0);
	}

	public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSubProperty(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getObjectSubPropertyAxiomsForSubProperty(arg0);
	}

	public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSuperProperty(
			OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getObjectSubPropertyAxiomsForSuperProperty(arg0);
	}

	public OWLOntologyID getOntologyID() {
		return unsupportedOntology.getOntologyID();
	}

	public Set<OWLAxiom> getRBoxAxioms(boolean arg0) {
		return unsupportedOntology.getRBoxAxioms(arg0);
	}

	public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals() {
		return unsupportedOntology.getReferencedAnonymousIndividuals();
	}

	public Set<OWLAxiom> getReferencingAxioms(OWLAnonymousIndividual arg0) {
		return unsupportedOntology.getReferencingAxioms(arg0);
	}

	public Set<OWLAxiom> getReferencingAxioms(OWLEntity arg0, boolean arg1) {
		return unsupportedOntology.getReferencingAxioms(arg0, arg1);
	}

	public Set<OWLAxiom> getReferencingAxioms(OWLEntity arg0) {
		return unsupportedOntology.getReferencingAxioms(arg0);
	}

	public Set<OWLReflexiveObjectPropertyAxiom> getReflexiveObjectPropertyAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getReflexiveObjectPropertyAxioms(arg0);
	}

	public Set<OWLSameIndividualAxiom> getSameIndividualAxioms(OWLIndividual arg0) {
		return unsupportedOntology.getSameIndividualAxioms(arg0);
	}

	public Set<OWLEntity> getSignature() {
		return unsupportedOntology.getSignature();
	}

	public Set<OWLEntity> getSignature(boolean arg0) {
		return unsupportedOntology.getSignature(arg0);
	}

	public Set<OWLSubAnnotationPropertyOfAxiom> getSubAnnotationPropertyOfAxioms(OWLAnnotationProperty arg0) {
		return unsupportedOntology.getSubAnnotationPropertyOfAxioms(arg0);
	}

	public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSubClass(OWLClass arg0) {
		return unsupportedOntology.getSubClassAxiomsForSubClass(arg0);
	}

	public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSuperClass(OWLClass arg0) {
		return unsupportedOntology.getSubClassAxiomsForSuperClass(arg0);
	}

	public Set<OWLSymmetricObjectPropertyAxiom> getSymmetricObjectPropertyAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getSymmetricObjectPropertyAxioms(arg0);
	}

	public Set<OWLAxiom> getTBoxAxioms(boolean arg0) {
		return unsupportedOntology.getTBoxAxioms(arg0);
	}

	public Set<OWLTransitiveObjectPropertyAxiom> getTransitiveObjectPropertyAxioms(OWLObjectPropertyExpression arg0) {
		return unsupportedOntology.getTransitiveObjectPropertyAxioms(arg0);
	}

	public boolean isAnonymous() {
		return unsupportedOntology.isAnonymous();
	}

	public boolean isBottomEntity() {
		return unsupportedOntology.isBottomEntity();
	}

	public boolean isDeclared(OWLEntity arg0, boolean arg1) {
		return unsupportedOntology.isDeclared(arg0, arg1);
	}

	public boolean isDeclared(OWLEntity arg0) {
		return unsupportedOntology.isDeclared(arg0);
	}

	public boolean isEmpty() {
		return unsupportedOntology.isEmpty();
	}

	public boolean isTopEntity() {
		return unsupportedOntology.isTopEntity();
	}
}
