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
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Set;

/**
 * Abstracts the handling of {@link CDOID IDs} of different {@link ObjectType ID types}.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IIDHandler extends Comparator<CDOID>
{
  public IDBStore getStore();

  public DBType getDBType();

  public Set<ObjectType> getObjectIDTypes();

  public ITypeMapping getObjectTypeMapping();

  public CDOID createCDOID(String val);

  public boolean isLocalCDOID(CDOID id);

  public CDOID getLastObjectID();

  public void setLastObjectID(CDOID lastObjectID);

  /**
   * @since 4.1
   */
  public void adjustLastObjectID(CDOID maxID);

  public CDOID getNextLocalObjectID();

  public void setNextLocalObjectID(CDOID nextLocalObjectID);

  public CDOID getNextCDOID(CDORevision revision);

  public void appendCDOID(StringBuilder builder, CDOID id);

  /**
   * @since 4.1
   */
  public void setCDOIDRaw(PreparedStatement stmt, int column, Object id) throws SQLException;

  public void setCDOID(PreparedStatement stmt, int column, CDOID id) throws SQLException;

  public void setCDOID(PreparedStatement stmt, int column, CDOID id, long commitTime) throws SQLException;

  public CDOID getCDOID(ResultSet resultSet, int column) throws SQLException;

  public CDOID getCDOID(ResultSet resultSet, String name) throws SQLException;

  public CDOID getMinCDOID();

  public CDOID getMaxCDOID();

  public CDOID mapURI(IDBStoreAccessor accessor, String uri, long commitTime);

  public String unmapURI(IDBStoreAccessor accessor, CDOID id);

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException;

  public void rawImport(Connection connection, CDODataInput in, long fromCommitTime, long toCommitTime, OMMonitor fork)
      throws IOException;
}
