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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LoadMergeDataIndication extends CDOServerReadIndicationWithMonitoring
{
  private int infos;

  private CDORevisionAvailabilityInfo targetInfo;

  private CDORevisionAvailabilityInfo sourceInfo;

  private CDORevisionAvailabilityInfo targetBaseInfo;

  private CDORevisionAvailabilityInfo sourceBaseInfo;

  /**
   * @since Snow Owl 2.6
   */
	private String[] nsURIs;

  public LoadMergeDataIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_MERGE_DATA);
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    infos = in.readInt();
    final int nsURISize = in.readInt();
    
    nsURIs = new String[nsURISize];
    
    for (int i = 0; i < nsURISize; i++) {
    	nsURIs[i] = in.readUTF();
    }
    
    monitor.begin(infos);

    try
    {
      targetInfo = readRevisionAvailabilityInfo(in, monitor.fork());
      sourceInfo = readRevisionAvailabilityInfo(in, monitor.fork());

      if (infos > 2)
      {
        targetBaseInfo = readRevisionAvailabilityInfo(in, monitor.fork());
      }

      if (infos > 3)
      {
        sourceBaseInfo = readRevisionAvailabilityInfo(in, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private CDORevisionAvailabilityInfo readRevisionAvailabilityInfo(CDODataInput in, OMMonitor monitor)
      throws IOException
  {
    CDOBranchPoint branchPoint = in.readCDOBranchPoint();
    CDORevisionAvailabilityInfo info = new CDORevisionAvailabilityInfo(branchPoint);

    int size = in.readInt();
    monitor.begin(size);

    try
    {
      for (int i = 0; i < size; i++)
      {
        CDOID id = in.readCDOID();
        info.getAvailableRevisions().put(id, null);
        monitor.worked();
      }

      return info;
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    monitor.begin(2 + infos);

    try
    {
      InternalRepository repository = getRepository();
      Set<CDOID> ids = repository.getMergeData(targetInfo, sourceInfo, targetBaseInfo, sourceBaseInfo, nsURIs, monitor.fork());

      out.writeInt(ids.size());
      for (CDOID id : ids)
      {
        out.writeCDOID(id);
      }

      monitor.worked();

      Set<CDORevisionKey> writtenRevisions = new HashSet<CDORevisionKey>();
      writeRevisionAvailabilityInfo(out, targetInfo, writtenRevisions, monitor.fork());
      writeRevisionAvailabilityInfo(out, sourceInfo, writtenRevisions, monitor.fork());

      if (infos > 2)
      {
        writeRevisionAvailabilityInfo(out, targetBaseInfo, writtenRevisions, monitor.fork());
      }

      if (infos > 3)
      {
        writeRevisionAvailabilityInfo(out, sourceBaseInfo, writtenRevisions, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void writeRevisionAvailabilityInfo(final CDODataOutput out, CDORevisionAvailabilityInfo info,
      Set<CDORevisionKey> writtenRevisions, OMMonitor monitor) throws IOException
  {
    Collection<CDORevisionKey> revisions = info.getAvailableRevisions().values();
    for (Iterator<CDORevisionKey> it = revisions.iterator(); it.hasNext();)
    {
      CDORevisionKey key = it.next();
      if (key == null)
      {
        it.remove();
      }
    }

    int size = revisions.size();
    out.writeInt(size);
    monitor.begin(size);

    try
    {
      for (CDORevisionKey revision : revisions)
      {
        CDORevisionKey key = CDORevisionUtil.copyRevisionKey(revision);
        if (writtenRevisions.add(key))
        {
          out.writeBoolean(true);
          out.writeCDORevision((CDORevision)revision, CDORevision.UNCHUNKED); // Exposes revision to client side
        }
        else
        {
          out.writeBoolean(false);
          out.writeCDORevisionKey(key);
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }
}
