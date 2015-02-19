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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadBranchesRequest extends CDOClientRequest<Integer>
{
  private int startID;

  private int endID;

  private CDOBranchHandler handler;

  public LoadBranchesRequest(CDOClientProtocol protocol, int startID, int endID, CDOBranchHandler handler)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_BRANCHES);
    this.startID = startID;
    this.endID = endID;
    this.handler = handler;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(startID);
    out.writeInt(endID);
  }

  @Override
  protected Integer confirming(CDODataInput in) throws IOException
  {
    int count = 0;
    while (in.readByte() == CDOProtocolConstants.REPLICATE_BRANCH)
    {
      CDOBranch branch = in.readCDOBranch();
      handler.handleBranch(branch);
      ++count;
    }

    return count;
  }
}
