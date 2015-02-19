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

import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalFailoverParticipant;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

/**
 * @author Eike Stepper
 */
public class FailoverParticipant extends SynchronizableRepository implements InternalFailoverParticipant
{
  private boolean allowBackupCommits;

  public FailoverParticipant()
  {
    setState(OFFLINE);
  }

  public boolean isAllowBackupCommits()
  {
    return allowBackupCommits;
  }

  public void setAllowBackupCommits(boolean allowBackupCommits)
  {
    this.allowBackupCommits = allowBackupCommits;
  }

  @Override
  public void setType(Type type)
  {
    checkArg(type == MASTER || type == BACKUP, "Type must be MASTER or BACKUP");
    super.setType(type);
  }

  @Override
  protected void changingType(Type oldType, Type newType)
  {
    if (isActive())
    {
      if (newType == MASTER)
      {
        // Switch off synchronizer
        doStopSynchronization();
      }
      else
      {
        // Bug 312879
        setReplicationCountersToLatest();

        // Switch on synchronizer
        doStartSynchronization();
      }
    }

    super.changingType(oldType, newType);
  }

  @Override
  protected void initRootResource()
  {
    if (getType() == BACKUP)
    {
      super.initRootResource();
    }
    else
    {
      doInitRootResource();
    }
  }

  protected void doStartSynchronization()
  {
    super.startSynchronization();
  }

  protected void doStopSynchronization()
  {
    super.stopSynchronization();
  }

  @Override
  protected void startSynchronization()
  {
    if (getType() == BACKUP)
    {
      doStartSynchronization();
    }
  }

  @Override
  protected void stopSynchronization()
  {
    if (getType() == BACKUP)
    {
      doStopSynchronization();
    }
  }

  @Override
  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    if (getType() == BACKUP)
    {
      if (getState() != ONLINE)
      {
        throw new IllegalStateException("Backup repository is not online");
      }

      if (allowBackupCommits || transaction.getSession() == getReplicatorSession())
      {
        return createWriteThroughCommitContext(transaction);
      }

      throw new IllegalStateException(
          "Only the repository synchronizer is allowed to commit transactions to a backup repository");
    }

    return createNormalCommitContext(transaction);
  }
}
