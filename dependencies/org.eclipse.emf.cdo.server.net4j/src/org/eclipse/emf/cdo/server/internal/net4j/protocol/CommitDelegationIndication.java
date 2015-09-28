/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 213402
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Eike Stepper
 */
public class CommitDelegationIndication extends CommitTransactionIndication
{
  private InternalTransaction transaction;

  public CommitDelegationIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_DELEGATION);
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    super.indicating(in, monitor);
  }

  @Override
  protected void initializeCommitContext(CDODataInput in) throws Exception
  {
    CDOBranch branch = in.readCDOBranch();
    transaction = getSession().openTransaction(InternalSession.TEMP_VIEW_ID, branch.getHead());

    final String userID = in.readString();
    commitContext = new TransactionCommitContext(transaction)
    {
      @Override
      public String getUserID()
      {
        return userID;
      }
    };
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    try
    {
      super.responding(out, monitor);
    }
    finally
    {
      transaction.close();
    }
  }
}
