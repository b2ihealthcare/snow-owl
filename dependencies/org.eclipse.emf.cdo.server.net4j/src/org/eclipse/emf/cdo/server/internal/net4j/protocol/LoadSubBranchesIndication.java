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
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.SubBranchInfo;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadSubBranchesIndication extends CDOServerReadIndication
{
  private int branchID;

  public LoadSubBranchesIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_SUB_BRANCHES);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    branchID = in.readInt();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalCDOBranchManager branchManager = getRepository().getBranchManager();
    InternalCDOBranch branch = branchManager.getBranch(branchID);
    InternalCDOBranch[] branches = branch.getBranches();
    out.writeInt(branches.length);
    for (InternalCDOBranch subBranch : branches)
    {
      SubBranchInfo info = new SubBranchInfo(subBranch.getID(), subBranch.getName(), subBranch.getBase().getTimeStamp());
      info.write(out);
    }
  }
}
