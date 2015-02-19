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

import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ReplicateRepositoryRequest extends CDOClientRequest<Boolean>
{
  private CDOReplicationContext context;

  public ReplicateRepositoryRequest(CDOClientProtocol protocol, CDOReplicationContext context, OMMonitor monitor)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REPLICATE_REPOSITORY);
    this.context = context;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(context.getLastReplicatedBranchID());
    out.writeLong(context.getLastReplicatedCommitTime());

    String[] lockAreaIDs = context.getLockAreaIDs();
    out.writeInt(lockAreaIDs.length);
    for (int i = 0; i < lockAreaIDs.length; i++)
    {
      out.writeString(lockAreaIDs[i]);
    }
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    for (;;)
    {
      byte opcode = in.readByte();
      switch (opcode)
      {
      case CDOProtocolConstants.REPLICATE_FINISHED:
        return true;

      case CDOProtocolConstants.REPLICATE_BRANCH:
        context.handleBranch(in.readCDOBranch());
        break;

      case CDOProtocolConstants.REPLICATE_COMMIT:
        context.handleCommitInfo(in.readCDOCommitInfo());
        break;

      case CDOProtocolConstants.REPLICATE_LOCKAREA:
        boolean missing = !in.readBoolean();
        if (missing)
        {
          String missingLockAreaID = in.readString();
          LockArea area = CDOLockUtil.createLockArea(missingLockAreaID);
          context.handleLockArea(area);
        }
        else
        {
          context.handleLockArea(in.readCDOLockArea());
        }
        break;

      default:
        throw new IllegalStateException("Invalid replicate opcode: " + opcode);
      }
    }
  }
}
