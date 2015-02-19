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
import org.eclipse.net4j.util.container.delegate.IContainerSet;
import org.eclipse.net4j.util.container.delegate.IContainerSortedSet;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of a {@link Container container} that stores its {@link #getElements() elements} in a {@link #getSet() set}.
 *
 * @since 3.2
 * @author Eike Stepper
 * @see IContainerSet
 * @see IContainerSortedSet
 */
public class SetContainer<E> extends Container<E>
{
  private final Class<E> componentType;

  private final Set<E> set;

  public SetContainer(Class<E> componentType)
  {
    this(componentType, new HashSet<E>());
  }

  public SetContainer(Class<E> componentType, Set<E> set)
  {
    this.componentType = componentType;
    this.set = set;
  }

  public final Class<E> getComponentType()
  {
    return componentType;
  }

  @Override
  public synchronized boolean isEmpty()
  {
    checkActive();
    return set.isEmpty();
  }

  public synchronized E[] getElements()
  {
    checkActive();

    @SuppressWarnings("unchecked")
    E[] a = (E[])Array.newInstance(componentType, set.size());

    E[] array = set.toArray(a);
    array = sortElements(array);

    return array;
  }

  public void clear()
  {
    ContainerEvent<E> event = new ContainerEvent<E>(this);
    synchronized (this)
    {
      for (E element : set)
      {
        if (set.remove(element))
        {
          event.addDelta(element, Kind.REMOVED);
        }
      }

      notifyAll();
    }

    fireEvent(event);
  }

  public boolean addElement(E element)
  {
    boolean added;
    synchronized (this)
    {
      if (!validateElement(element))
      {
        return false;
      }

      added = set.add(element);
      notifyAll();
    }

    if (added)
    {
      fireElementAddedEvent(element);
    }

    return added;
  }

  public boolean removeElement(E element)
  {
    boolean removed;
    synchronized (this)
    {
      removed = set.remove(element);
      notifyAll();
    }

    if (removed)
    {
      fireElementRemovedEvent(element);
    }

    return removed;
  }

  protected Set<E> getSet()
  {
    return set;
  }

  protected E[] sortElements(E[] array)
  {
    return array;
  }

  protected boolean validateElement(E element)
  {
    return true;
  }
}
