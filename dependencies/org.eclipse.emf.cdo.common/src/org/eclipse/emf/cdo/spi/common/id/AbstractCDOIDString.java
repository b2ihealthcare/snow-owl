/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.spi.common.id;

import java.io.IOException;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * @author Martin Taal
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 */
public abstract class AbstractCDOIDString extends AbstractCDOID
{
  /**
   * @since 4.0
   */
  public static final String NULL_VALUE = null;

  private static final long serialVersionUID = 1L;

  private String value;

  public AbstractCDOIDString()
  {
  }

  public AbstractCDOIDString(String value)
  {
    this.value = value;
  }

  public String getStringValue()
  {
    return value;
  }

  public String toURIFragment()
  {
    return value;
  }

  @Override
  public void read(String fragmentPart)
  {
    value = fragmentPart;
  }

  @Override
  public void read(ExtendedDataInput in) throws IOException
  {
    value = in.readString();
  }

  @Override
  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeString(value);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj != null && obj.getClass() == getClass())
    {
      AbstractCDOIDString that = (AbstractCDOIDString)obj;
      return value.equals(that.value);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return getClass().hashCode() ^ ObjectUtil.hashCode(value);
  }

  @Override
  public String toString()
  {
    return value;
  }
}
