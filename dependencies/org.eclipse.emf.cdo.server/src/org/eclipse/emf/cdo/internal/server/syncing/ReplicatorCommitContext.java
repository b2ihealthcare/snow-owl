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
package org.eclipse.emf.cdo.internal.server.syncing;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.List;

/**
 * TODO Optimize createCommitInfo()
 * 
 * @author Eike Stepper
 */
public final class ReplicatorCommitContext extends TransactionCommitContext
{
  private final CDOCommitInfo commitInfo;

  public ReplicatorCommitContext(InternalTransaction transaction, CDOCommitInfo commitInfo)
  {
    super(transaction);
    this.commitInfo = commitInfo;

    setCommitComment(commitInfo.getComment());

    InternalCDOPackageUnit[] newPackageUnits = getNewPackageUnits(commitInfo, getPackageRegistry());
    setNewPackageUnits(newPackageUnits);

    InternalCDORevision[] newObjects = getNewObjects(commitInfo);
    setNewObjects(newObjects);

    InternalCDORevisionDelta[] dirtyObjectDeltas = getDirtyObjectDeltas(commitInfo);
    setDirtyObjectDeltas(dirtyObjectDeltas);

    CDOID[] detachedObjects = getDetachedObjects(commitInfo);
    setDetachedObjects(detachedObjects);
  }

  @Override
  public String getUserID()
  {
    return commitInfo.getUserID();
  }

  @Override
  protected long[] createTimeStamp(OMMonitor monitor)
  {
    InternalRepository repository = getTransaction().getSession().getManager().getRepository();

    long commitTimeStamp = commitInfo.getTimeStamp();
    if (commitTimeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      commitTimeStamp = repository.getTimeStamp();
    }

    return repository.forceCommitTimeStamp(commitInfo.getTimeStamp(), monitor);
  }

  @Override
  protected void adjustForCommit()
  {
    // Do nothing
  }

  @Override
  public void applyIDMappings(OMMonitor monitor)
  {
    monitor.begin();

    try
    {
      notifyBeforeCommitting(monitor);
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected void lockObjects() throws InterruptedException
  {
    // Do nothing
  }

  @Override
  protected void checkXRefs()
  {
    // Do nothing
  }

  private static InternalCDOPackageUnit[] getNewPackageUnits(CDOCommitInfo commitInfo,
      InternalCDOPackageRegistry packageRegistry)
  {
    List<CDOPackageUnit> list = commitInfo.getNewPackageUnits();
    InternalCDOPackageUnit[] result = new InternalCDOPackageUnit[list.size()];

    int i = 0;
    for (CDOPackageUnit packageUnit : list)
    {
      result[i] = (InternalCDOPackageUnit)packageUnit;
      packageRegistry.putPackageUnit(result[i]);
      ++i;
    }

    return result;
  }

  private static InternalCDORevision[] getNewObjects(CDOCommitInfo commitInfo)
  {
    List<CDOIDAndVersion> list = commitInfo.getNewObjects();
    InternalCDORevision[] result = new InternalCDORevision[list.size()];

    int i = 0;
    for (CDOIDAndVersion revision : list)
    {
      result[i++] = (InternalCDORevision)revision;
    }

    return result;
  }

  private static InternalCDORevisionDelta[] getDirtyObjectDeltas(CDOCommitInfo commitInfo)
  {
    List<CDORevisionKey> list = commitInfo.getChangedObjects();
    InternalCDORevisionDelta[] result = new InternalCDORevisionDelta[list.size()];

    int i = 0;
    for (CDORevisionKey delta : list)
    {
      result[i++] = (InternalCDORevisionDelta)delta;
    }

    return result;
  }

  private static CDOID[] getDetachedObjects(CDOCommitInfo commitInfo)
  {
    List<CDOIDAndVersion> list = commitInfo.getDetachedObjects();
    CDOID[] result = new CDOID[list.size()];

    int i = 0;
    for (CDOIDAndVersion key : list)
    {
      result[i++] = key.getID();
    }

    return result;
  }
}
