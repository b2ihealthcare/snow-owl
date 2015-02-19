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

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class EclipseMonitor extends Monitor
{
  private IProgressMonitor progressMonitor;

  private String taskName;

  public EclipseMonitor(IProgressMonitor progressMonitor, String taskName)
  {
    this.progressMonitor = progressMonitor;
    this.taskName = taskName;
  }

  public EclipseMonitor(IProgressMonitor progressMonitor)
  {
    this(progressMonitor, StringUtil.EMPTY);
  }

  public String getTaskName()
  {
    return taskName;
  }

  @Override
  public boolean isCanceled()
  {
    if (super.isCanceled())
    {
      return true;
    }

    return progressMonitor.isCanceled();
  }

  @Override
  public OMMonitor begin(double totalWork) throws MonitorCanceledException
  {
    super.begin(totalWork);
    int eclipseWork = totalWork > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)totalWork;
    progressMonitor.beginTask(taskName, eclipseWork);
    return this;
  }

  @Override
  public void worked(double work) throws MonitorCanceledException
  {
    super.worked(work);
    progressMonitor.internalWorked(work);
  }

  @Override
  public void done()
  {
    super.done();
    progressMonitor.done();
  }

  /**
   * A sub progress monitor that synchronizes all methods on the parent monitor instance.
   * 
   * @author Eike Stepper
   * @since 3.0
   */
  public static class SynchronizedSubProgressMonitor extends SubProgressMonitor
  {
    public SynchronizedSubProgressMonitor(IProgressMonitor monitor, int ticks)
    {
      super(monitor, ticks);
    }

    @Override
    public void beginTask(String name, int totalWork)
    {
      synchronized (getWrappedProgressMonitor())
      {
        super.beginTask(name, totalWork);
      }
    }

    @Override
    public void clearBlocked()
    {
      synchronized (getWrappedProgressMonitor())
      {
        super.clearBlocked();
      }
    }

    @Override
    public void done()
    {
      synchronized (getWrappedProgressMonitor())
      {
        super.done();
      }
    }

    @Override
    public void internalWorked(double work)
    {
      synchronized (getWrappedProgressMonitor())
      {
        super.internalWorked(work);
      }
    }

    @Override
    public boolean isCanceled()
    {
      synchronized (getWrappedProgressMonitor())
      {
        return super.isCanceled();
      }
    }

    @Override
    public void setBlocked(IStatus reason)
    {
      synchronized (getWrappedProgressMonitor())
      {
        super.setBlocked(reason);
      }
    }

    @Override
    public void setCanceled(boolean b)
    {
      synchronized (getWrappedProgressMonitor())
      {
        super.setCanceled(b);
      }
    }

    @Override
    public void setTaskName(String name)
    {
      synchronized (getWrappedProgressMonitor())
      {
        super.setTaskName(name);
      }
    }

    @Override
    public void subTask(String name)
    {
      synchronized (getWrappedProgressMonitor())
      {
        super.subTask(name);
      }
    }

    @Override
    public void worked(int work)
    {
      synchronized (getWrappedProgressMonitor())
      {
        super.worked(work);
      }
    }
  }
}
