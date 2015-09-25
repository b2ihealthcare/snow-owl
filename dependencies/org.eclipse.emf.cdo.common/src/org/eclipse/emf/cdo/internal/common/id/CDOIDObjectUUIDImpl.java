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
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDByteArray;
import org.eclipse.emf.cdo.spi.common.id.InternalCDOIDObject;

/**
 * @author Martin Taal
 * @since 3.0
 */
public class CDOIDObjectUUIDImpl extends AbstractCDOIDByteArray implements InternalCDOIDObject
{
  private static final long serialVersionUID = 1L;

  public CDOIDObjectUUIDImpl()
  {
  }

  public CDOIDObjectUUIDImpl(byte[] value)
  {
    super(value);
  }

  public Type getType()
  {
    return Type.OBJECT;
  }

  public boolean isDangling()
  {
    return false;
  }

  public boolean isExternal()
  {
    return false;
  }

  public boolean isNull()
  {
    return false;
  }

  public boolean isObject()
  {
    return true;
  }

  public boolean isTemporary()
  {
    return false;
  }

  public CDOID.ObjectType getSubType()
  {
    return CDOID.ObjectType.UUID;
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    byte[] thisValue = getByteArrayValue();
    byte[] thatValue = ((CDOIDObjectUUIDImpl)o).getByteArrayValue();
    int minLength = Math.min(thisValue.length, thatValue.length);

    for (int i = 0; i < minLength; i++)
    {
      byte thisByte = thisValue[i];
      byte thatByte = thatValue[i];
      if (thisByte < thatByte)
      {
        return -1;
      }

      if (thisByte > thatByte)
      {
        return 1;
      }
    }

    if (thisValue.length < thatValue.length)
    {
      return -1;
    }

    if (thisValue.length > thatValue.length)
    {
      return 1;
    }

    return 0;
  }
}
