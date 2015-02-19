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
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 *    Andre Dietisheim - bug 256649
 *
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.DBSchema;

/**
 * @author Eike Stepper
 */
public class CDODBSchema extends DBSchema
{
  public static final CDODBSchema INSTANCE = new CDODBSchema();

  /**
   * DBTable cdo_properties
   */
  public static final IDBTable PROPERTIES = INSTANCE.addTable("cdo_properties"); //$NON-NLS-1$

  public static final IDBField PROPERTIES_NAME = //
  PROPERTIES.addField("name", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField PROPERTIES_VALUE = //
  PROPERTIES.addField("value", DBType.LONGVARCHAR); //$NON-NLS-1$

  public static final IDBIndex INDEX_PROPERTIES_PK = //
  PROPERTIES.addIndex(IDBIndex.Type.PRIMARY_KEY, PROPERTIES_NAME);

  public static final String SQL_DELETE_PROPERTIES = "DELETE FROM " + PROPERTIES + " WHERE " + PROPERTIES_NAME + "=?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  public static final String SQL_INSERT_PROPERTIES = "INSERT INTO " + PROPERTIES + " (" + PROPERTIES_NAME + ", " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      + PROPERTIES_VALUE + ") VALUES (?, ?)"; //$NON-NLS-1$

  public static final String SQL_SELECT_PROPERTIES = "SELECT " + PROPERTIES_VALUE + " FROM " + PROPERTIES + " WHERE " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      + PROPERTIES_NAME + "=?"; //$NON-NLS-1$

  public static final String SQL_SELECT_ALL_PROPERTIES = "SELECT " + PROPERTIES_NAME + ", " + PROPERTIES_VALUE //$NON-NLS-1$ //$NON-NLS-2$
      + " FROM " + PROPERTIES; //$NON-NLS-1$

  /**
   * DBTable cdo_package_units
   */
  public static final IDBTable PACKAGE_UNITS = INSTANCE.addTable("cdo_package_units"); //$NON-NLS-1$

  public static final IDBField PACKAGE_UNITS_ID = //
  PACKAGE_UNITS.addField("id", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField PACKAGE_UNITS_ORIGINAL_TYPE = //
  PACKAGE_UNITS.addField("original_type", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField PACKAGE_UNITS_TIME_STAMP = //
  PACKAGE_UNITS.addField("time_stamp", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField PACKAGE_UNITS_PACKAGE_DATA = //
  PACKAGE_UNITS.addField("package_data", DBType.BLOB); //$NON-NLS-1$

  public static final IDBIndex INDEX_PACKAGE_UNITS_PK = //
  PACKAGE_UNITS.addIndex(IDBIndex.Type.PRIMARY_KEY, PACKAGE_UNITS_ID);

  /**
   * DBTable cdo_packages
   */
  public static final IDBTable PACKAGE_INFOS = INSTANCE.addTable("cdo_package_infos"); //$NON-NLS-1$

  public static final IDBField PACKAGE_INFOS_URI = //
  PACKAGE_INFOS.addField("uri", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField PACKAGE_INFOS_PARENT = //
  PACKAGE_INFOS.addField("parent", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField PACKAGE_INFOS_UNIT = //
  PACKAGE_INFOS.addField("unit", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBIndex INDEX_PACKAGE_INFOS_PK = //
  PACKAGE_INFOS.addIndex(IDBIndex.Type.PRIMARY_KEY, PACKAGE_INFOS_URI);

  public static final IDBIndex INDEX_PACKAGE_INFOS_PARENT = //
  PACKAGE_INFOS.addIndex(IDBIndex.Type.NON_UNIQUE, PACKAGE_INFOS_PARENT);

  public static final IDBIndex INDEX_PACKAGE_INFOS_UNIT = //
  PACKAGE_INFOS.addIndex(IDBIndex.Type.NON_UNIQUE, PACKAGE_INFOS_UNIT);

  /**
   * DBTable cdo_branches
   */
  public static final IDBTable BRANCHES = INSTANCE.addTable("cdo_branches"); //$NON-NLS-1$

  public static final IDBField BRANCHES_ID = //
  BRANCHES.addField("id", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField BRANCHES_NAME = //
  BRANCHES.addField("name", DBType.VARCHAR); //$NON-NLS-1$

  public static final IDBField BRANCHES_BASE_BRANCH_ID = //
  BRANCHES.addField("base_id", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField BRANCHES_BASE_TIMESTAMP = //
  BRANCHES.addField("base_time", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBIndex INDEX_BRANCHES_ID = //
  BRANCHES.addIndex(IDBIndex.Type.PRIMARY_KEY, BRANCHES_ID);

  public static final String SQL_CREATE_BRANCH = "INSERT INTO " + BRANCHES + " (" + BRANCHES_ID + ", " + BRANCHES_NAME //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      + ", " + BRANCHES_BASE_BRANCH_ID + ", " + BRANCHES_BASE_TIMESTAMP + ") VALUES (?, ?, ?, ?)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  public static final String SQL_LOAD_BRANCH = "SELECT " + BRANCHES_NAME + ", " + BRANCHES_BASE_BRANCH_ID + ", " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      + BRANCHES_BASE_TIMESTAMP + " FROM " + BRANCHES + " WHERE " + BRANCHES_ID + "=?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  public static final String SQL_LOAD_SUB_BRANCHES = "SELECT " + BRANCHES_ID + ", " + BRANCHES_NAME + ", " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      + BRANCHES_BASE_TIMESTAMP + " FROM " + BRANCHES + " WHERE " + BRANCHES_BASE_BRANCH_ID + "=?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  public static final String SQL_LOAD_BRANCHES = "SELECT " + BRANCHES_ID + ", " + BRANCHES_NAME + ", " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      BRANCHES_BASE_BRANCH_ID + ", " + BRANCHES_BASE_TIMESTAMP //$NON-NLS-1$
      + " FROM " + BRANCHES + " WHERE " + BRANCHES_ID + " BETWEEN ? AND ? ORDER BY " + BRANCHES_ID; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  /**
   * DBTable cdo_commit_infos
   */
  public static final IDBTable COMMIT_INFOS = INSTANCE.addTable("cdo_commit_infos"); //$NON-NLS-1$

  public static final IDBField COMMIT_INFOS_TIMESTAMP = //
  COMMIT_INFOS.addField("commit_time", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField COMMIT_INFOS_PREVIOUS_TIMESTAMP = //
  COMMIT_INFOS.addField("previous_time", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField COMMIT_INFOS_BRANCH = //
  COMMIT_INFOS.addField("branch_id", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField COMMIT_INFOS_USER = //
  COMMIT_INFOS.addField("user_id", DBType.VARCHAR); //$NON-NLS-1$

  public static final IDBField COMMIT_INFOS_COMMENT = //
  COMMIT_INFOS.addField("commit_comment", DBType.VARCHAR); //$NON-NLS-1$

  public static final IDBIndex INDEX_COMMIT_INFOS_PK = //
  COMMIT_INFOS.addIndex(IDBIndex.Type.PRIMARY_KEY, COMMIT_INFOS_TIMESTAMP);

  public static final IDBIndex INDEX_COMMIT_INFOS_BRANCH = //
  COMMIT_INFOS.addIndex(IDBIndex.Type.NON_UNIQUE, COMMIT_INFOS_BRANCH);

  public static final IDBIndex INDEX_COMMIT_INFOS_USER = //
  COMMIT_INFOS.addIndex(IDBIndex.Type.NON_UNIQUE, COMMIT_INFOS_USER);

  public static final String SQL_CREATE_COMMIT_INFO = "INSERT INTO " + COMMIT_INFOS + "(" + COMMIT_INFOS_TIMESTAMP //$NON-NLS-1$ //$NON-NLS-2$
      + ", " + COMMIT_INFOS_PREVIOUS_TIMESTAMP + ", " + COMMIT_INFOS_BRANCH + ", " + COMMIT_INFOS_USER + ", " + COMMIT_INFOS_COMMENT + ") " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      + "VALUES (?, ?, ?, ?, ?)"; //$NON-NLS-1$

  /**
   * DBTable cdo_lobs
   */
  public static final IDBTable LOBS = INSTANCE.addTable("cdo_lobs"); //$NON-NLS-1$

  public static final IDBField LOBS_ID = //
  LOBS.addField("id", DBType.VARCHAR, 64); //$NON-NLS-1$

  public static final IDBField LOBS_SIZE = //
  LOBS.addField("size", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField LOBS_BDATA = //
  LOBS.addField("bdata", DBType.BLOB); //$NON-NLS-1$

  public static final IDBField LOBS_CDATA = //
  LOBS.addField("cdata", DBType.CLOB); //$NON-NLS-1$

  public static final IDBIndex INDEX_LOBS_ID = //
  LOBS.addIndex(IDBIndex.Type.PRIMARY_KEY, LOBS_ID);

  public static final String SQL_QUERY_LOBS = "SELECT 1 FROM " + CDODBSchema.LOBS + " WHERE " + CDODBSchema.LOBS_ID + "=?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  public static final String SQL_HANDLE_LOBS = "SELECT " + CDODBSchema.LOBS_ID + ", " + CDODBSchema.LOBS_SIZE + ", " + CDODBSchema.LOBS_BDATA + ", " + CDODBSchema.LOBS_CDATA + " FROM " + CDODBSchema.LOBS; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

  public static final String SQL_LOAD_LOB = "SELECT " + CDODBSchema.LOBS_SIZE + ", " + CDODBSchema.LOBS_BDATA + ", " + CDODBSchema.LOBS_CDATA + " FROM " + CDODBSchema.LOBS + " WHERE " + CDODBSchema.LOBS_ID + "=?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

  public static final String SQL_WRITE_BLOB = "INSERT INTO " + CDODBSchema.LOBS + "(" + CDODBSchema.LOBS_ID + ", " + CDODBSchema.LOBS_SIZE + ", " + CDODBSchema.LOBS_BDATA + ") VALUES(?, ?, ?)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

  public static final String SQL_WRITE_CLOB = "INSERT INTO " + CDODBSchema.LOBS + "(" + CDODBSchema.LOBS_ID + ", " + CDODBSchema.LOBS_SIZE + ", " + CDODBSchema.LOBS_CDATA + ") VALUES(?, ?, ?)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

  /**
   * Name of object table
   */
  public static final String CDO_OBJECTS = "cdo_objects"; //$NON-NLS-1$

  /**
   * Field names of attribute tables
   */
  public static final String ATTRIBUTES_ID = "cdo_id"; //$NON-NLS-1$

  public static final String ATTRIBUTES_BRANCH = "cdo_branch"; //$NON-NLS-1$

  public static final String ATTRIBUTES_VERSION = "cdo_version"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CLASS = "cdo_class"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CREATED = "cdo_created"; //$NON-NLS-1$

  public static final String ATTRIBUTES_REVISED = "cdo_revised"; //$NON-NLS-1$

  public static final String ATTRIBUTES_RESOURCE = "cdo_resource"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CONTAINER = "cdo_container"; //$NON-NLS-1$

  public static final String ATTRIBUTES_FEATURE = "cdo_feature"; //$NON-NLS-1$

  /**
   * Field names of list tables
   */
  public static final String LIST_FEATURE = "cdo_feature"; //$NON-NLS-1$

  public static final String LIST_REVISION_ID = "cdo_source"; //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION = "cdo_version"; //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION_ADDED = "cdo_version_added"; //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION_REMOVED = "cdo_version_removed"; //$NON-NLS-1$

  public static final String LIST_REVISION_BRANCH = "cdo_branch"; //$NON-NLS-1$

  public static final String LIST_IDX = "cdo_idx"; //$NON-NLS-1$

  public static final String LIST_VALUE = "cdo_value"; //$NON-NLS-1$

  /**
   * Field names of featuremap tables
   */
  public static final String FEATUREMAP_REVISION_ID = LIST_REVISION_ID;

  public static final String FEATUREMAP_VERSION = LIST_REVISION_VERSION;

  public static final String FEATUREMAP_VERSION_ADDED = LIST_REVISION_VERSION_ADDED;

  public static final String FEATUREMAP_VERSION_REMOVED = LIST_REVISION_VERSION_REMOVED;

  public static final String FEATUREMAP_BRANCH = LIST_REVISION_BRANCH;

  public static final String FEATUREMAP_IDX = LIST_IDX;

  public static final String FEATUREMAP_TAG = LIST_FEATURE;

  public static final String FEATUREMAP_VALUE = LIST_VALUE;

  private CDODBSchema()
  {
    super("CDO"); //$NON-NLS-1$
  }

  static
  {
    INSTANCE.lock();
  }
}
