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

import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A distributed (XA) transaction that can atomically commit the changes to multiple {@link ResourceSet resource sets},
 * each represented by a registered {@link CDOViewSet view set}.
 * 
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOXATransaction extends CDOUserTransaction
{
  public void add(CDOViewSet viewSet);

  public void remove(CDOViewSet viewSet);

  /**
   * see {@link CDOXATransaction#isAllowRequestFromTransactionEnabled()}
   */
  public void setAllowRequestFromTransactionEnabled(boolean allRequest);

  /**
   * Allow request that come from contains {@link CDOTransaction}. Default value is true.
   * <p>
   * If the value is true, cdoTransaction.commit() will call xaTransaction.commit and all {@link CDOXATransaction} part
   * of xaTransaction will be committed.
   * <p>
   * If the value is false, the user will receive an exception by calling cdoTransaction.commit(). The user can only
   * commit from {@link CDOXATransaction}.
   */
  public boolean isAllowRequestFromTransactionEnabled();

  /**
   * @since 3.0
   */
  public CDOXASavepoint setSavepoint();

  /**
   * @since 3.0
   */
  public CDOXASavepoint getLastSavepoint();
}
