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

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * A default implementation of a {@link IFactoryKey factory key}.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public final class FactoryKey implements IFactoryKey, Serializable, Comparable<FactoryKey>
{
  private static final long serialVersionUID = 1L;

  private String productGroup;

  private String type;

  public FactoryKey(String productGroup, String type)
  {
    this.productGroup = productGroup;
    this.type = type;
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  public void setProductGroup(String productGroup)
  {
    this.productGroup = productGroup;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof FactoryKey)
    {
      FactoryKey key = (FactoryKey)obj;
      return ObjectUtil.equals(productGroup, key.productGroup) && ObjectUtil.equals(type, key.type);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(productGroup) ^ ObjectUtil.hashCode(type);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[{1}]", productGroup, type); //$NON-NLS-1$
  }

  public int compareTo(FactoryKey key)
  {
    int result = StringUtil.compare(productGroup, key.productGroup);
    if (result == 0)
    {
      result = StringUtil.compare(type, key.type);
    }

    return result;
  }
}
