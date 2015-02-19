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
public class StreamWrapperChain extends DelegatingStreamWrapper
{
  private IStreamWrapper head;

  public StreamWrapperChain(IStreamWrapper head, IStreamWrapper delegate)
  {
    super(delegate);
    this.head = head;
  }

  public IStreamWrapper getHead()
  {
    return head;
  }

  @Override
  protected InputStream doWrapInputStream(InputStream in) throws IOException
  {
    return head.wrapInputStream(in);
  }

  @Override
  protected OutputStream doWrapOutputStream(OutputStream out) throws IOException
  {
    return head.wrapOutputStream(out);
  }

  @Override
  protected void doFinishInputStream(InputStream in) throws IOException
  {
    head.finishInputStream(in);
  }

  @Override
  protected void doFinishOutputStream(OutputStream out) throws IOException
  {
    head.finishOutputStream(out);
  }
}
