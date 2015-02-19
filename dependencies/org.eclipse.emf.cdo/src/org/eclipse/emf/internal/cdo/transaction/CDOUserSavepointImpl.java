/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.transaction.CDOUserSavepoint;

import org.eclipse.emf.spi.cdo.InternalCDOUserSavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOUserTransaction;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public abstract class CDOUserSavepointImpl implements InternalCDOUserSavepoint
{
  private InternalCDOUserTransaction transaction;

  private InternalCDOUserSavepoint previousSavepoint;

  private InternalCDOUserSavepoint nextSavepoint;

  public CDOUserSavepointImpl(InternalCDOUserTransaction transaction, InternalCDOUserSavepoint lastSavepoint)
  {
    this.transaction = transaction;
    previousSavepoint = lastSavepoint;
    if (previousSavepoint != null)
    {
      previousSavepoint.setNextSavepoint(this);
    }
  }

  public InternalCDOUserTransaction getTransaction()
  {
    return transaction;
  }

  public InternalCDOUserSavepoint getPreviousSavepoint()
  {
    return previousSavepoint;
  }

  public void setPreviousSavepoint(InternalCDOUserSavepoint previousSavepoint)
  {
    this.previousSavepoint = previousSavepoint;
  }

  public InternalCDOUserSavepoint getNextSavepoint()
  {
    return nextSavepoint;
  }

  public void setNextSavepoint(InternalCDOUserSavepoint nextSavepoint)
  {
    this.nextSavepoint = nextSavepoint;
  }

  public InternalCDOUserSavepoint getFirstSavePoint()
  {
    return previousSavepoint != null ? previousSavepoint.getFirstSavePoint() : this;
  }

  public boolean isValid()
  {
    InternalCDOUserSavepoint lastSavepoint = getTransaction().getLastSavepoint();
    for (InternalCDOUserSavepoint savepoint = lastSavepoint; savepoint != null; savepoint = savepoint
        .getPreviousSavepoint())
    {
      if (savepoint == this)
      {
        return true;
      }
    }

    return false;
  }

  public int getNumber()
  {
    int number = 1;
    CDOUserSavepoint savepoint = this;
    while ((savepoint = savepoint.getPreviousSavepoint()) != null)
    {
      ++number;
    }

    return number;
  }

  @Override
  public String toString()
  {
    return "Savepoint #" + getNumber();
  }
}
