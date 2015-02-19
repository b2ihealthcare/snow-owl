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
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

import org.eclipse.net4j.util.collection.Pair;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CreateBranchRequest extends CDOClientRequest<Pair<Integer, Long>>
{
  private int branchID;

  private BranchInfo branchInfo;

  public CreateBranchRequest(CDOClientProtocol protocol, int branchID, BranchInfo branchInfo)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CREATE_BRANCH);
    this.branchID = branchID;
    this.branchInfo = branchInfo;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(branchID);
    branchInfo.write(out);
  }

  @Override
  protected Pair<Integer, Long> confirming(CDODataInput in) throws IOException
  {
    branchID = in.readInt();
    long baseTimeStamp = in.readLong();
    return new Pair<Integer, Long>(branchID, baseTimeStamp);
  }
}
