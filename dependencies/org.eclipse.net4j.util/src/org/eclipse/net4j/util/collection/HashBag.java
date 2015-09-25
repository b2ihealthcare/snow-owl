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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class HashBag<T> implements Set<T>
{
  private Map<T, HashBag.Counter> map;

  public HashBag()
  {
    map = new HashMap<T, Counter>();
  }

  public HashBag(int initialCapacity, float loadFactor)
  {
    map = new HashMap<T, Counter>(initialCapacity, loadFactor);
  }

  public HashBag(int initialCapacity)
  {
    map = new HashMap<T, Counter>(initialCapacity);
  }

  public HashBag(Map<? extends T, ? extends HashBag.Counter> m)
  {
    map = new HashMap<T, Counter>(m);
  }

  /**
   * @since 3.0
   */
  public int getCounterFor(T o)
  {
    Counter counter = map.get(o);
    if (counter == null)
    {
      return 0;
    }

    return counter.getValue();
  }

  public boolean add(T o)
  {
    HashBag.Counter counter = map.get(o);
    if (counter == null)
    {
      counter = new Counter();
      map.put(o, counter);
      return true;
    }

    counter.incValue();
    return false;
  }

  public boolean addAll(Collection<? extends T> c)
  {
    for (T t : c)
    {
      add(t);
    }

    return true;
  }

  public void clear()
  {
    map.clear();
  }

  public boolean contains(Object o)
  {
    return map.containsKey(o);
  }

  public boolean containsAll(Collection<?> c)
  {
    return map.keySet().containsAll(c);
  }

  public boolean isEmpty()
  {
    return map.isEmpty();
  }

  public Iterator<T> iterator()
  {
    return map.keySet().iterator();
  }

  public boolean remove(Object o)
  {
    HashBag.Counter counter = map.get(o);
    if (counter == null)
    {
      return false;
    }

    if (counter.decValue() == 0)
    {
      map.remove(o);
    }

    return true;
  }

  public boolean removeAll(Collection<?> c)
  {
    boolean changed = false;
    for (Object object : c)
    {
      if (remove(object))
      {
        changed = true;
      }
    }

    return changed;
  }

  public boolean retainAll(Collection<?> c)
  {
    throw new UnsupportedOperationException();
  }

  public int size()
  {
    return map.size();
  }

  public Object[] toArray()
  {
    return map.keySet().toArray();
  }

  @SuppressWarnings("hiding")
  public <T> T[] toArray(T[] a)
  {
    return map.keySet().toArray(a);
  }

  /**
   * @author Eike Stepper
   */
  private static final class Counter
  {
    private int value = 1;

    public Counter()
    {
    }

    public int getValue()
    {
      return value;
    }

    public int incValue()
    {
      return ++value;
    }

    public int decValue()
    {
      return --value;
    }
  }
}
