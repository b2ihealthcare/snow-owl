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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataFactory
 * @model kind="package"
 * @generated
 */
public interface TerminologymetadataPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "terminologymetadata";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "urn:com:b2international:snowowl:terminologymetadata:model";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "terminologymetadata";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TerminologymetadataPackage eINSTANCE = com.b2international.snowowl.terminologymetadata.impl.TerminologymetadataPackageImpl.init();

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionImpl <em>Code System Version</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionImpl
	 * @see com.b2international.snowowl.terminologymetadata.impl.TerminologymetadataPackageImpl#getCodeSystemVersion()
	 * @generated
	 */
	int CODE_SYSTEM_VERSION = 0;

	/**
	 * The feature id for the '<em><b>Version Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM_VERSION__VERSION_ID = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM_VERSION__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Parent Branch Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM_VERSION__PARENT_BRANCH_PATH = 2;

	/**
	 * The feature id for the '<em><b>Effective Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM_VERSION__EFFECTIVE_DATE = 3;

	/**
	 * The feature id for the '<em><b>Import Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM_VERSION__IMPORT_DATE = 4;

	/**
	 * The feature id for the '<em><b>Last Update Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM_VERSION__LAST_UPDATE_DATE = 5;

	/**
	 * The number of structural features of the '<em>Code System Version</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM_VERSION_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemImpl <em>Code System</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.terminologymetadata.impl.CodeSystemImpl
	 * @see com.b2international.snowowl.terminologymetadata.impl.TerminologymetadataPackageImpl#getCodeSystem()
	 * @generated
	 */
	int CODE_SYSTEM = 1;

	/**
	 * The feature id for the '<em><b>Short Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__SHORT_NAME = 0;

	/**
	 * The feature id for the '<em><b>Code System OID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__CODE_SYSTEM_OID = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__NAME = 2;

	/**
	 * The feature id for the '<em><b>Maintaining Organization Link</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__MAINTAINING_ORGANIZATION_LINK = 3;

	/**
	 * The feature id for the '<em><b>Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__LANGUAGE = 4;

	/**
	 * The feature id for the '<em><b>Citation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__CITATION = 5;

	/**
	 * The feature id for the '<em><b>Icon Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__ICON_PATH = 6;

	/**
	 * The feature id for the '<em><b>Terminology Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__TERMINOLOGY_COMPONENT_ID = 7;

	/**
	 * The feature id for the '<em><b>Repository Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__REPOSITORY_UUID = 8;

	/**
	 * The feature id for the '<em><b>Code System Versions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__CODE_SYSTEM_VERSIONS = 9;

	/**
	 * The feature id for the '<em><b>Branch Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__BRANCH_PATH = 10;

	/**
	 * The feature id for the '<em><b>Extension Of</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM__EXTENSION_OF = 11;

	/**
	 * The number of structural features of the '<em>Code System</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODE_SYSTEM_FEATURE_COUNT = 12;


	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion <em>Code System Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Code System Version</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystemVersion
	 * @generated
	 */
	EClass getCodeSystemVersion();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getVersionId <em>Version Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version Id</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getVersionId()
	 * @see #getCodeSystemVersion()
	 * @generated
	 */
	EAttribute getCodeSystemVersion_VersionId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getDescription()
	 * @see #getCodeSystemVersion()
	 * @generated
	 */
	EAttribute getCodeSystemVersion_Description();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getParentBranchPath <em>Parent Branch Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parent Branch Path</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getParentBranchPath()
	 * @see #getCodeSystemVersion()
	 * @generated
	 */
	EAttribute getCodeSystemVersion_ParentBranchPath();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getEffectiveDate <em>Effective Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Effective Date</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getEffectiveDate()
	 * @see #getCodeSystemVersion()
	 * @generated
	 */
	EAttribute getCodeSystemVersion_EffectiveDate();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getImportDate <em>Import Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Import Date</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getImportDate()
	 * @see #getCodeSystemVersion()
	 * @generated
	 */
	EAttribute getCodeSystemVersion_ImportDate();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getLastUpdateDate <em>Last Update Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Last Update Date</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystemVersion#getLastUpdateDate()
	 * @see #getCodeSystemVersion()
	 * @generated
	 */
	EAttribute getCodeSystemVersion_LastUpdateDate();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.terminologymetadata.CodeSystem <em>Code System</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Code System</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem
	 * @generated
	 */
	EClass getCodeSystem();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getShortName <em>Short Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Short Name</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getShortName()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EAttribute getCodeSystem_ShortName();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getCodeSystemOID <em>Code System OID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Code System OID</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getCodeSystemOID()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EAttribute getCodeSystem_CodeSystemOID();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getName()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EAttribute getCodeSystem_Name();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getMaintainingOrganizationLink <em>Maintaining Organization Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maintaining Organization Link</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getMaintainingOrganizationLink()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EAttribute getCodeSystem_MaintainingOrganizationLink();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getLanguage <em>Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Language</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getLanguage()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EAttribute getCodeSystem_Language();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getCitation <em>Citation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Citation</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getCitation()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EAttribute getCodeSystem_Citation();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getIconPath <em>Icon Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Icon Path</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getIconPath()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EAttribute getCodeSystem_IconPath();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getTerminologyComponentId <em>Terminology Component Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Terminology Component Id</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getTerminologyComponentId()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EAttribute getCodeSystem_TerminologyComponentId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getRepositoryUuid <em>Repository Uuid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Repository Uuid</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getRepositoryUuid()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EAttribute getCodeSystem_RepositoryUuid();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getCodeSystemVersions <em>Code System Versions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Code System Versions</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getCodeSystemVersions()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EReference getCodeSystem_CodeSystemVersions();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getBranchPath <em>Branch Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Branch Path</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getBranchPath()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EAttribute getCodeSystem_BranchPath();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.terminologymetadata.CodeSystem#getExtensionOf <em>Extension Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Extension Of</em>'.
	 * @see com.b2international.snowowl.terminologymetadata.CodeSystem#getExtensionOf()
	 * @see #getCodeSystem()
	 * @generated
	 */
	EReference getCodeSystem_ExtensionOf();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	TerminologymetadataFactory getTerminologymetadataFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionImpl <em>Code System Version</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.terminologymetadata.impl.CodeSystemVersionImpl
		 * @see com.b2international.snowowl.terminologymetadata.impl.TerminologymetadataPackageImpl#getCodeSystemVersion()
		 * @generated
		 */
		EClass CODE_SYSTEM_VERSION = eINSTANCE.getCodeSystemVersion();

		/**
		 * The meta object literal for the '<em><b>Version Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM_VERSION__VERSION_ID = eINSTANCE.getCodeSystemVersion_VersionId();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM_VERSION__DESCRIPTION = eINSTANCE.getCodeSystemVersion_Description();

		/**
		 * The meta object literal for the '<em><b>Parent Branch Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM_VERSION__PARENT_BRANCH_PATH = eINSTANCE.getCodeSystemVersion_ParentBranchPath();

		/**
		 * The meta object literal for the '<em><b>Effective Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM_VERSION__EFFECTIVE_DATE = eINSTANCE.getCodeSystemVersion_EffectiveDate();

		/**
		 * The meta object literal for the '<em><b>Import Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM_VERSION__IMPORT_DATE = eINSTANCE.getCodeSystemVersion_ImportDate();

		/**
		 * The meta object literal for the '<em><b>Last Update Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM_VERSION__LAST_UPDATE_DATE = eINSTANCE.getCodeSystemVersion_LastUpdateDate();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.terminologymetadata.impl.CodeSystemImpl <em>Code System</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.terminologymetadata.impl.CodeSystemImpl
		 * @see com.b2international.snowowl.terminologymetadata.impl.TerminologymetadataPackageImpl#getCodeSystem()
		 * @generated
		 */
		EClass CODE_SYSTEM = eINSTANCE.getCodeSystem();

		/**
		 * The meta object literal for the '<em><b>Short Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM__SHORT_NAME = eINSTANCE.getCodeSystem_ShortName();

		/**
		 * The meta object literal for the '<em><b>Code System OID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM__CODE_SYSTEM_OID = eINSTANCE.getCodeSystem_CodeSystemOID();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM__NAME = eINSTANCE.getCodeSystem_Name();

		/**
		 * The meta object literal for the '<em><b>Maintaining Organization Link</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM__MAINTAINING_ORGANIZATION_LINK = eINSTANCE.getCodeSystem_MaintainingOrganizationLink();

		/**
		 * The meta object literal for the '<em><b>Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM__LANGUAGE = eINSTANCE.getCodeSystem_Language();

		/**
		 * The meta object literal for the '<em><b>Citation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM__CITATION = eINSTANCE.getCodeSystem_Citation();

		/**
		 * The meta object literal for the '<em><b>Icon Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM__ICON_PATH = eINSTANCE.getCodeSystem_IconPath();

		/**
		 * The meta object literal for the '<em><b>Terminology Component Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM__TERMINOLOGY_COMPONENT_ID = eINSTANCE.getCodeSystem_TerminologyComponentId();

		/**
		 * The meta object literal for the '<em><b>Repository Uuid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM__REPOSITORY_UUID = eINSTANCE.getCodeSystem_RepositoryUuid();

		/**
		 * The meta object literal for the '<em><b>Code System Versions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODE_SYSTEM__CODE_SYSTEM_VERSIONS = eINSTANCE.getCodeSystem_CodeSystemVersions();

		/**
		 * The meta object literal for the '<em><b>Branch Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODE_SYSTEM__BRANCH_PATH = eINSTANCE.getCodeSystem_BranchPath();

		/**
		 * The meta object literal for the '<em><b>Extension Of</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODE_SYSTEM__EXTENSION_OF = eINSTANCE.getCodeSystem_ExtensionOf();

	}

} //TerminologymetadataPackage
