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
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.spi.server.SyncingUtil;

import java.io.IOException;

/**
 * @author Caspar De Groot
 */
public class LockDelegationIndication extends LockObjectsIndication
{
  private InternalView view;

  private String lockAreaID;

  private CDOBranch viewedBranch;

  public LockDelegationIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_DELEGATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    lockAreaID = in.readString();
    viewedBranch = in.readCDOBranch();
    super.indicating(in);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    try
    {
      super.responding(out);
    }
    finally
    {
      view.close();
    }
  }

  @Override
  protected IView getView(int viewID)
  {
    // The viewID received as an argument, is the ID of the client's view, which
    // does not exist on the master. So we ignore this argument and open a new
    // view instead.
    InternalLockManager lockManager = getRepository().getLockingManager();
    InternalSession session = getSession();
    view = SyncingUtil.openViewWithLockArea(session, lockManager, viewedBranch, lockAreaID);
    return view;
  }
}
