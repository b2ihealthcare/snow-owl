/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Simon McDuff - bug 230832
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class ChangeSubscriptionRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, ChangeSubscriptionRequest.class);

  private int viewID;

  private List<CDOID> ids;

  /**
   * true - it will subscribe id's. <br>
   * false - it will unsubscribe id's.
   */
  private boolean subscribeMode;

  private boolean clear;

  public ChangeSubscriptionRequest(CDOClientProtocol protocol, int viewID, List<CDOID> ids, boolean subscribeMode,
      boolean clear)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_SUBSCRIPTION);
    this.viewID = viewID;
    this.ids = ids;
    this.subscribeMode = subscribeMode;
    this.clear = clear;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("View " + viewID + " subscribing to " + ids.size()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    out.writeInt(viewID);
    out.writeBoolean(clear);
    out.writeInt(subscribeMode ? ids.size() : -ids.size());
    for (CDOID id : ids)
    {
      out.writeCDOID(id);
    }
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
