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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Caspar De Groot
 */
public class UnlockDelegationRequest extends UnlockObjectsRequest
{
  private String lockAreaID;

  public UnlockDelegationRequest(CDOClientProtocol protocol, String lockAreaID, Collection<CDOID> objectIDs,
      LockType lockType, boolean recursive)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_UNLOCK_DELEGATION, 0, objectIDs, lockType, recursive);
    this.lockAreaID = lockAreaID;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeString(lockAreaID);
    super.requesting(out);
  }
}
