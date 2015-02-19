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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class ObjectWriteAccessHandler implements WriteAccessHandler
{
  private boolean legacyModeEnabled;

  public ObjectWriteAccessHandler()
  {
  }

  public ObjectWriteAccessHandler(boolean legacyModeEnabled)
  {
    this.legacyModeEnabled = legacyModeEnabled;
  }

  public final boolean isLegacyModeEnabled()
  {
    return legacyModeEnabled;
  }

  protected final EObject[] getNewObjects(final TransactionCommitContext commitContext, final CDOView view)
  {
    InternalCDORevision[] newRevisions = commitContext.getNewObjects();
    EObject[] newObjects = new EObject[newRevisions.length];

    for (int i = 0; i < newRevisions.length; i++)
    {
      InternalCDORevision newRevision = newRevisions[i];
      CDOObject newObject = view.getObject(newRevision.getID());
      newObjects[i] = CDOUtil.getEObject(newObject);
    }

    return newObjects;
  }

  protected abstract CDOView getView(final TransactionCommitContext commitContext);

  protected final EObject[] getDirtyObjects(final TransactionCommitContext commitContext, final CDOView view)
  {
    InternalCDORevision[] dirtyRevisions = commitContext.getDirtyObjects();
    EObject[] dirtyObjects = new EObject[dirtyRevisions.length];

    for (int i = 0; i < dirtyRevisions.length; i++)
    {
      InternalCDORevision dirtyRevision = dirtyRevisions[i];
      CDOObject dirtyObject = view.getObject(dirtyRevision.getID());
      dirtyObjects[i] = CDOUtil.getEObject(dirtyObject);
    }

    return dirtyObjects;
  }

  public final void handleTransactionBeforeCommitting(ITransaction transaction,
      IStoreAccessor.CommitContext commitContext, OMMonitor monitor) throws RuntimeException
  {
    handleTransactionBeforeCommitting(monitor, (TransactionCommitContext)commitContext);
  }

  public final void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext,
      OMMonitor monitor)
  {
    handleTransactionAfterCommitted(monitor, (TransactionCommitContext)commitContext);
  }

  public void handleTransactionRollback(ITransaction transaction, CommitContext commitContext)
  {
    handleTransactionRollback((TransactionCommitContext)commitContext);
  }

  protected void handleTransactionBeforeCommitting(OMMonitor monitor, TransactionCommitContext commitContext)
      throws RuntimeException
  {
  }

  protected void handleTransactionAfterCommitted(OMMonitor monitor, TransactionCommitContext commitContext)
  {
  }

  protected void handleTransactionRollback(TransactionCommitContext commitContext)
  {
  }
}
