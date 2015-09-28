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

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class ExpectedFileReader extends FileReader
{
  private long timeout = IOUtil.DEFAULT_TIMEOUT;

  private File file;

  private long expectedSize;

  private long pos;

  public ExpectedFileReader(File file, long expectedSize) throws FileNotFoundException
  {
    super(file);
    this.file = file;
    this.expectedSize = expectedSize;
  }

  public long getTimeout()
  {
    return timeout;
  }

  public void setTimeout(long timeout)
  {
    this.timeout = timeout;
  }

  @Override
  public long skip(long n) throws IOException
  {
    waitForInput(n);
    return super.skip(n);
  }

  @Override
  public int read() throws IOException
  {
    waitForInput(1L);
    return super.read();
  }

  @Override
  public int read(char[] cbuf, int offset, int length) throws IOException
  {
    waitForInput(length);
    return super.read(cbuf, offset, length);
  }

  @Override
  public int read(CharBuffer target) throws IOException
  {
    waitForInput(target.remaining());
    return super.read(target);
  }

  @Override
  public int read(char[] cbuf) throws IOException
  {
    waitForInput(cbuf.length);
    return super.read(cbuf);
  }

  private void waitForInput(long n) throws IOException
  {
    synchronized (this)
    {
      n = Math.min(n, expectedSize - pos);
      long restSize = file.length() - pos;
      long endTime = 0;

      while (restSize < n)
      {
        long restTime;
        if (endTime == 0)
        {
          endTime = System.currentTimeMillis() + timeout;
          restTime = timeout;
        }
        else
        {
          restTime = endTime - System.currentTimeMillis();
        }

        if (restTime <= 0)
        {
          throw new TimeoutRuntimeException("Timeout while reading from " + file.getAbsolutePath());
        }

        try
        {
          wait(Math.max(100L, restTime));
        }
        catch (InterruptedException ex)
        {
          throw WrappedException.wrap(ex);
        }

        restSize = file.length() - pos;
      }

      pos += n;
    }
  }
}
