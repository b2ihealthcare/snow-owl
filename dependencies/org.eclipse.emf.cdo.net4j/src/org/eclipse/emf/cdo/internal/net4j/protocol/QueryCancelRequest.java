/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryCancelRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryCancelRequest.class);

  private int queryID;

  public QueryCancelRequest(CDOClientProtocol protocol, int queryID)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY_CANCEL);
    this.queryID = queryID;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Cancel query " + queryID); //$NON-NLS-1$
    }

    out.writeInt(queryID);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    boolean exception = in.readBoolean();
    if (exception)
    {
      String message = in.readString();
      throw new RuntimeException(message);
    }

    return true;
  }
}
