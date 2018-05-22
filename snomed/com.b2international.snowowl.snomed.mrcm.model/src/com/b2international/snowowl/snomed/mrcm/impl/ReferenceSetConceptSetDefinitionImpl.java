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
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Reference Set Concept Set Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ReferenceSetConceptSetDefinitionImpl#getRefSetIdentifierConceptId <em>Ref Set Identifier Concept Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ReferenceSetConceptSetDefinitionImpl extends ConceptSetDefinitionImpl implements ReferenceSetConceptSetDefinition {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReferenceSetConceptSetDefinitionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MrcmPackage.Literals.REFERENCE_SET_CONCEPT_SET_DEFINITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRefSetIdentifierConceptId() {
		return (String)eGet(MrcmPackage.Literals.REFERENCE_SET_CONCEPT_SET_DEFINITION__REF_SET_IDENTIFIER_CONCEPT_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRefSetIdentifierConceptId(String newRefSetIdentifierConceptId) {
		eSet(MrcmPackage.Literals.REFERENCE_SET_CONCEPT_SET_DEFINITION__REF_SET_IDENTIFIER_CONCEPT_ID, newRefSetIdentifierConceptId);
	}

} //ReferenceSetConceptSetDefinitionImpl
