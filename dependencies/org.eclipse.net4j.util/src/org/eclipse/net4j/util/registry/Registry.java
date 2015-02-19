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

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class Registry<K, V> extends Container<Map.Entry<K, V>> implements IRegistry<K, V>
{
  private boolean autoCommit;

  private Transaction transaction;

  protected Registry(boolean autoCommit)
  {
    this.autoCommit = autoCommit;
  }

  protected Registry()
  {
    this(true);
  }

  @Override
  public boolean isEmpty()
  {
    return keySet().isEmpty();
  }

  public int size()
  {
    return keySet().size();
  }

  public Set<Entry<K, V>> entrySet()
  {
    return getMap().entrySet();
  }

  public Set<K> keySet()
  {
    return getMap().keySet();
  }

  public Collection<V> values()
  {
    return getMap().values();
  }

  public boolean containsKey(Object key)
  {
    return keySet().contains(key);
  }

  public boolean containsValue(Object value)
  {
    return values().contains(value);
  }

  public V get(Object key)
  {
    return getMap().get(key);
  }

  /**
   * Requires {@link #commit()} to be called later if not {@link #isAutoCommit()}.
   */
  public synchronized V put(K key, V value)
  {
    V result = register(key, value);
    autoCommit();
    return result;
  }

  /**
   * Requires {@link #commit()} to be called later if not {@link #isAutoCommit()}.
   */
  public synchronized void putAll(Map<? extends K, ? extends V> t)
  {
    if (!t.isEmpty())
    {
      Iterator<? extends Entry<? extends K, ? extends V>> i = t.entrySet().iterator();
      while (i.hasNext())
      {
        Entry<? extends K, ? extends V> e = i.next();
        register(e.getKey(), e.getValue());
      }

      autoCommit();
    }
  }

  /**
   * Requires {@link #commit()} to be called later if not {@link #isAutoCommit()}.
   */
  public synchronized V remove(Object key)
  {
    V result = deregister(key);
    autoCommit();
    return result;
  }

  /**
   * Requires {@link #commit()} to be called later if not {@link #isAutoCommit()}.
   */
  public synchronized void clear()
  {
    if (!isEmpty())
    {
      for (Object key : keySet().toArray())
      {
        deregister(key);
      }

      autoCommit();
    }
  }

  @SuppressWarnings("unchecked")
  public Entry<K, V>[] getElements()
  {
    return entrySet().toArray(new Entry[size()]);
  }

  public boolean isAutoCommit()
  {
    return autoCommit;
  }

  public void setAutoCommit(boolean autoCommit)
  {
    this.autoCommit = autoCommit;
  }

  public synchronized void commit(boolean notifications)
  {
    if (transaction != null)
    {
      if (!transaction.isOwned())
      {
        OM.LOG.warn("Committing thread is not owner of transaction: " + Thread.currentThread()); //$NON-NLS-1$
      }

      transaction.commit(notifications);
      transaction = null;
      notifyAll();
    }
  }

  public void commit()
  {
    commit(true);
  }

  @Override
  public String toString()
  {
    return getMap().toString();
  }

  protected V register(K key, V value)
  {
    Transaction transaction = getTransaction();
    V oldValue = getMap().put(key, value);
    if (oldValue != null)
    {
      transaction.rememberDeregistered(key, oldValue);
    }

    transaction.rememberRegistered(key, value);
    return oldValue;
  }

  @SuppressWarnings("unchecked")
  protected V deregister(Object key)
  {
    V value = getMap().remove(key);
    if (value != null)
    {
      getTransaction().rememberDeregistered((K)key, value);
    }

    return value;
  }

  protected Transaction getTransaction()
  {
    for (;;)
    {
      if (transaction == null)
      {
        transaction = new Transaction();
        return transaction;
      }

      if (transaction.isOwned())
      {
        transaction.increaseNesting();
        return transaction;
      }

      try
      {
        wait();
      }
      catch (InterruptedException ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }

  protected void autoCommit()
  {
    if (autoCommit)
    {
      commit();
    }
  }

  protected abstract Map<K, V> getMap();

  /**
   * @author Eike Stepper
   */
  protected class Transaction
  {
    private int nesting = 1;

    private ContainerEvent<Map.Entry<K, V>> event;

    private Thread owner;

    public Transaction()
    {
      owner = Thread.currentThread();
      initEvent();
    }

    private void initEvent()
    {
      event = newContainerEvent();
    }

    public boolean isOwned()
    {
      return owner == Thread.currentThread();
    }

    public void increaseNesting()
    {
      ++nesting;
    }

    public void commit(boolean notifications)
    {
      if (--nesting == 0)
      {
        if (notifications && !event.isEmpty())
        {
          fireEvent(event);
        }

        initEvent();
      }
    }

    public void rememberRegistered(K key, V value)
    {
      event.addDelta(new Element<K, V>(key, value), IContainerDelta.Kind.ADDED);
    }

    public void rememberDeregistered(K key, V value)
    {
      event.addDelta(new Element<K, V>(key, value), IContainerDelta.Kind.REMOVED);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Element<K, V> implements Map.Entry<K, V>
  {
    private final K key;

    private final V value;

    private Element(K key, V value)
    {
      this.key = key;
      this.value = value;
    }

    public K getKey()
    {
      return key;
    }

    public V getValue()
    {
      return value;
    }

    public V setValue(V value)
    {
      throw new UnsupportedOperationException();
    }
  }
}
