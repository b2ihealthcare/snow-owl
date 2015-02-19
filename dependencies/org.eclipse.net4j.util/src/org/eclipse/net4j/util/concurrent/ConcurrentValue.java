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
package org.eclipse.net4j.util.concurrent;

/**
 * Allow synchronization between many threads for a specific value.
 *
 * <pre>
 * MainThread cv.set(1);
 * Thread1 cv.acquire(3);
 * Thread2 cv.acquire(4);
 * Thread3 cv.acquire(100);
 * Thread4 cv.acquire(new Object()
 *   {
 *     public boolean equals(Object other)
 *     {
 *       return other.equals(2) || other.equals(3);
 *     }
 *   });
 * Thread5 cv.acquire(1);
 * ...
 * // Thread 1,2,3 and 4 are blocked
 * // Thread 5 isn't blocked.
 *
 * MainThread cv.set(3);
 *
 * // Thread 1 and 4 are unblocked.
 * // Thread 2 and 3 are still blocked.
 * </pre>
 *
 * @author Simon McDuff
 * @since 2.0
 */
public final class ConcurrentValue<T>
{
  private Object notifier = new Notifier();

  private T value;

  public ConcurrentValue(T value)
  {
    this.value = value;
  }

  public T get()
  {
    return value;
  }

  /**
   * Specify the new value.
   */
  public void set(T newValue)
  {
    synchronized (notifier)
    {
      value = newValue;
      notifier.notifyAll();
    }
  }

  /**
   * Reevaluate the condition. It is only useful if a thread is blocked at {@link #acquire(Object)} and the parameter
   * passed changed. {@link #acquire(Object)} generates a reevaluation automatically.
   */
  public void reevaluate()
  {
    synchronized (notifier)
    {
      notifier.notifyAll();
    }
  }

  /**
   * Blocking call.
   * <p>
   * Return when value accept is equal to {@link #get()}.
   */
  public void acquire(Object accept) throws InterruptedException
  {
    if (accept == null)
    {
      throw new IllegalArgumentException("accept == null"); //$NON-NLS-1$
    }

    synchronized (notifier)
    {
      while (!accept.equals(value))
      {
        notifier.wait();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Notifier extends Object
  {
  }
}
