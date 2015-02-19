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

/**
 * A {@link Runnable runnable} that can be {@link #stop() stopped}.
 *
 * @since 3.2
 * @author Eike Stepper
 */
public abstract class Stoppable implements Runnable
{
  private boolean stopped;

  public Stoppable()
  {
  }

  public final boolean isStopped()
  {
    return stopped;
  }

  public final void stop()
  {
    stopped = true;
  }

  public final void run()
  {
    while (!isStopped())
    {
      try
      {
        doRun();
      }
      catch (InterruptedException ex)
      {
        stop();
      }
      catch (Throwable t)
      {
        OM.LOG.error(t);
      }
    }
  }

  protected abstract void doRun() throws InterruptedException;
}
