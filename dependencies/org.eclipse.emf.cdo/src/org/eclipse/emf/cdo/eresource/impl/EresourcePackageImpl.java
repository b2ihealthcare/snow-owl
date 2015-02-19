/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.etypes.EtypesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>.
 * 
 * @noextend This interface is not intended to be extended by clients. <!-- end-user-doc -->
 * @generated
 */
public class EresourcePackageImpl extends EPackageImpl implements EresourcePackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoResourceNodeEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoResourceFolderEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoResourceEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoResourceLeafEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoFileResourceEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoBinaryResourceEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass cdoTextResourceEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType resourceSetEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType uriEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType diagnosticEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#eNS_URI
   * @see #init()
   * @generated
   */
  private EresourcePackageImpl()
  {
    super(eNS_URI, EresourceFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * <p>
   * This method is used to initialize {@link EresourcePackage#eINSTANCE} when that field is accessed. Clients should
   * not invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static EresourcePackage init()
  {
    if (isInited)
      return (EresourcePackage)EPackage.Registry.INSTANCE.getEPackage(EresourcePackage.eNS_URI);

    // Obtain or create and register package
    EresourcePackageImpl theEresourcePackage = (EresourcePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof EresourcePackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new EresourcePackageImpl());

    isInited = true;

    // Initialize simple dependencies
    EtypesPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theEresourcePackage.createPackageContents();

    // Initialize created meta-data
    theEresourcePackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theEresourcePackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(EresourcePackage.eNS_URI, theEresourcePackage);
    return theEresourcePackage;
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @generated
   */
  public EClass getCDOResourceNode()
  {
    return cdoResourceNodeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @generated
   */
  public EReference getCDOResourceNode_Folder()
  {
    return (EReference)cdoResourceNodeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCDOResourceNode_Name()
  {
    return (EAttribute)cdoResourceNodeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCDOResourceNode_Path()
  {
    return (EAttribute)cdoResourceNodeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @generated
   */
  public EClass getCDOResourceFolder()
  {
    return cdoResourceFolderEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @generated
   */
  public EReference getCDOResourceFolder_Nodes()
  {
    return (EReference)cdoResourceFolderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCDOResource()
  {
    return cdoResourceEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_ResourceSet()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_URI()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCDOResource_Contents()
  {
    return (EReference)cdoResourceEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_Modified()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_Loaded()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_TrackingModification()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_Errors()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_Warnings()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getCDOResource_TimeStamp()
  {
    return (EAttribute)cdoResourceEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   */
  public EClass getCDOResourceLeaf()
  {
    return cdoResourceLeafEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   */
  public EClass getCDOFileResource()
  {
    return cdoFileResourceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   */
  public EClass getCDOBinaryResource()
  {
    return cdoBinaryResourceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCDOBinaryResource_Contents()
  {
    return (EAttribute)cdoBinaryResourceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   */
  public EClass getCDOTextResource()
  {
    return cdoTextResourceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCDOTextResource_Contents()
  {
    return (EAttribute)cdoTextResourceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getResourceSet()
  {
    return resourceSetEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getURI()
  {
    return uriEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getDiagnostic()
  {
    return diagnosticEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EresourceFactory getEresourceFactory()
  {
    return (EresourceFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its
   * first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
      return;
    isCreated = true;

    // Create classes and their features
    cdoResourceNodeEClass = createEClass(CDO_RESOURCE_NODE);
    createEReference(cdoResourceNodeEClass, CDO_RESOURCE_NODE__FOLDER);
    createEAttribute(cdoResourceNodeEClass, CDO_RESOURCE_NODE__NAME);
    createEAttribute(cdoResourceNodeEClass, CDO_RESOURCE_NODE__PATH);

    cdoResourceFolderEClass = createEClass(CDO_RESOURCE_FOLDER);
    createEReference(cdoResourceFolderEClass, CDO_RESOURCE_FOLDER__NODES);

    cdoResourceEClass = createEClass(CDO_RESOURCE);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__RESOURCE_SET);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__URI);
    createEReference(cdoResourceEClass, CDO_RESOURCE__CONTENTS);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__MODIFIED);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__LOADED);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__TRACKING_MODIFICATION);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__ERRORS);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__WARNINGS);
    createEAttribute(cdoResourceEClass, CDO_RESOURCE__TIME_STAMP);

    cdoResourceLeafEClass = createEClass(CDO_RESOURCE_LEAF);

    cdoFileResourceEClass = createEClass(CDO_FILE_RESOURCE);

    cdoBinaryResourceEClass = createEClass(CDO_BINARY_RESOURCE);
    createEAttribute(cdoBinaryResourceEClass, CDO_BINARY_RESOURCE__CONTENTS);

    cdoTextResourceEClass = createEClass(CDO_TEXT_RESOURCE);
    createEAttribute(cdoTextResourceEClass, CDO_TEXT_RESOURCE__CONTENTS);

    // Create data types
    resourceSetEDataType = createEDataType(RESOURCE_SET);
    uriEDataType = createEDataType(URI);
    diagnosticEDataType = createEDataType(DIAGNOSTIC);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
   * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
      return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
    EtypesPackage theEtypesPackage = (EtypesPackage)EPackage.Registry.INSTANCE.getEPackage(EtypesPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    cdoResourceFolderEClass.getESuperTypes().add(this.getCDOResourceNode());
    cdoResourceEClass.getESuperTypes().add(this.getCDOResourceLeaf());
    cdoResourceLeafEClass.getESuperTypes().add(this.getCDOResourceNode());
    cdoFileResourceEClass.getESuperTypes().add(this.getCDOResourceLeaf());
    cdoBinaryResourceEClass.getESuperTypes().add(this.getCDOFileResource());
    cdoTextResourceEClass.getESuperTypes().add(this.getCDOFileResource());

    // Initialize classes and features; add operations and parameters
    initEClass(cdoResourceNodeEClass, CDOResourceNode.class,
        "CDOResourceNode", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getCDOResourceNode_Folder(),
        this.getCDOResourceFolder(),
        this.getCDOResourceFolder_Nodes(),
        "folder", null, 0, 1, CDOResourceNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getCDOResourceNode_Name(),
        ecorePackage.getEString(),
        "name", null, 0, 1, CDOResourceNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getCDOResourceNode_Path(),
        ecorePackage.getEString(),
        "path", null, 0, 1, CDOResourceNode.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(cdoResourceFolderEClass, CDOResourceFolder.class,
        "CDOResourceFolder", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getCDOResourceFolder_Nodes(),
        this.getCDOResourceNode(),
        this.getCDOResourceNode_Folder(),
        "nodes", null, 0, -1, CDOResourceFolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    EOperation op = addEOperation(cdoResourceFolderEClass, this.getCDOResourceFolder(),
        "addResourceFolder", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "name", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

    op = addEOperation(cdoResourceFolderEClass, this.getCDOResource(), "addResource", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "name", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

    initEClass(cdoResourceEClass, CDOResource.class,
        "CDOResource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(
        getCDOResource_ResourceSet(),
        this.getResourceSet(),
        "resourceSet", null, 0, 1, CDOResource.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getCDOResource_URI(),
        this.getURI(),
        "uRI", null, 0, 1, CDOResource.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getCDOResource_Contents(),
        theEcorePackage.getEObject(),
        null,
        "contents", null, 0, -1, CDOResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getCDOResource_Modified(),
        ecorePackage.getEBoolean(),
        "modified", null, 0, 1, CDOResource.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getCDOResource_Loaded(),
        ecorePackage.getEBoolean(),
        "loaded", "true", 0, 1, CDOResource.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
    initEAttribute(
        getCDOResource_TrackingModification(),
        ecorePackage.getEBoolean(),
        "trackingModification", null, 0, 1, CDOResource.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getCDOResource_Errors(),
        this.getDiagnostic(),
        "errors", null, 0, -1, CDOResource.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getCDOResource_Warnings(),
        this.getDiagnostic(),
        "warnings", null, 0, -1, CDOResource.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getCDOResource_TimeStamp(),
        theEcorePackage.getELong(),
        "timeStamp", null, 0, 1, CDOResource.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(cdoResourceLeafEClass, CDOResourceLeaf.class,
        "CDOResourceLeaf", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(cdoFileResourceEClass, CDOFileResource.class,
        "CDOFileResource", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    addEOperation(cdoFileResourceEClass, theEtypesPackage.getLob(), "getContents", 1, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

    initEClass(cdoBinaryResourceEClass, CDOBinaryResource.class,
        "CDOBinaryResource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(
        getCDOBinaryResource_Contents(),
        theEtypesPackage.getBlob(),
        "contents", null, 1, 1, CDOBinaryResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(cdoTextResourceEClass, CDOTextResource.class,
        "CDOTextResource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(
        getCDOTextResource_Contents(),
        theEtypesPackage.getClob(),
        "contents", null, 1, 1, CDOTextResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    // Initialize data types
    initEDataType(resourceSetEDataType, ResourceSet.class,
        "ResourceSet", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEDataType(uriEDataType, org.eclipse.emf.common.util.URI.class,
        "URI", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEDataType(diagnosticEDataType, Diagnostic.class, "Diagnostic", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http://www.eclipse.org/CDO/DBStore
    createDBStoreAnnotations();
  }

  /**
   * Initializes the annotations for <b>http://www.eclipse.org/CDO/DBStore</b>. <!-- begin-user-doc -->
   * 
   * @since 4.0 <!-- end-user-doc -->
   * @generated
   */
  protected void createDBStoreAnnotations()
  {
    String source = "http://www.eclipse.org/CDO/DBStore"; //$NON-NLS-1$		
    addAnnotation(getCDOResourceNode_Name(), source, new String[] { "columnType", "VARCHAR", //$NON-NLS-1$ //$NON-NLS-2$
        "columnLength", "255" //$NON-NLS-1$ //$NON-NLS-2$
    });
  }

} // EresourcePackageImpl
