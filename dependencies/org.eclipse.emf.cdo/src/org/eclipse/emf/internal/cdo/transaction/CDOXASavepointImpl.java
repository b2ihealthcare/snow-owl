/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.transaction.CDOSavepoint;

import org.eclipse.emf.spi.cdo.InternalCDOXASavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction;

import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOXASavepointImpl extends CDOUserSavepointImpl implements InternalCDOXASavepoint
{
  private List<CDOSavepoint> savepoints;

  public CDOXASavepointImpl(InternalCDOXATransaction transaction, InternalCDOXASavepoint lastSavepoint)
  {
    super(transaction, lastSavepoint);
  }

  @Override
  public InternalCDOXATransaction getTransaction()
  {
    return (InternalCDOXATransaction)super.getTransaction();
  }

  @Override
  public InternalCDOXASavepoint getFirstSavePoint()
  {
    return (InternalCDOXASavepoint)super.getFirstSavePoint();
  }

  @Override
  public InternalCDOXASavepoint getNextSavepoint()
  {
    return (InternalCDOXASavepoint)super.getNextSavepoint();
  }

  @Override
  public InternalCDOXASavepoint getPreviousSavepoint()
  {
    return (InternalCDOXASavepoint)super.getPreviousSavepoint();
  }

  public List<CDOSavepoint> getSavepoints()
  {
    return savepoints;
  }

  public void setSavepoints(List<CDOSavepoint> savepoints)
  {
    this.savepoints = savepoints;
  }

  public void rollback()
  {
    getTransaction().rollback(this);
  }
}
