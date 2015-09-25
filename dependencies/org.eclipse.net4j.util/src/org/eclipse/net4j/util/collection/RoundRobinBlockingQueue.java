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

import org.eclipse.net4j.util.ObjectUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class RoundRobinBlockingQueue<E> implements BlockingQueue<E>
{
  private BlockingQueue<Entry<E>> list = new LinkedBlockingQueue<Entry<E>>();

  public RoundRobinBlockingQueue()
  {
  }

  public int remainingCapacity()
  {
    return Integer.MAX_VALUE;
  }

  public int size()
  {
    int size = 0;
    synchronized (list)
    {
      for (Entry<E> entry : list)
      {
        size += entry.getCount();
      }
    }

    return size;
  }

  public boolean isEmpty()
  {
    synchronized (list)
    {
      return list.isEmpty();
    }
  }

  public boolean offer(E e)
  {
    synchronized (list)
    {
      for (Entry<E> entry : list)
      {
        if (ObjectUtil.equals(entry.getElement(), e))
        {
          entry.increaseCount();
          return true;
        }
      }

      return list.add(new Entry<E>(e));
    }
  }

  public boolean offer(E o, long timeout, TimeUnit unit) throws InterruptedException
  {
    return offer(o);
  }

  public void put(E o) throws InterruptedException
  {
    offer(o);
  }

  public boolean add(E o)
  {
    return offer(o);
  }

  public E poll(long timeout, TimeUnit unit) throws InterruptedException
  {
    synchronized (list)
    {
      Entry<E> entry = list.poll(timeout, unit);
      if (entry == null)
      {
        return null;
      }

      if (entry.decreaseCount() > 0)
      {
        list.add(entry);
      }

      return entry.getElement();
    }
  }

  public E poll()
  {
    synchronized (list)
    {
      Entry<E> entry = list.poll();
      if (entry == null)
      {
        return null;
      }

      if (entry.decreaseCount() > 0)
      {
        list.add(entry);
      }

      return entry.getElement();
    }
  }

  public E take() throws InterruptedException
  {
    synchronized (list)
    {
      Entry<E> entry = list.take();
      if (entry.decreaseCount() > 0)
      {
        list.add(entry);
      }

      return entry.getElement();
    }
  }

  public E peek()
  {
    synchronized (list)
    {
      Entry<E> entry = list.peek();
      if (entry == null)
      {
        return null;
      }

      return entry.getElement();
    }
  }

  public E element()
  {
    synchronized (list)
    {
      Entry<E> entry = list.element();
      if (entry == null)
      {
        return null;
      }

      return entry.getElement();
    }
  }

  public E remove()
  {
    synchronized (list)
    {
      Entry<E> entry = list.remove();
      if (entry.decreaseCount() > 0)
      {
        list.add(entry);
      }

      return entry.getElement();
    }
  }

  public boolean remove(Object o)
  {
    synchronized (list)
    {
      for (Iterator<Entry<E>> it = list.iterator(); it.hasNext();)
      {
        Entry<E> entry = it.next();
        if (ObjectUtil.equals(entry.getElement(), o))
        {
          if (entry.decreaseCount() > 0)
          {
            it.remove();
          }

          return true;
        }
      }
    }

    return false;
  }

  public void clear()
  {
    synchronized (list)
    {
      list.clear();
    }
  }

  public Iterator<E> iterator()
  {
    List<E> copy = new ArrayList<E>();

    synchronized (list)
    {
      int round = 0;
      boolean again;

      do
      {
        again = false;
        for (Entry<E> entry : list)
        {
          int rest = entry.getCount() - round;
          if (rest > 0)
          {
            copy.add(entry.getElement());
            if (rest > 1)
            {
              again = true;
            }
          }
        }

        ++round;
      } while (again);
    }

    return copy.iterator();
  }

  public boolean contains(Object o)
  {
    synchronized (list)
    {
      for (Entry<E> entry : list)
      {
        if (ObjectUtil.equals(entry.getElement(), o))
        {
          return true;
        }
      }
    }

    return false;
  }

  public Object[] toArray()
  {
    synchronized (list)
    {
      return list.toArray();
    }
  }

  public <T> T[] toArray(T[] array)
  {
    synchronized (list)
    {
      return list.toArray(array);
    }
  }

  public boolean containsAll(Collection<?> c)
  {
    // TODO: implement RoundRobinBlockingQueue.containsAll(c)
    throw new UnsupportedOperationException();
  }

  public boolean addAll(Collection<? extends E> c)
  {
    // TODO: implement RoundRobinBlockingQueue.addAll(c)
    throw new UnsupportedOperationException();
  }

  public boolean removeAll(Collection<?> c)
  {
    // TODO: implement RoundRobinBlockingQueue.removeAll(c)
    throw new UnsupportedOperationException();
  }

  public boolean retainAll(Collection<?> c)
  {
    // TODO: implement RoundRobinBlockingQueue.retainAll(c)
    throw new UnsupportedOperationException();
  }

  public int drainTo(Collection<? super E> c)
  {
    // TODO: implement RoundRobinBlockingQueue.drainTo(c)
    throw new UnsupportedOperationException();
  }

  public int drainTo(Collection<? super E> c, int maxElements)
  {
    // TODO: implement RoundRobinBlockingQueue.drainTo(c, maxElements)
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString()
  {
    synchronized (list)
    {
      return list.toString();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Entry<E>
  {
    private E element;

    private int count;

    public Entry(E element)
    {
      this.element = element;
      count = 1;
    }

    public E getElement()
    {
      return element;
    }

    public int getCount()
    {
      return count;
    }

    public int increaseCount()
    {
      return ++count;
    }

    public int decreaseCount()
    {
      return --count;
    }

    @Override
    public String toString()
    {
      return element.toString() + "(" + count + ")";
    }
  }
}
