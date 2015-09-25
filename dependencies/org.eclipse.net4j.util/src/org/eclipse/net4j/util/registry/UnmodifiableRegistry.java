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
package org.eclipse.net4j.util.registry;

import org.eclipse.net4j.util.event.IListener;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UnmodifiableRegistry<K, V> implements IRegistry<K, V>
{
  private IRegistry<K, V> delegate;

  public UnmodifiableRegistry(IRegistry<K, V> delegate)
  {
    this.delegate = delegate;
  }

  public void addListener(IListener listener)
  {
    delegate.addListener(listener);
  }

  public void removeListener(IListener listener)
  {
    delegate.removeListener(listener);
  }

  /**
   * @since 3.0
   */
  public IListener[] getListeners()
  {
    return delegate.getListeners();
  }

  /**
   * @since 3.0
   */
  public boolean hasListeners()
  {
    return delegate.hasListeners();
  }

  public V put(K key, V value)
  {
    throw new UnsupportedOperationException();
  }

  public void putAll(Map<? extends K, ? extends V> t)
  {
    throw new UnsupportedOperationException();
  }

  public V remove(Object key)
  {
    throw new UnsupportedOperationException();
  }

  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  public void commit()
  {
    throw new UnsupportedOperationException();
  }

  public void commit(boolean notifications)
  {
    throw new UnsupportedOperationException();
  }

  public void setAutoCommit(boolean on)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isAutoCommit()
  {
    return delegate.isAutoCommit();
  }

  public boolean isEmpty()
  {
    return delegate.isEmpty();
  }

  public int size()
  {
    return delegate.size();
  }

  public Entry<K, V>[] getElements()
  {
    return delegate.getElements();
  }

  public V get(Object key)
  {
    return delegate.get(key);
  }

  public boolean containsKey(Object key)
  {
    return delegate.containsKey(key);
  }

  public boolean containsValue(Object value)
  {
    return delegate.containsValue(value);
  }

  public Set<Entry<K, V>> entrySet()
  {
    return delegate.entrySet();
  }

  public Set<K> keySet()
  {
    return delegate.keySet();
  }

  public Collection<V> values()
  {
    return delegate.values();
  }

  @Override
  public boolean equals(Object o)
  {
    return delegate.equals(o);
  }

  @Override
  public int hashCode()
  {
    return delegate.hashCode();
  }
}
