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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerBrowser.AbstractPage;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class DBBrowserPage extends AbstractPage
{
  public DBBrowserPage()
  {
    super("tables", "Database Tables");
  }

  public boolean canDisplay(InternalRepository repository)
  {
    return repository.getStore() instanceof IDBConnectionProvider;
  }

  public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out)
  {
    IDBConnectionProvider connectionProvider = (IDBConnectionProvider)repository.getStore();
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();

      out.print("<table border=\"0\">\r\n");
      out.print("<tr>\r\n");

      out.print("<td valign=\"top\">\r\n");
      String table = showTables(browser, out, connection, repository.getName());
      out.print("</td>\r\n");

      if (table != null)
      {
        out.print("<td valign=\"top\">\r\n");
        showTable(browser, out, connection, table);
        out.print("</td>\r\n");
      }

      out.print("</tr>\r\n");
      out.print("</table>\r\n");
    }
    catch (DBException ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * @since 4.0
   */
  protected String showTables(CDOServerBrowser browser, PrintStream pout, Connection connection, String repo)
  {
    String table = browser.getParam("table");

    List<String> allTableNames = DBUtil.getAllTableNames(connection, repo);
    for (String tableName : allTableNames)
    {
      if (table == null)
      {
        table = tableName;
      }

      String label = browser.escape(tableName)/* .toLowerCase() */;
      if (tableName.equals(table))
      {
        pout.print("<b>" + label + "</b><br>\r\n");
      }
      else
      {
        pout.print(browser.href(label, getName(), "table", tableName, "order", null, "direction", null) + "<br>\r\n");
      }
    }

    return table;
  }

  /**
   * @since 4.0
   */
  protected void showTable(CDOServerBrowser browser, PrintStream pout, Connection connection, String table)
  {
    try
    {
      String order = browser.getParam("order");
      executeQuery(browser, pout, connection, "SELECT * FROM " + table
          + (order == null ? "" : " ORDER BY " + order + " " + browser.getParam("direction")));
    }
    catch (Exception ex)
    {
      browser.removeParam("order");
      browser.removeParam("direction");
      executeQuery(browser, pout, connection, "SELECT * FROM " + table);
    }
  }

  protected void executeQuery(CDOServerBrowser browser, PrintStream pout, Connection connection, String sql)
  {
    String order = browser.getParam("order");
    String direction = browser.getParam("direction");
    String highlight = browser.getParam("highlight");

    Statement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = connection.createStatement();
      resultSet = stmt.executeQuery(sql);

      ResultSetMetaData metaData = resultSet.getMetaData();
      int columns = metaData.getColumnCount();

      pout.print("<table border=\"1\" cellpadding=\"2\">\r\n");
      pout.print("<tr>\r\n");
      pout.print("<td>&nbsp;</td>\r\n");
      for (int i = 0; i < columns; i++)
      {
        String column = metaData.getColumnLabel(1 + i);
        String type = metaData.getColumnTypeName(1 + i).toLowerCase();

        String dir = column.equals(order) && "ASC".equals(direction) ? "DESC" : "ASC";
        pout.print("<td align=\"center\"><b>" + browser.href(column, getName(), "order", column, "direction", dir));
        pout.print("</b><br>" + type + "</td>\r\n");
      }

      pout.print("</tr>\r\n");

      int row = 0;
      while (resultSet.next())
      {
        ++row;
        pout.print("<tr>\r\n");
        pout.print("<td><b>" + row + "</b></td>\r\n");
        for (int i = 0; i < columns; i++)
        {
          String value = resultSet.getString(1 + i);
          String bgcolor = highlight != null && highlight.equals(value) ? " bgcolor=\"#fffca6\"" : "";
          pout.print("<td" + bgcolor + ">" + browser.href(value, getName(), "highlight", value) + "</td>\r\n");
        }

        pout.print("</tr>\r\n");
      }

      pout.print("</table>\r\n");
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String TYPE = "default";

    public Factory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    public DBBrowserPage create(String description) throws ProductCreationException
    {
      return new DBBrowserPage();
    }
  }
}
