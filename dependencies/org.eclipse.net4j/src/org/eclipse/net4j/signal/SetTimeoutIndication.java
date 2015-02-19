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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

/**
 * @author Eike Stepper
 */
class SetTimeoutIndication extends IndicationWithResponse
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, SetTimeoutIndication.class);

  public SetTimeoutIndication(SignalProtocol<?> protocol)
  {
    super(protocol, SignalProtocol.SIGNAL_SET_TIMEOUT);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    long timeout = in.readLong();
    if (TRACER.isEnabled())
    {
      TRACER.format("Set timeout: {0}", timeout); //$NON-NLS-1$
    }

    getProtocol().handleSetTimeOut(timeout);
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    out.writeBoolean(true);
  }
}
