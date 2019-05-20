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

import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed Description Type Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedDescriptionTypeRefSetMemberImpl#getDescriptionFormat <em>Description Format</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedDescriptionTypeRefSetMemberImpl#getDescriptionLength <em>Description Length</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedDescriptionTypeRefSetMemberImpl extends SnomedRefSetMemberImpl implements SnomedDescriptionTypeRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedDescriptionTypeRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDescriptionFormat() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__DESCRIPTION_FORMAT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDescriptionFormat(String newDescriptionFormat) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__DESCRIPTION_FORMAT, newDescriptionFormat);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getDescriptionLength() {
		return (Integer)eGet(SnomedRefSetPackage.Literals.SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__DESCRIPTION_LENGTH, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDescriptionLength(int newDescriptionLength) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__DESCRIPTION_LENGTH, newDescriptionLength);
	}

} //SnomedDescriptionTypeRefSetMemberImpl