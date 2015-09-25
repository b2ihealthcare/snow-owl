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
package org.eclipse.emf.cdo.internal.common.commit;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointImpl;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.net4j.util.CheckUtil;

/**
 * @author Eike Stepper
 */
public class CDOCommitInfoImpl extends CDOBranchPointImpl implements CDOCommitInfo
{
  private InternalCDOCommitInfoManager commitInfoManager;

  private long previousTimeStamp;

  private String userID;

  private String comment;

  private CDOCommitData commitData;

  public CDOCommitInfoImpl(InternalCDOCommitInfoManager commitInfoManager, CDOBranch branch, long timeStamp,
      long previousTimeStamp, String userID, String comment, CDOCommitData commitData)
  {
    super(branch, timeStamp);
    CheckUtil.checkArg(commitInfoManager, "commitInfoManager"); //$NON-NLS-1$
    this.commitInfoManager = commitInfoManager;
    this.previousTimeStamp = previousTimeStamp;
    this.userID = userID;
    this.comment = comment;
    this.commitData = commitData;
  }

  public InternalCDOCommitInfoManager getCommitInfoManager()
  {
    return commitInfoManager;
  }

  public long getPreviousTimeStamp()
  {
    return previousTimeStamp;
  }

  public String getUserID()
  {
    return userID;
  }

  public String getComment()
  {
    return comment;
  }

  public boolean isEmpty()
  {
    return false;
  }

  public CDOChangeSetData copy()
  {
    return commitData == null ? null : commitData.copy();
  }

  public void merge(CDOChangeSetData changeSetData)
  {
    loadCommitDataIfNeeded();
    commitData.merge(changeSetData);
  }

  public synchronized List<CDOPackageUnit> getNewPackageUnits()
  {
    loadCommitDataIfNeeded();
    return commitData.getNewPackageUnits();
  }

  public synchronized List<CDOIDAndVersion> getNewObjects()
  {
    loadCommitDataIfNeeded();
    return commitData.getNewObjects();
  }

  public synchronized List<CDORevisionKey> getChangedObjects()
  {
    loadCommitDataIfNeeded();
    return commitData.getChangedObjects();
  }

  public synchronized List<CDOIDAndVersion> getDetachedObjects()
  {
    loadCommitDataIfNeeded();
    return commitData.getDetachedObjects();
  }

  public Map<CDOID, CDOChangeKind> getChangeKinds()
  {
    loadCommitDataIfNeeded();
    return commitData.getChangeKinds();
  }

  public CDOChangeKind getChangeKind(CDOID id)
  {
    loadCommitDataIfNeeded();
    return commitData.getChangeKind(id);
  }

  @Override
  public String toString()
  {
    String data = null;
    if (commitData != null)
    {
      data = commitData.toString();
    }

    String timeStamp = CDOCommonUtil.formatTimeStamp(getTimeStamp());
    return MessageFormat
        .format(
            "CommitInfo[{0}, {1}, {2}, {3}, {4}, {5}]", getPreviousTimeStamp(), getBranch(), timeStamp, getUserID(), getComment(), data); //$NON-NLS-1$
  }

  private void loadCommitDataIfNeeded()
  {
    if (commitData == null)
    {
      commitData = commitInfoManager.getCommitInfoLoader().loadCommitData(getTimeStamp());
    }
  }
}
