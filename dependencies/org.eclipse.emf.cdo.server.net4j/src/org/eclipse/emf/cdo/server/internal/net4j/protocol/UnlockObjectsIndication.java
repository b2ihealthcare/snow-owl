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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class UnlockObjectsIndication extends CDOServerWriteIndication
{
  private UnlockObjectsResult result;

  public UnlockObjectsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_UNLOCK_OBJECTS);
  }

  protected UnlockObjectsIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    LockType lockType = in.readCDOLockType();
    boolean recursive = in.readBoolean();
    int size = in.readInt();

    InternalRepository repository = getRepository();
    IView view = getView(viewID);

    if (size == CDOProtocolConstants.RELEASE_ALL_LOCKS)
    {
      result = repository.unlock((InternalView)view, null, null, false);
    }
    else
    {
      List<CDOID> objectIDs = new LinkedList<CDOID>();
      for (int i = 0; i < size; i++)
      {
        objectIDs.add(in.readCDOID());
      }

      result = repository.unlock((InternalView)view, lockType, objectIDs, recursive);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeLong(result.getTimestamp());
    CDOLockState[] newLockStates = result.getNewLockStates();
    out.writeInt(newLockStates.length);
    for (CDOLockState state : newLockStates)
    {
      out.writeCDOLockState(state);
    }
  }

  protected IView getView(int viewID)
  {
    return getSession().getView(viewID);
  }
}
