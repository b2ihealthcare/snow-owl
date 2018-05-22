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
package com.b2international.snowowl.snomed.mrcm.impl;

import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relationship Concept Set Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.RelationshipConceptSetDefinitionImpl#getTypeConceptId <em>Type Concept Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.RelationshipConceptSetDefinitionImpl#getDestinationConceptId <em>Destination Concept Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RelationshipConceptSetDefinitionImpl extends ConceptSetDefinitionImpl implements RelationshipConceptSetDefinition {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RelationshipConceptSetDefinitionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MrcmPackage.Literals.RELATIONSHIP_CONCEPT_SET_DEFINITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTypeConceptId() {
		return (String)eGet(MrcmPackage.Literals.RELATIONSHIP_CONCEPT_SET_DEFINITION__TYPE_CONCEPT_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTypeConceptId(String newTypeConceptId) {
		eSet(MrcmPackage.Literals.RELATIONSHIP_CONCEPT_SET_DEFINITION__TYPE_CONCEPT_ID, newTypeConceptId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDestinationConceptId() {
		return (String)eGet(MrcmPackage.Literals.RELATIONSHIP_CONCEPT_SET_DEFINITION__DESTINATION_CONCEPT_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDestinationConceptId(String newDestinationConceptId) {
		eSet(MrcmPackage.Literals.RELATIONSHIP_CONCEPT_SET_DEFINITION__DESTINATION_CONCEPT_ID, newDestinationConceptId);
	}

} //RelationshipConceptSetDefinitionImpl
