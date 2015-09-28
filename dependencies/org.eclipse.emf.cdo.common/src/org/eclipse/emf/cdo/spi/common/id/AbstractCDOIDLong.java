/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 */
package org.eclipse.emf.cdo.spi.common.id;

import java.io.IOException;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 */
public abstract class AbstractCDOIDLong extends AbstractCDOID
{
  public static final long NULL_VALUE = 0L;

  private static final long serialVersionUID = 1L;

  private long value;

  public AbstractCDOIDLong()
  {
  }

  public AbstractCDOIDLong(long value)
  {
    if (value == NULL_VALUE)
    {
      throw new IllegalArgumentException("value == NULL_VALUE"); //$NON-NLS-1$
    }

    this.value = value;
  }

  public long getLongValue()
  {
    return value;
  }

  public String toURIFragment()
  {
    return String.valueOf(value);
  }

  @Override
  public void read(String fragmentPart)
  {
    value = Long.valueOf(fragmentPart);
  }

  @Override
  public void read(ExtendedDataInput in) throws IOException
  {
    value = in.readLong();
  }

  @Override
  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeLong(value);
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
      AbstractCDOIDLong that = (AbstractCDOIDLong)obj;
      return value == that.value;
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
    return String.valueOf(value);
  }
}
