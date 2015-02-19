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
import java.util.List;
import java.util.ListIterator;

/**
 * A default implementation of a {@link IContainerList container list}.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public class ContainerList<E> extends ContainerCollection<E> implements IContainerList<E>
{
  public ContainerList(List<E> delegate)
  {
    super(delegate);
  }

  @Override
  public List<E> getDelegate()
  {
    return (List<E>)super.getDelegate();
  }

  /**
   * @category WRITE
   */
  public void add(int index, E element)
  {
    getDelegate().add(index, element);
    fireAddedEvent(element);
  }

  /**
   * @category WRITE
   */
  public boolean addAll(int index, Collection<? extends E> c)
  {
    ContainerEvent<E> event = createEvent(getDelegate(), IContainerDelta.Kind.ADDED);
    getDelegate().addAll(index, c);
    return dispatchEvent(event);
  }

  /**
   * @category READ
   */
  public E get(int index)
  {
    return getDelegate().get(index);
  }

  /**
   * @category READ
   */
  public int indexOf(Object o)
  {
    return getDelegate().indexOf(o);
  }

  /**
   * @category READ
   */
  public int lastIndexOf(Object o)
  {
    return getDelegate().lastIndexOf(o);
  }

  /**
   * @category READ
   */
  public ListIterator<E> listIterator()
  {
    return new DelegatingListIterator(getDelegate().listIterator());
  }

  /**
   * @category READ
   */
  public ListIterator<E> listIterator(int index)
  {
    return new DelegatingListIterator(getDelegate().listIterator(index));
  }

  /**
   * @category WRITE
   */
  public E remove(int index)
  {
    E removed = getDelegate().remove(index);
    if (removed != null)
    {
      fireRemovedEvent(removed);
    }

    return removed;
  }

  /**
   * @category WRITE
   */
  public E set(int index, E element)
  {
    E removed = getDelegate().set(index, element);
    ContainerEvent<E> event = new ContainerEvent<E>(ContainerList.this);
    event.addDelta(removed, IContainerDelta.Kind.REMOVED);
    event.addDelta(element, IContainerDelta.Kind.ADDED);
    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(event, listeners);
    }

    return removed;
  }

  /**
   * @category READ
   */
  public List<E> subList(int fromIndex, int toIndex)
  {
    return getDelegate().subList(fromIndex, toIndex);
  }

  /**
   * A delegating {@link ListIterator list iterator}.
   * 
   * @author Eike Stepper
   * @apiviz.exclude
   */
  public class DelegatingListIterator extends DelegatingIterator implements ListIterator<E>
  {
    public DelegatingListIterator(ListIterator<E> delegate)
    {
      super(delegate);
    }

    @Override
    public ListIterator<E> getDelegate()
    {
      return (ListIterator<E>)super.getDelegate();
    }

    /**
     * @category WRITE
     */
    public void add(E o)
    {
      getDelegate().add(o);
      fireAddedEvent(o);
      last = o;
    }

    /**
     * @category WRITE
     */
    public void set(E o)
    {
      getDelegate().set(o);
      ContainerEvent<E> event = new ContainerEvent<E>(ContainerList.this);
      event.addDelta(last, IContainerDelta.Kind.REMOVED);
      event.addDelta(o, IContainerDelta.Kind.ADDED);
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(event, listeners);
      }

      last = o;
    }

    /**
     * @category READ
     */
    public boolean hasPrevious()
    {
      return getDelegate().hasPrevious();
    }

    /**
     * @category READ
     */
    public int nextIndex()
    {
      return getDelegate().nextIndex();
    }

    /**
     * @category READ
     */
    public E previous()
    {
      return getDelegate().previous();
    }

    /**
     * @category READ
     */
    public int previousIndex()
    {
      return getDelegate().previousIndex();
    }
  }
}
