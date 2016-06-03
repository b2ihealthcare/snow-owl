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

import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Version</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.SnomedVersion#getModules <em>Modules</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.SnomedPackage#getSnomedVersion()
 * @model
 * @generated
 */
public interface SnomedVersion extends CodeSystemVersion {
	/**
	 * Returns the value of the '<em><b>Modules</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Modules</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Modules</em>' attribute list.
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getSnomedVersion_Modules()
	 * @model
	 * @generated
	 */
	EList<String> getModules();

} // SnomedVersion
