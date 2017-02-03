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
package com.b2international.snowowl.terminologymetadata.impl;

import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataFactory;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TerminologymetadataPackageImpl extends EPackageImpl implements TerminologymetadataPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass codeSystemVersionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass codeSystemEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private TerminologymetadataPackageImpl() {
		super(eNS_URI, TerminologymetadataFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link TerminologymetadataPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static TerminologymetadataPackage init() {
		if (isInited) return (TerminologymetadataPackage)EPackage.Registry.INSTANCE.getEPackage(TerminologymetadataPackage.eNS_URI);

		// Obtain or create and register package
		TerminologymetadataPackageImpl theTerminologymetadataPackage = (TerminologymetadataPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof TerminologymetadataPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new TerminologymetadataPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theTerminologymetadataPackage.createPackageContents();

		// Initialize created meta-data
		theTerminologymetadataPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theTerminologymetadataPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(TerminologymetadataPackage.eNS_URI, theTerminologymetadataPackage);
		return theTerminologymetadataPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCodeSystemVersion() {
		return codeSystemVersionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystemVersion_VersionId() {
		return (EAttribute)codeSystemVersionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystemVersion_Description() {
		return (EAttribute)codeSystemVersionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystemVersion_ParentBranchPath() {
		return (EAttribute)codeSystemVersionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystemVersion_EffectiveDate() {
		return (EAttribute)codeSystemVersionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystemVersion_ImportDate() {
		return (EAttribute)codeSystemVersionEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystemVersion_LastUpdateDate() {
		return (EAttribute)codeSystemVersionEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCodeSystem() {
		return codeSystemEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystem_ShortName() {
		return (EAttribute)codeSystemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystem_CodeSystemOID() {
		return (EAttribute)codeSystemEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystem_Name() {
		return (EAttribute)codeSystemEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystem_MaintainingOrganizationLink() {
		return (EAttribute)codeSystemEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystem_Language() {
		return (EAttribute)codeSystemEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystem_Citation() {
		return (EAttribute)codeSystemEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystem_IconPath() {
		return (EAttribute)codeSystemEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystem_TerminologyComponentId() {
		return (EAttribute)codeSystemEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystem_RepositoryUuid() {
		return (EAttribute)codeSystemEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCodeSystem_CodeSystemVersions() {
		return (EReference)codeSystemEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCodeSystem_BranchPath() {
		return (EAttribute)codeSystemEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCodeSystem_ExtensionOf() {
		return (EReference)codeSystemEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TerminologymetadataFactory getTerminologymetadataFactory() {
		return (TerminologymetadataFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		codeSystemVersionEClass = createEClass(CODE_SYSTEM_VERSION);
		createEAttribute(codeSystemVersionEClass, CODE_SYSTEM_VERSION__VERSION_ID);
		createEAttribute(codeSystemVersionEClass, CODE_SYSTEM_VERSION__DESCRIPTION);
		createEAttribute(codeSystemVersionEClass, CODE_SYSTEM_VERSION__PARENT_BRANCH_PATH);
		createEAttribute(codeSystemVersionEClass, CODE_SYSTEM_VERSION__EFFECTIVE_DATE);
		createEAttribute(codeSystemVersionEClass, CODE_SYSTEM_VERSION__IMPORT_DATE);
		createEAttribute(codeSystemVersionEClass, CODE_SYSTEM_VERSION__LAST_UPDATE_DATE);

		codeSystemEClass = createEClass(CODE_SYSTEM);
		createEAttribute(codeSystemEClass, CODE_SYSTEM__SHORT_NAME);
		createEAttribute(codeSystemEClass, CODE_SYSTEM__CODE_SYSTEM_OID);
		createEAttribute(codeSystemEClass, CODE_SYSTEM__NAME);
		createEAttribute(codeSystemEClass, CODE_SYSTEM__MAINTAINING_ORGANIZATION_LINK);
		createEAttribute(codeSystemEClass, CODE_SYSTEM__LANGUAGE);
		createEAttribute(codeSystemEClass, CODE_SYSTEM__CITATION);
		createEAttribute(codeSystemEClass, CODE_SYSTEM__ICON_PATH);
		createEAttribute(codeSystemEClass, CODE_SYSTEM__TERMINOLOGY_COMPONENT_ID);
		createEAttribute(codeSystemEClass, CODE_SYSTEM__REPOSITORY_UUID);
		createEReference(codeSystemEClass, CODE_SYSTEM__CODE_SYSTEM_VERSIONS);
		createEAttribute(codeSystemEClass, CODE_SYSTEM__BRANCH_PATH);
		createEReference(codeSystemEClass, CODE_SYSTEM__EXTENSION_OF);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(codeSystemVersionEClass, CodeSystemVersion.class, "CodeSystemVersion", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCodeSystemVersion_VersionId(), ecorePackage.getEString(), "versionId", null, 1, 1, CodeSystemVersion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystemVersion_Description(), ecorePackage.getEString(), "description", null, 0, 1, CodeSystemVersion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystemVersion_ParentBranchPath(), ecorePackage.getEString(), "parentBranchPath", null, 0, 1, CodeSystemVersion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystemVersion_EffectiveDate(), ecorePackage.getEDate(), "effectiveDate", null, 0, 1, CodeSystemVersion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystemVersion_ImportDate(), ecorePackage.getEDate(), "importDate", null, 0, 1, CodeSystemVersion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystemVersion_LastUpdateDate(), ecorePackage.getEDate(), "lastUpdateDate", null, 0, 1, CodeSystemVersion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		addEOperation(codeSystemVersionEClass, this.getCodeSystem(), "getCodeSystem", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(codeSystemEClass, CodeSystem.class, "CodeSystem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCodeSystem_ShortName(), ecorePackage.getEString(), "shortName", null, 1, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystem_CodeSystemOID(), ecorePackage.getEString(), "codeSystemOID", "", 0, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystem_Name(), ecorePackage.getEString(), "name", null, 0, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystem_MaintainingOrganizationLink(), ecorePackage.getEString(), "maintainingOrganizationLink", null, 0, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getCodeSystem_Language(), ecorePackage.getEString(), "language", "ENG", 0, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystem_Citation(), ecorePackage.getEString(), "citation", null, 0, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystem_IconPath(), ecorePackage.getEString(), "iconPath", null, 0, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystem_TerminologyComponentId(), ecorePackage.getEString(), "terminologyComponentId", null, 0, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystem_RepositoryUuid(), ecorePackage.getEString(), "repositoryUuid", null, 1, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodeSystem_CodeSystemVersions(), this.getCodeSystemVersion(), null, "codeSystemVersions", null, 0, -1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeSystem_BranchPath(), ecorePackage.getEString(), "branchPath", null, 1, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodeSystem_ExtensionOf(), this.getCodeSystem(), null, "extensionOf", null, 0, 1, CodeSystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://www.eclipse.org/CDO/DBStore
		createDBStoreAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/CDO/DBStore</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createDBStoreAnnotations() {
		String source = "http://www.eclipse.org/CDO/DBStore";	
		addAnnotation
		  (getCodeSystem_CodeSystemOID(), 
		   source, 
		   new String[] {
			 "columnLength", "255"
		   });	
		addAnnotation
		  (getCodeSystem_Language(), 
		   source, 
		   new String[] {
			 "columnLength", "255"
		   });	
		addAnnotation
		  (getCodeSystem_RepositoryUuid(), 
		   source, 
		   new String[] {
			 "columnLength", "255"
		   });
	}

} //TerminologymetadataPackageImpl
