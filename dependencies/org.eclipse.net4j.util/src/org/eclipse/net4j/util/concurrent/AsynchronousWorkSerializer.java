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

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class AsynchronousWorkSerializer implements IWorkSerializer, Runnable
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CONCURRENCY, AsynchronousWorkSerializer.class);

  private ExecutorService executorService;

  private Queue<Runnable> workQueue;

  private Occupation occupation = new Occupation();

  // private Object newElementLock = new Object();

  public AsynchronousWorkSerializer(ExecutorService executorService, Queue<Runnable> workQueue)
  {
    if (executorService == null)
    {
      throw new IllegalArgumentException("executorService == null"); //$NON-NLS-1$
    }

    this.executorService = executorService;
    this.workQueue = workQueue;
  }

  public AsynchronousWorkSerializer(ExecutorService executorService)
  {
    this(executorService, new ConcurrentLinkedQueue<Runnable>());
  }

  public ExecutorService getExecutorService()
  {
    return executorService;
  }

  public boolean addWork(Runnable work)
  {
    // Need to be a block of execution. Cannot add when doing last check
    // XXX synchronized (newElementLock)
    {
      workQueue.add(work);

      // isOccupied can (and must) be called unsynchronized here
      if (!occupation.isOccupied())
      {
        synchronized (occupation)
        {
          occupation.setOccupied(true);
        }

        if (TRACER.isEnabled())
        {
          TRACER.trace("Notifying executor service"); //$NON-NLS-1$
        }

        executorService.execute(this);
      }
    }

    return true;
  }

  /**
   * Executed in the context of the {@link #getExecutorService() executor service}.
   * <p>
   */
  public void run()
  {
    // XXX synchronized (occupation)
    {
      Runnable work;
      // for (;;)
      {
        while (occupation.isOccupied() && (work = workQueue.poll()) != null)
        {
          try
          {
            work.run();
          }
          catch (RuntimeException ex)
          {
            if (TRACER.isEnabled())
            {
              TRACER.trace(ex);
            }
          }
        }

        // ConcurrencyUtil.sleep(500);

        // Could put the sync in the while loop... but not efficient.
        // Doing a last check to make sure that no one added something in the
        // queue
        // synchronized (newElementLock)
        // {
        // if (!occupation.isOccupied() || (work = workQueue.peek()) == null)
        // {
        // occupation.setOccupied(false);
        // break;
        // }
        // }
      }
    }
  }

  public void dispose()
  {
    if (occupation.isOccupied())
    {
      occupation.setOccupied(false);
    }

    workQueue.clear();
    workQueue = null;
    executorService = null;
  }

  @Override
  public String toString()
  {
    return AsynchronousWorkSerializer.class.getSimpleName();
  }

  /**
   * @author Eike Stepper
   */
  private static final class Occupation
  {
    private boolean occupied;

    public boolean isOccupied()
    {
      return occupied;
    }

    public void setOccupied(boolean occupied)
    {
      this.occupied = occupied;
    }
  }
}
