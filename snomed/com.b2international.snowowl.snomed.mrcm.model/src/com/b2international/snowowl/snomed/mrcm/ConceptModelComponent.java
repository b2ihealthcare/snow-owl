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

import java.util.Date;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Concept Model Component</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Abstract base class for various concept model components.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getUuid <em>Uuid</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#isActive <em>Active</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getEffectiveTime <em>Effective Time</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getAuthor <em>Author</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConceptModelComponent()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface ConceptModelComponent extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Uuid</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uuid</em>' attribute.
	 * @see #setUuid(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConceptModelComponent_Uuid()
	 * @model required="true"
	 * @generated
	 */
	String getUuid();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getUuid <em>Uuid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uuid</em>' attribute.
	 * @see #getUuid()
	 * @generated
	 */
	void setUuid(String value);

	/**
	 * Returns the value of the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Active</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Active</em>' attribute.
	 * @see #setActive(boolean)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConceptModelComponent_Active()
	 * @model required="true"
	 * @generated
	 */
	boolean isActive();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#isActive <em>Active</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Active</em>' attribute.
	 * @see #isActive()
	 * @generated
	 */
	void setActive(boolean value);

	/**
	 * Returns the value of the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Effective Time</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Effective Time</em>' attribute.
	 * @see #setEffectiveTime(Date)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConceptModelComponent_EffectiveTime()
	 * @model required="true"
	 * @generated
	 */
	Date getEffectiveTime();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getEffectiveTime <em>Effective Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Effective Time</em>' attribute.
	 * @see #getEffectiveTime()
	 * @generated
	 */
	void setEffectiveTime(Date value);

	/**
	 * Returns the value of the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Author</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Author</em>' attribute.
	 * @see #setAuthor(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getConceptModelComponent_Author()
	 * @model required="true"
	 * @generated
	 */
	String getAuthor();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.ConceptModelComponent#getAuthor <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Author</em>' attribute.
	 * @see #getAuthor()
	 * @generated
	 */
	void setAuthor(String value);

} // ConceptModelComponent
