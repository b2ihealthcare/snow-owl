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
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RemoteMessageIndication extends CDOServerReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, RemoteMessageIndication.class);

  private List<Integer> result;

  public RemoteMessageIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REMOTE_MESSAGE);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    CDORemoteSessionMessage message = new CDORemoteSessionMessage(in);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Read message: " + message); //$NON-NLS-1$
    }

    int count = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} recipients", count); //$NON-NLS-1$
    }

    int[] recipients = new int[count];
    for (int i = 0; i < recipients.length; i++)
    {
      recipients[i] = in.readInt();
    }

    InternalSessionManager sessionManager = getRepository().getSessionManager();
    result = sessionManager.sendRemoteMessageNotification(getSession(), message, recipients);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeInt(result.size());
    for (Integer recipient : result)
    {
      out.writeInt(recipient);
    }
  }
}
