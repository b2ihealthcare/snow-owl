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
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Provides functionality that is common to both {@link CDOTransaction single} transactions and {@link CDOXATransaction
 * distributed} (XA) transactions.
 * 
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOUserTransaction
{
  /**
   * @since 3.0
   */
  public CDOCommitInfo commit() throws CommitException;

  /**
   * @since 3.0
   */
  public CDOCommitInfo commit(IProgressMonitor progressMonitor) throws CommitException;

  public void rollback();

  /**
   * Creates a save point in the {@link CDOTransaction} that can be used to roll back a part of the transaction
   * <p>
   * Save points do not involve the server side, everything is done on the client side.
   * <p>
   * 
   * @since 3.0
   */
  public CDOUserSavepoint setSavepoint();

  /**
   * @since 3.0
   */
  public CDOUserSavepoint getLastSavepoint();
}
