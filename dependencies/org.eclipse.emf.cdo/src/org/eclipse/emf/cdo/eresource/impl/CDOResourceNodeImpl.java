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

import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.text.MessageFormat;
import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Resource Node</b></em>'.
 * 
 * @noextend This interface is not intended to be extended by clients. <!-- end-user-doc -->
 *           <p>
 *           The following features are implemented:
 *           <ul>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl#getFolder <em>Folder</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl#getName <em>Name</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl#getPath <em>Path</em>}</li>
 *           </ul>
 *           </p>
 * @generated
 */
public abstract class CDOResourceNodeImpl extends CDOObjectImpl implements CDOResourceNode
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected CDOResourceNodeImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EresourcePackage.Literals.CDO_RESOURCE_NODE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CDOResourceFolder getFolder()
  {
    return (CDOResourceFolder)eGet(EresourcePackage.Literals.CDO_RESOURCE_NODE__FOLDER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setFolderGen(CDOResourceFolder newFolder)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE_NODE__FOLDER, newFolder);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public void setFolder(CDOResourceFolder newFolder)
  {
    basicSetFolder(newFolder, true);
  }

  /**
   * @ADDED
   */
  public void basicSetFolder(CDOResourceFolder newFolder, boolean checkDuplicates)
  {
    CDOResourceFolder oldFolder = getFolder();
    if (!ObjectUtil.equals(oldFolder, newFolder))
    {
      if (checkDuplicates)
      {
        String name = getName();
        if (name != null)
        {
          String newPath = (newFolder == null ? "" : newFolder.getPath()) + CDOURIUtil.SEGMENT_SEPARATOR + name; //$NON-NLS-1$
          checkDuplicates(newPath);
        }
      }

      setFolderGen(newFolder);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    return (String)eGet(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setNameGen(String newName)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public void setName(String newName)
  {
    basicSetName(newName, true);
  }

  /**
   * @ADDED
   */
  public void basicSetName(String newName, boolean checkDuplicates)
  {
    String oldName = getName();
    if (!ObjectUtil.equals(oldName, newName))
    {
      if (checkDuplicates)
      {
        CDOResourceFolder parent = getFolder();
        if (parent != null)
        {
          String newPath = parent.getPath() + CDOURIUtil.SEGMENT_SEPARATOR + getName();
          checkDuplicates(newPath);
        }
      }

      setNameGen(newName);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public String getPath()
  {
    if (isRoot())
    {
      return CDOResourceNode.ROOT_PATH;
    }

    CDOResourceFolder folder = getFolder();
    if (folder == null)
    {
      return CDOResourceNode.ROOT_PATH + getName();
    }

    return folder.getPath() + CDOResourceNode.ROOT_PATH + getName();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public void setPath(String newPath)
  {
    InternalCDOTransaction transaction = cdoView().toTransaction();
    if (newPath == null)
    {
      throw new CDOException(Messages.getString("CDOResourceNodeImpl.3")); //$NON-NLS-1$
    }

    String oldPath = getPath();
    if (!ObjectUtil.equals(oldPath, newPath))
    {
      // TODO check for duplicates
      List<String> names = CDOURIUtil.analyzePath(newPath);
      if (names.isEmpty())
      {
        throw new CDOException(Messages.getString("CDOResourceNodeImpl.4")); //$NON-NLS-1$
      }

      String newName = names.remove(names.size() - 1);
      CDOResourceFolder newFolder = transaction.getOrCreateResourceFolder(names);
      if (newFolder == null)
      {
        transaction.getRootResource().getContents().add(this);
      }

      basicSetFolder(newFolder, false);
      basicSetName(newName, false);
    }
  }

  /**
   * @ADDED
   */
  public URI getURI()
  {
    return CDOURIUtil.createResourceURI(cdoView(), getPath());
  }

  /**
   * @ADDED
   */
  private void checkDuplicates(String newPath)
  {
    try
    {
      cdoView().getResourceNodeID(newPath);
    }
    catch (Exception ex)
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOResourceNodeImpl.5"), newPath)); //$NON-NLS-1$
    }
  }
} // CDOResourceNodeImpl
