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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Numeric Data Clause</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getConcepts <em>Concepts</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getOperator <em>Operator</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getValue <em>Value</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getUnitType <em>Unit Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getNumericDataClause()
 * @model
 * @generated
 */
public interface NumericDataClause extends RValue {
	/**
	 * Returns the value of the '<em><b>Operator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operator</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operator</em>' attribute.
	 * @see #setOperator(String)
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getNumericDataClause_Operator()
	 * @model required="true"
	 * @generated
	 */
	String getOperator();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getOperator <em>Operator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operator</em>' attribute.
	 * @see #getOperator()
	 * @generated
	 */
	void setOperator(String value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(int)
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getNumericDataClause_Value()
	 * @model required="true"
	 * @generated
	 */
	int getValue();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(int value);

	/**
	 * Returns the value of the '<em><b>Unit Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Unit Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unit Type</em>' attribute.
	 * @see #setUnitType(String)
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getNumericDataClause_UnitType()
	 * @model required="true"
	 * @generated
	 */
	String getUnitType();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getUnitType <em>Unit Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unit Type</em>' attribute.
	 * @see #getUnitType()
	 * @generated
	 */
	void setUnitType(String value);

	/**
	 * Returns the value of the '<em><b>Concepts</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Concepts</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Concepts</em>' reference.
	 * @see #setConcepts(RValue)
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getNumericDataClause_Concepts()
	 * @model
	 * @generated
	 */
	RValue getConcepts();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getConcepts <em>Concepts</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Concepts</em>' reference.
	 * @see #getConcepts()
	 * @generated
	 */
	void setConcepts(RValue value);

} // NumericDataClause