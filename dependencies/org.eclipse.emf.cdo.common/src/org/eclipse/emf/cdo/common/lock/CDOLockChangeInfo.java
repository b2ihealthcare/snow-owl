/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lock;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

/**
 * Represents a change in the lock state of a set of objects. Instances are meant to be sent from the server to the
 * client for the purpose of notifying the latter.
 * 
 * @author Caspar De Groot
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.has {@link CDOLockOwner}
 * @apiviz.has {@link CDOLockChangeInfo.Operation}
 * @apiviz.has {@link org.eclipse.net4j.util.concurrent.IRWLockManager.LockType}
 * @apiviz.composedOf {@link CDOLockState}
 */
public interface CDOLockChangeInfo extends CDOBranchPoint
{
  /**
   * @return <code>true</code> if this instance signals that all {@link CDOLockState lockstates} must be invalidated,
   *         <code>false</code> otherwise
   */
  public boolean isInvalidateAll();

  /**
   * @return The branch at which the lock changes took place, same as <code>getView().getBranch()</code>.
   */
  public CDOBranch getBranch();

  /**
   * @return The repository time at which the lock changes took place. This is only an informal indication; no formal
   *         relation (e.g. an ordering) with commit timestamps is guaranteed.
   */
  public long getTimeStamp();

  /**
   * @return The view, represented as a {@link CDOLockOwner}, that authored the lock changes.
   */
  public CDOLockOwner getLockOwner();

  /**
   * @return The new lock states of the objects that were affected by the change
   */
  public CDOLockState[] getLockStates();

  /**
   * @return the type of lock operation that caused the lock changes
   */
  public Operation getOperation();

  /**
   * @return the type of locks that were affected by the lock operation
   */
  public LockType getLockType();

  /**
   * Enumerates the possible locking operations.
   * 
   * @author Caspar De Groot
   */
  public enum Operation
  {
    LOCK, UNLOCK
  }
}
