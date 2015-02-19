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

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class DelegatingMonitor implements OMMonitor
{
  private OMMonitor delegate;

  public DelegatingMonitor(OMMonitor delegate)
  {
    this.delegate = delegate;
  }

  public OMMonitor getDelegate()
  {
    return delegate;
  }

  public boolean hasBegun() throws MonitorCanceledException
  {
    return delegate.hasBegun();
  }

  public OMMonitor begin() throws MonitorCanceledException
  {
    return delegate.begin();
  }

  public OMMonitor begin(double totalWork) throws MonitorCanceledException
  {
    return delegate.begin(totalWork);
  }

  public void checkCanceled() throws MonitorCanceledException
  {
    delegate.checkCanceled();
  }

  public void done()
  {
    delegate.done();
  }

  public OMMonitor fork()
  {
    return delegate.fork();
  }

  public OMMonitor fork(double work)
  {
    return delegate.fork(work);
  }

  public Async forkAsync()
  {
    return delegate.forkAsync();
  }

  public Async forkAsync(double work)
  {
    return delegate.forkAsync(work);
  }

  public double getTotalWork()
  {
    return delegate.getTotalWork();
  }

  public double getWork()
  {
    return delegate.getWork();
  }

  public double getWorkPercent()
  {
    return delegate.getWorkPercent();
  }

  public boolean isCanceled()
  {
    return delegate.isCanceled();
  }

  public void worked() throws MonitorCanceledException
  {
    delegate.worked();
  }

  public void worked(double work) throws MonitorCanceledException
  {
    delegate.worked(work);
  }
}
