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
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDLong;
import org.eclipse.emf.cdo.spi.common.id.InternalCDOIDObject;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOIDObjectLongImpl extends AbstractCDOIDLong implements InternalCDOIDObject
{
  private static final long serialVersionUID = 1L;

  public CDOIDObjectLongImpl()
  {
  }

  public CDOIDObjectLongImpl(long value)
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

  /**
   * @since 3.0
   */
  public CDOID.ObjectType getSubType()
  {
    return CDOID.ObjectType.LONG;
  }

  @Override
  public String toString()
  {
    return "OID" + getLongValue(); //$NON-NLS-1$
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    return new Long(getLongValue()).compareTo(((CDOIDObjectLongImpl)o).getLongValue());
  }
}
