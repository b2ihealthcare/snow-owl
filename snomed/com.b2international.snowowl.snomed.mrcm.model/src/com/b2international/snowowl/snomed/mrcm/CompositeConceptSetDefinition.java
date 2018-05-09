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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Composite Concept Set Definition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Represents a composite concept set definition.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition#getChildren <em>Children</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getCompositeConceptSetDefinition()
 * @model
 * @generated
 */
public interface CompositeConceptSetDefinition extends ConceptSetDefinition {
	/**
	 * Returns the value of the '<em><b>Children</b></em>' containment reference list.
	 * The list contents are of type {@link com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Children</em>' containment reference list.
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getCompositeConceptSetDefinition_Children()
	 * @model containment="true"
	 * @generated
	 */
	EList<ConceptSetDefinition> getChildren();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model required="true" childRequired="true"
	 * @generated
	 */
	boolean addChild(ConceptSetDefinition child);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model required="true" childRequired="true"
	 * @generated
	 */
	boolean removeChild(ConceptSetDefinition child);

} // CompositeConceptSetDefinition
