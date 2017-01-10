/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Mathieu Velten - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import java.io.IOException;

/**
 * @author Mathieu Velten
 */
public class RenameBranchIndication extends CDOServerWriteIndication
{
  private int branchID;

  private String newName;

  public RenameBranchIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_RENAME_BRANCH);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    branchID = in.readInt();
    newName = in.readString();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalCDOBranchManager branchManager = getRepository().getBranchManager();
    InternalCDOBranch branch = branchManager.getBranch(branchID);

    branchManager.renameBranch(branch, newName);
    InternalSessionManager sessionManager = getRepository().getSessionManager();
    sessionManager.sendBranchNotification(getSession(), branch, ChangeKind.RENAMED);
    out.writeBoolean(true);
  }
}
