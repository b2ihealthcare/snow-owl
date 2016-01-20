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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;

import java.util.LinkedList;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class StoreAccessorPool
{
  /**
   * @since 4.2
   */
  public static final int DEFAULT_CAPACITY = 5;

  /**
   * The {@link IStore store} instance that manages this pool.
   */
  private IStore store;

  /**
   * The pooling context of this pool. An instance of either {@link ISession} or {@link IView}, or <code>null</code> if
   * this pool is not contextual.
   */
  private Object context;

  private int capacity = DEFAULT_CAPACITY;

  private LinkedList<StoreAccessorBase> accessors = new LinkedList<StoreAccessorBase>();

  public StoreAccessorPool(IStore store, Object context)
  {
    this.store = store;
    this.context = context;
  }

  public IStore getStore()
  {
    return store;
  }

  public Object getContext()
  {
    return context;
  }

  /**
   * @since 4.2
   */
  public int getCapacity()
  {
    return capacity;
  }

  /**
   * @since 4.2
   */
  public void setCapacity(int capacity)
  {
    this.capacity = capacity;
    retainStoreAccessors(capacity);
  }

  /**
   * Passivates the given {@link StoreAccessor store accessor} and adds it to this pool if the pool size is smaller than the {@link #getCapacity() capacity},
   * or disposes of the store accessor otherwise.
   *
   * @since 4.0
   */
  public void addStoreAccessor(StoreAccessorBase storeAccessor)
  {
    try
    {
      storeAccessor.doPassivate();

      boolean full = false;
      synchronized (accessors)
      {
        if (accessors.size() >= capacity)
        {
          full = true;
        }
        else
        {
          accessors.addFirst(storeAccessor);
        }
      }

      if (full)
      {
        disposeStoreAccessor(storeAccessor);
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  /**
   * Returns a {@link StoreAccessor store accessor} from this pool if one is available, or <code>null</code> otherwise.
   * If a store accessor is available it is removed from this pool and its unpassivate method is called.
   *
   * @since 4.0
   */
  public StoreAccessorBase removeStoreAccessor(Object context)
  {
    StoreAccessorBase accessor;
    synchronized (accessors)
    {
      accessor = accessors.poll();
    }

    if (accessor != null)
    {
      try
      {
        accessor.doUnpassivate();
        accessor.setContext(context);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        return null;
      }
    }

    return accessor;
  }

  /**
   * Deactivates all contained {@link StoreAccessor store accessors} and clears this pool.
   */
  public void dispose()
  {
    retainStoreAccessors(0);

    context = null;
    store = null;
  }

  /**
   * @since 4.2
   */
  protected void retainStoreAccessors(int targetSize)
  {
    synchronized (accessors)
    {
      while (accessors.size() > targetSize)
      {
        StoreAccessorBase accessor = accessors.removeLast();
        disposeStoreAccessor(accessor);
      }
    }
  }

  /**
   * @since 4.2
   */
  protected void disposeStoreAccessor(StoreAccessorBase accessor)
  {
    LifecycleUtil.deactivate(accessor, OMLogger.Level.WARN);
  }
}