/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RemoteSessionNotificationIndication extends CDOClientIndication
{
  public RemoteSessionNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REMOTE_SESSION_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int sessionID = in.readInt();
    byte opcode = in.readByte();
    switch (opcode)
    {
    case CDOProtocolConstants.REMOTE_SESSION_OPENED:
      String userID = in.readString();
      getSession().getRemoteSessionManager().handleRemoteSessionOpened(sessionID, userID);
      break;

    case CDOProtocolConstants.REMOTE_SESSION_CLOSED:
      getSession().getRemoteSessionManager().handleRemoteSessionClosed(sessionID);
      break;

    case CDOProtocolConstants.REMOTE_SESSION_SUBSCRIBED:
      getSession().getRemoteSessionManager().handleRemoteSessionSubscribed(sessionID, true);
      break;

    case CDOProtocolConstants.REMOTE_SESSION_UNSUBSCRIBED:
      getSession().getRemoteSessionManager().handleRemoteSessionSubscribed(sessionID, false);
      break;
    }
  }
}
