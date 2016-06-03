/**
 *  Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.b2international.snowowl.snomed.impl;

import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.SnomedRelease;
import com.b2international.snowowl.snomed.SnomedReleaseType;

import com.b2international.snowowl.terminologymetadata.impl.CodeSystemImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Release</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.impl.SnomedReleaseImpl#getBaseCodeSystemOID <em>Base Code System OID</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.SnomedReleaseImpl#getReleaseType <em>Release Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SnomedReleaseImpl extends CodeSystemImpl implements SnomedRelease {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedReleaseImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedPackage.Literals.SNOMED_RELEASE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBaseCodeSystemOID() {
		return (String)eGet(SnomedPackage.Literals.SNOMED_RELEASE__BASE_CODE_SYSTEM_OID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBaseCodeSystemOID(String newBaseCodeSystemOID) {
		eSet(SnomedPackage.Literals.SNOMED_RELEASE__BASE_CODE_SYSTEM_OID, newBaseCodeSystemOID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedReleaseType getReleaseType() {
		return (SnomedReleaseType)eGet(SnomedPackage.Literals.SNOMED_RELEASE__RELEASE_TYPE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReleaseType(SnomedReleaseType newReleaseType) {
		eSet(SnomedPackage.Literals.SNOMED_RELEASE__RELEASE_TYPE, newReleaseType);
	}

} //SnomedReleaseImpl
