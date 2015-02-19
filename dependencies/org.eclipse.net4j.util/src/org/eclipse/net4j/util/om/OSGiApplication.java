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
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import java.util.concurrent.CountDownLatch;

/**
 * A default implementation of an OSGi {@link IApplication application}.
 * 
 * @author Eike Stepper
 */
public class OSGiApplication implements IApplication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OM, OSGiApplication.class);

  private String applicationID;

  private IApplicationContext context;

  private CountDownLatch stopLatch;

  public OSGiApplication(String applicationID)
  {
    this.applicationID = applicationID;
  }

  public String getApplicationID()
  {
    return applicationID;
  }

  public IApplicationContext getApplicationContext()
  {
    return context;
  }

  public boolean isRunning()
  {
    return stopLatch != null;
  }

  public final Object start(IApplicationContext context) throws Exception
  {
    this.context = context;
    traceStart(applicationID);

    try
    {
      doStart();
    }
    catch (Error error)
    {
      OM.LOG.error(error);
      throw error;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }

    if (context != null)
    {
      context.applicationRunning();
    }

    stopLatch = new CountDownLatch(1);
    stopLatch.await();
    stopLatch = null;
    return EXIT_OK;
  }

  public final void stop()
  {
    traceStop(applicationID);

    try
    {
      doStop();
    }
    catch (Error error)
    {
      OM.LOG.error(error);
      throw error;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    context = null;
    if (stopLatch != null)
    {
      stopLatch.countDown();
    }
  }

  protected void doStart() throws Exception
  {
  }

  protected void doStop() throws Exception
  {
  }

  public static void traceStart(String applicationID)
  {
    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Starting application {0}", applicationID); //$NON-NLS-1$
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  public static void traceStop(String applicationID)
  {
    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Stopping application {0}", applicationID); //$NON-NLS-1$
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }
}
