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

import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

/**
 * @author Eike Stepper
 */
class MonitorCanceledRequest extends Request
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, MonitorCanceledRequest.class);

  private int correlationID;

  public MonitorCanceledRequest(SignalProtocol<?> protocol, int correlationID)
  {
    super(protocol, SignalProtocol.SIGNAL_MONITOR_CANCELED);
    this.correlationID = correlationID;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Canceling monitor of signal {0}", correlationID); //$NON-NLS-1$
    }

    out.writeInt(correlationID);
  }
}
