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

import com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Enumerated Concept Set Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.EnumeratedConceptSetDefinitionImpl#getConceptIds <em>Concept Ids</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EnumeratedConceptSetDefinitionImpl extends ConceptSetDefinitionImpl implements EnumeratedConceptSetDefinition {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EnumeratedConceptSetDefinitionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MrcmPackage.Literals.ENUMERATED_CONCEPT_SET_DEFINITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<String> getConceptIds() {
		return (EList<String>)eGet(MrcmPackage.Literals.ENUMERATED_CONCEPT_SET_DEFINITION__CONCEPT_IDS, true);
	}

} //EnumeratedConceptSetDefinitionImpl
