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

import org.eclipse.net4j.util.event.INotifier;

/**
 * @author Eike Stepper
 */
public interface ICacheMonitor extends INotifier
{
  public ConditionPolicy getConditionPolicy();

  public Condition getCondition();

  public ICacheProbe registerCache(ICache cache);

  public void deregisterCache(ICache cache);

  /**
   * @author Eike Stepper
   */
  public enum Condition
  {
    /**
     * Condition <b>GREEN</b> indicates that the system is operating normally and enough free memory is available so
     * that caches are free to cache additional elements.
     */
    GREEN,

    /**
     * Condition <b>YELLOW</b> indicates that the system is operating normally but free memory is about to go low so
     * that caches should stop to cache additional elements.
     */
    YELLOW,

    /**
     * Condition <b>RED</b> indicates that the system state is critical and free memory is almost exhausted so that
     * caches must immediately stop to cache additional elements. Depending of the eviction strategy element eviction is
     * ordered.
     */
    RED;
  }

  /**
   * @author Eike Stepper
   */
  public interface ConditionPolicy
  {
    public Condition getNewCondition(Condition oldCondition);
  }
}
