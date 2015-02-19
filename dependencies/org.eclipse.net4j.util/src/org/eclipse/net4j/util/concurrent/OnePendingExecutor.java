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

import java.util.concurrent.Executor;

public class OnePendingExecutor implements Executor
{
  private Runnable command;

  private Thread thread;

  public OnePendingExecutor()
  {
  }

  public synchronized void execute(Runnable command)
  {
    if (this.command != null)
    {
      throw new IllegalStateException("One command already pending"); //$NON-NLS-1$
    }

    this.command = command;
    if (thread == null)
    {
      thread = new Thread()
      {
        @Override
        public void run()
        {
          for (;;)
          {
            Runnable command;
            synchronized (OnePendingExecutor.this)
            {
              if (OnePendingExecutor.this.command == null)
              {
                thread = null;
                return;
              }

              command = OnePendingExecutor.this.command;
              OnePendingExecutor.this.command = null;
            }

            command.run();
          }
        }
      };

      thread.setDaemon(true);
      thread.start();
    }
  }
}
