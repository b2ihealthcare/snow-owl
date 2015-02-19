/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim -  Bug 262875: java.nio.BufferUnderFlowException https://bugs.eclipse.org/bugs/show_bug.cgi?id=262875
 */
package org.eclipse.net4j.buffer;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.IErrorHandler;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * An {@link OutputStream output stream} that fragments the written byte sequence into fixed-sized {@link IBuffer
 * buffers} and passes them to configured {@link IBufferHandler buffer handler}.
 * 
 * @author Eike Stepper
 */
public class BufferOutputStream extends OutputStream
{
  public static final boolean DEFAULT_PROPAGATE_CLOSE = false;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER_STREAM, BufferOutputStream.class);

  private final boolean tracerEnabled;

  private IBufferProvider bufferProvider;

  private IBufferHandler bufferHandler;

  private IBuffer currentBuffer;

  private short channelID;

  private Throwable error;

  @ExcludeFromDump
  private transient IErrorHandler errorHandler = new IErrorHandler()
  {
    public void handleError(Throwable t)
    {
      setError(t);
    }
  };

  public BufferOutputStream(IBufferHandler bufferHandler, IBufferProvider bufferProvider, short channelID)
  {
    if (bufferHandler == null)
    {
      throw new IllegalArgumentException("bufferHandler == null"); //$NON-NLS-1$
    }

    if (bufferProvider == null)
    {
      throw new IllegalArgumentException("bufferProvider == null"); //$NON-NLS-1$
    }

    this.bufferHandler = bufferHandler;
    this.bufferProvider = bufferProvider;
    this.channelID = channelID;
    tracerEnabled = TRACER.isEnabled();
  }

  public BufferOutputStream(IBufferHandler bufferHandler, short channelID)
  {
    this(bufferHandler, extractBufferProvider(bufferHandler), channelID);
  }

  /**
   * @since 2.0
   */
  public Throwable getError()
  {
    return error;
  }

  /**
   * @since 2.0
   */
  public void setError(Throwable error)
  {
    this.error = error;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void write(int b) throws IOException
  {
    throwExceptionOnError();
    flushIfFilled();
    ensureBufferPrivate();

    // If this was called with a primitive byte with a negative value,
    // the implicit conversion prepended 24 leading 1's. We'll undo those.
    b = b & 0xFF;

    if (tracerEnabled)
    {
      TRACER.trace("--> " + HexUtil.formatByte(b) //$NON-NLS-1$
          + (b >= 32 ? " " + Character.toString((char)b) : "")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    ByteBuffer buffer = currentBuffer.getByteBuffer();
    buffer.put((byte)b);
  }

  /**
   * Flushes the current buffer, it's handled over to the buffer handler.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see #currentBuffer
   * @see IBufferHandler#handleBuffer(IBuffer)
   */
  @Override
  public void flush() throws IOException
  {
    flushPrivate();
  }

  /**
   * Flushes the current buffer if it has no remaining space.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void flushIfFilled() throws IOException
  {
    if (currentBuffer != null && !currentBuffer.getByteBuffer().hasRemaining())
    {
      flushPrivate();
    }
  }

  private void flushPrivate()
  {
    if (currentBuffer != null)
    {
      bufferHandler.handleBuffer(currentBuffer);
      currentBuffer = null;
    }
  }

  public void flushWithEOS() throws IOException
  {
    throwExceptionOnError();
    ensureBufferPrivate();
    currentBuffer.setEOS(true);
    flushPrivate();
  }

  @Override
  public void close() throws IOException
  {
    try
    {
      if (isPropagateClose())
      {
        LifecycleUtil.deactivate(bufferHandler);
      }
    }
    finally
    {
      bufferHandler = null;
      bufferProvider = null;
      currentBuffer = null;
      super.close();
    }
  }

  @Override
  public String toString()
  {
    return "BufferOutputStream"; //$NON-NLS-1$
  }

  /**
   * Ensures that this BufferOutputStream has a buffer. If the current buffer was flushed a new one is fetched from the
   * buffer provider.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see #flush()
   * @see IBufferProvider#provideBuffer()
   */
  protected void ensureBuffer() throws IOException
  {
    ensureBufferPrivate();
  }

  private void ensureBufferPrivate()
  {
    if (currentBuffer == null)
    {
      currentBuffer = bufferProvider.provideBuffer();
      currentBuffer.setErrorHandler(errorHandler);
      currentBuffer.startPutting(channelID);
    }
  }

  /**
   * Throws an exception if there's an error.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see #error
   */
  private void throwExceptionOnError() throws IOException
  {
    if (error != null)
    {
      if (error instanceof IOException)
      {
        throw (IOException)error;
      }

      if (error instanceof RuntimeException)
      {
        throw (RuntimeException)error;
      }

      throw new IORuntimeException(error);
    }
  }

  protected boolean isPropagateClose()
  {
    return DEFAULT_PROPAGATE_CLOSE;
  }

  private static IBufferProvider extractBufferProvider(IBufferHandler bufferHandler)
  {
    if (bufferHandler instanceof IBufferProvider)
    {
      return (IBufferProvider)bufferHandler;
    }

    throw new IllegalArgumentException("Buffer handler unable to provide buffers"); //$NON-NLS-1$
  }
}
