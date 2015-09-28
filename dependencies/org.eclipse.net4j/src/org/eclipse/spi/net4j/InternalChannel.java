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
package org.eclipse.spi.net4j;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public interface InternalChannel extends IChannel, IBufferProvider, ILifecycle
{
  /**
   * @since 2.0
   */
  public void setID(short id);

  /**
   * @since 2.0
   */
  public void setUserID(String userID);

  public ExecutorService getReceiveExecutor();

  public void setReceiveExecutor(ExecutorService receiveExecutor);

  /**
   * @since 2.0
   */
  public void setMultiplexer(IChannelMultiplexer channelMultiplexer);

  public void handleBufferFromMultiplexer(IBuffer buffer);

  /**
   * @since 3.0
   */
  public long getReceivedBuffers();

  /**
   * @since 3.0
   */
  public long getSentBuffers();

  public Queue<IBuffer> getSendQueue();

  /**
   * An {@link IEvent event} fired from a {@link InternalChannel channel} when a {@link IBuffer buffer} is enqueued or
   * dequeued.
   * 
   * @author Eike Stepper
   * @since 3.0
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface SendQueueEvent extends IEvent
  {
    public InternalChannel getSource();

    public Type getType();

    public int getQueueSize();

    /**
     * Enumerates the possible {@link InternalChannel#getSendQueue() send queue} {@link SendQueueEvent event} types.
     * 
     * @author Eike Stepper
     */
    public enum Type
    {
      ENQUEUED, DEQUEUED
    }
  }
}
