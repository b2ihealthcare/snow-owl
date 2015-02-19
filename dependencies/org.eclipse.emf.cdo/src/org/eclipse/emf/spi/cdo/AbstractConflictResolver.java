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

import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class AbstractConflictResolver implements CDOConflictResolver
{
  private CDOTransaction transaction;

  public AbstractConflictResolver()
  {
  }

  public CDOTransaction getTransaction()
  {
    return transaction;
  }

  public void setTransaction(CDOTransaction transaction)
  {
    if (this.transaction != transaction)
    {
      if (this.transaction != null)
      {
        unhookTransaction(this.transaction);
      }

      this.transaction = transaction;

      if (this.transaction != null)
      {
        hookTransaction(this.transaction);
      }
    }
  }

  protected void hookTransaction(CDOTransaction transaction)
  {
  }

  protected void unhookTransaction(CDOTransaction transaction)
  {
  }
}
