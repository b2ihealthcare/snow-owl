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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class AbstractChangeSetsConflictResolver extends AbstractConflictResolver
{
  private CDOTransactionHandler handler = new CDODefaultTransactionHandler()
  {
    @Override
    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta ignored)
    {
      if (getTransaction() == transaction)
      {
        adapter.attach(object);
      }
    }

    @Override
    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      if (getTransaction() == transaction)
      {
        adapter.reset();
        aggregator.reset();
      }
    }

    @Override
    public void rolledBackTransaction(CDOTransaction transaction)
    {
      // Reset the accumulation only if it rolled back the transaction completely
      if (getTransaction() == transaction && transaction.getLastSavepoint().getPreviousSavepoint() == null)
      {
        adapter.reset();
        aggregator.reset();
      }
    }
  };

  private CDOChangeSubscriptionAdapter adapter;

  private RemoteAggregator aggregator;

  public AbstractChangeSetsConflictResolver()
  {
  }

  public CDOChangeSetData getLocalChangeSetData()
  {
    return getTransaction().getChangeSetData();
  }

  public CDOChangeSet getLocalChangeSet()
  {
    CDOTransaction transaction = getTransaction();
    return CDORevisionUtil.createChangeSet(transaction, transaction, getLocalChangeSetData());
  }

  public CDOChangeSetData getRemoteChangeSetData()
  {
    return aggregator.getChangeSetData();
  }

  public CDOChangeSet getRemoteChangeSet()
  {
    CDOTransaction transaction = getTransaction();
    return CDORevisionUtil.createChangeSet(transaction, transaction, getRemoteChangeSetData());
  }

  @Override
  protected void hookTransaction(CDOTransaction transaction)
  {
    transaction.addTransactionHandler(handler);
    adapter = new CDOChangeSubscriptionAdapter(getTransaction());
    aggregator = new RemoteAggregator();
  }

  @Override
  protected void unhookTransaction(CDOTransaction transaction)
  {
    aggregator.dispose();
    aggregator = null;

    adapter.dispose();
    adapter = null;

    transaction.removeTransactionHandler(handler);
  }

  /**
   * @author Eike Stepper
   */
  private final class RemoteAggregator extends CDOSessionInvalidationAggregator
  {
    public RemoteAggregator()
    {
      super(getTransaction().getSession());
    }

    @Override
    protected void handleEvent(CDOSessionInvalidationEvent event) throws Exception
    {
      if (event.getBranch() == getTransaction().getBranch())
      {
        super.handleEvent(event);
      }
    }
  }
}
