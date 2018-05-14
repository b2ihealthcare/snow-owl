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
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getName <em>Name</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getLabel <em>Label</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getType <em>Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getCharacteristicTypeConceptId <em>Characteristic Type Concept Id</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConcreteDomainElementPredicate()
 * @model
 * @generated
 */
public interface ConcreteDomainElementPredicate extends ConceptModelPredicate {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConcreteDomainElementPredicate_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Label</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Label</em>' attribute.
	 * @see #setLabel(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConcreteDomainElementPredicate_Label()
	 * @model default="" required="true"
	 * @generated
	 */
	String getLabel();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getLabel <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Label</em>' attribute.
	 * @see #getLabel()
	 * @generated
	 */
	void setLabel(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(DataType)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConcreteDomainElementPredicate_Type()
	 * @model dataType="com.b2international.snowowl.snomed.mrcm.DataType" required="true"
	 * @generated
	 */
	DataType getType();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(DataType value);

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
