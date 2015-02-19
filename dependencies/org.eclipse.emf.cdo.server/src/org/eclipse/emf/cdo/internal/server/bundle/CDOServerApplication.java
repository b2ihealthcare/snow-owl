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
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.emf.cdo.internal.server.messages.Messages;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.server.IAppExtension;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiApplication;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOServerApplication extends OSGiApplication
{
  public static final String ID = OM.BUNDLE_ID + ".app"; //$NON-NLS-1$

  public static final String PROP_BROWSER_PORT = "org.eclipse.emf.cdo.server.browser.port"; //$NON-NLS-1$

  private IRepository[] repositories;

  private List<IAppExtension> extensions = new ArrayList<IAppExtension>();

  public CDOServerApplication()
  {
    super(ID);
  }

  @Override
  protected void doStart() throws Exception
  {
    super.doStart();
    IManagedContainer container = getContainer();

    OM.LOG.info(Messages.getString("CDOServerApplication.1")); //$NON-NLS-1$
    File configFile = OMPlatform.INSTANCE.getConfigFile("cdo-server.xml"); //$NON-NLS-1$
    if (configFile != null && configFile.exists())
    {
      RepositoryConfigurator repositoryConfigurator = new RepositoryConfigurator(container);
      repositories = repositoryConfigurator.configure(configFile);
      if (repositories == null || repositories.length == 0)
      {
        OM.LOG.warn(Messages.getString("CDOServerApplication.3") + " " + configFile.getAbsolutePath()); //$NON-NLS-1$
      }

      String port = OMPlatform.INSTANCE.getProperty(PROP_BROWSER_PORT);
      if (port != null)
      {
        container.getElement("org.eclipse.emf.cdo.server.browsers", "default", port); //$NON-NLS-1$ //$NON-NLS-2$
      }

      startExtensions(configFile);
    }
    else
    {
      OM.LOG.warn(Messages.getString("CDOServerApplication.5") + " " + configFile.getAbsolutePath()); //$NON-NLS-1$
    }

    OM.LOG.info(Messages.getString("CDOServerApplication.6")); //$NON-NLS-1$
  }

  @Override
  protected void doStop() throws Exception
  {
    OM.LOG.info(Messages.getString("CDOServerApplication.7")); //$NON-NLS-1$
    for (IAppExtension extension : extensions)
    {
      try
      {
        extension.stop();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    if (repositories != null)
    {
      for (IRepository repository : repositories)
      {
        LifecycleUtil.deactivate(repository);
      }
    }

    OM.LOG.info(Messages.getString("CDOServerApplication.8")); //$NON-NLS-1$
    super.doStop();
  }

  private void startExtensions(File configFile)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, IAppExtension.EXT_POINT);
    for (final IConfigurationElement element : elements)
    {
      if ("appExtension".equals(element.getName())) //$NON-NLS-1$
      {
        try
        {
          IAppExtension extension = (IAppExtension)element.createExecutableExtension("class"); //$NON-NLS-1$
          extension.start(configFile);
          extensions.add(extension);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  public static IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
