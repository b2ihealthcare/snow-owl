/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * @author Stefan Winkler
 * @since 2.0
 */
public class NullPreparedStatementCache extends AbstractPreparedStatementCache
{
  private HashSet<PreparedStatement> allocatedStatements = new HashSet<PreparedStatement>();

  public NullPreparedStatementCache()
  {
  }

  public PreparedStatement getPreparedStatement(String sql, ReuseProbability reuseProbability)
  {
    try
    {
      PreparedStatement result = getConnection().prepareStatement(sql);
      allocatedStatements.add(result);
      return result;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void releasePreparedStatement(PreparedStatement ps)
  {
    allocatedStatements.remove(ps);
    DBUtil.close(ps);
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    if (!allocatedStatements.isEmpty())
    {
      OM.LOG.warn("Possible Leak Detected:"); //$NON-NLS-1$
      for (PreparedStatement ps : allocatedStatements)
      {
        OM.LOG.warn("- " + ps.toString()); //$NON-NLS-1$
      }

      assert false;
    }
  }
}
