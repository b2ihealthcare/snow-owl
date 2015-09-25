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
package org.eclipse.internal.net4j.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 * 
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.net4j"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMTracer DEBUG_BUFFER = DEBUG.tracer("buffer"); //$NON-NLS-1$

  public static final OMTracer DEBUG_BUFFER_STREAM = DEBUG_BUFFER.tracer("stream"); //$NON-NLS-1$

  public static final OMTracer DEBUG_CHANNEL = DEBUG.tracer("channel"); //$NON-NLS-1$

  public static final OMTracer DEBUG_ACCEPTOR = DEBUG.tracer("acceptor"); //$NON-NLS-1$

  public static final OMTracer DEBUG_CONNECTOR = DEBUG.tracer("connector"); //$NON-NLS-1$

  public static final OMTracer DEBUG_SIGNAL = DEBUG.tracer("signal"); //$NON-NLS-1$

  public static final boolean SET_SIGNAL_THREAD_NAME = BUNDLE.getDebugSupport().getDebugOption(
      "set.signal.thread.name", false); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator
  {
    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStart() throws Exception
    {
      new Net4jCommandProvider(bundleContext);
    }
  }
}
