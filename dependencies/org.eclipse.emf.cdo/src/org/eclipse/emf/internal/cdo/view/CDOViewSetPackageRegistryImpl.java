/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;

/**
 * @author Simon McDuff
 */
public class CDOViewSetPackageRegistryImpl extends EPackageRegistryImpl
{
  private static final long serialVersionUID = 1L;

  private CDOViewSet viewSet;

  public CDOViewSetPackageRegistryImpl(CDOViewSet viewSet, EPackage.Registry delegateRegistry)
  {
    super(delegateRegistry);
    this.viewSet = viewSet;
  }

  @Override
  protected EPackage delegatedGetEPackage(String nsURI)
  {
    for (CDOView view : viewSet.getViews())
    {
      EPackage ePackage = view.getSession().getPackageRegistry().getEPackage(nsURI);
      if (ePackage != null)
      {
        return ePackage;
      }
    }

    return super.delegatedGetEPackage(nsURI);
  }

  @Override
  public Object put(String key, Object value)
  {
    super.put(key, value);
    for (CDOView view : viewSet.getViews())
    {
      view.getSession().getPackageRegistry().put(key, value);
    }

    return null;
  }

  @Override
  public synchronized Object get(Object key)
  {
    Object ePackage = super.get(key);
    if (ePackage == null)
    {
      for (CDOView view : viewSet.getViews())
      {
        ePackage = view.getSession().getPackageRegistry().get(key);
        if (ePackage != null)
        {
          return ePackage;
        }
      }
    }

    return ePackage;
  }
}
