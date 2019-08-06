/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
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
package com.b2international.snowowl.snomed.snomedrefset.impl;

import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed Attribute Value Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAttributeValueRefSetMemberImpl#getValueId <em>Value Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedAttributeValueRefSetMemberImpl extends SnomedRefSetMemberImpl implements SnomedAttributeValueRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedAttributeValueRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getValueId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__VALUE_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValueId(String newValueId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__VALUE_ID, newValueId);
	}

} //SnomedAttributeValueRefSetMemberImpl