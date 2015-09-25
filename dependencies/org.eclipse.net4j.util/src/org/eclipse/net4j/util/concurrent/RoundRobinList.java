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
package org.eclipse.net4j.util.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Eike Stepper
 */
public final class RoundRobinList<E> extends LinkedList<E>
{
  private static final long serialVersionUID = 1L;

  private ReadWriteLock lock = new ReentrantReadWriteLock();

  private Iterator<E> it;

  public RoundRobinList()
  {
  }

  public RoundRobinList(Collection<? extends E> c)
  {
    super(c);
  }

  public void executeReads(Runnable runnable)
  {
    try
    {
      lock.readLock().lock();
      runnable.run();
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  public void executeWrites(Runnable runnable)
  {
    try
    {
      lock.writeLock().lock();
      runnable.run();
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void add(int index, E element)
  {
    try
    {
      lock.writeLock().lock();
      super.add(index, element);
      it = null;
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean add(E o)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.add(o);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean addAll(Collection<? extends E> c)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.addAll(c);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.addAll(index, c);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void addFirst(E o)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      super.addFirst(o);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void addLast(E o)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      super.addLast(o);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void clear()
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      super.clear();
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public Object clone()
  {
    try
    {
      lock.readLock().lock();
      return super.clone();
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public boolean contains(Object o)
  {
    try
    {
      lock.readLock().lock();
      return super.contains(o);
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    try
    {
      lock.readLock().lock();
      return super.containsAll(c);
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public E element()
  {
    try
    {
      lock.readLock().lock();
      if (isEmpty())
      {
        return null;
      }

      if (it == null || !it.hasNext())
      {
        it = iterator();
      }

      return it.next();
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public E get(int index)
  {
    try
    {
      lock.readLock().lock();
      return super.get(index);
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public E getFirst()
  {
    try
    {
      lock.readLock().lock();
      return super.getFirst();
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public E getLast()
  {
    try
    {
      lock.readLock().lock();
      return super.getLast();
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public int indexOf(Object o)
  {
    try
    {
      lock.readLock().lock();
      return super.indexOf(o);
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public int lastIndexOf(Object o)
  {
    try
    {
      lock.readLock().lock();
      return super.lastIndexOf(o);
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public boolean offer(E o)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.offer(o);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public E peek()
  {
    try
    {
      lock.readLock().lock();
      return super.peek();
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public E poll()
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.poll();
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public E remove()
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.remove();
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public E remove(int index)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.remove(index);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean remove(Object o)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.remove(o);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.removeAll(c);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public E removeFirst()
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.removeFirst();
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public E removeLast()
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.removeLast();
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.retainAll(c);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public E set(int index, E element)
  {
    try
    {
      lock.writeLock().lock();
      it = null;
      return super.set(index, element);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex)
  {
    try
    {
      lock.readLock().lock();
      return super.subList(fromIndex, toIndex);
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public Object[] toArray()
  {
    try
    {
      lock.readLock().lock();
      return super.toArray();
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
    try
    {
      lock.readLock().lock();
      return super.toArray(a);
    }
    finally
    {
      lock.readLock().unlock();
    }
  }
}
