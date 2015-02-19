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
package org.eclipse.net4j.util.transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class Transaction<CONTEXT> implements ITransaction<CONTEXT>
{
  private List<ITransactionalOperation<CONTEXT>> operations = new ArrayList<ITransactionalOperation<CONTEXT>>();

  private CONTEXT context;

  private boolean undoPhase1OnRollback;

  public Transaction(CONTEXT context, boolean undoPhase1OnRollback)
  {
    this.context = context;
    this.undoPhase1OnRollback = undoPhase1OnRollback;
  }

  public Transaction(CONTEXT context)
  {
    this(context, true);
  }

  public boolean isUndoPhase1OnRollback()
  {
    return undoPhase1OnRollback;
  }

  public boolean isActive()
  {
    return operations != null;
  }

  public CONTEXT getContext()
  {
    return context;
  }

  public void execute(ITransactionalOperation<CONTEXT> operation) throws TransactionException
  {
    if (!isActive())
    {
      throw new TransactionException("Transaction inactive"); //$NON-NLS-1$
    }

    try
    {
      operation.phase1(context);
      operations.add(operation);
    }
    catch (RuntimeException ex)
    {
      rollback();
      throw ex;
    }
    catch (Exception ex)
    {
      rollback();
      throw new TransactionException(ex);
    }
  }

  public void commit()
  {
    for (ITransactionalOperation<CONTEXT> operation : end())
    {
      operation.phase2(context);
    }
  }

  public void rollback()
  {
    if (undoPhase1OnRollback)
    {
      for (ITransactionalOperation<CONTEXT> operation : end())
      {
        operation.undoPhase1(context);
      }
    }
  }

  private List<ITransactionalOperation<CONTEXT>> end()
  {
    List<ITransactionalOperation<CONTEXT>> tmp = operations;
    operations = null;
    return tmp;
  }
}
