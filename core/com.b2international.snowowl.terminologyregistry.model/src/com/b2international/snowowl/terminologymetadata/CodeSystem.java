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
 * </ul>
 *
 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#getCodeSystem()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface CodeSystem extends CDOObject {
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
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Code System Version Group</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	CodeSystemVersionGroup getCodeSystemVersionGroup();

} // CodeSystem