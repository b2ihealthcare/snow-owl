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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class OpenViewRequest extends CDOClientRequest<CDOBranchPoint>
{
  private int viewID;

  private boolean readOnly;

  private CDOBranchPoint branchPoint;

  private String durableLockingID;

  public OpenViewRequest(CDOClientProtocol protocol, int viewID, boolean readOnly, CDOBranchPoint branchPoint)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_VIEW);
    this.viewID = viewID;
    this.readOnly = readOnly;
    this.branchPoint = branchPoint;
  }

  public OpenViewRequest(CDOClientProtocol protocol, int viewID, boolean readOnly, String durableLockingID)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_VIEW);
    this.viewID = viewID;
    this.readOnly = readOnly;
    this.durableLockingID = durableLockingID;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(viewID);
    out.writeBoolean(readOnly);

    if (branchPoint != null)
    {
      out.writeBoolean(true);
      out.writeCDOBranchPoint(branchPoint);
    }
    else
    {
      out.writeBoolean(false);
      out.writeString(durableLockingID);
    }
  }

  @Override
  protected CDOBranchPoint confirming(CDODataInput in) throws IOException
  {
    if (in.readBoolean())
    {
      return in.readCDOBranchPoint();
    }

    if (durableLockingID != null)
    {
      String message = in.readString();
      if (message != null)
      {
        throw new IllegalStateException(message);
      }

      throw new LockAreaNotFoundException(durableLockingID);
    }

    return null;
  }
}
