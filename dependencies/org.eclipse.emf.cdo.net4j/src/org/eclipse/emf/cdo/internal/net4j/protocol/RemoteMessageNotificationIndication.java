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
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RemoteMessageNotificationIndication extends CDOClientIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      RemoteMessageNotificationIndication.class);

  public RemoteMessageNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REMOTE_MESSAGE_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int senderID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Read senderID: " + senderID); //$NON-NLS-1$
    }

    CDORemoteSessionMessage message = new CDORemoteSessionMessage(in);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Read message: " + message); //$NON-NLS-1$
    }

    InternalCDORemoteSessionManager remoteSessionManager = getSession().getRemoteSessionManager();
    remoteSessionManager.handleRemoteSessionMessage(senderID, message);
  }
}
