/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.net4j.ReconnectingCDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Caspar De Groot
 */
public class ReconnectingCDOSessionConfigurationImpl extends RecoveringCDOSessionConfigurationImpl implements
    ReconnectingCDOSessionConfiguration
{
  private String hostAndPort;

  private long reconnectInterval = 0;

  private int maxReconnectAttempts = Integer.MAX_VALUE;

  public ReconnectingCDOSessionConfigurationImpl(String hostAndPort, String repositoryName, IManagedContainer container)
  {
    super(container);

    this.hostAndPort = hostAndPort;
    setRepositoryName(repositoryName);
  }

  public long getReconnectInterval()
  {
    return reconnectInterval;
  }

  public void setReconnectInterval(long reconnectInterval)
  {
    this.reconnectInterval = reconnectInterval;
  }

  public int getMaxReconnectAttempts()
  {
    return maxReconnectAttempts;
  }

  public void setMaxReconnectAttempts(int maxReconnectAttempts)
  {
    this.maxReconnectAttempts = maxReconnectAttempts;
  }

  @Override
  public void setExceptionHandler(ExceptionHandler handler)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InternalCDOSession createSession()
  {
    ReconnectingCDOSessionImpl session = new ReconnectingCDOSessionImpl();

    // A ReconnectingCDOSessionImpl has its own exceptionHandler; but the configuration mechanism
    // expects the configuration object (i.e. *this*) to hold a reference to the desired handler.
    // We therefore fetch the handler from the session and plug it into *this*, so that the
    // config mechanism can proceed normally. (It will "set" the same handler again.)
    //
    super.setExceptionHandler(session.getExceptionHandler());
    return session;
  }

  @Override
  protected void configureSession(InternalCDOSession session)
  {
    super.configureSession(session);

    ReconnectingCDOSessionImpl sessionImpl = (ReconnectingCDOSessionImpl)session;
    sessionImpl.setRepositoryConnectorDescription(hostAndPort);
    sessionImpl.setReconnectInterval(reconnectInterval);
    sessionImpl.setMaxReconnectAttempts(maxReconnectAttempts);
  }
}
