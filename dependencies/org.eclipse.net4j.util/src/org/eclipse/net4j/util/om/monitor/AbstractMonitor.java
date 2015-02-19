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
package org.eclipse.net4j.util.om.monitor;

import org.eclipse.net4j.internal.util.bundle.OM;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractMonitor implements OMMonitor
{
  private static final long NOT_BEGUN = -1;

  private double totalWork = NOT_BEGUN;

  private double work;

  public AbstractMonitor()
  {
  }

  public boolean hasBegun() throws MonitorCanceledException
  {
    checkCanceled();
    return totalWork != NOT_BEGUN;
  }

  public OMMonitor begin(double totalWork) throws MonitorCanceledException
  {
    checkCanceled();
    this.totalWork = totalWork;
    return this;
  }

  public OMMonitor begin() throws MonitorCanceledException
  {
    return begin(ONE);
  }

  public void worked(double work) throws MonitorCanceledException
  {
    checkBegun();
    this.work += work;
  }

  public void worked() throws MonitorCanceledException
  {
    worked(ONE);
  }

  public OMMonitor fork(double work)
  {
    checkBegun();
    return createNestedMonitor(work);
  }

  public OMMonitor fork()
  {
    return fork(ONE);
  }

  public Async forkAsync(double work)
  {
    checkBegun();
    AsyncTimerTask asyncTimerTask = createAsyncTimerTask(work);
    if (asyncTimerTask == null)
    {
      throw new NullPointerException("No async timer task has been created");
    }

    long period = getAsyncSchedulePeriod();
    scheduleAtFixedRate(asyncTimerTask, period, period);
    return asyncTimerTask;
  }

  public Async forkAsync()
  {
    return forkAsync(ONE);
  }

  public void done()
  {
    if (!isCanceled())
    {
      double rest = totalWork - work;
      if (rest > 0)
      {
        worked(rest);
      }
    }
  }

  public double getTotalWork()
  {
    return totalWork;
  }

  public double getWork()
  {
    return work;
  }

  public double getWorkPercent()
  {
    return percent(work, totalWork);
  }

  protected OMMonitor createNestedMonitor(double work)
  {
    return new NestedMonitor(this, work);
  }

  protected AsyncTimerTask createAsyncTimerTask(double work)
  {
    return new AsyncTimerTask(this, work, DEFAULT_TIME_FACTOR);
  }

  protected abstract long getAsyncSchedulePeriod();

  protected abstract Timer getTimer();

  /**
   * @since 3.0
   */
  protected abstract void scheduleAtFixedRate(TimerTask task, long delay, long period);

  private void checkBegun() throws MonitorCanceledException
  {
    if (!hasBegun())
    {
      throw new IllegalStateException("begin() has not been called"); //$NON-NLS-1$
    }
  }

  /**
   * @since 3.1
   */
  protected static double percent(double part, double whole)
  {
    return Math.min(part * HUNDRED / whole, HUNDRED);
  }

  /**
   * @author Eike Stepper
   */
  public static class AsyncTimerTask extends TimerTask implements Async
  {
    private OMMonitor monitor;

    private boolean canceled;

    public AsyncTimerTask(AbstractMonitor parent, double parentWork, double timeFactor)
    {
      monitor = parent.fork(parentWork);
      monitor.begin();
    }

    @Override
    public void run()
    {
      try
      {
        if (!canceled)
        {
          double work = 1 - monitor.getWork();
          monitor.worked(work / TEN);
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error("AsyncTimerTask failed", ex);
      }
    }

    public void stop()
    {
      try
      {
        monitor.done();
        cancel();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    @Override
    public boolean cancel()
    {
      canceled = true;
      return super.cancel();
    }
  }
}
