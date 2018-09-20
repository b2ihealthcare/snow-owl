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

import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Concrete Domain Element Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Predicate to check for the presence of a concrete model element.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getAttribute <em>Attribute</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getRange <em>Range</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getCharacteristicTypeConceptId <em>Characteristic Type Concept Id</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConcreteDomainElementPredicate()
 * @model
 * @generated
 */
public interface ConcreteDomainElementPredicate extends ConceptModelPredicate {
	/**
	 * Returns the value of the '<em><b>Attribute</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attribute</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attribute</em>' containment reference.
	 * @see #setAttribute(ConceptSetDefinition)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConcreteDomainElementPredicate_Attribute()
	 * @model containment="true" required="true"
	 * @generated
	 */
	ConceptSetDefinition getAttribute();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getAttribute <em>Attribute</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attribute</em>' containment reference.
	 * @see #getAttribute()
	 * @generated
	 */
	void setAttribute(ConceptSetDefinition value);

	/**
	 * Returns the value of the '<em><b>Range</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Range</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Range</em>' attribute.
	 * @see #setRange(DataType)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConcreteDomainElementPredicate_Range()
	 * @model dataType="com.b2international.snowowl.snomed.mrcm.DataType" required="true"
	 * @generated
	 */
	DataType getRange();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getRange <em>Range</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Range</em>' attribute.
	 * @see #getRange()
	 * @generated
	 */
	void setRange(DataType value);

	/**
	 * Returns the value of the '<em><b>Characteristic Type Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Characteristic Type Concept Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Characteristic Type Concept Id</em>' attribute.
	 * @see #setCharacteristicTypeConceptId(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConcreteDomainElementPredicate_CharacteristicTypeConceptId()
	 * @model
	 * @generated
	 */
	String getCharacteristicTypeConceptId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getCharacteristicTypeConceptId <em>Characteristic Type Concept Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Characteristic Type Concept Id</em>' attribute.
	 * @see #getCharacteristicTypeConceptId()
	 * @generated
	 */
	void setCharacteristicTypeConceptId(String value);

} // ConcreteDomainElementPredicate
