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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

import java.io.IOException;

/**
 * @author Caspar De Groot
 */
public class LockStateIndication extends CDOServerReadIndication
{
  private CDOLockState[] cdoLockStates;

  public LockStateIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_STATE);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    InternalView view = getSession().getView(viewID);
    if (view == null)
    {
      throw new IllegalStateException("View not found");
    }

    InternalLockManager lockMgr = getRepository().getLockingManager();

    int n = in.readInt();
    cdoLockStates = new CDOLockState[n];
    for (int i = 0; i < n; i++)
    {
      Object key = indicatingCDOID(in, view.getBranch());
      LockState<Object, IView> lockState = lockMgr.getLockState(key);
      if (lockState != null)
      {
        cdoLockStates[i] = CDOLockUtil.createLockState(lockState);
      }
      else
      {
        cdoLockStates[i] = CDOLockUtil.createLockState(key);
      }
    }
  }

  private Object indicatingCDOID(CDODataInput in, CDOBranch viewedBranch) throws IOException
  {
    CDOID id = in.readCDOID();
    if (getRepository().isSupportingBranches())
    {
      return CDOIDUtil.createIDAndBranch(id, viewedBranch);
    }

    return id;
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeInt(cdoLockStates.length);
    for (CDOLockState lockState : cdoLockStates)
    {
      out.writeCDOLockState(lockState);
    }
  }
}
