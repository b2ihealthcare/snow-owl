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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class GetRemoteSessionsIndication extends CDOServerReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, GetRemoteSessionsIndication.class);

  private boolean subscribe;

  public GetRemoteSessionsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_GET_REMOTE_SESSIONS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    subscribe = in.readBoolean();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read subscribe: {0}", subscribe); //$NON-NLS-1$
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalSession localSession = getSession();
    InternalSession[] sessions = getSession().getManager().getSessions();
    for (InternalSession session : sessions)
    {
      if (session != localSession)
      {
        out.writeInt(session.getSessionID());
        out.writeString(session.getUserID());
        out.writeBoolean(session.isSubscribed());
      }
    }

    out.writeInt(CDOProtocolConstants.NO_MORE_REMOTE_SESSIONS);
    localSession.setSubscribed(subscribe);
  }
}
