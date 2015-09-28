/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RemoteMessageNotificationRequest extends CDOServerRequest
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      RemoteMessageNotificationRequest.class);

  private int senderID;

  private CDORemoteSessionMessage message;

  public RemoteMessageNotificationRequest(CDOServerProtocol serverProtocol, InternalSession sender,
      CDORemoteSessionMessage message)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_REMOTE_MESSAGE_NOTIFICATION);
    senderID = sender.getSessionID();
    this.message = message;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Writing senderID: " + senderID); //$NON-NLS-1$
    }

    out.writeInt(senderID);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Writing message: " + message); //$NON-NLS-1$
    }

    message.write(out);
  }
}
