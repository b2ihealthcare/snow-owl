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

import java.util.Date;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl#getReferencedComponentType <em>Referenced Component Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl#getEffectiveTime <em>Effective Time</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl#isActive <em>Active</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl#getRefSet <em>Ref Set</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl#isReleased <em>Released</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl#getReferencedComponentId <em>Referenced Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl#getModuleId <em>Module Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl#getRefSetIdentifierId <em>Ref Set Identifier Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl#getUuid <em>Uuid</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedRefSetMemberImpl extends CDOObjectImpl implements SnomedRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected int eStaticFeatureCount() {
		return 0;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Calculates referenced component type by invoking
	 * {@link SnomedTerminologyComponentConstants#getTerminologyComponentIdValue(String)}
	 * with the member's referenced component identifier. Assumes that
	 * {@link #getReferencedComponentId()} returns a valid SNOMED CT component id. If this
	 * is not the case (particularly with mapping reference set members),
	 * subclasses should override.
	 * <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public short getReferencedComponentType() {
		return SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(getReferencedComponentId());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getEffectiveTime() {
		return (Date)eGet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEffectiveTime(Date newEffectiveTime) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME, newEffectiveTime);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetEffectiveTime() {
		eUnset(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetEffectiveTime() {
		return eIsSet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isActive() {
		return (Boolean)eGet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__ACTIVE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setActive(boolean newActive) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__ACTIVE, newActive);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SnomedRefSet getRefSet() {
		return (SnomedRefSet)eGet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__REF_SET, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRefSet(SnomedRefSet newRefSet) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__REF_SET, newRefSet);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isReleased() {
		return (Boolean)eGet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__RELEASED, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setReleased(boolean newReleased) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__RELEASED, newReleased);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getReferencedComponentId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setReferencedComponentId(String newReferencedComponentId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID, newReferencedComponentId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getModuleId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__MODULE_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setModuleId(String newModuleId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__MODULE_ID, newModuleId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getRefSetIdentifierId() {
		return getRefSet().getIdentifierId();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getUuid() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__UUID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUuid(String newUuid) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__UUID, newUuid);
	}

} //SnomedRefSetMemberImpl