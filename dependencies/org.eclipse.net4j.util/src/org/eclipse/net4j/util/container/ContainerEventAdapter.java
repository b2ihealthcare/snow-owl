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
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * A {@link IListener listener} that dispatches container {@link IContainerEvent events} to methods that can be
 * overridden by extenders.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public class ContainerEventAdapter<E> implements IListener
{
  public ContainerEventAdapter()
  {
  }

  public final void notifyEvent(IEvent event)
  {
    if (event instanceof IContainerEvent<?>)
    {
      @SuppressWarnings("unchecked")
      IContainerEvent<E> e = (IContainerEvent<E>)event;
      notifyContainerEvent(e);
    }
    else
    {
      notifyOtherEvent(event);
    }
  }

  protected void notifyContainerEvent(IContainerEvent<E> event)
  {
    final IContainer<E> container = event.getSource();
    event.accept(new IContainerEventVisitor<E>()
    {
      public void added(E element)
      {
        onAdded(container, element);
      }

      public void removed(E element)
      {
        onRemoved(container, element);
      }
    });
  }

  protected void notifyOtherEvent(IEvent event)
  {
  }

  protected void onAdded(IContainer<E> container, E element)
  {
  }

  protected void onRemoved(IContainer<E> container, E element)
  {
  }
}
