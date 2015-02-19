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

import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIData;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>CDO Resource</b></em>'.
 *
 * @extends Resource
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * <!-- end-user-doc -->
 *                  <p>
 *                  The following features are supported:
 *                  <ul>
 *                  <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getResourceSet <em>Resource Set</em>}</li>
 *                  <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getURI <em>URI</em>}</li>
 *                  <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getContents <em>Contents</em>}</li>
 *                  <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#isModified <em>Modified</em>}</li>
 *                  <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#isLoaded <em>Loaded</em>}</li>
 *                  <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#isTrackingModification <em>Tracking
 *                  Modification</em>}</li>
 *                  <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getErrors <em>Errors</em>}</li>
 *                  <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getWarnings <em>Warnings</em>}</li>
 *                  <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getTimeStamp <em>Time Stamp</em>}</li>
 *                  </ul>
 *                  </p>
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource()
 * @model
 * @generated
 */
public interface CDOResource extends CDOResourceLeaf, Resource
{
  /**
   * @ADDED
   * @since 2.0
   */
  public static final String OPTION_SAVE_PROGRESS_MONITOR = IProgressMonitor.class.getName();

  /**
   * @ADDED
   * @since 3.0
   */
  public static final String OPTION_SAVE_OVERRIDE_TRANSACTION = CDOTransaction.class.getName();

  /**
   * @ADDED
   * @since 4.0
   */
  public static final String OPTION_SAVE_BASE_URI = "OPTION_SAVE_BASE_URI";

  /**
   * @ADDED
   * @since 4.1
   */
  public static final String PREFETCH_PARAMETER = "prefetch";

  /**
   * Returns the value of the '<em><b>Resource Set</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Resource Set</em>' attribute.
   * @see #setResourceSet(ResourceSet)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_ResourceSet()
   * @model dataType="org.eclipse.emf.cdo.eresource.ResourceSet" transient="true"
   * @generated
   */
  ResourceSet getResourceSet();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#getResourceSet <em>Resource Set</em>}'
   * attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Resource Set</em>' attribute.
   * @see #getResourceSet()
   * @generated
   */
  void setResourceSet(ResourceSet value);

  /**
   * Returns the value of the '<em><b>URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * The URI format is explained in {@link CDOURIUtil} and {@link CDOURIData}.
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>URI</em>' attribute.
   * @see #setURI(URI)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_URI()
   * @model dataType="org.eclipse.emf.cdo.eresource.URI" transient="true" volatile="true" derived="true"
   * @generated
   */
  URI getURI();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#getURI <em>URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * The URI format is explained in {@link CDOURIUtil} and {@link CDOURIData}.
   * <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>URI</em>' attribute.
   * @see #getURI()
   * @generated
   */
  void setURI(URI value);

  /**
   * Returns the value of the '<em><b>Contents</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.ecore.EObject}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Contents</em>' containment reference list.
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Contents()
   * @model containment="true"
   * @generated
   */
  EList<EObject> getContents();

  /**
   * Returns the value of the '<em><b>Modified</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Modified</em>' attribute.
   * @see #setModified(boolean)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Modified()
   * @model transient="true"
   * @generated
   */
  boolean isModified();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#isModified <em>Modified</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Modified</em>' attribute.
   * @see #isModified()
   * @generated
   */
  void setModified(boolean value);

  /**
   * Returns the value of the '<em><b>Loaded</b></em>' attribute. The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Loaded</em>' attribute.
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Loaded()
   * @model default="true" transient="true" suppressedSetVisibility="true"
   * @generated
   */
  boolean isLoaded();

  /**
   * Returns the value of the '<em><b>Tracking Modification</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Tracking Modification</em>' attribute.
   * @see #setTrackingModification(boolean)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_TrackingModification()
   * @model transient="true"
   * @generated
   */
  boolean isTrackingModification();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#isTrackingModification
   * <em>Tracking Modification</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Tracking Modification</em>' attribute.
   * @see #isTrackingModification()
   * @generated
   */
  void setTrackingModification(boolean value);

  /**
   * Returns the value of the '<em><b>Errors</b></em>' attribute list. The list contents are of type
   * {@link org.eclipse.emf.ecore.resource.Resource.Diagnostic}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Errors</em>' attribute list.
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Errors()
   * @model dataType="org.eclipse.emf.cdo.eresource.Diagnostic" transient="true" changeable="false" volatile="true"
   *        derived="true"
   * @generated
   */
  EList<Diagnostic> getErrors();

  /**
   * Returns the value of the '<em><b>Warnings</b></em>' attribute list. The list contents are of type
   * {@link org.eclipse.emf.ecore.resource.Resource.Diagnostic}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Warnings</em>' attribute list.
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Warnings()
   * @model dataType="org.eclipse.emf.cdo.eresource.Diagnostic" transient="true" changeable="false" volatile="true"
   *        derived="true"
   * @generated
   */
  EList<Diagnostic> getWarnings();

  /**
   * Returns the value of the '<em><b>Time Stamp</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Time Stamp</em>' attribute.
   * @see #setTimeStamp(long)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_TimeStamp()
   * @model transient="true"
   * @generated
   */
  long getTimeStamp();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#getTimeStamp <em>Time Stamp</em>}'
   * attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Time Stamp</em>' attribute.
   * @see #getTimeStamp()
   * @generated
   */
  void setTimeStamp(long value);

  /**
   * @ADDED
   * @since 2.0
   */
  public boolean isExisting();

} // CDOResource
