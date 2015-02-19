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
package org.eclipse.emf.cdo.etypes.impl;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>.
 * 
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients. <!-- end-user-doc -->
 * @generated
 */
public class EtypesPackageImpl extends EPackageImpl implements EtypesPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass modelElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass annotationEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType lobEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType blobEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType clobEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.etypes.EtypesPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private EtypesPackageImpl()
  {
    super(eNS_URI, EtypesFactory.eINSTANCE);
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
   * This method is used to initialize {@link EtypesPackage#eINSTANCE} when that field is accessed. Clients should not
   * invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static EtypesPackage init()
  {
    if (isInited)
      return (EtypesPackage)EPackage.Registry.INSTANCE.getEPackage(EtypesPackage.eNS_URI);

    // Obtain or create and register package
    EtypesPackageImpl theEtypesPackage = (EtypesPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof EtypesPackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new EtypesPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    EcorePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theEtypesPackage.createPackageContents();

    // Initialize created meta-data
    theEtypesPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theEtypesPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(EtypesPackage.eNS_URI, theEtypesPackage);
    return theEtypesPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getModelElement()
  {
    return modelElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getModelElement_Annotations()
  {
    return (EReference)modelElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getAnnotation()
  {
    return annotationEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getAnnotation_Source()
  {
    return (EAttribute)annotationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getAnnotation_Details()
  {
    return (EReference)annotationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getAnnotation_ModelElement()
  {
    return (EReference)annotationEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getAnnotation_Contents()
  {
    return (EReference)annotationEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getAnnotation_References()
  {
    return (EReference)annotationEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   */
  public EDataType getLob()
  {
    return lobEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getBlob()
  {
    return blobEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getClob()
  {
    return clobEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EtypesFactory getEtypesFactory()
  {
    return (EtypesFactory)getEFactoryInstance();
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
    modelElementEClass = createEClass(MODEL_ELEMENT);
    createEReference(modelElementEClass, MODEL_ELEMENT__ANNOTATIONS);

    annotationEClass = createEClass(ANNOTATION);
    createEAttribute(annotationEClass, ANNOTATION__SOURCE);
    createEReference(annotationEClass, ANNOTATION__DETAILS);
    createEReference(annotationEClass, ANNOTATION__MODEL_ELEMENT);
    createEReference(annotationEClass, ANNOTATION__CONTENTS);
    createEReference(annotationEClass, ANNOTATION__REFERENCES);

    // Create data types
    blobEDataType = createEDataType(BLOB);
    clobEDataType = createEDataType(CLOB);
    lobEDataType = createEDataType(LOB);
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

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    annotationEClass.getESuperTypes().add(this.getModelElement());

    // Initialize classes and features; add operations and parameters
    initEClass(modelElementEClass, ModelElement.class,
        "ModelElement", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getModelElement_Annotations(),
        this.getAnnotation(),
        this.getAnnotation_ModelElement(),
        "annotations", null, 0, -1, ModelElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    EOperation op = addEOperation(modelElementEClass, this.getAnnotation(),
        "getAnnotation", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "source", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

    initEClass(annotationEClass, Annotation.class,
        "Annotation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(
        getAnnotation_Source(),
        theEcorePackage.getEString(),
        "source", null, 0, 1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getAnnotation_Details(),
        theEcorePackage.getEStringToStringMapEntry(),
        null,
        "details", null, 0, -1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getAnnotation_ModelElement(),
        this.getModelElement(),
        this.getModelElement_Annotations(),
        "modelElement", null, 0, 1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getAnnotation_Contents(),
        theEcorePackage.getEObject(),
        null,
        "contents", null, 0, -1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getAnnotation_References(),
        theEcorePackage.getEObject(),
        null,
        "references", null, 0, -1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    // Initialize data types
    initEDataType(blobEDataType, CDOBlob.class, "Blob", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEDataType(clobEDataType, CDOClob.class, "Clob", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEDataType(lobEDataType, CDOLob.class, "Lob", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    // Create resource
    createResource(eNS_URI);
  }

} // EtypesPackageImpl
