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
package org.eclipse.net4j.util.factory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;

/**
 * A {@link IFactory factory} that delegates to an {@link IExtensionRegistry extension registry} contribution.
 * <p>
 * Example contribution:
 * 
 * <pre>
 *    &lt;extension
 *          point="org.eclipse.net4j.util.factories">
 *       &lt;factory
 *             class="org.eclipse.net4j.util.concurrent.TimerLifecycle$DaemonFactory"
 *             productGroup="org.eclipse.net4j.util.timers"
 *             type="daemon"/>
 *    &lt;/extension>
 * </pre>
 * 
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 * @apiviz.exclude
 */
public class FactoryDescriptor extends Factory
{
  private static final String ATTR_PRODUCT_GROUP = "productGroup"; //$NON-NLS-1$

  private static final String ATTR_TYPE = "type"; //$NON-NLS-1$

  private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

  private IConfigurationElement configurationElement;

  public FactoryDescriptor(IConfigurationElement configurationElement)
  {
    super(createFactoryKey(configurationElement));
    this.configurationElement = configurationElement;
  }

  public IConfigurationElement getConfigurationElement()
  {
    return configurationElement;
  }

  public IFactory createFactory()
  {
    try
    {
      return (IFactory)configurationElement.createExecutableExtension(ATTR_CLASS);
    }
    catch (CoreException ex)
    {
      throw new FactoryCreationException(ex);
    }
  }

  public Object create(String description)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getDescriptionFor(Object product)
  {
    throw new UnsupportedOperationException();
  }

  private static FactoryKey createFactoryKey(IConfigurationElement element)
  {
    String productGroup = element.getAttribute(ATTR_PRODUCT_GROUP);
    String type = element.getAttribute(ATTR_TYPE);
    return new FactoryKey(productGroup, type);
  }
}
