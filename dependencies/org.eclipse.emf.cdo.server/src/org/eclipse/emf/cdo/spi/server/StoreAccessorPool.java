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

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class StoreAccessorPool
{
  /**
   * The {@link IStore store} instance that manages this pool.
   */
  private IStore store;

  /**
   * The pooling context of this pool. An instance of either {@link ISession} or {@link IView}, or <code>null</code> if
   * this pool is not contextual.
   */
  private Object context;

  private ConcurrentLinkedQueue<StoreAccessorBase> accessors = new ConcurrentLinkedQueue<StoreAccessorBase>();

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
   * Passivates the given {@link StoreAccessor store accessor} and adds it to this pool.
   * 
   * @since 4.0
   */
  public void addStoreAccessor(StoreAccessorBase storeAccessor)
  {
    try
    {
      storeAccessor.doPassivate();
      accessors.add(storeAccessor);
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
    StoreAccessorBase accessor = accessors.poll();
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
    for (;;)
    {
      StoreAccessorBase accessor = accessors.poll();
      if (accessor == null)
      {
        break;
      }

      LifecycleUtil.deactivate(accessor, OMLogger.Level.WARN);
    }

    context = null;
    store = null;
  }
}
