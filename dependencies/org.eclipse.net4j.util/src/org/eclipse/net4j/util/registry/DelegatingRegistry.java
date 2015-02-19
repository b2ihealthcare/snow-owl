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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation note: AbstractDelegatingRegistry does not preserve the "modifyable view" contract of
 * {@link Map#entrySet()} as well as of {@link Map#keySet()}, i.e. they are disconnected sets and modifications applied
 * to them are not applied to their underlying AbstractDelegatingRegistry.
 * <p>
 * 
 * @author Eike Stepper
 */
public abstract class DelegatingRegistry<K, V> extends Registry<K, V>
{
  private IRegistry<K, V> delegate;

  public DelegatingRegistry(IRegistry<K, V> delegate)
  {
    this.delegate = delegate;
  }

  public DelegatingRegistry(IRegistry<K, V> delegate, boolean autoCommit)
  {
    super(autoCommit);
    this.delegate = delegate;
  }

  @Override
  public V get(Object key)
  {
    V result = getMap().get(key);
    if (result == null && delegate != null)
    {
      result = delegate.get(key);
    }

    return result;
  }

  @Override
  public Set<Entry<K, V>> entrySet()
  {
    return mergedEntrySet();
  }

  @Override
  public Set<K> keySet()
  {
    return mergedKeySet();
  }

  @Override
  public Collection<V> values()
  {
    return mergedValues();
  }

  @Override
  protected V register(K key, V value)
  {
    V delegated = delegate != null ? delegate.get(key) : null;
    V old = getMap().put(key, value);
    if (old == null)
    {
      if (delegated != null)
      {
        // Unhidden delegated element now becomes hidden
        getTransaction().rememberDeregistered(key, delegated);
      }

      getTransaction().rememberRegistered(key, value);
      return delegated;
    }

    getTransaction().rememberDeregistered(key, old);
    getTransaction().rememberRegistered(key, value);
    return old;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected V deregister(Object key)
  {
    V delegated = delegate != null ? delegate.get(key) : null;
    V old = getMap().remove(key);
    if (old != null)
    {
      getTransaction().rememberDeregistered((K)key, old);
      if (delegated != null)
      {
        // Hidden delegated element now becomes unhidden
        getTransaction().rememberRegistered((K)key, delegated);
      }
    }

    return old;
  }

  protected Set<Entry<K, V>> mergedEntrySet()
  {
    final Map<K, V> merged = new HashMap<K, V>();
    if (delegate != null)
    {
      merged.putAll(delegate);
    }

    merged.putAll(getMap());
    return merged.entrySet();
  }

  protected Set<K> mergedKeySet()
  {
    final Set<K> merged = new HashSet<K>();
    if (delegate != null)
    {
      merged.addAll(delegate.keySet());
    }

    merged.addAll(getMap().keySet());
    return merged;
  }

  protected Collection<V> mergedValues()
  {
    final List<V> result = new ArrayList<V>();
    for (K key : keySet())
    {
      result.add(get(key));
    }

    return result;
  }
}
