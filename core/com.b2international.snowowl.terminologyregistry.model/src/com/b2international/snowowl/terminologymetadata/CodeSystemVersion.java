/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Date;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Code System Version</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getVersionId <em>Version Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getDescription <em>Description</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getParentBranchPath <em>Parent Branch Path</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getEffectiveDate <em>Effective Date</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getImportDate <em>Import Date</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getLastUpdateDate <em>Last Update Date</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersion()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface CodeSystemVersion extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Version Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version Id</em>' attribute.
	 * @see #setVersionId(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersion_VersionId()
	 * @model required="true"
	 * @generated
	 */
	String getVersionId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getVersionId <em>Version Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version Id</em>' attribute.
	 * @see #getVersionId()
	 * @generated
	 */
	void setVersionId(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersion_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Parent Branch Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Branch Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Branch Path</em>' attribute.
	 * @see #setParentBranchPath(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersion_ParentBranchPath()
	 * @model
	 * @generated
	 */
	String getParentBranchPath();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getParentBranchPath <em>Parent Branch Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Branch Path</em>' attribute.
	 * @see #getParentBranchPath()
	 * @generated
	 */
	void setParentBranchPath(String value);

	/**
	 * Returns the value of the '<em><b>Effective Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Effective Date</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Effective Date</em>' attribute.
	 * @see #setEffectiveDate(Date)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersion_EffectiveDate()
	 * @model
	 * @generated
	 */
	Date getEffectiveDate();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getEffectiveDate <em>Effective Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Effective Date</em>' attribute.
	 * @see #getEffectiveDate()
	 * @generated
	 */
	void setEffectiveDate(Date value);

	/**
	 * Returns the value of the '<em><b>Import Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Import Date</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Import Date</em>' attribute.
	 * @see #setImportDate(Date)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersion_ImportDate()
	 * @model
	 * @generated
	 */
	Date getImportDate();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getImportDate <em>Import Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Import Date</em>' attribute.
	 * @see #getImportDate()
	 * @generated
	 */
	void setImportDate(Date value);

	/**
	 * Returns the value of the '<em><b>Last Update Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Update Date</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Last Update Date</em>' attribute.
	 * @see #setLastUpdateDate(Date)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystemVersion_LastUpdateDate()
	 * @model
	 * @generated
	 */
	Date getLastUpdateDate();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getLastUpdateDate <em>Last Update Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Last Update Date</em>' attribute.
	 * @see #getLastUpdateDate()
	 * @generated
	 */
	void setLastUpdateDate(Date value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	CodeSystem getCodeSystem();

} // CodeSystemVersion
