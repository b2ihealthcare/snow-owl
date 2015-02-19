/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.io.IOException;

/**
 * Determine at which moment the server side can complete the transaction.
 * <p>
 * At this stage, everything on the database was done except to flush on the disk.
 * <p>
 * It is useful to assure that all {@link CDOTransaction} involve in that commit are synchronize.
 * 
 * @author Simon McDuff
 */
public class CommitXATransactionPhase3Request extends CommitXATransactionRequest
{
  public CommitXATransactionPhase3Request(CDOClientProtocol protocol, InternalCDOXACommitContext xaContext)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_XA_COMMIT_TRANSACTION_PHASE3, xaContext);
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    requestingTransactionInfo(out);
  }

  @Override
  protected CommitTransactionResult confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    CommitTransactionResult result = confirmingCheckError(in);
    if (result != null)
    {
      return result;
    }

    InternalCDOXACommitContext context = getCommitContext();
    return context.getResult();
  }
}
