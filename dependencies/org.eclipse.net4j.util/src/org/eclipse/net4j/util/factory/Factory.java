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

import java.text.MessageFormat;

/**
 * A default implementation of a {@link IFactory factory}.
 * 
 * @author Eike Stepper
 */
public abstract class Factory implements IFactory
{
  private FactoryKey key;

  public Factory(FactoryKey key)
  {
    this.key = key;
  }

  public Factory(String productGroup, String type)
  {
    this(new FactoryKey(productGroup, type));
  }

  public FactoryKey getKey()
  {
    return key;
  }

  public String getProductGroup()
  {
    return key.getProductGroup();
  }

  public String getType()
  {
    return key.getType();
  }

  public String getDescriptionFor(Object product)
  {
    return null;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Factory[{0}, {1}]", getProductGroup(), getType()); //$NON-NLS-1$
  }
}
