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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class HandleRevisionsIndication extends CDOServerReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, HandleRevisionsIndication.class);

  private EClass eClass;

  private CDOBranch branch;

  private boolean exactBranch;

  private long timeStamp;

  private boolean exactTime;

  public HandleRevisionsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_HANDLE_REVISIONS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    if (in.readBoolean())
    {
      eClass = (EClass)in.readCDOClassifierRefAndResolve();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read eClass: {0}", eClass); //$NON-NLS-1$
      }
    }

    if (in.readBoolean())
    {
      branch = in.readCDOBranch();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read branch: {0}", branch); //$NON-NLS-1$
      }

      exactBranch = in.readBoolean();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read exactBranch: {0}", exactBranch); //$NON-NLS-1$
      }
    }

    timeStamp = in.readLong();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read timeStamp: {0}", CDOCommonUtil.formatTimeStamp(timeStamp)); //$NON-NLS-1$
    }

    exactTime = in.readBoolean();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read exactTime: {0}", exactTime); //$NON-NLS-1$
    }
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    final IOException[] ioException = { null };
    final RuntimeException[] runtimeException = { null };

    getRepository().handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime,
        new CDORevisionHandler.Filtered.Undetached(new CDORevisionHandler()
        {
          public boolean handleRevision(CDORevision revision)
          {
            try
            {
              out.writeBoolean(true);
              out.writeCDORevision(revision, CDORevision.UNCHUNKED); // Exposes revision to client side
              return true;
            }
            catch (IOException ex)
            {
              ioException[0] = ex;
            }
            catch (RuntimeException ex)
            {
              runtimeException[0] = ex;
            }

            return false;
          }
        }));

    if (ioException[0] != null)
    {
      throw ioException[0];
    }

    if (runtimeException[0] != null)
    {
      throw runtimeException[0];
    }

    out.writeBoolean(false);
  }
}
