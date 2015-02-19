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
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * Asynchronously executes a delegate handler's pre-event methods. The delegate handler's code may access the
 * {@link CDOView view} without causing deadlocks.
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOAsyncTransactionHandler implements CDOTransactionHandler
{
  private CDOTransactionHandler delegate;

  public CDOAsyncTransactionHandler(CDOTransactionHandler delegate)
  {
    this.delegate = delegate;
  }

  /**
   * Asynchronously executes the delegate handler's {@link #attachingObject(CDOTransaction, CDOObject)
   * attachingObject()} method.
   */
  public final void attachingObject(final CDOTransaction transaction, final CDOObject object)
  {
    runAsync(new Runnable()
    {
      public void run()
      {
        delegate.attachingObject(transaction, object);
      }
    });
  }

  /**
   * Asynchronously executes the delegate handler's {@link #detachingObject(CDOTransaction, CDOObject)
   * detachingObject()} method.
   */
  public final void detachingObject(final CDOTransaction transaction, final CDOObject object)
  {
    runAsync(new Runnable()
    {
      public void run()
      {
        delegate.detachingObject(transaction, object);
      }
    });
  }

  /**
   * Asynchronously executes the delegate handler's {@link #modifyingObject(CDOTransaction, CDOObject, CDOFeatureDelta)
   * modifyingObject()} method.
   */
  public final void modifyingObject(final CDOTransaction transaction, final CDOObject object,
      final CDOFeatureDelta featureChange)
  {
    runAsync(new Runnable()
    {
      public void run()
      {
        delegate.modifyingObject(transaction, object, featureChange);
      }
    });
  }

  /**
   * Asynchronously executes the delegate handler's {@link #committingTransaction(CDOTransaction, CDOCommitContext)
   * committingTransaction()} method.
   */
  public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    delegate.committingTransaction(transaction, commitContext);
  }

  /**
   * Synchronously executes the delegate handler's {@link #committedTransaction(CDOTransaction, CDOCommitContext)
   * committedTransaction()} method.
   */
  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    delegate.committedTransaction(transaction, commitContext);
  }

  /**
   * Synchronously executes the delegate handler's {@link #rolledBackTransaction(CDOTransaction)
   * rolledBackTransaction()} method.
   */
  public void rolledBackTransaction(CDOTransaction transaction)
  {
    delegate.rolledBackTransaction(transaction);
  }

  /**
   * Should be overridden if you want to use different threading mechanism.
   */
  protected void runAsync(Runnable runnable)
  {
    new Thread(runnable).start();
  }
}
