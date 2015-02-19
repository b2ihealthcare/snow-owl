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

/**
 * @author Eike Stepper
 * @since 3.2
 */
public class LimitedInputStream extends InputStream
{
  private InputStream in;

  private long remaining;

  private long remainingAtMark = 0;

  public LimitedInputStream(InputStream in, long length)
  {
    this.in = in;
    remaining = length;
  }

  @Override
  public int read() throws IOException
  {
    if ((remaining -= 1) < 0)
    {
      return -1;
    }

    return in.read();
  }

  @Override
  public int read(byte[] cbuf, int off, int len) throws IOException
  {
    if (remaining <= 0)
    {
      return -1;
    }

    if (len > remaining)
    {
      len = (int)remaining;
    }

    len = in.read(cbuf, off, len);
    if (len > 0)
    {
      remaining -= len;
    }
    else
    {
      remaining -= remaining;
    }

    return len;
  }

  @Override
  public long skip(long n) throws IOException
  {
    if (n > remaining)
    {
      n = remaining;
    }

    remaining -= n = in.skip(n);
    return n;
  }

  @Override
  public boolean markSupported()
  {
    return in.markSupported();
  }

  @Override
  public synchronized void mark(int readlimit)
  {
    if (markSupported())
    {
      in.mark(readlimit);
      remainingAtMark = remaining;
    }
  }

  @Override
  public synchronized void reset() throws IOException
  {
    in.reset();
    remaining = remainingAtMark;
  }

  @Override
  public void close() throws IOException
  {
    remaining = 0;
    in.close();
  }
}
