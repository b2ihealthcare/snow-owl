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
package org.eclipse.emf.cdo.spi.common.id;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * @author Eike Stepper
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 */
public abstract class AbstractCDOIDByteArray extends AbstractCDOID
{
  public static final String NULL_VALUE = null;

  private static final long serialVersionUID = 1L;

  private byte[] value;

  public AbstractCDOIDByteArray()
  {
  }

  public AbstractCDOIDByteArray(byte[] value)
  {
    CheckUtil.checkArg(value != null && value.length == 16, "Illegal UUID value");
    this.value = value;
  }

  public byte[] getByteArrayValue()
  {
    return value;
  }

  public String toURIFragment()
  {
    return CDOIDUtil.encodeUUID(value);
  }

  @Override
  public void read(String fragmentPart)
  {
    value = CDOIDUtil.decodeUUID(fragmentPart);
  }

  @Override
  public void read(ExtendedDataInput in) throws IOException
  {
    value = in.readByteArray();
  }

  @Override
  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeByteArray(value);
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
      AbstractCDOIDByteArray that = (AbstractCDOIDByteArray)obj;
      return Arrays.equals(value, that.value);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return getClass().hashCode() ^ Arrays.hashCode(value);
  }
}
