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
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RepositoryTimeResult;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOTimeRequest<RESULT> extends CDOClientRequest<RESULT>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDOTimeRequest.class);

  private RepositoryTimeResult repositoryTimeResult = new RepositoryTimeResult();

  public CDOTimeRequest(CDOClientProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  public RepositoryTimeResult getRepositoryTimeResult()
  {
    return repositoryTimeResult;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    repositoryTimeResult.setRequested(System.currentTimeMillis());
    if (TRACER.isEnabled())
    {
      TRACER.format("Requested: {0}", CDOCommonUtil.formatTimeStamp(repositoryTimeResult.getRequested())); //$NON-NLS-1$
    }
  }

  @Override
  protected RESULT confirming(CDODataInput in) throws IOException
  {
    repositoryTimeResult.setConfirmed(System.currentTimeMillis());
    if (TRACER.isEnabled())
    {
      TRACER.format("Confirmed: {0}", CDOCommonUtil.formatTimeStamp(repositoryTimeResult.getConfirmed())); //$NON-NLS-1$
    }

    repositoryTimeResult.setIndicated(in.readLong());
    if (TRACER.isEnabled())
    {
      TRACER.format("Read indicated: {0}", CDOCommonUtil.formatTimeStamp(repositoryTimeResult.getIndicated())); //$NON-NLS-1$
    }

    repositoryTimeResult.setResponded(in.readLong());
    if (TRACER.isEnabled())
    {
      TRACER.format("Read responded: {0}", CDOCommonUtil.formatTimeStamp(repositoryTimeResult.getResponded())); //$NON-NLS-1$
    }

    return null;
  }
}
