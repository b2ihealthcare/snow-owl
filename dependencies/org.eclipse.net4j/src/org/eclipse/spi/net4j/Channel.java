/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim - maintenance
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.concurrent.IWorkSerializer;
import org.eclipse.net4j.util.concurrent.QueueWorkerWorkSerializer;
import org.eclipse.net4j.util.concurrent.SynchronousWorkSerializer;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.InternalChannel.SendQueueEvent.Type;

import java.text.MessageFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Channel extends Lifecycle implements InternalChannel
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CHANNEL, Channel.class);

  private String userID;

  private InternalChannelMultiplexer channelMultiplexer;

  private short id = IBuffer.NO_CHANNEL;

  private ExecutorService receiveExecutor;

  /**
   * The external handler for buffers passed from the {@link #connector}.
   */
  private IBufferHandler receiveHandler;

  private IWorkSerializer receiveSerializer;

  private transient Queue<IBuffer> sendQueue;

  private transient long sentBuffers;

  private transient long receivedBuffers;

  public Channel()
  {
  }

  public String getUserID()
  {
    return userID;
  }

  public void setUserID(String userID)
  {
    this.userID = userID;
  }

  public Location getLocation()
  {
    return channelMultiplexer.getLocation();
  }

  public boolean isClient()
  {
    return channelMultiplexer.isClient();
  }

  public boolean isServer()
  {
    return channelMultiplexer.isServer();
  }

  public IChannelMultiplexer getMultiplexer()
  {
    return channelMultiplexer;
  }

  public void setMultiplexer(IChannelMultiplexer channelMultiplexer)
  {
    this.channelMultiplexer = (InternalChannelMultiplexer)channelMultiplexer;
  }

  public short getID()
  {
    return id;
  }

  public void setID(short id)
  {
    checkArg(id != IBuffer.NO_CHANNEL, "id == IBuffer.NO_CHANNEL"); //$NON-NLS-1$
    this.id = id;
  }

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public void setReceiveExecutor(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IBufferHandler getReceiveHandler()
  {
    return receiveHandler;
  }

  public void setReceiveHandler(IBufferHandler receiveHandler)
  {
    this.receiveHandler = receiveHandler;
  }

  /**
   * @since 3.0
   */
  public long getSentBuffers()
  {
    return sentBuffers;
  }

  /**
   * @since 3.0
   */
  public long getReceivedBuffers()
  {
    return receivedBuffers;
  }

  public Queue<IBuffer> getSendQueue()
  {
    return sendQueue;
  }

  public void sendBuffer(IBuffer buffer)
  {
    handleBuffer(buffer);
  }

  /**
   * Handles the given buffer. Ensures it is in the PUTTING state (otherwise ignores it) and sends it on behalf of the
   * send queue.
   *
   * @see IBuffer#getState
   * @see BufferState#PUTTING
   * @see Channel#sendQueue
   */
  public void handleBuffer(IBuffer buffer)
  {
    BufferState state = buffer.getState();
    if (state != BufferState.PUTTING)
    {
      OM.LOG.warn("Ignoring buffer in state == " + state + ": " + this); //$NON-NLS-1$ //$NON-NLS-2$
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Handling buffer: {0} --> {1}", buffer, this); //$NON-NLS-1$
    }

    if (sendQueue == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Ignoring buffer because sendQueue == null: " + this); //$NON-NLS-1$
      }

      buffer.release();
    }
    else
    {
      sendQueue.add(buffer);
      ++sentBuffers;
      channelMultiplexer.multiplexChannel(this);
    }
  }

  /**
   * Handles a buffer sent by the multiplexer. Adds work to the receive queue or releases the buffer.
   *
   * @see InternalChannelMultiplexer#multiplexChannel
   * @see IWorkSerializer
   * @see ReceiverWork
   */
  public void handleBufferFromMultiplexer(IBuffer buffer)
  {
    if (receiveHandler != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Handling buffer from multiplexer: {0} --> {1}", buffer, this); //$NON-NLS-1$
      }

      ++receivedBuffers;
      receiveSerializer.addWork(createReceiverWork(buffer));
    }
    else
    {
      // Shutting down
      buffer.release();
    }
  }

  protected ReceiverWork createReceiverWork(IBuffer buffer)
  {
    return new ReceiverWork(buffer);
  }

  public short getBufferCapacity()
  {
    return channelMultiplexer.getBufferCapacity();
  }

  public IBuffer provideBuffer()
  {
    return channelMultiplexer.provideBuffer();
  }

  public void retainBuffer(IBuffer buffer)
  {
    channelMultiplexer.retainBuffer(buffer);
  }

  @Override
  public String toString()
  {
    if (receiveHandler instanceof IProtocol)
    {
      IProtocol<?> protocol = (IProtocol<?>)receiveHandler;
      return MessageFormat.format("Channel[{0}, {1}, {2}]", id, getLocation(), protocol.getType()); //$NON-NLS-1$
    }

    return MessageFormat.format("Channel[{0}, {1}]", id, getLocation()); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(id != IBuffer.NO_CHANNEL, "channelID == NO_CHANNEL"); //$NON-NLS-1$
    checkState(channelMultiplexer, "channelMultiplexer"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    sendQueue = new SendQueue();
    if (receiveExecutor == null)
    {
      receiveSerializer = new SynchronousWorkSerializer();
    }
    else
    {
      receiveSerializer = new ReceiveSerializer();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    unregisterFromMultiplexer();
    if (receiveSerializer != null)
    {
      receiveSerializer.dispose();
      receiveSerializer = null;
    }

    if (sendQueue != null)
    {
      sendQueue.clear();
      sendQueue = null;
    }

    super.doDeactivate();
  }

  protected void unregisterFromMultiplexer()
  {
    channelMultiplexer.closeChannel(this);
  }

  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  public boolean isClosed()
  {
    return !isActive();
  }

  /**
   * @author Eike Stepper
   * @since 4.1
   */
  protected class ReceiveSerializer extends QueueWorkerWorkSerializer
  {
    // CompletionWorkSerializer throws "One command already pending"
    // CompletionWorkSerializer
    // AsynchronousWorkSerializer
    // SynchronousWorkSerializer

    @Override
    protected String getThreadName()
    {
      return "ReceiveSerializer-" + Channel.this; //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  protected class ReceiverWork implements Runnable
  {
    private final IBuffer buffer;

    /**
     * @since 3.0
     */
    public ReceiverWork(IBuffer buffer)
    {
      this.buffer = buffer;
    }

    public void run()
    {
      IBufferHandler receiveHandler = getReceiveHandler();
      if (receiveHandler != null)
      {
        receiveHandler.handleBuffer(buffer);
      }
      else
      {
        // Shutting down
        buffer.release();
      }
    }
  }

  /**
   * A queue that holds buffers that shall be sent. This implementation notifies observers of enqueued and dequeued
   * buffers. The notification's deliberately not synchronized. It shall only be used by O&M tooling to offer (not 100%
   * accurate) statistical insights
   *
   * @author Eike Stepper
   * @since 3.0
   */
  protected class SendQueue extends ConcurrentLinkedQueue<IBuffer>
  {
    private static final long serialVersionUID = 1L;

    private AtomicInteger size = new AtomicInteger();

    protected SendQueue()
    {
    }

    @Override
    public boolean add(IBuffer o)
    {
      super.add(o);
      added();
      return true;
    }

    @Override
    public boolean offer(IBuffer o)
    {
      super.offer(o);
      added();
      return true;
    }

    @Override
    public IBuffer poll()
    {
      IBuffer result = super.poll();
      if (result != null)
      {
        removed();
      }

      return result;
    }

    @Override
    public IBuffer remove()
    {
      IBuffer result = super.remove();
      if (result != null)
      {
        removed();
      }

      return result;
    }

    @Override
    public boolean remove(Object o)
    {
      boolean result = super.remove(o);
      if (result)
      {
        removed();
      }

      return result;
    }

    private void added()
    {
      int queueSize = size.incrementAndGet();
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new SendQueueEventImpl(Type.ENQUEUED, queueSize), listeners);
      }
    }

    private void removed()
    {
      int queueSize = size.decrementAndGet();
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new SendQueueEventImpl(Type.DEQUEUED, queueSize), listeners);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SendQueueEventImpl extends Event implements SendQueueEvent
  {
    private static final long serialVersionUID = 1L;

    private Type type;

    private final int queueSize;

    private SendQueueEventImpl(Type type, int queueSize)
    {
      super(Channel.this);
      this.type = type;
      this.queueSize = queueSize;
    }

    @Override
    public InternalChannel getSource()
    {
      return (InternalChannel)super.getSource();
    }

    public Type getType()
    {
      return type;
    }

    public int getQueueSize()
    {
      return queueSize;
    }
  }
}
