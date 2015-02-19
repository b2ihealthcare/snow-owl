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
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

/**
 * @author Eike Stepper
 */
class MonitorCanceledIndication extends Indication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, MonitorCanceledIndication.class);

  public MonitorCanceledIndication(SignalProtocol<?> protocol)
  {
    super(protocol, SignalProtocol.SIGNAL_MONITOR_CANCELED);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    int correlationID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Canceling monitor of signal {0}", correlationID); //$NON-NLS-1$
    }

    getProtocol().handleMonitorCanceled(correlationID);
  }
}
