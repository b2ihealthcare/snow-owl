/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author Stefan Winkler
 * @since 2.0
 */
public class SmartPreparedStatementCache extends AbstractPreparedStatementCache
{
  private Cache cache;

  private HashMap<PreparedStatement, CachedPreparedStatement> checkedOut = new HashMap<PreparedStatement, CachedPreparedStatement>();

  public SmartPreparedStatementCache(int capacity)
  {
    cache = new Cache(capacity);
  }

  public PreparedStatement getPreparedStatement(String sql, ReuseProbability reuseProbability)
  {
    CachedPreparedStatement cachedStatement = cache.remove(sql);
    if (cachedStatement == null)
    {
      cachedStatement = createCachedPreparedStatement(sql, reuseProbability);
    }

    PreparedStatement result = cachedStatement.getPreparedStatement();
    checkedOut.put(result, cachedStatement);

    return result;
  }

  /**
   * @param ps
   *          the prepared statement to be released to the cache, or <code>null</code>.
   */
  public void releasePreparedStatement(PreparedStatement ps)
  {
    if (ps != null) // Bug 276926: Silently accept ps == null and do nothing.
    {
      CachedPreparedStatement cachedStatement = checkedOut.remove(ps);
      cache.put(cachedStatement);
    }
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    if (!checkedOut.isEmpty())
    {
      OM.LOG.warn("Statement leak detected"); //$NON-NLS-1$
    }
  }

  private CachedPreparedStatement createCachedPreparedStatement(String sql, ReuseProbability reuseProbability)
  {
    try
    {
      Connection connection = getConnection();
      PreparedStatement stmt = connection.prepareStatement(sql);
      return new CachedPreparedStatement(sql, reuseProbability, stmt);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  /**
   * @author Stefan Winkler
   */
  private static final class Cache
  {
    private CacheList lists[];

    private HashMap<String, CachedPreparedStatement> lookup;

    private int capacity;

    public Cache(int capacity)
    {
      this.capacity = capacity;

      lookup = new HashMap<String, CachedPreparedStatement>(capacity);

      lists = new CacheList[ReuseProbability.values().length];
      for (ReuseProbability prob : ReuseProbability.values())
      {
        lists[prob.ordinal()] = new CacheList();
      }
    }

    public void put(CachedPreparedStatement cachedStatement)
    {
      // refresh age
      cachedStatement.touch();

      // put into appripriate list
      lists[cachedStatement.getProbability().ordinal()].add(cachedStatement);

      // put into lookup table
      CachedPreparedStatement oldCachedStatement = lookup.put(cachedStatement.getSQL(), cachedStatement);

      // XXX (apeteri): evict and dispose of old instance on collision instead of throwing an exception
      if (oldCachedStatement != null)
      {
        lists[oldCachedStatement.getProbability().ordinal()].remove(oldCachedStatement);
        DBUtil.close(oldCachedStatement.statement);
        oldCachedStatement.statement = null;
      }

      // handle capacity overflow
      if (lookup.size() > capacity)
      {
        evictOne();
      }
    }

    private void evictOne()
    {
      long maxAge = -1;
      int ordinal = -1;

      for (ReuseProbability prob : ReuseProbability.values())
      {
        if (!lists[prob.ordinal()].isEmpty())
        {
          long age = lists[prob.ordinal()].tail().getAge();
          if (maxAge < age)
          {
            maxAge = age;
            ordinal = prob.ordinal();
          }
        }
      }

      remove(lists[ordinal].tail().getSQL());
    }

    public CachedPreparedStatement remove(String sql)
    {
      CachedPreparedStatement result = lookup.remove(sql);
      if (result == null)
      {
        return null;
      }

      lists[result.getProbability().ordinal()].remove(result);
      return result;
    }

    /**
     * @author Stefan Winkler
     */
    private class CacheList
    {
      private CachedPreparedStatement first;

      private CachedPreparedStatement last;

      public CacheList()
      {
      }

      public void add(CachedPreparedStatement s)
      {
        if (first == null)
        {
          first = s;
          last = s;
          s.previous = null;
          s.next = null;
        }
        else
        {
          first.previous = s;
          s.next = first;
          first = s;
        }
      }

      public void remove(CachedPreparedStatement s)
      {
        if (s == first)
        {
          first = s.next;
        }

        if (s.next != null)
        {
          s.next.previous = s.previous;
        }

        if (s == last)
        {
          last = s.previous;
        }

        if (s.previous != null)
        {
          s.previous.next = s.next;
        }

        s.previous = null;
        s.next = null;
      }

      public CachedPreparedStatement tail()
      {
        return last;
      }

      public boolean isEmpty()
      {
        return first == null;
      }
    }
  }

  /**
   * @author Stefan Winkler
   */
  private static final class CachedPreparedStatement
  {
    private long timeStamp;

    private String sql;

    private ReuseProbability probability;

    private PreparedStatement statement;

    /**
     * DL field
     */
    private CachedPreparedStatement previous;

    /**
     * DL field
     */
    private CachedPreparedStatement next;

    public CachedPreparedStatement(String sql, ReuseProbability prob, PreparedStatement stmt)
    {
      this.sql = sql;
      probability = prob;
      statement = stmt;
      timeStamp = System.currentTimeMillis();
    }

    public PreparedStatement getPreparedStatement()
    {
      return statement;
    }

    public long getAge()
    {
      long currentTime = System.currentTimeMillis();
      return (currentTime - timeStamp) * probability.ordinal();
    }

    public void touch()
    {
      timeStamp = System.currentTimeMillis();
    }

    public String getSQL()
    {
      return sql;
    }

    public ReuseProbability getProbability()
    {
      return probability;
    }
  }
}
