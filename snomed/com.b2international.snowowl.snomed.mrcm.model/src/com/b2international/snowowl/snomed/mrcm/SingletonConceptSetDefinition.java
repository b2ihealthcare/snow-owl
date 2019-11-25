/**
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
 * A representation of the model object '<em><b>Singleton Concept Set Definition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Concept set definition for a single concept.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.SingletonConceptSetDefinition#getConceptId <em>Concept Id</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getSingletonConceptSetDefinition()
 * @model
 * @generated
 */
public interface SingletonConceptSetDefinition extends ConceptSetDefinition {
	/**
	 * Returns the value of the '<em><b>Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Concept Id</em>' attribute.
	 * @see #setConceptId(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getSingletonConceptSetDefinition_ConceptId()
	 * @model required="true"
	 * @generated
	 */
	String getConceptId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.SingletonConceptSetDefinition#getConceptId <em>Concept Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Concept Id</em>' attribute.
	 * @see #getConceptId()
	 * @generated
	 */
	void setConceptId(String value);

} // SingletonConceptSetDefinition
