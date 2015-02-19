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
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * @author Caspar De Groot
 * @since 4.1
 */
public class DefaultLocksChangedEvent extends Event implements CDOLockChangeInfo
{
  private static final long serialVersionUID = 1L;

  private final InternalCDOView sender;

  private final CDOLockChangeInfo lockChangeInfo;

  public DefaultLocksChangedEvent(INotifier notifier, InternalCDOView sender, CDOLockChangeInfo lockChangeInfo)
  {
    super(notifier);
    this.sender = sender;
    this.lockChangeInfo = lockChangeInfo;
  }

  public InternalCDOView getSender()
  {
    return sender;
  }

  public CDOBranch getBranch()
  {
    return lockChangeInfo.getBranch();
  }

  public long getTimeStamp()
  {
    return lockChangeInfo.getTimeStamp();
  }

  public CDOLockOwner getLockOwner()
  {
    return lockChangeInfo.getLockOwner();
  }

  public CDOLockState[] getLockStates()
  {
    return lockChangeInfo.getLockStates();
  }

  public Operation getOperation()
  {
    return lockChangeInfo.getOperation();
  }

  public LockType getLockType()
  {
    return lockChangeInfo.getLockType();
  }

  public boolean isInvalidateAll()
  {
    return lockChangeInfo.isInvalidateAll();
  }
}
