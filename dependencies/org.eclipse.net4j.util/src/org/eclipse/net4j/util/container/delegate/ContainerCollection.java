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
package org.eclipse.net4j.util.container.delegate;

import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.IListener;

import java.util.Collection;
import java.util.Iterator;

/**
 * A default implementation of a {@link IContainerCollection container collection}.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public class ContainerCollection<E> extends AbstractDelegator<E> implements IContainerCollection<E>
{
  private Collection<E> delegate;

  public ContainerCollection(Collection<E> delegate)
  {
    this.delegate = delegate;
  }

  public Collection<E> getDelegate()
  {
    return delegate;
  }

  @SuppressWarnings("unchecked")
  public E[] getElements()
  {
    return (E[])toArray();
  }

  /**
   * @category WRITE
   */
  public boolean add(E o)
  {
    boolean modified = getDelegate().add(o);
    if (modified)
    {
      fireAddedEvent(o);
    }

    return modified;
  }

  /**
   * @category WRITE
   */
  public boolean addAll(Collection<? extends E> c)
  {
    ContainerEvent<E> event = new ContainerEvent<E>(this);
    for (E e : c)
    {
      boolean modified = getDelegate().add(e);
      if (modified)
      {
        event.addDelta(e, IContainerDelta.Kind.ADDED);
      }
    }

    return dispatchEvent(event);
  }

  /**
   * @category WRITE
   */
  public void clear()
  {
    if (!isEmpty())
    {
      ContainerEvent<E> event = createEvent(getDelegate(), IContainerDelta.Kind.REMOVED);
      getDelegate().clear();
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(event, listeners);
      }
    }
  }

  /**
   * @category WRITE
   */
  public boolean remove(Object o)
  {
    boolean modified = getDelegate().remove(o);
    if (modified)
    {
      fireRemovedEvent(o);
    }

    return modified;
  }

  /**
   * @category WRITE
   */
  @SuppressWarnings("unchecked")
  public boolean removeAll(Collection<?> c)
  {
    ContainerEvent<E> event = new ContainerEvent<E>(this);
    for (Object o : c)
    {
      boolean modified = getDelegate().remove(o);
      if (modified)
      {
        event.addDelta((E)o, IContainerDelta.Kind.REMOVED);
      }
    }

    return dispatchEvent(event);
  }

  /**
   * @category WRITE
   */
  @SuppressWarnings("unchecked")
  public boolean retainAll(Collection<?> c)
  {
    ContainerEvent<E> event = new ContainerEvent<E>(this);
    for (Object o : getDelegate())
    {
      if (!c.contains(o))
      {
        getDelegate().remove(o);
        event.addDelta((E)o, IContainerDelta.Kind.REMOVED);
      }
    }

    return dispatchEvent(event);
  }

  /**
   * @category READ
   */
  public boolean contains(Object o)
  {
    return getDelegate().contains(o);
  }

  /**
   * @category READ
   */
  public boolean containsAll(Collection<?> c)
  {
    return getDelegate().containsAll(c);
  }

  /**
   * @category READ
   */
  @Override
  public boolean equals(Object o)
  {
    return getDelegate().equals(o);
  }

  /**
   * @category READ
   */
  @Override
  public int hashCode()
  {
    return getDelegate().hashCode();
  }

  /**
   * @category READ
   */
  public boolean isEmpty()
  {
    return getDelegate().isEmpty();
  }

  /**
   * @category READ
   */
  public Iterator<E> iterator()
  {
    return new DelegatingIterator(getDelegate().iterator());
  }

  /**
   * @category READ
   */
  public int size()
  {
    return getDelegate().size();
  }

  /**
   * @category READ
   */
  public Object[] toArray()
  {
    return getDelegate().toArray();
  }

  /**
   * @category READ
   */
  public <T> T[] toArray(T[] a)
  {
    return getDelegate().toArray(a);
  }
}
