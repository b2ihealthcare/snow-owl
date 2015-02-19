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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.view.CDOView;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LockAreaRequest extends CDOClientRequest<String>
{
  private CDOView view;

  private boolean create;

  public LockAreaRequest(CDOClientProtocol protocol, CDOView view, boolean create)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_AREA);
    this.view = view;
    this.create = create;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(view.getViewID());
    out.writeBoolean(create);
  }

  @Override
  protected String confirming(CDODataInput in) throws IOException
  {
    return in.readString();
  }
}
