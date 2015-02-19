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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class SynchronizingCorrelator<CORRELATION, RESULT> implements ICorrelator<CORRELATION, ISynchronizer<RESULT>>
{
  private ConcurrentMap<CORRELATION, ISynchronizer<RESULT>> map = new ConcurrentHashMap<CORRELATION, ISynchronizer<RESULT>>(
      0);

  /**
   * @since 3.0
   */
  public ISynchronizer<RESULT> getSynchronizer(CORRELATION correlation)
  {
    return map.get(correlation);
  }

  public boolean isCorrelated(CORRELATION correlation)
  {
    return map.containsKey(correlation);
  }

  public ISynchronizer<RESULT> correlate(CORRELATION correlation)
  {
    ISynchronizer<RESULT> synchronizer = map.get(correlation);
    if (synchronizer == null)
    {
      synchronizer = createSynchronizer(correlation);
      map.put(correlation, synchronizer);
    }

    return synchronizer;
  }

  public ISynchronizer<RESULT> correlateUnique(CORRELATION correlation)
  {
    ISynchronizer<RESULT> synchronizer = createSynchronizer(correlation);
    if (map.putIfAbsent(correlation, synchronizer) != null)
    {
      throw new IllegalStateException("Already correlated: " + correlation); //$NON-NLS-1$
    }

    return synchronizer;
  }

  public ISynchronizer<RESULT> uncorrelate(CORRELATION correlation)
  {
    return map.remove(correlation);
  }

  public RESULT get(CORRELATION correlation, long timeout)
  {
    return correlate(correlation).get(timeout);
  }

  public void put(CORRELATION correlation, RESULT result)
  {
    correlate(correlation).put(result);
  }

  /**
   * @since 3.0
   */
  public boolean putIfCorrelated(CORRELATION correlation, RESULT result)
  {
    ISynchronizer<RESULT> synchronizer = getSynchronizer(correlation);
    if (synchronizer != null)
    {
      synchronizer.put(result);
      return true;
    }

    return false;
  }

  public boolean put(CORRELATION correlation, RESULT result, long timeout)
  {
    return correlate(correlation).put(result, timeout);
  }

  protected ISynchronizer<RESULT> createSynchronizer(final CORRELATION correlation)
  {
    // TODO Make top level class
    return new ISynchronizer<RESULT>()
    {
      private ISynchronizer<RESULT> delegate = new ResultSynchronizer<RESULT>();

      public RESULT get(long timeout)
      {
        RESULT result = delegate.get(timeout);
        uncorrelate(correlation);
        return result;
      }

      public void put(RESULT result)
      {
        delegate.put(result);
      }

      public boolean put(RESULT result, long timeout)
      {
        return delegate.put(result, timeout);
      }
    };
  }

  @Override
  public String toString()
  {
    return "SynchronizingCorrelator" + map; //$NON-NLS-1$
  }
}
