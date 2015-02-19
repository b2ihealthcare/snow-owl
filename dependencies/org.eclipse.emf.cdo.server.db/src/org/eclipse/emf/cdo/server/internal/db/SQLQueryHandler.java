/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kai Schlamp - initial API and implementation
 *    Eike Stepper - maintenance
 *    Kai Schlamp - Bug 284812: [DB] Query non CDO object fails
 *    Erdal Karaca - added cdoObjectResultAsMap parameter to return Map<String,Object> in result
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.db.IIDHandler;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements server side SQL query execution.
 *
 * @author Kai Schlamp
 */
public class SQLQueryHandler implements IQueryHandler
{
  public static final String QUERY_LANGUAGE = "sql";

  public static final String FIRST_RESULT = "firstResult";

  public static final String CDO_OBJECT_QUERY = "cdoObjectQuery";

  public static final String MAP_QUERY = "mapQuery";

  public static final String QUERY_STATEMENT = "queryStatement";

  private DBStoreAccessor storeAccessor;

  public SQLQueryHandler(DBStoreAccessor storeAccessor)
  {
    this.storeAccessor = storeAccessor;
  }

  public DBStoreAccessor getStoreAccessor()
  {
    return storeAccessor;
  }

  /**
   * Executes SQL queries. Gets the connection from {@link DBStoreAccessor}, creates a SQL query and sets the parameters
   * taken from the {@link CDOQueryInfo#getParameters()}.
   * <p>
   * Takes into account the {@link CDOQueryInfo#getMaxResults()} and the {@link SQLQueryHandler#FIRST_RESULT} (numbered
   * from 0) values for paging.
   * <p>
   * By default (parameter {@link SQLQueryHandler#CDO_OBJECT_QUERY} == true) a query for CDO Objects is exectued. The
   * SQL query must return the CDO ID in the first column for this to work. If you set
   * {@link SQLQueryHandler#CDO_OBJECT_QUERY} parameter to false, the value of the first column of a row itself is
   * returned.
   * <p>
   * By default (parameter {@link SQLQueryHandler#QUERY_STATEMENT} == true) query statements are executed. Set this
   * parameter to false for update/DDL statements.
   * <p>
   * It is possible to use variables inside the SQL string with ":" as prefix. E.g.
   * "SELECT cdo_id FROM Company WHERE name LIKE :name". The value must then be set by using a parameter. E.g.
   * query.setParameter(":name", "Foo%");
   *
   * @param info
   *          the object containing the query and parameters
   * @param context
   *          the query results are placed in the context
   * @see IQueryHandler#executeQuery(CDOQueryInfo, IQueryContext)
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    String language = info.getQueryLanguage();
    if (!QUERY_LANGUAGE.equals(language))
    {
      throw new IllegalArgumentException("Unsupported query language: " + language);
    }

    IIDHandler idHandler = storeAccessor.getStore().getIDHandler();
    Connection connection = storeAccessor.getConnection();
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    String query = info.getQueryString();

    try
    {
      int firstResult = -1;
      boolean queryStatement = true;
      boolean objectQuery = true;
      boolean mapQuery = false;

      HashMap<String, List<Integer>> paramMap = new HashMap<String, List<Integer>>();
      query = parse(query, paramMap);
      statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      for (String key : info.getParameters().keySet())
      {
        if (FIRST_RESULT.equalsIgnoreCase(key))
        {
          final Object o = info.getParameters().get(key);
          if (o != null)
          {
            try
            {
              firstResult = (Integer)o;
            }
            catch (ClassCastException ex)
            {
              throw new IllegalArgumentException("Parameter " + FIRST_RESULT + " must be an integer but it is a " + o
                  + " class " + o.getClass().getName(), ex);
            }
          }
        }
        else if (QUERY_STATEMENT.equalsIgnoreCase(key))
        {
          final Object o = info.getParameters().get(key);
          if (o != null)
          {
            try
            {
              queryStatement = (Boolean)o;
            }
            catch (ClassCastException ex)
            {
              throw new IllegalArgumentException("Parameter " + QUERY_STATEMENT + " must be an boolean but it is a "
                  + o + " class " + o.getClass().getName(), ex);
            }
          }
        }
        else if (CDO_OBJECT_QUERY.equalsIgnoreCase(key))
        {
          final Object o = info.getParameters().get(key);
          if (o != null)
          {
            try
            {
              objectQuery = (Boolean)o;
            }
            catch (ClassCastException ex)
            {
              throw new IllegalArgumentException("Parameter " + CDO_OBJECT_QUERY + " must be a boolean but it is a "
                  + o + " class " + o.getClass().getName(), ex);
            }
          }
        }
        else if (MAP_QUERY.equalsIgnoreCase(key))
        {
          final Object o = info.getParameters().get(key);
          if (o != null)
          {
            try
            {
              mapQuery = (Boolean)o;
            }
            catch (ClassCastException ex)
            {
              throw new IllegalArgumentException("Parameter " + MAP_QUERY + " must be a boolean but it is a " + o
                  + " class " + o.getClass().getName(), ex);
            }
          }
        }
        else
        {
          if (!paramMap.containsKey(key) || paramMap.get(key) == null)
          {
            throw new IllegalArgumentException("No parameter value found for named parameter " + key);
          }

          Integer[] indexes = paramMap.get(key).toArray(new Integer[0]);
          for (int i = 0; i < indexes.length; i++)
          {
            Object parameter = info.getParameters().get(key);
            // parameter = convertToSQL(parameter);
            statement.setObject(indexes[i], parameter);
          }
        }
      }

      if (queryStatement)
      {
        resultSet = statement.executeQuery();
        if (firstResult > -1)
        {
          resultSet.absolute(firstResult);
        }

        String[] columnNames = null;
        if (mapQuery)
        {
          columnNames = new String[resultSet.getMetaData().getColumnCount()];
          for (int i = 1; i <= columnNames.length; i++)
          {
            columnNames[i - 1] = resultSet.getMetaData().getColumnName(i);
          }
        }

        int maxResults = info.getMaxResults();
        int counter = 0;

        while (resultSet.next())
        {
          if (maxResults != CDOQueryInfo.UNLIMITED_RESULTS && counter++ >= maxResults)
          {
            break;
          }

          if (objectQuery)
          {
            CDOID result = idHandler.getCDOID(resultSet, 1);
            context.addResult(result);
          }
          else
          {
            int columnCount = resultSet.getMetaData().getColumnCount();
            if (columnCount == 1)
            {
              Object result = convertFromSQL(resultSet.getObject(1));
              context.addResult(mapQuery ? toMap(columnNames, new Object[] { result }) : result);
            }
            else
            {
              Object[] results = new Object[columnCount];
              for (int i = 0; i < columnCount; i++)
              {
                results[i] = convertFromSQL(resultSet.getObject(i + 1));
              }

              context.addResult(mapQuery ? toMap(columnNames, results) : results);
            }
          }
        }
      }
      else
      {
        int result = statement.executeUpdate();
        context.addResult(result);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException("Problem while executing SQL query: " + query, ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(statement);
    }
  }

  @SuppressWarnings("unused")
  private Object convertToSQL(Object value)
  {
    if (value instanceof java.util.Date)
    {
      java.util.Date date = (java.util.Date)value;
      value = new java.sql.Date(date.getTime());
    }

    return value;
  }

  private Object convertFromSQL(Object value)
  {
    // Conversion of java.sql.Date not needed in this direction

    if (value instanceof Clob)
    {
      Clob clob = (Clob)value;

      try
      {
        value = clob.getSubString(1, (int)clob.length());
      }
      catch (SQLException ex)
      {
        throw new DBException("Could not extract CLOB value", ex);
      }
    }

    return value;
  }

  private Map<String, Object> toMap(String[] columnNames, Object[] results)
  {
    Map<String, Object> ret = new HashMap<String, Object>();

    for (int i = 0; i < columnNames.length; i++)
    {
      String columnName = columnNames[i];
      ret.put(columnName, results[i]);
    }

    return ret;
  }

  private String parse(String query, Map<String, List<Integer>> paramMap)
  {
    int length = query.length();
    StringBuilder builder = new StringBuilder(length);

    boolean inSingleQuote = false;
    boolean inDoubleQuote = false;
    int index = 1;

    for (int i = 0; i < length; i++)
    {
      char c = query.charAt(i);
      if (inSingleQuote)
      {
        if (c == '\'')
        {
          inSingleQuote = false;
        }
      }
      else if (inDoubleQuote)
      {
        if (c == '"')
        {
          inDoubleQuote = false;
        }
      }
      else
      {
        if (c == '\'')
        {
          inSingleQuote = true;
        }
        else if (c == '"')
        {
          inDoubleQuote = true;
        }
        else if (c == ':' && i + 1 < length && Character.isJavaIdentifierStart(query.charAt(i + 1)))
        {
          int j = i + 2;
          while (j < length && Character.isJavaIdentifierPart(query.charAt(j)))
          {
            j++;
          }

          String name = query.substring(i + 1, j);
          c = '?';
          i += name.length();

          List<Integer> indexList = paramMap.get(name);
          if (indexList == null)
          {
            indexList = new ArrayList<Integer>();
            paramMap.put(name, indexList);
          }

          indexList.add(new Integer(index));
          index++;
        }
      }

      builder.append(c);
    }

    return builder.toString();
  }
}
