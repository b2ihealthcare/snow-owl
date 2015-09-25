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
 * An abstract base class for custom iterators that only requires to implement a single {@link #computeNextElement()}
 * method.
 * 
 * @author Eike Stepper
 * @since 3.2
 */
public abstract class AbstractIterator<T> implements Iterator<T>
{
  /**
   * The token to be used in {@link #computeNextElement()} to indicate the end of the iteration.
   */
  protected static final Object END_OF_DATA = new Object();

  private boolean computed;

  private T next;

  public AbstractIterator()
  {
  }

  public final boolean hasNext()
  {
    if (computed)
    {
      return true;
    }

    Object object = computeNextElement();
    computed = true;

    if (object == END_OF_DATA)
    {
      return false;
    }

    @SuppressWarnings("unchecked")
    T cast = (T)object;
    next = cast;
    return true;
  }

  public final T next()
  {
    if (!hasNext())
    {
      throw new NoSuchElementException();
    }

    computed = false;
    return next;
  }

  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the next iteration element, or {@link #END_OF_DATA} if the end of the iteration has been reached.
   */
  protected abstract Object computeNextElement();
}
