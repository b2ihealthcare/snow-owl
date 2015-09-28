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

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RepositoryTimeIndication extends CDOServerIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, RepositoryTimeIndication.class);

  private long indicated;

  public RepositoryTimeIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REPOSITORY_TIME);
  }

  public RepositoryTimeIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    indicated = System.currentTimeMillis();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    long responded = System.currentTimeMillis();
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing indicated: {0}", CDOCommonUtil.formatTimeStamp(indicated)); //$NON-NLS-1$
    }

    out.writeLong(indicated);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing responded: {0}", CDOCommonUtil.formatTimeStamp(responded)); //$NON-NLS-1$
    }

    out.writeLong(responded);
  }
}
