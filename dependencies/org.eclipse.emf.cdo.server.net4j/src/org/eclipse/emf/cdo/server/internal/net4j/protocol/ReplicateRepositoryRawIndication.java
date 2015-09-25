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

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ReplicateRepositoryRawIndication extends CDOServerReadIndicationWithMonitoring
{
  private int lastReplicatedBranchID;

  private long lastReplicatedCommitTime;

  public ReplicateRepositoryRawIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REPLICATE_REPOSITORY_RAW);
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws IOException
  {
    try
    {
      monitor.begin();

      lastReplicatedBranchID = in.readInt();
      lastReplicatedCommitTime = in.readLong();
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    try
    {
      monitor.begin();
      Async async = monitor.forkAsync();

      try
      {
        getRepository().replicateRaw(out, lastReplicatedBranchID, lastReplicatedCommitTime);
      }
      finally
      {
        async.stop();
      }
    }
    finally
    {
      monitor.done();
    }
  }
}
