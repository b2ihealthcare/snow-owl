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
package org.eclipse.net4j.util.container.delegate;

import org.eclipse.net4j.util.collection.MapEntry;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.IListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A default implementation of a {@link IContainerMap container map}.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public class ContainerMap<K, V> extends AbstractDelegator<Map.Entry<K, V>> implements IContainerMap<K, V>
{
  private Map<K, V> delegate;

  public ContainerMap(Map<K, V> delegate)
  {
    this.delegate = delegate;
  }

  public Map<K, V> getDelegate()
  {
    return delegate;
  }

  /**
   * @category WRITE
   */
  public void clear()
  {
    if (!isEmpty())
    {
      ContainerEvent<Map.Entry<K, V>> event = createEvent(getDelegate().entrySet(), IContainerDelta.Kind.REMOVED);
      getDelegate().clear();
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(event, listeners);
      }
    }
  }

  /**
   * @category WRITE
   */
  public V put(K key, V value)
  {
    ContainerEvent<Map.Entry<K, V>> event = new ContainerEvent<Map.Entry<K, V>>(this);
    V removed = getDelegate().put(key, value);
    if (removed != null)
    {
      event.addDelta(new ContainerMapEntry<K, V>(key, removed), IContainerDelta.Kind.REMOVED);
    }

    event.addDelta(new ContainerMapEntry<K, V>(key, value), IContainerDelta.Kind.ADDED);
    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(event, listeners);
    }

    return removed;
  }

  /**
   * @category WRITE
   */
  public void putAll(Map<? extends K, ? extends V> t)
  {
    ContainerEvent<Map.Entry<K, V>> event = new ContainerEvent<Map.Entry<K, V>>(this);
    Iterator<? extends Entry<? extends K, ? extends V>> i = t.entrySet().iterator();
    while (i.hasNext())
    {
      Entry<? extends K, ? extends V> entry = i.next();
      K key = entry.getKey();
      V value = entry.getValue();
      V removed = getDelegate().put(key, value);
      if (removed != null)
      {
        event.addDelta(new ContainerMapEntry<K, V>(key, removed), IContainerDelta.Kind.REMOVED);
      }

      event.addDelta(new ContainerMapEntry<K, V>(key, value), IContainerDelta.Kind.ADDED);
    }

    dispatchEvent(event);
  }

  /**
   * @category WRITE
   */
  public V remove(Object key)
  {
    V removed = getDelegate().remove(key);
    if (removed != null)
    {
      fireRemovedEvent(new ContainerMapEntry<Object, V>(key, removed));
    }

    return removed;
  }

  /**
   * @category READ
   */
  public boolean containsKey(Object key)
  {
    return getDelegate().containsKey(key);
  }

  /**
   * @category READ
   */
  public boolean containsValue(Object value)
  {
    return getDelegate().containsValue(value);
  }

  /**
   * @category READ
   */
  public V get(Object key)
  {
    return getDelegate().get(key);
  }

  /**
   * @category READ
   */
  public int size()
  {
    return getDelegate().size();
  }

  /**
   * @category READ
   */
  @SuppressWarnings("unchecked")
  public Map.Entry<K, V>[] getElements()
  {
    return (Entry<K, V>[])getDelegate().entrySet().toArray();
  }

  /**
   * @category READ
   */
  public boolean isEmpty()
  {
    return getDelegate().isEmpty();
  }

  /**
   * @category READ
   */
  public Set<Map.Entry<K, V>> entrySet()
  {
    return new ContainerSet<Map.Entry<K, V>>(getDelegate().entrySet());
  }

  /**
   * @category READ
   */
  public Set<K> keySet()
  {
    return new ContainerSet<K>(getDelegate().keySet());
  }

  /**
   * @category READ
   */
  public Collection<V> values()
  {
    return new ContainerCollection<V>(getDelegate().values());
  }

  /**
   * @author Eike Stepper
   */
  private static final class ContainerMapEntry<K, V> extends MapEntry<K, V>
  {
    public ContainerMapEntry(K key, V value)
    {
      super(key, value);
    }

    @Override
    public V setValue(V value)
    {
      throw new UnsupportedOperationException();
    }
  }
}
