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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class ObjectLockedIndication extends CDOServerReadIndication
{
  private boolean isLocked;

  public ObjectLockedIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OBJECT_LOCKED);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    IView view = getSession().getView(viewID);
    InternalLockManager lockManager = getRepository().getLockingManager();

    LockType lockType = in.readCDOLockType();
    CDOID id = in.readCDOID();
    Object key = getRepository().isSupportingBranches() ? CDOIDUtil.createIDAndBranch(id, view.getBranch()) : id;

    boolean byOthers = in.readBoolean();
    if (byOthers)
    {
      isLocked = lockManager.hasLockByOthers(lockType, view, key);
    }
    else
    {
      isLocked = lockManager.hasLock(lockType, view, key);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(isLocked);
  }
}
