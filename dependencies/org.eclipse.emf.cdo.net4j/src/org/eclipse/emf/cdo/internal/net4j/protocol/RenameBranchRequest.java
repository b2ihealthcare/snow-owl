/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Mathieu Velten - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Mathieu Velten
 */
public class RenameBranchRequest extends CDOClientRequest<Boolean>
{
  private int branchID;

  private String newName;

  public RenameBranchRequest(CDOClientProtocol protocol, int branchID, String newName)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_RENAME_BRANCH);
    this.branchID = branchID;
    this.newName = newName;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(branchID);
    out.writeString(newName);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
