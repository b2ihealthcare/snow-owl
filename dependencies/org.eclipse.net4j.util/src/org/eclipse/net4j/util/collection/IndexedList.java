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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public abstract class IndexedList<E> implements List<E>
{
  public IndexedList()
  {
  }

  public abstract E get(int index);

  public abstract int size();

  public boolean isEmpty()
  {
    return size() == 0;
  }

  public boolean contains(Object o)
  {
    int size = size();
    for (int i = 0; i < size; i++)
    {
      if (get(i).equals(o))
      {
        return true;
      }
    }

    return false;
  }

  public boolean containsAll(Collection<?> c)
  {
    for (Object object : c)
    {
      if (!contains(object))
      {
        return false;
      }
    }

    return true;
  }

  public int indexOf(Object o)
  {
    return 0;
  }

  public int lastIndexOf(Object o)
  {
    return 0;
  }

  public Iterator<E> iterator()
  {
    return new IndexedIterator();
  }

  public ListIterator<E> listIterator()
  {
    return new IndexedListIterator(0);
  }

  public ListIterator<E> listIterator(int index)
  {
    if (index < 0 || index > size())
    {
      throw new IndexOutOfBoundsException("Index: " + index);
    }

    return new IndexedListIterator(index);
  }

  public List<E> subList(int fromIndex, int toIndex)
  {
    return null;
  }

  public Object[] toArray()
  {
    throw new UnsupportedOperationException();
  }

  public <T> T[] toArray(T[] a)
  {
    throw new UnsupportedOperationException();
  }

  public boolean add(E o)
  {
    throw new UnsupportedOperationException();
  }

  public boolean remove(Object o)
  {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(Collection<? extends E> c)
  {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(int index, Collection<? extends E> c)
  {
    throw new UnsupportedOperationException();
  }

  public boolean removeAll(Collection<?> c)
  {
    throw new UnsupportedOperationException();
  }

  public boolean retainAll(Collection<?> c)
  {
    throw new UnsupportedOperationException();
  }

  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  public E set(int index, E element)
  {
    throw new UnsupportedOperationException();
  }

  public void add(int index, E element)
  {
    throw new UnsupportedOperationException();
  }

  public E remove(int index)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("{"); //$NON-NLS-1$
    for (int i = 0; i < size(); i++)
    {
      if (i != 0)
      {
        builder.append(", "); //$NON-NLS-1$
      }

      builder.append(get(i).toString());
    }

    builder.append("}"); //$NON-NLS-1$
    return builder.toString();
  }

  /**
   * @author Eike Stepper
   */
  private class IndexedIterator implements Iterator<E>
  {
    int pos = 0;

    public boolean hasNext()
    {
      return pos != size();
    }

    public E next()
    {
      try
      {
        return get(pos++);
      }
      catch (IndexOutOfBoundsException ex)
      {
        throw new NoSuchElementException();
      }
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * @author Eike Stepper
   */
  private class IndexedListIterator extends IndexedIterator implements ListIterator<E>
  {
    IndexedListIterator(int index)
    {
      pos = index;
    }

    public boolean hasPrevious()
    {
      return pos != 0;
    }

    public E previous()
    {
      try
      {
        return get(--pos);
      }
      catch (IndexOutOfBoundsException ex)
      {
        throw new NoSuchElementException();
      }
    }

    public int nextIndex()
    {
      return pos;
    }

    public int previousIndex()
    {
      return pos - 1;
    }

    public void set(E o)
    {
      throw new UnsupportedOperationException();
    }

    public void add(E o)
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class ArrayBacked<E> extends IndexedList<E>
  {
    public ArrayBacked()
    {
    }

    protected abstract E[] getArray();

    @Override
    public E get(int i)
    {
      return getArray()[i];
    }

    @Override
    public int size()
    {
      return getArray().length;
    }
  }
}
