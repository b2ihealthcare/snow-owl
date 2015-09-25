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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;

import org.eclipse.net4j.util.WrappedException;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadCommitInfosIndication extends CDOServerReadIndication
{
  private CDOBranch branch;

  private long startTime;

  private long endTime;

  public LoadCommitInfosIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_COMMIT_INFOS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    branch = in.readBoolean() ? in.readCDOBranch() : null;
    startTime = in.readLong();
    endTime = in.readLong();
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    try
    {
      InternalCDOCommitInfoManager manager = getRepository().getCommitInfoManager();
      manager.getCommitInfos(branch, startTime, endTime, new CDOCommitInfoHandler()
      {
        public void handleCommitInfo(CDOCommitInfo commitInfo)
        {
          try
          {
            out.writeBoolean(true);
            out.writeLong(commitInfo.getPreviousTimeStamp());
            if (branch == null)
            {
              out.writeCDOBranch(commitInfo.getBranch());
            }

            out.writeLong(commitInfo.getTimeStamp());
            out.writeString(commitInfo.getUserID());
            out.writeString(commitInfo.getComment());
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      });
    }
    catch (RuntimeException ex)
    {
      Exception unwrapped = WrappedException.unwrap(ex);
      if (unwrapped instanceof IOException)
      {
        throw (IOException)unwrapped;

      }

      throw ex;
    }

    out.writeBoolean(false);
  }
}
