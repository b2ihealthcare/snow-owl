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

import java.util.Map;
import java.util.Random;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockAreaImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockChangeInfoImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockOwnerImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockStateImpl;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

/**
 * Various static methods that may help with classes related to CDO locks.
 * 
 * @author Caspar De Groot
 * @since 4.1
 */
public final class CDOLockUtil
{
  private static final int DURABLE_SESSION_ID = 0;

  private static final int DURABLE_VIEW_ID = 0;

  private CDOLockUtil()
  {
  }

  public static CDOLockState copyLockState(CDOLockState lockState)
  {
    CheckUtil.checkArg(lockState instanceof CDOLockStateImpl, "lockState instanceof CDOLockStateImpl");
    return ((CDOLockStateImpl)lockState).copy();
  }

  public static CDOLockState createLockState(Object target)
  {
    return new CDOLockStateImpl(target);
  }

  public static CDOLockState createLockState(LockState<Object, ? extends CDOCommonView> lockState)
  {
    CheckUtil.checkArg(lockState, "lockState");
    InternalCDOLockState cdoLockState = new CDOLockStateImpl(lockState.getLockedObject());

    for (CDOCommonView view : lockState.getReadLockOwners())
    {
      String durableLockingID = view.getDurableLockingID();
      int sessionID, viewID;
      CDOCommonSession session = view.getSession();
      boolean isDurableView = session == null;
      if (isDurableView)
      {
        sessionID = DURABLE_SESSION_ID;
        viewID = DURABLE_VIEW_ID;
      }
      else
      {
        sessionID = session.getSessionID();
        viewID = view.getViewID();
      }

      CDOLockOwner owner = new CDOLockOwnerImpl(sessionID, viewID, durableLockingID, isDurableView);
      cdoLockState.addReadLockOwner(owner);
    }

    CDOCommonView writeLockOwner = lockState.getWriteLockOwner();
    if (writeLockOwner != null)
    {
      CDOLockOwner owner = createLockOwner(writeLockOwner);
      cdoLockState.setWriteLockOwner(owner);
    }

    CDOCommonView writeOptionOwner = lockState.getWriteOptionOwner();
    if (writeOptionOwner != null)
    {
      CDOLockOwner owner = createLockOwner(writeOptionOwner);
      cdoLockState.setWriteOptionOwner(owner);
    }

    return cdoLockState;
  }

  public static CDOLockOwner createLockOwner(CDOCommonView view)
  {
    CDOCommonSession session = view.getSession();
    String durableLockingID = view.getDurableLockingID();
    if (session != null)
    {
      int sessionID = session.getSessionID();
      int viewID = view.getViewID();
      return new CDOLockOwnerImpl(sessionID, viewID, durableLockingID, false);
    }

    CheckUtil.checkNull(durableLockingID, "durableLockingID");
    return new CDOLockOwnerImpl(DURABLE_SESSION_ID, DURABLE_VIEW_ID, durableLockingID, true);
  }

  public static CDOLockChangeInfo createLockChangeInfo(long timestamp, CDOLockOwner lockOwner, CDOBranch branch,
      Operation op, LockType lockType, CDOLockState[] cdoLockStates)
  {
    return new CDOLockChangeInfoImpl(branch.getPoint(timestamp), lockOwner, cdoLockStates, op, lockType);
  }

  public static CDOLockChangeInfo createLockChangeInfo()
  {
    return new CDOLockChangeInfoImpl();
  }

  public static CDOLockChangeInfo createLockChangeInfo(long timestamp, CDOCommonView view, CDOBranch viewedBranch,
      Operation op, LockType lockType, CDOLockState[] cdoLockStates)
  {
    CDOLockOwner lockOwner = createLockOwner(view);
    return createLockChangeInfo(timestamp, lockOwner, viewedBranch, op, lockType, cdoLockStates);
  }

  public static LockArea createLockArea(String durableLockingID, String userID, CDOBranchPoint branchPoint,
      boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    return new CDOLockAreaImpl(durableLockingID, userID, branchPoint, readOnly, locks);
  }

  public static LockArea createLockArea(String durableLockingID)
  {
    return new CDOLockAreaImpl(durableLockingID);
  }

  public static String createDurableLockingID()
  {
    return createDurableLockingID(LockArea.DEFAULT_DURABLE_LOCKING_ID_BYTES);
  }

  public static String createDurableLockingID(int bytes)
  {
    byte[] buffer = new byte[bytes];

    Random random = new Random(System.currentTimeMillis());
    random.nextBytes(buffer);

    return HexUtil.bytesToHex(buffer);
  }
}
