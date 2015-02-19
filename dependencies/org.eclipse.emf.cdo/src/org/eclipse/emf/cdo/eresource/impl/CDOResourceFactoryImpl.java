/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 213402
 *    Simon McDuff - bug 246705
 */
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * Creates default {@link CDOResource} instances.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 */
public class CDOResourceFactoryImpl implements CDOResourceFactory
{
  private static final String RESOURCE_SET_CLASS_NAME = ResourceSetImpl.class.getName();

  /**
   * @since 4.0
   */
  public static final CDOResourceFactory INSTANCE = new CDOResourceFactoryImpl();

  public CDOResourceFactoryImpl()
  {
  }

  public Resource createResource(URI uri)
  {
    CDOResourceImpl resource = createCDOResource(uri);
    resource.setExisting(isGetResource());
    return resource;
  }

  /**
   * @since 4.0
   */
  protected CDOResourceImpl createCDOResource(URI uri)
  {
    return new CDOResourceImpl(uri);
  }

  /**
   * TODO Add TCs to ensure that Ecore internally doesn't change the way the stack is used!!!
   * 
   * @since 3.0
   */
  protected boolean isGetResource()
  {
    boolean inResourceSet = false;
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (int i = 3; i < elements.length; i++)
    {
      StackTraceElement element = elements[i];
      if (RESOURCE_SET_CLASS_NAME.equals(element.getClassName()))
      {
        inResourceSet = true;
      }
      else
      {
        if (inResourceSet)
        {
          break;
        }
      }

      if (inResourceSet && "getResource".equals(element.getMethodName())) //$NON-NLS-1$
      {
        return true;
      }
    }

    return false;
  }
}
