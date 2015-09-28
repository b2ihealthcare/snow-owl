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
package org.eclipse.net4j.buffer;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOTimeoutException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A {@link IBufferHandler buffer handler} that defragments the passed {@link IBuffer buffers} into a continuous byte
 * sequence which is exposed as an {@link InputStream input stream}.
 * 
 * @author Eike Stepper
 */
public class BufferInputStream extends InputStream implements IBufferHandler
{
  public static final long NO_TIMEOUT = -1;

  public static final long DEFAULT_MILLIS_BEFORE_TIMEOUT = NO_TIMEOUT;

  public static final long DEFAULT_MILLIS_INTERRUPT_CHECK = 100;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER_STREAM, BufferInputStream.class);

  private final boolean tracerEnabled;

  private BlockingQueue<IBuffer> buffers = new LinkedBlockingQueue<IBuffer>();

  private IBuffer currentBuffer;

  private boolean eos;

  private RemoteException exception;

  private long stopTimeMillis;

  public BufferInputStream()
  {
    tracerEnabled = TRACER.isEnabled();
  }

  public long getMillisBeforeTimeout()
  {
    return DEFAULT_MILLIS_BEFORE_TIMEOUT;
  }

  public long getMillisInterruptCheck()
  {
    return DEFAULT_MILLIS_INTERRUPT_CHECK;
  }

  /**
   * @since 2.0
   */
  public void restartTimeout()
  {
    synchronized (this)
    {
      stopTimeMillis = System.currentTimeMillis() + getMillisBeforeTimeout();
    }
  }

  /**
   * @since 2.0
   */
  public RuntimeException getException()
  {
    return exception;
  }

  /**
   * @since 4.0
   */
  public void setException(RemoteException exception)
  {
    this.exception = exception;
  }

  public void handleBuffer(IBuffer buffer)
  {
    buffers.add(buffer);
  }

  @SuppressWarnings("deprecation")
  @Override
  public int read() throws IOException
  {
    if (currentBuffer == null)
    {
      if (eos)
      {
        // End of stream
        return IOUtil.EOF;
      }

      if (!ensureBuffer())
      {
        // Timeout or interrupt
        return IOUtil.EOF;
      }
    }

    ByteBuffer byteBuffer = currentBuffer.getByteBuffer();
    if (!byteBuffer.hasRemaining())
    {
      // End of stream
      return IOUtil.EOF;
    }

    final int result = byteBuffer.get() & 0xFF;
    if (tracerEnabled)
    {
      TRACER.trace("<-- " + HexUtil.formatByte(result) //$NON-NLS-1$
          + (result >= 32 ? " " + Character.toString((char)result) : "")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (!byteBuffer.hasRemaining())
    {
      currentBuffer.release();
      currentBuffer = null;
    }

    return result;
  }

  @Override
  public void close() throws IOException
  {
    buffers = null;
    currentBuffer = null;
    super.close();
  }

  @Override
  public String toString()
  {
    return "BufferInputStream"; //$NON-NLS-1$
  }

  protected boolean ensureBuffer() throws IOException
  {
    final long check = getMillisInterruptCheck();

    try
    {
      if (getMillisBeforeTimeout() == NO_TIMEOUT)
      {
        while (currentBuffer == null)
        {
          throwRemoteExceptionIfExists();

          if (buffers == null)
          {
            // Stream has been closed - shutting down
            return false;
          }

          currentBuffer = buffers.poll(check, TimeUnit.MILLISECONDS);
        }
      }
      else
      {
        restartTimeout();
        while (currentBuffer == null)
        {
          throwRemoteExceptionIfExists();

          if (buffers == null)
          {
            // Stream has been closed - shutting down
            return false;
          }

          long remaining;
          synchronized (this)
          {
            remaining = stopTimeMillis;
          }

          remaining -= System.currentTimeMillis();
          if (remaining <= 0)
          {
            // Throw an exception so that caller can distinguish between end-of-stream and a timeout
            throw new IOTimeoutException();
          }

          currentBuffer = buffers.poll(Math.min(remaining, check), TimeUnit.MILLISECONDS);
        }
      }
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }

    eos = currentBuffer.isEOS();
    return true;
  }

  private void throwRemoteExceptionIfExists()
  {
    if (exception != null)
    {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      exception.setLocalStacktrace(stackTrace);
      throw exception;
    }
  }
}
