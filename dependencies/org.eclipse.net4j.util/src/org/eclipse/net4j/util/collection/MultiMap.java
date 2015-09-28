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

import org.eclipse.net4j.util.ObjectUtil;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class MultiMap<K, V> implements Map<K, V>
{
  private transient Entries entries;

  private transient Set<K> keys;

  private transient Collection<V> values;

  public MultiMap()
  {
  }

  public abstract int getDelegateCount();

  public Map<K, V> getDelegate(int index)
  {
    if (0 <= index && index < getDelegateCount())
    {
      return doGetDelegate(index);
    }

    return null;
  }

  /**
   * @category WRITE
   */
  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @category WRITE
   */
  public V put(K key, V value)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @category WRITE
   */
  public void putAll(Map<? extends K, ? extends V> t)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @category WRITE
   */
  public V remove(Object key)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @category READ
   */
  public boolean containsKey(Object key)
  {
    return containsKey(key, getDelegateCount());
  }

  /**
   * @category READ
   */
  public boolean containsValue(Object value)
  {
    for (int i = 0; i < getDelegateCount(); i++)
    {
      Map<K, V> delegate = getDelegate(i);
      if (delegate != null)
      {
        if (delegate.containsValue(value))
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @category READ
   */
  public V get(Object key)
  {
    for (int i = 0; i < getDelegateCount(); i++)
    {
      Map<K, V> delegate = getDelegate(i);
      if (delegate != null)
      {
        if (delegate.containsKey(key))
        {
          return delegate.get(key);
        }
      }
    }

    return null;
  }

  /**
   * @category READ
   */
  public int size()
  {
    int size = 0;
    Map<K, V> delegate = getDelegate(0);
    if (delegate != null)
    {
      size += delegate.size();
      for (int i = 1; i < getDelegateCount(); i++)
      {
        delegate = getDelegate(i);
        if (delegate != null)
        {
          Set<K> keySet = delegate.keySet();
          for (K key : keySet)
          {
            if (!containsKey(key, i))
            {
              ++size;
            }
          }
        }
      }
    }

    return size;
  }

  /**
   * @category READ
   */
  public boolean isEmpty()
  {
    for (int i = 0; i < getDelegateCount(); i++)
    {
      Map<K, V> delegate = getDelegate(i);
      if (delegate != null)
      {
        if (!delegate.isEmpty())
        {
          return false;
        }
      }
    }

    return true;
  }

  public synchronized Set<Map.Entry<K, V>> entrySet()
  {
    if (entries == null)
    {
      entries = new Entries();
    }

    return entries;
  }

  public synchronized Set<K> keySet()
  {
    if (keys == null)
    {
      keys = new Keys();
    }

    return keys;
  }

  public synchronized Collection<V> values()
  {
    if (values == null)
    {
      values = new Values();
    }

    return values;
  }

  protected boolean containsKey(Object key, int delegateCount)
  {
    for (int i = 0; i < delegateCount; i++)
    {
      Map<K, V> delegate = getDelegate(i);
      if (delegate != null)
      {
        if (delegate.containsKey(key))
        {
          return true;
        }
      }
    }

    return false;
  }

  protected abstract Map<K, V> doGetDelegate(int index);

  /**
   * @author Eike Stepper
   */
  public static class ListBased<K, V> extends MultiMap<K, V>
  {
    private List<Map<K, V>> delegates;

    public ListBased()
    {
    }

    public ListBased(List<Map<K, V>> delegates)
    {
      this.delegates = delegates;
    }

    public synchronized List<Map<K, V>> getDelegates()
    {
      if (delegates == null)
      {
        delegates = createDelegates();
      }

      return delegates;
    }

    public void setDelegates(List<Map<K, V>> delegates)
    {
      this.delegates = delegates;
    }

    @Override
    public int getDelegateCount()
    {
      return getDelegates().size();
    }

    @Override
    protected Map<K, V> doGetDelegate(int index)
    {
      return getDelegates().get(index);
    }

    protected List<Map<K, V>> createDelegates()
    {
      return new ArrayList<Map<K, V>>();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Entries extends AbstractSet<Entry<K, V>>
  {
    public Entries()
    {
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean add(Entry<K, V> o)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean addAll(Collection<? extends Entry<K, V>> c)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public void clear()
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean remove(Object o)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean removeAll(Collection<?> c)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean retainAll(Collection<?> c)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category READ
     */
    @Override
    public boolean contains(Object o)
    {
      if (o instanceof Map.Entry<?, ?>)
      {
        for (int i = 0; i < getDelegateCount(); i++)
        {
          Map<K, V> delegate = getDelegate(i);
          if (delegate != null)
          {
            @SuppressWarnings("unchecked")
            K key = ((Map.Entry<K, V>)o).getKey();
            if (delegate.containsKey(key))
            {
              @SuppressWarnings("unchecked")
              V value = ((Map.Entry<K, V>)o).getValue();
              if (ObjectUtil.equals(delegate.get(key), value))
              {
                return true;
              }
            }
          }
        }
      }

      return false;
    }

    /**
     * @category READ
     */
    @Override
    public boolean isEmpty()
    {
      return MultiMap.this.isEmpty();
    }

    /**
     * @category READ
     */
    @Override
    public int size()
    {
      return MultiMap.this.size();
    }

    @Override
    public Iterator<Entry<K, V>> iterator()
    {
      return new Iterator<Entry<K, V>>()
      {
        private Entry<K, V> next;

        private int delegateIndex = -1;

        private Iterator<Entry<K, V>> delegateIt;

        /**
         * @category WRITE
         */
        public void remove()
        {
          throw new UnsupportedOperationException();
        }

        /**
         * @category READ
         */
        public boolean hasNext()
        {
          next = null;
          while (next == null)
          {
            if (delegateIt == null)
            {
              Map<K, V> delegate = getDelegate(++delegateIndex);
              if (delegate == null)
              {
                // All delegates have been iterated
                break;
              }

              delegateIt = delegate.entrySet().iterator();
            }

            if (delegateIt.hasNext())
            {
              next = delegateIt.next();

              // Check if this key has been returned previously
              if (containsKey(next.getKey(), delegateIndex))
              {
                next = null;
              }
            }
            else
            {
              // Determine next delegate iterator in next loop
              delegateIt = null;
            }
          }

          return next != null;
        }

        /**
         * @category READ
         */
        public Map.Entry<K, V> next()
        {
          if (next == null)
          {
            throw new NoSuchElementException();
          }

          try
          {
            return next;
          }
          finally
          {
            next = null;
          }
        }
      };
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Keys extends AbstractSet<K>
  {
    public Keys()
    {
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean add(K o)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean addAll(Collection<? extends K> c)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public void clear()
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean remove(Object o)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean removeAll(Collection<?> c)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean retainAll(Collection<?> c)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category READ
     */
    @Override
    public boolean contains(Object o)
    {
      return MultiMap.this.containsKey(o);
    }

    /**
     * @category READ
     */
    @Override
    public boolean isEmpty()
    {
      return MultiMap.this.isEmpty();
    }

    /**
     * @category READ
     */
    @Override
    public int size()
    {
      return MultiMap.this.size();
    }

    /**
     * @category READ
     */
    @Override
    public Iterator<K> iterator()
    {
      return new Iterator<K>()
      {
        private K next;

        private int delegateIndex = -1;

        private Iterator<K> delegateIt;

        /**
         * @category WRITE
         */
        public void remove()
        {
          throw new UnsupportedOperationException();
        }

        /**
         * @category READ
         */
        public boolean hasNext()
        {
          next = null;
          while (next == null)
          {
            if (delegateIt == null)
            {
              Map<K, V> delegate = getDelegate(++delegateIndex);
              if (delegate == null)
              {
                // All delegates have been iterated
                break;
              }

              delegateIt = delegate.keySet().iterator();
            }

            if (delegateIt.hasNext())
            {
              next = delegateIt.next();

              // Check if this key has been returned previously
              if (containsKey(next, delegateIndex))
              {
                next = null;
              }
            }
            else
            {
              // Determine next delegate iterator in next loop
              delegateIt = null;
            }
          }

          return next != null;
        }

        /**
         * @category READ
         */
        public K next()
        {
          if (next == null)
          {
            throw new NoSuchElementException();
          }

          try
          {
            return next;
          }
          finally
          {
            next = null;
          }
        }
      };
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Values extends AbstractCollection<V>
  {
    public Values()
    {
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean add(V o)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean addAll(Collection<? extends V> c)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public void clear()
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean remove(Object o)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean removeAll(Collection<?> c)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category WRITE
     */
    @Override
    public boolean retainAll(Collection<?> c)
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @category READ
     */
    @Override
    public boolean contains(Object o)
    {
      return MultiMap.this.containsValue(o);
    }

    /**
     * @category READ
     */
    @Override
    public boolean isEmpty()
    {
      return MultiMap.this.isEmpty();
    }

    /**
     * @category READ
     */
    @Override
    public int size()
    {
      return MultiMap.this.size();
    }

    /**
     * @category READ
     */
    @Override
    public Iterator<V> iterator()
    {
      return new Iterator<V>()
      {
        private Iterator<Entry<K, V>> delegateIt = entrySet().iterator();

        /**
         * @category WRITE
         */
        public void remove()
        {
          throw new UnsupportedOperationException();
        }

        /**
         * @category READ
         */
        public boolean hasNext()
        {
          return delegateIt.hasNext();
        }

        /**
         * @category READ
         */
        public V next()
        {
          return delegateIt.next().getValue();
        }
      };
    }
  }
}
