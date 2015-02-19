/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.IErrorHandler;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.InternalBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class Buffer implements InternalBuffer
{
  public static final int EOS_OFFSET = 1;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER, Buffer.class);

  private IErrorHandler errorHandler;

  private IBufferProvider bufferProvider;

  private short channelID;

  private boolean eos;

  private BufferState state = BufferState.INITIAL;

  private ByteBuffer byteBuffer;

  public Buffer(IBufferProvider provider, short capacity)
  {
    bufferProvider = provider;
    byteBuffer = ByteBuffer.allocateDirect(capacity);
  }

  public boolean isEOS()
  {
    return eos;
  }

  public void setEOS(boolean eos)
  {
    this.eos = eos;
  }

  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public short getChannelID()
  {
    if (state == BufferState.INITIAL || state == BufferState.READING_HEADER)
    {
      throw new IllegalStateException(toString());
    }

    return channelID;
  }

  public void setChannelID(short channelID)
  {
    this.channelID = channelID;
  }

  public short getCapacity()
  {
    return (short)byteBuffer.capacity();
  }

  public BufferState getState()
  {
    return state;
  }

  public void setState(BufferState state)
  {
    this.state = state;
  }

  public ByteBuffer getByteBuffer()
  {
    return byteBuffer;
  }

  public void setByteBuffer(ByteBuffer buffer)
  {
    byteBuffer = buffer;
  }

  public void clear()
  {
    state = BufferState.INITIAL;
    channelID = NO_CHANNEL;
    eos = false;
    byteBuffer.clear();
  }

  public void release()
  {
    if (state != BufferState.RELEASED)
    {
      state = BufferState.RELEASED;
      errorHandler = null;
      if (bufferProvider != null)
      {
        bufferProvider.retainBuffer(this);
      }
    }
  }

  public void dispose()
  {
    state = BufferState.DISPOSED;
    bufferProvider = null;
    byteBuffer = null;
  }

  public ByteBuffer startGetting(SocketChannel socketChannel) throws IOException
  {
    try
    {
      if (state != BufferState.INITIAL && state != BufferState.READING_HEADER && state != BufferState.READING_BODY)
      {
        throw new IllegalStateException(toString());
      }

      if (state == BufferState.INITIAL)
      {
        byteBuffer.limit(IBuffer.HEADER_SIZE);
        state = BufferState.READING_HEADER;
      }

      if (state == BufferState.READING_HEADER)
      {
        readChannel(socketChannel, byteBuffer);
        if (byteBuffer.hasRemaining())
        {
          return null;
        }

        byteBuffer.flip();
        channelID = byteBuffer.getShort();
        short payloadSize = byteBuffer.getShort();
        if (payloadSize < 0)
        {
          eos = true;
          payloadSize = (short)-payloadSize;
        }

        payloadSize -= EOS_OFFSET;

        byteBuffer.clear();
        byteBuffer.limit(payloadSize);
        state = BufferState.READING_BODY;
      }

      readChannel(socketChannel, byteBuffer);
      if (byteBuffer.hasRemaining())
      {
        return null;
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Read " + byteBuffer.limit() + " bytes" //$NON-NLS-1$ //$NON-NLS-2$
            + (eos ? " (EOS)" : "") + StringUtil.NL + formatContent(false)); //$NON-NLS-1$ //$NON-NLS-2$
      }

      byteBuffer.flip();
      state = BufferState.GETTING;
      return byteBuffer;
    }
    catch (IOException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (RuntimeException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (Error ex)
    {
      handleError(ex);
      throw ex;
    }
  }

  public ByteBuffer startPutting(short channelID)
  {
    try
    {
      if (state == BufferState.PUTTING)
      {
        if (channelID != this.channelID)
        {
          throw new IllegalArgumentException("channelID != this.channelID"); //$NON-NLS-1$
        }
      }
      else if (state != BufferState.INITIAL)
      {
        throw new IllegalStateException("state: " + state); //$NON-NLS-1$
      }
      else
      {
        state = BufferState.PUTTING;
        this.channelID = channelID;

        byteBuffer.clear();
        byteBuffer.position(IBuffer.HEADER_SIZE);
      }

      return byteBuffer;
    }
    catch (RuntimeException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (Error ex)
    {
      handleError(ex);
      throw ex;
    }
  }

  /**
   * @return <code>true</code> if the buffer has been completely written, <code>false</code> otherwise.
   */
  public boolean write(SocketChannel socketChannel) throws IOException
  {
    try
    {
      if (byteBuffer.position() == HEADER_SIZE)
      {
        clear();
        return true; // *Pretend* that this empty buffer has been written
      }

      if (state != BufferState.PUTTING && state != BufferState.WRITING)
      {
        throw new IllegalStateException(toString());
      }

      if (state == BufferState.PUTTING)
      {
        if (channelID == NO_CHANNEL)
        {
          throw new IllegalStateException("channelID == NO_CHANNEL"); //$NON-NLS-1$
        }

        int payloadSize = byteBuffer.position() - IBuffer.HEADER_SIZE + EOS_OFFSET;
        if (eos)
        {
          payloadSize = -payloadSize;
        }

        if (TRACER.isEnabled())
        {
          TRACER.trace("Writing " + (Math.abs(payloadSize) - 1) + " bytes" //$NON-NLS-1$ //$NON-NLS-2$
              + (eos ? " (EOS)" : "") + StringUtil.NL + formatContent(false)); //$NON-NLS-1$ //$NON-NLS-2$
        }

        byteBuffer.flip();
        byteBuffer.putShort(channelID);
        byteBuffer.putShort((short)payloadSize);
        byteBuffer.position(0);
        state = BufferState.WRITING;
      }

      int numBytes = socketChannel.write(byteBuffer);
      if (numBytes == -1)
      {
        throw new IOException("Channel closed"); //$NON-NLS-1$
      }

      if (byteBuffer.hasRemaining())
      {
        return false;
      }

      clear();
      return true;
    }
    catch (IOException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (RuntimeException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (Error ex)
    {
      handleError(ex);
      throw ex;
    }
  }

  public void flip()
  {
    try
    {
      if (state != BufferState.PUTTING)
      {
        throw new IllegalStateException(toString());
      }

      byteBuffer.flip();
      byteBuffer.position(IBuffer.HEADER_SIZE);
      state = BufferState.GETTING;
    }
    catch (RuntimeException ex)
    {
      handleError(ex);
      throw ex;
    }
    catch (Error ex)
    {
      handleError(ex);
      throw ex;
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Buffer@{0}[{1}]", ReflectUtil.getID(this), state); //$NON-NLS-1$
  }

  @SuppressWarnings("deprecation")
  public String formatContent(boolean showHeader)
  {
    final int oldPosition = byteBuffer.position();
    final int oldLimit = byteBuffer.limit();

    try
    {
      if (state != BufferState.GETTING)
      {
        byteBuffer.flip();
      }

      if (state == BufferState.PUTTING && !showHeader)
      {
        byteBuffer.position(IBuffer.HEADER_SIZE);
      }

      StringBuilder builder = new StringBuilder();
      while (byteBuffer.hasRemaining())
      {
        byte b = byteBuffer.get();
        HexUtil.appendHex(builder, b < 0 ? ~b : b);
        builder.append(' ');
      }

      return builder.toString();
    }
    finally
    {
      byteBuffer.position(oldPosition);
      byteBuffer.limit(oldLimit);
    }
  }

  public IErrorHandler getErrorHandler()
  {
    return errorHandler;
  }

  public void setErrorHandler(IErrorHandler errorHandler)
  {
    this.errorHandler = errorHandler;
  }

  public void handleError(Throwable t)
  {
    if (errorHandler != null)
    {
      errorHandler.handleError(t);
    }

    release();
  }

  private static void readChannel(SocketChannel socketChannel, ByteBuffer buffer) throws ClosedChannelException
  {
    try
    {
      if (socketChannel.read(buffer) == -1)
      {
        throw new ClosedChannelException();
      }
    }
    catch (ClosedChannelException ex)
    {
      throw ex;
    }
    catch (IOException ex)
    {
      throw new ClosedChannelException();
    }
  }
}
