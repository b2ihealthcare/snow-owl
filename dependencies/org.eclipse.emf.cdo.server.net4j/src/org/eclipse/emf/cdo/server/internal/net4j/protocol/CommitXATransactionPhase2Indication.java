/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.XATransactionCommitContext;
import org.eclipse.emf.cdo.internal.server.XATransactionCommitContext.CommitState;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.concurrent.ExecutionException;

/**
 * @author Simon McDuff
 */
public class CommitXATransactionPhase2Indication extends CommitTransactionIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL,
      CommitXATransactionPhase2Indication.class);

  public CommitXATransactionPhase2Indication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_XA_COMMIT_TRANSACTION_PHASE2);
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    initializeCommitContext(in);
    XATransactionCommitContext xaContextContext = (XATransactionCommitContext)commitContext;

    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Receiving {0} mapping informations", size); //$NON-NLS-1$
    }

    for (int i = 0; i < size; i++)
    {
      CDOIDTemp oldID = (CDOIDTemp)in.readCDOID();
      CDOID newID = in.readCDOID();
      xaContextContext.addIDMapping(oldID, newID);
    }

    // Mapping information from others CDOTransactions was added. Notify the commit process to continue.
    xaContextContext.getState().set(CommitState.APPLY_ID_MAPPING_DONE);
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    String exceptionMessage = null;

    try
    {
      // Return to the client only when the process is ready to commit
      getRepository().getCommitManager().waitForTermination(commitContext.getTransaction());
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
    catch (ExecutionException ex)
    {
      exceptionMessage = ex.getMessage();
    }

    if (exceptionMessage == null)
    {
      exceptionMessage = commitContext.getRollbackMessage();
    }

    respondingException(out, exceptionMessage, null);
  }

  @Override
  protected void initializeCommitContext(CDODataInput in) throws Exception
  {
    int viewID = in.readInt();
    commitContext = getRepository().getCommitManager().get(getTransaction(viewID));
  }
}
