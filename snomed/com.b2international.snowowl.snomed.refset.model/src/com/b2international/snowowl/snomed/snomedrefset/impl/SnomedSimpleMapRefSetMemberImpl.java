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

import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed Simple Map Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedSimpleMapRefSetMemberImpl#getMapTargetComponentId <em>Map Target Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedSimpleMapRefSetMemberImpl#getMapTargetComponentType <em>Map Target Component Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedSimpleMapRefSetMemberImpl#getMapTargetComponentDescription <em>Map Target Component Description</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedSimpleMapRefSetMemberImpl extends SnomedRefSetMemberImpl implements SnomedSimpleMapRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedSimpleMapRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_SIMPLE_MAP_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMapTargetComponentId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMapTargetComponentId(String newMapTargetComponentId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_ID, newMapTargetComponentId);
	}

	/**
	 * @generated NOT
	 */
	@Override
	public short getReferencedComponentType() {
		return getRefSet().getReferencedComponentType();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public short getMapTargetComponentType() {
		
		if (getRefSet() instanceof SnomedMappingRefSet) {
			return ((SnomedMappingRefSet) getRefSet()).getMapTargetComponentType();
		}
		
		throw new RuntimeException("Container of the current member was not a mapping but " + eContainer.getClass());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMapTargetComponentDescription() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_DESCRIPTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMapTargetComponentDescription(String newMapTargetComponentDescription) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_DESCRIPTION, newMapTargetComponentDescription);
	}

} //SnomedSimpleMapRefSetMemberImpl