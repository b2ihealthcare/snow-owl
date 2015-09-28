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

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Timeouter
{
  private Timer timer;

  private long timeout;

  private TimerTask timeoutTask;

  private volatile long touched;

  public Timeouter(Timer timer, long timeout)
  {
    this.timer = timer;
    this.timeout = timeout;

    touch();
    scheduleTimeout();
  }

  public long getTimeout()
  {
    return timeout;
  }

  public void setTimeout(long timeout)
  {
    this.timeout = timeout;
  }

  public void touch()
  {
    touched = System.currentTimeMillis();
  }

  public void dispose()
  {
    if (timeoutTask != null)
    {
      TimerTask task = timeoutTask;
      timeoutTask = null;
      task.cancel();
    }
  }

  protected boolean isDisposed()
  {
    return timeoutTask == null;
  }

  protected abstract void handleTimeout(long untouched);

  private void scheduleTimeout()
  {
    timeoutTask = new TimerTask()
    {
      @Override
      public void run()
      {
        try
        {
          if (!isDisposed())
          {
            long untouched = System.currentTimeMillis() - touched;
            if (untouched > timeout)
            {
              timeoutTask = null;
              handleTimeout(untouched);
            }
            else
            {
              scheduleTimeout();
            }
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error("TimeouterTask failed", ex);
        }
      }
    };

    try
    {
      long delay = Math.max(timeout - (System.currentTimeMillis() - touched), 0L);
      timer.schedule(timeoutTask, delay);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }
}
