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
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.WrappedException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public abstract class NegotiationContext implements INegotiationContext
{
  private Receiver receiver;

  private Enum<?> state;

  private Object info;

  private CountDownLatch finishedLatch = new CountDownLatch(1);

  public NegotiationContext()
  {
  }

  public Receiver getReceiver()
  {
    return receiver;
  }

  public void setReceiver(Receiver receiver)
  {
    this.receiver = receiver;
  }

  public Enum<?> getState()
  {
    return state;
  }

  public void setState(Enum<?> state)
  {
    this.state = state;
  }

  public Object getInfo()
  {
    return info;
  }

  public void setInfo(Object info)
  {
    this.info = info;
  }

  public void setFinished(boolean success)
  {
    if (finishedLatch != null)
    {
      finishedLatch.countDown();
    }
  }

  public Enum<?> waitUntilFinished(long timeout)
  {
    if (finishedLatch == null)
    {
      throw new IllegalStateException("finishedLatch == null"); //$NON-NLS-1$
    }

    try
    {
      if (timeout == NO_TIMEOUT)
      {
        finishedLatch.await();
      }
      else
      {
        finishedLatch.await(timeout, TimeUnit.MILLISECONDS);
      }
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      finishedLatch = null;
    }

    return state;
  }
}
