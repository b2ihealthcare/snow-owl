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
package org.eclipse.net4j.util.cache;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CacheMonitor extends Worker implements ICacheMonitor
{
  // percentFreeAllocated = Round((freeMemory / totalMemory) * 100);
  // percentAllocated = Round((totalMemory / maxMemory ) * 100);

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CacheMonitor.class);

  private static final long DEFAULT_PAUSE_GREEN = 60L * 1000L; // 1 minute

  private static final long DEFAULT_PAUSE_YELLOW = 5L * 1000L; // 5 seconds

  private static final long DEFAULT_PAUSE_RED = 100L; // 100 milliseconds

  private long pauseGREEN = DEFAULT_PAUSE_GREEN;

  private long pauseYELLOW = DEFAULT_PAUSE_YELLOW;

  private long pauseRED = DEFAULT_PAUSE_RED;

  private ConditionPolicy conditionPolicy;

  private Condition condition;

  private Map<ICache, ICacheRegistration> registrations = new HashMap<ICache, ICacheRegistration>();

  public CacheMonitor()
  {
  }

  public long getPauseGREEN()
  {
    return pauseGREEN;
  }

  public void setPauseGREEN(long pauseGREEN)
  {
    this.pauseGREEN = pauseGREEN;
  }

  public long getPauseYELLOW()
  {
    return pauseYELLOW;
  }

  public void setPauseYELLOW(long pauseYELLOW)
  {
    this.pauseYELLOW = pauseYELLOW;
  }

  public long getPauseRED()
  {
    return pauseRED;
  }

  public void setPauseRED(long pauseRED)
  {
    this.pauseRED = pauseRED;
  }

  public ConditionPolicy getConditionPolicy()
  {
    return conditionPolicy;
  }

  public void setConditionPolicy(ConditionPolicy conditionPolicy)
  {
    this.conditionPolicy = conditionPolicy;
  }

  public Condition getCondition()
  {
    return condition;
  }

  public ICacheRegistration[] getRegistrations()
  {
    synchronized (registrations)
    {
      return registrations.values().toArray(new ICacheRegistration[registrations.size()]);
    }
  }

  public ICacheRegistration registerCache(ICache cache)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Registering cache " + cache); //$NON-NLS-1$
    }

    ICacheRegistration registration = new CacheRegistration(this, cache);
    ICacheRegistration oldRegistration;
    synchronized (registrations)
    {
      oldRegistration = registrations.put(cache, registration);
    }

    if (oldRegistration != null)
    {
      oldRegistration.dispose();
    }

    return registration;
  }

  public void deregisterCache(ICache cache)
  {
    ICacheRegistration registration;
    synchronized (registrations)
    {
      registration = registrations.remove(cache);
    }

    if (registration != null)
    {
      registration.dispose();
      if (TRACER.isEnabled())
      {
        TRACER.trace("Deregistered cache " + cache); //$NON-NLS-1$
      }
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (conditionPolicy == null)
    {
      throw new IllegalStateException("conditionPolicy == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (ICacheRegistration registration : getRegistrations())
    {
      registration.dispose();
    }

    registrations.clear();
    super.doDeactivate();
  }

  @Override
  protected void work(WorkContext context) throws Exception
  {
    Condition newCondition = conditionPolicy.getNewCondition(condition);
    setCondition(newCondition);

    switch (newCondition)
    {
    case GREEN:
      context.nextWork(pauseGREEN);
      break;

    case YELLOW:
      context.nextWork(pauseYELLOW);
      break;

    case RED:
      handleConditionRED();
      context.nextWork(pauseRED);
      break;
    }
  }

  protected void setCondition(Condition newCondition)
  {
    if (newCondition == null)
    {
      throw new ImplementationError("newCondition == null"); //$NON-NLS-1$
    }

    Condition oldCondition = condition;
    if (newCondition != oldCondition)
    {
      condition = newCondition;
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new CacheMonitorEvent(oldCondition, newCondition), listeners);
      }
    }
  }

  protected void handleConditionRED()
  {
    OM.LOG.warn("CONDITION RED"); //$NON-NLS-1$
  }

  /**
   * @author Eike Stepper
   */
  private final class CacheMonitorEvent extends Event implements ICacheMonitorEvent
  {
    private static final long serialVersionUID = 1L;

    private Condition oldCondition;

    private Condition newCondition;

    public CacheMonitorEvent(Condition oldCondition, Condition newCondition)
    {
      super(CacheMonitor.this);
      this.oldCondition = oldCondition;
      this.newCondition = newCondition;
    }

    @Override
    public ICacheMonitor getSource()
    {
      return (ICacheMonitor)super.getSource();
    }

    public Condition getOldCondition()
    {
      return oldCondition;
    }

    public Condition getNewCondition()
    {
      return newCondition;
    }
  }
}
