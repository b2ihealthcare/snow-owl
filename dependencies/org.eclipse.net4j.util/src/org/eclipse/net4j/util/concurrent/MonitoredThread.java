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
import org.eclipse.net4j.util.WrappedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class MonitoredThread extends Thread
{
  private MonitoredThread.ThreadMonitor monitor;

  private long timeStamp;

  private boolean shutdown;

  public MonitoredThread(String name, MonitoredThread.ThreadMonitor monitor)
  {
    super(name);
    this.monitor = monitor;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public boolean isIdleTimeoutExpired(long idleTimeOut)
  {
    if (timeStamp != 0L) // Skip in first loop
    {
      long idle = System.currentTimeMillis() - timeStamp;
      return idle > idleTimeOut;
    }

    return false;
  }

  public void heartBeat()
  {
    if (shutdown)
    {
      throw new ShutdownException();
    }

    timeStamp = System.currentTimeMillis();
  }

  public void shutdown()
  {
    shutdown = true;
  }

  @Override
  public void run()
  {
    monitor.handleStarting(this);

    try
    {
      doRun();
    }
    catch (MonitoredThread.ShutdownException ex)
    {
      return;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      throw WrappedException.wrap(ex);
    }
    finally
    {
      monitor.handleFinished(this);
    }
  }

  protected abstract void doRun() throws Exception;

  /**
   * @author Eike Stepper
   */
  private static final class ShutdownException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;
  }

  /**
   * @author Eike Stepper
   */
  public static interface ThreadMonitor
  {
    public void handleStarting(MonitoredThread thread);

    public void handleFinished(MonitoredThread thread);
  }

  /**
   * @author Eike Stepper
   */
  public static class MultiThreadMonitor implements MonitoredThread.ThreadMonitor, Runnable
  {
    public static final long SYNCED_START = -1;

    private long idleTimeOut;

    private long startOffset;

    private CountDownLatch startLatch;

    private List<MonitoredThread> threads = new ArrayList<MonitoredThread>();

    /**
     * @param idleTimeOut
     *          The number of milli seconds one of the threads may be idle (i.e. not having called
     *          {@link MonitoredThread#heartBeat()}) before {@link #handleTimeoutExpiration(MonitoredThread)} is called.
     * @param startOffset
     *          The number of milli seconds to sleep between threads are started. Zero means not to sleep and
     *          {@link #SYNCED_START} means that all threads start at the same time by waiting on a shared latch.
     */
    public MultiThreadMonitor(long idleTimeOut, long startOffset)
    {
      this.idleTimeOut = idleTimeOut;
      this.startOffset = startOffset;
      if (startOffset == SYNCED_START)
      {
        startLatch = new CountDownLatch(1);
      }
    }

    /**
     * Same as calling <tt>MonitoredThread(idleTimeOut, SYNCED_START)</tt>.
     */
    public MultiThreadMonitor(long timeOut)
    {
      this(timeOut, SYNCED_START);
    }

    public long getIdleTimeOut()
    {
      return idleTimeOut;
    }

    public void addThread(MonitoredThread thread)
    {
      synchronized (threads)
      {
        threads.add(thread);
      }
    }

    public void handleStarting(MonitoredThread thread)
    {
      if (startLatch != null)
      {
        try
        {
          startLatch.await();
        }
        catch (InterruptedException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
      else if (startOffset > 0L)
      {
        ConcurrencyUtil.sleep(startOffset);
      }
    }

    public void handleFinished(MonitoredThread thread)
    {
      synchronized (threads)
      {
        threads.remove(thread);
      }
    }

    public void run()
    {
      startupThreads();

      for (;;)
      {
        List<MonitoredThread> idleThreads = new ArrayList<MonitoredThread>();
        synchronized (threads)
        {
          if (threads.isEmpty())
          {
            break;
          }

          for (MonitoredThread thread : threads)
          {
            if (thread.isIdleTimeoutExpired(idleTimeOut))
            {
              idleThreads.add(thread);
            }
          }
        }

        for (MonitoredThread thread : idleThreads)
        {
          handleTimeoutExpiration(thread);
        }
      }

      ConcurrencyUtil.sleep(10);
    }

    protected void handleTimeoutExpiration(MonitoredThread thread)
    {
      synchronized (threads)
      {
        threads.remove(thread);
      }

      shutdownThreads();
      throw new RuntimeException("Idle timeout expired: " + thread.getName()); //$NON-NLS-1$
    }

    private void startupThreads()
    {
      for (MonitoredThread thread : threads)
      {
        thread.start();
      }

      if (startLatch != null)
      {
        startLatch.countDown();
      }
    }

    private void shutdownThreads()
    {
      synchronized (threads)
      {
        for (MonitoredThread t : threads)
        {
          t.shutdown();
        }
      }
    }
  }
}
