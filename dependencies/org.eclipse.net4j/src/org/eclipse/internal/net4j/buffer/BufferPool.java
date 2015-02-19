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
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.InternalBuffer;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public class BufferPool extends BufferProvider implements IBufferPool.Introspection
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER, BufferPool.class);

  private final IBufferProvider provider;

  private int pooledBuffers;

  @ExcludeFromDump
  private final Queue<BufferRef> buffers = new ConcurrentLinkedQueue<BufferRef>();

  @ExcludeFromDump
  private final ReferenceQueue<IBuffer> referenceQueue = new ReferenceQueue<IBuffer>();

  @ExcludeFromDump
  private Monitor monitor;

  public BufferPool(IBufferProvider provider)
  {
    super(provider.getBufferCapacity());
    this.provider = provider;
  }

  public IBufferProvider getProvider()
  {
    return provider;
  }

  public ReferenceQueue<IBuffer> getReferenceQueue()
  {
    return referenceQueue;
  }

  public int getPooledBuffers()
  {
    return pooledBuffers;
  }

  public boolean evictOne()
  {
    for (;;)
    {
      BufferRef bufferRef = buffers.poll();
      if (bufferRef == null)
      {
        return false;
      }

      IBuffer buffer = bufferRef.get();
      if (buffer != null)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Evicting " + buffer); //$NON-NLS-1$
        }

        provider.retainBuffer(buffer);
        --pooledBuffers;
        return true;
      }
    }
  }

  public int evict(int survivors)
  {
    int evictedBuffers = 0;
    while (pooledBuffers > survivors)
    {
      if (evictOne())
      {
        ++evictedBuffers;
      }
      else
      {
        break;
      }
    }

    return evictedBuffers;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("BufferPool[{0}]", getBufferCapacity()); //$NON-NLS-1$
  }

  protected BufferRef createBufferRef(IBuffer buffer)
  {
    return new BufferRef(buffer, referenceQueue);
  }

  @Override
  protected IBuffer doProvideBuffer()
  {
    IBuffer buffer = null;
    BufferRef bufferRef = buffers.poll();
    if (bufferRef != null)
    {
      buffer = bufferRef.get();
    }

    if (buffer == null)
    {
      buffer = provider.provideBuffer();
      ((InternalBuffer)buffer).setBufferProvider(this);
    }
    else
    {
      --pooledBuffers;
    }

    buffer.clear();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Obtained " + buffer); //$NON-NLS-1$
    }

    return buffer;
  }

  @Override
  protected void doRetainBuffer(IBuffer buffer)
  {
    if (buffer.getCapacity() != getBufferCapacity())
    {
      throw new IllegalArgumentException("buffer.getCapacity() != getBufferCapacity()"); //$NON-NLS-1$
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Retaining " + buffer); //$NON-NLS-1$
    }

    BufferRef bufferRef = createBufferRef(buffer);
    buffers.add(bufferRef);
    ++pooledBuffers;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    monitor = new Monitor();
    monitor.start();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    monitor.interrupt();
    monitor = null;
    super.doDeactivate();
  }

  private static final class BufferRef extends SoftReference<IBuffer>
  {
    public BufferRef(IBuffer buffer, ReferenceQueue<IBuffer> queue)
    {
      super(buffer, queue);
    }
  }

  private final class Monitor extends Thread
  {
    public Monitor()
    {
      setName("BufferPoolMonitor"); //$NON-NLS-1$
      setDaemon(true);
    }

    @Override
    public void run()
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Start monitoring"); //$NON-NLS-1$
      }

      try
      {
        while (isActive() && !isInterrupted())
        {
          Reference<? extends IBuffer> bufferRef = referenceQueue.remove(200);
          if (bufferRef != null)
          {
            if (buffers.remove(bufferRef))
            {
              --pooledBuffers;
              if (TRACER.isEnabled())
              {
                TRACER.trace("Collected buffer"); //$NON-NLS-1$
              }
            }
          }
        }
      }
      catch (InterruptedException ex)
      {
        return;
      }
      finally
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Stop monitoring"); //$NON-NLS-1$
        }
      }
    }
  }
}
