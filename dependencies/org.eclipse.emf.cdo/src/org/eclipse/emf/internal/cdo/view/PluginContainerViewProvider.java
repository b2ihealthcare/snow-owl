/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.ManagedContainerViewProvider;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Provides <code>CDOView</code> from <code>CDOSession</code> registered in IPluginContainer
 * 
 * @author Victor Roldan Betancort
 */
public class PluginContainerViewProvider extends ManagedContainerViewProvider
{
  public static final CDOViewProvider INSTANCE = new PluginContainerViewProvider();

  private final static String REGEX = "cdo:.*"; //$NON-NLS-1$

  private final static int PRIORITY = DEFAULT_PRIORITY - 200;

  public PluginContainerViewProvider()
  {
    super(IPluginContainer.INSTANCE, REGEX, PRIORITY);
  }

  public CDOView getView(URI uri, ResourceSet resourceSet)
  {
    IManagedContainer container = getContainer();
    if (container == null)
    {
      return null;
    }

    String repoUUID = CDOURIUtil.extractRepositoryUUID(uri);
    for (Object element : container.getElements(CDOSessionFactory.PRODUCT_GROUP))
    {
      CDOSession session = (CDOSession)element;
      String uuid = session.getRepositoryInfo().getUUID();
      if (repoUUID.equals(uuid))
      {
        CDOView view = openView(session, resourceSet);
        if (view != null)
        {
          return view;
        }
      }
    }

    return null;
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected CDOView openView(CDOSession session, ResourceSet resourceSet)
  {
    return session.openTransaction(resourceSet);
  }
}
