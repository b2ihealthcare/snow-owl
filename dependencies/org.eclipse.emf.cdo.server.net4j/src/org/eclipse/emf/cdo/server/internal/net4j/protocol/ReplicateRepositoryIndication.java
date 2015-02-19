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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;

import org.eclipse.net4j.util.WrappedException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ReplicateRepositoryIndication extends CDOServerReadIndication
{
  private int lastReplicatedBranchID;

  private long lastReplicatedCommitTime;

  private String[] lockAreaIDs;

  public ReplicateRepositoryIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REPLICATE_REPOSITORY);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    lastReplicatedBranchID = in.readInt();
    lastReplicatedCommitTime = in.readLong();
    lockAreaIDs = new String[in.readInt()];
    for (int i = 0; i < lockAreaIDs.length; i++)
    {
      lockAreaIDs[i] = in.readString();
    }
  }

  private Set<String> createLockAreaIDSet()
  {
    Set<String> idSet = new HashSet<String>(lockAreaIDs.length);
    for (String id : lockAreaIDs)
    {
      idSet.add(id);
    }
    return idSet;
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    // We will remove IDs from this set as we process lockAreas one by one;
    // what remains in this set at the end are the lockAreas that the client
    // has, but we don't have, which means that they were removed.
    //
    final Set<String> lockAreaIDSet = createLockAreaIDSet();

    getRepository().replicate(new CDOReplicationContext()
    {
      public int getLastReplicatedBranchID()
      {
        return lastReplicatedBranchID;
      }

      public long getLastReplicatedCommitTime()
      {
        return lastReplicatedCommitTime;
      }

      public String[] getLockAreaIDs()
      {
        return lockAreaIDs;
      }

      public void handleBranch(CDOBranch branch)
      {
        try
        {
          out.writeByte(CDOProtocolConstants.REPLICATE_BRANCH);
          out.writeCDOBranch(branch);
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        try
        {
          out.writeByte(CDOProtocolConstants.REPLICATE_COMMIT);
          out.writeCDOCommitInfo(commitInfo);
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      public boolean handleLockArea(LockArea lockArea)
      {
        try
        {
          out.writeByte(CDOProtocolConstants.REPLICATE_LOCKAREA);
          out.writeBoolean(true);
          out.writeCDOLockArea(lockArea);
          lockAreaIDSet.remove(lockArea.getDurableLockingID());
          return true;
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    });

    // The IDs that are still in the lockAreaIDSet, must be the IDs of lockAreas that have
    // been removed.
    for (String deletedLockAreaID : lockAreaIDSet)
    {
      out.writeByte(CDOProtocolConstants.REPLICATE_LOCKAREA);
      out.writeBoolean(false);
      out.writeString(deletedLockAreaID);
    }

    out.writeByte(CDOProtocolConstants.REPLICATE_FINISHED);
  }
}
