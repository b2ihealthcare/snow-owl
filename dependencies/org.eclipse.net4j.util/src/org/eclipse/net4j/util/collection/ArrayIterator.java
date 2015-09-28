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
package org.eclipse.net4j.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An object that iterates over the elements of an array
 * 
 * @author Eike Stepper
 * @since 3.0
 */
public class ArrayIterator<T> implements Iterator<T>
{
  private T[] elements;

  private int index;

  private int lastElement;

  public ArrayIterator(T[] elements)
  {
    this(elements, 0, elements.length - 1);
  }

  public ArrayIterator(T[] elements, int firstElement)
  {
    this(elements, firstElement, elements.length - 1);
  }

  public ArrayIterator(T[] elements, int firstElement, int lastElement)
  {
    this.elements = elements;
    index = firstElement;
    this.lastElement = lastElement;
  }

  public boolean hasNext()
  {
    return elements != null && index <= lastElement;
  }

  public T next() throws NoSuchElementException
  {
    if (!hasNext())
    {
      throw new NoSuchElementException();
    }

    return elements[index++];
  }

  /**
   * Unsupported.
   * 
   * @throws UnsupportedOperationException
   *           always
   */
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}
