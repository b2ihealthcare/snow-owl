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

import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.concurrent.Timeouter;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class TimeoutMonitor extends Monitor
{
  private long timeout;

  private transient Timeouter timeouter;

  public TimeoutMonitor(long timeout)
  {
    this.timeout = timeout;
  }

  public long getTimeout()
  {
    return timeout;
  }

  public void setTimeout(long timeout)
  {
    this.timeout = timeout;
    if (timeouter != null)
    {
      timeouter.setTimeout(timeout);
    }
  }

  public void touch()
  {
    if (timeouter != null)
    {
      timeouter.touch();
    }
  }

  @Override
  public OMMonitor begin(double totalWork)
  {
    timeouter = new Timeouter(getTimer(), timeout)
    {
      @Override
      protected void handleTimeout(long untouched)
      {
        TimeoutMonitor.this.handleTimeout(untouched);
      }
    };

    touch();
    super.begin(totalWork);
    return this;
  }

  @Override
  public void worked(double work)
  {
    touch();
    super.worked(work);
  }

  @Override
  public OMMonitor fork(double work)
  {
    touch();
    return super.fork(work);
  }

  @Override
  public Async forkAsync(double work)
  {
    touch();
    return super.forkAsync(work);
  }

  @Override
  public void done()
  {
    cancelTimeouter();
    super.done();
  }

  @Override
  public void cancel(RuntimeException cancelException)
  {
    cancelTimeouter();
    super.cancel(cancelException);
  }

  @Override
  public boolean isCanceled()
  {
    touch();
    return super.isCanceled();
  }

  @Override
  public void checkCanceled() throws MonitorCanceledException
  {
    touch();
    super.checkCanceled();
  }

  protected void handleTimeout(long untouched)
  {
    cancel(new TimeoutRuntimeException("Timeout after " + untouched + " millis"));
  }

  private void cancelTimeouter()
  {
    if (timeouter != null)
    {
      timeouter.dispose();
      timeouter = null;
    }
  }
}
