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
package org.eclipse.emf.cdo.eresource;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the Eresource model. It contains accessors for the meta objects to
 * represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * 
 * @apiviz.exclude
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.eresource.EresourceFactory
 * @model kind="package"
 * @generated
 */
public interface EresourcePackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "eresource"; //$NON-NLS-1$

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/Eresource/4.0.0"; //$NON-NLS-1$

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "eresource"; //$NON-NLS-1$

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  EresourcePackage eINSTANCE = org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl
   * <em>CDO Resource Node</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOResourceNode()
   * @generated
   */
  int CDO_RESOURCE_NODE = 0;

  /**
   * The feature id for the '<em><b>Folder</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_NODE__FOLDER = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_NODE__NAME = 1;

  /**
   * The feature id for the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_NODE__PATH = 2;

  /**
   * The number of structural features of the '<em>CDO Resource Node</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_NODE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceFolderImpl
   * <em>CDO Resource Folder</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.eresource.impl.CDOResourceFolderImpl
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOResourceFolder()
   * @generated
   */
  int CDO_RESOURCE_FOLDER = 1;

  /**
   * The feature id for the '<em><b>Folder</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_FOLDER__FOLDER = CDO_RESOURCE_NODE__FOLDER;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_FOLDER__NAME = CDO_RESOURCE_NODE__NAME;

  /**
   * The feature id for the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_FOLDER__PATH = CDO_RESOURCE_NODE__PATH;

  /**
   * The feature id for the '<em><b>Nodes</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_FOLDER__NODES = CDO_RESOURCE_NODE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>CDO Resource Folder</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_FOLDER_FEATURE_COUNT = CDO_RESOURCE_NODE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceLeafImpl
   * <em>CDO Resource Leaf</em>}' class. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.eresource.impl.CDOResourceLeafImpl
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOResourceLeaf()
   * @generated NOT
   */
  int CDO_RESOURCE_LEAF = 7;

  /**
   * The feature id for the '<em><b>Folder</b></em>' container reference. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_LEAF__FOLDER = CDO_RESOURCE_NODE__FOLDER;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_LEAF__NAME = CDO_RESOURCE_NODE__NAME;

  /**
   * The feature id for the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_LEAF__PATH = CDO_RESOURCE_NODE__PATH;

  /**
   * The number of structural features of the '<em>CDO Resource Leaf</em>' class. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_LEAF_FEATURE_COUNT = CDO_RESOURCE_NODE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl <em>CDO Resource</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOResource()
   * @generated
   */
  int CDO_RESOURCE = 2;

  /**
   * The feature id for the '<em><b>Folder</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__FOLDER = CDO_RESOURCE_LEAF__FOLDER;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__NAME = CDO_RESOURCE_LEAF__NAME;

  /**
   * The feature id for the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__PATH = CDO_RESOURCE_LEAF__PATH;

  /**
   * The feature id for the '<em><b>Resource Set</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__RESOURCE_SET = CDO_RESOURCE_LEAF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>URI</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__URI = CDO_RESOURCE_LEAF_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Contents</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__CONTENTS = CDO_RESOURCE_LEAF_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Modified</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__MODIFIED = CDO_RESOURCE_LEAF_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Loaded</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__LOADED = CDO_RESOURCE_LEAF_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Tracking Modification</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__TRACKING_MODIFICATION = CDO_RESOURCE_LEAF_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Errors</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__ERRORS = CDO_RESOURCE_LEAF_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Warnings</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__WARNINGS = CDO_RESOURCE_LEAF_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Time Stamp</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__TIME_STAMP = CDO_RESOURCE_LEAF_FEATURE_COUNT + 8;

  /**
   * The number of structural features of the '<em>CDO Resource</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_FEATURE_COUNT = CDO_RESOURCE_LEAF_FEATURE_COUNT + 9;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOFileResourceImpl
   * <em>CDO File Resource</em>}' class. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.eresource.impl.CDOFileResourceImpl
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOFileResource()
   * @generated NOT
   */
  int CDO_FILE_RESOURCE = 8;

  /**
   * The feature id for the '<em><b>Folder</b></em>' container reference. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_FILE_RESOURCE__FOLDER = CDO_RESOURCE_LEAF__FOLDER;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_FILE_RESOURCE__NAME = CDO_RESOURCE_LEAF__NAME;

  /**
   * The feature id for the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_FILE_RESOURCE__PATH = CDO_RESOURCE_LEAF__PATH;

  /**
   * The number of structural features of the '<em>CDO File Resource</em>' class. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_FILE_RESOURCE_FEATURE_COUNT = CDO_RESOURCE_LEAF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOBinaryResourceImpl
   * <em>CDO Binary Resource</em>}' class. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.eresource.impl.CDOBinaryResourceImpl
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOBinaryResource()
   * @generated NOT
   */
  int CDO_BINARY_RESOURCE = 9;

  /**
   * The feature id for the '<em><b>Folder</b></em>' container reference. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_BINARY_RESOURCE__FOLDER = CDO_FILE_RESOURCE__FOLDER;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_BINARY_RESOURCE__NAME = CDO_FILE_RESOURCE__NAME;

  /**
   * The feature id for the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_BINARY_RESOURCE__PATH = CDO_FILE_RESOURCE__PATH;

  /**
   * The feature id for the '<em><b>Contents</b></em>' attribute. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_BINARY_RESOURCE__CONTENTS = CDO_FILE_RESOURCE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>CDO Binary Resource</em>' class. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_BINARY_RESOURCE_FEATURE_COUNT = CDO_FILE_RESOURCE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOTextResourceImpl
   * <em>CDO Text Resource</em>}' class. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.eresource.impl.CDOTextResourceImpl
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOTextResource()
   * @generated NOT
   */
  int CDO_TEXT_RESOURCE = 10;

  /**
   * The feature id for the '<em><b>Folder</b></em>' container reference. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_TEXT_RESOURCE__FOLDER = CDO_FILE_RESOURCE__FOLDER;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_TEXT_RESOURCE__NAME = CDO_FILE_RESOURCE__NAME;

  /**
   * The feature id for the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_TEXT_RESOURCE__PATH = CDO_FILE_RESOURCE__PATH;

  /**
   * The feature id for the '<em><b>Contents</b></em>' attribute. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_TEXT_RESOURCE__CONTENTS = CDO_FILE_RESOURCE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>CDO Text Resource</em>' class. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_TEXT_RESOURCE_FEATURE_COUNT = CDO_FILE_RESOURCE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '<em>Resource Set</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.resource.ResourceSet
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getResourceSet()
   * @generated NOT
   */
  int RESOURCE_SET = 3;

  /**
   * The meta object id for the '<em>URI</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.common.util.URI
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getURI()
   * @generated NOT
   */
  int URI = 4;

  /**
   * The meta object id for the '<em>Diagnostic</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getDiagnostic()
   * @generated NOT
   */
  int DIAGNOSTIC = 5;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.eresource.CDOResourceNode <em>CDO Resource Node</em>}
   * '. <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @return the meta object for class '<em>CDO Resource Node</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResourceNode
   * @generated
   */
  EClass getCDOResourceNode();

  /**
   * Returns the meta object for the container reference '
   * {@link org.eclipse.emf.cdo.eresource.CDOResourceNode#getFolder <em>Folder</em>}'. <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Folder</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResourceNode#getFolder()
   * @see #getCDOResourceNode()
   * @generated
   */
  EReference getCDOResourceNode_Folder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResourceNode#getName
   * <em>Name</em>}'. <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResourceNode#getName()
   * @see #getCDOResourceNode()
   * @generated
   */
  EAttribute getCDOResourceNode_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResourceNode#getPath
   * <em>Path</em>}'. <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Path</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResourceNode#getPath()
   * @see #getCDOResourceNode()
   * @generated
   */
  EAttribute getCDOResourceNode_Path();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.eresource.CDOResourceFolder
   * <em>CDO Resource Folder</em>}'. <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @return the meta object for class '<em>CDO Resource Folder</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResourceFolder
   * @generated
   */
  EClass getCDOResourceFolder();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.eresource.CDOResourceFolder#getNodes <em>Nodes</em>}'. <!-- begin-user-doc -->
   * 
   * @since 2.0<!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Nodes</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResourceFolder#getNodes()
   * @see #getCDOResourceFolder()
   * @generated
   */
  EReference getCDOResourceFolder_Nodes();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.eresource.CDOResource <em>CDO Resource</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>CDO Resource</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource
   * @generated
   */
  EClass getCDOResource();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#getResourceSet
   * <em>Resource Set</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Resource Set</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getResourceSet()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_ResourceSet();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#getURI <em>URI</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>URI</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getURI()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_URI();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.eresource.CDOResource#getContents <em>Contents</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Contents</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getContents()
   * @see #getCDOResource()
   * @generated
   */
  EReference getCDOResource_Contents();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#isModified
   * <em>Modified</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Modified</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#isModified()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_Modified();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#isLoaded
   * <em>Loaded</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Loaded</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#isLoaded()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_Loaded();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#isTrackingModification
   * <em>Tracking Modification</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Tracking Modification</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#isTrackingModification()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_TrackingModification();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.eresource.CDOResource#getErrors
   * <em>Errors</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Errors</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getErrors()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_Errors();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.eresource.CDOResource#getWarnings
   * <em>Warnings</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Warnings</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getWarnings()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_Warnings();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#getTimeStamp
   * <em>Time Stamp</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Time Stamp</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getTimeStamp()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_TimeStamp();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.eresource.CDOResourceLeaf <em>CDO Resource Leaf</em>}
   * '. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return the meta object for class '<em>CDO Resource Leaf</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResourceLeaf
   * @generated
   */
  EClass getCDOResourceLeaf();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.eresource.CDOFileResource <em>CDO File Resource</em>}
   * '. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return the meta object for class '<em>CDO File Resource</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOFileResource
   * @generated
   */
  EClass getCDOFileResource();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.eresource.CDOBinaryResource
   * <em>CDO Binary Resource</em>}'. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return the meta object for class '<em>CDO Binary Resource</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOBinaryResource
   * @generated
   */
  EClass getCDOBinaryResource();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOBinaryResource#getContents
   * <em>Contents</em>}'. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Contents</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOBinaryResource#getContents()
   * @see #getCDOBinaryResource()
   * @generated
   */
  EAttribute getCDOBinaryResource_Contents();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.eresource.CDOTextResource <em>CDO Text Resource</em>}
   * '. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return the meta object for class '<em>CDO Text Resource</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOTextResource
   * @generated
   */
  EClass getCDOTextResource();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOTextResource#getContents
   * <em>Contents</em>}'. <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Contents</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOTextResource#getContents()
   * @see #getCDOTextResource()
   * @generated
   */
  EAttribute getCDOTextResource_Contents();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.ecore.resource.ResourceSet <em>Resource Set</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>Resource Set</em>'.
   * @see org.eclipse.emf.ecore.resource.ResourceSet
   * @model instanceClass="org.eclipse.emf.ecore.resource.ResourceSet" serializeable="false"
   * @generated
   */
  EDataType getResourceSet();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>URI</em>'.
   * @see org.eclipse.emf.common.util.URI
   * @model instanceClass="org.eclipse.emf.common.util.URI"
   * @generated
   */
  EDataType getURI();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.ecore.resource.Resource.Diagnostic
   * <em>Diagnostic</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>Diagnostic</em>'.
   * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic
   * @model instanceClass="org.eclipse.emf.ecore.resource.Resource.Diagnostic" serializeable="false"
   * @generated
   */
  EDataType getDiagnostic();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  EresourceFactory getEresourceFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * 
   * @since 4.1
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients. <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl
     * <em>CDO Resource Node</em>}' class. <!-- begin-user-doc -->
     * 
     * @since 2.0<!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOResourceNode()
     * @generated
     */
    EClass CDO_RESOURCE_NODE = eINSTANCE.getCDOResourceNode();

    /**
     * The meta object literal for the '<em><b>Folder</b></em>' container reference feature. <!-- begin-user-doc -->
     * 
     * @since 2.0<!-- end-user-doc -->
     * @generated
     */
    EReference CDO_RESOURCE_NODE__FOLDER = eINSTANCE.getCDOResourceNode_Folder();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc -->
     * 
     * @since 2.0<!-- end-user-doc -->
     * @generated
     */
    EAttribute CDO_RESOURCE_NODE__NAME = eINSTANCE.getCDOResourceNode_Name();

    /**
     * The meta object literal for the '<em><b>Path</b></em>' attribute feature. <!-- begin-user-doc -->
     * 
     * @since 2.0<!-- end-user-doc -->
     * @generated
     */
    EAttribute CDO_RESOURCE_NODE__PATH = eINSTANCE.getCDOResourceNode_Path();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceFolderImpl
     * <em>CDO Resource Folder</em>}' class. <!-- begin-user-doc -->
     * 
     * @since 2.0<!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.eresource.impl.CDOResourceFolderImpl
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOResourceFolder()
     * @generated
     */
    EClass CDO_RESOURCE_FOLDER = eINSTANCE.getCDOResourceFolder();

    /**
     * The meta object literal for the '<em><b>Nodes</b></em>' containment reference list feature. <!-- begin-user-doc
     * -->
     * 
     * @since 2.0<!-- end-user-doc -->
     * @generated
     */
    EReference CDO_RESOURCE_FOLDER__NODES = eINSTANCE.getCDOResourceFolder_Nodes();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl <em>CDO Resource</em>}
     * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOResource()
     * @generated
     */
    EClass CDO_RESOURCE = eINSTANCE.getCDOResource();

    /**
     * The meta object literal for the '<em><b>Resource Set</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__RESOURCE_SET = eINSTANCE.getCDOResource_ResourceSet();

    /**
     * The meta object literal for the '<em><b>URI</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__URI = eINSTANCE.getCDOResource_URI();

    /**
     * The meta object literal for the '<em><b>Contents</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CDO_RESOURCE__CONTENTS = eINSTANCE.getCDOResource_Contents();

    /**
     * The meta object literal for the '<em><b>Modified</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__MODIFIED = eINSTANCE.getCDOResource_Modified();

    /**
     * The meta object literal for the '<em><b>Loaded</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__LOADED = eINSTANCE.getCDOResource_Loaded();

    /**
     * The meta object literal for the '<em><b>Tracking Modification</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__TRACKING_MODIFICATION = eINSTANCE.getCDOResource_TrackingModification();

    /**
     * The meta object literal for the '<em><b>Errors</b></em>' attribute list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__ERRORS = eINSTANCE.getCDOResource_Errors();

    /**
     * The meta object literal for the '<em><b>Warnings</b></em>' attribute list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__WARNINGS = eINSTANCE.getCDOResource_Warnings();

    /**
     * The meta object literal for the '<em><b>Time Stamp</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__TIME_STAMP = eINSTANCE.getCDOResource_TimeStamp();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceLeafImpl
     * <em>CDO Resource Leaf</em>}' class. <!-- begin-user-doc -->
     * 
     * @since 4.1 <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.eresource.impl.CDOResourceLeafImpl
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOResourceLeaf()
     * @generated
     */
    EClass CDO_RESOURCE_LEAF = eINSTANCE.getCDOResourceLeaf();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOFileResourceImpl
     * <em>CDO File Resource</em>}' class. <!-- begin-user-doc -->
     * 
     * @since 4.1 <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.eresource.impl.CDOFileResourceImpl
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOFileResource()
     * @generated
     */
    EClass CDO_FILE_RESOURCE = eINSTANCE.getCDOFileResource();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOBinaryResourceImpl
     * <em>CDO Binary Resource</em>}' class. <!-- begin-user-doc -->
     * 
     * @since 4.1 <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.eresource.impl.CDOBinaryResourceImpl
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOBinaryResource()
     * @generated
     */
    EClass CDO_BINARY_RESOURCE = eINSTANCE.getCDOBinaryResource();

    /**
     * The meta object literal for the '<em><b>Contents</b></em>' attribute feature. <!-- begin-user-doc -->
     * 
     * @since 4.1 <!-- end-user-doc -->
     * @generated
     */
    EAttribute CDO_BINARY_RESOURCE__CONTENTS = eINSTANCE.getCDOBinaryResource_Contents();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOTextResourceImpl
     * <em>CDO Text Resource</em>}' class. <!-- begin-user-doc -->
     * 
     * @since 4.1 <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.eresource.impl.CDOTextResourceImpl
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOTextResource()
     * @generated
     */
    EClass CDO_TEXT_RESOURCE = eINSTANCE.getCDOTextResource();

    /**
     * The meta object literal for the '<em><b>Contents</b></em>' attribute feature. <!-- begin-user-doc -->
     * 
     * @since 4.1 <!-- end-user-doc -->
     * @generated
     */
    EAttribute CDO_TEXT_RESOURCE__CONTENTS = eINSTANCE.getCDOTextResource_Contents();

    /**
     * The meta object literal for the '<em>Resource Set</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getResourceSet()
     * @generated
     */
    EDataType RESOURCE_SET = eINSTANCE.getResourceSet();

    /**
     * The meta object literal for the '<em>URI</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.common.util.URI
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getURI()
     * @generated
     */
    EDataType URI = eINSTANCE.getURI();

    /**
     * The meta object literal for the '<em>Diagnostic</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getDiagnostic()
     * @generated
     */
    EDataType DIAGNOSTIC = eINSTANCE.getDiagnostic();

  }

} // EresourcePackage
