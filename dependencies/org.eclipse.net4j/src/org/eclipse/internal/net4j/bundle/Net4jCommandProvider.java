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
package org.eclipse.internal.net4j.bundle;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.spi.net4j.ConnectorFactory;

import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public class Net4jCommandProvider implements CommandProvider
{
  private static final String NEW_LINE = "\r\n"; //$NON-NLS-1$

  private static final String INDENT = "   "; //$NON-NLS-1$

  public Net4jCommandProvider(BundleContext bundleContext)
  {
    bundleContext.registerService(CommandProvider.class.getName(), this, null);
  }

  public String getHelp()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("---Net4j commands---" + NEW_LINE);
    buffer.append(INDENT + "elements - list all managed elements" + NEW_LINE);
    buffer.append(INDENT + "acceptors - list all active acceptors, their connectors and channels" + NEW_LINE);
    buffer.append(INDENT + "connectors - list all active connectors and their channels" + NEW_LINE);
    return buffer.toString();
  }

  public Object _elements(CommandInterpreter interpreter)
  {
    try
    {
      for (String productGroup : getContainer().getProductGroups())
      {
        interpreter.println(productGroup);
        printFactoryTypes(interpreter, productGroup, INDENT);
      }
    }
    catch (Exception ex)
    {
      interpreter.printStackTrace(ex);
    }

    return null;
  }

  public Object _acceptors(CommandInterpreter interpreter)
  {
    try
    {
      printFactoryTypes(interpreter, AcceptorFactory.PRODUCT_GROUP, "");
    }
    catch (Exception ex)
    {
      interpreter.printStackTrace(ex);
    }

    return null;
  }

  public Object _connectors(CommandInterpreter interpreter)
  {
    try
    {
      printFactoryTypes(interpreter, ConnectorFactory.PRODUCT_GROUP, "");
    }
    catch (Exception ex)
    {
      interpreter.printStackTrace(ex);
    }

    return null;
  }

  protected IPluginContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  private void printFactoryTypes(CommandInterpreter interpreter, String productGroup, String prefix)
  {
    IPluginContainer container = getContainer();
    for (String factoryType : container.getFactoryTypes(productGroup))
    {
      interpreter.println(prefix + factoryType);
      printElements(interpreter, container.getElements(productGroup, factoryType), prefix + INDENT);
    }
  }

  private void printElements(CommandInterpreter interpreter, Object[] elements, String prefix)
  {
    for (Object element : elements)
    {
      interpreter.println(prefix + element);
      if (element instanceof IContainer)
      {
        IContainer<?> container = (IContainer<?>)element;
        printElements(interpreter, container.getElements(), prefix + INDENT);
      }
    }
  }
}
