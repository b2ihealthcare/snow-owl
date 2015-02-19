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

import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Resource Leaf</b></em>'.
 *
 * @since 4.1
 * @noextend This class is not intended to be subclassed by clients. <!-- end-user-doc -->
 *           <p>
 *           </p>
 * @generated
 */
public abstract class CDOResourceLeafImpl extends CDOResourceNodeImpl implements CDOResourceLeaf
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected CDOResourceLeafImpl()
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
    return EresourcePackage.Literals.CDO_RESOURCE_LEAF;
  }

} // CDOResourceLeafImpl
