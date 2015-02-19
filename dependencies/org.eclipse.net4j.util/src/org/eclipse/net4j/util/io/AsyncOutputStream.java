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

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.WrappedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public abstract class AsyncOutputStream extends PipedOutputStream
{
  private CountDownLatch latch = new CountDownLatch(1);

  public AsyncOutputStream() throws IOException
  {
    final PipedInputStream in = new PipedInputStream(this);
    Thread thread = new Thread("AsyncOutputStream")
    {
      @Override
      public void run()
      {
        try
        {
          asyncWrite(in);
        }
        catch (IOException ex)
        {
          OM.LOG.error(ex);
          throw WrappedException.wrap(ex);
        }
        finally
        {
          latch.countDown();
        }
      }
    };

    thread.setDaemon(true);
    thread.start();
  }

  @Override
  public void close() throws IOException
  {
    super.close();

    try
    {
      latch.await();
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  protected abstract void asyncWrite(InputStream in) throws IOException;
}
