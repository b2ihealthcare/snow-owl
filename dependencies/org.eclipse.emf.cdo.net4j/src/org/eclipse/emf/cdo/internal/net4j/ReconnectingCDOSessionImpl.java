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

import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.IConnector;

/**
 * @author Caspar De Groot
 */
public class ReconnectingCDOSessionImpl extends RecoveringCDOSessionImpl
{
  private long reconnectInterval = 0;

  private int maxReconnectAttempts = Integer.MAX_VALUE;

  public ReconnectingCDOSessionImpl()
  {
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
  public void setConnector(IConnector connector)
  {
    // Do nothing (ignore an externally configured connector)
    // Note: we cannot throw UnsupportedOperationException because the
    // SessionConfig object will call this.
  }

  @Override
  public void setRepositoryConnectorDescription(String description)
  {
    if (getRepositoryConnectorDescription() != null)
    {
      throw new IllegalStateException("Don't call setRepositoryConnectorDescription more than once");
    }

    super.setRepositoryConnectorDescription(description);
  }

  @Override
  protected void updateConnectorAndRepositoryName()
  {
    removeTCPConnector();

    IConnector newConnector = null;
    int failedAttempts = 0;
    long startOfLastAttempt = 0;

    while (newConnector == null && failedAttempts < maxReconnectAttempts)
    {
      try
      {
        if (startOfLastAttempt > 0)
        {
          delayAsNeeded(startOfLastAttempt);
        }

        startOfLastAttempt = System.currentTimeMillis();
        newConnector = createTCPConnector(getUseHeartBeat());
      }
      catch (ConnectorException ex)
      {
        failedAttempts++;
      }
    }

    if (newConnector == null)
    {
      throw new RuntimeException("Recovery failed"); // TODO (CD) Create custom exception type?
    }

    super.setConnector(newConnector);
  }

  private void delayAsNeeded(long startOfLastAttempt)
  {
    long timeToWait = requiredDelay(startOfLastAttempt);
    while (timeToWait > 0)
    {
      try
      {
        Thread.sleep(timeToWait);
        timeToWait = 0;
      }
      catch (InterruptedException ex)
      {
        timeToWait = requiredDelay(startOfLastAttempt);
      }
    }
  }

  private long requiredDelay(long startOfLastAttempt)
  {
    long now = System.currentTimeMillis();
    long timeSinceLastAttempt = now - startOfLastAttempt;
    long timeToWait = reconnectInterval - timeSinceLastAttempt;
    return timeToWait;
  }
}
