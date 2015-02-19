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
package org.eclipse.net4j.util.ref;

import org.eclipse.net4j.util.collection.MapEntry;

import java.lang.ref.ReferenceQueue;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A {@link ConcurrentMap} implementation that uses {@link KeyedReference} instances ({@link KeyedStrongReference},
 * {@link KeyedSoftReference}, {@link KeyedWeakReference} or {@link KeyedPhantomReference}) as its values.
 * <p>
 * A <code>ReferenceValueMap</code> can be used to cache mappings until the <em>value</em> of the mapping is no longer
 * reachable from outside of the map
 * <p>
 * <b>Note:</b> This map is not synchronized. If it is to be used by multiple threads concurrently the user is
 * responsible for applying proper external synchronization!
 * 
 * @author Eike Stepper
 */
public abstract class ReferenceValueMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>
{
  private ConcurrentMap<K, KeyedReference<K, V>> map;

  private ReferenceQueue<V> queue;

  private EntrySet entrySet;

  public ReferenceValueMap()
  {
    this(new ConcurrentHashMap<K, KeyedReference<K, V>>());
  }

  public ReferenceValueMap(ConcurrentMap<K, KeyedReference<K, V>> map)
  {
    if (!map.isEmpty())
    {
      throw new IllegalArgumentException("!map.isEmpty()"); //$NON-NLS-1$
    }

    this.map = map;
    queue = createQueue();
  }

  @Override
  public int size()
  {
    purgeQueue();
    return map.size();
  }

  @Override
  public boolean isEmpty()
  {
    purgeQueue();
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key)
  {
    KeyedReference<K, V> ref = map.get(key);
    if (ref != null)
    {
      if (ref.get() == null)
      {
        // ref.enqueue();
        return false;
      }

      return true;
    }

    return false;
  }

  @Override
  public boolean containsValue(Object value)
  {
    if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    for (KeyedReference<K, V> ref : map.values())
    {
      V v = ref.get();
      if (v == null)
      {
        // ref.enqueue();
        return false;
      }

      if (value.equals(v))
      {
        return true;
      }
    }

    return false;
  }

  @Override
  public V get(Object key)
  {
    KeyedReference<K, V> ref = map.get(key);
    return dereference(ref);
  }

  @Override
  public V put(K key, V value)
  {
    try
    {
      KeyedReference<K, V> ref = createReference(key, value, queue);
      KeyedReference<K, V> oldRef = map.put(key, ref);
      return dereference(oldRef);
    }
    finally
    {
      purgeQueue();
    }
  }

  public V putIfAbsent(K key, V value)
  {
    try
    {
      KeyedReference<K, V> ref = createReference(key, value, queue);
      KeyedReference<K, V> oldRef = map.putIfAbsent(key, ref);
      return dereference(oldRef);
    }
    finally
    {
      purgeQueue();
    }
  }

  public V replace(K key, V value)
  {
    try
    {
      KeyedReference<K, V> ref = createReference(key, value, queue);
      KeyedReference<K, V> oldRef = map.replace(key, ref);
      return dereference(oldRef);
    }
    finally
    {
      purgeQueue();
    }
  }

  public boolean replace(K key, V oldValue, V newValue)
  {
    try
    {
      // TODO Consider a dummy KeyedReference class for oldRef
      KeyedReference<K, V> oldRef = createReference(key, oldValue, queue);
      KeyedReference<K, V> newRef = createReference(key, newValue, queue);
      return map.replace(key, oldRef, newRef);
    }
    finally
    {
      purgeQueue();
    }
  }

  @Override
  public V remove(Object key)
  {
    KeyedReference<K, V> ref = map.remove(key);
    return dereference(ref);
  }

  public boolean remove(Object key, Object value)
  {
    // TODO Consider a dummy KeyedReference class for value
    return map.remove(key, value);
  }

  @Override
  public void clear()
  {
    purgeQueue();
    map.clear();
  }

  @Override
  public Set<Map.Entry<K, V>> entrySet()
  {
    if (entrySet == null)
    {
      purgeQueue();
      entrySet = new EntrySet();
    }

    return entrySet;
  }

  protected ReferenceQueue<V> createQueue()
  {
    return new ReferenceQueue<V>();
  }

  @SuppressWarnings("unchecked")
  protected void purgeQueue()
  {
    if (queue != null)
    {
      KeyedReference<K, V> ref;
      while ((ref = (KeyedReference<K, V>)queue.poll()) != null)
      {
        // Slightly faster than map.get() + map.remove()
        K key = ref.getKey();
        map.remove(key, ref);
        purged(key);
      }
    }
  }

