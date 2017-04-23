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

import org.eclipse.net4j.ITransportConfigAware;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.channel.IChannelMultiplexer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalChannelMultiplexer extends IChannelMultiplexer, IBufferProvider, ITransportConfigAware
{
  /**
   * @since 4.5
   */
  public static final ThreadLocal<InternalChannelMultiplexer> CONTEXT_MULTIPLEXER = new ThreadLocal<InternalChannelMultiplexer>();
	
  /**
   * @since 4.0
   */
  public static final short RESERVED_CHANNEL = 0;

  /**
   * Called by an {@link IChannel} each time a new buffer is available for multiplexing. This or another buffer can be
   * dequeued from the outputQueue of the {@link IChannel}.
   */
  public void multiplexChannel(InternalChannel channel);

  /**
   * @since 2.0
   */
  public void closeChannel(InternalChannel channel);
}
