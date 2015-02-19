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

import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Caspar De Groot
 */
public class LockNotificationRequest extends CDOServerRequest
{
  private CDOLockChangeInfo lockChangeInfo;

  public LockNotificationRequest(CDOServerProtocol serverProtocol, CDOLockChangeInfo lockChangeInfo)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_LOCK_NOTIFICATION);
    this.lockChangeInfo = lockChangeInfo;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOLockChangeInfo(lockChangeInfo);
  }
}
