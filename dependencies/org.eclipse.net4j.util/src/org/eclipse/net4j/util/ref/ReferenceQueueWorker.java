/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - Bug 311840 - OutOfMemory with fast population of revision cache
 */
package org.eclipse.net4j.util.ref;

import org.eclipse.net4j.util.concurrent.Worker;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ReferenceQueueWorker<T> extends Worker
{
  /**
   * @since 3.0
   */
  public static final int DEFAULT_POLL_MILLIS = 1000 * 60; // One minute

  /**
   * @since 3.0
   */
  public static final int ALL_WORK_PER_POLL = -1;

  /**
   * @since 3.0
   */
  public static final int DEFAULT_MAX_WORK_PER_POLL = ALL_WORK_PER_POLL;

  private ReferenceQueue<T> queue = new ReferenceQueue<T>();

  private long pollMillis;

  private int maxWorkPerPoll;

  public ReferenceQueueWorker()
  {
    setPollMillis(DEFAULT_POLL_MILLIS);
    setMaxWorkPerPoll(DEFAULT_MAX_WORK_PER_POLL);
  }

  public long getPollMillis()
  {
    return pollMillis;
  }

  public void setPollMillis(long pollMillis)
  {
    this.pollMillis = pollMillis;
  }

  public int getMaxWorkPerPoll()
  {
    return maxWorkPerPoll;
  }

  public void setMaxWorkPerPoll(int maxWorkPerPoll)
  {
    this.maxWorkPerPoll = maxWorkPerPoll;
  }

  protected ReferenceQueue<T> getQueue()
  {
    return queue;
  }

  @Override
  protected final void work(WorkContext context) throws Exception
  {
    int count = maxWorkPerPoll;
    if (count == ALL_WORK_PER_POLL)
    {
      count = Integer.MAX_VALUE;
    }

    for (int i = 0; i < count; i++)
    {
      Reference<? extends T> reference = queue.poll();
      if (reference == null)
      {
        break;
      }

      work(reference);
    }

    context.nextWork(pollMillis);
  }

  protected abstract void work(Reference<? extends T> reference);
}
