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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class RefreshSessionRequest extends CDOClientRequest<RefreshSessionResult>
{
  private long lastUpdateTime;

  private Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions;

  private int initialChunkSize;

  private boolean enablePassiveUpdates;

  public RefreshSessionRequest(CDOClientProtocol protocol, long lastUpdateTime,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions, int initialChunkSize,
      boolean enablePassiveUpdates)
  {
    this(protocol, CDOProtocolConstants.SIGNAL_REFRESH_SESSION, lastUpdateTime, viewedRevisions, initialChunkSize,
        enablePassiveUpdates);
  }

  protected RefreshSessionRequest(CDOClientProtocol protocol, short signalID, long lastUpdateTime,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions, int initialChunkSize,
      boolean enablePassiveUpdates)
  {
    super(protocol, signalID);
    this.lastUpdateTime = lastUpdateTime;
    this.viewedRevisions = viewedRevisions;
    this.initialChunkSize = initialChunkSize;
    this.enablePassiveUpdates = enablePassiveUpdates;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeLong(lastUpdateTime);
    out.writeInt(initialChunkSize);
    out.writeBoolean(enablePassiveUpdates);

    out.writeInt(viewedRevisions.size());
    for (Entry<CDOBranch, Map<CDOID, InternalCDORevision>> entry : viewedRevisions.entrySet())
    {
      CDOBranch branch = entry.getKey();
      Map<CDOID, InternalCDORevision> revisions = entry.getValue();

      out.writeCDOBranch(branch);
      out.writeInt(revisions.size());
      for (InternalCDORevision revision : revisions.values())
      {
        out.writeCDORevisionKey(revision);
      }
    }
  }

  @Override
  protected RefreshSessionResult confirming(CDODataInput in) throws IOException
  {
    lastUpdateTime = in.readLong();
    RefreshSessionResult result = new RefreshSessionResult(lastUpdateTime);

    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet();
    for (;;)
    {
      byte type = in.readByte();
      switch (type)
      {
      case CDOProtocolConstants.REFRESH_PACKAGE_UNIT:
      {
        CDOPackageUnit packageUnit = in.readCDOPackageUnit(resourceSet);
        result.addPackageUnit(packageUnit);
        break;
      }

      case CDOProtocolConstants.REFRESH_CHANGED_OBJECT:
      {
        InternalCDORevision revision = (InternalCDORevision)in.readCDORevision();
        result.addChangedObject(revision);
        break;
      }

      case CDOProtocolConstants.REFRESH_DETACHED_OBJECT:
      {
        CDORevisionKey key = in.readCDORevisionKey();
        result.addDetachedObject(key);
        break;
      }

      case CDOProtocolConstants.REFRESH_FINISHED:
        return result;

      default:
        throw new IOException("Invalid refresh type: " + type);
      }
    }
  }
}
