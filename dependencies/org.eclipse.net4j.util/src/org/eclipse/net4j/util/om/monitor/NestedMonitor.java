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

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class NestedMonitor extends AbstractMonitor
{
  private AbstractMonitor parent;

  private double parentWork;

  private double sentToParent;

  private double scale;

  private boolean usedUp;

  public NestedMonitor(AbstractMonitor parent, double parentWork)
  {
    this.parent = parent;
    this.parentWork = parentWork > ZERO ? parentWork : ZERO;
  }

  public AbstractMonitor getParent()
  {
    return parent;
  }

  public double getParentWork()
  {
    return parentWork;
  }

  public boolean isCanceled()
  {
    return parent.isCanceled();
  }

  public void checkCanceled() throws MonitorCanceledException
  {
    parent.checkCanceled();
  }

  @Override
  public OMMonitor begin(double totalWork) throws MonitorCanceledException
  {
    super.begin(totalWork);
    scale = totalWork > ZERO ? parentWork / totalWork : ZERO;
    return this;
  }

  @Override
  public void worked(double work) throws MonitorCanceledException
  {
    if (!usedUp)
    {
      super.worked(work);
      double realWork = work > ZERO ? scale * work : ZERO;
      parent.worked(realWork);
      sentToParent += realWork;
      if (sentToParent >= parentWork)
      {
        usedUp = true;
      }
    }
  }

  @Override
  public void done()
  {
    super.done();
    sentToParent = ZERO;
    usedUp = true;
  }

  @Override
  protected long getAsyncSchedulePeriod()
  {
    return parent.getAsyncSchedulePeriod();
  }

  @Override
  protected void scheduleAtFixedRate(TimerTask task, long delay, long period)
  {
    parent.scheduleAtFixedRate(task, delay, period);
  }

  @Override
  protected Timer getTimer()
  {
    return parent.getTimer();
  }
}
