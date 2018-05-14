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
 * A representation of the model object '<em><b>Constraint Base</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Abstract base class for concept model constraints.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase#getStrength <em>Strength</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase#getValidationMessage <em>Validation Message</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConstraintBase()
 * @model abstract="true"
 * @generated
 */
public interface ConstraintBase extends ConceptModelComponent {
	/**
	 * Returns the value of the '<em><b>Strength</b></em>' attribute.
	 * The literals are from the enumeration {@link com.b2international.snowowl.snomed.mrcm.ConstraintStrength}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Strength</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Strength</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintStrength
	 * @see #setStrength(ConstraintStrength)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConstraintBase_Strength()
	 * @model required="true"
	 * @generated
	 */
	ConstraintStrength getStrength();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase#getStrength <em>Strength</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Strength</em>' attribute.
	 * @see com.b2international.snowowl.snomed.mrcm.ConstraintStrength
	 * @see #getStrength()
	 * @generated
	 */
	void setStrength(ConstraintStrength value);

	/**
	 * Returns the value of the '<em><b>Validation Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Validation Message</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validation Message</em>' attribute.
	 * @see #setValidationMessage(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConstraintBase_ValidationMessage()
	 * @model
	 * @generated
	 */
	String getValidationMessage();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase#getValidationMessage <em>Validation Message</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Validation Message</em>' attribute.
	 * @see #getValidationMessage()
	 * @generated
	 */
	void setValidationMessage(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConstraintBase_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConstraintBase#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

} // ConstraintBase
