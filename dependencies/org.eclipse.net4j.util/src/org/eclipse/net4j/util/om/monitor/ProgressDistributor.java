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
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.Arrays;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ProgressDistributor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_MONITOR, ProgressDistributor.class);

  private double[] distribution;

  public ProgressDistributor()
  {
  }

  public final <CONTEXT> void run(ProgressDistributable<CONTEXT>[] distributables, CONTEXT context, OMMonitor monitor)
      throws RuntimeException, WrappedException
  {
    double[] distributionCopy;
    synchronized (this)
    {
      if (distribution == null)
      {
        distribution = new double[distributables.length];
        Arrays.fill(distribution, OMMonitor.ONE);
      }
      else
      {
        CheckUtil.checkArg(distribution.length == distributables.length, "distributables.length"); //$NON-NLS-1$
      }

      distributionCopy = new double[distribution.length];
      System.arraycopy(distribution, 0, distributionCopy, 0, distribution.length);
    }

    double total = OMMonitor.ZERO;
    for (int i = 0; i < distributionCopy.length; i++)
    {
      total += distributionCopy[i];
    }

    if (TRACER.isEnabled())
    {
      StringBuilder builder = new StringBuilder("Distribution: "); //$NON-NLS-1$
      for (int i = 0; i < distributionCopy.length; i++)
      {
        builder.append(distributionCopy[i] * OMMonitor.HUNDRED / total);
        builder.append("%, "); //$NON-NLS-1$
      }

      builder.append("("); //$NON-NLS-1$
      builder.append(this);
      builder.append(")"); //$NON-NLS-1$
      TRACER.trace(builder.toString());
    }

    monitor.begin(total);

    try
    {
      double[] times = new double[distributables.length];
      for (int i = 0; i < distributables.length; i++)
      {
        ProgressDistributable<CONTEXT> distributable = distributables[i];
        int count = distributable.getLoopCount(context);
        double work = distributable.getLoopWork(context);

        OMMonitor distributableMonitor = monitor.fork(distributionCopy[i]);
        distributableMonitor.begin(work * count);

        try
        {
          long start = System.currentTimeMillis();
          for (int loop = 0; loop < count; loop++)
          {
            try
            {
              distributable.runLoop(loop, context, distributableMonitor);
            }
            catch (Exception ex)
            {
              throw WrappedException.wrap(ex);
            }
          }

          times[i] = (double)(System.currentTimeMillis() - start) / count;
        }
        finally
        {
          distributableMonitor.done();
        }
      }

      synchronized (this)
      {
        distribute(distribution, times);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected abstract void distribute(double[] distribution, double[] times);

  public static <CONTEXT> ProgressDistributable<CONTEXT>[] array(ProgressDistributable<CONTEXT>... ops)
  {
    return ops;
  }

  /**
   * @author Eike Stepper
   */
  public static class Arithmetic extends ProgressDistributor
  {
    private long count;

    private double[] times;

    public Arithmetic()
    {
    }

    @Override
    protected void distribute(double[] distribution, double[] times)
    {
      ++count;
      for (int i = 0; i < times.length; i++)
      {
        this.times[i] += times[i];
        distribution[i] = this.times[i] / count;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Geometric extends ProgressDistributor
  {
    public Geometric()
    {
    }

    @Override
    protected void distribute(double[] distribution, double[] times)
    {
      for (int i = 0; i < times.length; i++)
      {
        distribution[i] = (distribution[i] + times[i]) / 2;
      }
    }
  }
}
