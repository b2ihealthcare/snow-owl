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
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.ILifecycle.DeferrableActivation;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.concurrent.Semaphore;

/**
 * A default implementation of an entity with a {@link ILifecycle lifecycle}.
 *
 * @author Eike Stepper
 * @apiviz.exclude
 */
public class Lifecycle extends Notifier implements ILifecycle, DeferrableActivation
{
  public static boolean USE_LABEL = true;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_LIFECYCLE, Lifecycle.class);

  private static final ContextTracer DUMPER = new ContextTracer(OM.DEBUG_LIFECYCLE_DUMP, Lifecycle.class);

  private static final boolean TRACE_IGNORING = false;

  private static final boolean LOCKING = true;

  private LifecycleState lifecycleState = LifecycleState.INACTIVE;

  @ExcludeFromDump
  private Semaphore lifecycleSemaphore = new Semaphore(1);

  /**
   * @since 2.0
   */
  public Lifecycle()
  {
  }

  public final void activate() throws LifecycleException
  {
    try
    {
      if (lifecycleState == LifecycleState.INACTIVE)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Activating " + this); //$NON-NLS-1$
        }

        lock();

        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ABOUT_TO_ACTIVATE), listeners);
        }

        doBeforeActivate();

        lifecycleState = LifecycleState.ACTIVATING;
        doActivate();

        if (!isDeferredActivation())
        {
          deferredActivate(true);
        }

        dump();
      }
      else
      {
        if (TRACE_IGNORING)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Ignoring activation in state {0} for {1}", lifecycleState, this); //$NON-NLS-1$
          }
        }
      }
    }
    catch (RuntimeException ex)
    {
      deferredActivate(false);
      throw ex;
    }
    catch (Exception ex)
    {
      deferredActivate(false);
      throw new LifecycleException(ex);
    }
  }

  public final Exception deactivate()
  {
    try
    {
      if (lifecycleState == LifecycleState.ACTIVE)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Deactivating " + this); //$NON-NLS-1$
        }

        lock();

        doBeforeDeactivate();
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ABOUT_TO_DEACTIVATE), listeners);
        }

        lifecycleState = LifecycleState.DEACTIVATING;
        doDeactivate();

        lifecycleState = LifecycleState.INACTIVE;
        unlock();

        if (listeners != null)
        {
          fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.DEACTIVATED), listeners);
        }

        return null;
      }

      if (TRACE_IGNORING)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Ignoring deactivation in state {0} for {1}", lifecycleState, this); //$NON-NLS-1$
        }
      }

      return null;
    }
    catch (Exception ex)
    {
      lifecycleState = LifecycleState.INACTIVE;
      unlock();
      return ex;
    }
  }

  /**
   * @since 3.0
   */
  public final LifecycleState getLifecycleState()
  {
    return lifecycleState;
  }

  public final boolean isActive()
  {
    return lifecycleState == LifecycleState.ACTIVE;
  }

  @Override
  public String toString()
  {
    if (USE_LABEL)
    {
      return ReflectUtil.getLabel(this);
    }

    return super.toString();
  }

  protected final void dump()
  {
    if (DUMPER.isEnabled())
    {
      DUMPER.trace("DUMP" + ReflectUtil.toString(this)); //$NON-NLS-1$
    }
  }

  protected final void checkActive()
  {
    LifecycleUtil.checkActive(this);
  }

  protected final void checkInactive()
  {
    LifecycleUtil.checkInactive(this);
  }

  protected final void checkNull(Object handle, String msg) throws NullPointerException
  {
    CheckUtil.checkNull(handle, msg);
  }

  protected final void checkArg(boolean expr, String msg) throws IllegalArgumentException
  {
    CheckUtil.checkArg(expr, msg);
  }

  protected final void checkArg(Object handle, String handleName) throws IllegalArgumentException
  {
    CheckUtil.checkState(handle, handleName);
  }

  protected final void checkState(boolean expr, String msg) throws IllegalStateException
  {
    CheckUtil.checkState(expr, msg);
  }

  protected final void checkState(Object handle, String handleName) throws IllegalStateException
  {
    CheckUtil.checkState(handle, handleName);
  }

  /**
   * @since 2.0
   */
  protected final void deferredActivate(boolean successful)
  {
    if (successful)
    {
      lifecycleState = LifecycleState.ACTIVE;

      try
      {
        doAfterActivate();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        deactivate();
        return;
      }

      unlock();
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ACTIVATED), listeners);
      }
    }
    else
    {
      lifecycleState = LifecycleState.INACTIVE;
      unlock();
    }
  }

  /**
   * @since 3.2
   */
  public boolean isDeferredActivation()
  {
    return false;
  }

  protected void doBeforeActivate() throws Exception
  {
  }

  protected void doActivate() throws Exception
  {
  }

  /**
   * @since 3.0
   */
  protected void doAfterActivate() throws Exception
  {
  }

  protected void doBeforeDeactivate() throws Exception
  {
  }

  protected void doDeactivate() throws Exception
  {
  }

  private void lock() throws InterruptedException
  {
    if (LOCKING)
    {
      lifecycleSemaphore.acquire();
    }
  }

  private void unlock()
  {
    if (LOCKING)
    {
      lifecycleSemaphore.release();
    }
  }
}
