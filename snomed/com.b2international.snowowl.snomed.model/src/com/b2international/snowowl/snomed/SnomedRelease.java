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
package com.b2international.snowowl.snomed;

import com.b2international.snowowl.terminologymetadata.CodeSystem;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Release</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.SnomedRelease#getBaseCodeSystemOID <em>Base Code System OID</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.SnomedRelease#getReleaseType <em>Release Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.SnomedPackage#getSnomedRelease()
 * @model
 * @generated
 */
public interface SnomedRelease extends CodeSystem {
	/**
	 * Returns the value of the '<em><b>Base Code System OID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Base Code System OID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Base Code System OID</em>' attribute.
	 * @see #setBaseCodeSystemOID(String)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getSnomedRelease_BaseCodeSystemOID()
	 * @model annotation="http://www.eclipse.org/CDO/DBStore columnLength='255'"
	 * @generated
	 */
	String getBaseCodeSystemOID();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.SnomedRelease#getBaseCodeSystemOID <em>Base Code System OID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Base Code System OID</em>' attribute.
	 * @see #getBaseCodeSystemOID()
	 * @generated
	 */
	void setBaseCodeSystemOID(String value);

	/**
	 * Returns the value of the '<em><b>Release Type</b></em>' attribute.
	 * The literals are from the enumeration {@link com.b2international.snowowl.snomed.SnomedReleaseType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Release Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Release Type</em>' attribute.
	 * @see com.b2international.snowowl.snomed.SnomedReleaseType
	 * @see #setReleaseType(SnomedReleaseType)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getSnomedRelease_ReleaseType()
	 * @model
	 * @generated
	 */
	SnomedReleaseType getReleaseType();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.SnomedRelease#getReleaseType <em>Release Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Release Type</em>' attribute.
	 * @see com.b2international.snowowl.snomed.SnomedReleaseType
	 * @see #getReleaseType()
	 * @generated
	 */
	void setReleaseType(SnomedReleaseType value);

} // SnomedRelease
