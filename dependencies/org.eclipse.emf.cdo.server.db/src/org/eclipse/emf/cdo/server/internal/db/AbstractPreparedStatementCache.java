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

import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.sql.Connection;

/**
 * @author Stefan Winkler
 * @since 2.0
 */
public abstract class AbstractPreparedStatementCache extends Lifecycle implements IPreparedStatementCache
{
  private Connection connection;

  public AbstractPreparedStatementCache()
  {
  }

  public final Connection getConnection()
  {
    return connection;
  }

  public final void setConnection(Connection connection)
  {
    checkInactive();
    this.connection = connection;
  }

  @Override
  protected void doBeforeActivate()
  {
    checkState(connection, "Must have valid connection to start"); //$NON-NLS-1$
  }
}
