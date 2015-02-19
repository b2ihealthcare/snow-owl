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

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A {@link ContextTracer context tracer} that maintains a number of timers for performance measurements.
 *
 * @author Eike Stepper
 */
public class PerfTracer extends ContextTracer
{
  public static final long NOT_STARTED = 0L;

  private Map<Object, Long> timers = new IdentityHashMap<Object, Long>();

  public PerfTracer(OMTracer delegate, Class<?> context)
  {
    super(delegate, context);
  }

  public void start(Object object)
  {
    if (isEnabled())
    {
      synchronized (timers)
      {
        timers.put(object, System.currentTimeMillis());
      }
    }
  }

  public long stop(Object object)
  {
    long duration = NOT_STARTED;
    if (isEnabled())
    {
      Long timer;
      synchronized (timers)
      {
        timer = timers.remove(object);
      }

      if (timer != null)
      {
        duration = System.currentTimeMillis() - timer;
        format("{0} = {1} millis", object, duration); //$NON-NLS-1$
      }
    }

    return duration;
  }

  public long getDuration(Object object)
  {
    long duration = NOT_STARTED;
    if (isEnabled())
    {
      Long timer;
      synchronized (timers)
      {
        timer = timers.get(object);
      }

      if (timer != null)
      {
        duration = System.currentTimeMillis() - timer;
      }
    }

    return duration;
  }
}
