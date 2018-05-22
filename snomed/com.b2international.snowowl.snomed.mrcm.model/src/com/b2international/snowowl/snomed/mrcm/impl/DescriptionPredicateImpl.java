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

import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Description Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.DescriptionPredicateImpl#getTypeId <em>Type Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DescriptionPredicateImpl extends ConceptModelPredicateImpl implements DescriptionPredicate {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DescriptionPredicateImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MrcmPackage.Literals.DESCRIPTION_PREDICATE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTypeId() {
		return (String)eGet(MrcmPackage.Literals.DESCRIPTION_PREDICATE__TYPE_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTypeId(String newTypeId) {
		eSet(MrcmPackage.Literals.DESCRIPTION_PREDICATE__TYPE_ID, newTypeId);
	}

} //DescriptionPredicateImpl
