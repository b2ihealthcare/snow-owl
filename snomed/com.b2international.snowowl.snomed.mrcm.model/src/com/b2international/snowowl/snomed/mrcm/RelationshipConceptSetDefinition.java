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
 * A representation of the model object '<em><b>Relationship Concept Set Definition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Concept set definition describing concepts which include a relationship of the given type and destination.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition#getTypeConceptId <em>Type Concept Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition#getDestinationConceptId <em>Destination Concept Id</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getRelationshipConceptSetDefinition()
 * @model
 * @generated
 */
public interface RelationshipConceptSetDefinition extends ConceptSetDefinition {
	/**
	 * Returns the value of the '<em><b>Type Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type Concept Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type Concept Id</em>' attribute.
	 * @see #setTypeConceptId(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getRelationshipConceptSetDefinition_TypeConceptId()
	 * @model required="true"
	 * @generated
	 */
	String getTypeConceptId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition#getTypeConceptId <em>Type Concept Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Concept Id</em>' attribute.
	 * @see #getTypeConceptId()
	 * @generated
	 */
	void setTypeConceptId(String value);

	/**
	 * Returns the value of the '<em><b>Destination Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Destination Concept Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Destination Concept Id</em>' attribute.
	 * @see #setDestinationConceptId(String)
	 * @see com.b2international.snowowl.snomed.mrcm.MrcmPackage#getRelationshipConceptSetDefinition_DestinationConceptId()
	 * @model required="true"
	 * @generated
	 */
	String getDestinationConceptId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition#getDestinationConceptId <em>Destination Concept Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Destination Concept Id</em>' attribute.
	 * @see #getDestinationConceptId()
	 * @generated
	 */
	void setDestinationConceptId(String value);

} // RelationshipConceptSetDefinition
