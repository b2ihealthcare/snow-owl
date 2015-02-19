/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStore.CanHandleClientAssignedIDs;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBSchema;

/**
 * The main entry point to the API of CDO's proprietary object/relational mapper.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDBStore extends IStore, IDBConnectionProvider, CanHandleClientAssignedIDs
{
  /**
   * @since 2.0
   */
  public IMappingStrategy getMappingStrategy();

  /**
   * @since 4.0
   */
  public IIDHandler getIDHandler();

  public IDBAdapter getDBAdapter();

  public IDBSchema getDBSchema();

  /**
   * Get the meta data manager associated with this DBStore.
   *
   * @since 2.0
   */
  public IMetaDataManager getMetaDataManager();

  /**
   * @since 2.0
   */
  public IDBStoreAccessor getReader(ISession session);

  /**
   * @since 2.0
   */
  public IDBStoreAccessor getWriter(ITransaction transaction);

  /**
   * Contains symbolic constants that specifiy valid keys of {@link IRepository#getProperties() DB store properties}.
   *
   * @author Eike Stepper
   * @since 4.0
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Props
  {
    public static final String CONNECTION_KEEPALIVE_PERIOD = "connectionKeepAlivePeriod"; //$NON-NLS-1$
  }
}
