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
package org.eclipse.net4j.util.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 2.0
 * @author Eike Stepper
 */
public class BidiMapper<T1, T2>
{
  private Map<T1, T2> map1 = new HashMap<T1, T2>();

  private Map<T2, T1> map2 = new HashMap<T2, T1>();

  public BidiMapper()
  {
  }

  public synchronized void map(T1 v1, T2 v2)
  {
    map1.put(v1, v2);
    map2.put(v2, v1);
  }

  public synchronized int size()
  {
    return map1.size();
  }

  public synchronized void clear()
  {
    map1.clear();
    map2.clear();
  }

  public synchronized T2 lookup1(T1 v1)
  {
    return map1.get(v1);
  }

  public synchronized T1 lookup2(T2 v2)
  {
    return map2.get(v2);
  }

  public synchronized boolean remove1(T1 v1)
  {
    T2 v2 = map1.remove(v1);
    if (v2 != null)
    {
      map2.remove(v2);
      return true;
    }

    return false;
  }

  public synchronized boolean remove2(T2 v2)
  {
    T1 v1 = map2.remove(v2);
    if (v1 != null)
    {
      map1.remove(v1);
      return true;
    }

    return false;
  }
}
