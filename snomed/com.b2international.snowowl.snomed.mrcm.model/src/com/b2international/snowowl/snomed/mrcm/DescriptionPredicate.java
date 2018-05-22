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
 * A representation of the model object '<em><b>Description Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Predicate to check for the presence of a description of a specific type (e.g. fully specified name, synonym, etc.).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.DescriptionPredicate#getTypeId <em>Type Id</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getDescriptionPredicate()
 * @model
 * @generated
 */
public interface DescriptionPredicate extends ConceptModelPredicate {
	/**
	 * Returns the value of the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type Id</em>' attribute.
	 * @see #setTypeId(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getDescriptionPredicate_TypeId()
	 * @model required="true"
	 * @generated
	 */
	String getTypeId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.DescriptionPredicate#getTypeId <em>Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Id</em>' attribute.
	 * @see #getTypeId()
	 * @generated
	 */
	void setTypeId(String value);

} // DescriptionPredicate
