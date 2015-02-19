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
 * A representation of the model object '<em><b>Concept Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getQuantifier <em>Quantifier</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getConceptId <em>Concept Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getLabel <em>Label</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getConceptRef()
 * @model
 * @generated
 */
public interface ConceptRef extends RValue {
	/**
	 * Returns the value of the '<em><b>Quantifier</b></em>' attribute.
	 * The literals are from the enumeration {@link com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Quantifier</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Quantifier</em>' attribute.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier
	 * @see #setQuantifier(SubsumptionQuantifier)
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getConceptRef_Quantifier()
	 * @model required="true"
	 * @generated
	 */
	SubsumptionQuantifier getQuantifier();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getQuantifier <em>Quantifier</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Quantifier</em>' attribute.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier
	 * @see #getQuantifier()
	 * @generated
	 */
	void setQuantifier(SubsumptionQuantifier value);

	/**
	 * Returns the value of the '<em><b>Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Concept Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Concept Id</em>' attribute.
	 * @see #setConceptId(String)
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getConceptRef_ConceptId()
	 * @model required="true"
	 * @generated
	 */
	String getConceptId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getConceptId <em>Concept Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Concept Id</em>' attribute.
	 * @see #getConceptId()
	 * @generated
	 */
	void setConceptId(String value);

	/**
	 * Returns the value of the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Label</em>' attribute.
	 * @see #setLabel(String)
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage#getConceptRef_Label()
	 * @model
	 * @generated
	 */
	String getLabel();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getLabel <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Label</em>' attribute.
	 * @see #getLabel()
	 * @generated
	 */
	void setLabel(String value);

} // ConceptRef