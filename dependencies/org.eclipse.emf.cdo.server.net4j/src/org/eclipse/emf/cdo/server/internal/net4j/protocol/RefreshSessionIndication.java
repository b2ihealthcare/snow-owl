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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.ObjectUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Simon McDuff
 */
public class RefreshSessionIndication extends CDOServerReadIndication
{
  private Map<CDOBranch, List<CDORevisionKey>> viewedRevisions = new HashMap<CDOBranch, List<CDORevisionKey>>();

  private long lastUpdateTime;

  private int initialChunkSize;

  private boolean enablePassiveUpdates;

  public RefreshSessionIndication(CDOServerProtocol protocol)
  {
    this(protocol, CDOProtocolConstants.SIGNAL_REFRESH_SESSION);
  }

  protected RefreshSessionIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  public Map<CDOBranch, List<CDORevisionKey>> getViewedRevisions()
  {
    return viewedRevisions;
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    lastUpdateTime = in.readLong();
    initialChunkSize = in.readInt();
    enablePassiveUpdates = in.readBoolean();

    int branches = in.readInt();
    for (int i = 0; i < branches; i++)
    {
      CDOBranch branch = in.readCDOBranch();
      List<CDORevisionKey> revisions = new ArrayList<CDORevisionKey>();
      viewedRevisions.put(branch, revisions);
      int size = in.readInt();
      for (int j = 0; j < size; j++)
      {
        CDORevisionKey revision = in.readCDORevisionKey();
        revisions.add(revision);
      }
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    long lastCommitTimeStamp = getRepository().getLastCommitTimeStamp();
    out.writeLong(lastCommitTimeStamp);

    writePackageUnits(out, lastCommitTimeStamp);
    writeRevisions(out);

    respondingDone();
  }

  protected void respondingDone()
  {
    InternalSession session = getSession();
    session.setPassiveUpdateEnabled(enablePassiveUpdates);
  }

  protected void writPackageUnit(CDODataOutput out, InternalCDOPackageUnit packageUnit) throws IOException
  {
    out.writeByte(CDOProtocolConstants.REFRESH_PACKAGE_UNIT);
    out.writeCDOPackageUnit(packageUnit, false);
  }

  /**
   * @deprecated Call {@link #writeChangedObject(CDODataOutput, InternalCDORevision, CDOBranchPoint)}
   */
  @Deprecated
  protected void writeChangedObject(CDODataOutput out, InternalCDORevision revision) throws IOException
  {
    writeChangedObject(out, revision, null);
  }

  protected void writeChangedObject(CDODataOutput out, InternalCDORevision revision, CDOBranchPoint securityContext)
      throws IOException
  {
    out.writeByte(CDOProtocolConstants.REFRESH_CHANGED_OBJECT);
    out.writeCDORevision(revision, initialChunkSize, securityContext); // Exposes revision to client side
  }

  protected void writeDetachedObject(CDODataOutput out, CDORevisionKey key) throws IOException
  {
    out.writeByte(CDOProtocolConstants.REFRESH_DETACHED_OBJECT);
    out.writeCDORevisionKey(key);
  }

  private void writePackageUnits(CDODataOutput out, long lastCommitTimeStamp) throws IOException
  {
    InternalCDOPackageRegistry packageRegistry = getRepository().getPackageRegistry();
    InternalCDOPackageUnit[] packageUnits = packageRegistry.getPackageUnits(lastUpdateTime + 1L, lastCommitTimeStamp);
    for (InternalCDOPackageUnit packageUnit : packageUnits)
    {
      writPackageUnit(out, packageUnit);
    }
  }

  private void writeRevisions(CDODataOutput out) throws IOException
  {
    InternalCDORevisionManager revisionManager = getRepository().getRevisionManager();
    SyntheticCDORevision[] synthetics = new SyntheticCDORevision[1];

    for (Entry<CDOBranch, List<CDORevisionKey>> entry : viewedRevisions.entrySet())
    {
      CDOBranch branch = entry.getKey();
      CDOBranchPoint head = branch.getHead();

      for (CDORevisionKey key : entry.getValue())
      {
        CDOID id = key.getID();
        synthetics[0] = null;
        InternalCDORevision revision = revisionManager.getRevision(id, head, CDORevision.UNCHUNKED,
            CDORevision.DEPTH_NONE, true, synthetics);

        if (revision == null)
        {
          writeDetachedObject(out, synthetics[0]);
        }
        else if (hasChanged(key, revision))
        {
          writeChangedObject(out, revision, head);
        }
      }
    }

    out.writeByte(CDOProtocolConstants.REFRESH_FINISHED);
  }

  private static boolean hasChanged(CDORevisionKey oldKey, CDORevisionKey newKey)
  {
    return !ObjectUtil.equals(oldKey.getBranch(), newKey.getBranch()) || oldKey.getVersion() != newKey.getVersion();
  }
}