  protected void purged(K key)
  {
  }

  protected V dereference(KeyedReference<K, V> ref)
  {
    if (ref == null)
    {
      return null;
    }

    return ref.get();
  }

  protected abstract KeyedReference<K, V> createReference(K key, V value, ReferenceQueue<V> queue);

  /**
   * @author Eike Stepper
   */
  public static class Strong<K, V> extends ReferenceValueMap<K, V>
  {
    public Strong()
    {
    }

    public Strong(ConcurrentMap<K, KeyedReference<K, V>> map)
    {
      super(map);
    }

    @Override
    protected KeyedReference<K, V> createReference(K key, V value, ReferenceQueue<V> queue)
    {
      return new KeyedStrongReference<K, V>(key, value);
    }

    @Override
    protected ReferenceQueue<V> createQueue()
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Soft<K, V> extends ReferenceValueMap<K, V>
  {
    public Soft()
    {
    }

    public Soft(ConcurrentMap<K, KeyedReference<K, V>> map)
    {
      super(map);
    }

    @Override
    protected KeyedReference<K, V> createReference(K key, V value, ReferenceQueue<V> queue)
    {
      return new KeyedSoftReference<K, V>(key, value, queue);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Weak<K, V> extends ReferenceValueMap<K, V>
  {
    public Weak()
    {
    }

    public Weak(ConcurrentMap<K, KeyedReference<K, V>> map)
    {
      super(map);
    }

    @Override
    protected KeyedReference<K, V> createReference(K key, V value, ReferenceQueue<V> queue)
    {
      return new KeyedWeakReference<K, V>(key, value, queue);
    }
  }

  /**
   * @author Eike Stepper
   */
  private class EntrySet extends AbstractSet<Map.Entry<K, V>>
  {
    public EntrySet()
    {
    }

    @Override
    public int size()
    {
      return map.size();
    }

    @Override
    public boolean isEmpty()
    {
      return map.isEmpty();
    }

    @Override
    public boolean contains(Object object)
    {
      if (object == null)
      {
        throw new IllegalArgumentException("object == null"); //$NON-NLS-1$
      }

      if (object instanceof Map.Entry<?, ?>)
      {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)object;
        Object key = entry.getKey();
        Object value = entry.getValue();
        return key != null && value != null && value.equals(get(key));
      }

      return false;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new EntrySetIterator();
    }

    @Override
    public Object[] toArray()
    {
      Object[] a = new Object[size()];
      int i = 0;
      for (Map.Entry<K, V> entry : this)
      {
        a[i++] = entry;
      }

      return a;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a)
    {
      if (a == null)
      {
        throw new IllegalArgumentException("array == null"); //$NON-NLS-1$
      }

      int size = size();
      if (a.length < size)
      {
        a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
      }

      int i = 0;
      for (Map.Entry<K, V> entry : this)
      {
        a[i++] = (T)entry;
      }

      if (a.length > size)
      {
        a[size] = null;
      }

      return a;
    }

    @Override
    public boolean remove(Object object)
    {
      if (object == null)
      {
        throw new IllegalArgumentException("object == null"); //$NON-NLS-1$
      }

      if (object instanceof Map.Entry<?, ?>)
      {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)object;
        return map.remove(entry.getKey(), entry.getValue());
      }

      return false;
    }

    @Override
    public void clear()
    {
      map.clear();
    }
  }

  /**
   * @author Eike Stepper
   */
  private class EntrySetIterator implements Iterator<Map.Entry<K, V>>
  {
    private Iterator<Entry<K, KeyedReference<K, V>>> it = map.entrySet().iterator();

    private MapEntry<K, V> nextEntry;

    private K lastKey;

    public EntrySetIterator()
    {
    }

    public boolean hasNext()
    {
      if (nextEntry != null)
      {
        return true;
      }

      while (it.hasNext())
      {
        Entry<K, KeyedReference<K, V>> entry = it.next();
        lastKey = entry.getKey();
        V value = dereference(entry.getValue());
        if (value != null)
        {
          nextEntry = new MapEntry<K, V>(lastKey, value);
          return true;
        }
      }

      return false;
    }

    public Entry<K, V> next()
    {
      if (nextEntry == null)
      {
        if (!hasNext())
        {
          throw new NoSuchElementException();
        }
      }

      try
      {
        return nextEntry;
      }
      finally
      {
        nextEntry = null;
      }
    }

    public void remove()
    {
      if (lastKey == null)
      {
        throw new IllegalStateException("lastKey == null"); //$NON-NLS-1$
      }

      map.remove(lastKey);
      lastKey = null;
      nextEntry = null;
    }
  }
}
