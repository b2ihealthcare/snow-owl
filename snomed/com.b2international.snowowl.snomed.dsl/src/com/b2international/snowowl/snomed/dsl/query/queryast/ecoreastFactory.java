/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.dsl.query.queryast;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage
 * @generated
 */
public interface ecoreastFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ecoreastFactory eINSTANCE = com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Concept Ref</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Concept Ref</em>'.
	 * @generated
	 */
	ConceptRef createConceptRef();

	/**
	 * Returns a new object of class '<em>Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Ref Set</em>'.
	 * @generated
	 */
	RefSet createRefSet();

	/**
	 * Returns a new object of class '<em>Not Clause</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Not Clause</em>'.
	 * @generated
	 */
	NotClause createNotClause();

	/**
	 * Returns a new object of class '<em>Sub Expression</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Sub Expression</em>'.
	 * @generated
	 */
	SubExpression createSubExpression();

	/**
	 * Returns a new object of class '<em>And Clause</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>And Clause</em>'.
	 * @generated
	 */
	AndClause createAndClause();

	/**
	 * Returns a new object of class '<em>Or Clause</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Or Clause</em>'.
	 * @generated
	 */
	OrClause createOrClause();

	/**
	 * Returns a new object of class '<em>Attribute Clause</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attribute Clause</em>'.
	 * @generated
	 */
	AttributeClause createAttributeClause();

	/**
	 * Returns a new object of class '<em>Attribute Clause Group</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attribute Clause Group</em>'.
	 * @generated
	 */
	AttributeClauseGroup createAttributeClauseGroup();

	/**
	 * Returns a new object of class '<em>Numeric Data Clause</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Numeric Data Clause</em>'.
	 * @generated
	 */
	NumericDataClause createNumericDataClause();

	/**
	 * Returns a new object of class '<em>Numeric Data Group Clause</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Numeric Data Group Clause</em>'.
	 * @generated
	 */
	NumericDataGroupClause createNumericDataGroupClause();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ecoreastPackage getecoreastPackage();

} //ecoreastFactory