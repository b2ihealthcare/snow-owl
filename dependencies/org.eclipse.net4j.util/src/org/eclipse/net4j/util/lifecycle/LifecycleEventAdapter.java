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

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * A {@link IListener listener} that dispatches lifecycle {@link ILifecycleEvent events} to methods that can be
 * overridden by extenders.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public class LifecycleEventAdapter implements IListener
{
  public LifecycleEventAdapter()
  {
  }

  public final void notifyEvent(IEvent event)
  {
    if (event instanceof ILifecycleEvent)
    {
      ILifecycleEvent e = (ILifecycleEvent)event;
      notifyLifecycleEvent(e);
    }
    else
    {
      notifyOtherEvent(event);
    }
  }

  protected void notifyLifecycleEvent(ILifecycleEvent event)
  {
    switch (event.getKind())
    {
    case ABOUT_TO_ACTIVATE:
      onAboutToActivate(event.getSource());
      break;
    case ACTIVATED:
      onActivated(event.getSource());
      break;
    case ABOUT_TO_DEACTIVATE:
      onAboutToDeactivate(event.getSource());
      break;
    case DEACTIVATED:
      onDeactivated(event.getSource());
      break;
    }
  }

  protected void notifyOtherEvent(IEvent event)
  {
  }

  protected void onAboutToActivate(ILifecycle lifecycle)
  {
  }

  protected void onActivated(ILifecycle lifecycle)
  {
  }

  protected void onAboutToDeactivate(ILifecycle lifecycle)
  {
  }

  protected void onDeactivated(ILifecycle lifecycle)
  {
  }
}
