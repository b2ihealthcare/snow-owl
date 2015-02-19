/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Caspar De Groot
 */
public class LockStateRequest extends CDOClientRequest<CDOLockState[]>
{
  private int viewID;

  private Collection<CDOID> ids;

  public LockStateRequest(CDOClientProtocol protocol, int viewID, Collection<CDOID> ids)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_STATE);
    this.viewID = viewID;
    this.ids = ids;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(viewID);
    out.writeInt(ids.size());
    for (CDOID id : ids)
    {
      out.writeCDOID(id);
    }
  }

  @Override
  protected CDOLockState[] confirming(CDODataInput in) throws IOException
  {
    int n = in.readInt();
    CDOLockState[] lockStates = new CDOLockState[n];
    for (int i = 0; i < n; i++)
    {
      lockStates[i] = in.readCDOLockState();
    }

    return lockStates;
  }
}
