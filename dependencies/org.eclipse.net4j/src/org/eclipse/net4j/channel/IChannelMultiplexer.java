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
package org.eclipse.net4j.channel;

import org.eclipse.net4j.ILocationAware;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.util.Collection;

/**
 * Manages and multiplexes virtual data {@link IChannel channels} over a shared physical connection.
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IChannelMultiplexer extends ILocationAware, IContainer<IChannel>
{
  /**
   * @since 2.0
   */
  public static final long NO_CHANNEL_TIMEOUT = Long.MAX_VALUE;

  /**
   * Indicates to use the timeout that is configured via debug property <code>open.channel.timeout</code> (see .options
   * file) which has a default of 10 seconds.
   * 
   * @since 2.0
   */
  public static final long DEFAULT_OPEN_CHANNEL_TIMEOUT = -1;

  /**
   * Synchronous request to open a new {@link IChannel} with an undefined channel protocol. Since the peer connector
   * can't lookup a protocol {@link IFactory factory} without a protocol identifier the {@link IBufferHandler} of the
   * peer {@link IChannel} can only be provided by externally provided channel {@link ILifecycle lifecycle}
   * {@link IListener listeners}.
   * <p>
   * 
   * @see #openChannel(String, Object)
   * @see #openChannel(IProtocol)
   * @since 2.0
   */
  public IChannel openChannel() throws ChannelException;

  /**
   * Synchronous request to open a new {@link IChannel} with a channel protocol defined by a given protocol identifier.
   * The peer connector will lookup a protocol {@link IFactory factory} with the protocol identifier, create a
   * {@link IBufferHandler} and inject it into the peer {@link IChannel}.
   * <p>
   * 
   * @see #openChannel()
   * @see #openChannel(IProtocol)
   * @since 2.0
   */
  public IChannel openChannel(String protocolID, Object infraStructure) throws ChannelException;

  /**
   * Synchronous request to open a new {@link IChannel} with the given channel protocol . The peer connector will lookup
   * a protocol {@link IFactory factory} with the protocol identifier, create a {@link IBufferHandler} and inject it
   * into the peer channel.
   * <p>
   * 
   * @see #openChannel()
   * @see #openChannel(String, Object)
   * @since 2.0
   */
  public IChannel openChannel(IProtocol<?> protocol) throws ChannelException;

  /**
   * Returns a collection of currently open channels.
   * 
   * @since 2.0
   */
  public Collection<IChannel> getChannels();

  /**
   * @since 2.0
   */
  public long getOpenChannelTimeout();

  /**
   * @since 2.0
   */
  public void setOpenChannelTimeout(long openChannelTimeout);
}
