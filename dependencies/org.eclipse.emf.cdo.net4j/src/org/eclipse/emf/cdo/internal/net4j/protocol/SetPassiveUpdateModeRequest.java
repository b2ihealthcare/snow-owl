/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 230832
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class SetPassiveUpdateModeRequest extends CDOClientRequest<Boolean>
{
  private PassiveUpdateMode mode;

  public SetPassiveUpdateModeRequest(CDOClientProtocol protocol, PassiveUpdateMode mode)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SET_PASSIVE_UPDATE_MODE);
    this.mode = mode;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeByte(mode.ordinal());
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
