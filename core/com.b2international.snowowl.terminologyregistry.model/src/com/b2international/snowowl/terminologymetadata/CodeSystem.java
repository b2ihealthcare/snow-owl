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

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Code System</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getShortName <em>Short Name</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getCodeSystemOID <em>Code System OID</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getName <em>Name</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getMaintainingOrganizationLink <em>Maintaining Organization Link</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getLanguage <em>Language</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getCitation <em>Citation</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getIconPath <em>Icon Path</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getTerminologyComponentId <em>Terminology Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getRepositoryUuid <em>Repository Uuid</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getCodeSystemVersions <em>Code System Versions</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getBranchPath <em>Branch Path</em>}</li>
 *   <li>{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getExtensionOf <em>Extension Of</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface CodeSystem extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Short Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Short Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Short Name</em>' attribute.
	 * @see #setShortName(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_ShortName()
	 * @model required="true"
	 * @generated
	 */
	String getShortName();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getShortName <em>Short Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Short Name</em>' attribute.
	 * @see #getShortName()
	 * @generated
	 */
	void setShortName(String value);

	/**
	 * Returns the value of the '<em><b>Code System OID</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Code System OID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Code System OID</em>' attribute.
	 * @see #setCodeSystemOID(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_CodeSystemOID()
	 * @model default=""
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnLength='255'"
	 * @generated
	 */
	String getCodeSystemOID();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getCodeSystemOID <em>Code System OID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Code System OID</em>' attribute.
	 * @see #getCodeSystemOID()
	 * @generated
	 */
	void setCodeSystemOID(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Maintaining Organization Link</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Maintaining Organization Link</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Maintaining Organization Link</em>' attribute.
	 * @see #setMaintainingOrganizationLink(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_MaintainingOrganizationLink()
	 * @model ordered="false"
	 * @generated
	 */
	String getMaintainingOrganizationLink();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getMaintainingOrganizationLink <em>Maintaining Organization Link</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Maintaining Organization Link</em>' attribute.
	 * @see #getMaintainingOrganizationLink()
	 * @generated
	 */
	void setMaintainingOrganizationLink(String value);

	/**
	 * Returns the value of the '<em><b>Language</b></em>' attribute.
	 * The default value is <code>"ENG"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Language</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Language</em>' attribute.
	 * @see #setLanguage(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_Language()
	 * @model default="ENG"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnLength='255'"
	 * @generated
	 */
	String getLanguage();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getLanguage <em>Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Language</em>' attribute.
	 * @see #getLanguage()
	 * @generated
	 */
	void setLanguage(String value);

	/**
	 * Returns the value of the '<em><b>Citation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Citation</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Citation</em>' attribute.
	 * @see #setCitation(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_Citation()
	 * @model
	 * @generated
	 */
	String getCitation();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getCitation <em>Citation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Citation</em>' attribute.
	 * @see #getCitation()
	 * @generated
	 */
	void setCitation(String value);

	/**
	 * Returns the value of the '<em><b>Icon Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Icon Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Icon Path</em>' attribute.
	 * @see #setIconPath(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_IconPath()
	 * @model
	 * @generated
	 */
	String getIconPath();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getIconPath <em>Icon Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Icon Path</em>' attribute.
	 * @see #getIconPath()
	 * @generated
	 */
	void setIconPath(String value);

	/**
	 * Returns the value of the '<em><b>Terminology Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Terminology Component Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Terminology Component Id</em>' attribute.
	 * @see #setTerminologyComponentId(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_TerminologyComponentId()
	 * @model
	 * @generated
	 */
	String getTerminologyComponentId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getTerminologyComponentId <em>Terminology Component Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Terminology Component Id</em>' attribute.
	 * @see #getTerminologyComponentId()
	 * @generated
	 */
	void setTerminologyComponentId(String value);

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
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_RepositoryUuid()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnLength='255'"
	 * @generated
	 */
	String getRepositoryUuid();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getRepositoryUuid <em>Repository Uuid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Repository Uuid</em>' attribute.
	 * @see #getRepositoryUuid()
	 * @generated
	 */
	void setRepositoryUuid(String value);

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
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_CodeSystemVersions()
	 * @model containment="true"
	 * @generated
	 */
	EList<CodeSystemVersion> getCodeSystemVersions();

	/**
	 * Returns the value of the '<em><b>Branch Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Branch Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Branch Path</em>' attribute.
	 * @see #setBranchPath(String)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_BranchPath()
	 * @model required="true"
	 * @generated
	 */
	String getBranchPath();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getBranchPath <em>Branch Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Branch Path</em>' attribute.
	 * @see #getBranchPath()
	 * @generated
	 */
	void setBranchPath(String value);

	/**
	 * Returns the value of the '<em><b>Extension Of</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Extension Of</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extension Of</em>' reference.
	 * @see #setExtensionOf(CodeSystem)
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem_ExtensionOf()
	 * @model
	 * @generated
	 */
	CodeSystem getExtensionOf();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getExtensionOf <em>Extension Of</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Extension Of</em>' reference.
	 * @see #getExtensionOf()
	 * @generated
	 */
	void setExtensionOf(CodeSystem value);

} // CodeSystem
