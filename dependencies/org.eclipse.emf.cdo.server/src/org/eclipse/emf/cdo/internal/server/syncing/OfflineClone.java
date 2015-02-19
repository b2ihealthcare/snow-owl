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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Eike Stepper
 */
public class OfflineClone extends SynchronizableRepository
{
  public OfflineClone()
  {
    setState(OFFLINE);
  }

  @Override
  public final Type getType()
  {
    return CLONE;
  }

  @Override
  public final void setType(Type type)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    CDOBranch branch = transaction.getBranch();
    if (branch.isLocal())
    {
      return createNormalCommitContext(transaction);
    }

    if (getState() != ONLINE)
    {
      return createBranchingCommitContext(transaction, branch);
    }

    return createWriteThroughCommitContext(transaction);
  }

  protected InternalCommitContext createBranchingCommitContext(InternalTransaction transaction, CDOBranch branch)
  {
    long[] times = createCommitTimeStamp(new Monitor());
    CDOBranch offlineBranch = createOfflineBranch(branch, times[0] - 1L);
    transaction.setBranchPoint(offlineBranch.getHead());
    return new BranchingCommitContext(transaction, times);
  }

  protected CDOBranch createOfflineBranch(CDOBranch baseBranch, long baseTimeStamp)
  {
    try
    {
      StoreThreadLocal.setSession(getReplicatorSession());
      InternalCDOBranchManager branchManager = getBranchManager();
      return branchManager.createBranch(NEW_LOCAL_BRANCH,
          "Offline-" + baseTimeStamp, (InternalCDOBranch)baseBranch, baseTimeStamp); //$NON-NLS-1$
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  /**
   * @author Eike Stepper
   */
  protected final class BranchingCommitContext extends TransactionCommitContext
  {
    private long[] times;

    public BranchingCommitContext(InternalTransaction transaction, long[] times)
    {
      super(transaction);
      this.times = times;
    }

    @Override
    protected void lockObjects() throws InterruptedException
    {
      // Do nothing
    }

    @Override
    protected long[] createTimeStamp(OMMonitor monitor)
    {
      return times;
    }
  }
}
