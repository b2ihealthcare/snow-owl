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
package com.b2international.snowowl.snomed.impl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Inactivatable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.impl.InactivatableImpl#getInactivationIndicatorRefSetMembers <em>Inactivation Indicator Ref Set Members</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.InactivatableImpl#getAssociationRefSetMembers <em>Association Ref Set Members</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class InactivatableImpl extends CDOObjectImpl implements Inactivatable {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InactivatableImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedPackage.Literals.INACTIVATABLE;
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
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<SnomedAttributeValueRefSetMember> getInactivationIndicatorRefSetMembers() {
		return (EList<SnomedAttributeValueRefSetMember>)eGet(SnomedPackage.Literals.INACTIVATABLE__INACTIVATION_INDICATOR_REF_SET_MEMBERS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<SnomedAssociationRefSetMember> getAssociationRefSetMembers() {
		return (EList<SnomedAssociationRefSetMember>)eGet(SnomedPackage.Literals.INACTIVATABLE__ASSOCIATION_REF_SET_MEMBERS, true);
	}

} //InactivatableImpl