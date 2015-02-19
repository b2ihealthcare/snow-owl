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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.io.IOException;
import java.util.List;

/**
 * @author Caspar De Groot
 */
public class LockDelegationRequest extends LockObjectsRequest
{
  private String lockAreaID;

  private CDOBranch viewedBranch;

  public LockDelegationRequest(CDOClientProtocol protocol, String lockAreaID, List<CDORevisionKey> revisionKeys,
      CDOBranch viewedBranch, LockType lockType, boolean recursive, long timeout)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_DELEGATION, revisionKeys, 0, lockType, recursive, timeout);
    this.lockAreaID = lockAreaID;
    this.viewedBranch = viewedBranch;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeString(lockAreaID);
    out.writeCDOBranch(viewedBranch);
    super.requesting(out);
  }
}
