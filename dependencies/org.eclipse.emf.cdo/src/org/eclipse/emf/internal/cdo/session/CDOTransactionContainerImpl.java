/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionContainer;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class CDOTransactionContainerImpl extends CDOViewContainerImpl implements CDOTransactionContainer
{
  public CDOTransactionContainerImpl()
  {
  }

  public InternalCDOTransaction getTransaction(int viewID)
  {
    CDOView view = getView(viewID);
    if (view instanceof InternalCDOTransaction)
    {
      return (InternalCDOTransaction)view;
    }

    return null;
  }

  public InternalCDOTransaction[] getTransactions()
  {
    checkActive();
    List<InternalCDOTransaction> result = new ArrayList<InternalCDOTransaction>();

    for (InternalCDOView view : getViews())
    {
      if (view instanceof InternalCDOTransaction)
      {
        result.add((InternalCDOTransaction)view);
      }
    }

    return result.toArray(new InternalCDOTransaction[result.size()]);
  }

  public CDOTransaction openTransaction(CDOBranchPoint target, ResourceSet resourceSet)
  {
    checkArg(target.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE, "Target is not head of a branch: " + target);
    return null;
  }

  public CDOTransaction openTransaction(CDOBranchPoint target)
  {
    return openTransaction(target, createResourceSet());
  }

  public InternalCDOTransaction openTransaction(CDOBranch branch, ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOTransaction transaction = createTransaction(branch);
    initView(transaction, resourceSet);
    return transaction;
  }

  public InternalCDOTransaction openTransaction(ResourceSet resourceSet)
  {
    return openTransaction(getMainBranch(), resourceSet);
  }

  public InternalCDOTransaction openTransaction(CDOBranch branch)
  {
    return openTransaction(branch, createResourceSet());
  }

  /**
   * @since 2.0
   */
  public InternalCDOTransaction openTransaction()
  {
    return openTransaction(getMainBranch());
  }

  public CDOTransaction openTransaction(String durableLockingID)
  {
    return openTransaction(durableLockingID, createResourceSet());
  }

  public CDOTransaction openTransaction(String durableLockingID, ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOTransaction transaction = createTransaction(durableLockingID);
    initView(transaction, resourceSet);
    return transaction;
  }

  /**
   * @since 2.0
   */
  protected InternalCDOTransaction createTransaction(CDOBranch branch)
  {
    return new CDOTransactionImpl(branch);
  }

  /**
   * @since 4.0
   */
  protected InternalCDOTransaction createTransaction(String durableLockingID)
  {
    return new CDOTransactionImpl(durableLockingID);
  }
}
