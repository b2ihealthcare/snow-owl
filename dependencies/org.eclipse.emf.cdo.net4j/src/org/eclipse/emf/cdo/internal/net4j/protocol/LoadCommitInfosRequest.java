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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadCommitInfosRequest extends CDOClientRequest<Boolean>
{
  private CDOBranch branch;

  private long startTime;

  private long endTime;

  private CDOCommitInfoHandler handler;

  public LoadCommitInfosRequest(CDOClientProtocol protocol, CDOBranch branch, long startTime, long endTime,
      CDOCommitInfoHandler handler)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_COMMIT_INFOS);
    this.branch = branch;
    this.startTime = startTime;
    this.endTime = endTime;
    this.handler = handler;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (branch == null)
    {
      out.writeBoolean(false);
    }
    else
    {
      out.writeBoolean(true);
      out.writeCDOBranch(branch);
    }

    out.writeLong(startTime);
    out.writeLong(endTime);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    InternalCDOCommitInfoManager manager = getSession().getCommitInfoManager();
    while (in.readBoolean())
    {
      long id = in.readLong();
      CDOBranch branch = this.branch == null ? in.readCDOBranch() : this.branch;
      long timeStamp = in.readLong();
      String userID = in.readString();
      String comment = in.readString();

      try
      {
        CDOCommitInfo commitInfo = manager.createCommitInfo(branch, timeStamp, id, userID, comment, null);
        handler.handleCommitInfo(commitInfo);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    return true;
  }
}
