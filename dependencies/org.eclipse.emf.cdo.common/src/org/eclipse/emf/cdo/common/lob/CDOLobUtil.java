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
package org.eclipse.emf.cdo.common.lob;

import java.io.IOException;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * Some useful helpers for dealing with {@link CDOLob large objects}.
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public final class CDOLobUtil
{
  private CDOLobUtil()
  {
  }

  public static CDOBlob readBlob(ExtendedDataInput in) throws IOException
  {
    return new CDOBlob(in);
  }

  public static CDOClob readClob(ExtendedDataInput in) throws IOException
  {
    return new CDOClob(in);
  }

  public static void write(ExtendedDataOutput out, CDOLob<?> lob) throws IOException
  {
    lob.write(out);
  }

  public static CDOBlob createBlob(byte[] id, long size)
  {
    return new CDOBlob(id, size);
  }

  public static CDOClob createClob(byte[] id, long size)
  {
    return new CDOClob(id, size);
  }

  public static void setStore(CDOLobStore store, CDOLob<?> lob) throws IOException
  {
    lob.setStore(store);
  }
}
