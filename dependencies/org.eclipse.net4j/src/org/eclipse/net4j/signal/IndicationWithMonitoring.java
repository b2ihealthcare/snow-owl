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
package org.eclipse.net4j.signal;

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

import org.eclipse.internal.net4j.bundle.OM;
import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.TimeoutMonitor;

/**
 * Represents the receiver side of a two-way {@link IndicationWithResponse signal} with additional support for remote progress monitoring.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class IndicationWithMonitoring extends IndicationWithResponse
{
  private ReportingMonitor monitor;

  /**
   * @since 2.0
   */
  public IndicationWithMonitoring(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  /**
   * @since 2.0
   */
  public IndicationWithMonitoring(SignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  /**
   * @since 2.0
   */
  public IndicationWithMonitoring(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    try
    {
      super.execute(in, out);
    }
    finally
    {
      if (monitor != null)
      {
        monitor.done();
        monitor = null;
      }
    }
  }

  @Override
  protected final void indicating(ExtendedDataInputStream in) throws Exception
  {
    int monitorProgressSeconds = in.readInt();
    int monitorTimeoutSeconds = in.readInt();

    monitor = new ReportingMonitor(monitorProgressSeconds, monitorTimeoutSeconds);
    monitor.begin(OMMonitor.HUNDRED);

    indicating(in, monitor.fork(getIndicatingWorkPercent()));
  }

  @Override
  protected final void responding(ExtendedDataOutputStream out) throws Exception
  {
    responding(out, monitor.fork(OMMonitor.HUNDRED - getIndicatingWorkPercent()));
  }

  protected abstract void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception;

  protected abstract void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception;

  /**
   * @since 2.0
   */
  protected ExecutorService getMonitoringExecutorService()
  {
    return getProtocol().getExecutorService();
  }

  protected int getIndicatingWorkPercent()
  {
    return 99;
  }

  void setMonitorCanceled()
  {
    monitor.cancel();
  }

  /**
   * @author Eike Stepper
   */
  private final class ReportingMonitor extends TimeoutMonitor
  {
    private TimerTask sendProgressTask = new TimerTask()
    {
      @Override
      public void run()
      {
        try
        {
          sendProgress();
        }
        catch (Throwable ex)
        {
          OM.LOG.warn("ReportingMonitorTask failed " + ex.getMessage());
          cancel();
        }
      }
    };

    public ReportingMonitor(int monitorProgressSeconds, int monitorTimeoutSeconds)
    {
      super(1000L * monitorTimeoutSeconds);
      long period = 1000L * monitorProgressSeconds;
      scheduleAtFixedRate(sendProgressTask, period, period);
    }

    @Override
    public void cancel(RuntimeException cancelException)
    {
      sendProgressTask.cancel();
      super.cancel(cancelException);
    }

    @Override
    public void done()
    {
      sendProgressTask.cancel();
      super.done();
    }

    private void sendProgress() throws Exception
    {
    	SignalProtocol<?> protocol = getProtocol();

        try
        {
          int correlationID = -getCorrelationID();
          double totalWork = getTotalWork();
          double work = getWork();

          new MonitorProgressRequest(protocol, correlationID, totalWork, work).sendAsync();
        }
        catch (Exception ex)
        {
          IChannel channel = protocol.getChannel();
          if (LifecycleUtil.isActive(channel))
          {
            OM.LOG.error(ex);
          }
        }
    }
  }
}
