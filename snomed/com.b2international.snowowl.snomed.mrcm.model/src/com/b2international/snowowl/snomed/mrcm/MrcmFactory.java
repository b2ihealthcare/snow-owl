/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.mrcm;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage
 * @generated
 */
public interface MrcmFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MrcmFactory eINSTANCE = com.b2international.snowowl.snomed.mrcm.impl.MrcmFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Concept Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Concept Model</em>'.
	 * @generated
	 */
	ConceptModel createConceptModel();

	/**
	 * Returns a new object of class '<em>Concept Model Component</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Concept Model Component</em>'.
	 * @generated
	 */
	ConceptModelComponent createConceptModelComponent();

	/**
	 * Returns a new object of class '<em>Cardinality Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Cardinality Predicate</em>'.
	 * @generated
	 */
	CardinalityPredicate createCardinalityPredicate();

	/**
	 * Returns a new object of class '<em>Concrete Domain Element Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Concrete Domain Element Predicate</em>'.
	 * @generated
	 */
	ConcreteDomainElementPredicate createConcreteDomainElementPredicate();

	/**
	 * Returns a new object of class '<em>Dependency Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Dependency Predicate</em>'.
	 * @generated
	 */
	DependencyPredicate createDependencyPredicate();

	/**
	 * Returns a new object of class '<em>Description Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Description Predicate</em>'.
	 * @generated
	 */
	DescriptionPredicate createDescriptionPredicate();

	/**
	 * Returns a new object of class '<em>Relationship Predicate</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Relationship Predicate</em>'.
	 * @generated
	 */
	RelationshipPredicate createRelationshipPredicate();

	/**
	 * Returns a new object of class '<em>Composite Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Composite Concept Set Definition</em>'.
	 * @generated
	 */
	CompositeConceptSetDefinition createCompositeConceptSetDefinition();

	/**
	 * Returns a new object of class '<em>Enumerated Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Enumerated Concept Set Definition</em>'.
	 * @generated
	 */
	EnumeratedConceptSetDefinition createEnumeratedConceptSetDefinition();

	/**
	 * Returns a new object of class '<em>Hierarchy Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Hierarchy Concept Set Definition</em>'.
	 * @generated
	 */
	HierarchyConceptSetDefinition createHierarchyConceptSetDefinition();

	/**
	 * Returns a new object of class '<em>Reference Set Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Reference Set Concept Set Definition</em>'.
	 * @generated
	 */
	ReferenceSetConceptSetDefinition createReferenceSetConceptSetDefinition();

	/**
	 * Returns a new object of class '<em>Relationship Concept Set Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Relationship Concept Set Definition</em>'.
	 * @generated
	 */
	RelationshipConceptSetDefinition createRelationshipConceptSetDefinition();

	/**
	 * Returns a new object of class '<em>Attribute Constraint</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attribute Constraint</em>'.
	 * @generated
	 */
	AttributeConstraint createAttributeConstraint();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	MrcmPackage getMrcmPackage();

} //MrcmFactory
