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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;

import org.eclipse.net4j.util.CheckUtil;

/**
 * Static methods that may help with classes related to repository synchronization.
 * 
 * @author Eike Stepper
 * @since 4.1
 */
public final class SyncingUtil
{
  private SyncingUtil()
  {
  }

  public static InternalView openViewWithLockArea(InternalSession session, InternalLockManager lockManager,
      CDOBranch viewedBranch, String lockAreaID)
  {
    LockArea lockArea;
    InternalView view;

    try
    {
      lockArea = lockManager.getLockArea(lockAreaID);

      // If we get here, the lockArea already exists.
      view = (InternalView)lockManager.openView(session, InternalSession.TEMP_VIEW_ID, true, lockAreaID);
    }
    catch (LockAreaNotFoundException e)
    {
      // If we get here, the lockArea does not yet exist, so we open
      // a view without a lockArea first, then create a lockArea with the given ID,
      // and associate it with the view.
      view = session.openView(InternalSession.TEMP_VIEW_ID, viewedBranch.getHead());
      lockArea = lockManager.createLockArea(view, lockAreaID);
      view.setDurableLockingID(lockAreaID);
    }

    CheckUtil.checkNull(lockAreaID, "lockAreaID");
    CheckUtil.checkNull(lockArea, "lockArea");
    CheckUtil.checkState(lockAreaID.equals(lockArea.getDurableLockingID()), "lockAreaID has incorrect value");

    return view;
  }
}
