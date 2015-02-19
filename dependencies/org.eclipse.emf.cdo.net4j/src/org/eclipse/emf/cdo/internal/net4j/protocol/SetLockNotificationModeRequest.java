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

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Caspar De Groot
 */
public class SetLockNotificationModeRequest extends CDOClientRequest<Boolean>
{
  private LockNotificationMode mode;

  public SetLockNotificationModeRequest(CDOClientProtocol protocol, LockNotificationMode mode)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SET_LOCK_NOTIFICATION_MODE);
    this.mode = mode;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeEnum(mode);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
