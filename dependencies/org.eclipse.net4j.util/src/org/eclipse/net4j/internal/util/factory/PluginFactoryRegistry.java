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
package org.eclipse.net4j.internal.util.factory;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.factory.FactoryDescriptor;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.registry.HashMapRegistry;

/**
 * @author Eike Stepper
 */
public class PluginFactoryRegistry extends HashMapRegistry<IFactoryKey, IFactory>
{
  public static final String NAMESPACE = OM.BUNDLE_ID;

  public static final String EXT_POINT = "factories"; //$NON-NLS-1$

  private Object extensionRegistryListener;

  public PluginFactoryRegistry()
  {
  }

  @Override
  public IFactory get(Object key)
  {
    IFactory factory = super.get(key);
    if (factory instanceof FactoryDescriptor)
    {
      FactoryDescriptor descriptor = (FactoryDescriptor)factory;
      factory = descriptor.createFactory();
    }

    return factory;
  }

  public void registerFactory(FactoryDescriptor factory)
  {
    put(factory.getKey(), factory);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    try
    {
      doActivateOSGi();
    }
    catch (Throwable t)
    {
      OM.LOG.warn(t);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      doDeactivateOSGi();
    }
    catch (Throwable t)
    {
      OM.LOG.warn(t);
    }

    clear();
    super.doDeactivate();
  }

  private void doActivateOSGi()
  {
    org.eclipse.core.runtime.IExtensionRegistry extensionRegistry = org.eclipse.core.runtime.Platform
        .getExtensionRegistry();
    if (extensionRegistry == null)
    {
      return;
    }

    org.eclipse.core.runtime.IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(
        NAMESPACE, EXT_POINT);
    for (org.eclipse.core.runtime.IConfigurationElement element : elements)
    {
      registerFactory(new FactoryDescriptor(element));
    }

    org.eclipse.core.runtime.IRegistryChangeListener listener = new org.eclipse.core.runtime.IRegistryChangeListener()
    {
      public void registryChanged(org.eclipse.core.runtime.IRegistryChangeEvent event)
      {
        org.eclipse.core.runtime.IExtensionDelta[] deltas = event.getExtensionDeltas(NAMESPACE, EXT_POINT);
        for (org.eclipse.core.runtime.IExtensionDelta delta : deltas)
        {
          // TODO Handle ExtensionDelta
          OM.LOG.warn("ExtensionDelta not handled: " + delta); //$NON-NLS-1$
        }
      }
    };

    extensionRegistry.addRegistryChangeListener(listener, NAMESPACE);
    extensionRegistryListener = listener;
  }

  private void doDeactivateOSGi()
  {
    org.eclipse.core.runtime.IExtensionRegistry extensionRegistry = org.eclipse.core.runtime.Platform
        .getExtensionRegistry();
    if (extensionRegistry == null)
    {
      return;
    }

    extensionRegistry
        .removeRegistryChangeListener((org.eclipse.core.runtime.IRegistryChangeListener)extensionRegistryListener);
  }
}
