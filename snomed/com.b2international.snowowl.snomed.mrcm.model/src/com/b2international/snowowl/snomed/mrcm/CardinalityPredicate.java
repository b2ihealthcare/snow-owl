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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Cardinality Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Predicate representing an MRCM cardinality check.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getMinCardinality <em>Min Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getMaxCardinality <em>Max Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getGroupRule <em>Group Rule</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getPredicate <em>Predicate</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getCardinalityPredicate()
 * @model
 * @generated
 */
public interface CardinalityPredicate extends ConceptModelPredicate {
	/**
	 * Returns the value of the '<em><b>Min Cardinality</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Min Cardinality</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Cardinality</em>' attribute.
	 * @see #setMinCardinality(int)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getCardinalityPredicate_MinCardinality()
	 * @model required="true"
	 * @generated
	 */
	int getMinCardinality();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getMinCardinality <em>Min Cardinality</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Cardinality</em>' attribute.
	 * @see #getMinCardinality()
	 * @generated
	 */
	void setMinCardinality(int value);

	/**
	 * Returns the value of the '<em><b>Max Cardinality</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Max Cardinality</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Cardinality</em>' attribute.
	 * @see #setMaxCardinality(int)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getCardinalityPredicate_MaxCardinality()
	 * @model required="true"
	 * @generated
	 */
	int getMaxCardinality();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getMaxCardinality <em>Max Cardinality</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Cardinality</em>' attribute.
	 * @see #getMaxCardinality()
	 * @generated
	 */
	void setMaxCardinality(int value);

	/**
	 * Returns the value of the '<em><b>Group Rule</b></em>' attribute.
	 * The literals are from the enumeration {@link com.b2international.snowowl.snomed.mrcm.GroupRule}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group Rule</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group Rule</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.GroupRule
	 * @see #setGroupRule(GroupRule)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getCardinalityPredicate_GroupRule()
	 * @model required="true"
	 * @generated
	 */
	GroupRule getGroupRule();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getGroupRule <em>Group Rule</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Group Rule</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.GroupRule
	 * @see #getGroupRule()
	 * @generated
	 */
	void setGroupRule(GroupRule value);

	/**
	 * Returns the value of the '<em><b>Predicate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Predicate</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Predicate</em>' containment reference.
	 * @see #setPredicate(ConceptModelPredicate)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getCardinalityPredicate_Predicate()
	 * @model containment="true" required="true"
	 * @generated
	 */
	ConceptModelPredicate getPredicate();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.CardinalityPredicate#getPredicate <em>Predicate</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Predicate</em>' containment reference.
	 * @see #getPredicate()
	 * @generated
	 */
	void setPredicate(ConceptModelPredicate value);

} // CardinalityPredicate
