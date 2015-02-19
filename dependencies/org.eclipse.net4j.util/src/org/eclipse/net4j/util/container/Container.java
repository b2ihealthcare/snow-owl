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

import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.List;

/**
 * An abstract base implementation of a {@link IContainer container} with a {@link ILifecycle lifecycle}.
 *
 * @author Eike Stepper
 */
public abstract class Container<E> extends Lifecycle implements IContainer<E>
{
  public Container()
  {
  }

  public boolean isEmpty()
  {
    if (!isActive())
    {
      return true;
    }

    E[] elements = getElements();
    return elements == null || elements.length == 0;
  }

  @Override
  public void fireEvent(IEvent event)
  {
    if (event instanceof IContainerEvent<?>)
    {
      if (((IContainerEvent<?>)event).isEmpty())
      {
        return;
      }
    }

    super.fireEvent(event);
  }

  public void fireContainerEvent(E element, Kind kind)
  {
    fireEvent(newContainerEvent(element, kind));
  }

  public void fireElementAddedEvent(E element)
  {
    fireContainerEvent(element, IContainerDelta.Kind.ADDED);
  }

  public void fireElementRemovedEvent(E element)
  {
    fireContainerEvent(element, IContainerDelta.Kind.REMOVED);
  }

  /**
   * @since 2.0
   */
  public void fireContainerEvent(E[] elements, Kind kind)
  {
    ContainerEvent<E> event = new ContainerEvent<E>(this);
    for (E element : elements)
    {
      event.addDelta(element, kind);
    }

    fireEvent(event);
  }

  /**
   * @since 2.0
   */
  public void fireElementsAddedEvent(E[] elements)
  {
    fireContainerEvent(elements, IContainerDelta.Kind.ADDED);
  }

  /**
   * @since 2.0
   */
  public void fireElementsRemovedEvent(E[] elements)
  {
    fireContainerEvent(elements, IContainerDelta.Kind.REMOVED);
  }

  public void fireContainerEvent(List<IContainerDelta<E>> deltas)
  {
    fireEvent(new ContainerEvent<E>(this, deltas));
  }

  protected SingleDeltaContainerEvent<E> newContainerEvent(E element, IContainerDelta.Kind kind)
  {
    return new SingleDeltaContainerEvent<E>(this, element, kind);
  }

  protected ContainerEvent<E> newContainerEvent()
  {
    return new ContainerEvent<E>(this);
  }
}
