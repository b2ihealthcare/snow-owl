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
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CreateBranchIndication extends CDOServerWriteIndication
{
  private int branchID;

  private BranchInfo branchInfo;

  public CreateBranchIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CREATE_BRANCH);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    branchID = in.readInt();
    branchInfo = new BranchInfo(in);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalCDOBranchManager branchManager = getRepository().getBranchManager();
    InternalCDOBranch baseBranch = branchManager.getBranch(branchInfo.getBaseBranchID());
    InternalCDOBranch branch = branchManager.createBranch(branchID, branchInfo.getName(), baseBranch,
        branchInfo.getBaseTimeStamp());

    InternalSessionManager sessionManager = getRepository().getSessionManager();
    sessionManager.sendBranchNotification(getSession(), branch);

    out.writeInt(branch.getID());
    out.writeLong(branch.getBase().getTimeStamp());
  }
}
