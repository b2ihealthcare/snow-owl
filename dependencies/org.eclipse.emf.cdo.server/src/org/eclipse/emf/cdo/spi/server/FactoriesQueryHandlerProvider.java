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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;

import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class FactoriesQueryHandlerProvider implements IQueryHandlerProvider
{
  private IRegistry<String, IFactory> registry;

  public FactoriesQueryHandlerProvider()
  {
  }

  public FactoriesQueryHandlerProvider(IRegistry<String, IFactory> registry)
  {
    setRegistry(registry);
  }

  public FactoriesQueryHandlerProvider(IFactory factory)
  {
    addFactory(factory);
  }

  public IRegistry<String, IFactory> getRegistry()
  {
    if (registry == null)
    {
      registry = new HashMapRegistry<String, IFactory>();
    }

    return registry;
  }

  public void setRegistry(IRegistry<String, IFactory> registry)
  {
    this.registry = registry;
  }

  public void addFactory(IFactory factory)
  {
    getRegistry().put(factory.getKey().getType(), factory);
  }

  /**
   * @since 3.0
   */
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    IFactory factory = registry.get(info.getQueryLanguage());
    if (factory != null)
    {
      return (IQueryHandler)factory.create(null);
    }

    return null;
  }
}
