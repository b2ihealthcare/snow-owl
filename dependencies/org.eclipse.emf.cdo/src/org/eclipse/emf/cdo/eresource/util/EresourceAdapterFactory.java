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
package org.eclipse.emf.cdo.eresource.util;

//import org.eclipse.emf.cdo.eresource.*;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage
 * @generated
 */
public class EresourceAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static EresourcePackage modelPackage;

  /**
   * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EresourceAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = EresourcePackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This implementation
   * returns <code>true</code> if the object is either the model's package or is an instance object of the model. <!--
   * end-user-doc -->
   * 
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected EresourceSwitch<Adapter> modelSwitch = new EresourceSwitch<Adapter>()
  {
    @Override
    public Adapter caseCDOResourceNode(CDOResourceNode object)
    {
      return createCDOResourceNodeAdapter();
    }

    @Override
    public Adapter caseCDOResourceFolder(CDOResourceFolder object)
    {
      return createCDOResourceFolderAdapter();
    }

    @Override
    public Adapter caseCDOResource(CDOResource object)
    {
      return createCDOResourceAdapter();
    }

    @Override
    public Adapter caseCDOResourceLeaf(CDOResourceLeaf object)
    {
      return createCDOResourceLeafAdapter();
    }

    @Override
    public Adapter caseCDOFileResource(CDOFileResource<?> object)
    {
      return createCDOFileResourceAdapter();
    }

    @Override
    public Adapter caseCDOBinaryResource(CDOBinaryResource object)
    {
      return createCDOBinaryResourceAdapter();
    }

    @Override
    public Adapter caseCDOTextResource(CDOTextResource object)
    {
      return createCDOTextResourceAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param target
   *          the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.eresource.CDOResourceNode
   * <em>CDO Resource Node</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * 
   * @since 2.0<!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.eresource.CDOResourceNode
   * @generated
   */
  public Adapter createCDOResourceNodeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.eresource.CDOResourceFolder
   * <em>CDO Resource Folder</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * 
   * @since 2.0<!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.eresource.CDOResourceFolder
   * @generated
   */
  public Adapter createCDOResourceFolderAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.eresource.CDOResource
   * <em>CDO Resource</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.eresource.CDOResource
   * @generated
   */
  public Adapter createCDOResourceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.eresource.CDOResourceLeaf
   * <em>CDO Resource Leaf</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.eresource.CDOResourceLeaf
   * @generated
   */
  public Adapter createCDOResourceLeafAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.eresource.CDOFileResource
   * <em>CDO File Resource</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.eresource.CDOFileResource
   * @generated
   */
  public Adapter createCDOFileResourceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.eresource.CDOBinaryResource
   * <em>CDO Binary Resource</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.eresource.CDOBinaryResource
   * @generated
   */
  public Adapter createCDOBinaryResourceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.eresource.CDOTextResource
   * <em>CDO Text Resource</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.eresource.CDOTextResource
   * @generated
   */
  public Adapter createCDOTextResourceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // EresourceAdapterFactory
