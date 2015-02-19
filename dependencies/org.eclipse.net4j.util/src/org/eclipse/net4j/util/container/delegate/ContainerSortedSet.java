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

import java.util.Comparator;
import java.util.SortedSet;

/**
 * A default implementation of a {@link IContainerSortedSet container sorted set}.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public class ContainerSortedSet<E> extends ContainerSet<E> implements IContainerSortedSet<E>
{
  public ContainerSortedSet(SortedSet<E> delegate)
  {
    super(delegate);
  }

  @Override
  public SortedSet<E> getDelegate()
  {
    return (SortedSet<E>)super.getDelegate();
  }

  /**
   * @category READ
   */
  public Comparator<? super E> comparator()
  {
    return getDelegate().comparator();
  }

  /**
   * @category READ
   */
  public E first()
  {
    return getDelegate().first();
  }

  /**
   * @category READ
   */
  public E last()
  {
    return getDelegate().last();
  }

  /**
   * @category READ
   */
  public SortedSet<E> headSet(E toElement)
  {
    return getDelegate().headSet(toElement);
  }

  /**
   * @category READ
   */
  public SortedSet<E> subSet(E fromElement, E toElement)
  {
    return getDelegate().subSet(fromElement, toElement);
  }

  /**
   * @category READ
   */
  public SortedSet<E> tailSet(E fromElement)
  {
    return getDelegate().tailSet(fromElement);
  }
}
