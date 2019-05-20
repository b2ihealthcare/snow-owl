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

import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed Association Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAssociationRefSetMemberImpl#getTargetComponentId <em>Target Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAssociationRefSetMemberImpl#getTargetComponentType <em>Target Component Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedAssociationRefSetMemberImpl extends SnomedRefSetMemberImpl implements SnomedAssociationRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedAssociationRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_ASSOCIATION_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTargetComponentId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_ASSOCIATION_REF_SET_MEMBER__TARGET_COMPONENT_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTargetComponentId(String newTargetComponentId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_ASSOCIATION_REF_SET_MEMBER__TARGET_COMPONENT_ID, newTargetComponentId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public short getTargetComponentType() {
		return SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(getTargetComponentId());
	}

} //SnomedAssociationRefSetMemberImpl