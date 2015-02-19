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

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class UnsubscribeRemoteSessionsRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      UnsubscribeRemoteSessionsRequest.class);

  public UnsubscribeRemoteSessionsRequest(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_UNSUBSCRIBE_REMOTE_SESSIONS);
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Unsubscribing"); //$NON-NLS-1$
    }
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
