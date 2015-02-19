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

//import org.eclipse.emf.cdo.eresource.*;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>.
 * 
 * @noextend This interface is not intended to be extended by clients. <!-- end-user-doc -->
 * @generated
 */
public class EresourceFactoryImpl extends EFactoryImpl implements EresourceFactory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static EresourceFactory init()
  {
    try
    {
      EresourceFactory theEresourceFactory = (EresourceFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/Eresource/4.0.0"); //$NON-NLS-1$ 
      if (theEresourceFactory != null)
      {
        return theEresourceFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new EresourceFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EresourceFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("cast")
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case EresourcePackage.CDO_RESOURCE_FOLDER:
      return (EObject)createCDOResourceFolder();
    case EresourcePackage.CDO_RESOURCE:
      return (EObject)createCDOResource();
    case EresourcePackage.CDO_BINARY_RESOURCE:
      return (EObject)createCDOBinaryResource();
    case EresourcePackage.CDO_TEXT_RESOURCE:
      return (EObject)createCDOTextResource();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case EresourcePackage.URI:
      return createURIFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case EresourcePackage.URI:
      return convertURIToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 2.0 <!-- end-user-doc -->
   * @generated
   */
  public CDOResourceFolder createCDOResourceFolder()
  {
    CDOResourceFolderImpl cdoResourceFolder = new CDOResourceFolderImpl();
    return cdoResourceFolder;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CDOResource createCDOResource()
  {
    CDOResourceImpl cdoResource = new CDOResourceImpl();
    return cdoResource;
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   */
  public CDOBinaryResource createCDOBinaryResource()
  {
    CDOBinaryResourceImpl cdoBinaryResource = new CDOBinaryResourceImpl();
    return cdoBinaryResource;
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @generated
   */
  public CDOTextResource createCDOTextResource()
  {
    CDOTextResourceImpl cdoTextResource = new CDOTextResourceImpl();
    return cdoTextResource;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public URI createURIFromString(EDataType eDataType, String initialValue)
  {
    return URI.createURI(initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public String convertURIToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EresourcePackage getEresourcePackage()
  {
    return (EresourcePackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static EresourcePackage getPackage()
  {
    return EresourcePackage.eINSTANCE;
  }

} // EresourceFactoryImpl
