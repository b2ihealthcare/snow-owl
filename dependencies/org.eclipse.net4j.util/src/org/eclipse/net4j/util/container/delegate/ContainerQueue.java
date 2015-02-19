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

import java.util.Queue;

/**
 * A default implementation of a {@link IContainerQueue container queue}.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public class ContainerQueue<E> extends ContainerCollection<E> implements IContainerQueue<E>
{
  public ContainerQueue(Queue<E> delegate)
  {
    super(delegate);
  }

  @Override
  public Queue<E> getDelegate()
  {
    return (Queue<E>)super.getDelegate();
  }

  /**
   * @category READ
   */
  public E element()
  {
    return getDelegate().element();
  }

  /**
   * @category WRITE
   */
  public boolean offer(E o)
  {
    boolean modified = getDelegate().offer(o);
    if (modified)
    {
      fireAddedEvent(o);
    }

    return modified;
  }

  /**
   * @category READ
   */
  public E peek()
  {
    return getDelegate().element();
  }

  /**
   * @category WRITE
   */
  public E poll()
  {
    E removed = getDelegate().poll();
    if (removed != null)
    {
      fireRemovedEvent(removed);
    }

    return removed;
  }

  /**
   * @category WRITE
   */
  public E remove()
  {
    E removed = getDelegate().remove();
    if (removed != null)
    {
      fireRemovedEvent(removed);
    }

    return removed;
  }
}
