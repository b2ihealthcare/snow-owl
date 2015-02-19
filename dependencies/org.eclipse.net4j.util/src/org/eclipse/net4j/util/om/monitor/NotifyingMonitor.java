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

import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.event.Notifier;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class NotifyingMonitor extends Monitor implements INotifier
{
  private Notifier notifier = new Notifier();

  private String task;

  public NotifyingMonitor()
  {
  }

  public NotifyingMonitor(String task)
  {
    this.task = task;
  }

  public NotifyingMonitor(String task, IListener[] listeners)
  {
    this(task);
    if (listeners != null)
    {
      for (IListener listener : listeners)
      {
        addListener(listener);
      }
    }
  }

  public String getTask()
  {
    return task;
  }

  public boolean hasListeners()
  {
    return notifier.hasListeners();
  }

  public IListener[] getListeners()
  {
    return notifier.getListeners();
  }

  public void addListener(IListener listener)
  {
    notifier.addListener(listener);
  }

  public void removeListener(IListener listener)
  {
    notifier.removeListener(listener);
  }

  @Override
  public void worked(double work) throws MonitorCanceledException
  {
    super.worked(work);

    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      notifier.fireEvent(new ProgressEvent(this, getTotalWork(), getWork()), listeners);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ProgressEvent extends Event implements OMMonitorProgress
  {
    private static final long serialVersionUID = 1L;

    private double totalWork;

    private double work;

    /**
     * @since 3.2
     */
    public ProgressEvent(INotifier notifier, double totalWork, double work)
    {
      super(notifier);
      this.totalWork = totalWork;
      this.work = work;
    }

    public String getTask()
    {
      INotifier source = getSource();
      if (source instanceof NotifyingMonitor)
      {
        NotifyingMonitor monitor = (NotifyingMonitor)source;
        return monitor.getTask();
      }

      return null;
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
  }
}
