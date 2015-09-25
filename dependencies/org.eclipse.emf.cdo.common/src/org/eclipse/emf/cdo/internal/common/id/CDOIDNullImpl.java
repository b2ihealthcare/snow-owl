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
 *
 */
package org.eclipse.emf.cdo.internal.common.id;

import java.io.IOException;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOID;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * @author Eike Stepper
 */
public final class CDOIDNullImpl extends AbstractCDOID implements CDOIDTemp, CDOIDObject
{
  public static final CDOIDNullImpl INSTANCE = new CDOIDNullImpl();

  private static final long serialVersionUID = 1L;

  private CDOIDNullImpl()
  {
  }

  public Type getType()
  {
    return Type.NULL;
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
    return true;
  }

  public boolean isObject()
  {
    return false;
  }

  public boolean isTemporary()
  {
    return false;
  }

  public int getIntValue()
  {
    return 0;
  }

  public long getLongValue()
  {
    return 0L;
  }

  public String toURIFragment()
  {
    return "NULL"; //$NON-NLS-1$
  }

  @Override
  public void read(String fragmentPart)
  {
    // Do nothing
  }

  @Override
  public void read(ExtendedDataInput in) throws IOException
  {
    // Do nothing
  }

  @Override
  public void write(ExtendedDataOutput out) throws IOException
  {
    // Do nothing
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj == INSTANCE;
  }

  @Override
  public int hashCode()
  {
    return 0;
  }

  @Override
  public String toString()
  {
    return "NULL"; //$NON-NLS-1$
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    ((CDOIDNullImpl)o).getIntValue(); // Possibly throw ClassCastException
    return 0; // NULL == NULL
  }
}
