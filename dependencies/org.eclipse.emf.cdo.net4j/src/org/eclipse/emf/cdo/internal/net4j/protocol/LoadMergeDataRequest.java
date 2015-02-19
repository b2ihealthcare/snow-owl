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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LoadMergeDataRequest extends CDOClientRequestWithMonitoring<Set<CDOID>>
{
  private CDORevisionAvailabilityInfo targetInfo;

  private CDORevisionAvailabilityInfo sourceInfo;

  private CDORevisionAvailabilityInfo targetBaseInfo;

  private CDORevisionAvailabilityInfo sourceBaseInfo;

  private int infos;

  private final String[] nsURIs;

  /**
   * @since Snow Owl 2.6
   * Added NS URI restriction
   */
  public LoadMergeDataRequest(CDOClientProtocol protocol, CDORevisionAvailabilityInfo targetInfo,
      CDORevisionAvailabilityInfo sourceInfo, CDORevisionAvailabilityInfo targetBaseInfo,
      CDORevisionAvailabilityInfo sourceBaseInfo, String... nsURIs)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_MERGE_DATA);
    this.targetInfo = targetInfo;
    this.sourceInfo = sourceInfo;
    this.targetBaseInfo = targetBaseInfo;
    this.sourceBaseInfo = sourceBaseInfo;
    infos = 2 + (targetBaseInfo != null ? 1 : 0) + (sourceBaseInfo != null ? 1 : 0);
    this.nsURIs = nsURIs;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    out.writeInt(infos);
    out.writeInt(nsURIs.length);

    for (int i = 0; i < nsURIs.length; i++)
    {
      out.writeUTF(nsURIs[i]);
    }

    monitor.begin(infos);

    try
    {
      writeRevisionAvailabilityInfo(out, targetInfo, monitor.fork());
      writeRevisionAvailabilityInfo(out, sourceInfo, monitor.fork());

      if (infos > 2)
      {
        writeRevisionAvailabilityInfo(out, targetBaseInfo, monitor.fork());
      }

      if (infos > 3)
      {
        writeRevisionAvailabilityInfo(out, sourceBaseInfo, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void writeRevisionAvailabilityInfo(CDODataOutput out, CDORevisionAvailabilityInfo info, OMMonitor monitor)
      throws IOException
  {
    Set<CDOID> availableRevisions = info.getAvailableRevisions().keySet();
    int size = availableRevisions.size();

    out.writeCDOBranchPoint(info.getBranchPoint());
    out.writeInt(size);

    monitor.begin(size);

    try
    {
      for (CDOID id : availableRevisions)
      {
        out.writeCDOID(id);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected Set<CDOID> confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    Set<CDOID> result = new HashSet<CDOID>();

    int size = in.readInt();
    monitor.begin(size + infos);

    try
    {
      for (int i = 0; i < size; i++)
      {
        CDOID id = in.readCDOID();
        result.add(id);
        monitor.worked();
      }

      readRevisionAvailabilityInfo(in, targetInfo, result, monitor.fork());
      readRevisionAvailabilityInfo(in, sourceInfo, result, monitor.fork());

      if (infos > 2)
      {
        readRevisionAvailabilityInfo(in, targetBaseInfo, result, monitor.fork());
      }

      if (infos > 3)
      {
        readRevisionAvailabilityInfo(in, sourceBaseInfo, result, monitor.fork());
      }

      return result;
    }
    finally
    {
      monitor.done();
    }
  }

  private void readRevisionAvailabilityInfo(CDODataInput in, CDORevisionAvailabilityInfo info, Set<CDOID> result,
      OMMonitor monitor) throws IOException
  {
    int size = in.readInt();
    monitor.begin(size + 1);

    try
    {
      for (int i = 0; i < size; i++)
      {
        CDORevision revision;
        if (in.readBoolean())
        {
          revision = in.readCDORevision();
        }
        else
        {
          CDORevisionKey key = in.readCDORevisionKey();
          revision = getRevision(key, targetInfo);

          if (revision == null && sourceInfo != null)
          {
            revision = getRevision(key, sourceInfo);
          }

          if (revision == null && targetBaseInfo != null)
          {
            revision = getRevision(key, targetBaseInfo);
          }

          if (revision == null)
          {
            throw new IllegalStateException("Missing revision: " + key);
          }
        }

        info.addRevision(revision);
        monitor.worked();
      }

      Set<Map.Entry<CDOID, CDORevisionKey>> entrySet = info.getAvailableRevisions().entrySet();
      for (Iterator<Map.Entry<CDOID, CDORevisionKey>> it = entrySet.iterator(); it.hasNext();)
      {
        Map.Entry<CDOID, CDORevisionKey> entry = it.next();
        if (!result.contains(entry.getKey()))
        {
          it.remove();
        }
      }

      monitor.worked();
    }
    finally
    {
      monitor.done();
    }
  }

  private CDORevision getRevision(CDORevisionKey key, CDORevisionAvailabilityInfo info)
  {
    CDORevisionKey revision = info.getRevision(key.getID());
    if (revision instanceof CDORevision)
    {
      if (key.equals(revision))
      {
        return (CDORevision)revision;
      }
    }

    return null;
  }
}
