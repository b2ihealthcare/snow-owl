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
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDString;
import org.eclipse.emf.cdo.spi.common.id.InternalCDOIDObject;

/**
 * @author Martin Taal
 * @since 3.0
 */
public class CDOIDObjectStringImpl extends AbstractCDOIDString implements InternalCDOIDObject
{
  private static final long serialVersionUID = 1L;

  public CDOIDObjectStringImpl()
  {
  }

  public CDOIDObjectStringImpl(String value)
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
    return CDOID.ObjectType.STRING;
  }

  @Override
  public String toString()
  {
    return "OID" + getStringValue(); //$NON-NLS-1$
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    return getStringValue().compareTo(((CDOIDObjectStringImpl)o).getStringValue());
  }
}
