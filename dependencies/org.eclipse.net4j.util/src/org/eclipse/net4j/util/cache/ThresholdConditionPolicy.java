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
package org.eclipse.net4j.util.cache;

import org.eclipse.net4j.util.cache.ICacheMonitor.Condition;

/**
 * @author Eike Stepper
 */
public class ThresholdConditionPolicy implements ICacheMonitor.ConditionPolicy
{
  private long thresholdRedYellow;

  private long thresholdYellowGreen;

  public ThresholdConditionPolicy(long thresholdRedYellow, long thresholdYellowGreen)
  {
    if (thresholdRedYellow > thresholdYellowGreen)
    {
      throw new IllegalArgumentException("thresholdRedYellow > thresholdYellowGreen"); //$NON-NLS-1$
    }

    this.thresholdRedYellow = thresholdRedYellow;
    this.thresholdYellowGreen = thresholdYellowGreen;
  }

  public long getThresholdRedYellow()
  {
    return thresholdRedYellow;
  }

  public long getThresholdYellowGreen()
  {
    return thresholdYellowGreen;
  }

  public Condition getNewCondition(Condition oldCondition)
  {
    return getNewCondition(oldCondition, Runtime.getRuntime().freeMemory());
  }

  protected Condition getNewCondition(Condition oldCondition, long freeMemory)
  {
    if (freeMemory > thresholdYellowGreen)
    {
      return Condition.GREEN;
    }

    if (freeMemory > thresholdRedYellow)
    {
      return Condition.YELLOW;
    }

    return Condition.RED;
  }
}
