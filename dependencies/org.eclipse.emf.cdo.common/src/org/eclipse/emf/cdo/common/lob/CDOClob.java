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
import java.io.Reader;

import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;
import org.eclipse.net4j.util.io.ExtendedDataInput;

/**
 * A identifiable character large object with streaming support.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @apiviz.landmark
 */
public final class CDOClob extends CDOLob<Reader>
{
  public CDOClob(Reader contents) throws IOException
  {
    super(contents, CDOLobStoreImpl.INSTANCE);
  }

  public CDOClob(Reader contents, CDOLobStore store) throws IOException
  {
    super(contents, store);
  }

  CDOClob(byte[] id, long size)
  {
    super(id, size);
  }

  CDOClob(ExtendedDataInput in) throws IOException
  {
    super(in);
  }

  @Override
  public Reader getContents() throws IOException
  {
    return getStore().getCharacter(this);
  }

  @Override
  protected CDOLobInfo put(Reader contents) throws IOException
  {
    return getStore().putCharacter(contents);
  }
}
