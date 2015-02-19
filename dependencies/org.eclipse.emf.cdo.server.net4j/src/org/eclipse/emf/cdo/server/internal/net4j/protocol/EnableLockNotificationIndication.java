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

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.server.InternalView;

import java.io.IOException;

/**
 * @author Caspar De Groot
 */
public class EnableLockNotificationIndication extends CDOServerIndication
{
  public EnableLockNotificationIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_ENABLE_LOCK_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    boolean enable = in.readBoolean();

    InternalView view = getSession().getView(viewID);
    view.options().setLockNotificationEnabled(enable);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(true);
  }
}
