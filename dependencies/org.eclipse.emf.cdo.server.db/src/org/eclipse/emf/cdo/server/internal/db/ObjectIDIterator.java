/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - bug 208689
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.collection.CloseableIterator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * @author Eike Stepper
 */
public abstract class ObjectIDIterator implements CloseableIterator<CDOID>
{
  private IMappingStrategy mappingStrategy;

  private IIDHandler idHandler;

  private IDBStoreAccessor accessor;

  private ResultSet currentResultSet;

  private CDOID nextID;

  private boolean closed;

  /**
   * Creates an iterator over all objects in a store. It is important to {@link #close()} of this iterator after usage
   * to properly close internal result sets.
   */
  public ObjectIDIterator(IMappingStrategy mappingStrategy, IDBStoreAccessor accessor)
  {
    this.mappingStrategy = mappingStrategy;
    this.accessor = accessor;
    idHandler = getMappingStrategy().getStore().getIDHandler();

  }

  public void close()
  {
    closeCurrentResultSet();
    nextID = null;
    closed = true;
  }

  public boolean isClosed()
  {
    return closed;
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public IDBStoreAccessor getAccessor()
  {
    return accessor;
  }

  public boolean hasNext()
  {
    if (closed)
    {
      return false;
    }

    nextID = null;
    for (;;)
    {
      if (currentResultSet == null)
      {
        currentResultSet = getNextResultSet();
        if (currentResultSet == null)
        {
          return false;
        }
      }

      try
      {
        if (currentResultSet.next())
        {
          nextID = idHandler.getCDOID(currentResultSet, 1);
          return true;
        }

        closeCurrentResultSet();

        currentResultSet = null;
        return false;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }
  }

  protected void closeCurrentResultSet()
  {
    DBUtil.close(currentResultSet);
  }

  public CDOID next()
  {
    if (nextID == null)
    {
      throw new NoSuchElementException();
    }

    return nextID;
  }

  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  protected abstract ResultSet getNextResultSet();
}
