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
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDInteger;

/**
 * @author Eike Stepper
 */
public class CDOIDTempObjectImpl extends AbstractCDOIDInteger implements CDOIDTemp
{
  private static final long serialVersionUID = 1L;

  public CDOIDTempObjectImpl(int value)
  {
    super(value);
  }

  public Type getType()
  {
    return Type.TEMP_OBJECT;
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
    return true;
  }

  @Override
  public String toString()
  {
    return "oid" + getIntValue(); //$NON-NLS-1$
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    return new Integer(getIntValue()).compareTo(((CDOIDTempObjectImpl)o).getIntValue());
  }
}
