/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.common.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Closeable;

/**
 * The {@link Queue queue} that represents the result of a CDOQuery.
 * 
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public class CDOQueryQueue<E> implements Queue<E>, Closeable
{
  // Static not allowed due to <E>
  private final QueueEntry<E> QUEUE_CLOSED = new QueueEntry<E>();

  private PriorityBlockingQueue<QueueEntry<E>> queue = new PriorityBlockingQueue<QueueEntry<E>>(10);

  private boolean closed;

  private Object closeLock = new Object();

  public CDOQueryQueue()
  {
  }

  public void setException(Throwable exception)
  {
    queue.add(new QueueEntry<E>(exception));
  }

  public void close()
  {
    synchronized (closeLock)
    {
      if (!closed)
      {
        closed = true;
        queue.add(QUEUE_CLOSED);
      }
    }
  }

  public boolean isClosed()
  {
    synchronized (closeLock)
    {
      return closed;
    }
  }

  public boolean add(E e)
  {
    return queue.add(new QueueEntry<E>(e));
  }

  public void clear()
  {
    queue.clear();
  }

  public boolean contains(Object o)
  {
    return queue.contains(o);
  }

  public E element()
  {
    return checkObject(queue.element());
  }

  @Override
  public boolean equals(Object obj)
  {
    return queue.equals(obj);
  }

  @Override
  public int hashCode()
  {
    return queue.hashCode();
  }

  public boolean isEmpty()
  {
    return queue.isEmpty();
  }

  public BlockingCloseableIterator<E> iterator()
  {
    return new BlockingCloseableIteratorImpl();
  }

  public boolean offer(E e, long timeout, TimeUnit unit)
  {
    return queue.offer(new QueueEntry<E>(e), timeout, unit);
  }

  public boolean offer(E e)
  {
    return queue.offer(new QueueEntry<E>(e));
  }

  public E peek()
  {
    return checkObject(queue.peek());
  }

  public E poll(long timeout, TimeUnit unit) throws InterruptedException
  {
    return checkObject(queue.poll(timeout, unit));
  }

  public void put(E e)
  {
    queue.put(new QueueEntry<E>(e));
  }

  public int remainingCapacity()
  {
    return queue.remainingCapacity();
  }

  public E remove()
  {
    return checkObject(queue.remove());
  }

  public boolean remove(Object o)
  {
    return queue.remove(o);
  }

  public int size()
  {
    return queue.size();
  }

  public E take() throws InterruptedException
  {
    QueueEntry<E> entry = null;

    entry = queue.take();

    return checkObject(entry);
  }

  public Object[] toArray()
  {
    return queue.toArray();
  }

  @SuppressWarnings("unchecked")
  public Object[] toArray(Object[] a)
  {
    return queue.toArray(a);
  }

  @Override
  public String toString()
  {
    return queue.toString();
  }

  public E poll()
  {
    QueueEntry<E> entry = queue.poll();
    return checkObject(entry);
  }

  public Comparator<?> comparator()
  {
    throw new UnsupportedOperationException();
  }

  public boolean containsAll(Collection<?> c)
  {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(Collection<? extends E> c)
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

  private E checkObject(QueueEntry<E> entry)
  {
    if (entry == null || entry == QUEUE_CLOSED)
    {
      return null;
    }

    return entry.getObjectWithException();
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  private static class QueueEntry<E> implements Comparable<QueueEntry<E>>
  {
    private static final AtomicLong nextSeq = new AtomicLong(0);

    private long seqNumber;

    private Object internalObject;

    public QueueEntry()
    {
      seqNumber = Long.MAX_VALUE;
    }

    public QueueEntry(E object)
    {
      internalObject = object;
      seqNumber = nextSeq.getAndIncrement();
    }

    public QueueEntry(Throwable object)
    {
      internalObject = object;
      seqNumber = nextSeq.getAndIncrement();
    }

    @SuppressWarnings("unchecked")
    public E getObjectWithException()
    {
      Throwable exception = getException();
      if (exception instanceof Exception)
      {
        throw WrappedException.wrap((Exception)exception);
      }
      else if (exception instanceof Error)
      {
        throw (Error)exception;
      }

      return (E)internalObject;
    }

    public Throwable getException()
    {
      if (internalObject instanceof Throwable)
      {
        return (Throwable)internalObject;
      }

      return null;
    }

    public int compareTo(QueueEntry<E> o)
    {
      if (getException() != null)
      {
        return -1;
      }

      if (o.getException() != null)
      {
        return 1;
      }

      if (this == o)
      {
        return 0;
      }

      if (seqNumber == o.seqNumber)
      {
        // Should not be possible
        return 0;
      }

      return seqNumber < o.seqNumber ? -1 : 1;
    }

    // @Override
    // public boolean equals(Object obj)
    // {
    // if (this == obj)
    // {
    // return true;
    // }
    //
    // if (obj instanceof QueueEntry<?>)
    // {
    // @SuppressWarnings("unchecked")
    // QueueEntry<E> that = (QueueEntry<E>)obj;
    // return compareTo(that) == 0;
    // }
    //
    // return false;
    // }
  }

  /**
   * A blocking iterator that takes elements from a {@link CDOQueryQueue}.
   * 
   * @author Simon McDuff
   * @since 2.0
   */
  public class BlockingCloseableIteratorImpl implements BlockingCloseableIterator<E>
  {
    private boolean closed;

    private E nextElement;

    public BlockingCloseableIteratorImpl()
    {
    }

    public E peek()
    {
      if (nextElement == null)
      {
        return CDOQueryQueue.this.peek();
      }

      return nextElement;
    }

    public boolean hasNext()
    {
      privateNext(false);
      return nextElement != null;
    }

    private void privateNext(boolean failOnNull)
    {
      if (nextElement == null)
      {
        try
        {
          synchronized (closeLock)
          {
            if (CDOQueryQueue.this.isEmpty() && CDOQueryQueue.this.isClosed())
            {
              if (failOnNull)
              {
                throw new NoSuchElementException();
              }

              return;
            }
          }

          nextElement = take();
        }
        catch (InterruptedException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }

    public E next()
    {
      try
      {
        privateNext(true);
        return nextElement;
      }
      finally
      {
        nextElement = null;
      }
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }

    public void close()
    {
      this.closed = true;
    }

    public boolean isClosed()
    {
      return this.closed;
    }
  }
}
