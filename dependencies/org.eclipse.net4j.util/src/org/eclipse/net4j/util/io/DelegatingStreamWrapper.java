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
package org.eclipse.net4j.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public abstract class DelegatingStreamWrapper implements IStreamWrapper
{
  private IStreamWrapper delegate;

  public DelegatingStreamWrapper(IStreamWrapper delegate)
  {
    this.delegate = delegate;
  }

  public IStreamWrapper getDelegate()
  {
    return delegate;
  }

  public InputStream wrapInputStream(InputStream in) throws IOException
  {
    return doWrapInputStream(delegate.wrapInputStream(in));
  }

  public OutputStream wrapOutputStream(OutputStream out) throws IOException
  {
    return doWrapOutputStream(delegate.wrapOutputStream(out));
  }

  public void finishInputStream(InputStream in) throws IOException
  {
    delegate.finishInputStream(in);
    doFinishInputStream(in);
  }

  public void finishOutputStream(OutputStream out) throws IOException
  {
    delegate.finishOutputStream(out);
    doFinishOutputStream(out);
  }

  protected abstract InputStream doWrapInputStream(InputStream in) throws IOException;

  protected abstract OutputStream doWrapOutputStream(OutputStream out) throws IOException;

  protected abstract void doFinishInputStream(InputStream in) throws IOException;

  protected abstract void doFinishOutputStream(OutputStream out) throws IOException;
}
