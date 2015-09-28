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
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;

import java.util.Timer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class TimerLifecycle extends Timer implements ILifecycle
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.timers";

  private Lifecycle delegate = new Lifecycle()
  {
    @Override
    protected void doDeactivate() throws Exception
    {
      cancel();
    }
  };

  public TimerLifecycle()
  {
    activate();
  }

  public TimerLifecycle(boolean isDaemon)
  {
    super(isDaemon);
    activate();
  }

  public TimerLifecycle(String name)
  {
    super(name);
    activate();
  }

  public TimerLifecycle(String name, boolean isDaemon)
  {
    super(name, isDaemon);
    activate();
  }

  /**
   * @since 3.0
   */
  public final LifecycleState getLifecycleState()
  {
    return delegate.getLifecycleState();
  }

  public final boolean isActive()
  {
    return delegate.isActive();
  }

  public void addListener(IListener listener)
  {
    delegate.addListener(listener);
  }

  public void removeListener(IListener listener)
  {
    delegate.removeListener(listener);
  }

  public IListener[] getListeners()
  {
    return delegate.getListeners();
  }

  public boolean hasListeners()
  {
    return delegate.hasListeners();
  }

  public final void activate() throws LifecycleException
  {
    delegate.activate();
  }

  public final Exception deactivate()
  {
    return delegate.deactivate();
  }

  /**
   * @author Eike Stepper
   */
  public static class DaemonFactory extends Factory
  {
    public static final String TYPE = "daemon";

    public DaemonFactory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    public Object create(String name) throws ProductCreationException
    {
      if (name == null)
      {
        return new TimerLifecycle(true);
      }

      return new TimerLifecycle(name, true);
    }

    public static TimerLifecycle getTimer(IManagedContainer container, String name)
    {
      return (TimerLifecycle)container.getElement(PRODUCT_GROUP, TYPE, name);
    }
  }
}
