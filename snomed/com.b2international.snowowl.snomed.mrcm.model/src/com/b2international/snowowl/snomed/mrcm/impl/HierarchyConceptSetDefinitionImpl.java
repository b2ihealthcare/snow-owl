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

import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Hierarchy Concept Set Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.HierarchyConceptSetDefinitionImpl#getConceptId <em>Concept Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.HierarchyConceptSetDefinitionImpl#getInclusionType <em>Inclusion Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HierarchyConceptSetDefinitionImpl extends ConceptSetDefinitionImpl implements HierarchyConceptSetDefinition {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HierarchyConceptSetDefinitionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MrcmPackage.Literals.HIERARCHY_CONCEPT_SET_DEFINITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getConceptId() {
		return (String)eGet(MrcmPackage.Literals.HIERARCHY_CONCEPT_SET_DEFINITION__CONCEPT_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConceptId(String newConceptId) {
		eSet(MrcmPackage.Literals.HIERARCHY_CONCEPT_SET_DEFINITION__CONCEPT_ID, newConceptId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HierarchyInclusionType getInclusionType() {
		return (HierarchyInclusionType)eGet(MrcmPackage.Literals.HIERARCHY_CONCEPT_SET_DEFINITION__INCLUSION_TYPE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInclusionType(HierarchyInclusionType newInclusionType) {
		eSet(MrcmPackage.Literals.HIERARCHY_CONCEPT_SET_DEFINITION__INCLUSION_TYPE, newInclusionType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getFocusConceptId() {
		return getConceptId();
	}

} //HierarchyConceptSetDefinitionImpl
