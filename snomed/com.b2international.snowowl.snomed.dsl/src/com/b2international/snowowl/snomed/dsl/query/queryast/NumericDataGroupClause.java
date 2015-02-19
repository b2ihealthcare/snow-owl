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
 * A representation of the model object '<em><b>Numeric Data Group Clause</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getConcepts <em>Concepts</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getNumericData <em>Numeric Data</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getSubstance <em>Substance</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getNumericDataGroupClause()
 * @model
 * @generated
 */
public interface NumericDataGroupClause extends RValue {
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
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getNumericDataGroupClause_Concepts()
	 * @model
	 * @generated
	 */
	RValue getConcepts();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getConcepts <em>Concepts</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Concepts</em>' reference.
	 * @see #getConcepts()
	 * @generated
	 */
	void setConcepts(RValue value);

	/**
	 * Returns the value of the '<em><b>Numeric Data</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Numeric Data</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Numeric Data</em>' reference.
	 * @see #setNumericData(NumericDataClause)
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getNumericDataGroupClause_NumericData()
	 * @model required="true"
	 * @generated
	 */
	NumericDataClause getNumericData();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getNumericData <em>Numeric Data</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Numeric Data</em>' reference.
	 * @see #getNumericData()
	 * @generated
	 */
	void setNumericData(NumericDataClause value);

	/**
	 * Returns the value of the '<em><b>Substance</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Substance</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Substance</em>' reference.
	 * @see #setSubstance(RValue)
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getNumericDataGroupClause_Substance()
	 * @model required="true"
	 * @generated
	 */
	RValue getSubstance();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getSubstance <em>Substance</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Substance</em>' reference.
	 * @see #getSubstance()
	 * @generated
	 */
	void setSubstance(RValue value);

} // NumericDataGroupClause