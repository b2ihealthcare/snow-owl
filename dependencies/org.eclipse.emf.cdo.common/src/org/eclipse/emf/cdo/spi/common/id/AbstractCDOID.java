/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.common.id;

import java.io.IOException;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 */
public abstract class AbstractCDOID implements CDOID
{
  private static final long serialVersionUID = 1L;

  public AbstractCDOID()
  {
  }

  @Override
  public String toString()
  {
    return toURIFragment();
  }

  public final int compareTo(CDOID o)
  {
    try
    {
      return doCompareTo(o);
    }
    catch (ClassCastException ex)
    {
      return getType().compareTo(o.getType());
    }
  }

  protected abstract int doCompareTo(CDOID o) throws ClassCastException;

  /**
   * <b>Note:</b> {@link CDOID#toURIFragment()} and {@link AbstractCDOID#read(String)} need to match.
   */
  public abstract void read(String fragmentPart);

  /**
   * TODO: Change the parameter to CDODataInput to prevent casting in IDs with classifier.
   */
  public abstract void read(ExtendedDataInput in) throws IOException;

  /**
   * TODO: Change the parameter to CDODataInput to prevent casting in IDs with classifier.
   */
  public abstract void write(ExtendedDataOutput out) throws IOException;
}
