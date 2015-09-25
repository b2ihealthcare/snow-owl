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
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;

import java.util.Collection;
import java.util.Iterator;

/**
 * A delegating base class for {@link IContainer containers}.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public abstract class AbstractDelegator<E> extends Notifier implements IContainer<E>
{
  public AbstractDelegator()
  {
  }

  protected void fireAddedEvent(E o)
  {
    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(new SingleDeltaContainerEvent<E>(this, o, IContainerDelta.Kind.ADDED), listeners);
    }
  }

  @SuppressWarnings("unchecked")
  protected void fireRemovedEvent(Object o)
  {
    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(new SingleDeltaContainerEvent<E>(this, (E)o, IContainerDelta.Kind.REMOVED), listeners);
    }
  }

  @SuppressWarnings("unchecked")
  protected ContainerEvent<E> createEvent(Collection<? super E> c, Kind kind)
  {
    ContainerEvent<E> event = new ContainerEvent<E>(this);
    for (Object o : c)
    {
      event.addDelta((E)o, kind);
    }

    return event;
  }

  protected boolean dispatchEvent(ContainerEvent<E> event)
  {
    if (event.isEmpty())
    {
      return false;
    }

    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(event, listeners);
    }

    return true;
  }

  /**
   * A delegating {@link Iterator iterator}.
   * 
   * @author Eike Stepper
   * @apiviz.exclude
   */
  public class DelegatingIterator implements Iterator<E>
  {
    private Iterator<E> delegate;

    protected E last;

    public DelegatingIterator(Iterator<E> delegate)
    {
      this.delegate = delegate;
    }

    public Iterator<E> getDelegate()
    {
      return delegate;
    }

    /**
     * @category READ
     */
    public boolean hasNext()
    {
      return getDelegate().hasNext();
    }

    /**
     * @category READ
     */
    public E next()
    {
      return last = getDelegate().next();
    }

    /**
     * @category WRITE
     */
    public void remove()
    {
      getDelegate().remove();
      fireRemovedEvent(last);
      last = null;
    }
  }
}
