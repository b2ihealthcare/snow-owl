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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public final class NonBlockingIntCounter
{
  private AtomicInteger value;

  public NonBlockingIntCounter()
  {
    this(0);
  }

  public NonBlockingIntCounter(int initialValue)
  {
    value = new AtomicInteger(initialValue);
  }

  public int getValue()
  {
    return value.get();
  }

  public int increment()
  {
    int v;
    do
    {
      v = value.get();
    } while (!value.compareAndSet(v, v + 1));

    return v + 1;
  }

  /**
   * @since 3.0
   */
  public int decrement()
  {
    int v;
    do
    {
      v = value.get();
    } while (!value.compareAndSet(v, v - 1));

    return v - 1;
  }

  @Override
  public String toString()
  {
    return Long.toString(getValue());
  }
}
