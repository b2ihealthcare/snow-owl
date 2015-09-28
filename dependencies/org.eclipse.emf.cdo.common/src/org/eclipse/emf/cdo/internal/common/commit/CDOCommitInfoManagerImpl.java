/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * @author Andre Dietisheim
 */
public class CDOCommitInfoManagerImpl extends Lifecycle implements InternalCDOCommitInfoManager
{
  private CommitInfoLoader commitInfoLoader;

  public CDOCommitInfoManagerImpl()
  {
  }

  public CommitInfoLoader getCommitInfoLoader()
  {
    return commitInfoLoader;
  }

  public void setCommitInfoLoader(CommitInfoLoader commitInfoLoader)
  {
    checkInactive();
    this.commitInfoLoader = commitInfoLoader;
  }

  public CDOCommitInfo createCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, CDOCommitData commitData)
  {
    checkActive();
    return new CDOCommitInfoImpl(this, branch, timeStamp, previousTimeStamp, userID, comment, commitData);
  }

  public CDOCommitInfo getCommitInfo(long timeStamp)
  {
    checkActive();
    final CDOCommitInfo[] result = { null };
    getCommitInfos(null, timeStamp, timeStamp, new CDOCommitInfoHandler()
    {
      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        result[0] = commitInfo;
      }
    });

    return result[0];
  }

  public void getCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    checkActive();
    commitInfoLoader.loadCommitInfos(branch, startTime, endTime, handler);
  }

  public void getCommitInfos(CDOBranch branch, long startTime, String userID, String comment, int count,
      CDOCommitInfoHandler handler)
  {
    checkActive();

    // TODO: implement CDOCommitInfoManagerImpl.getCommitInfos(branch, startTime, userID, comment, count, handler)
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(commitInfoLoader, "commitInfoLoader"); //$NON-NLS-1$
  }
}
