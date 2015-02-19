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
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class HandleRevisionsRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, HandleRevisionsRequest.class);

  private EClass eClass;

  private CDOBranch branch;

  private boolean exactBranch;

  private long timeStamp;

  private boolean exactTime;

  private CDORevisionHandler handler;

  public HandleRevisionsRequest(CDOClientProtocol protocol, EClass eClass, CDOBranch branch, boolean exactBranch,
      long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_HANDLE_REVISIONS);
    this.eClass = eClass;
    this.branch = branch;
    this.exactBranch = exactBranch;
    this.timeStamp = timeStamp;
    this.exactTime = exactTime;
    this.handler = handler;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (eClass != null)
    {
      out.writeBoolean(true);
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing eClass: {0}", eClass); //$NON-NLS-1$
      }

      out.writeCDOClassifierRef(eClass);
    }
    else
    {
      out.writeBoolean(false);
    }

    if (branch != null)
    {
      out.writeBoolean(true);
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing branch: {0}", branch); //$NON-NLS-1$
      }

      out.writeCDOBranch(branch);
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing exactBranch: {0}", exactBranch); //$NON-NLS-1$
      }

      out.writeBoolean(exactBranch);
    }
    else
    {
      out.writeBoolean(false);
    }
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing timeStamp: {0}", CDOCommonUtil.formatTimeStamp(timeStamp)); //$NON-NLS-1$
    }

    out.writeLong(timeStamp);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing exactTime: {0}", exactTime); //$NON-NLS-1$
    }

    out.writeBoolean(exactTime);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    while (in.readBoolean())
    {
      CDORevision revision = in.readCDORevision();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read revision: {0}", revision); //$NON-NLS-1$
      }

      handler.handleRevision(revision);
    }

    return true;
  }
}
