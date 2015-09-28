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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Stores and loads {@link CDOLob large objects}.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @apiviz.composedOf {@link CDOLob}
 */
public interface CDOLobStore
{
  public File getBinaryFile(byte[] id);

  public InputStream getBinary(CDOLobInfo info) throws IOException;

  public CDOLobInfo putBinary(InputStream contents) throws IOException;

  public File getCharacterFile(byte[] id);

  public Reader getCharacter(CDOLobInfo info) throws IOException;

  public CDOLobInfo putCharacter(Reader contents) throws IOException;

  /**
   * An abstract {@link CDOLobStore large object store} that delegates all method calls to a delegate.
   * 
   * @author Eike Stepper
   */
  public static abstract class Delegating implements CDOLobStore
  {
    public File getBinaryFile(byte[] id)
    {
      return getDelegate().getBinaryFile(id);
    }

    public InputStream getBinary(CDOLobInfo info) throws IOException
    {
      return getDelegate().getBinary(info);
    }

    public CDOLobInfo putBinary(InputStream contents) throws IOException
    {
      return getDelegate().putBinary(contents);
    }

    public File getCharacterFile(byte[] id)
    {
      return getDelegate().getCharacterFile(id);
    }

    public Reader getCharacter(CDOLobInfo info) throws IOException
    {
      return getDelegate().getCharacter(info);
    }

    public CDOLobInfo putCharacter(Reader contents) throws IOException
    {
      return getDelegate().putCharacter(contents);
    }

    protected abstract CDOLobStore getDelegate();
  }
}
