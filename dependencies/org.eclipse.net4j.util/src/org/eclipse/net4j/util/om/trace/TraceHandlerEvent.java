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
package org.eclipse.net4j.util.om.trace;

import java.io.Serializable;

/**
 * The default implementation of a {@link OMTraceHandlerEvent trace event}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 */
public class TraceHandlerEvent implements OMTraceHandlerEvent, Serializable
{
  private static final long serialVersionUID = 1L;

  protected long timeStamp;

  protected OMTracer tracer;

  protected Class<?> context;

  protected String message;

  protected Throwable throwable;

  public TraceHandlerEvent(OMTracer tracer, Class<?> context, String message, Throwable throwable)
  {
    if (tracer == null)
    {
      throw new IllegalArgumentException("tracer == null"); //$NON-NLS-1$
    }

    if (context == null)
    {
      throw new IllegalArgumentException("context == null"); //$NON-NLS-1$
    }

    timeStamp = System.currentTimeMillis();
    this.tracer = tracer;
    this.context = context;
    this.message = message;
    this.throwable = throwable;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public OMTracer getTracer()
  {
    return tracer;
  }

  public Class<?> getContext()
  {
    return context;
  }

  public String getMessage()
  {
    return message;
  }

  public Throwable getThrowable()
  {
    return throwable;
  }
}
