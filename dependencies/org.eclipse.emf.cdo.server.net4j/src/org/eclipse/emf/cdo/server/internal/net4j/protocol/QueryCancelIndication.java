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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryCancelIndication extends CDOServerReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryCancelIndication.class);

  private int queryID;

  public QueryCancelIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY_CANCEL);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    queryID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Query " + queryID + " will be cancelled"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    try
    {
      getRepository().getQueryManager().cancel(queryID);
      out.writeBoolean(false);
    }
    catch (Exception exception)
    {
      out.writeBoolean(true);
      out.writeString(exception.getMessage());
    }
  }
}
