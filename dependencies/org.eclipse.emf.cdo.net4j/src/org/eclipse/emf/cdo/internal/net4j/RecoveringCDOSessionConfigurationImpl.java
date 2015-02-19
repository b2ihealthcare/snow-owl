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

import org.eclipse.emf.cdo.net4j.RecoveringCDOSessionConfiguration;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Caspar De Groot
 */
public abstract class RecoveringCDOSessionConfigurationImpl extends CDONet4jSessionConfigurationImpl implements
    RecoveringCDOSessionConfiguration
{
  private IManagedContainer container;

  private boolean heartBeatEnabled = false;

  private long heartBeatPeriod = 1000L;

  private long heartBeatTimeout = 5000L;

  private long connectorTimeout = 10000L;

  public RecoveringCDOSessionConfigurationImpl(IManagedContainer container)
  {
    this.container = container;
  }

  protected IManagedContainer getContainer()
  {
    return container;
  }

  public long getConnectorTimeout()
  {
    return connectorTimeout;
  }

  public void setConnectorTimeout(long timeout)
  {
    connectorTimeout = timeout;
  }

  public boolean isHeartBeatEnabled()
  {
    return heartBeatEnabled;
  }

  public void setHeartBeatEnabled(boolean enabled)
  {
    heartBeatEnabled = enabled;
  }

  public long getHeartBeatTimeout()
  {
    return heartBeatTimeout;
  }

  public void setHeartBeatTimeout(long timeout)
  {
    heartBeatTimeout = timeout;
  }

  public long getHeartBeatPeriod()
  {
    return heartBeatPeriod;
  }

  public void setHeartBeatPeriod(long period)
  {
    heartBeatPeriod = period;
  }

  @Override
  protected void configureSession(InternalCDOSession session)
  {
    super.configureSession(session);

    if (heartBeatEnabled && (heartBeatPeriod == 0 || heartBeatTimeout == 0))
    {
      throw new IllegalStateException("Cannot use a heartbeat with zero value set for period or timeout.");
    }

    RecoveringCDOSessionImpl sessionImpl = (RecoveringCDOSessionImpl)session;
    sessionImpl.setContainer(getContainer());
    sessionImpl.setUseHeartBeat(heartBeatEnabled);
    sessionImpl.setHeartBeatPeriod(heartBeatPeriod);
    sessionImpl.setHeartBeatTimeout(heartBeatTimeout);
    sessionImpl.setConnectorTimeout(connectorTimeout);
  }
}
