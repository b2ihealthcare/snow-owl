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
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class RemoteMessageRequest extends CDOClientRequest<Set<Integer>>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, RemoteMessageRequest.class);

  private CDORemoteSessionMessage message;

  private List<CDORemoteSession> recipients;

  public RemoteMessageRequest(CDOClientProtocol protocol, CDORemoteSessionMessage message,
      List<CDORemoteSession> recipients)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REMOTE_MESSAGE);
    this.message = message;
    this.recipients = recipients;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Writing message: " + message); //$NON-NLS-1$
    }

    message.write(out);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} recipients", recipients.size()); //$NON-NLS-1$
    }

    out.writeInt(recipients.size());
    for (CDORemoteSession recipient : recipients)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Writing recipient: " + recipient); //$NON-NLS-1$
      }

      out.writeInt(recipient.getSessionID());
    }
  }

  @Override
  protected Set<Integer> confirming(CDODataInput in) throws IOException
  {
    Set<Integer> sessionIDs = new HashSet<Integer>();
    int count = in.readInt();
    for (int i = 0; i < count; i++)
    {
      int sessionID = in.readInt();
      sessionIDs.add(sessionID);
    }

    return sessionIDs;
  }
}
