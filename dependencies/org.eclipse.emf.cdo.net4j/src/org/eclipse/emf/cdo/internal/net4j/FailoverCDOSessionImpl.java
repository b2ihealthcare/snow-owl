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
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class FailoverCDOSessionImpl extends RecoveringCDOSessionImpl
{
  private String monitorConnectorDescription;

  private String repositoryGroup;

  public FailoverCDOSessionImpl()
  {
  }

  public void setMonitorConnectionDescription(String monitorConnectorDescription)
  {
    this.monitorConnectorDescription = monitorConnectorDescription;
  }

  public void setRepositoryGroup(String repositoryGroup)
  {
    this.repositoryGroup = repositoryGroup;
  }

  @Override
  protected void updateConnectorAndRepositoryName()
  {
    queryRepositoryInfoFromMonitor();
    IConnector connector = createTCPConnector(getUseHeartBeat());
    setConnector(connector);
  }

  protected void queryRepositoryInfoFromMonitor()
  {
    IConnector connector = getTCPConnector(monitorConnectorDescription);
    SignalProtocol<Object> protocol = new SignalProtocol<Object>("failover-client");
    protocol.open(connector);

    try
    {
      String oldRepositoryConnectorDescription = getRepositoryConnectorDescription();
      String oldRepositoryName = getRepositoryName();

      while (ObjectUtil.equals(getRepositoryConnectorDescription(), oldRepositoryConnectorDescription)
          && ObjectUtil.equals(getRepositoryName(), oldRepositoryName))
      {
        new RequestWithConfirmation<Boolean>(protocol, (short)1, "QueryRepositoryInfo")
        {
          @Override
          protected void requesting(ExtendedDataOutputStream out) throws Exception
          {
            out.writeString(repositoryGroup);
          }

          @Override
          protected Boolean confirming(ExtendedDataInputStream in) throws Exception
          {
            setRepositoryConnectorDescription(in.readString());
            setRepositoryName(in.readString());
            return true;
          }
        }.send();
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      protocol.close();
      if (connector.getChannels().isEmpty())
      {
        connector.close();
      }
    }
  }
}
