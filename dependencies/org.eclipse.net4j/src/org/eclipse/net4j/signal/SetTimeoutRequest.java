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
class SetTimeoutRequest extends RequestWithConfirmation<Boolean>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, SetTimeoutRequest.class);

  private long timeout;

  public SetTimeoutRequest(SignalProtocol<?> protocol, long timeout)
  {
    super(protocol, SignalProtocol.SIGNAL_SET_TIMEOUT);
    this.timeout = timeout;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Set timeout: {0}", timeout); //$NON-NLS-1$
    }

    out.writeLong(timeout);
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readBoolean();
  }
}
