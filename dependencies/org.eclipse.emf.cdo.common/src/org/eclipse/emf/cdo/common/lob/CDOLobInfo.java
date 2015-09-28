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

import java.text.MessageFormat;

import org.eclipse.net4j.util.HexUtil;

/**
 * Encapsulates {@link #getID() ID} and {@link #getSize() size} of a {@link CDOLob large object}.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CDOLobInfo
{
  byte[] id;

  long size;

  CDOLobInfo()
  {
  }

  public CDOLobInfo(byte[] id, long size)
  {
    this.id = id;
    this.size = size;
  }

  /**
   * The identifier of this large object. A SHA-1 digest of the content of this large object.
   */
  public final byte[] getID()
  {
    return id;
  }

  public final long getSize()
  {
    return size;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[id={1}, size={2}]", getClass().getSimpleName(), HexUtil.bytesToHex(id), size);
  }
}
