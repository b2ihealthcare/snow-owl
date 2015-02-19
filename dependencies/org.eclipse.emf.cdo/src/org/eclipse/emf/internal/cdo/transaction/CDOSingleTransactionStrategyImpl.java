/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ReferentialIntegrityException;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.CDOTransactionStrategy;
import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOUserSavepoint;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOSingleTransactionStrategyImpl implements CDOTransactionStrategy
{
  public static final CDOSingleTransactionStrategyImpl INSTANCE = new CDOSingleTransactionStrategyImpl();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION,
      CDOSingleTransactionStrategyImpl.class);

  public CDOSingleTransactionStrategyImpl()
  {
  }

  public CDOCommitInfo commit(InternalCDOTransaction transaction, IProgressMonitor progressMonitor) throws Exception
  {
    String comment = transaction.getCommitComment();
    InternalCDOCommitContext commitContext = transaction.createCommitContext();
    if (TRACER.isEnabled())
    {
      TRACER.format("CDOCommitContext.preCommit"); //$NON-NLS-1$
    }

    commitContext.preCommit();

    CDOCommitData commitData = commitContext.getCommitData();
    InternalCDOSession session = transaction.getSession();
    CommitTransactionResult result = null;

    OMMonitor monitor = new EclipseMonitor(progressMonitor);
    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    result = sessionProtocol.commitTransaction(commitContext, monitor);

    commitContext.postCommit(result);

    String rollbackMessage = result.getRollbackMessage();
    if (rollbackMessage != null)
    {
      List<CDOObjectReference> xRefs = result.getXRefs();
      if (xRefs != null)
      {
        throw new ReferentialIntegrityException(rollbackMessage, xRefs);
      }

      throw new CommitException(rollbackMessage);
    }

    transaction.setCommitComment(null);

    long previousTimeStamp = result.getPreviousTimeStamp();
    CDOBranch branch = transaction.getBranch();
    long timeStamp = result.getTimeStamp();
    String userID = session.getUserID();

    InternalCDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
    return commitInfoManager.createCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, commitData);
  }

  public void rollback(InternalCDOTransaction transaction, InternalCDOUserSavepoint savepoint)
  {
    transaction.handleRollback((InternalCDOSavepoint)savepoint);
  }

  public InternalCDOUserSavepoint setSavepoint(InternalCDOTransaction transaction)
  {
    return transaction.handleSetSavepoint();
  }

  public void setTarget(InternalCDOTransaction transaction)
  {
    // Do nothing
  }

  public void unsetTarget(InternalCDOTransaction transaction)
  {
    // Do nothing
  }
}
