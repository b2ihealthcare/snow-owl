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
package org.eclipse.net4j;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.security.INegotiatorAware;

import java.util.concurrent.ExecutorService;

/**
 * A common transport configuration that specifies basic dependencies for {@link IChannelMultiplexer channel
 * multiplexers}, {@link IConnector connectors} and {@link IAcceptor acceptors}.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITransportConfig extends INegotiatorAware
{
  /**
   * Returns the lifecycle delegate used for inactivity checks in the setter implementations of this transport
   * configuration.
   */
  public ILifecycle getLifecycle();

  /**
   * Sets the lifecycle delegate to be used for inactivity checks in the setter implementations of this transport
   * configuration.
   */
  public void setLifecycle(ILifecycle lifecycle);

  public IBufferProvider getBufferProvider();

  public void setBufferProvider(IBufferProvider bufferProvider);

  public ExecutorService getReceiveExecutor();

  public void setReceiveExecutor(ExecutorService receiveExecutor);

  public IProtocolProvider getProtocolProvider();

  public void setProtocolProvider(IProtocolProvider protocolProvider);
}
