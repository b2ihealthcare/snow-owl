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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.IOException;
import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Resource Folder</b></em>'.
 * 
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients. <!-- end-user-doc -->
 *           <p>
 *           The following features are implemented:
 *           <ul>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceFolderImpl#getNodes <em>Nodes</em>}</li>
 *           </ul>
 *           </p>
 * @generated
 */
public class CDOResourceFolderImpl extends CDOResourceNodeImpl implements CDOResourceFolder
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected CDOResourceFolderImpl()
  {
    super();
  }

  /**
   * @ADDED
   */
  public boolean isRoot()
  {
    return false;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EresourcePackage.Literals.CDO_RESOURCE_FOLDER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<CDOResourceNode> getNodes()
  {
    return (EList<CDOResourceNode>)eGet(EresourcePackage.Literals.CDO_RESOURCE_FOLDER__NODES, true);
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.0 <!-- end-user-doc -->
   * @generated NOT
   */
  public CDOResourceFolder addResourceFolder(String name)
  {
    return cdoView().toTransaction().createResourceFolder(getPath() + CDOURIUtil.SEGMENT_SEPARATOR + name);
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 4.0 <!-- end-user-doc -->
   * @generated NOT
   */
  public CDOResource addResource(String name)
  {
    return cdoView().toTransaction().createResource(getPath() + CDOURIUtil.SEGMENT_SEPARATOR + name);
  }

  /**
   * @ADDED
   */
  public void delete(Map<?, ?> options) throws IOException
  {
    if (!FSMUtil.isTransient(this))
    {
      if (getFolder() == null)
      {
        InternalCDOView view = cdoView();
        view.getRootResource().getContents().remove(this);
      }
      else
      {
        basicSetFolder(null, false);
      }
    }
  }
} // CDOResourceFolderImpl
