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
import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author Eike Stepper
 */
class RemoteExceptionRequest extends Request
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, RemoteExceptionRequest.class);

  private int correlationID;

  private boolean responding;

  private String message;

  private Throwable t;

  public RemoteExceptionRequest(SignalProtocol<?> protocol, int correlationID, boolean responding, String message,
      Throwable t)
  {
    super(protocol, SignalProtocol.SIGNAL_REMOTE_EXCEPTION);
    this.correlationID = correlationID;
    this.message = message;
    this.t = t;
    this.responding = responding;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    if (TRACER.isEnabled())
    {
      String msg = getFirstLine(message);
      TRACER.format("Writing remote exception for signal {0}: {1}", correlationID, msg); //$NON-NLS-1$
    }

    out.writeInt(correlationID);
    out.writeBoolean(responding);
    out.writeString(message);
    out.writeByteArray(serializeThrowable(t));
  }

  public static byte[] serializeThrowable(Throwable t)
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      ExtendedIOUtil.writeObject(dos, t);
      return baos.toByteArray();
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  public static String getFirstLine(String message)
  {
    if (message == null)
    {
      return null;
    }

    int nl = message.indexOf('\n');
    if (nl == -1)
    {
      nl = message.length();
    }

    if (nl > 100)
    {
      nl = 100;
    }

    return message.substring(0, nl);
  }
}
