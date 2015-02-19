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
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;

import java.sql.Connection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class ObjectTypeCache extends DelegatingObjectTypeMapper
{
  public static final int DEFAULT_CACHE_CAPACITY = 100000;

  private Map<CDOID, CDOID> memoryCache;

  private int cacheSize;

  public ObjectTypeCache(int cacheSize)
  {
    this.cacheSize = cacheSize;
  }

  @Override
  protected CDOID doGetObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    return memoryCache.get(id);
  }

  @Override
  protected void doPutObjectType(IDBStoreAccessor accessor, CDOID id, CDOID type)
  {
    memoryCache.put(id, type);
  }

  @Override
  protected void doRemoveObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    memoryCache.remove(id);
  }

  @Override
  protected CDOID doGetMaxID(Connection connection, IIDHandler idHandler)
  {
    return null;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    memoryCache = Collections.synchronizedMap(new MemoryCache(cacheSize));
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    memoryCache = null;
    super.doDeactivate();
  }

  /**
   * @author Stefan Winkler
   */
  private static final class MemoryCache extends LinkedHashMap<CDOID, CDOID>
  {
    private static final long serialVersionUID = 1L;

    private int capacity;

    public MemoryCache(int capacity)
    {
      super(capacity, 0.75f, true);
      this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<CDOID, CDOID> eldest)
    {
      return size() > capacity;
    }
  }
}
