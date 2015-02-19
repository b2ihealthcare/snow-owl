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
 *    Caspar De Groot - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockObjectsResult;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class LockObjectsIndication extends CDOServerWriteIndication
{
  private LockObjectsResult result;

  public LockObjectsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_OBJECTS);
  }

  protected LockObjectsIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    LockType lockType = in.readCDOLockType();
    boolean recursive = in.readBoolean();
    long timeout = in.readLong();

    int nRevisions = in.readInt();
    List<CDORevisionKey> revisionKeys = new LinkedList<CDORevisionKey>();
    for (int i = 0; i < nRevisions; i++)
    {
      revisionKeys.add(in.readCDORevisionKey());
    }

    InternalRepository repository = getRepository();
    IView view = getView(viewID);
    result = repository.lock((InternalView)view, lockType, revisionKeys, recursive, timeout);
  }

  protected IView getView(int viewID)
  {
    return getSession().getView(viewID);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(result.isSuccessful());
    out.writeBoolean(result.isTimedOut());
    out.writeBoolean(result.isWaitForUpdate());
    out.writeLong(result.getRequiredTimestamp());

    CDORevisionKey[] staleRevisions = result.getStaleRevisions();
    out.writeInt(staleRevisions.length);
    for (CDORevisionKey revKey : staleRevisions)
    {
      out.writeCDORevisionKey(revKey);
    }

    out.writeLong(result.getTimestamp());

    CDOLockState[] newLockStates = result.getNewLockStates();
    out.writeInt(newLockStates.length);
    for (CDOLockState lockState : newLockStates)
    {
      out.writeCDOLockState(lockState);
    }
  }
}
