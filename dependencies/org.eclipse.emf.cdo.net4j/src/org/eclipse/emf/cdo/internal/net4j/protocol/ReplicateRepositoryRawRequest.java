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

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.CDORawReplicationContext;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ReplicateRepositoryRawRequest extends CDOClientRequestWithMonitoring<Boolean>
{
  private CDORawReplicationContext context;

  public ReplicateRepositoryRawRequest(CDOClientProtocol protocol, CDORawReplicationContext context)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REPLICATE_REPOSITORY_RAW);
    this.context = context;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    out.writeInt(context.getLastReplicatedBranchID());
    out.writeLong(context.getLastReplicatedCommitTime());
  }

  @Override
  protected Boolean confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    context.replicateRaw(in, monitor);
    return true;
  }
}
