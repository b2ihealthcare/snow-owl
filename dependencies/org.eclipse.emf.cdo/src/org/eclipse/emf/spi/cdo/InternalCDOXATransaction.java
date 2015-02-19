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
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectExternalImpl;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;

import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOXATransaction extends CDOXATransaction, InternalCDOUserTransaction
{
  /**
   * @since 3.0
   */
  public InternalCDOXASavepoint setSavepoint();

  /**
   * @since 3.0
   */
  public InternalCDOXASavepoint getLastSavepoint();

  /**
   * @since 3.0
   */
  public void rollback(InternalCDOXASavepoint savepoint);

  /**
   * @since 3.0
   */
  public void add(InternalCDOTransaction transaction, CDOID object);

  /**
   * @since 3.0
   */
  public void add(InternalCDOTransaction transaction);

  /**
   * @since 3.0
   */
  public void remove(InternalCDOTransaction transaction);

  public InternalCDOXACommitContext getCommitContext(CDOTransaction transaction);

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public interface InternalCDOXACommitContext extends Callable<Object>, CDOIDProvider, InternalCDOCommitContext
  {
    public InternalCDOXATransaction getTransactionManager();

    public Map<CDOIDTempObjectExternalImpl, InternalCDOTransaction> getRequestedIDs();

    public CommitTransactionResult getResult();

    /**
     * @since 3.0
     */
    public void setResult(CommitTransactionResult result);

    /**
     * @since 3.0
     */
    public CDOXAState getState();

    /**
     * @since 3.0
     */
    public void setState(CDOXAState state);

    /**
     * @since 3.0
     */
    public void setProgressMonitor(IProgressMonitor progressMonitor);

    /**
     * @author Simon McDuff
     * @since 3.0
     */
    public static abstract class CDOXAState
    {
      public void check_result(CommitTransactionResult result)
      {
        if (result != null && result.getRollbackMessage() != null)
        {
          throw new TransactionException(result.getRollbackMessage());
        }
      }

      public abstract void handle(InternalCDOXACommitContext xaContext, IProgressMonitor progressMonitor)
          throws Exception;
    }
  }
}
