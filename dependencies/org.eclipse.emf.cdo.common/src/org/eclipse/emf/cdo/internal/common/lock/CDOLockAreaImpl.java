/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.emf.cdo.internal.common.lock;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;

/**
 * @author Eike Stepper
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 */
public class CDOLockAreaImpl implements LockArea
{
  public static final int DEFAULT_DURABLE_LOCKING_ID_BYTES = 32;

  private final String durableLockingID;

  private final String userID;

  private final CDOBranchPoint branchPoint;

  private final boolean readOnly;

  private final Map<CDOID, LockGrade> locks;

  private final boolean missing;

  public CDOLockAreaImpl(String durableLockingID, String userID, CDOBranchPoint branchPoint, boolean readOnly,
      Map<CDOID, LockGrade> locks)
  {
    this.durableLockingID = durableLockingID;
    this.userID = userID;
    this.branchPoint = branchPoint;
    this.readOnly = readOnly;
    this.locks = locks;
    missing = false;
  }

  public CDOLockAreaImpl(String durableLockingID)
  {
    this.durableLockingID = durableLockingID;
    userID = null;
    branchPoint = null;
    readOnly = false;
    locks = null;
    missing = true;
  }

  public String getDurableLockingID()
  {
    return durableLockingID;
  }

  public String getUserID()
  {
    return userID;
  }

  public CDOBranch getBranch()
  {
    return branchPoint.getBranch();
  }

  public long getTimeStamp()
  {
    return branchPoint.getTimeStamp();
  }

  public boolean isReadOnly()
  {
    return readOnly;
  }

  public Map<CDOID, LockGrade> getLocks()
  {
    return locks;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("DurableLockArea\nid={0}\nuser={1}\nbranchPoint={2}\nreadOnly={3}\nlocks={4}",
        durableLockingID, userID, branchPoint, readOnly, locks);
  }

  public boolean isMissing()
  {
    return missing;
  }
}
