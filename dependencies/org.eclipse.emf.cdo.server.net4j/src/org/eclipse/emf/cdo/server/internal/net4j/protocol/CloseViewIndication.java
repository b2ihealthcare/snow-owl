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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CloseViewIndication extends CDOServerReadIndication
{
  public CloseViewIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CLOSE_VIEW);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    InternalSession session = getSession();
    IView view = session.getView(viewID);
    if (view != null)
    {
      view.close();
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(true);
  }
}
