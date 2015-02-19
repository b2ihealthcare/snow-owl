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
import org.eclipse.net4j.util.IErrorHandler;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Worker extends Lifecycle
{
  public static final int DEFAULT_TIMEOUT = 10000;

  /**
   * @since 3.0
   */
  public static final IErrorHandler DEFAULT_ERROR_HANDLER = new IErrorHandler()
  {
    public void handleError(Throwable t)
    {
      OM.LOG.error(t);
    }
  };

  private static IErrorHandler globalErrorHandler = DEFAULT_ERROR_HANDLER;

  private boolean daemon;

  private long activationTimeout = DEFAULT_TIMEOUT;

  private long deactivationTimeout = DEFAULT_TIMEOUT;

  @ExcludeFromDump
  private transient CountDownLatch activationLatch;

  @ExcludeFromDump
  private transient WorkerThread workerThread;

  public Worker()
  {
  }

  public boolean isDaemon()
  {
    return daemon;
  }

  public void setDaemon(boolean daemon)
  {
    this.daemon = daemon;
  }

  public long getActivationTimeout()
  {
    return activationTimeout;
  }

  public void setActivationTimeout(long activationTimeout)
  {
    this.activationTimeout = activationTimeout;
  }

  public long getDeactivationTimeout()
  {
    return deactivationTimeout;
  }

  public void setDeactivationTimeout(long deactivationTimeout)
  {
    this.deactivationTimeout = deactivationTimeout;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    activationLatch = new CountDownLatch(1);
    String threadName = getThreadName();
    workerThread = threadName == null ? new WorkerThread() : new WorkerThread(threadName);
    workerThread.start();
    if (!activationLatch.await(activationTimeout, TimeUnit.MILLISECONDS))
    {
      try
      {
        workerThread.stopRunning();
        workerThread.interrupt();
      }
      catch (RuntimeException ex)
      {
        OM.LOG.warn(ex);
      }

      throw new TimeoutException("Worker thread activation timed out after " + activationTimeout + " millis"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      workerThread.stopRunning();
      workerThread.interrupt();
      workerThread.join(deactivationTimeout);
    }
    catch (RuntimeException ex)
    {
      OM.LOG.warn(ex);
    }

    super.doDeactivate();
  }

  protected String getThreadName()
  {
    return null;
  }

  /**
   * @since 3.1
   */
  protected void handleError(Exception ex)
  {
    try
    {
      if (globalErrorHandler != null)
      {
        globalErrorHandler.handleError(ex);
      }
    }
    catch (Exception ex1)
    {
      OM.LOG.error(ex1);
    }
  }

  protected abstract void work(WorkContext context) throws Exception;

  /**
   * @since 3.0
   */
  public static IErrorHandler getGlobalErrorHandler()
  {
    return globalErrorHandler;
  }

  /**
   * @since 3.0
   */
  public static IErrorHandler setGlobalErrorHandler(IErrorHandler globalErrorHandler)
  {
    IErrorHandler oldHandler = Worker.globalErrorHandler;
    Worker.globalErrorHandler = globalErrorHandler;
    return oldHandler;
  }

  /**
   * @author Eike Stepper
   */
  private final class WorkerThread extends Thread
  {
    private boolean running = true;

    public WorkerThread()
    {
      setDaemon(daemon);
    }

    public WorkerThread(String threadName)
    {
      super(threadName);
      setDaemon(daemon);
    }

    public void stopRunning()
    {
      running = false;
    }

    @Override
    public void run()
    {
      WorkContext context = new WorkContext();
      activationLatch.countDown();
      while (running && !isInterrupted())
      {
        try
        {
          context.increaseCount();
          work(context);
        }
        catch (NextWork nextWork)
        {
          try
          {
            nextWork.pause();
          }
          catch (InterruptedException ex)
          {
            break;
          }
        }
        catch (Terminate terminate)
        {
          break;
        }
        catch (InterruptedException ex)
        {
          break;
        }
        catch (Exception ex)
        {
          handleError(ex);
        }
      }

      deactivate();
    }
  }

  /**
   * @author Eike Stepper
   */
  public class WorkContext
  {
    private long count;

    public WorkContext()
    {
    }

    public long getCount()
    {
      return count;
    }

    public void nextWork()
    {
      throw new NextWork();
    }

    public void nextWork(long pauseMillis)
    {
      throw new NextWork(pauseMillis);
    }

    public void terminate()
    {
      throw new Terminate();
    }

    private void increaseCount()
    {
      ++count;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class NextWork extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    private long pauseMillis;

    public NextWork()
    {
    }

    public NextWork(long pauseMillis)
    {
      this.pauseMillis = pauseMillis;
    }

    public void pause() throws InterruptedException
    {
      if (pauseMillis > 0)
      {
        Thread.sleep(pauseMillis);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Terminate extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public Terminate()
    {
    }
  }
}
