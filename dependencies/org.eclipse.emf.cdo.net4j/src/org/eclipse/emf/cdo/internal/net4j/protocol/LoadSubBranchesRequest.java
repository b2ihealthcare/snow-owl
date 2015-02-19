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
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.SubBranchInfo;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadSubBranchesRequest extends CDOClientRequest<SubBranchInfo[]>
{
  private int branchID;

  public LoadSubBranchesRequest(CDOClientProtocol protocol, int branchID)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_SUB_BRANCHES);
    this.branchID = branchID;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(branchID);
  }

  @Override
  protected SubBranchInfo[] confirming(CDODataInput in) throws IOException
  {
    int size = in.readInt();
    SubBranchInfo[] infos = new SubBranchInfo[size];
    for (int i = 0; i < infos.length; i++)
    {
      infos[i] = new SubBranchInfo(in);
    }

    return infos;
  }
}
