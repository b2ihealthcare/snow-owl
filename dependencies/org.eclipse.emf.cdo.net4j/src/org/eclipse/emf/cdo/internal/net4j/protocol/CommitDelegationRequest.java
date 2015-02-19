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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CommitDelegationRequest extends CommitTransactionRequest
{
  private CDOBranch branch;

  private String userID;

  private static final DelegationIDProvider delegationIDProvider = new DelegationIDProvider();

  public CommitDelegationRequest(CDOClientProtocol protocol, InternalCDOCommitContext context)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_DELEGATION, context);

    branch = context.getBranch();
    userID = context.getUserID();
  }

  @Override
  protected void requestingTransactionInfo(CDODataOutput out) throws IOException
  {
    out.writeCDOBranch(branch);
    out.writeString(userID);
  }

  @Override
  protected EClass getObjectType(CDOID id)
  {
    // The types of detached objects are delivered through the wire and don't need to be queried locally.
    throw new UnsupportedOperationException();
  }

  @Override
  protected CDOIDProvider getIDProvider()
  {
    return delegationIDProvider;
  }

  /**
   * @author Eike Stepper
   */
  private static class DelegationIDProvider implements CDOIDProvider
  {
    public CDOID provideCDOID(Object idOrObject)
    {
      return (CDOID)idOrObject;
    }
  }
}
