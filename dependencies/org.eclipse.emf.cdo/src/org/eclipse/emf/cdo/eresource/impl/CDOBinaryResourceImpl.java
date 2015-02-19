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

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.emf.ecore.EClass;

import java.io.InputStream;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Binary Resource</b></em>'.
 *
 * @since 4.1
 * @noextend This class is not intended to be subclassed by clients. <!-- end-user-doc -->
 *           <p>
 *           The following features are implemented:
 *           <ul>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOBinaryResourceImpl#getContents <em>Contents</em>}</li>
 *           </ul>
 *           </p>
 * @generated
 */
public class CDOBinaryResourceImpl extends CDOFileResourceImpl<InputStream> implements CDOBinaryResource
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected CDOBinaryResourceImpl()
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
    return EresourcePackage.Literals.CDO_BINARY_RESOURCE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public CDOBlob getContents()
  {
    return (CDOBlob)eGet(EresourcePackage.Literals.CDO_BINARY_RESOURCE__CONTENTS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setContents(CDOBlob newContents)
  {
    eSet(EresourcePackage.Literals.CDO_BINARY_RESOURCE__CONTENTS, newContents);
  }

} // CDOBinaryResourceImpl
