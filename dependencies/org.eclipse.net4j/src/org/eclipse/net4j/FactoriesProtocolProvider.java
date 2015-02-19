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
package org.eclipse.net4j;

import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

/**
 * Factory-based {@link IProtocolProvider protocol provider}.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public class FactoriesProtocolProvider implements IProtocolProvider
{
  private IRegistry<String, IFactory> registry;

  public FactoriesProtocolProvider()
  {
  }

  public FactoriesProtocolProvider(IRegistry<String, IFactory> registry)
  {
    setRegistry(registry);
  }

  public FactoriesProtocolProvider(IFactory factory)
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

  public IProtocol<?> getProtocol(String type)
  {
    IFactory factory = registry.get(type);
    if (factory != null)
    {
      return (IProtocol<?>)factory.create(null);
    }

    return null;
  }
}
