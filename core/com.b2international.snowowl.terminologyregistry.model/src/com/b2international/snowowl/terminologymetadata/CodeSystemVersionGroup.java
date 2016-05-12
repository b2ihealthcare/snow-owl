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
package com.b2international.snowowl.terminologymetadata;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Code System Version Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersionGroup#getRepositoryUuid <em>Repository Uuid</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersionGroup#getCodeSystems <em>Code Systems</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersionGroup#getCodeSystemVersions <em>Code System Versions</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersionGroup()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface CodeSystemVersionGroup extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Repository Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Repository Uuid</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Repository Uuid</em>' attribute.
	 * @see #setRepositoryUuid(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersionGroup_RepositoryUuid()
	 * @model required="true"
	 * @generated
	 */
	String getRepositoryUuid();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersionGroup#getRepositoryUuid <em>Repository Uuid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Repository Uuid</em>' attribute.
	 * @see #getRepositoryUuid()
	 * @generated
	 */
	void setRepositoryUuid(String value);

	/**
	 * Returns the value of the '<em><b>Code Systems</b></em>' containment reference list.
	 * The list contents are of type {@link com.b2international.snowowl.terminologymetadata.CodeSystem}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Code Systems</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Code Systems</em>' containment reference list.
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersionGroup_CodeSystems()
	 * @model containment="true"
	 * @generated
	 */
	EList<CodeSystem> getCodeSystems();

	/**
	 * Returns the value of the '<em><b>Code System Versions</b></em>' containment reference list.
	 * The list contents are of type {@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Code System Versions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Code System Versions</em>' containment reference list.
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersionGroup_CodeSystemVersions()
	 * @model containment="true"
	 * @generated
	 */
	EList<CodeSystemVersion> getCodeSystemVersions();

} // CodeSystemVersionGroup