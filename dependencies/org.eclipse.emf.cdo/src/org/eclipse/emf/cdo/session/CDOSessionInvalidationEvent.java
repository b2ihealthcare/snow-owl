/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.CDOInvalidationNotification;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * A {@link CDOSessionEvent session event} fired when passive updates (commit notifications) are being received from a
 * remote repository. {@link CDOSession.Options#setPassiveUpdateEnabled(boolean) Passive updates} must be enabled for
 * this event to be fired.
 * 
 * @author Eike Stepper
 * @see CDOInvalidationNotification
 * @see CDOAdapterPolicy
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSessionInvalidationEvent extends CDOSessionEvent, CDOCommitInfo
{
  public static final long LOCAL_ROLLBACK = CDORevision.UNSPECIFIED_DATE;

  /**
   * Returns the transaction that was committed and thereby caused this event to be emitted if this transaction is
   * local, or <code>null</code> if the transaction was remote.
   * 
   * @since 4.0
   */
  public CDOTransaction getLocalTransaction();

  /**
   * @deprecated Use {@link #getLocalTransaction()}.
   */
  @Deprecated
  public CDOView getView();

  /**
   * @since 3.0
   */
  public boolean isRemote();
}
