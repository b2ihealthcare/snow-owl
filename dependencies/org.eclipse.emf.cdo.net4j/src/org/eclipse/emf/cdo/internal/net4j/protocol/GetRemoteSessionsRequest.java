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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class GetRemoteSessionsRequest extends CDOClientRequest<List<CDORemoteSession>>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, GetRemoteSessionsRequest.class);

  private boolean subscribe;

  public GetRemoteSessionsRequest(CDOClientProtocol protocol, boolean subscribe)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_GET_REMOTE_SESSIONS);
    this.subscribe = subscribe;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing subscribe: {0}", subscribe); //$NON-NLS-1$
    }

    out.writeBoolean(subscribe);
  }

  @Override
  protected List<CDORemoteSession> confirming(CDODataInput in) throws IOException
  {
    List<CDORemoteSession> result = new ArrayList<CDORemoteSession>();

    for (;;)
    {
      int sessionID = in.readInt();
      if (sessionID == CDOProtocolConstants.NO_MORE_REMOTE_SESSIONS)
      {
        break;
      }

      String userID = in.readString();
      boolean subscribed = in.readBoolean();
      InternalCDORemoteSessionManager manager = getSession().getRemoteSessionManager();
      CDORemoteSession remoteSession = manager.createRemoteSession(sessionID, userID, subscribed);
      result.add(remoteSession);
    }

    return result;
  }
}
